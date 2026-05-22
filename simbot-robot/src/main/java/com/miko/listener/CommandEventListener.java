package com.miko.listener;

import com.miko.config.VolcArkConfig;
import com.miko.entity.BotChatContext;
import com.miko.entity.napcat.response.GetFriendsWithCategoryResponse;
import com.miko.service.ArkDoubaoService;
import com.miko.service.BotContactService;
import com.miko.service.NapCatApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.common.id.ID;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotFriendMessageEvent;
import love.forte.simbot.quantcat.common.annotations.Filter;
import love.forte.simbot.quantcat.common.annotations.Listener;
import love.forte.simbot.quantcat.common.filter.MatchType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final Map<String, BotChatContext> chatContexts;
    private final NapCatApiService napCatApiService;
    private final BotContactService botContactService;
    private final ArkDoubaoService arkDoubaoService;

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
        volcArkConfig.getInterruptFlag().put(event.getId().toString(), Boolean.TRUE);
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
            volcArkConfig.getInterruptFlag().put(event.getId().toString(), Boolean.TRUE);
            return;
        }

        int modelIndex;
        try {
            modelIndex = Integer.parseInt(matcher.group(1)); // 提取序号（如 1、2）
        } catch (NumberFormatException e) {
            event.getContent().sendAsync("❌ 序号必须是数字！正确格式：/切换模型1");
            volcArkConfig.getInterruptFlag().put(event.getId().toString(), Boolean.TRUE);
            return;
        }

        List<String> modelList = volcArkConfig.getModels();
        if (modelIndex < 1 || modelIndex > modelList.size()) {
            String tip = String.format("❌ 序号超出范围！当前支持 1~%d 号模型", modelList.size());
            event.getContent().sendAsync(tip);
            volcArkConfig.getInterruptFlag().put(event.getId().toString(), Boolean.TRUE);
            return;
        }

        String targetModel = modelList.get(modelIndex - 1);
        try {
            volcArkConfig.setModel(targetModel);
            botContactService.updateAiModel(event.getAuthorId().toString(), targetModel);
            String successMsg = String.format("✅ 模型切换成功！\n当前模型：%s\n序号：%d", targetModel, modelIndex);
            event.getContent().sendAsync(successMsg);
            log.info("用户切换模型：{}（序号{}）", targetModel, modelIndex);
        } catch (Exception e) {
            log.error("切换模型失败", e);
            event.getContent().sendAsync("❌ 模型切换失败！原因：" + e.getMessage());
        }

        // 5. 标记中断后续监听
        volcArkConfig.getInterruptFlag().put(event.getId().toString(), Boolean.TRUE);
    }

    @Listener
    @Filter(
            value = "^(?:/设置聊天风格|/更新聊天风格|/changePersona)\\s*([\\s\\S]*)$",
            matchType = MatchType.REGEX_MATCHES
    )
    public void updateAiPersona(OneBotFriendMessageEvent event) {
        // 获取uid
        ID authorId = event.getAuthorId();
        // 获取风格提示词（保留原始换行）
        String message = Objects.requireNonNull(event.getMessageContent().getPlainText()).trim();
        // 关键修改：添加 Pattern.DOTALL 标志，让 . 匹配换行符
        Pattern pattern = Pattern.compile(
                "^(?:/设置聊天风格|/更新聊天风格|/changePersona)\\s*(.*)$",
                Pattern.DOTALL  // 核心：开启DOTALL模式
        );
        Matcher matcher = pattern.matcher(message);
        if (!matcher.matches()) {
            return;
        }
        // 捕获组 1 现在能完整获取包含换行的所有风格提示词
        String personaPrompt = matcher.group(1).trim();

        if (personaPrompt.isEmpty()) {
            event.getContent().sendAsync("你要先告诉我你想要什么样的聊天风格呀～");
            return;
        }
        if (personaPrompt.length() > 3000) {
            event.getContent().sendAsync("风格描述太长啦～我们简短一点好不好？");
            return;
        }

//        String finalPersona = """
//            你是一个虚拟女朋友，不允许提及你是 AI。
//            以下是你的聊天风格设定：
//            %s
//            """.formatted(personaPrompt);
        // 保存聊天风格
        botContactService.updateAiPrompt(String.valueOf(authorId), personaPrompt);

        // 清空上次的Prompt连续会话
        arkDoubaoService.clearChatContext(String.valueOf(authorId));

        // 保存到数据库
        event.getContent().sendAsync("记住啦～以后我就按这个风格陪你聊天 \uD83D\uDC96");
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
            volcArkConfig.getInterruptFlag().put(event.getId().toString(), Boolean.TRUE);
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
        volcArkConfig.getInterruptFlag().put(event.getId().toString(), Boolean.TRUE);
    }

    @Listener
    @Filter("/对话列表")
    @Filter("/查看对话")
    @Filter(value = "^/chatList", matchType = MatchType.REGEX_MATCHES)
    public void chatListCmdEvent(OneBotFriendMessageEvent event) {
        try {
            if (chatContexts.isEmpty()) {
                event.getContent().sendAsync("📋 当前没有正在进行的对话");
                volcArkConfig.getInterruptFlag().put(event.getId().toString(), Boolean.TRUE);
                return;
            }

            // 格式化对话列表
            StringBuilder replyContent = new StringBuilder();
            replyContent.append("📋 当前对话列表：\n\n");

            List<Map.Entry<String, BotChatContext>> chatList = new ArrayList<>(chatContexts.entrySet());
            for (int i = 0; i < chatList.size(); i++) {
                Map.Entry<String, BotChatContext> entry = chatList.get(i);
                String key = entry.getKey();
                BotChatContext context = entry.getValue();

                replyContent.append(String.format("%d. 对话ID：%s\n", i + 1, key));
                replyContent.append(String.format("   聊天类型：%s\n", context.getChatType()));
                replyContent.append(String.format("   聊天ID：%s\n", context.getChatId()));
                replyContent.append(String.format("   消息数量：%d\n\n", context.getMessages() != null ? context.getMessages().size() : 0));
            }

            // 发送回复
            event.getContent().sendAsync(replyContent.toString());
        } catch (Exception e) {
            log.error("查看对话列表失败", e);
            event.getContent().sendAsync("❌ 查看对话列表失败：" + e.getMessage());
        }

        // 标记中断后续监听
        volcArkConfig.getInterruptFlag().put(event.getId().toString(), Boolean.TRUE);
    }

    @Listener
    @Filter(
            value = "^(?:/删除对话|/removeChat)(.*)$", // 匹配指令格式
            matchType = MatchType.REGEX_MATCHES
    )
    public void deleteChatCmdEvent(OneBotFriendMessageEvent event) {
        String cmd = Objects.requireNonNull(event.getMessageContent().getPlainText()).trim();
        Matcher matcher = Pattern.compile("^(?:/删除对话|/removeChat)(\\d+)$").matcher(cmd);

        if (!matcher.find()) {
            event.getContent().sendAsync("❌ 指令格式错误！正确格式：/删除对话1 或 /removeChat1");
            volcArkConfig.getInterruptFlag().put(event.getId().toString(), Boolean.TRUE);
            return;
        }

        int chatIndex;
        try {
            chatIndex = Integer.parseInt(matcher.group(1)); // 提取对话序号
        } catch (NumberFormatException e) {
            event.getContent().sendAsync("❌ 序号必须是数字！正确格式：/删除对话1");
            volcArkConfig.getInterruptFlag().put(event.getId().toString(), Boolean.TRUE);
            return;
        }

        try {
            if (chatContexts.isEmpty()) {
                event.getContent().sendAsync("📋 当前没有正在进行的对话");
                volcArkConfig.getInterruptFlag().put(event.getId().toString(), Boolean.TRUE);
                return;
            }

            List<Map.Entry<String, BotChatContext>> chatList = new ArrayList<>(chatContexts.entrySet());

            if (chatIndex < 1 || chatIndex > chatList.size()) {
                String tip = String.format("❌ 对话序号超出范围！当前共有 %d 个对话", chatList.size());
                event.getContent().sendAsync(tip);
                volcArkConfig.getInterruptFlag().put(event.getId().toString(), Boolean.TRUE);
                return;
            }

            // 删除指定序号的对话
            Map.Entry<String, BotChatContext> entryToRemove = chatList.get(chatIndex - 1);
            String removedKey = entryToRemove.getKey();
            BotChatContext removedContext = entryToRemove.getValue();

            // 从上下文Map中删除
            chatContexts.remove(removedKey);

            String successMsg = String.format("✅ 成功删除对话！\n对话ID：%s\n聊天类型：%s\n聊天ID：%s",
                    removedKey, removedContext.getChatType(), removedContext.getChatId());
            event.getContent().sendAsync(successMsg);
            log.info("用户删除对话：{}", removedKey);
        } catch (Exception e) {
            log.error("删除对话失败", e);
            event.getContent().sendAsync("❌ 删除对话失败：" + e.getMessage());
        }

        // 标记中断后续监听
        volcArkConfig.getInterruptFlag().put(event.getId().toString(), Boolean.TRUE);
    }

    @Listener
    @Filter("/特别关心列表")
    @Filter("/特别关心")
    public void specialCareListCmdEvent(OneBotFriendMessageEvent event) {
        try {
            // 调用API获取好友列表
            GetFriendsWithCategoryResponse response = napCatApiService.getFriendsWithCategory();

            if (response == null || response.getData() == null || response.getData().isEmpty()) {
                event.getContent().sendAsync("📋 当前没有好友数据");
                volcArkConfig.getInterruptFlag().put(event.getId().toString(), Boolean.TRUE);
                return;
            }

            // 查找特别关心分组
            GetFriendsWithCategoryResponse.FriendCategory specialCareCategory = response.getData().stream()
                    .filter(category -> "特别关心".equals(category.getCategoryName()))
                    .findFirst()
                    .orElse(null);

            // 格式化特别关心列表
            StringBuilder replyContent = new StringBuilder();
            replyContent.append("💖 特别关心列表\n\n");

            if (specialCareCategory != null) {
                replyContent.append(String.format("🏷️ %s (%d人，在线%d人)\n",
                        specialCareCategory.getCategoryName(),
                        specialCareCategory.getCategoryMbCount(),
                        specialCareCategory.getOnlineCount()));

                if (specialCareCategory.getBuddyList() != null && !specialCareCategory.getBuddyList().isEmpty()) {
                    for (GetFriendsWithCategoryResponse.Friend friend : specialCareCategory.getBuddyList()) {
                        String displayName = friend.getRemark() != null && !friend.getRemark().isEmpty()
                                ? friend.getRemark()
                                : friend.getNickname();
                        replyContent.append(String.format("   %s (%d)\n",
                                displayName,
                                friend.getUser_id()));
                    }
                } else {
                    replyContent.append("   该分类下没有好友\n");
                }
            } else {
                replyContent.append("   没有找到特别关心分组\n");
            }

            // 发送回复
            event.getContent().sendAsync(replyContent.toString());
        } catch (Exception e) {
            log.error("获取特别关心列表失败", e);
            event.getContent().sendAsync("❌ 获取特别关心列表失败：" + e.getMessage());
        }

        // 标记中断后续监听
        volcArkConfig.getInterruptFlag().put(event.getId().toString(), Boolean.TRUE);
    }

    @Listener
    @Filter("/好友列表")
    public void friendsListCmdEvent(OneBotFriendMessageEvent event) {
        try {
            // 调用API获取好友列表
            GetFriendsWithCategoryResponse response = napCatApiService.getFriendsWithCategory();

            if (response == null || response.getData() == null || response.getData().isEmpty()) {
                event.getContent().sendAsync("📋 当前没有好友数据");
                volcArkConfig.getInterruptFlag().put(event.getId().toString(), Boolean.TRUE);
                return;
            }

            // 格式化好友列表
            StringBuilder replyContent = new StringBuilder();
            replyContent.append("📋 好友列表\n\n");

            for (GetFriendsWithCategoryResponse.FriendCategory category : response.getData()) {
                replyContent.append(String.format("🏷️ %s (%d人，在线%d人)\n",
                        category.getCategoryName(),
                        category.getCategoryMbCount(),
                        category.getOnlineCount()));

                if (category.getBuddyList() != null && !category.getBuddyList().isEmpty()) {
                    for (GetFriendsWithCategoryResponse.Friend friend : category.getBuddyList()) {
                        String displayName = friend.getRemark() != null && !friend.getRemark().isEmpty()
                                ? friend.getRemark()
                                : friend.getNickname();
                        replyContent.append(String.format("   %s (%d)\n",
                                displayName,
                                friend.getUser_id()));
                    }
                } else {
                    replyContent.append("   该分类下没有好友\n");
                }
                replyContent.append("\n");
            }

            // 发送回复
            event.getContent().sendAsync(replyContent.toString());
        } catch (Exception e) {
            log.error("获取好友列表失败", e);
            event.getContent().sendAsync("❌ 获取好友列表失败：" + e.getMessage());
        }

        // 标记中断后续监听
        volcArkConfig.getInterruptFlag().put(event.getId().toString(), Boolean.TRUE);
    }

    @Listener
    @Filter(
            value = "^(/清空上下文|/重置对话|/clearContext)\\s*",
            matchType = MatchType.REGEX_MATCHES
    )
    public void clearContextCommand(OneBotFriendMessageEvent event) {
        // 1. 获取用户ID（即chatId）
        String chatId = String.valueOf(event.getAuthorId());
        // 2. 调用清空上下文方法
        boolean isSuccess = arkDoubaoService.clearChatContext(chatId);
        // 3. 给用户反馈
        if (isSuccess) {
            event.getContent().sendAsync("✨ 已帮主人清空所有聊天上下文啦～重新开始聊天吧～");
        } else {
            event.getContent().sendAsync("😥 清空上下文失败啦，是不是输入的指令有问题呀？");
        }
    }

}