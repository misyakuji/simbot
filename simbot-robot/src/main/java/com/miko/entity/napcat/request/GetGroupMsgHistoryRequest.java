package com.miko.entity.napcat.request;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 获取群历史消息请求实体
 * 用于调用 /get_group_msg_history 接口获取群聊历史消息
 */
@Data
@Accessors(chain = true)
public class GetGroupMsgHistoryRequest {

    /**
     * 群ID
     * 支持数字或字符串类型
     */
    private String groupId;

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
