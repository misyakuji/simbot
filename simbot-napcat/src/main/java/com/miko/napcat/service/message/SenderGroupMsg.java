package com.miko.napcat.service.message;


import com.miko.napcat.service.message.request.SendGroupMsgRequest;
import com.miko.napcat.service.message.response.SendGroupMsgResponse;

/**
 * 群消息发送接口
 * 提供群组@消息发送功能
 */
public interface SenderGroupMsg {
    /**
     * 发送群组@消息
     * @param request 发送群消息请求对象，包含目标群号和消息内容等信息
     * @return SendGroupMsgResponse 返回消息发送结果，包括消息ID等信息
     */
    SendGroupMsgResponse sendGroupAt(SendGroupMsgRequest request);

}