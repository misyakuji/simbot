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
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatApiStrategy implements ArkApiStrategy {
    private final WebClient webClient;
    private final String baseUrl;
    private final String model;
    private final BotToolRegistry botToolRegistry;
    private final BotToolExecutor botToolExecutor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ChatApiStrategy(WebClient webClient, String baseUrl, String model,
                           BotToolRegistry botToolRegistry, BotToolExecutor botToolExecutor) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
        this.model = model;
        this.botToolRegistry = botToolRegistry;
        this.botToolExecutor = botToolExecutor;
    }

    /** 同步调用 */
    @Override
    public ChatResponse call(Prompt prompt) {
        return reactiveCall(prompt)
                .timeout(Duration.ofSeconds(ArkChatConstants.DEFAULT_TIMEOUT_SECONDS))
                .block(); // 阻塞返回
    }

    /** 异步响应式调用 */
    @Override
    public Mono<ChatResponse> reactiveCall(Prompt prompt) {
        // 转换消息
        List<Map<String, String>> arkMessages = ArkMessageConverter.convertToArkMessages(prompt);

        // 构建工具JSON
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

        // 请求体
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", arkMessages,
                "tools", toolsJson
        );

        return webClient.post()
                .uri(baseUrl + ArkApiMode.CHAT_API.getValue())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(ArkChatResponse.class)
                // 异步执行阻塞工具
                .flatMap(arkResponse ->
                        Mono.fromCallable(() -> parseResponse(arkResponse))
                                .subscribeOn(Schedulers.boundedElastic())
                )
                .onErrorResume(e -> {
                    AssistantMessage fallback = new AssistantMessage("接口调用异常，请稍后重试: " + e.getMessage());
                    return Mono.just(ChatResponse.builder()
                            .generations(List.of(new Generation(fallback)))
                            .build());
                });
    }

    @Override
    public ArkApiMode getSupportMode() {
        return ArkApiMode.CHAT_API;
    }

    /** 同步解析响应并执行工具 */
    private ChatResponse parseResponse(ArkChatResponse arkResponse) {
        ArkChatResponse.Choice firstChoice = arkResponse.getChoices().getFirst();
        ArkChatResponse.Message message = firstChoice.getMessage();
        AssistantMessage assistantMessage;

        if (message.getToolCalls() != null && !message.getToolCalls().isEmpty()) {
            assistantMessage = handleToolCalls(message); // 同步执行工具
        } else {
            assistantMessage = new AssistantMessage(message.getContent());
        }

        Generation generation = new Generation(assistantMessage);
        return ChatResponse.builder()
                .generations(List.of(generation))
                .metadata("model", "volc-ark-" + model)
                .build();
    }

    /** 同步执行工具调用 */
    private AssistantMessage handleToolCalls(ArkChatResponse.Message message) {
        List<AssistantMessage.ToolCall> toolCalls = new ArrayList<>();
        List<Map<String, Object>> results = new ArrayList<>();

        for (var tc : message.getToolCalls()) {
            String toolName = tc.getFunction().getName();
            String arguments = tc.getFunction().getArguments();

            Map<String, Object> argsMap;
            try {
                argsMap = objectMapper.readValue(arguments, new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException("解析工具参数失败: " + arguments, e);
            }

            // ✅ 核心：同步执行工具
            Object result = Mono.fromCallable(() -> botToolExecutor.execute(toolName, argsMap))
                    .subscribeOn(Schedulers.boundedElastic()) // 让阻塞操作跑在弹性线程池
                    .block(); // 现在.block()不会再报错
//            Object result = botToolExecutor.execute(toolName, argsMap);
            results.add(Map.of("toolName", toolName, "result", result));
            toolCalls.add(new AssistantMessage.ToolCall(tc.getId(), tc.getType(), toolName, arguments));
        }

        return AssistantMessage.builder()
                .content(ArkChatConstants.TOOL_TRACE_PREFIX + ": Completed :" + results)
                .toolCalls(toolCalls)
                .build();
    }
}
