package com.miko.service;


/**
 * 通用群消息发送接口（common模块）
 * 抽象NapCat的SendGroupMsgService核心能力，解耦具体实现
 */
public interface GroupMsgSender {
    /**
     * 发送群消息
     * @param groupId 群ID
     * @param msg 消息内容
     * @return 是否发送成功
     */
    SendGroupMsgResponse sendGroupAt(SendGroupMsgRequest request);

}