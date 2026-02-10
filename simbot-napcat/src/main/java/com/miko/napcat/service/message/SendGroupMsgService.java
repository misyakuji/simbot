package com.miko.napcat.service.message;

import com.miko.napcat.service.message.request.SendGroupAtRequest;
import com.miko.napcat.service.message.request.SendGroupImageRequest;
import com.miko.napcat.service.message.request.SendGroupReplyRequest;
import com.miko.napcat.service.message.request.SendGroupTextRequest;
import com.miko.napcat.service.message.response.SendGroupMsgResponse;
import com.miko.napcat.enums.NapCatApiEnum;
import com.miko.napcat.service.NapCatApiService;
import com.miko.napcat.service.BaseApiService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 群消息发送服务类
 * 提供向指定群组发送消息的相关功能
 */
@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class SendGroupMsgService implements SenderGroupMsg {

    private final BaseApiService baseApiService;
    private final NapCatApiService napCatApiService;


    /**
     * 发送群组文本消息
     * @param request 发送群文本消息请求对象，包含目标群号和文本内容等信息
     * @return SendGroupTextMsgResponse 返回消息发送结果，包括消息ID等信息
     */
    @Override
    public SendGroupMsgResponse sendGroupText(SendGroupTextRequest request) {
        // 调用NapCat API发送群组文本消息
        return napCatApiService.callNapCatApi(NapCatApiEnum.SEND_GROUP_MSG, request, SendGroupMsgResponse.class);
    }

    /**
     * 向群组发送@消息
     * 通过调用NapCat API实现群组@功能
     *
     * @param request 包含群号、@目标等信息的请求对象
     * @return 包含消息发送结果的响应对象
     */
    public SendGroupMsgResponse sendGroupAt(SendGroupAtRequest request) {
        // 调用NapCat API发送群组@消息
        return napCatApiService.callNapCatApi(NapCatApiEnum.SEND_GROUP_AT, request, SendGroupMsgResponse.class);
    }

    /**
     * 发送群组回复消息
     * @param request 发送群回复消息请求对象，包含目标群号和回复内容等信息
     * @return SendGroupMsgResponse 返回消息发送结果，包括消息ID等信息
     */
    @Override
    public SendGroupMsgResponse sendGroupReply(SendGroupReplyRequest request) {
        // 调用NapCat API发送群组回复消息
        return napCatApiService.callNapCatApi(NapCatApiEnum.SEND_GROUP_MSG, request, SendGroupMsgResponse.class);
    }

    /**
     * 发送群组图片消息
     * @param request 发送群图片消息请求对象，包含目标群号和图片信息等
     * @return SendGroupMsgResponse 返回消息发送结果，包括消息ID等信息
     */
    @Override
    public SendGroupMsgResponse sendGroupImage(SendGroupImageRequest request) {
        // 调用NapCat API发送群组图片消息
        return napCatApiService.callNapCatApi(NapCatApiEnum.SEND_GROUP_MSG, request, SendGroupMsgResponse.class);
    }

}