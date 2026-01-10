package com.miko.entity.napcat.request;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 消息转发到私聊请求实体
 * 用于调用 /forward_friend_single_msg 接口将消息转发到私聊
 */
@Data
@Accessors(chain = true)
public class ForwardFriendSingleMsgRequest {

    /**
     * 用户ID
     * 支持数字或字符串类型
     */
    private String userId;

    /**
     * 消息ID
     * 支持数字或字符串类型
     */
    private String messageId;
}
