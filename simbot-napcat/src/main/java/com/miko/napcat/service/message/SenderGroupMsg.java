package com.miko.napcat.service.message;


import com.miko.napcat.service.message.request.SendGroupAtRequest;
import com.miko.napcat.service.message.request.SendGroupImageRequest;
import com.miko.napcat.service.message.request.SendGroupReplyRequest;
import com.miko.napcat.service.message.request.SendGroupTextRequest;
import com.miko.napcat.service.message.response.SendGroupMsgResponse;

/**
 * 群消息发送接口
 */
public interface SenderGroupMsg {
    /**
     * 发送群组文本消息
     * @param request 发送群文本消息请求对象，包含目标群号和文本内容等信息
     * @return SendGroupTextMsgResponse 返回消息发送结果，包括消息ID等信息
     */
    SendGroupMsgResponse sendGroupText(SendGroupTextRequest request);
    /**
     * 发送群组@消息
     * @param request 发送群消息请求对象，包含目标群号和消息内容等信息
     * @return SendGroupMsgResponse 返回消息发送结果，包括消息ID等信息
     */
    SendGroupMsgResponse sendGroupAt(SendGroupAtRequest request);
    /**
     * 发送群组回复消息
     * @param request 发送群回复消息请求对象，包含目标群号、回复消息ID和消息内容等信息
     * @return SendGroupReplyMsgResponse 返回消息发送结果，包括消息ID等信息
     */
    SendGroupMsgResponse sendGroupReply(SendGroupReplyRequest request);

    /**
     * 发送群组图片消息
     * @param request 发送群图片消息请求对象，包含目标群号和图片内容等信息
     * @return SendGroupImageMsgResponse 返回消息发送结果，包括消息ID等信息
     */
    SendGroupMsgResponse sendGroupImage(SendGroupImageRequest request);

}