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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 聊天API策略实现类，负责处理与火山引擎Ark模型的交互。
 * 支持同步和异步调用，并能处理工具调用。
 */
@Component
public class ChatApiStrategy implements ArkApiStrategy {
    private static final Logger log = LoggerFactory.getLogger(ChatApiStrategy.class);
    private final WebClient webClient;
    private final String baseUrl;

    private final String model;
    private final BotToolRegistry botToolRegistry;
    private final BotToolExecutor botToolExecutor;
    private final ObjectMapper objectMapper = new ObjectMapper();
   private final String apiKey  ="7793e4ac-c8ae-4430-8aa4-9272dd64b712";


    public ChatApiStrategy(WebClient webClient, @Value("${spring.ai.openai.base-url}") String baseUrl, @Value("${spring.ai.openai.chat.options.model}") String model, BotToolRegistry botToolRegistry, BotToolExecutor botToolExecutor) {
        this.baseUrl = baseUrl;
        this.model = model;
        this.botToolRegistry = botToolRegistry;
        this.botToolExecutor = botToolExecutor;

        this.webClient = webClient = WebClient.builder()
                .baseUrl(baseUrl) // 可以加上默认 baseUrl
                .defaultHeader("Authorization", "Bearer " + apiKey) // 🔑 核心
                .build();
    }

    /**
     * 同步调用方法，阻塞等待响应结果。
     *
     * @param prompt 用户输入的提示信息
     * @return 聊天响应对象
     */
    @Override
    public ChatResponse call(Prompt prompt) {
        return reactiveCall(prompt)
                .timeout(Duration.ofSeconds(ArkChatConstants.DEFAULT_TIMEOUT_SECONDS))
                .block(); // 阻塞返回
    }

    /**
     * 异步响应式调用方法，返回Mono类型的响应。
     *
     * @param prompt 用户输入的提示信息
     * @return Mono类型的聊天响应
     */
//    @Override
    public Mono<ChatResponse> reactiveCall同步(Prompt prompt) {
        // 将Spring AI的Prompt转换为Ark模型可识别的消息格式
        List<Map<String, String>> arkMessages = ArkMessageConverter.convertToArkMessages(prompt);

        // 构建工具定义的JSON结构
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

    /**
     * 获取支持的API模式。
     *
     * @return 支持的Ark API模式
     */
    @Override
    public ArkApiMode getSupportMode() {
        return ArkApiMode.CHAT_API;
    }

    /**
     * 解析Ark模型的响应并执行相关工具。
     *
     * @param arkResponse Ark模型的原始响应
     * @return 处理后的聊天响应
     */
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

    /**
     * 处理工具调用，同步执行所有工具并收集结果。
     *
     * @param message 包含工具调用信息的消息对象
     * @return 包含工具执行结果的助理消息
     */
    private AssistantMessage handleToolCalls(ArkChatResponse.Message message) {
        List<AssistantMessage.ToolCall> toolCalls = new ArrayList<>();
        List<Map<String, Object>> results = new ArrayList<>();

        for (var tc : message.getToolCalls()) {
            String toolName = tc.getFunction().getName();
            String arguments = tc.getFunction().getArguments();

            Map<String, Object> argsMap;
            try {
                argsMap = objectMapper.readValue(arguments, new TypeReference<>() {
                });
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

    /*
    2026.2.6 19:30 新增：测试异步调用
     */
    private Mono<AssistantMessage> handleToolCallsAsync(ArkChatResponse.Message message) {
        List<AssistantMessage.ToolCall> toolCalls = new ArrayList<>();

        // 使用Flux顺序执行所有工具
        return Flux.fromIterable(message.getToolCalls())
                .flatMap(tc -> {
                    String toolName = tc.getFunction().getName();
                    String arguments = tc.getFunction().getArguments();

                    Map<String, Object> argsMap;
                    try {
                        argsMap = objectMapper.readValue(arguments, new TypeReference<>() {
                        });
                    } catch (JsonProcessingException e) {
                        return Mono.error(new RuntimeException("解析工具参数失败: " + arguments, e));
                    }

                    toolCalls.add(new AssistantMessage.ToolCall(tc.getId(), tc.getType(), toolName, arguments));

                    // 异步执行工具
                    return botToolExecutor.executeAsync(toolName, argsMap)
                            .map(result ->{
                                log.info("异步执行工具成功: {}", result.toString());
                                return Map.of("toolName", toolName, "result", result.toString());
                            });
                })
                .collectList() // 收集所有工具执行结果
                .map(results -> AssistantMessage.builder()
                        .content(ArkChatConstants.TOOL_TRACE_PREFIX + ": Completed :" + results)
                        .toolCalls(toolCalls)
                        .build()
                );
    }

    /*
    2026.2.6 19:30 新增：测试异步调用
     */
    @Override
    public Mono<ChatResponse> reactiveCall(Prompt prompt) {
        List<Map<String, String>> arkMessages = ArkMessageConverter.convertToArkMessages(prompt);

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
                .flatMap(arkResponse -> {
                    ArkChatResponse.Message message = arkResponse.getChoices().getFirst().getMessage();
                    if (message.getToolCalls() != null && !message.getToolCalls().isEmpty()) {
                        return handleToolCallsAsync(message) // 异步执行工具
                                .map(assistantMessage -> {
                                    Generation generation = new Generation(assistantMessage);
                                    return ChatResponse.builder()
                                            .generations(List.of(generation))
                                            .metadata("model", "volc-ark-" + model)
                                            .build();
                                });
                    } else {
                        AssistantMessage assistantMessage = new AssistantMessage(message.getContent());
                        Generation generation = new Generation(assistantMessage);
                        return Mono.just(ChatResponse.builder()
                                .generations(List.of(generation))
                                .metadata("model", "volc-ark-" + model)
                                .build());
                    }
                })
                .onErrorResume(e -> {
                    AssistantMessage fallback = new AssistantMessage("接口调用异常，请稍后重试: " + e.getMessage());
                    return Mono.just(ChatResponse.builder()
                            .generations(List.of(new Generation(fallback)))
                            .build());
                });
    }


}
