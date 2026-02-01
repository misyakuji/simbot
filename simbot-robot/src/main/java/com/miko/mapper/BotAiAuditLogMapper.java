package com.miko.mapper;

import com.miko.entity.BotAiAuditLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BotAiAuditLogMapper {

    /**
     * 根据ID获取日志
     * @param auditId 日志ID
     * @return 日志信息
     */
    @Select("SELECT `audit_id`,`bot_id`,`request_id`,`model_name`,`input_text`,`output_text`,`user_id`,`ip_address`,`processing_time`,`risk_level`,`created_at` FROM `bot_ai_audit_log` WHERE `audit_id` = #{auditId}")
    BotAiAuditLog getById(Long auditId);

    /**
     * 根据请求ID获取日志
     * @param requestId 请求ID
     * @return 日志信息
     */
    @Select("SELECT `audit_id`,`bot_id`,`request_id`,`model_name`,`input_text`,`output_text`,`user_id`,`ip_address`,`processing_time`,`risk_level`,`created_at` FROM `bot_ai_audit_log` WHERE `request_id` = #{requestId}")
    BotAiAuditLog getByRequestId(String requestId);

    /**
     * 根据BotID获取日志列表
     * @param botId Bot ID
     * @param limit 限制数量
     * @return 日志列表
     */
    @Select("SELECT `audit_id`,`bot_id`,`request_id`,`model_name`,`input_text`,`output_text`,`user_id`,`ip_address`,`processing_time`,`risk_level`,`created_at` FROM `bot_ai_audit_log` WHERE `bot_id` = #{botId} ORDER BY `created_at` DESC LIMIT #{limit}")
    List<BotAiAuditLog> getByBotId(@Param("botId") String botId, @Param("limit") Integer limit);

    /**
     * 根据用户ID获取日志列表
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 日志列表
     */
    @Select("SELECT `audit_id`,`bot_id`,`request_id`,`model_name`,`input_text`,`output_text`,`user_id`,`ip_address`,`processing_time`,`risk_level`,`created_at` FROM `bot_ai_audit_log` WHERE `user_id` = #{userId} ORDER BY `created_at` DESC LIMIT #{limit}")
    List<BotAiAuditLog> getByUserId(@Param("userId") String userId, @Param("limit") Integer limit);

    /**
     * 获取最新日志列表
     * @param limit 限制数量
     * @return 日志列表
     */
    @Select("SELECT `audit_id`,`bot_id`,`request_id`,`model_name`,`input_text`,`output_text`,`user_id`,`ip_address`,`processing_time`,`risk_level`,`created_at` FROM `bot_ai_audit_log` ORDER BY `created_at` DESC LIMIT #{limit}")
    List<BotAiAuditLog> getLatestLogs(@Param("limit") Integer limit);

    /**
     * 插入日志
     * @param auditLog 日志信息
     */
    @Insert("INSERT INTO `bot_ai_audit_log` (`bot_id`,`request_id`,`model_name`,`input_text`,`output_text`,`user_id`,`ip_address`,`processing_time`,`risk_level`,`created_at`) VALUES (#{auditLog.botId}, #{auditLog.requestId}, #{auditLog.modelName}, #{auditLog.inputText}, #{auditLog.outputText}, #{auditLog.userId}, #{auditLog.ipAddress}, #{auditLog.processingTime}, #{auditLog.riskLevel}, #{auditLog.createdAt})")
    void insert(@Param("auditLog") BotAiAuditLog auditLog);

    /**
     * 更新日志
     * @param auditLog 日志信息
     */
    @Update("UPDATE `bot_ai_audit_log` SET `bot_id` = #{auditLog.botId}, `request_id` = #{auditLog.requestId}, `model_name` = #{auditLog.modelName}, `input_text` = #{auditLog.inputText}, `output_text` = #{auditLog.outputText}, `user_id` = #{auditLog.userId}, `ip_address` = #{auditLog.ipAddress}, `processing_time` = #{auditLog.processingTime}, `risk_level` = #{auditLog.riskLevel} WHERE `audit_id` = #{auditLog.auditId}")
    void update(@Param("auditLog") BotAiAuditLog auditLog);

    /**
     * 删除日志
     * @param auditId 日志ID
     */
    @Delete("DELETE FROM `bot_ai_audit_log` WHERE `audit_id` = #{auditId}")
    void delete(Long auditId);
}