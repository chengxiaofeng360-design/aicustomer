# MySQL数据库配置说明

## 数据库配置要求

### 1. MySQL版本要求
- MySQL 8.0 或更高版本
- 支持UTF8MB4字符集

### 2. 数据库创建
```sql
CREATE DATABASE ai_customer CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 用户权限
确保MySQL用户具有以下权限：
- CREATE
- DROP
- INSERT
- UPDATE
- DELETE
- SELECT
- INDEX
- ALTER

### 4. 连接配置
在 `application.yml` 中配置：
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/ai_customer?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: 123456
```

### 5. 注意事项
- 请根据实际环境修改数据库连接信息
- 确保MySQL服务正在运行
- 建议使用专用数据库用户，避免使用root用户
- 生产环境请修改默认密码

### 6. 禁止使用H2数据库
- 已从pom.xml中移除H2依赖
- 不再支持H2数据库配置
- 仅支持MySQL数据库

## 启动步骤
1. 确保MySQL服务运行
2. 创建数据库：`ai_customer`
3. 修改application.yml中的数据库连接信息
4. 启动应用程序
5. 系统将自动执行初始化脚本
