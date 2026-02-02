package com.miko.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
@Data
@Builder
public class ChatMessage {
//    public enum Role {SYSTEM, USER, ASSISTANT, FUNCTION, TOOL}

    private ChatMessageRole role;
    private Object content;
    @JsonProperty("reasoning_content")
    private String reasoningContent; // 可选
    private String name;             // 可选
    @JsonProperty("function_call")
    ChatFunctionCall functionCall;
    @JsonProperty("tool_calls")
    List<ChatToolCall> toolCalls;
    @JsonProperty("tool_call_id")
    String toolCallId;

    @Data
    @RequiredArgsConstructor
    public class ChatFunctionCall {
        String name;
        String arguments;
    }
    public enum ChatMessageRole {
        SYSTEM("system"),
        USER("user"),
        ASSISTANT("assistant"),
        FUNCTION("function"),
        TOOL("tool");

        @JsonValue
        private final String value;

        private ChatMessageRole(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }
    @Data
    @RequiredArgsConstructor
    public class ChatToolCall {
        String id;
        String type;
        ChatFunctionCall function;
        Integer index;
    }
}
