package com.miko.napcat.service.message;

import com.miko.napcat.service.message.request.SendGroupMsgRequest;
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
     * 向群组发送@消息
     * 通过调用NapCat API实现群组@功能
     *
     * @param request 包含群号、@目标等信息的请求对象
     * @return 包含消息发送结果的响应对象
     */
    public SendGroupMsgResponse sendGroupAt(SendGroupMsgRequest request) {
        return napCatApiService.callNapCatApi(NapCatApiEnum.SEND_GROUP_AT, request, SendGroupMsgResponse.class);
    }

}