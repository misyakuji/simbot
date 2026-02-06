package com.miko.ai.strategy;

import com.miko.ai.enums.ArkApiMode;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Mono;

/**
 * 火山方舟API调用接口
 * 定义了与火山方舟AI服务交互的通用方法
 */
public interface ArkApiStrategy {
    /**
     * 同步调用火山方舟AI服务
     * @param prompt 包含用户输入和上下文的提示信息
     * @return ChatResponse AI服务返回的聊天响应
     */
    ChatResponse call(Prompt prompt);

    /**
     * 获取当前策略支持的API模式
     * @return ArkApiMode 返回该策略支持的API模式枚举值
     */
    ArkApiMode getSupportMode();
}
