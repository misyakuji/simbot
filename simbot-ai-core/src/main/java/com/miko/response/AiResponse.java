package com.miko.response;

import com.miko.tool.ToolCall;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * AI响应类，用于封装AI模型的响应结果。
 */
@Data
@Builder
public class AiResponse {
    /**
     * 响应内容，通常是AI生成的文本。
     */
    private String content;

    /**
     * 工具调用列表，表示AI在生成响应时调用的工具。
     */
    private List<ToolCall> toolCalls;

    /**
     * 结束原因，表示AI生成响应的结束状态（如完成、中断等）。
     */
    private String finishReason;
}
