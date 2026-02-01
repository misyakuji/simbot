package com.miko.service;

import com.miko.entity.BotAiAuditLog;
import com.miko.mapper.BotAiAuditLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BotAiAuditLogService {

    private final BotAiAuditLogMapper botAiAuditLogMapper;

    /**
     * 根据ID获取日志
     * @param auditId 日志ID
     * @return 日志信息
     */
    public BotAiAuditLog getById(Long auditId) {
        return botAiAuditLogMapper.getById(auditId);
    }

    /**
     * 根据请求ID获取日志
     * @param requestId 请求ID
     * @return 日志信息
     */
    public BotAiAuditLog getByRequestId(String requestId) {
        return botAiAuditLogMapper.getByRequestId(requestId);
    }

    /**
     * 根据BotID获取日志列表
     * @param botId Bot ID
     * @param limit 限制数量
     * @return 日志列表
     */
    public List<BotAiAuditLog> getByBotId(String botId, Integer limit) {
        return botAiAuditLogMapper.getByBotId(botId, limit);
    }

    /**
     * 根据用户ID获取日志列表
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 日志列表
     */
    public List<BotAiAuditLog> getByUserId(String userId, Integer limit) {
        return botAiAuditLogMapper.getByUserId(userId, limit);
    }

    /**
     * 获取最新日志列表
     * @param limit 限制数量
     * @return 日志列表
     */
    public List<BotAiAuditLog> getLatestLogs(Integer limit) {
        return botAiAuditLogMapper.getLatestLogs(limit);
    }

    /**
     * 创建日志
     * @param auditLog 日志信息
     */
    public void createLog(BotAiAuditLog auditLog) {
        LocalDateTime now = LocalDateTime.now();
        auditLog.setCreatedAt(now);
        botAiAuditLogMapper.insert(auditLog);
        log.info("创建AI审计日志成功: requestId={}, botId={}, userId={}", auditLog.getRequestId(), auditLog.getBotId(), auditLog.getUserId());
    }

    /**
     * 更新日志
     * @param auditLog 日志信息
     */
    public void updateLog(BotAiAuditLog auditLog) {
        botAiAuditLogMapper.update(auditLog);
        log.info("更新AI审计日志成功: auditId={}, requestId={}", auditLog.getAuditId(), auditLog.getRequestId());
    }

    /**
     * 删除日志
     * @param auditId 日志ID
     */
    public void deleteLog(Long auditId) {
        botAiAuditLogMapper.delete(auditId);
        log.info("删除AI审计日志成功: auditId={}", auditId);
    }
}