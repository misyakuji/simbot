package com.miko.listener;


import com.miko.service.ArkDoubaoService;
import com.miko.util.OneBotUtil;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.common.PriorityConstant;
import love.forte.simbot.common.id.ID;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotFriendMessageEvent;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotGroupMessageEvent;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotMessageEvent;
import love.forte.simbot.component.onebot.v11.message.segment.*;
import love.forte.simbot.quantcat.common.annotations.ContentTrim;
import love.forte.simbot.quantcat.common.annotations.Filter;
import love.forte.simbot.quantcat.common.annotations.Listener;
import love.forte.simbot.quantcat.common.filter.MatchType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class MessageEventListener {

    private final ArkDoubaoService arkDoubaoService;

    public MessageEventListener(ArkDoubaoService arkDoubaoService) {
        this.arkDoubaoService = arkDoubaoService;
    }

    // 全局线程安全标记：key=事件唯一ID，value=是否中断后续监听
    private final ConcurrentHashMap<ID, Boolean> interruptFlag = new ConcurrentHashMap<>();

    @Listener
    public void msgEvent(OneBotMessageEvent event) {
        log.debug("msgEvent: {}", event);
    }


    @Listener
    @ContentTrim
    public void groupMsgEvent(OneBotGroupMessageEvent event) {
        // 检查是否被标记中断，是则直接返回（不执行后续逻辑）
        if (Boolean.TRUE.equals(interruptFlag.get(event.getMessageId()))) {
            interruptFlag.remove(event.getMessageId()); // 清理标记，避免内存泄漏
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
        String msgfix = OneBotUtil.fixMessage(event);
        log.info("接收 <- 群聊 [{}({})] [{}({})] {}", groupNickname, groupId, groupMemberNickname, groupMemberId, msgfix);
    }

    @Listener
    @Filter("/模型列表")
    @Filter("/获取模型列表")
    @Filter(value = "^/models", matchType = MatchType.REGEX_MATCHES)
    public void friendMsgCmdEvent(OneBotFriendMessageEvent event) {
        // 1. 获取当前模型和模型列表
        String currentModel = arkDoubaoService.getCurrentModel();
        List<String> modelList = arkDoubaoService.getModelList();

        // 2. 格式化输出：首行显示当前模型，后续显示带序号的列表
        // 拼接首行（当前模型）
        StringBuilder replyContent = new StringBuilder();
        replyContent.append("✅ 当前使用模型：").append(currentModel).append("\n\n");
        // 拼接可用模型列表（带序号）
        replyContent.append("📋 可用模型列表：\n");
        String modelListFormat = IntStream.range(0, modelList.size())
                .mapToObj(i -> String.format("  %d. %s", i + 1, modelList.get(i)))
                .collect(Collectors.joining("\n"));
        replyContent.append(modelListFormat);

        // 3. 发送回复（保持原有异步发送方式）
        event.getContent().sendAsync(replyContent.toString());

        // 4. 标记中断（保持你原有逻辑）
        interruptFlag.put(event.getId(), Boolean.TRUE);
    }

    @Listener
    @Filter(
            value = "^(?:/切换模型|/changeModel)(.*)$", // 匹配指令格式
            matchType = MatchType.REGEX_MATCHES
    )
    public void modelSwitchCmdEvent(OneBotFriendMessageEvent event) {

        String cmd = Objects.requireNonNull(event.getMessageContent().getPlainText()).trim();
        Matcher matcher = Pattern.compile("^(?:/切换模型|/changeModel)(\\d+)$").matcher(cmd);
        if (!matcher.find()) {
            event.getContent().sendAsync("❌ 指令格式错误！正确格式：/切换模型1 或 /changeModel1");
            interruptFlag.put(event.getId(), Boolean.TRUE);
            return;
        }

        int modelIndex;
        try {
            modelIndex = Integer.parseInt(matcher.group(1)); // 提取序号（如 1、2）
        } catch (NumberFormatException e) {
            event.getContent().sendAsync("❌ 序号必须是数字！正确格式：/切换模型1");
            interruptFlag.put(event.getId(), Boolean.TRUE);
            return;
        }

        List<String> modelList = arkDoubaoService.getModelList();
        if (modelIndex < 1 || modelIndex > modelList.size()) {
            String tip = String.format("❌ 序号超出范围！当前支持 1~%d 号模型", modelList.size());
            event.getContent().sendAsync(tip);
            interruptFlag.put(event.getId(), Boolean.TRUE);
            return;
        }

        String targetModel = modelList.get(modelIndex - 1);
        try {
            arkDoubaoService.setCurrentModel(targetModel);
            String successMsg = String.format("✅ 模型切换成功！\n当前模型：%s\n序号：%d", targetModel, modelIndex);
            event.getContent().sendAsync(successMsg);
            log.info("用户切换模型：{}（序号{}）", targetModel, modelIndex);
        } catch (Exception e) {
            log.error("切换模型失败", e);
            event.getContent().sendAsync("❌ 模型切换失败！原因：" + e.getMessage());
        }

        // 5. 标记中断后续监听
        interruptFlag.put(event.getId(), Boolean.TRUE);
    }

    @Listener(priority = PriorityConstant.DE_PRIORITIZE_1)
    @ContentTrim
    @Filter(targets = @Filter.Targets(atBot = true))
    public void groupMsgEventByAt(OneBotGroupMessageEvent event) {
        // 检查是否被标记中断，是则直接返回（不执行后续逻辑）
        if (Boolean.TRUE.equals(interruptFlag.get(event.getMessageId()))) {
            interruptFlag.remove(event.getMessageId()); // 清理标记，避免内存泄漏
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
        String msgfix = OneBotUtil.fixMessage(event);

        String reply = arkDoubaoService.streamChatWithDoubao(msgfix);
        event.replyAsync(reply);
//        event.getContent().sendAsync(reply);
        log.info("回复 -> 群聊[{}({})]: {}", groupNickname, groupId, reply);
    }

    @Listener(priority = PriorityConstant.DE_PRIORITIZE_1)
    public void friendMsgEvent(OneBotFriendMessageEvent event) {
        // 检查是否被标记中断，是则直接返回（不执行后续逻辑）
        if (Boolean.TRUE.equals(interruptFlag.get(event.getId()))) {
            interruptFlag.remove(event.getId()); // 清理标记，避免内存泄漏
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
        String msgfix = OneBotUtil.fixMessage(event);
        log.info("接收 <- 私聊 [{}({})] {}", friendNickname, friendId, msgfix);
        String value = arkDoubaoService.streamChatWithDoubao(msgfix);
        log.info("发送 -> {} - {}", event.getId(),value);
        event.replyAsync(value);
        //event.getContent().sendAsync(value);
    }
}