package com.miko.model;

import com.miko.request.AiRequest;

/**
 * AI模型接口，定义了AI模型的基本行为和功能。
 * 所有具体的AI模型实现类都应实现此接口。
 */
public interface AiModel {
    /**
     * 处理AI请求并返回响应结果。
     *
     * @param request AI请求对象，包含用户输入和其他相关参数
     * @return AI响应对象，包含模型处理后的结果
     */
    String chat(AiRequest request);

    /**
     * 处理AI请求并返回响应结果。
     *
     * @param id 用户ID，用于标识不同的用户会话
     * @param message 用户发送的消息内容
     * @return AI响应结果，通常为字符串形式的回复
     */
    String chat(String id, String message);
}
