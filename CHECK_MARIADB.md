# MariaDB 服务检查与启动指南

## 🔍 问题诊断

### 1. 检查 MariaDB 服务状态
```bash
# 检查服务是否存在
sudo systemctl status mariadb
# 或
sudo systemctl status mysql

# 检查进程
ps aux | grep mariadb
ps aux | grep mysql
```

### 2. 检查端口是否监听
```bash
# 检查 3306 端口
sudo netstat -tlnp | grep 3306
# 或
sudo ss -tlnp | grep 3306
```

### 3. 检查 MariaDB 是否安装
```bash
# 检查安装包
rpm -qa | grep mariadb  # CentOS/RHEL
dpkg -l | grep mariadb  # Ubuntu/Debian

# 检查可执行文件
which mysql
which mariadb
```

---

## 🚀 解决方案

### 情况1：MariaDB 未安装

#### Ubuntu/Debian
```bash
sudo apt update
sudo apt install -y mariadb-server mariadb-client
sudo systemctl start mariadb
sudo systemctl enable mariadb
```

#### CentOS/RHEL
```bash
sudo yum install -y mariadb-server mariadb
sudo systemctl start mariadb
sudo systemctl enable mariadb
```

### 情况2：MariaDB 已安装但未启动

```bash
# 启动服务
sudo systemctl start mariadb
# 或
sudo systemctl start mysql

# 设置开机自启
sudo systemctl enable mariadb
# 或
sudo systemctl enable mysql

# 检查状态
sudo systemctl status mariadb
```

### 情况3：MariaDB 配置问题

```bash
# 初始化数据库（如果是新安装）
sudo mysql_secure_installation

# 检查配置文件
sudo cat /etc/mysql/mariadb.conf.d/50-server.cnf  # Ubuntu/Debian
sudo cat /etc/my.cnf  # CentOS/RHEL

# 检查是否只监听本地
grep bind-address /etc/mysql/mariadb.conf.d/50-server.cnf
# 应该显示：bind-address = 127.0.0.1
```

---

## ✅ 验证步骤

### 1. 测试本地连接
```bash
# 使用 root 用户连接
sudo mysql -u root -p

# 或指定主机
mysql -h 127.0.0.1 -P 3306 -u root -p
```

### 2. 创建数据库
```sql
CREATE DATABASE IF NOT EXISTS zqgl CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SHOW DATABASES;
EXIT;
```

### 3. 测试应用连接
```bash
# 启动应用后检查日志
tail -f logs/ai-customer.log

# 或直接运行看输出
java -jar ai-customer-management-1.0.0.jar
```

---

## 🔧 如果仍然无法连接

### 检查防火墙
```bash
# 检查防火墙规则
sudo ufw status  # Ubuntu
sudo firewall-cmd --list-all  # CentOS

# 确保本地连接不需要开放端口（127.0.0.1 是本地回环）
```

### 检查 MariaDB 用户权限
```sql
-- 登录 MariaDB
sudo mysql -u root -p

-- 检查用户
SELECT User, Host FROM mysql.user;

-- 创建/授权用户（如果需要）
CREATE USER IF NOT EXISTS 'root'@'127.0.0.1' IDENTIFIED BY '1q2w3e4R';
GRANT ALL PRIVILEGES ON zqgl.* TO 'root'@'127.0.0.1';
FLUSH PRIVILEGES;
EXIT;
```

---

## 📝 快速诊断脚本

```bash
#!/bin/bash
echo "=== MariaDB 诊断 ==="
echo ""
echo "1. 检查服务状态："
sudo systemctl status mariadb 2>/dev/null || sudo systemctl status mysql 2>/dev/null || echo "❌ 服务未找到"
echo ""
echo "2. 检查端口监听："
sudo netstat -tlnp | grep 3306 || echo "❌ 3306 端口未监听"
echo ""
echo "3. 检查进程："
ps aux | grep -E 'mariadb|mysql' | grep -v grep || echo "❌ 进程未运行"
echo ""
echo "4. 测试连接："
mysql -h 127.0.0.1 -P 3306 -u root -p1q2w3e4R -e "SELECT VERSION();" 2>&1 || echo "❌ 连接失败"
```

