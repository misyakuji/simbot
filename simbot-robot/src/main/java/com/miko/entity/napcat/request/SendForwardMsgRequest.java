package com.miko.entity.napcat.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 发送合并转发消息请求实体
 * 用于调用 /send_forward_msg 接口发送合并转发消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SendForwardMsgRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 群ID（可选）
     * 支持数字或字符串类型
     */
    private String groupId;

    /**
     * 用户ID（可选）
     * 支持数字或字符串类型
     */
    private String userId;

    /**
     * 消息列表
     */
    private List<ForwardMessageItem> messages;

    /**
     * 转发消息项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForwardMessageItem {
        /**
         * 类型
         */
        private String type;

        /**
         * 数据
         */
        private ForwardData data;
    }

    /**
     * 转发数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForwardData {
        /**
         * ID
         */
        private String id;

        /**
         * 内容
         */
        private Object content;
    }
}
