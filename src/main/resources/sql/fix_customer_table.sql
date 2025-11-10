-- 修复customer表结构：添加缺失字段
-- 此脚本会在应用启动时自动执行

USE ai_customer_db;

-- 添加position字段（如果不存在）
ALTER TABLE customer 
ADD COLUMN IF NOT EXISTS position VARCHAR(100) COMMENT '职务' AFTER nationality;

-- 添加qq_weixin字段（如果不存在）
ALTER TABLE customer 
ADD COLUMN IF NOT EXISTS qq_weixin VARCHAR(100) COMMENT 'QQ/微信' AFTER position;

-- 添加cooperation_content字段（如果不存在）
ALTER TABLE customer 
ADD COLUMN IF NOT EXISTS cooperation_content TEXT COMMENT '合作内容' AFTER qq_weixin;

-- 添加region字段（如果不存在）
ALTER TABLE customer 
ADD COLUMN IF NOT EXISTS region VARCHAR(50) COMMENT '地区' AFTER cooperation_content;

