package com.miko.listener;

import com.miko.config.VolcArkConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotFriendMessageEvent;
import love.forte.simbot.quantcat.common.annotations.Filter;
import love.forte.simbot.quantcat.common.annotations.Listener;
import love.forte.simbot.quantcat.common.filter.MatchType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandEventListener {

    private final VolcArkConfig volcArkConfig;

    @Listener
    @Filter("/模型列表")
    @Filter("/获取模型列表")
    @Filter(value = "^/models", matchType = MatchType.REGEX_MATCHES)
    public void friendMsgCmdEvent(OneBotFriendMessageEvent event) {
        // 1. 获取当前模型和模型列表
        String currentModel = volcArkConfig.getModel();
        List<String> modelList = volcArkConfig.getModels();

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
        volcArkConfig.getInterruptFlag().put(event.getId(), Boolean.TRUE);
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
            volcArkConfig.getInterruptFlag().put(event.getId(), Boolean.TRUE);
            return;
        }

        int modelIndex;
        try {
            modelIndex = Integer.parseInt(matcher.group(1)); // 提取序号（如 1、2）
        } catch (NumberFormatException e) {
            event.getContent().sendAsync("❌ 序号必须是数字！正确格式：/切换模型1");
            volcArkConfig.getInterruptFlag().put(event.getId(), Boolean.TRUE);
            return;
        }

        List<String> modelList = volcArkConfig.getModels();
        if (modelIndex < 1 || modelIndex > modelList.size()) {
            String tip = String.format("❌ 序号超出范围！当前支持 1~%d 号模型", modelList.size());
            event.getContent().sendAsync(tip);
            volcArkConfig.getInterruptFlag().put(event.getId(), Boolean.TRUE);
            return;
        }

        String targetModel = modelList.get(modelIndex - 1);
        try {
            volcArkConfig.setModel(targetModel);
            String successMsg = String.format("✅ 模型切换成功！\n当前模型：%s\n序号：%d", targetModel, modelIndex);
            event.getContent().sendAsync(successMsg);
            log.info("用户切换模型：{}（序号{}）", targetModel, modelIndex);
        } catch (Exception e) {
            log.error("切换模型失败", e);
            event.getContent().sendAsync("❌ 模型切换失败！原因：" + e.getMessage());
        }

        // 5. 标记中断后续监听
        volcArkConfig.getInterruptFlag().put(event.getId(), Boolean.TRUE);
    }

    @Listener
    @Filter(
            value = "^/(?:开启深度思考|关闭深度思考|deepThinkingOn|deepThinkingOff)$", // 匹配新的指令格式
            matchType = MatchType.REGEX_MATCHES
    )
    public void deepThinkingCmdEvent(OneBotFriendMessageEvent event) {
        String cmd = Objects.requireNonNull(event.getMessageContent().getPlainText()).trim();

        boolean isDeepThinking;
        if (cmd.equals("/开启深度思考") || cmd.equals("/deepThinkingOn")) {
            isDeepThinking = true;
        } else if (cmd.equals("/关闭深度思考") || cmd.equals("/deepThinkingOff")) {
            isDeepThinking = false;
        } else {
            event.getContent().sendAsync("❌ 指令格式错误！正确格式：/开启深度思考 或 /关闭深度思考");
            volcArkConfig.getInterruptFlag().put(event.getId(), Boolean.TRUE);
            return;
        }

        try {
            volcArkConfig.setDeepThinking(isDeepThinking);
            String successMsg = String.format("✅ 深度思考设置成功！\n当前状态：%s", isDeepThinking ? "开启" : "关闭");
            event.getContent().sendAsync(successMsg);
            log.info("用户设置深度思考：{}", isDeepThinking);
        } catch (Exception e) {
            log.error("设置深度思考失败", e);
            event.getContent().sendAsync("❌ 深度思考设置失败！原因：" + e.getMessage());
        }
        // 标记中断后续监听
        volcArkConfig.getInterruptFlag().put(event.getId(), Boolean.TRUE);
    }

}
