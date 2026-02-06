package com.miko.ai.adapter;

import com.miko.ai.enums.ArkApiMode;
import com.miko.ai.factory.ArkApiStrategyFactory;
import com.miko.ai.strategy.ArkApiStrategy;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


/**
 * 火山方舟 ChatModel 适配器
 * 设计模式：Adapter(适配器模式)
 * 作用：将火山方舟私有API适配为Spring AI标准ChatModel接口，解决默认路径拼接404问题
 * 注解说明：@Primary 优先注入该实现，覆盖框架默认配置
 */
@Primary
@Component
public class ArkChatModelAdapter implements ChatModel {
    /**
     * 策略实例，用于处理具体的API调用逻辑
     */
    private final ArkApiStrategy strategy;

    /**
     * 构造函数，初始化适配器所需的组件
     *
     * @param apiKey          API密钥，用于认证
     * @param baseUrl         基础URL，API请求的基础路径
     * @param model           模型名称，指定使用的AI模型
     * @param apiModeStr      API模式字符串，用于确定具体的API策略
     * @param botToolRegistry 工具注册中心，管理可用的工具
     * @param botToolExecutor 工具执行器，负责执行工具调用
     */
    public ArkChatModelAdapter(
            @Value("${spring.ai.openai.api-key}") String apiKey,
            @Value("${spring.ai.openai.base-url}") String baseUrl,
            @Value("${spring.ai.openai.chat.options.model}") String model,
            @Value("${spring.ai.ark.api.chat-api}") String apiModeStr,
            com.miko.tool.BotToolRegistry botToolRegistry,
            com.miko.tool.BotToolExecutor botToolExecutor) {
        // 初始化WebClient，设置认证头
        WebClient webClient = WebClient.builder()
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();

        // 初始化策略工厂，传入必要的依赖
        ArkApiStrategyFactory factory = new ArkApiStrategyFactory(
                webClient, baseUrl, model, botToolRegistry, botToolExecutor
        );

        // 根据配置解析API模式并获取对应的策略实例
        ArkApiMode apiMode = ArkApiMode.getByValue(apiModeStr);
        this.strategy = factory.getStrategy(apiMode);
    }

    /**
     * 同步调用方法，实现Spring AI ChatModel接口
     *
     * @param prompt 聊天提示信息
     * @return 聊天响应结果
     */
    @Override
    public ChatResponse call(@NonNull Prompt prompt) {
        return strategy.call(prompt);
    }

    /**
     * 响应式调用方法，供上层服务异步使用
     *
     * @param prompt 聊天提示信息
     * @return 响应式聊天结果
     */
    public Mono<ChatResponse> reactiveCall(Prompt prompt) {
        return strategy.reactiveCall(prompt);
    }
}
