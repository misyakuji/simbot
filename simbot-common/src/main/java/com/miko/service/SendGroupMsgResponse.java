package com.miko.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发送群消息响应实体
 * 用于接收 /send_group_msg 接口的返回结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SendGroupMsgResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 状态
     */
    private String status;

    /**
     * 返回码
     */
    private Integer retcode;

    /**
     * 数据
     */
    private GroupMsgData data;

    /**
     * 消息
     */
    private String message;

    /**
     * 回声（ws调用api才有此字段）
     */
    private String echo;

    /**
     * 错误信息（go-cqhttp字段）
     */
    private String wording;

    /**
     * 流式返回标记
     */
    private String stream;

    /**
     * 数据内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupMsgData {
        /**
         * 消息ID
         */
        private Long messageId;
    }
}
