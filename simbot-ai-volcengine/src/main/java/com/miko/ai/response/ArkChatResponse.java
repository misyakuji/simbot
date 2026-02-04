package com.miko.ai.response;

import lombok.Data;

import java.util.List;

/**
 * ArkChatResponse 类用于封装Ark(火山方舟)聊天服务的响应数据
 * 该类提供了一个数据结构来存储和管理从Ark聊天API返回的响应信息
 */
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * 火山方舟聊天响应实体类，适配标准tool_calls协议
 */
@Data
public class ArkChatResponse {
    private String id;
    private List<Choice> choices;

    @Data
    public static class Choice {
        private int index;
        private Message message;
        // 结束原因：tool_calls 代表工具调用
        @JsonProperty("finish_reason")
        private String finishReason;
    }

    @Data
    public static class Message {
        private String role;
        private String content;
        // 标准协议字段：tool_calls，映射为驼峰命名
        @JsonProperty("tool_calls")
        private List<ToolCall> toolCalls;
    }

    /**
     * 工具调用对象（对应数组中的每一个元素）
     */
    @Data
    public static class ToolCall {
        private String id;
        private String type;
        private String name;
        private String arguments;
        // 嵌套函数对象：名称+参数字符串
        private Function function;
    }

    /**
     * 函数信息：对应function节点
     */
    @Data
    public static class Function {
        private String name;
        // 标准协议：arguments是JSON字符串，不是对象
        private String arguments;
    }
}