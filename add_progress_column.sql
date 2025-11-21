-- 检查并添加 progress 字段到 customer 表
SET @sql = (
    SELECT IF(
        (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
         WHERE TABLE_SCHEMA = DATABASE()
           AND TABLE_NAME = 'customer'
           AND COLUMN_NAME = 'progress') = 0,
        'ALTER TABLE customer ADD COLUMN progress TINYINT DEFAULT 0 COMMENT ''进度(0:未开始,1:进行中,2:暂停中,3:已成功,4:放弃)'' AFTER protection_password',
        'SELECT ''progress 字段已存在，无需添加'' as message'
    )
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
