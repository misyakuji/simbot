package com.miko.listener;


import love.forte.simbot.component.onebot.v11.core.event.message.OneBotFriendMessageEvent;
import love.forte.simbot.component.onebot.v11.core.event.stage.OneBotBotRegisteredEvent;
import love.forte.simbot.event.Event;
import love.forte.simbot.quantcat.common.annotations.Listener;
import org.springframework.stereotype.Component;

@Component
public class NapCatListener {

    @Listener // 注解API，标记一个函数为事件处理函数
    public void handle(Event event) {
        System.out.println(event);
        // ...
    }
    public void BotRegisteredHandle(OneBotBotRegisteredEvent event) {

        System.out.println(event);
        // ...
    }
    @Listener // 复读
    public void msgEvent(OneBotFriendMessageEvent event) {
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
        event.replyAsync(event.getMessageContent().getMessages());
    }
}
