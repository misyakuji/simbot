package com.miko.napcat.tool;


import com.miko.napcat.service.message.SendGroupMsgService;
import com.miko.napcat.service.message.request.SendGroupMsgRequest;
import com.miko.napcat.service.message.response.SendGroupMsgResponse;
import com.miko.tool.BotTool;
import com.miko.tool.BotToolParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;


/**
 * QQ群消息发送工具类
 * 提供在指定QQ群中发送@消息的功能
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SendGroupMsgTool {

    private final SendGroupMsgService sendGroupMsgService;
    private final WebClient webClient = WebClient.create();

    /**
     * 在指定QQ群中@指定QQ号成员
     * 
     * @param groupId 群号，用于指定消息发送的目标群组
     * @param atQq 被@的QQ号，用于指定要@的群成员
     * @return 操作结果提示信息，包含成功@的成员QQ号
     * @throws IllegalArgumentException 当groupId或atQq为空时抛出
     * @throws RuntimeException 当消息发送失败时抛出
     */
    @BotTool(name = "send_group_at", description = "在指定QQ群中@指定QQ号成员，发送群@消息")
    public String sendGroupAt(@BotToolParam(name = "groupId") String groupId, @BotToolParam(name = "atQq") String atQq) {
        // 参数校验
        if (groupId == null || groupId.trim().isEmpty()) {
            throw new IllegalArgumentException("群号不能为空");
        }
        if (atQq == null || atQq.trim().isEmpty()) {
            throw new IllegalArgumentException("被@的QQ号不能为空");
        }
        
        log.info("🚨 sendGroupAt 工具方法被成功调用，群号：{}，被@QQ：{}", groupId, atQq);
        
        // 构建请求对象
        SendGroupMsgRequest request = new SendGroupMsgRequest();
        request.setGroupId(groupId);
        request.setMessage(new SendGroupMsgRequest.Message("at", new SendGroupMsgRequest.AtData(atQq, "string")));

        // 发送消息并获取响应
        SendGroupMsgResponse resp = sendGroupMsgService.sendGroupAt(request);
        
        // 返回操作结果
        return "已@成员：" + atQq;
    }
}