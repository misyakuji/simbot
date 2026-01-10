package com.miko.entity.napcat.request;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 获取消息详情请求实体
 * 用于调用 /get_msg 接口获取消息详情
 */
@Data
@Accessors(chain = true)
public class GetMsgRequest {

    /**
     * 消息ID
     * 支持数字或字符串类型
     */
    private String messageId;
}
