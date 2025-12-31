-- 修复 sys_user 表结构
-- 添加所有缺失的字段

USE zqgl;

-- 添加 real_name 字段（如果不存在）
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'zqgl' 
AND TABLE_NAME = 'sys_user' 
AND COLUMN_NAME = 'real_name';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE sys_user ADD COLUMN real_name VARCHAR(100) COMMENT ''真实姓名'' AFTER password',
    'SELECT ''real_name column already exists'' AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 create_by 字段（如果不存在）
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'zqgl' 
AND TABLE_NAME = 'sys_user' 
AND COLUMN_NAME = 'create_by';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE sys_user ADD COLUMN create_by VARCHAR(50) COMMENT ''创建人'' AFTER update_time',
    'SELECT ''create_by column already exists'' AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 update_by 字段（如果不存在）
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'zqgl' 
AND TABLE_NAME = 'sys_user' 
AND COLUMN_NAME = 'update_by';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE sys_user ADD COLUMN update_by VARCHAR(50) COMMENT ''更新人'' AFTER create_by',
    'SELECT ''update_by column already exists'' AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 deleted 字段（如果不存在）
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'zqgl' 
AND TABLE_NAME = 'sys_user' 
AND COLUMN_NAME = 'deleted';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE sys_user ADD COLUMN deleted TINYINT NOT NULL DEFAULT 0 COMMENT ''删除标志(0:未删除,1:已删除)'' AFTER update_by',
    'SELECT ''deleted column already exists'' AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 version 字段（如果不存在）
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'zqgl' 
AND TABLE_NAME = 'sys_user' 
AND COLUMN_NAME = 'version';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE sys_user ADD COLUMN version INT NOT NULL DEFAULT 1 COMMENT ''版本号'' AFTER deleted',
    'SELECT ''version column already exists'' AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 user_type 字段（如果不存在）
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'zqgl' 
AND TABLE_NAME = 'sys_user' 
AND COLUMN_NAME = 'user_type';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE sys_user ADD COLUMN user_type TINYINT COMMENT ''用户类型'' AFTER version',
    'SELECT ''user_type column already exists'' AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 last_login_time 字段（如果不存在）
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'zqgl' 
AND TABLE_NAME = 'sys_user' 
AND COLUMN_NAME = 'last_login_time';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE sys_user ADD COLUMN last_login_time DATETIME COMMENT ''最后登录时间'' AFTER user_type',
    'SELECT ''last_login_time column already exists'' AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 permission_settings 字段（如果不存在）
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'zqgl' 
AND TABLE_NAME = 'sys_user' 
AND COLUMN_NAME = 'permission_settings';

SET @sql = IF(@col_exists = 0, 
    'ALTER TABLE sys_user ADD COLUMN permission_settings TEXT COMMENT ''权限设置JSON'' AFTER last_login_time',
    'SELECT ''permission_settings column already exists'' AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
