package com.miko.entity.napcat.request;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 消息转发到群请求实体
 * 用于调用 /forward_group_single_msg 接口将消息转发到群
 */
@Data
@Accessors(chain = true)
public class ForwardGroupSingleMsgRequest {

    /**
     * 群ID
     * 支持数字或字符串类型
     */
    private String groupId;

    /**
     * 消息ID
     * 支持数字或字符串类型
     */
    private String messageId;
}
