package com.miko.ai.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;

/**
 * 对话消息记录，用于上下文存储
 */
@Data
@AllArgsConstructor
public class ChatMessageRecord {

    // 消息角色：user / assistant / system / tool
    private String role;

    // 消息内容
    private String content;

    /* ================= 语义判断方法 ================= */

    public boolean isUser() {
        return "user".equals(role);
    }

    public boolean isAssistant() {
        return "assistant".equals(role);
    }

    public boolean isSystem() {
        return "system".equals(role);
    }

    public boolean isTool() {
        return "tool".equals(role);
    }

    /* ================= 工厂方法 ================= */

    public static ChatMessageRecord user(String content) {
        return new ChatMessageRecord("user", content);
    }

    public static ChatMessageRecord assistant(String content) {
        return new ChatMessageRecord("assistant", content);
    }

    public static ChatMessageRecord system(String content) {
        return new ChatMessageRecord("system", content);
    }

    public static ChatMessageRecord tool(String content) {
        return new ChatMessageRecord("tool", content);
    }

    /**
     * 从 Spring AI Message 转为上下文记录
     */
    public static ChatMessageRecord fromAiMessage(Message message) {
        String role = convertTypeToRole(message.getMessageType());
        String content = message.getText();
        return new ChatMessageRecord(role, content);
    }

    /**
     * MessageType -> role 映射
     */
    private static String convertTypeToRole(MessageType type) {
        return switch (type) {
            case USER -> "user";
            case ASSISTANT -> "assistant";
            case SYSTEM -> "system";
            case TOOL -> "tool";
            default -> "user";
        };
    }
}
