-- 创建bot_task表（如果不存在）
CREATE TABLE IF NOT EXISTS `bot_task` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '任务主键ID',
    `target_type` CHAR(1) NOT NULL COMMENT '发送目标类型：0-群组，1-好友',
    `target_id` VARCHAR(255) NOT NULL COMMENT '发送目标（多个可逗号分隔）',
    `type` VARCHAR(50) NOT NULL COMMENT '任务类型（如：文本、图片、链接等）',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `active` CHAR(1) NOT NULL DEFAULT '1' COMMENT '是否激活：1-激活，0-禁用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX idx_bot_task_active (`active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Bot定时任务表';
