package com.miko.listener;


import com.miko.enums.CQFaceEnum;
import com.miko.service.ArkDoubaoService;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.common.id.ID;
import love.forte.simbot.common.id.Identifies;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotFriendMessageEvent;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotGroupMessageEvent;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotMessageEvent;
import love.forte.simbot.component.onebot.v11.message.segment.*;
import love.forte.simbot.quantcat.common.annotations.ContentTrim;
import love.forte.simbot.quantcat.common.annotations.Filter;
import love.forte.simbot.quantcat.common.annotations.Listener;

import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class MessageEventListener {

    private final ArkDoubaoService arkDoubaoService;

    public MessageEventListener(ArkDoubaoService arkDoubaoService) {
        this.arkDoubaoService = arkDoubaoService;
    }

    @Listener
    public void msgEvent(OneBotMessageEvent event) {
        log.debug("msgEvent: {}", event);
    }

    @Listener
    @ContentTrim
//    @Filter("你好")
    public void groupMsgEvent(OneBotGroupMessageEvent event) {

        // 群昵称
        String groupNickname = event.getContent().getName();
        // 群ID
        String groupId = event.getContent().getId().toString();
        // 群友ID
        ID groupMemberId = event.getAuthorId();
        // 群友昵称
        String groupMemberNickname = Objects.requireNonNull(event.getContent().getMember(groupMemberId)).getNick();
        //可能未设置群昵称，使用用户名代替
        if (groupMemberNickname == null || groupMemberNickname.isEmpty()) {
            groupMemberNickname = Objects.requireNonNull(event.getContent().getMember(groupMemberId)).getName();
        }
        // Bot群昵称
        String botNickname = event.getContent().getBotAsMember().getNick();
        if (botNickname == null || botNickname.isEmpty()) {
            botNickname = event.getBot().getName();
        }
        // BotID
        String botId = event.getBot().getId().toString();
        //默认不回复群消息
        AtomicBoolean isReply = new AtomicBoolean(false);
        // 消息内容
        StringBuilder msgfix = new StringBuilder();
        event.getSourceEvent().getMessage().forEach(msg -> {
            if (msg instanceof OneBotText text) {
                msgfix.append(text.getData().getText());
            } else if (msg instanceof OneBotImage image) {
                msgfix.append("img[").append(image.getData().getUrl()).append("]");
            } else if (msg instanceof OneBotFace face) {
                if (CQFaceEnum.isExist(face.getData().getId())){
                    msgfix.append("face[").append(CQFaceEnum.getFaceTextByID(face.getData().getId())).append("]");
                }
            } else if (msg instanceof OneBotJson json) {
                msgfix.append("[Json]").append(json.getData().getData());
            } else if (msg instanceof OneBotAt at) {
                String nick = Objects.requireNonNull(event.getContent().getMember(Identifies.of(at.getData().getQq()))).getNick();
                if (nick == null || nick.isEmpty()) {
                    nick = Objects.requireNonNull(event.getContent().getMember(Identifies.of(at.getData().getQq()))).getName();
                }
                msgfix.append("@").append(nick);
                // 如果at对象是bot自己，则回复消息
                if (Objects.equals(at.getData().getQq(), botId)) {
                    isReply.set(true);
                }
            }
        });
        log.info("接收 <- 群聊 [{}({})] [{}({})] {}", groupNickname, groupId, groupMemberNickname, groupMemberId, msgfix);
        if (isReply.get()) {
            String reply = arkDoubaoService.streamChatWithDoubao(String.valueOf(msgfix));
            event.replyAsync(reply);
//            event.getContent().sendAsync(reply);
            log.info("回复 -> 群聊 [{}({})] [{}({})] {}", groupNickname, groupId, botNickname, botId, reply);
        }


    }

//    @Listener
//    @Filter("cmd")
//    public void friendMsgCmdEvent(OneBotFriendMessageEvent event) throws InterruptedException {
//        event.getContent().sendAsync("执行命令:"+ event.getSourceEvent().getMessage().getFirst());
//    }
    @Listener
    public void friendMsgEvent(OneBotFriendMessageEvent event) {
        // 好友ID
        ID friendId = event.getAuthorId();
        // 好友昵称
        String friendNickname = event.getSourceEvent().getSender().getNickname();
        // 消息内容
        StringBuilder msgfix = new StringBuilder();
        event.getSourceEvent().getMessage().forEach(msg -> {
            if (msg instanceof OneBotText text) {
                msgfix.append(text.getData().getText());
            } else if (msg instanceof OneBotImage image) {
                msgfix.append("img[").append(image.getData().getUrl()).append("]");
            } else if (msg instanceof OneBotFace face) {
                if (CQFaceEnum.isExist(face.getData().getId())){
                    msgfix.append("face[").append(CQFaceEnum.getFaceTextByID(face.getData().getId())).append("]");
                }
            } else if (msg instanceof OneBotJson json) {
                msgfix.append("[Json]").append(json.getData().getData());
            }
        });
        // 接收 <- 私聊 (67252271) 获取模型列表
        log.info("接收 <- 私聊 [{}({})] {}", friendNickname, friendId, msgfix);
        String value;
        if (Objects.equals(String.valueOf(msgfix), "获取模型列表")) {
            value = arkDoubaoService.getModelList().toString();
        } else {
            value = "arkDoubaoService.streamChatWithDoubao(String.valueOf(msgfix))";
        }
        log.info("发送 -> {} - {}", event.getId(),value);
        event.replyAsync(value);
        //event.getContent().sendAsync(value);
    }
}