-- 添加品种字段到客户表
ALTER TABLE customer ADD COLUMN IF NOT EXISTS variety VARCHAR(255) COMMENT '品种信息';

-- 更新现有记录的品种字段为默认值
UPDATE customer SET variety = '' WHERE variety IS NULL;
