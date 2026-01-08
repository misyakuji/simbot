package com.miko.listener;

import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.common.id.ID;
import love.forte.simbot.common.id.Identifies;
import love.forte.simbot.component.onebot.v11.core.api.OneBotMessageOutgoing;
import love.forte.simbot.component.onebot.v11.core.api.SendGroupMsgApi;
import love.forte.simbot.component.onebot.v11.core.api.SendPrivateMsgApi;
import love.forte.simbot.component.onebot.v11.core.event.request.OneBotFriendRequestEvent;
import love.forte.simbot.component.onebot.v11.core.event.request.OneBotGroupRequestEvent;
import love.forte.simbot.component.onebot.v11.core.event.stage.OneBotBotStartedEvent;
import love.forte.simbot.event.Event;
import love.forte.simbot.quantcat.common.annotations.Listener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NapCatEventListener {


    @Listener
    public void handle(Event event) {
        log.debug("{}", event);
    }

    @Listener
    public void handle(OneBotBotStartedEvent event) {
        ID masterId = Identifies.of(67252271);
        ID groupId = Identifies.of(710117186);
        log.info("Bot[{}:{}]启动成功~", event.getBot().getName(), event.getBot().getId());
        event.getBot().executeAsync(SendPrivateMsgApi.create(masterId,OneBotMessageOutgoing.create("本宝闪亮登场~")));
        // event.getBot().executeAsync(SendGroupMsgApi.create(groupId,OneBotMessageOutgoing.create("本宝闪亮登场~")));
    }

    @Listener
    public void friendRequestEvent(OneBotFriendRequestEvent event) {
        log.info("收到好友申请{}", event.getSourceEvent().getUserId());
        event.acceptAsync();//接受好友申请
//        event.rejectAsync();//拒绝好友申请
    }

    @Listener
    public void groupRequestEvent(OneBotGroupRequestEvent event) {
        log.info("收到入群申请{}", event);
        event.acceptAsync();//接受入群申请
//        event.rejectAsync();//拒绝入群申请
    }

}
