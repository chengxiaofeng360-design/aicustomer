#!/bin/bash
# 数据库初始化脚本

DB_NAME="ai_customer_db"
DB_USER="root"
DB_PASS="12345678"
SCHEMA_FILE="src/main/resources/schema.sql"

echo "=========================================="
echo "初始化数据库: $DB_NAME"
echo "=========================================="
echo ""

# 检查MySQL是否可用
if ! command -v mysql &> /dev/null; then
    echo "❌ MySQL命令行工具不可用"
    echo ""
    echo "请手动执行以下SQL："
    echo "1. 创建数据库："
    echo "   CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    echo ""
    echo "2. 初始化表结构："
    echo "   使用MySQL客户端执行: $SCHEMA_FILE"
    echo ""
    exit 1
fi

# 创建数据库
echo "1. 创建数据库..."
mysql -u$DB_USER -p$DB_PASS -e "CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>&1

if [ $? -eq 0 ]; then
    echo "✅ 数据库创建成功"
else
    echo "❌ 数据库创建失败，请检查MySQL连接和权限"
    exit 1
fi

echo ""

# 初始化表结构
echo "2. 初始化表结构..."
mysql -u$DB_USER -p$DB_PASS $DB_NAME < $SCHEMA_FILE 2>&1

if [ $? -eq 0 ]; then
    echo "✅ 表结构初始化成功"
else
    echo "⚠️  表结构初始化可能有问题，请检查错误信息"
fi

echo ""
echo "=========================================="
echo "数据库初始化完成"
echo "=========================================="

