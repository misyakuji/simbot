-- 初始化数据库脚本
-- 确保数据库字符集正确
ALTER DATABASE simbot CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户并授权（如果不存在）
CREATE USER IF NOT EXISTS 'simbot_user'@'%' IDENTIFIED BY 'simbot_pass';
GRANT ALL PRIVILEGES ON simbot.* TO 'simbot_user'@'%';
FLUSH PRIVILEGES;