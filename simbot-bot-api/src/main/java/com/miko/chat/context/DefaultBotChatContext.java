package com.miko.chat.context;

import com.miko.chat.message.ChatMessage;
import com.miko.chat.message.ChatType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认机器人聊天上下文实现类
 * 
 * 负责管理聊天会话的核心上下文信息，包括：
 * - 会话唯一标识(chatId)
 * - 聊天类型(chatType)  
 * - 对话状态标识(conversationId)
 * - 历史消息记录(messagesList)
 * - 当前使用模型(currentModel)
 * 
 * 提供完整的上下文生命周期管理功能
 */
@Data
@Component
public class DefaultBotChatContext implements BotChatContext {
    
    /**
     * 聊天会话唯一标识符
     * 用于区分不同的聊天会话实例
     */
    private String chatId;
    
    /**
     * 聊天类型枚举值
     * 标识当前会话的聊天模式(如单聊、群聊等)
     */
    private ChatType chatType;
    
    /**
     * 上下文关联ID
     * 用于维持对话的连续性状态，在模型切换时需要特别处理
     */
    private String conversationId;
    
    /**
     * 历史消息列表
     * 存储当前会话的所有聊天记录，支持动态添加和清空
     */
    private List<String> messagesList = new ArrayList<>();

    /**
     * 当前使用的模型标识
     * 记录当前会话绑定的AI模型，用于模型切换检测
     */
    private String currentModel;

    /**
     * 向历史消息列表添加新消息
     * 
     * @param prompt 待添加的消息内容
     */
    @Override
    public void setMessagesList(String prompt) {
        messagesList.add(prompt);
    }

    /**
     * 完全清空聊天上下文
     * 
     * 执行以下清理操作：
     * 1. 重置对话状态标识(避免跨模型使用无效ID)
     * 2. 清空历史消息列表(保持List对象引用，防止空指针)
     * 3. 重置当前模型记录(支持模型切换检测)
     * 4. 输出清理日志(便于调试追踪)
     */
    @Override
    public void clearContext() {
        // 重置对话状态标识
        this.conversationId = null;

        // 清空历史消息列表
        if (this.messagesList != null) {
            this.messagesList.clear();
        } else {
            this.messagesList = new ArrayList<>();
        }

        // 重置当前模型记录
        this.currentModel = null;

        // 输出清理日志
        String chatIdStr = this.chatId == null ? "未知" : this.chatId.toString();
        System.out.println("会话[" + chatIdStr + "]的上下文已清空");
    }
    
    /**
     * 仅清空上下文关联ID
     * 
     * 保留历史消息记录不变，只重置对话状态标识
     * 适用于需要重新开始对话但保留历史记录的场景
     */
    public void clearMessageAssociationId() {
        this.conversationId = null;
    }
    
    /**
     * 仅清空历史消息列表
     * 
     * 保留上下文关联ID不变，只清空聊天记录
     * 适用于需要保留对话状态但清除敏感信息的场景
     */
    public void clearHistoricalMessages() {
        if (this.messagesList != null) {
            this.messagesList.clear();
        } else {
            this.messagesList = new ArrayList<>();
        }
    }
}
