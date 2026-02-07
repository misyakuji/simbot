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
    // 消息角色：user/assistant/system
    private String role;
    // 消息内容
    private String content;

    // 快速构建方法
    public static ChatMessageRecord fromAiMessage(Message message) {
        String role = convertTypeToRole(message.getMessageType());
        String content = message.getText();
        return new ChatMessageRecord(role, content);
    }

    /**
     * MessageType 映射为火山方舟API要求的角色字符串
     */
    private static String convertTypeToRole(MessageType type) {
        return switch (type) {
            case USER -> "user";
            case ASSISTANT -> "assistant";
            case SYSTEM -> "system";
            case TOOL -> "tool";
            default -> "user"; // 兜底默认值
        };
    }
}