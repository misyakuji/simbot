package com.miko.listener;


import com.miko.service.ArkDoubaoService;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotFriendMessageEvent;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotGroupMessageEvent;
import love.forte.simbot.event.Event;
import love.forte.simbot.event.MessageEvent;
import love.forte.simbot.quantcat.common.annotations.Listener;

import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NapCatListener {

    private final ArkDoubaoService arkDoubaoService;

    public NapCatListener(ArkDoubaoService arkDoubaoService) {
        this.arkDoubaoService = arkDoubaoService;
    }

    @Listener
    public void handle(Event event) {
        log.info("{}", event);
    }

    @Listener
    public void msgEvent(MessageEvent event) {
        log.info("msgEvent: {}", event);

    }

    @Listener
    public void groupMsgEvent(OneBotGroupMessageEvent event) {
        log.info("群聊消息: {}", event.getSourceEvent().getMessage());
    }

    @Listener
    public void friendMsgEvent(OneBotFriendMessageEvent event) {
        log.info("接收 <- ({}-{}) {}", event.getSubType(),event.getAuthorId(),event.getMessageContent().getPlainText());
    }

    @Listener
    public void doubaoChat(OneBotFriendMessageEvent event) {
        if (event.getMessageContent().getPlainText().equals("获取模型列表")) {
            event.replyAsync(arkDoubaoService.getModelList().toString());
            return;
        }
        String value = arkDoubaoService.streamChatWithDoubao(event.getMessageContent().getPlainText());
//        String value = "你好";
        log.info("发送 -> {} - {}", event.getId(),value);

        event.replyAsync(value);
    }
}

//        event.getSourceEvent().getMessage().forEach(msg -> {
//            if (msg.toString().startsWith("OneBotText")) {
//                event.replyAsync(event.getMessageContent().getPlainText());
//            } else if (msg.toString().startsWith("OneBotImage")) {
//                event.replyAsync(event.getMessageContent().getMessages());
//            } else if (msg.toString().startsWith("OneBotFace")) {
//                event.replyAsync(event.getMessageContent().getMessages());
//            } else if (msg.toString().startsWith("OneBotJson")) {
//                event.replyAsync(event.getMessageContent().getMessages());
//            } else if (msg.toString().startsWith("OneBotFile")) {
//                event.replyAsync("这是啥呀");
//            }
//        });
//        event.replyAsync(event.getMessageContent().getMessages());