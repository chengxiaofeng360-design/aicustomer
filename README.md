# AI客户管理系统

一个基于Spring Boot的智能客户管理系统，集成了AI功能，提供完整的客户管理解决方案。

## 🚀 技术栈

### 后端技术
- **框架**: Spring Boot 2.4.4
- **语言**: Java 8
- **构建工具**: Apache Maven
- **数据库**: MySQL 8.0
- **ORM**: MyBatis 2.2.0
- **安全**: Spring Security
- **监控**: Spring Actuator

### 前端技术
- **框架**: Bootstrap 5.1.3
- **图标**: Bootstrap Icons
- **交互**: 原生JavaScript

### 核心依赖
- **JSON处理**: Google Gson 2.8.9, Alibaba FastJSON 1.2.83
- **工具库**: Google Guava 30.1.1-jre, Apache Commons
- **文档处理**: Apache POI 5.1.0, POI-TL 1.12.0
- **PDF处理**: PDFBox 2.0.20
- **图表生成**: QuickChart 1.2.0
- **计算机视觉**: JavaCV 1.5.2
- **浏览器自动化**: Jvppeteer 1.1.5
- **AI服务**: Alibaba NLS SDK 2.2.1

## 📁 项目结构

```
AiCustomer/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/aicustomer/
│   │   │       ├── AiCustomerApplication.java          # 主启动类
│   │   │       ├── common/                             # 公共类
│   │   │       │   ├── Result.java                     # 统一返回结果
│   │   │       │   └── PageResult.java                 # 分页结果
│   │   │       ├── config/                             # 配置类
│   │   │       │   ├── DatabaseConfig.java             # 数据库配置
│   │   │       │   └── WebConfig.java                  # Web配置
│   │   │       ├── controller/                         # 控制器层
│   │   │       │   └── CustomerController.java         # 客户控制器
│   │   │       ├── entity/                             # 实体类
│   │   │       │   ├── BaseEntity.java                 # 基础实体
│   │   │       │   └── Customer.java                   # 客户实体
│   │   │       ├── mapper/                             # 数据访问层
│   │   │       │   └── CustomerMapper.java             # 客户Mapper
│   │   │       └── service/                            # 服务层
│   │   │           ├── CustomerService.java            # 客户服务接口
│   │   │           └── impl/
│   │   │               └── CustomerServiceImpl.java    # 客户服务实现
│   │   └── resources/
│   │       ├── mapper/                                 # MyBatis映射文件
│   │       │   └── CustomerMapper.xml
│   │       ├── sql/                                    # 数据库脚本
│   │       │   └── init.sql                            # 初始化脚本
│   │       ├── static/                                 # 静态资源
│   │       │   └── index.html                          # 前端页面
│   │       └── application.yml                         # 配置文件
├── pom.xml                                             # Maven配置
└── README.md                                           # 项目说明
```

## 🛠️ 快速开始

### 环境要求
- JDK 8+
- Maven 3.6+
- MySQL 8.0+

### 1. 克隆项目
```bash
git clone <repository-url>
cd AiCustomer
```

### 2. 数据库配置
1. 创建MySQL数据库：
```sql
CREATE DATABASE ai_customer_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```bash
mysql -u root -p ai_customer_db < src/main/resources/sql/init.sql
```

3. 修改数据库连接配置（`src/main/resources/application.yml`）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_customer_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password
```

### 3. 编译运行
```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/ai-customer-management-1.0.0.jar
```

### 4. 访问系统
- 前端页面: http://localhost:8080/ai-customer/
- API文档: http://localhost:8080/ai-customer/actuator
- 健康检查: http://localhost:8080/ai-customer/actuator/health

## 📋 功能特性

### 核心功能
- ✅ **客户管理**: 客户信息的增删改查
- ✅ **分页查询**: 支持分页和条件筛选
- ✅ **数据统计**: 客户数据统计和展示
- ✅ **响应式设计**: 适配各种设备屏幕

### 技术特性
- ✅ **RESTful API**: 标准的REST接口设计
- ✅ **统一返回格式**: 标准化的API响应格式
- ✅ **异常处理**: 全局异常处理机制
- ✅ **数据验证**: 输入数据验证
- ✅ **事务管理**: 数据库事务支持
- ✅ **日志记录**: 完整的日志记录

### 待开发功能
- 🔄 **用户认证**: Spring Security集成
- 🔄 **权限管理**: 基于角色的权限控制
- 🔄 **AI功能**: 智能客户分析
- 🔄 **数据导入导出**: Excel/PDF导出
- 🔄 **消息通知**: 客户联系提醒
- 🔄 **数据可视化**: 图表和报表

## 🔧 配置说明

### 数据库配置
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/ai_customer_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: 123456
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
```

### MyBatis配置
```yaml
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.aicustomer.entity
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: true
```

### 文件上传配置
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB
```

## 📊 API接口

### 客户管理接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/customer/{id}` | 根据ID查询客户 |
| GET | `/api/customer/code/{customerCode}` | 根据客户编号查询 |
| GET | `/api/customer/list` | 查询客户列表 |
| GET | `/api/customer/page` | 分页查询客户 |
| POST | `/api/customer` | 添加客户 |
| PUT | `/api/customer` | 更新客户 |
| DELETE | `/api/customer/{id}` | 删除客户 |
| DELETE | `/api/customer/batch` | 批量删除客户 |

### 请求示例

#### 添加客户
```bash
curl -X POST http://localhost:8080/ai-customer/api/customer \
  -H "Content-Type: application/json" \
  -d '{
    "customerCode": "CUST006",
    "customerName": "测试客户",
    "customerType": 1,
    "phone": "13800138006",
    "email": "test@example.com",
    "customerLevel": 1,
    "status": 1,
    "source": 1
  }'
```

#### 分页查询
```bash
curl "http://localhost:8080/ai-customer/api/customer/page?pageNum=1&pageSize=10"
```

## 🗄️ 数据库设计

### 客户表 (customer)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| customer_code | VARCHAR(50) | 客户编号 |
| customer_name | VARCHAR(100) | 客户姓名 |
| customer_type | TINYINT | 客户类型(1:个人,2:企业) |
| phone | VARCHAR(20) | 手机号码 |
| email | VARCHAR(100) | 邮箱 |
| address | VARCHAR(500) | 地址 |
| customer_level | TINYINT | 客户等级(1:普通,2:VIP,3:钻石) |
| status | TINYINT | 客户状态(1:正常,2:冻结,3:注销) |
| source | TINYINT | 客户来源(1:线上,2:线下,3:推荐) |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

## 🚀 部署说明

### 开发环境
```bash
# 启动开发服务器
mvn spring-boot:run
```

### 生产环境
```bash
# 打包
mvn clean package -Pprod

# 运行
java -jar target/ai-customer-management-1.0.0.jar --spring.profiles.active=prod
```

### Docker部署
```dockerfile
FROM openjdk:8-jre-alpine
COPY target/ai-customer-management-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📝 更新日志

### v1.0.0 (2024-01-15)
- ✅ 基础项目架构搭建
- ✅ 客户管理功能实现
- ✅ 前端界面开发
- ✅ 数据库设计和初始化
- ✅ RESTful API接口

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- 项目维护者: AI Customer Management Team
- 邮箱: support@aicustomer.com
- 项目地址: https://github.com/your-org/ai-customer-management

---

**注意**: 这是一个演示项目，生产环境使用前请进行充分测试和安全加固。

