-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS simbot CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE simbot;

-- 创建friend_user表（如果不存在）
CREATE TABLE IF NOT EXISTS `friend_user`
(
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '排序id',
    `user_id` BIGINT NOT NULL COMMENT 'QQ号 / 用户唯一ID',

    -- 核心关系字段
    `favorability` INT NOT NULL DEFAULT 0 COMMENT '好感度',
    `intimacy_level` INT NOT NULL DEFAULT 0 COMMENT '亲密等级',

    -- 互动记忆
    `talk_count` INT NOT NULL DEFAULT 0 COMMENT '累计聊天次数',
    `last_talk_time` DATETIME NULL COMMENT '最后一次聊天时间',

    -- 主观印象
    `mood` INT NOT NULL DEFAULT 0 COMMENT '她当前对你的情绪状态',
    `remark` VARCHAR(64) NULL COMMENT 'Bot给你的备注',

    -- AI状态与人格
    `ai_persona` VARCHAR(32) NOT NULL DEFAULT 'default' COMMENT 'AI人格模板',
    `ai_model` VARCHAR(64) NOT NULL DEFAULT 'deepseek-chat' COMMENT '当前使用的AI模型',
    `ai_temperature` DECIMAL(3,2) NOT NULL DEFAULT 0.7 COMMENT 'AI随机度/情绪浮动',
    `ai_memory_summary` TEXT NULL COMMENT 'AI对该用户的长期记忆摘要',

    -- 时间字段
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次见面时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP COMMENT '数据更新时间',

    PRIMARY KEY (`id`) USING BTREE,
    INDEX idx_favorability (`favorability`),
    INDEX idx_last_talk_time (`last_talk_time`),
    INDEX idx_intimacy_level (`intimacy_level`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT ='Bot用户表：记录她对用户的关系、好感度、人格和AI状态';

-- 初始化数据库字符集（确保正确）
ALTER DATABASE simbot CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户并授权（如果不存在）
CREATE USER IF NOT EXISTS 'simbot_user'@'%' IDENTIFIED BY 'simbot_pass';
GRANT ALL PRIVILEGES ON simbot.* TO 'simbot_user'@'%';
FLUSH PRIVILEGES;
