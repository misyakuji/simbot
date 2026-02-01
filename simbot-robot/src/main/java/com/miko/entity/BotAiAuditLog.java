package com.miko.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BotAiAuditLog {
    private Long auditId; // 日志ID
    private String botId; // Bot ID
    private String requestId; // 请求ID
    private String modelName; // 模型名称
    private String inputText; // 输入文本
    private String outputText; // 输出文本
    private String userId; // 用户ID
    private String ipAddress; // IP地址
    private Integer processingTime; // 处理时间（毫秒）
    private Integer riskLevel; // 风险等级（0-3）
    private LocalDateTime createdAt; // 创建时间
}