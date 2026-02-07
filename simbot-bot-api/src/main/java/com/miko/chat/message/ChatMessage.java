package com.miko.chat.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 聊天消息实体类，用于表示一次聊天交互中的消息内容。
 * 包含角色、内容、工具调用等相关信息。
 */
@Data
@Builder
public class ChatMessage {

    /**
     * 消息发送者的角色，如系统、用户或助手。
     */
    private ChatMessageRole role;

    /**
     * 消息的具体内容，可以是文本、图像或其他数据类型。
     */
    private Object content;

    /**
     * 推理内容，通常用于记录模型内部的推理过程或中间结果。
     */
    @JsonProperty("reasoning_content")
    private String reasoningContent;

    /**
     * 发送者的名字，可用于标识消息来源。
     */
    private String name;

    /**
     * 函数调用信息，表示本次消息触发的函数调用详情。
     */
    @JsonProperty("function_call")
    ChatFunctionCall functionCall;

    /**
     * 工具调用列表，表示本次消息涉及的多个工具调用。
     */
    @JsonProperty("tool_calls")
    List<ChatToolCall> toolCalls;

    /**
     * 工具调用ID，用于唯一标识某次工具调用。
     */
    @JsonProperty("tool_call_id")
    String toolCallId;

}
