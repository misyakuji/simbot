package com.miko.service;

import com.miko.entity.FriendUser;
import love.forte.simbot.common.id.ID;

public interface FriendUserBotService {

    /**
     * 查询好感度
     *
     * @param authorId 发送者QQ_ID
     * @return Integer
     */
    Integer getFavorability(String authorId);

    /**
     * 初始化好友列表
     *
     * @param authorId 发送者QQ_ID
     */
    void insertFriendUser(String authorId, String remark);

    /**
     * 更新聊天风格Prompt
     *
     * @param authorId 发送者QQ_ID
     * @param prompt   提示词
     */
    void updateAiPrompt(String authorId, String prompt);

    /**
     * 获取好友用户ai Prompt
     * @param chatId 发送者QQ_ID
     * @return String
     */
    String getFriendUserAiPersona(ID chatId);

    /**
     * 获取好友用户
     * @param authorId 发送者QQ_ID
     * @return FriendUser
     */
    FriendUser getFriendUser(String authorId);

    /**
     * 更新好友用户
     * @param user FriendUser
     * @param msgFix 消息内容
     */
    void updateFriendUser(FriendUser user,String msgFix);

    /**
     * 更新用户使用的ai model(模型)
     * @param authorId 发送者QQ_ID
     * @param targetModel 当前使用的模型
     */
    void updateAiModel(ID authorId, String targetModel);
}
