package com.miko.listener;


import com.miko.config.VolcArkConfig;
import com.miko.entity.ChatContext;
import com.miko.service.ArkDoubaoService;
import com.miko.util.OneBotMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.common.PriorityConstant;
import love.forte.simbot.common.id.ID;
import love.forte.simbot.common.id.Identifies;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotFriendMessageEvent;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotGroupMessageEvent;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotMessageEvent;
import love.forte.simbot.quantcat.common.annotations.ContentTrim;
import love.forte.simbot.quantcat.common.annotations.Filter;
import love.forte.simbot.quantcat.common.annotations.Listener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageEventListener {
    private final VolcArkConfig volcArkConfig;
    
    private final ArkDoubaoService arkDoubaoService;
    
    // 存储对话上下文: key = 对话类型+群聊ID/好友ID+对话ID value = 该对话的上下文
    private final Map<String, ChatContext> chatContexts = new ConcurrentHashMap<>();
    
    @Listener
    public void msgEvent(OneBotMessageEvent event) {
        log.debug("msgEvent: {}", event);
    }


    @Listener
    @ContentTrim
    public void groupMsgEvent(OneBotGroupMessageEvent event) {
        // 检查是否被标记中断，是则直接返回（不执行后续逻辑）
        if (Boolean.TRUE.equals(volcArkConfig.getInterruptFlag().get(event.getMessageId()))) {
            volcArkConfig.getInterruptFlag().remove(event.getMessageId()); // 清理标记，避免内存泄漏
            return;
        }
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
        // 消息内容
        String msgfix = OneBotMessageUtil.fixMessage(event);
        log.info("接收 <- 群聊 [{}({})] [{}({})] {}", groupNickname, groupId, groupMemberNickname, groupMemberId, msgfix);
    }

    @Listener(priority = PriorityConstant.DE_PRIORITIZE_1)
    @ContentTrim
    @Filter(targets = @Filter.Targets(atBot = true))
    public void groupMsgEventByAt(OneBotGroupMessageEvent event) {
        // 检查是否被标记中断，是则直接返回（不执行后续逻辑）
        if (Boolean.TRUE.equals(volcArkConfig.getInterruptFlag().get(event.getMessageId()))) {
            volcArkConfig.getInterruptFlag().remove(event.getMessageId()); // 清理标记，避免内存泄漏
            return;
        }
        if (Objects.requireNonNull(event.getMessageContent().getPlainText()).startsWith("/")){
            return;
        }
        // 群昵称
        String groupNickname = event.getContent().getName();
        // 群ID
        String groupId = event.getContent().getId().toString();
        // 消息内容
        String msgfix = OneBotMessageUtil.fixMessage(event);
        
        // 获取该群的对话上下文，如果不存在则创建新的
        String referenceKey = ChatContext.ChatType.PRIVATE.toString() + groupId;
        ChatContext chatContext = chatContexts.computeIfAbsent(referenceKey, k ->
                ChatContext.builder()
                        .chatId(Identifies.of(groupId))
                        .chatType(ChatContext.ChatType.PRIVATE)
                        .build()
        );
        // 调用连续对话方法
        //String reply = arkDoubaoService.streamMultiChatWithDoubao(msgfix, chatContext.getMessages());
        String reply = arkDoubaoService.multiChatWithDoubao(msgfix, chatContext);
        event.replyAsync(reply);
        log.info("回复 -> 群聊[{}({})]: {}", groupNickname, groupId, reply);
    }

    @Listener(priority = PriorityConstant.DE_PRIORITIZE_1)
    public void friendMsgEvent(OneBotFriendMessageEvent event) {
        // 检查是否被标记中断，是则直接返回（不执行后续逻辑）
        if (Boolean.TRUE.equals(volcArkConfig.getInterruptFlag().get(event.getId()))) {
            volcArkConfig.getInterruptFlag().remove(event.getId()); // 清理标记，避免内存泄漏
            return;
        }
        if (Objects.requireNonNull(event.getMessageContent().getPlainText()).startsWith("/")){
            return;
        }
        // 好友ID
        ID friendId = event.getAuthorId();
        // 好友昵称
        String friendNickname = event.getSourceEvent().getSender().getNickname();
        // 消息内容
        String msgfix = OneBotMessageUtil.fixMessage(event);
        log.info("接收 <- 私聊 [{}({})] {}", friendNickname, friendId, msgfix);
        
        // 获取该好友的对话上下文，如果不存在则创建新的
        String referenceKey = ChatContext.ChatType.PRIVATE.toString() + friendId;
        ChatContext chatContext = chatContexts.computeIfAbsent(referenceKey, k ->
            ChatContext.builder()
                    .chatId(friendId)
                    .chatType(ChatContext.ChatType.PRIVATE)
                    .build()
        );
        // 调用连续对话方法
//        String reply = arkDoubaoService.streamMultiChatWithDoubao(msgfix, chatContext.getMessages());
        String reply = arkDoubaoService.multiChatWithDoubao(msgfix, chatContext);
        log.info("发送 -> {} - {}", event.getId(), reply);
        event.replyAsync(reply);
    }
}