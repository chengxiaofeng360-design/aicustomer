#!/bin/bash
# 数据库连接测试脚本

echo "=== 数据库连接测试 ==="
echo ""

# 从 application.yml 读取配置（简单解析）
DB_HOST="bj-cynosdbmysql-grp-bbi2ygoo.sql.tencentcdb.com"
DB_PORT="26713"
DB_NAME="master_db"
DB_USER="root"  # 请修改为实际用户名
DB_PASS="your_password"  # 请修改为实际密码

echo "测试配置："
echo "  主机: $DB_HOST"
echo "  端口: $DB_PORT"
echo "  数据库: $DB_NAME"
echo "  用户: $DB_USER"
echo ""

# 测试1: 网络连通性
echo "1. 测试网络连通性..."
if ping -c 2 $DB_HOST > /dev/null 2>&1; then
    echo "   ✅ 网络连通正常"
else
    echo "   ❌ 无法 ping 通数据库服务器"
    echo "   提示: 检查网络连接或 DNS 解析"
fi
echo ""

# 测试2: 端口是否开放
echo "2. 测试端口连接..."
if timeout 5 bash -c "cat < /dev/null > /dev/tcp/$DB_HOST/$DB_PORT" 2>/dev/null; then
    echo "   ✅ 端口 $DB_PORT 可访问"
else
    echo "   ❌ 端口 $DB_PORT 无法连接"
    echo "   提示: 检查防火墙、安全组或白名单设置"
fi
echo ""

# 测试3: MySQL 连接
echo "3. 测试 MySQL 连接..."
if command -v mysql > /dev/null 2>&1; then
    mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" -e "SELECT VERSION(), DATABASE();" 2>&1
    if [ $? -eq 0 ]; then
        echo "   ✅ MySQL 连接成功"
        echo ""
        echo "4. 检查数据库是否存在..."
        mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" -e "SHOW DATABASES LIKE '$DB_NAME';" 2>&1 | grep -q "$DB_NAME"
        if [ $? -eq 0 ]; then
            echo "   ✅ 数据库 $DB_NAME 存在"
        else
            echo "   ⚠️  数据库 $DB_NAME 不存在，需要创建"
        fi
    else
        echo "   ❌ MySQL 连接失败"
        echo "   可能原因:"
        echo "   - 用户名或密码错误"
        echo "   - 用户没有远程连接权限"
        echo "   - IP 地址未加入白名单"
    fi
else
    echo "   ⚠️  mysql 客户端未安装，跳过连接测试"
    echo "   安装方法: sudo apt install mysql-client  # Ubuntu/Debian"
    echo "            sudo yum install mysql  # CentOS/RHEL"
fi
echo ""

echo "=== 测试完成 ==="
echo ""
echo "如果连接失败，请检查："
echo "1. 用户名和密码是否正确"
echo "2. 服务器 IP 是否已加入 CynosDB 白名单"
echo "3. 安全组是否允许访问 26713 端口"
echo "4. 数据库 master_db 是否已创建"

