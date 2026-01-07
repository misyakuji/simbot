package com.miko.listener;

import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.component.onebot.v11.core.api.OneBotMessageOutgoing;
import love.forte.simbot.component.onebot.v11.core.api.SendGroupMsgApi;
import love.forte.simbot.component.onebot.v11.core.api.SendPrivateMsgApi;
import love.forte.simbot.component.onebot.v11.core.event.notice.*;
import love.forte.simbot.quantcat.common.annotations.Listener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NoticeEventListener {
    
     /**
     * 监听通知事件
     */
    @Listener
    public void handle(OneBotNoticeEvent event) {
        log.info("{}", event);
    }

    /**
     * 监听好友新增事件
     */
    @Listener
    public void friendAddEvent(OneBotFriendAddEvent event) {
        log.info("好友新增事件{}", event);
        event.getBot().executeAsync(SendPrivateMsgApi.create(event.getUserId(), OneBotMessageOutgoing.create("欢迎新朋友！")));
    }

    /**
     * 监听群管理员变动事件
     */
    @Listener
    public void groupAdminEvent(OneBotGroupAdminEvent event) {
        log.info("群管理员变动事件{}", event);
    }

    /**
     * 监听群成员增加或减少事件
     */
    @Listener
    public void groupChangeEvent(OneBotGroupChangeEvent event) {
        log.info("群成员增加或减少事件{}", event);
        event.getContent().sendAsync("欢迎新来的~");
    }

    /**
     * 监听群禁言事件
     */
    @Listener
    public void groupBanEvent(OneBotGroupBanEvent event) {
        log.info("群禁言事件{}", event);
        event.getContent().sendAsync("喜提禁言哈哈");
    }

     /**
     * 监听群消息撤回事件
     */
    @Listener
    public void groupRecallEvent(OneBotGroupRecallEvent event) {
        log.info("群消息撤回事件{}", event);
        event.getContent().sendAsync("撤回了啥，让我瞅瞅");
    }

    /**
     * 监听群文件上传事件
     */
    @Listener
    public void groupUploadEvent(OneBotGroupUploadEvent event) {
        log.info("群文件上传事件{}", event);
        event.getContent().sendAsync("上传了啥好东西，让我瞅瞅");
    }

    /**
     * 监听好友消息撤回事件
     */
    @Listener
    public void friendRecallEvent(OneBotFriendRecallEvent event) {
        log.info("好友消息撤回事件{}", event);
        event.getContent().sendAsync("撤回了啥，让我瞅瞅");
    }

    /**
     * 监听群成员荣誉变更事件、红包人气王事件或戳一戳事件
     */
    @Listener
    public void notifyEvent(OneBotNotifyEvent event) {
        if (event.getSourceEvent().getNoticeType().equals("notify")) {
            if (event.getGroupId() == null) {
                log.info("好友戳一戳事件{}", event);
                event.getBot().executeAsync(SendPrivateMsgApi.create(event.getUserId(), OneBotMessageOutgoing.create("不許戳我！")));
            } else {
                log.info("群戳一戳事件{}", event);
                event.getBot().executeAsync(SendGroupMsgApi.create(event.getGroupId(), OneBotMessageOutgoing.create("不許戳我！")));
            }
        }
    }
}
