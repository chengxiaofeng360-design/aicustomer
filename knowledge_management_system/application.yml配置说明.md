# application.yml 配置说明

将以下配置添加到你的项目 `src/main/resources/application.yml` 文件中：

## 完整配置示例

```yaml
spring:
  # 数据库配置
  datasource:
    url: jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  # 文件上传配置
  servlet:
    multipart:
      # 单个文件最大大小
      max-file-size: 50MB
      # 整个请求最大大小
      max-request-size: 50MB
      # 是否启用文件上传
      enabled: true

# 文件上传路径配置（必须配置）
file:
  upload:
    # 文件存储路径，可以是绝对路径或相对路径
    path: /path/to/your/upload/directory
    # 示例：
    # Windows: D:/uploads/knowledge
    # Linux/Mac: /var/app/uploads/knowledge
    # 相对路径: uploads/knowledge

# MyBatis配置
mybatis:
  # Mapper XML文件位置
  mapper-locations: classpath:mapper/*.xml
  # 实体类包路径
  type-aliases-package: com.aicustomer.entity
  configuration:
    # 开启驼峰命名转换（数据库字段 user_name -> Java属性 userName）
    map-underscore-to-camel-case: true
    # 日志实现（开发环境可开启，生产环境建议关闭）
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

# PageHelper分页插件配置
pagehelper:
  # 数据库方言
  helper-dialect: mysql
  # 分页合理化，当页码<1时查询第一页，当页码>总页数时查询最后一页
  reasonable: true
  # 支持通过Mapper接口参数传递分页参数
  support-methods-arguments: true
  # 分页参数
  params: count=countSql
```

## 配置项说明

### 1. 数据库配置 (必需)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

**说明：**
- `url`: 数据库连接地址，修改 `your_database` 为你的数据库名
- `username`: 数据库用户名
- `password`: 数据库密码
- `useUnicode=true&characterEncoding=utf8`: 确保中文正确存储
- `serverTimezone=Asia/Shanghai`: 设置时区

### 2. 文件上传配置 (必需)

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB
      enabled: true
```

**说明：**
- `max-file-size`: 单个文件最大大小，可根据需求调整
- `max-request-size`: 整个请求最大大小（包含所有文件）
- `enabled`: 是否启用文件上传功能

**常用大小设置：**
- 小文件：`10MB`
- 中等文件：`50MB`
- 大文件：`100MB` 或更大

### 3. 文件存储路径配置 (必需)

```yaml
file:
  upload:
    path: /path/to/your/upload/directory
```

**说明：**
- 这是自定义配置，用于指定上传文件的存储位置
- 必须确保该目录存在且应用有写权限

**路径示例：**
```yaml
# Windows绝对路径
path: D:/uploads/knowledge

# Linux/Mac绝对路径
path: /var/app/uploads/knowledge

# 相对路径（相对于应用启动目录）
path: uploads/knowledge

# 用户目录
path: ~/uploads/knowledge
```

**创建目录命令：**
```bash
# Linux/Mac
mkdir -p /var/app/uploads/knowledge
chmod 755 /var/app/uploads/knowledge

# Windows
mkdir D:\uploads\knowledge
```

### 4. MyBatis配置 (必需)

```yaml
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.aicustomer.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

**说明：**
- `mapper-locations`: Mapper XML文件位置，默认在 `resources/mapper/` 目录
- `type-aliases-package`: 实体类包路径，如果你的包名不同需要修改
- `map-underscore-to-camel-case`: 自动转换数据库下划线命名到Java驼峰命名
- `log-impl`: SQL日志输出（生产环境建议注释掉）

### 5. PageHelper配置 (必需)

```yaml
pagehelper:
  helper-dialect: mysql
  reasonable: true
  support-methods-arguments: true
```

**说明：**
- `helper-dialect`: 数据库类型，支持 mysql、oracle、postgresql 等
- `reasonable`: 分页合理化，防止页码越界
- `support-methods-arguments`: 支持通过方法参数传递分页参数

## 环境特定配置

### 开发环境 (application-dev.yml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dev_database?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: dev_user
    password: dev_password

file:
  upload:
    path: ./uploads/knowledge

mybatis:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # 开启SQL日志
```

### 生产环境 (application-prod.yml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://prod-db-server:3306/prod_database?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai
    username: ${DB_USERNAME}  # 使用环境变量
    password: ${DB_PASSWORD}  # 使用环境变量
  
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB

file:
  upload:
    path: /var/app/uploads/knowledge

mybatis:
  configuration:
    # 生产环境不输出SQL日志
    # log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

## 配置检查清单

在启动应用前，请确认：

- [ ] 数据库连接信息正确（URL、用户名、密码）
- [ ] 数据库已创建并执行了建表SQL
- [ ] 文件上传路径已配置且目录存在
- [ ] 文件上传目录有写权限
- [ ] MyBatis的包路径与你的项目一致
- [ ] 文件大小限制符合你的需求

## 常见问题

**Q: 文件上传提示"超过最大大小"？**
```yaml
# 增加文件大小限制
spring:
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB
```

**Q: 找不到Mapper？**
```yaml
# 检查mapper-locations路径是否正确
mybatis:
  mapper-locations: classpath:mapper/*.xml
```

**Q: 数据库连接失败？**
- 检查数据库是否启动
- 检查URL、用户名、密码是否正确
- 检查防火墙是否允许连接
- 检查MySQL是否允许远程连接

**Q: 文件上传后找不到文件？**
- 检查 `file.upload.path` 配置
- 检查目录权限
- 查看应用日志确认实际保存路径
