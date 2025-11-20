# 数据库连接配置说明

## 📋 配置原则

### 情况1：使用远程数据库（腾讯云 CynosDB）
**必须使用远程地址**，无论应用部署在哪里。

```
jdbc:mysql://bj-cynosdbmysql-grp-bbi2ygoo.sql.tencentcdb.com:26713/master_db
```

### 情况2：数据库在同一台服务器上
**使用本地地址**（更快、更安全）。

```
jdbc:mysql://127.0.0.1:3306/zqgl
或
jdbc:mysql://localhost:3306/zqgl
```

---

## 🔧 配置步骤

### 1. 修改 `application.yml`

#### 生产环境（远程数据库）
```yaml
spring:
  datasource:
    url: jdbc:mysql://bj-cynosdbmysql-grp-bbi2ygoo.sql.tencentcdb.com:26713/master_db?useUnicode=true&autoReconnect=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username  # 腾讯云数据库用户名
    password: your_password   # 腾讯云数据库密码
```

#### 本地开发环境
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/zqgl?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
    username: root
    password: 12345678
```

---

## ⚠️ 重要注意事项

### 1. 数据库名称
- 远程数据库名：`master_db`（根据腾讯云实际数据库名修改）
- 本地数据库名：`zqgl`

**如果远程数据库名不是 `master_db`，请修改 URL 中的数据库名！**

### 2. 数据库初始化
- **远程数据库**：需要手动导入 `init.sql`，不要使用 `createDatabaseIfNotExist=true`
- **本地数据库**：可以使用 `createDatabaseIfNotExist=true` 自动创建

### 3. 网络连接
- 确保服务器可以访问腾讯云 CynosDB 的 IP 和端口
- 检查安全组规则（开放 26713 端口）
- 检查白名单设置（将服务器 IP 加入数据库白名单）

### 4. SSL 配置
- 远程数据库：`useSSL=false`（如果未启用 SSL）
- 如果启用了 SSL，需要配置证书

---

## 🧪 测试连接

### 在服务器上测试数据库连接
```bash
# 测试远程数据库连接
mysql -h bj-cynosdbmysql-grp-bbi2ygoo.sql.tencentcdb.com -P 26713 -u your_username -p

# 测试本地数据库连接
mysql -h 127.0.0.1 -P 3306 -u root -p
```

### 测试应用连接
```bash
# 启动应用后，检查日志
tail -f logs/ai-customer.log

# 或查看启动日志
java -jar ai-customer-management-1.0.0.jar
```

---

## 📝 配置示例

### 完整配置示例（生产环境）
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://bj-cynosdbmysql-grp-bbi2ygoo.sql.tencentcdb.com:26713/master_db?useUnicode=true&autoReconnect=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=Asia/Shanghai
    username: aicustomer_user
    password: your_secure_password
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      connection-timeout: 30000
      max-lifetime: 1800000
```

---

## 🔒 安全建议

1. **不要将密码提交到 Git**
   - 使用环境变量：`${DB_PASSWORD}`
   - 或使用 Spring Cloud Config
   - 或使用密钥管理服务

2. **使用环境变量配置（推荐）**
```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/zqgl}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD}
```

启动时传入：
```bash
java -jar app.jar --spring.datasource.password=your_password
```

---

## ✅ 检查清单

部署前确认：
- [ ] 数据库地址正确（远程/本地）
- [ ] 数据库名称正确（`master_db` 或 `zqgl`）
- [ ] 用户名和密码正确
- [ ] 端口号正确（26713 或 3306）
- [ ] 网络连通性（可以 ping 通数据库服务器）
- [ ] 安全组/防火墙规则已配置
- [ ] 数据库白名单已添加服务器 IP
- [ ] 数据库已初始化（执行了 `init.sql`）

