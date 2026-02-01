package com.miko.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class BotGlobalConfig {
    private Long configId; // 全局配置ID
    private String botId; // Bot ID
    private String masterId; // Bot主人ID
    private String currentModel; // 当前使用的模型
    private String modelList; // 可用模型列表（JSON格式）
    private String deepThinkingEnabled; // 深度思考标志：1-开启，0-关闭
    private String modelParameters; // 模型参数（JSON格式）
    private String enabled; // 可用性：1-可用，0-不可用
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}