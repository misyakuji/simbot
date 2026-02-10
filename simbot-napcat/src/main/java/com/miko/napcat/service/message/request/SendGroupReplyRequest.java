package com.miko.napcat.service.message.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 发送群回复消息请求类
 * 用于封装发送群回复消息的参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SendGroupReplyRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 目标群号
     * 用于指定消息发送的目标群组
     */
    @JsonProperty("group_id")
    private String groupId;

    /**
     * 消息内容
     * 包含要发送的消息列表
     */
    private List<Message> message;

    /**
     * 消息对象
     * 包含消息类型和具体数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        /**
         * 消息类型
         * 例如：text、image、at等
         */
        private String type;

        /**
         * 消息数据
         * 根据消息类型不同，包含不同的数据结构
         */
        private TextData data;
    }

    /**
     * 文本数据
     * 用于文本类型消息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TextData {
        /**
         * 消息ID
         * 用于标识特定的消息
         */
        private String id;
        
        /**
         * 文本内容
         * 实际要发送的文本信息
         */
        private String text;

        /**
         * 构造函数
         * @param text 文本内容
         */
        public TextData(String text) {
            this.text = text;
        }
    }
}
