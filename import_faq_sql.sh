#!/bin/bash

# 正确的数据库名称是 zqgl
echo "开始导入FAQ数据到 zqgl 数据库..."

# 方法1: 使用mysql命令行（如果可用）
if command -v mysql &> /dev/null; then
    mysql -h 127.0.0.1 -u root -p123456 zqgl < import_faq_data.sql
    echo "导入完成！"
else
    echo "未找到mysql命令，请手动执行："
    echo "mysql -h 127.0.0.1 -u root -p zqgl < import_faq_data.sql"
fi
