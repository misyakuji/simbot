package com.miko.entity;

import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import love.forte.simbot.common.id.ID;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatContext {
    private ID chatId;
    private ChatType chatType;
    private String messageId; // 对应previousResponseId
    private List<ChatMessage> messages; // 历史对话消息列表
    // 新增：记录当前使用的模型（用于检测模型切换）
    private String currentModel;

    public enum ChatType {
        GROUP,
        PRIVATE
    }

    // ========== 核心：新增清空上下文的方法 ==========
    public void clearContext() {
        // 1. 清空上下文关联ID（关键：避免跨模型使用无效ID）
        this.messageId = null;

        // 2. 清空历史消息列表（保留List对象，避免后续调用时空指针）
        if (this.messages != null) {
            this.messages.clear();
        } else {
            // 如果messages为null，初始化空列表（而非设为null）
            this.messages = new ArrayList<>();
        }

        // 3. 重置当前模型记录（配合模型切换检测）
        this.currentModel = null;

        // 可选：打印日志（方便调试，生产环境可注释）
        String chatIdStr = this.chatId == null ? "未知" : this.chatId.toString();
        System.out.println("会话[" + chatIdStr + "]的上下文已清空");
    }

    // ========== 可选：新增单独清空ID/消息的便捷方法（按需使用） ==========
    /**
     * 仅清空上下文关联ID（保留历史消息）
     */
    public void clearMessageId() {
        this.messageId = null;
    }

    /**
     * 仅清空历史消息列表（保留上下文ID）
     */
    public void clearMessages() {
        if (this.messages != null) {
            this.messages.clear();
        } else {
            this.messages = new ArrayList<>();
        }
    }
}