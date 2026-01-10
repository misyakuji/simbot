package com.miko.entity.napcat.request;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 获取合并转发消息请求实体
 * 用于调用 /get_forward_msg 接口获取合并转发消息
 */
@Data
@Accessors(chain = true)
public class GetForwardMsgRequest {

    /**
     * 消息ID
     * 支持数字或字符串类型
     */
    private String messageId;
}
