package com.miko.listener;

import com.miko.config.VolcArkConfig;
import com.miko.entity.BotChatContext;
import com.miko.entity.napcat.response.GetFriendsWithCategoryResponse;
import com.miko.service.ArkDoubaoService;
import com.miko.service.BotContactService;
import com.miko.service.NapCatApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotFriendMessageEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandEventHandler {

    private final VolcArkConfig volcArkConfig;
    private final Map<String, BotChatContext> chatContexts;
    private final NapCatApiService napCatApiService;
    private final BotContactService botContactService;
    private final ArkDoubaoService arkDoubaoService;

    public void handle(String text, OneBotFriendMessageEvent event) {
        String uid = String.valueOf(event.getAuthorId());
        String reply = switch (text) {
            case "/模型列表", "/获取模型列表" -> modelList();
            case String s when matches(s, "^/models$") -> modelList();
            case String s when matches(s, "^(?:/切换模型|/changeModel)\\d+$") -> modelSwitch(uid, s);
            case String s when matches(s, "(?s)^(?:/设置聊天风格|/更新聊天风格|/changePersona)\\s*(.*)$") -> updatePersona(uid, s);
            case String s when matches(s, "^/(?:开启深度思考|关闭深度思考|deepThinkingOn|deepThinkingOff)$") -> deepThinking(s);
            case "/对话列表", "/查看对话", "/chatList" -> chatList();
            case String s when matches(s, "^(?:/删除对话|/removeChat)\\d+$") -> deleteChat(s);
            case "/特别关心列表", "/特别关心" -> specialCareList();
            case "/好友列表" -> friendsList();
            case String s when matches(s, "^(/清空上下文|/重置对话|/clearContext)\\s*") -> clearContext(uid);
            default -> {
                log.debug("未知命令: {}", text);
                yield null;
            }
        };
        if (reply != null) {
            event.getContent().sendAsync(reply);
        }
    }

    // ========================================================================
    // 命令处理
    // ========================================================================

    private String modelList() {
        return "✅ 当前使用模型：" + volcArkConfig.getModel() + "\n\n"
                + "📋 可用模型列表：\n"
                + IntStream.range(0, volcArkConfig.getModels().size())
                        .mapToObj(i -> String.format("  %d. %s", i + 1, volcArkConfig.getModels().get(i)))
                        .collect(Collectors.joining("\n"));
    }

    private String modelSwitch(String uid, String text) {
        var m = Pattern.compile("^(?:/切换模型|/changeModel)(\\d+)$").matcher(text);
        if (!m.find()) return "❌ 指令格式错误！正确格式：/切换模型1 或 /changeModel1";
        int idx = parseInt(m.group(1));
        if (idx < 1 || idx > volcArkConfig.getModels().size())
            return "❌ 序号超出范围！当前支持 1~" + volcArkConfig.getModels().size() + " 号模型";
        try {
            String target = volcArkConfig.getModels().get(idx - 1);
            volcArkConfig.setModel(target);
            botContactService.updateAiModel(uid, target);
            log.info("用户切换模型：{}（序号{}）", target, idx);
            return "✅ 模型切换成功！\n当前模型：" + target + "\n序号：" + idx;
        } catch (Exception e) {
            log.error("切换模型失败", e);
            return "❌ 模型切换失败！原因：" + e.getMessage();
        }
    }

    private String updatePersona(String uid, String text) {
        var m = Pattern.compile("(?s)^(?:/设置聊天风格|/更新聊天风格|/changePersona)\\s*(.*)$").matcher(text);
        if (!m.matches()) return null;
        String prompt = m.group(1).trim();
        if (prompt.isEmpty()) return "你要先告诉我你想要什么样的聊天风格呀～";
        if (prompt.length() > 3000) return "风格描述太长啦～我们简短一点好不好？";
        botContactService.updateAiPrompt(uid, prompt);
        arkDoubaoService.clearChatContext(uid);
        return "记住啦～以后我就按这个风格陪你聊天 💖";
    }

    private String deepThinking(String text) {
        boolean on = text.equals("/开启深度思考") || text.equals("/deepThinkingOn");
        if (!on && !text.equals("/关闭深度思考") && !text.equals("/deepThinkingOff"))
            return "❌ 指令格式错误！正确格式：/开启深度思考 或 /关闭深度思考";
        try {
            volcArkConfig.setDeepThinking(on);
            log.info("用户设置深度思考：{}", on);
            return "✅ 深度思考设置成功！\n当前状态：" + (on ? "开启" : "关闭");
        } catch (Exception e) {
            log.error("设置深度思考失败", e);
            return "❌ 深度思考设置失败！原因：" + e.getMessage();
        }
    }

    private String chatList() {
        if (chatContexts.isEmpty()) return "📋 当前没有正在进行的对话";
        try {
            StringBuilder reply = new StringBuilder("📋 当前对话列表：\n\n");
            int i = 1;
            for (var entry : chatContexts.entrySet()) {
                int n = entry.getValue().getMessages() != null ? entry.getValue().getMessages().size() : 0;
                reply.append(String.format("%d. 对话ID：%s\n", i++, entry.getKey()));
                reply.append(String.format("   聊天类型：%s\n", entry.getValue().getChatType()));
                reply.append(String.format("   聊天ID：%s\n", entry.getValue().getChatId()));
                reply.append(String.format("   消息数量：%d\n\n", n));
            }
            return reply.toString();
        } catch (Exception e) {
            log.error("查看对话列表失败", e);
            return "❌ 查看对话列表失败：" + e.getMessage();
        }
    }

    private String deleteChat(String text) {
        var m = Pattern.compile("^(?:/删除对话|/removeChat)(\\d+)$").matcher(text);
        if (!m.find()) return "❌ 指令格式错误！正确格式：/删除对话1 或 /removeChat1";
        int idx = parseInt(m.group(1));
        if (idx < 1) return "❌ 序号必须是数字！正确格式：/删除对话1";
        if (chatContexts.isEmpty()) return "📋 当前没有正在进行的对话";
        try {
            var entries = new ArrayList<>(chatContexts.entrySet());
            if (idx > entries.size()) return "❌ 序号超出范围！当前共有 " + entries.size() + " 个对话";
            var entry = entries.get(idx - 1);
            chatContexts.remove(entry.getKey());
            log.info("用户删除对话：{}", entry.getKey());
            return "✅ 成功删除对话！\n对话ID：" + entry.getKey()
                    + "\n聊天类型：" + entry.getValue().getChatType() + "\n聊天ID：" + entry.getValue().getChatId();
        } catch (Exception e) {
            log.error("删除对话失败", e);
            return "❌ 删除对话失败：" + e.getMessage();
        }
    }

    private String specialCareList() {
        var resp = fetchFriends();
        if (resp == null) return "❌ 获取好友列表失败";
        try {
            var sc = resp.getData().stream()
                    .filter(c -> "特别关心".equals(c.getCategoryName()))
                    .findFirst().orElse(null);
            StringBuilder reply = new StringBuilder("💖 特别关心列表\n\n");
            if (sc != null) {
                reply.append(String.format("🏷️ %s (%d人，在线%d人)\n",
                        sc.getCategoryName(), sc.getCategoryMbCount(), sc.getOnlineCount()));
                if (sc.getBuddyList() != null) {
                    for (var f : sc.getBuddyList()) {
                        reply.append(String.format("   %s (%d)\n", fetchName(f), f.getUser_id()));
                    }
                }
            } else {
                reply.append("   没有找到特别关心分组\n");
            }
            return reply.toString();
        } catch (Exception e) {
            log.error("获取特别关心列表失败", e);
            return "❌ 获取特别关心列表失败：" + e.getMessage();
        }
    }

    private String friendsList() {
        var resp = fetchFriends();
        if (resp == null) return "❌ 获取好友列表失败";
        try {
            StringBuilder reply = new StringBuilder("📋 好友列表\n\n");
            for (var cat : resp.getData()) {
                reply.append(String.format("🏷️ %s (%d人，在线%d人)\n",
                        cat.getCategoryName(), cat.getCategoryMbCount(), cat.getOnlineCount()));
                if (cat.getBuddyList() != null) {
                    for (var f : cat.getBuddyList()) {
                        reply.append(String.format("   %s (%d)\n", fetchName(f), f.getUser_id()));
                    }
                }
                reply.append("\n");
            }
            return reply.toString();
        } catch (Exception e) {
            log.error("获取好友列表失败", e);
            return "❌ 获取好友列表失败：" + e.getMessage();
        }
    }

    private String clearContext(String uid) {
        boolean ok = arkDoubaoService.clearChatContext(uid);
        return ok ? "✨ 已帮主人清空所有聊天上下文啦～重新开始聊天吧～"
                : "😥 清空上下文失败啦，是不是输入的指令有问题呀？";
    }

    // ========================================================================
    // 工具方法
    // ========================================================================

    private static boolean matches(String text, String regex) {
        return text.matches(regex);
    }

    private static int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return -1; }
    }

    private GetFriendsWithCategoryResponse fetchFriends() {
        try {
            var r = napCatApiService.getFriendsWithCategory();
            if (r == null || r.getData() == null || r.getData().isEmpty()) return null;
            return r;
        } catch (Exception e) {
            log.error("获取好友列表失败", e);
            return null;
        }
    }

    private String fetchName(GetFriendsWithCategoryResponse.Friend f) {
        return f.getRemark() != null && !f.getRemark().isEmpty() ? f.getRemark() : f.getNickname();
    }
}
