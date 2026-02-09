package com.miko.listener;
import com.miko.manager.MessageInterruptManager;
import com.miko.router.AiModelRouter;
import com.miko.util.OneBotMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.common.PriorityConstant;
import love.forte.simbot.common.id.ID;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotFriendMessageEvent;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotGroupMessageEvent;
import love.forte.simbot.quantcat.common.annotations.ContentTrim;
import love.forte.simbot.quantcat.common.annotations.Filter;
import love.forte.simbot.quantcat.common.annotations.Listener;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 消息事件监听器类，用于处理来自OneBot的消息事件。
 * 包括群聊消息、被@的消息以及私聊消息。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageEventListener {
    /**
     * 消息中断管理器，用于检查和清除消息中断标记。
     */
    private final MessageInterruptManager messageInterrupt;
    
    /**
     * AI模型路由器：用于根据模型标识获取对应的模型实例。
     */
    private final AiModelRouter aiModelRouter ;

    /**
     * 处理群聊消息事件。
     * 
     * @param event 群聊消息事件对象
     */
    @Listener
    @ContentTrim
    public void groupMsgEvent(OneBotGroupMessageEvent event) {
        // 检查是否被标记中断，是则直接返回（不执行后续逻辑）
        if (messageInterrupt.checkAndClear(String.valueOf(event.getMessageId()))){
            return;
        }
        // 获取群昵称
        String groupNickname = event.getContent().getName();
        // 获取群ID
        String groupId = event.getContent().getId().toString();
        // 获取群友ID
        ID groupMemberId = event.getAuthorId();
        // 获取群友昵称
        String groupMemberNickname = Objects.requireNonNull(event.getContent().getMember(groupMemberId)).getNick();
        // 如果群昵称为空，则使用用户名代替
        if (groupMemberNickname == null || groupMemberNickname.isEmpty()) {
            groupMemberNickname = Objects.requireNonNull(event.getContent().getMember(groupMemberId)).getName();
        }
        // 获取并修复消息内容
        String msgfix = OneBotMessageUtil.fixMessage(event);
        // 记录接收到的群聊消息日志
        log.info("接收 <- 群聊 [{}({})] [{}({})] {}", groupNickname, groupId, groupMemberNickname, groupMemberId, msgfix);
    }


    /**
     * 处理被@的群聊消息事件。
     * 
     * @param event 群聊消息事件对象
     */
    @Listener(priority = PriorityConstant.DE_PRIORITIZE_1)
    @ContentTrim
    @Filter(targets = @Filter.Targets(atBot = true))
    public void groupMsgEventByAt(OneBotGroupMessageEvent event) {
        // 检查是否被标记中断，是则直接返回（不执行后续逻辑）
        if (messageInterrupt.checkAndClear(String.valueOf(event.getMessageId()))){
            return;
        }
        // 如果消息以"/"开头，则忽略该消息
        if (Objects.requireNonNull(event.getMessageContent().getPlainText()).startsWith("/")) {
            return;
        }
        // 获取群昵称
        String groupNickname = event.getContent().getName();
        // 获取群ID
        String groupId = event.getContent().getId().toString();
        // 获取并修复消息内容
        String msgfix = OneBotMessageUtil.fixMessage(event);
        // TODO: 这里可以添加处理被@消息的逻辑
    }

    /**
     * 处理私聊消息事件。
     * 
     * @param event 私聊消息事件对象
     */
    @Listener(priority = PriorityConstant.DE_PRIORITIZE_1)
    public void friendMsgEvent(OneBotFriendMessageEvent event) {
        // 检查是否被标记中断，是则直接返回（不执行后续逻辑）
        if (messageInterrupt.checkAndClear(String.valueOf(event.getId()))){
            return;
        }
        // 获取好友ID
        String friendId = event.getAuthorId().toString();
        // 获取好友昵称
        String friendNickname = event.getSourceEvent().getSender().getNickname();
        // 获取并修复消息内容
        String msgFix = OneBotMessageUtil.fixMessage(event);
        // 记录接收到的私聊消息日志
        log.info("接收 <- 私聊 [{}({})] {}", friendNickname, friendId, msgFix);
        // 调用AI模型进行聊天回复
        String reply =  aiModelRouter.getModel("volcengine").chat(friendId,msgFix);
        // 记录发送的回复日志
        log.info("发送 -> {} - {}", event.getId(), reply);
        // 异步回复消息
        event.replyAsync(reply);

    }
}