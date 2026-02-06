package com.miko.ai.factory;

import com.miko.ai.enums.ArkApiMode;
import com.miko.ai.strategy.ArkApiStrategy;
import com.miko.ai.strategy.impl.ChatApiStrategy;
import com.miko.ai.strategy.impl.ResponsesApiStrategy;
import com.miko.tool.BotToolExecutor;
import com.miko.tool.BotToolRegistry;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

/**
 * 工厂类，用于根据不同的API模式创建对应的策略实现。
 * 支持的模式包括Chat API和 Responses API。
 */
public class ArkApiStrategyFactory {
    private final Map<ArkApiMode, ArkApiStrategy> strategyMap = new HashMap<>();

    /**
     * 构造函数，初始化所有可用的API策略。
     *
     * @param webClient WebClient实例，用于HTTP请求
     * @param baseUrl   API的基础URL
     * @param model     使用的模型名称
     * @param registry  工具注册表，用于管理可用工具
     * @param executor  工具执行器，用于执行工具调用
     */
    public ArkApiStrategyFactory(WebClient webClient, String baseUrl, String model,
                                 BotToolRegistry registry, BotToolExecutor executor) {
        // 注册聊天API策略
        strategyMap.put(ArkApiMode.CHAT_API, new ChatApiStrategy(webClient, baseUrl, model, registry, executor));
        // 注册响应API策略
        strategyMap.put(ArkApiMode.RESPONSES_API, new ResponsesApiStrategy());
    }

    /**
     * 根据指定的API模式获取对应的策略实现。
     *
     * @param mode API模式枚举值
     * @return 对应的策略实现
     * @throws IllegalArgumentException 如果传入的模式不被支持
     */
    public ArkApiStrategy getStrategy(ArkApiMode mode) {
        ArkApiStrategy strategy = strategyMap.get(mode);
        if (strategy == null) {
            throw new IllegalArgumentException("不支持的API模式: " + mode);
        }
        return strategy;
    }
}
