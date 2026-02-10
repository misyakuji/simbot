package com.miko.napcat.service.message.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发送群组文本消息请求类
 * 用于封装发送到指定群组的文本消息数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SendGroupTextRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    
    /**
     * 群ID - 目标群组的唯一标识符
     */
    @JsonProperty("group_id")
    private String groupId;
    
    /**
     * 消息内容 - 包含消息类型和具体文本数据的对象
     */
    private Message message;

    /**
     * 消息内部类
     * 封装消息的基本结构，包括类型和数据内容
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        /**
         * 消息类型（固定为"text"）
         * 表示这是一条纯文本消息
         */
        private String type;

        /**
         * 文本消息数据
         * 包含实际的文本内容
         */
        private TextData data;
    }

    /**
     * 文本数据内部类
     * 专门用于存储文本消息的具体内容
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TextData {
        /**
         * 文本内容
         * 实际要发送的文本消息内容
         */
        private String text;
    }
}
