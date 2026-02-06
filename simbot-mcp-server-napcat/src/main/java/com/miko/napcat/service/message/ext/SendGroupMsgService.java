package com.miko.napcat.service.message.ext;

import com.miko.service.GroupMsgSender;
import com.miko.service.SendGroupMsgRequest;
import com.miko.service.SendGroupMsgResponse;
import com.miko.napcat.enums.NapCatApiEnum;
import com.miko.napcat.service.NapCatApiService;
import com.miko.service.BaseApiService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 发送群消息相关服务
 */
@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class SendGroupMsgService implements GroupMsgSender {

    private final BaseApiService baseApiService;
    private final NapCatApiService napCatApiService;

    /**
     * 发送群艾特
     *
     * @param request 请求对象
     * @return 响应对象
     */


    /*
    2026.2.6 19:30 新增：测试异步调用
     */
    public Mono<SendGroupMsgResponse> sendGroupAt(SendGroupMsgRequest request) {
         return napCatApiService.callNapCatApiReactive(NapCatApiEnum.SEND_GROUP_AT, request, SendGroupMsgResponse.class);
    }

}
