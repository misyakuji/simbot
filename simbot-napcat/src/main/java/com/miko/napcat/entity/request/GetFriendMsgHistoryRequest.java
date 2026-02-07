package com.miko.napcat.entity.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 获取好友历史消息请求实体
 * 用于调用 /get_friend_msg_history 接口获取好友历史消息
 */
@Data
@Accessors(chain = true)
public class GetFriendMsgHistoryRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     * 支持数字或字符串类型
     */
    private String userId;

    /**
     * 消息序列号
     * 0为最新
     * 支持数字或字符串类型
     */
    private String messageSeq;

    /**
     * 数量
     * 默认值：20
     */
    private Integer count = 20;

    /**
     * 倒序
     * 默认值：false
     */
    private Boolean reverseOrder = false;
}
