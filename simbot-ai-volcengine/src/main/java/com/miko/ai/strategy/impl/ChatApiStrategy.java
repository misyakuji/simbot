package com.miko.ai.strategy.impl;

import com.miko.ai.constant.ArkChatConstants;
import com.miko.ai.converter.ArkMessageConverter;
import com.miko.ai.enums.ArkApiMode;
import com.miko.ai.response.ArkChatResponse;
import com.miko.ai.strategy.ArkApiStrategy;
import com.miko.ai.util.ArkSchemaBuilder;
import com.miko.tool.BotToolExecutor;
import com.miko.tool.BotToolRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 火山方舟Chat API策略实现类，负责处理与火山引擎Ark模型的聊天交互。
 * 支持工具调用、消息转换、异步请求等功能。
 */
@RequiredArgsConstructor
public class ChatApiStrategy implements ArkApiStrategy {
    private final WebClient webClient;
    private final String baseUrl;
    private final String model;
    private final BotToolRegistry botToolRegistry;
    private final BotToolExecutor botToolExecutor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 同步调用Chat API，设置默认超时时间 30s。
     *
     * @param prompt 用户输入的提示信息
     * @return 聊天响应结果
     */
    @Override
    public ChatResponse call(Prompt prompt) {
        return reactiveCall(prompt)
                .timeout(Duration.ofSeconds(ArkChatConstants.DEFAULT_TIMEOUT_SECONDS))
                .block();
    }

    /**
     * 异步调用Chat API，构建请求体并处理响应。
     *
     * @param prompt 用户输入的提示信息
     * @return 异步聊天响应结果
     */
    private Mono<ChatResponse> reactiveCall(Prompt prompt) {
        // 将Spring AI的Prompt转换为火山引擎Ark兼容的消息格式
        List<Map<String, String>> arkMessages = ArkMessageConverter.convertToArkMessages(prompt);

        // 构建工具Schema，用于支持工具调用功能
        List<Map<String, Object>> toolsJson = botToolRegistry.getAllTools().stream()
                .map(meta -> Map.of(
                        "type", "function",
                        "function", Map.of(
                                "name", meta.name(),
                                "description", meta.description(),
                                "parameters", ArkSchemaBuilder.buildJsonSchema(meta)
                        )
                ))
                .toList();

        // 构建请求体
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", arkMessages,
                "tools", toolsJson
        );

        // 发起POST请求到火山引擎Ark API
        return webClient.post()
                .uri(baseUrl + ArkApiMode.CHAT_API.getValue())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(ArkChatResponse.class)
                // 异步解析响应结果,这里使用subscribeOn避免阻塞线程
                .flatMap(arkResponse ->
                        /*
                         * 这里使用 flatMap 是因为 parseResponse 依赖上游异步返回的 arkResponse，
                         * 并且 parseResponse 本身可能是耗时 / 阻塞操作（如：
                         *   - JSON 深度解析
                         *   - Tool 调用
                         *   - 复杂业务逻辑
                         *   - 同步 IO / CPU 密集计算）
                         *
                         * 不能直接在 reactor 线程（如 reactor-http-nio）中执行，
                         * 否则会阻塞事件循环，导致整个 WebClient/Flux 链路性能劣化。
                         */
                        Mono.fromCallable(() -> parseResponse(arkResponse))
                                /*
                                 * 将 parseResponse 的执行调度到 boundedElastic 线程池：
                                 *   - 专门用于阻塞或不可预期耗时的任务
                                 *   - 自动扩容，有上限，避免无限制创建线程
                                 *
                                 * 这样可以保证：
                                 *   1. reactor 事件线程不被阻塞
                                 *   2. 上游异步 IO 与下游同步逻辑安全隔离
                                 */
                                .subscribeOn(Schedulers.boundedElastic())
                )
                // 异常处理：返回降级响应
                .onErrorResume(e -> {
                    AssistantMessage fallback = new AssistantMessage("接口调用异常，请稍后重试: " + e.getMessage());
                    return Mono.just(ChatResponse.builder()
                            .generations(List.of(new Generation(fallback)))
                            .build());
                });
    }

    /**
     * 解析火山引擎Ark的响应结果，处理工具调用或普通消息。
     *
     * @param arkResponse 火山引擎Ark的原始响应
     * @return 格式化后的聊天响应
     */
    private ChatResponse parseResponse(ArkChatResponse arkResponse) {
        // 获取第一个选择项（通常只有一个）
        ArkChatResponse.Choice firstChoice = arkResponse.getChoices().getFirst();
        // 提取消息内容
        ArkChatResponse.Message message = firstChoice.getMessage();
        // 初始化消息变量
        AssistantMessage assistantMessage;
        // 判断是否存在工具调用
        if (message.getToolCalls() != null && !message.getToolCalls().isEmpty()) {
            assistantMessage = handleToolCalls(message);
        } else {
            assistantMessage = new AssistantMessage(message.getContent());
        }
        // 创建Generation对象，封装助理消息
        Generation generation = new Generation(assistantMessage);
        // 构建最终的ChatResponse响应对象
        return ChatResponse.builder()
                // 设置生成结果列表，包含当前助理消息
                .generations(List.of(generation))
                // 添加元数据信息，标识使用的模型名称
                .metadata("model", "volc-ark-" + model)
                // 构建并返回ChatResponse对象
                .build();
    }

    /**
     * 处理工具调用，同步执行所有工具并收集结果。
     *
     * @param message 包含工具调用信息的消息对象
     * @return 包含工具执行结果的助理消息
     */
    private AssistantMessage handleToolCalls(ArkChatResponse.Message message) {
        List<AssistantMessage.ToolCall> toolCalls = new ArrayList<>();
        List<Map<String, Object>> results = new ArrayList<>();
        // 遍历所有工具调用
        for (var tc : message.getToolCalls()) {
            String toolName = tc.getFunction().getName();
            String arguments = tc.getFunction().getArguments();
            try {
                // 解析工具参数
                Map<String, Object> argsMap = objectMapper.readValue(arguments, new TypeReference<>() {
                });
                // 执行工具调用
                Object result = botToolExecutor.execute(toolName, argsMap);
                results.add(Map.of("toolName", toolName, "result", result));
                toolCalls.add(new AssistantMessage.ToolCall(tc.getId(), tc.getType(), toolName, arguments));
            } catch (Exception e) {
                // 工具执行失败时记录错误信息
                results.add(Map.of("toolName", toolName, "toolError", e.getMessage()));
            }
        }
        return AssistantMessage.builder()
                .content(ArkChatConstants.TOOL_TRACE_PREFIX + ": Completed :" + results)
                .toolCalls(toolCalls)
                .build();
    }

    /**
     * 获取当前策略支持的API模式。
     *
     * @return 支持的API模式枚举值
     */
    @Override
    public ArkApiMode getSupportMode() {
        return ArkApiMode.CHAT_API;
    }
}
