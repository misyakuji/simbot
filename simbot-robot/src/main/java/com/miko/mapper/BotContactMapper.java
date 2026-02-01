package com.miko.mapper;

import com.miko.entity.BotChatContact;
import org.apache.ibatis.annotations.*;

@Mapper
public interface BotContactMapper {

    /**
     * 查询好友好感度，不存在返回 null
     * @param qqId 发送者QQ_ID
     * @return Integer
     */
    @Select("SELECT favorability " +
            "FROM friend_user " +
            "WHERE user_id = #{qqId}")
    Integer selectFavorabilityByQqId(@Param("qqId") String qqId);

    /**
     * 初始化好友列表
     *
     * @param user friend_user实体封装
     */
    @Insert("INSERT INTO friend_user " +
            "(user_id, favorability, intimacy_level, talk_count, last_talk_time, mood, remark, " +
            "ai_persona, ai_model, ai_temperature, ai_memory_summary, create_time, update_time) " +
            "VALUES (" +
            "#{userId}, #{favorability}, #{intimacyLevel}, #{talkCount}, #{lastTalkTime}, #{mood}, #{remark}, " +
            "#{aiPersona}, #{aiModel}, #{aiTemperature}, #{aiMemorySummary}, #{createTime}, #{updateTime}" +
            ")")
    void insertFriendUser(BotChatContact user);


    /**
     * 更新聊天风格Prompt
     * @param qqId 发送者QQ_ID
     * @param prompt 提示词
     */
    @Update("UPDATE friend_user SET ai_persona = #{prompt} WHERE user_id = #{qqId}")
    void updateAiPrompt(@Param("qqId") String qqId,@Param("prompt") String prompt);

    /**
     * 查询Prompt
     * @param qqId 发送者QQ_ID
     * @return String
     */
    @Select("select ai_persona from friend_user where user_id = #{qqId}")
    String getFriendUserAiPersona(@Param("qqId")String qqId);

    /**
     * 更新好感度相关信息
     * @param user BotChatContact
     */
    @Update("UPDATE friend_user SET favorability = #{favorability}, intimacy_level = #{intimacyLevel}, " +
            "talk_count = #{talkCount}, mood = #{mood}, last_talk_time = #{lastTalkTime} " +
            "WHERE user_id = #{userId}")
    void updateGoodFeeling(BotChatContact user);

    /**
     * 获取好友用户
     * @param qqId 发送者QQ_ID
     * @return BotChatContact
     */
    @Select("SELECT * FROM friend_user WHERE user_id = #{qqId}")
    BotChatContact getFriendUser(@Param("qqId") String qqId);

    /**
     * 更新ai模型
     * @param qqId 发送者QQ_ID
     * @param targetModel 使用的AI模型
     */
    @Update("UPDATE friend_user SET ai_model = #{targetModel} WHERE user_id = #{qqId}")
    void updateAiModel(@Param("qqId") String qqId,String targetModel);
}
