-- 迁移脚本：为 customer 表添加 business_type 字段
-- 日期：2024-11-20

SET @sql = (
    SELECT IF(
        (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
         WHERE TABLE_SCHEMA = DATABASE()
           AND TABLE_NAME = 'customer'
           AND COLUMN_NAME = 'business_type') = 0,
        'ALTER TABLE customer ADD COLUMN business_type TINYINT DEFAULT 1 COMMENT ''具体业务类型(1:品种权申请客户,2:品种权转化推广客户,3:知识产权协作客户,4:科普教育合作客户,5:景观设计服务客户,6:图书出版客户)'' AFTER progress',
        'SELECT ''business_type 字段已存在，无需添加'' as message'
    )
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
