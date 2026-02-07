package com.miko.chat.context;

import com.miko.chat.message.ChatMessage;
import com.miko.chat.message.ChatType;

import java.util.List;

/**
 * 机器人聊天上下文接口，定义了聊天上下文的核心功能。
 * 该接口提供了获取聊天信息、管理消息历史以及清理上下文的方法。
 */
public interface BotChatContext {
    /**
     * 群聊类型常量，用于表示群聊聊天类型。
     */
    ChatType GROUP = ChatType.GROUP;

    /**
     * 私聊类型常量，用于表示私聊聊天类型。
     */
    ChatType PRIVATE = ChatType.PRIVATE;

    /**
     * 获取当前聊天的唯一标识符。
     *
     * @return 聊天ID字符串
     */
    String getChatId();

    void setChatId(String chatId);

    /**
     * 获取当前聊天的类型。
     *
     * @return 聊天类型枚举值
     */
    ChatType getChatType();

    void setChatType(ChatType chatType);

    /**
     * 获取当前上下文中的所有聊天消息列表。
     *
     * @return 聊天消息列表
     */
    List<String> getMessagesList();

    void setMessagesList(String prompt);

    /**
     * 清理整个聊天上下文，包括所有相关数据。
     */
    void clearContext();

    /**
     * 清除消息关联ID，用于解除消息之间的关联关系。
     */
    void clearMessageAssociationId();

    /**
     * 清除历史消息记录，保留上下文结构但移除具体消息内容。
     */
    void clearHistoricalMessages();


}
