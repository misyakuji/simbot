package com.miko.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发送群消息请求实体
 * 用于接收 /send_group_msg 接口的返回结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SendGroupMsgRequest  implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 群ID
     */
    @JsonProperty("group_id")
    private String groupId;
    /**
     * 消息内容
     */
    private Message message;

    /**
     * 数据内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message  {
        /**
         * 消息类型（固定为"at"）
         */
        private String type;

        /**
         * at的具体数据（嵌套对象）
         */
        private AtData data;
    }
    /**
     * 数据内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AtData  {
        /**
         * 被@的QQ号（可为空）
         */
        private String qq;

        /**
         * 被@的昵称（可为空）
         */
        private String name;
    }
}
