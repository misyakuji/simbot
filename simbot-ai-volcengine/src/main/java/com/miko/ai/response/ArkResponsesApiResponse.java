package com.miko.ai.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 火山方舟 Responses API 响应数据封装类
 * 用于解析和映射火山方舟 API 返回的响应数据结构
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ArkResponsesApiResponse {

    /**
     * 响应唯一标识符
     */
    private String id;
    
    /**
     * 使用的模型名称
     */
    private String model;
    
    /**
     * 上一个响应的 ID，用于对话上下文追踪
     */
    private String previous_response_id;
    
    /**
     * 消息输出列表，包含完整的响应内容
     */
    private List<Message> output;

    /**
     * 消息实体类，表示单条响应消息
     */
    @Data
    public static class Message {
        /**
         * 消息唯一标识符
         */
        private String id;
        
        /**
         * 消息摘要列表
         */
        private List<Summary> summary;
        
        /**
         * 消息内容列表
         */
        private List<Content> content;

        /**
         * 调用ID，用于关联特定的函数调用
         */
        @JsonProperty("call_id")
        private String callId;
        
        /**
         * 消息类型标识
         */
        private String type;
        
        /**
         * 函数名称（当类型为function_call时使用）
         */
        private String name;
        
        /**
         * 函数调用参数（JSON格式字符串）
         */
        private String arguments;

    }

    /**
     * 内容实体类，表示消息的具体内容
     */
    @Data
    public static class Content {
        /**
         * 内容文本
         */
        private String text;
        
        /**
         * 内容类型（如text、image_url等）
         */
        private String type;
    }

    /**
     * 摘要实体类，表示消息的摘要信息
     */
    @Data
    public static class Summary {
        /**
         * 摘要文本内容
         */
        private String text;
        
        /**
         * 摘要类型标识
         */
        private String type;
    }
}
