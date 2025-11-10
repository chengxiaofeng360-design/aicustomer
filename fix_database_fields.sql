-- 修复数据库表结构：添加新字段
-- 执行此SQL以更新customer表结构

USE ai_customer_db;

-- 检查并添加position字段
ALTER TABLE customer 
ADD COLUMN IF NOT EXISTS position VARCHAR(100) COMMENT '职务' AFTER nationality;

-- 检查并添加qq_weixin字段
ALTER TABLE customer 
ADD COLUMN IF NOT EXISTS qq_weixin VARCHAR(100) COMMENT 'QQ/微信' AFTER position;

-- 检查并添加cooperation_content字段
ALTER TABLE customer 
ADD COLUMN IF NOT EXISTS cooperation_content TEXT COMMENT '合作内容' AFTER qq_weixin;

-- 检查并删除applicant_nature字段（如果存在）
-- ALTER TABLE customer DROP COLUMN IF EXISTS applicant_nature;

-- 验证字段
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    CHARACTER_MAXIMUM_LENGTH,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'ai_customer_db' 
  AND TABLE_NAME = 'customer' 
  AND COLUMN_NAME IN ('position', 'qq_weixin', 'cooperation_content', 'applicant_nature')
ORDER BY COLUMN_NAME;

