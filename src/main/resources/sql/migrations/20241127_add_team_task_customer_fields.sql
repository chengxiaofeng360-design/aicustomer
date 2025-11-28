-- 为 team_task 表添加客户关联字段
ALTER TABLE team_task
    ADD COLUMN IF NOT EXISTS customer_id BIGINT COMMENT '关联客户ID',
    ADD COLUMN IF NOT EXISTS customer_name VARCHAR(100) COMMENT '关联客户姓名';
