package com.miko.ai.strategy;

import com.miko.ai.enums.ArkApiMode;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Mono;

/**
 * 火山方舟API调用策略接口
 * 定义了不同API模式下的统一调用规范
 */
public interface ArkApiStrategy {

    /**
     * 同步方式调用AI模型
     * @param prompt 聊天提示信息
     * @return 聊天响应结果
     */
    ChatResponse call(Prompt prompt);

    /**
     * 响应式方式调用AI模型
     * @param prompt 聊天提示信息
     * @return 响应式聊天响应结果
     */
    Mono<ChatResponse> reactiveCall(Prompt prompt);

    /**
     * 获取当前策略实现所支持的API模式
     * @return 支持的ArkApiMode枚举值
     */
    ArkApiMode getSupportMode();
}