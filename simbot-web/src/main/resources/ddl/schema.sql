-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS simbot CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE simbot;

-- 定时任务表
CREATE TABLE IF NOT EXISTS `bot_task`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '任务主键ID',
    `target_type` CHAR(1)         NOT NULL COMMENT '发送目标类型：0-群组，1-好友',
    `target_id`   VARCHAR(255)    NOT NULL COMMENT '发送目标（多个可逗号分隔）',
    `type`        VARCHAR(50)     NOT NULL COMMENT '任务类型（如：文本、图片、链接等）',
    `content`     TEXT            NOT NULL COMMENT '消息内容',
    `active`      CHAR(1)         NOT NULL DEFAULT '1' COMMENT '是否激活：1-激活，0-禁用',
    `create_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX idx_bot_task_active (`active`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='Bot定时任务表';

-- bot聊天联系人
CREATE TABLE IF NOT EXISTS `bot_chat_contact`
(
    `id`              INT(11)          NOT NULL AUTO_INCREMENT COMMENT '排序id',
    `contact_id`      BIGINT           NOT NULL COMMENT 'QQ号 / 用户唯一ID',

    -- 核心关系字段
    `favorability`    INT              NOT NULL DEFAULT 0 COMMENT '好感度',
    `intimacy_level`  INT              NOT NULL DEFAULT 0 COMMENT '亲密等级',

    -- 互动记忆
    `talk_count`      INT              NOT NULL DEFAULT 0 COMMENT '累计聊天次数',
    `last_talk_time`  DATETIME         NULL COMMENT '最后一次聊天时间',

    -- 主观印象
    `mood`            INT              NOT NULL DEFAULT 0 COMMENT '她当前对你的情绪状态',
    `remark`          VARCHAR(64)      NULL COMMENT 'Bot给你的备注',

    -- AI状态与人格
    `ai_persona`      VARCHAR(32)      NOT NULL DEFAULT 'default' COMMENT 'AI人格模板',
    `ai_model`        VARCHAR(64)      NOT NULL DEFAULT 'deepseek-chat' COMMENT '当前使用的AI模型',
    `ai_temperature`  DECIMAL(3,2)     NOT NULL DEFAULT 0.7 COMMENT 'AI随机度/情绪浮动',
    `ai_memory_summary` TEXT           NULL COMMENT 'AI对该用户的长期记忆摘要',

    -- 时间字段
    `create_time`     DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次见面时间',
    `update_time`     DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据更新时间',

    PRIMARY KEY (`id`) USING BTREE,
    INDEX idx_favorability (`favorability`),
    INDEX idx_last_talk_time (`last_talk_time`),
    INDEX idx_intimacy_level (`intimacy_level`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT ='Bot联系人表：记录Bot对联系人的关系、好感度、人格和AI状态';

-- Bot全局配置表
CREATE TABLE IF NOT EXISTS `bot_global_config`
(
    `config_id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '全局配置ID',
    `bot_id`               VARCHAR(255)    NOT NULL COMMENT 'Bot ID',
    `master_id`            VARCHAR(255)    NOT NULL COMMENT 'Bot主人ID',
    `current_model`        VARCHAR(64)     NOT NULL COMMENT '当前使用的模型',
    `model_list`           JSON            NULL COMMENT '可用模型列表（JSON格式）',
    `deep_thinking_enabled` CHAR(1)        NOT NULL DEFAULT '0' COMMENT '深度思考标志：1-开启，0-关闭',
    `model_parameters`     TEXT            NULL COMMENT '模型参数（JSON格式）',
    `enabled`              CHAR(1)         NOT NULL DEFAULT '1' COMMENT '可用性：1-可用，0-不可用',
    `create_time`          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`config_id`, `bot_id`),
    INDEX idx_bot_global_config_enabled (`enabled`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='Bot全局配置表';