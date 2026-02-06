package com.miko.ai.strategy.impl;

import com.miko.ai.enums.ArkApiMode;
import com.miko.ai.strategy.ArkApiStrategy;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Mono;

/**
 * 响应API策略实现类
 * 该类实现了ArkApiStrategy接口，用于处理响应相关的API调用
 */
public class ResponsesApiStrategy implements ArkApiStrategy {
    
    /**
     * 执行聊天请求并返回响应
     * 
     * @param prompt 聊天提示信息
     * @return ChatResponse 聊天响应结果
     */
    @Override
    public ChatResponse call(Prompt prompt) {
        return null;
    }

    /**
     * 获取支持的API模式
     * 
     * @return ArkApiMode 支持的API模式枚举值
     */
    @Override
    public ArkApiMode getSupportMode() {
        return null;
    }
}
