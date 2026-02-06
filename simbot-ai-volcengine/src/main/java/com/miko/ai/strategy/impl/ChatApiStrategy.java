package com.miko.ai.strategy.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 聊天API策略实现类，负责处理与火山引擎AI模型的交互。
 * 支持工具调用、消息转换和响应解析等功能。
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
     * 同步调用聊天API，设置默认超时时间。
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
     * 异步调用聊天API，构建请求体并发送HTTP POST请求。
     *
     * @param prompt 用户输入的提示信息
     * @return 响应式聊天结果
     */
    @Override
    public Mono<ChatResponse> reactiveCall(Prompt prompt) {
        // 将Spring AI的Prompt对象转换为火山引擎兼容的消息格式
        List<Map<String, String>> arkMessages = ArkMessageConverter.convertToArkMessages(prompt);
        
        // 构建注册工具的JSON Schema，供模型调用
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

        // 组装请求体
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", arkMessages,
                "tools", toolsJson
        );

        // 发送POST请求到火山引擎API
        return webClient.post()
                .uri(baseUrl + ArkApiMode.CHAT_API.getValue())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(ArkChatResponse.class)
                .map(this::parseResponse)
                .onErrorResume(this::handleError);
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

    /**
     * 解析火山引擎API的响应结果，提取首个选择项并处理工具调用。
     *
     * @param arkResponse 火山引擎API原始响应
     * @return 标准化的聊天响应对象
     */
    private ChatResponse parseResponse(ArkChatResponse arkResponse) {
        ArkChatResponse.Choice firstChoice = arkResponse.getChoices().getFirst();
        ArkChatResponse.Message message = firstChoice.getMessage();
        AssistantMessage assistantMessage;

        // 判断是否存在工具调用，分别处理
        if (message.getToolCalls() != null && !message.getToolCalls().isEmpty()) {
            assistantMessage = handleToolCalls(message);
        } else {
            assistantMessage = new AssistantMessage(message.getContent());
        }

        Generation generation = new Generation(assistantMessage);
        return ChatResponse.builder()
                .generations(List.of(generation))
                .metadata("model", "volc-ark-" + model)
                .build();
    }

    /**
     * 处理工具调用逻辑，执行工具并记录调用结果。
     *
     * @param message 包含工具调用信息的消息对象
     * @return 包含工具调用结果的助手消息
     */
    private AssistantMessage handleToolCalls(ArkChatResponse.Message message) {
        List<AssistantMessage.ToolCall> toolCalls = new ArrayList<>();
        List<Map<String, Object>> results = new ArrayList<>();

        // 遍历所有工具调用，执行并收集结果
        for (var tc : message.getToolCalls()) {
            String toolName = tc.getFunction().getName();
            String arguments = tc.getFunction().getArguments();
            
            // 解析工具参数为Map结构
            Map<String, Object> argsMap;
            try {
                argsMap = objectMapper.readValue(arguments, new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException("解析工具参数失败: " + arguments, e);
            }

            // 执行工具并保存结果
            Object result = botToolExecutor.execute(toolName, argsMap);
            results.add(Map.of("toolName", toolName, "result", result));
            toolCalls.add(new AssistantMessage.ToolCall(tc.getId(), tc.getType(), toolName, arguments));
        }

        // 构造包含工具调用追踪信息的助手消息
        return AssistantMessage.builder()
                .content(ArkChatConstants.TOOL_TRACE_PREFIX + ": Completed :" + results)
                .toolCalls(toolCalls)
                .build();
    }

    /**
     * 统一异常处理方法，返回友好的错误提示。
     *
     * @param e 异常对象
     * @return 默认错误响应
     */
    private Mono<ChatResponse> handleError(Throwable e) {
        AssistantMessage fallback = new AssistantMessage("接口调用异常，请稍后重试");
        ChatResponse response = ChatResponse.builder()
                .generations(List.of(new Generation(fallback)))
                .build();
        return Mono.just(response);
    }
}
