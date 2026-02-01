package com.miko.mapper;

import com.miko.entity.BotChatContact;
import org.apache.ibatis.annotations.*;

@Mapper
public interface BotContactMapper {

    /**
     * 查询好友好感度，不存在返回 null
     *
     * @param qqId 发送者QQ_ID
     * @return Integer
     */
    @Select("SELECT favorability " +
            "FROM bot_chat_contact " +
            "WHERE contact_id = #{qqId}")
    Integer selectFavorabilityByQqId(@Param("qqId") String qqId);

    /**
     * 初始化好友列表
     *
     * @param user bot_chat_contact实体封装
     */
    @Insert("INSERT INTO bot_chat_contact " +
            "(contact_id, favorability, intimacy_level, talk_count, last_talk_time, mood, remark, " +
            "ai_persona, ai_model, ai_temperature, ai_memory_summary, create_time, update_time) " +
            "VALUES (" +
            "#{userId}, #{favorability}, #{intimacyLevel}, #{talkCount}, #{lastTalkTime}, #{mood}, #{remark}, " +
            "#{aiPersona}, #{aiModel}, #{aiTemperature}, #{aiMemorySummary}, #{createTime}, #{updateTime}" +
            ")")
    void insertFriendUser(BotChatContact user);


    /**
     * 更新聊天风格Prompt
     *
     * @param qqId   发送者QQ_ID
     * @param prompt 提示词
     */
    @Update("UPDATE bot_chat_contact SET ai_persona = #{prompt} WHERE contact_id = #{qqId}")
    void updateAiPrompt(@Param("qqId") String qqId, @Param("prompt") String prompt);

    /**
     * 查询Prompt
     *
     * @param qqId 发送者QQ_ID
     * @return String
     */
    @Select("select ai_persona from bot_chat_contact where contact_id = #{qqId}")
    String getFriendUserAiPersona(@Param("qqId") String qqId);

    /**
     * 更新好感度相关信息
     *
     * @param user BotChatContact
     */
    @Update("UPDATE bot_chat_contact SET favorability = #{favorability}, intimacy_level = #{intimacyLevel}, " +
            "talk_count = #{talkCount}, mood = #{mood}, last_talk_time = #{lastTalkTime} " +
            "WHERE contact_id = #{userId}")
    void updateGoodFeeling(BotChatContact user);

    /**
     * 获取好友用户
     *
     * @param qqId 发送者QQ_ID
     * @return BotChatContact
     */
    @Select("SELECT * FROM bot_chat_contact WHERE contact_id = #{qqId}")
    BotChatContact getFriendUser(@Param("qqId") String qqId);

    /**
     * 更新ai模型
     *
     * @param qqId        发送者QQ_ID
     * @param targetModel 使用的AI模型
     */
    @Update("UPDATE bot_chat_contact SET ai_model = #{targetModel} WHERE contact_id = #{qqId}")
    void updateAiModel(@Param("qqId") String qqId, String targetModel);
}
