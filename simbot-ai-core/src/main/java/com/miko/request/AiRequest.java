package com.miko.request;

import com.miko.message.AiMessage;
import com.miko.tool.ToolDefinition;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * AI请求类，用于封装发送给AI模型的请求参数。
 */
@Data
@Builder
public class AiRequest {
    /**
     * 指定要使用的AI模型名称。
     */
    private String model;

    /**
     * 包含对话历史的消息列表。
     */
    private List<AiMessage> messages;

    /**
     * 可用工具的定义列表。
     */
    private List<ToolDefinition> tools;

    /**
     * 控制生成文本随机性的温度参数，值越高结果越随机。
     */
    private Double temperature;

    /**
     * 生成文本的最大令牌数限制。
     */
    private Integer maxTokens;
}
