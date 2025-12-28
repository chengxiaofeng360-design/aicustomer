-- 添加 sys_user 表缺失的 real_name 字段
USE zqgl;

-- 检查并添加 real_name 字段
ALTER TABLE sys_user 
ADD COLUMN IF NOT EXISTS real_name VARCHAR(100) COMMENT '真实姓名' AFTER password;

-- 同时检查并添加 user_type 字段（如果也缺失）
ALTER TABLE sys_user 
ADD COLUMN IF NOT EXISTS user_type TINYINT COMMENT '用户类型' AFTER version;

-- 同时检查并添加 last_login_time 字段（UserMapper.xml 中用到）
ALTER TABLE sys_user 
ADD COLUMN IF NOT EXISTS last_login_time DATETIME COMMENT '最后登录时间' AFTER user_type;

SELECT 'Migration completed successfully!' AS status;
