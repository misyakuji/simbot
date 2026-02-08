package com.miko.ai.strategy.impl;

import com.miko.ai.constant.ArkChatConstants;
import com.miko.ai.converter.ArkMessageConverter;
import com.miko.ai.enums.ArkApiMode;
import com.miko.ai.response.ArkResponsesApiResponse;
import com.miko.ai.strategy.ArkApiStrategy;
import com.miko.ai.util.ArkSchemaBuilder;
import com.miko.tool.BotToolExecutor;
import com.miko.tool.BotToolRegistry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 响应API策略实现类
 * <p>
 * 该类实现了{@link ArkApiStrategy}接口，专门用于处理响应式API调用。
 *
 * <p><strong>核心特性：</strong></p>
 * <ul>
 *   <li><strong>多工具调用支持</strong>：根据AI模型能力支持多个函数调用，使用前请确认模型兼容性</li>
 *   <li><strong>自动上下文管理</strong>：无需手动维护对话历史，通过previous_response_id自动关联上下文</li>
 *   <li><strong>与ChatAPI一致性</strong>：调用方式与ChatAPI保持一致，仅响应格式有所区别</li>
 * </ul>
 *
 * <p><strong>关键机制：</strong></p>
 * <ul>
 *   <li>通过{@code previous_response_id}维护对话状态</li>
 *   <li>支持工具链式调用和递归处理</li>
 *   <li>异步非阻塞的响应处理机制</li>
 * </ul>
 *
 * @see ArkApiStrategy 基础策略接口
 * @see ArkApiMode#RESPONSES_API 支持的API模式
 */
@RequiredArgsConstructor
public class ResponsesApiStrategy implements ArkApiStrategy {
    private static final Logger log = LoggerFactory.getLogger(ResponsesApiStrategy.class);
    private final WebClient webClient;
    private final String baseUrl;
    private final String model;
    private final BotToolRegistry botToolRegistry;
    private final BotToolExecutor botToolExecutor;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String previousResponseId = null;

    /**
     * 同步执行聊天请求并返回响应
     * 使用默认超时时间 {@link ArkChatConstants#DEFAULT_TIMEOUT_SECONDS}
     *
     * @param prompt 聊天提示信息
     * @return ChatResponse 聊天响应结果
     */
    @Override
    public ChatResponse call(Prompt prompt) {
        return reactiveCall(prompt)
                .timeout(Duration.ofSeconds(ArkChatConstants.DEFAULT_TIMEOUT_SECONDS))
                .block();
    }

    /**
     * 异步执行聊天请求并返回响应
     * 发送请求到响应API端点，处理响应并解析结果
     *
     * @param prompt 聊天提示信息
     * @return Mono<ChatResponse> 异步聊天响应结果
     */
    private Mono<ChatResponse> reactiveCall(Prompt prompt) {
        // 构建初始请求参数，包含模型、输入消息、工具列表和上一次响应ID（如果存在）
        Map<String, Object> request = buildInitialRequest(prompt);
        // 发送请求并解析响应
        return webClient.post()
                .uri(baseUrl + ArkApiMode.RESPONSES_API.getValue())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ArkResponsesApiResponse.class)
                .flatMap(resp -> {
                            // 保存当前返回的 response.id，作为下一轮请求的 previous_response_id
                            if (resp.getId() != null) {
                                previousResponseId = resp.getId();
                            }
                            return Mono.fromCallable(() -> parseResponse(resp))
                                    .subscribeOn(Schedulers.boundedElastic());
                        }
                )// 异常处理：返回降级响应
                .onErrorResume(e -> {
                    AssistantMessage fallback = new AssistantMessage("接口调用异常，请稍后重试: " + e.getMessage());
                    return Mono.just(ChatResponse.builder()
                            .generations(List.of(new Generation(fallback)))
                            .build());
                });
    }

    /**
     * 构建初始请求参数
     * 包含模型名称、输入消息、工具列表以及上一次响应ID（如果存在）
     *
     * @param prompt 聊天提示信息
     * @return Map<String, Object> 请求参数映射
     */
    private Map<String, Object> buildInitialRequest(Prompt prompt) {
        // 创建请求参数映射
        Map<String, Object> req = new HashMap<>();
        // 设置模型名称
        req.put("model", model);
        // 转换并设置输入消息
        req.put("input", ArkMessageConverter.convertToResponsesInput(prompt));
        // 设置工具列表
        req.put("tools", buildTools());
        // 如果存在上一次的响应ID，则添加到请求中
        if (previousResponseId != null) {
            req.put("previous_response_id", previousResponseId);
        }
        // 返回构建好的请求参数
        return req;
    }

    /**
     * 构建工具Schema列表
     * 为每个注册的工具构建符合API要求的Schema结构
     *
     * @return List<Map<String, Object>> 工具Schema列表
     */
    private List<Map<String, Object>> buildTools() {
        // 获取所有已注册的工具元数据，并转换为符合API要求的工具Schema列表
        return botToolRegistry.getAllTools().stream()
                .map(meta -> Map.of(
                        "type", "function",
                        "name", meta.name(),
                        "description", meta.description(),
                        "parameters", ArkSchemaBuilder.buildJsonSchema(meta)
                ))
                .toList();
    }

    /**
     * 解析API响应并生成最终的聊天响应
     * 处理工具调用逻辑，如需继续工具调用则递归处理
     *
     * @param resp API响应对象
     * @return ChatResponse 最终聊天响应
     */
    private ChatResponse parseResponse(ArkResponsesApiResponse resp) {
        // 更新上一次响应ID，用于关联上下文历史
        if (resp.getId() != null) {
            previousResponseId = resp.getId();
        }
        // 初始化StringBuilder用于收集工具执行结果
        StringBuilder builder = new StringBuilder();
        // 遍历响应消息，处理函数调用类型的message
        List<ArkResponsesApiResponse.Message> messages = resp.getOutput();
        // 遍历所有消息，查找并处理函数调用类型的消息
        for (ArkResponsesApiResponse.Message msg : messages) {
            // 判断消息类型是否为函数调用
            if ("function_call".equals(msg.getType())) {
                // 执行对应的工具函数，并将执行结果追加到结果构建器中
                builder.append(executeTool(msg));
            }
        }
        // 如果有工具执行结果，构建工具输入并继续处理
        if (!builder.isEmpty()) {
            Map<String, Object> toolInput = buildToolResultInput(builder.toString());
            return continueAfterTool(toolInput);
        }
        // 获取第二个消息作为最终回复（第一个通常是用户输入）
        ArkResponsesApiResponse.Message msg = resp.getOutput().get(1);
        // 不执行工具则正常文本回复
        String text = msg.getContent().getFirst().getText();
        // 构建并返回最终的聊天响应
        return ChatResponse.builder()
                .generations(List.of(new Generation(new AssistantMessage(text))))
                .metadata("model", model)
                .build();
    }

    /**
     * 执行指定的工具调用
     * 将工具参数反序列化并执行对应工具
     *
     * @param msg 工具调用消息
     * @return Object 工具执行结果
     */
    private Object executeTool(ArkResponsesApiResponse.Message msg) {
        // 尝试将工具参数从JSON字符串反序列化为Map对象
        try {
            Map<String, Object> args = objectMapper.readValue(msg.getArguments(), new TypeReference<>() {
            });
            // 执行对应的工具函数，传入工具名称和解析后的参数
            return botToolExecutor.execute(msg.getName(), args);
        } catch (Exception e) {
            // 如果反序列化或执行过程中出现异常，返回包含错误信息的Map
            return Map.of("executeTool", e.getMessage());
        }
    }

    /**
     * 在工具执行后继续处理聊天流程
     * 将工具执行结果作为新的输入发送给API
     *
     * @param toolInput 工具执行结果输入
     * @return ChatResponse 继续处理后的聊天响应
     */
    private ChatResponse continueAfterTool(Map<String, Object> toolInput) {
        // 构建工具执行后的请求参数
        Map<String, Object> req = new HashMap<>();
        // 设置模型名称
        req.put("model", model);
        // 设置工具执行结果作为输入
        req.put("input", List.of(toolInput));
        // 设置上一次的响应ID以维持对话上下文
        req.put("previous_response_id", previousResponseId);
        // 发送请求到响应API端点并获取响应
        ArkResponsesApiResponse resp = webClient.post()
                .uri(baseUrl + ArkApiMode.RESPONSES_API.getValue())
                .bodyValue(req)
                .retrieve()
                .bodyToMono(ArkResponsesApiResponse.class)
                .block();

        // 解析并返回API响应
        return parseResponse(resp);
    }

    /**
     * 构建工具执行结果的输入格式
     * 将执行结果包装成符合API要求的用户角色消息
     *
     * @param result 工具执行结果
     * @return Map<String, Object> 格式化的输入映射
     */
    public Map<String, Object> buildToolResultInput(Object result) {
        return Map.of("role", "user", "content", objectMapper.writeValueAsString(result));
    }

    /**
     * 获取当前策略支持的API模式
     *
     * @return ArkApiMode 支持的API模式枚举值
     */
    @Override
    public ArkApiMode getSupportMode() {
        return ArkApiMode.RESPONSES_API;
    }
}
