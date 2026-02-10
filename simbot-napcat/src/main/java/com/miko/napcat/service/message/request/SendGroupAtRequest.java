package com.miko.napcat.service.message.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发送群艾特请求实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SendGroupAtRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 群ID，用于标识目标群聊
     */
    @JsonProperty("group_id")
    private String groupId;

    /**
     * 消息内容，包含消息类型和具体数据
     */
    private Message message;

    /**
     * 消息内容内部类
     * 用于封装消息的具体结构
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        /**
         * 消息类型，固定为"at"
         */
        private String type;

        /**
         * at的具体数据，包含被@用户的QQ号和昵称
         */
        private AtData atData;
    }

    /**
     * 艾特数据内部类
     * 用于存储被@用户的详细信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AtData {
        /**
         * 被@的QQ号，可为空
         */
        private String qq;

        /**
         * 被@的昵称，可为空
         */
        private String name;
    }
}
