package com.miko.entity.napcat.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 消息转发到私聊请求实体
 * 用于调用 /forward_friend_single_msg 接口将消息转发到私聊
 */
@Data
@Accessors(chain = true)
public class ForwardFriendSingleMsgRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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
