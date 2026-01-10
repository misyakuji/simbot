package com.miko.entity.napcat.request;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 撤回消息请求实体
 * 用于调用 /delete_msg 接口撤回消息
 */
@Data
@Accessors(chain = true)
public class DeleteMsgRequest {

    /**
     * 消息ID
     * 支持数字或字符串类型
     */
    private String messageId;
}
