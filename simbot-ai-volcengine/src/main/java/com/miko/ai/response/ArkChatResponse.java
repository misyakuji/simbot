package com.miko.ai.response;

import lombok.Data;

import java.util.List;

/**
 * ArkChatResponse 类用于封装Ark(火山方舟)聊天服务的响应数据
 * 该类提供了一个数据结构来存储和管理从Ark聊天API返回的响应信息
 */
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 火山方舟聊天响应实体类，适配标准tool_calls协议 - Chat API 响应
 */
@Data
@Component
public class ArkChatResponse {
    /**
     * 响应ID，唯一标识一次聊天请求
     */
    private String id;
    
    /**
     * 选择列表，包含模型生成的多个候选回复
     */
    private List<Choice> choices;

    /**
     * 选择项类，表示模型生成的一个候选回复
     */
    @Data
    public static class Choice {
        /**
         * 选择项索引
         */
        private int index;
        
        /**
         * 消息内容
         */
        private Message message;
        
        /**
         * 结束原因：tool_calls 代表工具调用
         */
        @JsonProperty("finish_reason")
        private String finishReason;
    }

    /**
     * 消息类，包含角色、内容和工具调用信息
     */
    @Data
    public static class Message {
        /**
         * 角色：通常为"user"或"assistant"
         */
        private String role;
        
        /**
         * 消息内容
         */
        private String content;
        
        /**
         * 标准协议字段：tool_calls，映射为驼峰命名
         */
        @JsonProperty("tool_calls")
        private List<ToolCall> toolCalls;
    }

    /**
     * 工具调用对象（对应数组中的每一个元素）
     */
    @Data
    public static class ToolCall {
        /**
         * 工具调用ID
         */
        private String id;
        
        /**
         * 工具类型
         */
        private String type;
        
        /**
         * 工具名称
         */
        private String name;
        
        /**
         * 工具参数
         */
        private String arguments;
        
        /**
         * 嵌套函数对象：名称+参数字符串
         */
        private Function function;
    }

    /**
     * 函数信息：对应function节点
     */
    @Data
    public static class Function {
        /**
         * 函数名称
         */
        private String name;
        
        /**
         * 标准协议：arguments是JSON字符串，不是对象
         */
        private String arguments;
    }
}