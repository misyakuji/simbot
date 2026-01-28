package com.miko.listener;

import com.miko.entity.napcat.response.GetFriendsWithCategoryResponse;
import com.miko.service.NapCatApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.common.id.ID;
import love.forte.simbot.common.id.Identifies;
import love.forte.simbot.component.onebot.v11.core.api.OneBotMessageOutgoing;
import love.forte.simbot.component.onebot.v11.core.api.SendGroupMsgApi;
import love.forte.simbot.component.onebot.v11.core.api.SendPrivateMsgApi;
import love.forte.simbot.component.onebot.v11.core.event.notice.*;
import love.forte.simbot.component.onebot.v11.core.event.request.OneBotFriendRequestEvent;
import love.forte.simbot.component.onebot.v11.core.event.request.OneBotGroupRequestEvent;
import love.forte.simbot.component.onebot.v11.core.event.stage.OneBotBotStartedEvent;
import love.forte.simbot.event.Event;
import love.forte.simbot.quantcat.common.annotations.Listener;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NapCatEventListener {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final NapCatApiService napCatApiService;

    @Listener
    public void handle(Event event) {
        log.debug("{}", event);
    }

    @Listener
    public void handle(OneBotBotStartedEvent event) {
        log.info("Bot[{}:{}]启动成功~", event.getBot().getName(), event.getBot().getId());
        love.forte.simbot.component.onebot.v11.core.bot.OneBotBot bot = event.getBot();
//        bot.getGroupRelation().getGroups().collectAsync(
//                bot, group -> group.sendAsync("本宝闪亮登场~")
//        );
        List<ID> masters = new ArrayList<>();
        // 调用API获取好友列表并流式处理，一站式完成：查找特别关心分组→过滤非空好友→转换为ID集合
        Optional.ofNullable(napCatApiService.getFriendsWithCategory())
                // 提取data，空则终止流
                .map(GetFriendsWithCategoryResponse::getData)
                // 过滤出"特别关心"分组（精准匹配）
                .stream()
                .flatMap(List::stream)
                .filter(category -> "特别关心".equals(category.getCategoryName()))
                .findFirst()
                // 提取好友列表，空则兜底为空集合（避免后续空判断）
                .map(GetFriendsWithCategoryResponse.FriendCategory::getBuddyList)
                .orElse(List.of())
                // 过滤好友列表中的null元素（双重空安全）
                .stream()
                .filter(java.util.Objects::nonNull)
                // 转换为ID并收集到目标列表
                .map(friend -> Identifies.of(friend.getUser_id()))
                .forEach(masters::add);
        // 发送消息给特别关分组中的好友
        bot.getContactRelation().getContacts().collectAsync(
                bot, friend -> {
                    if (masters.contains(friend.getId())) {
                        friend.sendAsync("本宝闪亮登场~");
                    }
                }
        );
    }

    /**
     * 监听好友申请事件
     */
    @Listener
    public void friendRequestEvent(OneBotFriendRequestEvent event) {
        log.info("收到好友申请{}", event.getSourceEvent().getUserId());
        event.acceptAsync();//接受好友申请
//        event.rejectAsync();//拒绝好友申请
    }

    /**
     * 监听入群申请事件
     */
    @Listener
    public void groupRequestEvent(OneBotGroupRequestEvent event) {
        log.info("收到入群申请{}", event);
        event.acceptAsync();//接受入群申请
//        event.rejectAsync();//拒绝入群申请
    }

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
//        event.getContent().sendAsync("撤回了啥，让我瞅瞅");
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
                event.getBot().executeAsync(SendPrivateMsgApi.create(event.getUserId(), OneBotMessageOutgoing.create("不許戳我啦！")));
            } else {
                log.info("群戳一戳事件{}", event);
                if (Objects.equals(event.getSourceEvent().getTargetId(), event.getSourceEvent().getSelfId())) {
                    event.getBot().executeAsync(SendGroupMsgApi.create(event.getGroupId(), OneBotMessageOutgoing.create("不許戳我！")));
                }
            }
        }
    }
}
