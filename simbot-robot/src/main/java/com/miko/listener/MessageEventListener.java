package com.miko.listener;

import com.miko.ai.service.ArkChatService;
import com.miko.entity.BotChatContext;
import com.miko.manager.AiInterruptManager;
import com.miko.service.*;
import com.miko.util.OneBotMessageUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.common.PriorityConstant;
import love.forte.simbot.common.id.ID;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotFriendMessageEvent;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotGroupMessageEvent;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotMessageEvent;
import love.forte.simbot.quantcat.common.annotations.ContentTrim;
import love.forte.simbot.quantcat.common.annotations.Filter;
import love.forte.simbot.quantcat.common.annotations.Listener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageEventListener {
    private final AiInterruptManager interruptManager;

//    private final ArkDoubaoService arkDoubaoService;
//    private final BotContactService botContactService;
    private final ArkChatService arkChatService;

    /**
     * 注入全局 对话上下文: key = 对话类型+群聊ID/好友ID+对话ID value = 该对话的上下文
     */
    private Map<String, BotChatContext> chatContexts = new HashMap<>();

    @Listener
    public void msgEvent(OneBotMessageEvent event) {
        log.debug("msgEvent: {}", event);
    }

    @Listener
    @ContentTrim
    public void groupMsgEvent(OneBotGroupMessageEvent event) {
        // 检查是否被标记中断，是则直接返回（不执行后续逻辑）
        if (CheckMarkBreak(event)) return;
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
        if (CheckMarkBreak(event)) return;
        if (Objects.requireNonNull(event.getMessageContent().getPlainText()).startsWith("/")) {
            return;
        }
        // 群昵称
        String groupNickname = event.getContent().getName();
        // 群ID
        String groupId = event.getContent().getId().toString();
        // 消息内容
        String msgfix = OneBotMessageUtil.fixMessage(event);

        // 获取该群的对话上下文，如果不存在则创建新的
        String referenceKey = BotChatContext.ChatType.PRIVATE + groupId;
        BotChatContext botChatContext = chatContexts.computeIfAbsent(referenceKey, k ->
                BotChatContext.builder()
                        .chatId(groupId)
                        .chatType(BotChatContext.ChatType.PRIVATE)
                        .build()
        );
        // 调用连续对话方法
        //String reply = arkDoubaoService.streamMultiChatWithDoubao(msgfix, botChatContext.getMessages());
        String reply = arkChatService.multiChatWithDoubao(msgfix, botChatContext);
        event.replyAsync(reply);
        log.info("回复 -> 群聊[{}({})]: {}", groupNickname, groupId, reply);
    }

    @Listener(priority = PriorityConstant.DE_PRIORITIZE_1)
    public void friendMsgEvent(OneBotFriendMessageEvent event) {
        // 检查是否被标记中断，是则直接返回（不执行后续逻辑）
        if (CheckMarkBreak(event)) return;
        // 好友ID
        String friendId = event.getAuthorId().toString();
        // 好友昵称
        String friendNickname = event.getSourceEvent().getSender().getNickname();
        // 消息内容
        String msgFix = OneBotMessageUtil.fixMessage(event);
        log.info("接收 <- 私聊 [{}({})] {}", friendNickname, friendId, msgFix);

//        // 3️⃣ 查询数据库好友记录
//        BotChatContact user = botContactService.getFriendUser(friendId);
//        if (user == null) {
//            // 首次消息，创建好友记录
//            botContactService.insertFriendUser(friendId, friendNickname);
//            user = botContactService.getFriendUser(friendId);
//        }
//
//        botContactService.updateFriendUser(user, msgFix);

        // 7️⃣ 调用 AI 处理聊天
        String referenceKey = BotChatContext.ChatType.PRIVATE + friendId;
        BotChatContext botChatContext = chatContexts.computeIfAbsent(referenceKey, k ->
                BotChatContext.builder()
                        .chatId(friendId)
                        .chatType(BotChatContext.ChatType.PRIVATE)
                        .build()
        );

//        String reply = arkDoubaoService.multiChatWithDoubao(msgFix, botChatContext);
        String reply = arkChatService.multiChatWithDoubao(msgFix, botChatContext);
        log.info("发送 -> {} - {}", event.getId(), reply);
        event.replyAsync(reply);

    }

    /**
     * 标记检查
     *
     * @param event OneBotFriendMessageEvent
     * @return boolean
     */
    private final GroupMsgSender groupMsgSender;

    @Listener
    @Filter("/测试")
    public void friendMsgCmdEvent(OneBotFriendMessageEvent event) {
        SendGroupMsgRequest request = new SendGroupMsgRequest();
        request.setGroupId("737138270").setMessage(new SendGroupMsgRequest.Message("at", new SendGroupMsgRequest.AtData("943869478", "string")));
        SendGroupMsgResponse sendGroupMsgResponse = groupMsgSender.sendGroupAt(request);
        System.out.println(sendGroupMsgResponse.getStatus().toString());
    }

    /**
     * 检查并处理消息中断标记
     * <p>
     * 该方法用于检查指定消息ID是否存在中断标记，如果存在则清除标记并返回true，
     * 表示需要中断当前处理流程；如果不存在标记则返回false。
     *
     * @param event OneBot群消息事件对象，包含消息ID等信息
     * @return boolean 如果存在中断标记并已清除则返回true，否则返回false
     */
    private boolean CheckMarkBreak(OneBotGroupMessageEvent event) {
        if (Boolean.TRUE.equals(interruptManager.getInterruptFlag().get(event.getMessageId().toString()))) {
            interruptManager.getInterruptFlag().remove(event.getMessageId().toString()); // 清理标记，避免内存泄漏
            return true;
        }
        return false;
    }

    /**
     * 检查并处理消息中断标记
     * <p>
     * 该方法用于检查指定事件是否存在中断标记，如果存在则清除标记并返回true，
     * 表示需要中断当前处理流程；如果不存在标记则返回false。
     *
     * @param event OneBot好友消息事件对象，用于获取事件ID以查找对应的中断标记
     * @return boolean 返回true表示存在中断标记且已清除，返回false表示不存在中断标记
     */
    private boolean CheckMarkBreak(OneBotFriendMessageEvent event) {
        if (Boolean.TRUE.equals(interruptManager.getInterruptFlag().get(event.getId().toString()))) {
            interruptManager.getInterruptFlag().remove(event.getId().toString()); // 清理标记，避免内存泄漏
            return true;
        }
        return false;
    }

}