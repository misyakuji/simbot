package com.miko.mapper;

import com.miko.entity.BotGlobalConfig;
import org.apache.ibatis.annotations.*;

@Mapper
public interface BotGlobalConfigMapper {

    @Select("SELECT `config_id`,`bot_id`,`master_id`,`current_model`,`model_list`,`deep_thinking_enabled`,`model_parameters`,`enabled`,`create_time`,`update_time` FROM `bot_global_config` WHERE `enabled` = '1'")
    BotGlobalConfig getEnabledConfig();

    @Select("SELECT `config_id`,`bot_id`,`master_id`,`current_model`,`model_list`,`deep_thinking_enabled`,`model_parameters`,`enabled`,`create_time`,`update_time` FROM `bot_global_config` WHERE `bot_id` = #{botId}")
    BotGlobalConfig getByBotId(String botId);

    @Select("SELECT `config_id`,`bot_id`,`master_id`,`current_model`,`model_list`,`deep_thinking_enabled`,`model_parameters`,`enabled`,`create_time`,`update_time` FROM `bot_global_config` WHERE `config_id` = #{configId} AND `bot_id` = #{botId}")
    BotGlobalConfig getById(@Param("configId") Long configId, @Param("botId") String botId);

    @Insert("INSERT INTO `bot_global_config` (`bot_id`,`master_id`,`current_model`,`model_list`,`deep_thinking_enabled`,`model_parameters`,`enabled`,`create_time`,`update_time`) VALUES (#{botId}, #{masterId}, #{currentModel}, #{modelList}, #{deepThinkingEnabled}, #{modelParameters}, #{enabled}, #{createTime}, #{updateTime})")
    void insert(BotGlobalConfig config);

    @Update("UPDATE `bot_global_config` SET `master_id` = #{masterId}, `current_model` = #{currentModel}, `model_list` = #{modelList}, `deep_thinking_enabled` = #{deepThinkingEnabled}, `model_parameters` = #{modelParameters}, `enabled` = #{enabled}, `update_time` = #{updateTime} WHERE `config_id` = #{configId} AND `bot_id` = #{botId}")
    void update(BotGlobalConfig config);

    /**
     * 部分更新全局配置
     * @param config 全局配置信息，包含要更新的字段
     */
    @Update("<script>UPDATE `bot_global_config` SET <set>" +
            "<if test='config.masterId != null'>`master_id` = #{config.masterId},</if>" +
            "<if test='config.currentModel != null'>`current_model` = #{config.currentModel},</if>" +
            "<if test='config.modelList != null'>`model_list` = #{config.modelList},</if>" +
            "<if test='config.deepThinkingEnabled != null'>`deep_thinking_enabled` = #{config.deepThinkingEnabled},</if>" +
            "<if test='config.modelParameters != null'>`model_parameters` = #{config.modelParameters},</if>" +
            "<if test='config.enabled != null'>`enabled` = #{config.enabled},</if>" +
            "`update_time` = #{config.updateTime}</set> WHERE `config_id` = #{config.configId} AND `bot_id` = #{config.botId}</script>")
    void patch(@Param("config") BotGlobalConfig config);

    @Update("UPDATE `bot_global_config` SET `enabled` = #{enabled}, `update_time` = #{updateTime} WHERE `config_id` = #{configId} AND `bot_id` = #{botId}")
    void updateEnabled(@Param("configId") Long configId, @Param("botId") String botId, @Param("enabled") String enabled, @Param("updateTime") java.time.LocalDateTime updateTime);

    @Delete("DELETE FROM `bot_global_config` WHERE `config_id` = #{configId} AND `bot_id` = #{botId}")
    void delete(@Param("configId") Long configId, @Param("botId") String botId);

}