# 技术架构约束文档

## 文档说明

本文档定义了AI客户管理系统项目的前后端技术约束和架构约束。**严格禁止**使用本文档规定之外的技术、框架、工具和脚本。

**文档版本**: 2.0.0  
**创建日期**: 2025-11-01  
**最后更新**: 2025-11-01  
**维护者**: AI客户管理系统架构团队

---

## 禁止使用的技术

### 明确禁止
- **Docker** - 禁止使用任何Docker相关技术、Dockerfile、docker-compose等
- **H2数据库** - 禁止使用H2内存数据库
- **任何当前技术栈之外的技术** - 禁止添加新的框架、库或工具

### 禁止的架构模式
- 微服务架构
- 领域驱动设计(DDD)
- 事件驱动架构
- 消息队列架构
- SPA框架（Vue/React/Angular）
- 前端路由框架
- 前端状态管理库

---

## 系统架构设计

### 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        AI客户管理系统                            │
├─────────────────────────────────────────────────────────────────┤
│  Frontend Layer (前端层)                                        │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │  HTML5 + CSS3 + JavaScript + Bootstrap 5.1.3              │ │
│  │  - 响应式设计                                               │ │
│  │  - 客户管理界面                                             │ │
│  │  - 数据可视化                                               │ │
│  └─────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│  Web Layer (Web层)                                             │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │  Spring MVC Controllers                                     │ │
│  │  - 统一异常处理                                             │ │
│  │  - 跨域配置                                                 │ │
│  └─────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│  Service Layer (服务层)                                        │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │  Business Logic Services                                   │ │
│  │  - 事务管理                                                 │ │
│  │  - 业务规则验证                                             │ │
│  └─────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│  Data Access Layer (数据访问层)                                │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │  MyBatis Mappers                                           │ │
│  │  - XML映射文件                                             │ │
│  │  - 动态SQL                                                 │ │
│  └─────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│  Database Layer (数据库层)                                     │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │  MySQL 8.0                                                 │ │
│  │  - customer (客户表)                                        │ │
│  │  - customer_detail (客户详情表)                             │ │
│  │  - customer_tag (客户标签表)                                │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### 分层架构约束

#### 后端架构
- **分层架构**: Controller → Service → Mapper → Database
- **设计模式**: 
  - MVC模式
  - Service层接口+实现模式
  - Repository模式（通过MyBatis Mapper实现）

#### 前端架构
- **单页面应用**: 多个HTML页面，无SPA框架
- **前后端分离**: 前端通过RESTful API与后端通信

### 包结构规范

```
com.aicustomer/
├── AiCustomerApplication.java          # 主启动类
├── common/                             # 公共类
│   ├── Result.java                     # 统一返回结果
│   └── PageResult.java                  # 分页结果
├── config/                             # 配置类
│   ├── DatabaseConfig.java             # 数据库配置
│   ├── WebConfig.java                  # Web配置
│   ├── SecurityConfig.java             # 安全配置
│   └── CustomUserDetailsService.java   # 用户详情服务
├── controller/                         # 控制器层
│   └── [业务]Controller.java           # 各业务控制器
├── entity/                             # 实体类
│   ├── BaseEntity.java                 # 基础实体
│   └── [业务]Entity.java               # 各业务实体
├── mapper/                             # 数据访问层
│   └── [业务]Mapper.java               # 各业务Mapper接口
└── service/                            # 服务层
    ├── [业务]Service.java              # 各业务服务接口
    └── impl/
        └── [业务]ServiceImpl.java      # 各业务服务实现
```

### 数据流向规范

```
1. 用户请求 → 前端页面 (HTML)
2. 前端页面 → AJAX请求 → Controller层
3. Controller层 → Service层 (业务逻辑)
4. Service层 → Mapper层 (数据访问)
5. Mapper层 → MySQL数据库
6. 数据库 → Mapper层 → Service层 → Controller层 → 前端页面
```

---

## 允许的后端技术栈

### 核心框架
- **Spring Boot**: 2.4.4（固定版本，不允许升级）
- **Java**: 8（固定版本）
- **构建工具**: Apache Maven 3.6+

### 数据库与持久层
- **数据库**: MySQL 8.0+
- **ORM框架**: MyBatis 2.2.0
- **连接池**: HikariCP（Spring Boot内置）

### 安全框架
- **Spring Security**: Spring Boot 2.4.4内置版本

### Web框架
- **Spring MVC**: Spring Boot 2.4.4内置版本
- **Thymeleaf**: Spring Boot 2.4.4内置版本

### 监控与管理
- **Spring Actuator**: Spring Boot 2.4.4内置版本

### 数据验证
- **Spring Validation**: Spring Boot 2.4.4内置版本（Hibernate Validator）

### JSON处理
- **Jackson**: Spring Boot 2.4.4内置版本
- **Google Gson**: 2.8.9
- **Alibaba FastJSON**: 1.2.83

### 工具库
- **Lombok**: 1.18.32
- **Google Guava**: 30.1.1-jre
- **Apache Commons Lang3**: Spring Boot 2.4.4内置版本
- **Commons IO**: 2.11.0
- **Commons FileUpload**: 1.4

### 文档处理
- **Apache POI**: 5.1.0（Excel处理）
- **POI-TL**: 1.12.0（Word模板处理）
- **Apache PDFBox**: 2.0.20（PDF处理）

### 时间处理
- **Joda-Time**: 2.10.10

### 图表生成
- **JFreeChart**: 1.5.3

### Web爬虫
- **Jsoup**: 1.15.4

### 计算机视觉
- **OpenCV Java**: 4.5.1-2（org.openpnp:opencv）

### 浏览器自动化
- **Selenium**: 4.15.0

### AI服务
- **阿里云NLS SDK**: 4.6.4（com.aliyun:aliyun-java-sdk-core）

### 测试框架
- **Spring Boot Test**: Spring Boot 2.4.4内置版本
- **JUnit**: Spring Boot 2.4.4内置版本

---

## 允许的前端技术栈

### UI框架
- **Bootstrap**: 5.1.3（仅限CSS和JavaScript，不包含其他Bootstrap相关库）

### 图标库
- **Bootstrap Icons**: 当前版本（仅限字体图标，不包含SVG或React组件）

### JavaScript
- **原生JavaScript (ES5/ES6)**: 不允许使用任何JavaScript框架
  - 禁止使用: Vue.js, React, Angular, jQuery等
  - 允许使用: 原生JavaScript、ES6语法、Fetch API

### CSS
- **原生CSS3**: 允许使用CSS3特性（Grid、Flexbox、变量等）
- **Bootstrap 5.1.3 CSS**: 允许使用Bootstrap提供的CSS类

### 前端技术实现规范

#### CSS样式规范
- 使用CSS变量管理主题色
- 使用Flexbox和Grid布局
- 响应式设计必须支持移动端
- 禁止使用CSS预处理器（Sass、Less等）

#### JavaScript编码规范
- 使用ES6+语法
- 遵循模块化思想（虽然不使用框架）
- 异步操作使用Promise或async/await
- 禁止使用eval()和Function构造函数

---

## 数据库设计与优化

### 数据库设计原则

#### 1. 表结构设计
- **三表分离原则**: 基本信息、详细信息、标签信息独立存储
  - `customer` - 客户基本信息表
  - `customer_detail` - 客户详细信息表
  - `customer_tag` - 客户标签表

#### 2. 表结构优势
- **性能优化**: 基本信息查询更快，减少不必要字段加载
- **维护性**: 表结构清晰，职责分离
- **扩展性**: 便于后续添加新的信息类型
- **数据完整性**: 通过外键关联保证数据一致性

### 数据库字段规范

#### 基础字段约定
每个表必须包含以下基础字段：
- `id` - BIGINT AUTO_INCREMENT PRIMARY KEY
- `create_time` - DATETIME（创建时间）
- `update_time` - DATETIME（更新时间）
- `create_by` - VARCHAR（创建人）
- `update_by` - VARCHAR（更新人）
- `deleted` - TINYINT DEFAULT 0（逻辑删除标志）
- `version` - INT DEFAULT 0（乐观锁版本号）

### 索引设计规范

#### 必须建立索引的字段
- 主键：`id`
- 外键：所有关联表的关联字段
- 查询条件字段：频繁用于WHERE条件的字段
- 排序字段：频繁用于ORDER BY的字段

#### 索引命名规范
- 主键索引：`PRIMARY`
- 唯一索引：`uk_[表名]_[字段名]`
- 普通索引：`idx_[表名]_[字段名]`
- 组合索引：`idx_[表名]_[字段1]_[字段2]`

### 数据库迁移规范

#### 迁移脚本位置
- 迁移脚本存放在：`src/main/resources/sql/`
- 命名规范：`migration_[功能描述].sql`

#### 迁移执行流程
1. 备份数据库
2. 在测试环境执行迁移脚本
3. 验证迁移结果
4. 在生产环境业务低峰期执行
5. 记录迁移日志

#### 回滚准备
- 每个迁移脚本必须包含对应的回滚SQL
- 回滚脚本命名：`rollback_[功能描述].sql`

### 植物新品种保护申请字段迁移

#### 迁移内容
新增以下字段到 `customer` 表：

**基本信息字段**
- `contact_person` VARCHAR(100) - 联系人
- `postal_code` VARCHAR(10) - 邮政编码
- `fax` VARCHAR(20) - 传真

**申请人信息字段**
- `organization_code` VARCHAR(50) - 机构代码或身份证号码
- `nationality` VARCHAR(50) - 国籍或所在国（地区）
- `applicant_nature` TINYINT - 申请人性质（1:个人,2:企业,3:科研院所,4:其他）

**代理机构信息字段**
- `agency_name` VARCHAR(200) - 代理机构名称
- `agency_code` VARCHAR(50) - 代理机构组织机构代码
- `agency_address` VARCHAR(500) - 代理机构地址
- `agency_postal_code` VARCHAR(10) - 代理机构邮政编码

**代理人信息字段**
- `agent_name` VARCHAR(100) - 代理人姓名
- `agent_phone` VARCHAR(20) - 代理人电话
- `agent_fax` VARCHAR(20) - 代理人传真
- `agent_mobile` VARCHAR(20) - 代理人手机
- `agent_email` VARCHAR(100) - 代理人邮箱

#### 迁移SQL示例

```sql
-- 添加联系人字段
ALTER TABLE customer ADD COLUMN contact_person VARCHAR(100) COMMENT '联系人' AFTER customer_name;

-- 添加植物新品种保护申请相关字段
ALTER TABLE customer ADD COLUMN postal_code VARCHAR(10) COMMENT '邮政编码' AFTER address;
ALTER TABLE customer ADD COLUMN fax VARCHAR(20) COMMENT '传真' AFTER postal_code;
ALTER TABLE customer ADD COLUMN organization_code VARCHAR(50) COMMENT '机构代码或身份证号码' AFTER fax;
ALTER TABLE customer ADD COLUMN nationality VARCHAR(50) COMMENT '国籍或所在国（地区）' AFTER organization_code;
ALTER TABLE customer ADD COLUMN applicant_nature TINYINT COMMENT '申请人性质(1:个人,2:企业,3:科研院所,4:其他)' AFTER nationality;

-- 添加代理机构信息字段
ALTER TABLE customer ADD COLUMN agency_name VARCHAR(200) COMMENT '代理机构名称' AFTER applicant_nature;
ALTER TABLE customer ADD COLUMN agency_code VARCHAR(50) COMMENT '代理机构组织机构代码' AFTER agency_name;
ALTER TABLE customer ADD COLUMN agency_address VARCHAR(500) COMMENT '代理机构地址' AFTER agency_code;
ALTER TABLE customer ADD COLUMN agency_postal_code VARCHAR(10) COMMENT '代理机构邮政编码' AFTER agency_address;

-- 添加代理人信息字段
ALTER TABLE customer ADD COLUMN agent_name VARCHAR(100) COMMENT '代理人姓名' AFTER agency_postal_code;
ALTER TABLE customer ADD COLUMN agent_phone VARCHAR(20) COMMENT '代理人电话' AFTER agent_name;
ALTER TABLE customer ADD COLUMN agent_fax VARCHAR(20) COMMENT '代理人传真' AFTER agent_phone;
ALTER TABLE customer ADD COLUMN agent_mobile VARCHAR(20) COMMENT '代理人手机' AFTER agent_fax;
ALTER TABLE customer ADD COLUMN agent_email VARCHAR(100) COMMENT '代理人邮箱' AFTER agent_mobile;

-- 添加索引
ALTER TABLE customer ADD INDEX idx_contact_person (contact_person);
ALTER TABLE customer ADD INDEX idx_applicant_nature (applicant_nature);
ALTER TABLE customer ADD INDEX idx_agency_name (agency_name);
ALTER TABLE customer ADD INDEX idx_agent_name (agent_name);
```

---

## 构建与部署约束

### 构建工具
- **仅允许**: Apache Maven 3.6+
- **不允许**:
  - Gradle
  - Ant
  - 其他构建工具

### 打包方式
- **仅允许**: JAR包
- **不允许**:
  - WAR包
  - EAR包

### 部署方式
- **传统部署**: 直接运行JAR包
- **不允许**:
  - Docker容器化部署
  - Kubernetes部署
  - 其他容器化技术

### 环境要求
- **JDK**: 8+
- **Maven**: 3.6+
- **MySQL**: 8.0+

---

## 禁止的脚本和工具

### 脚本类型
- Docker相关脚本（Dockerfile, docker-compose.yml等）
- Kubernetes配置文件
- CI/CD配置文件（如Jenkinsfile、GitHub Actions等）
- 任何自动化部署脚本（除基本启动脚本外）

### 允许的脚本
- 基本启动脚本（start.sh, start.bat）- 仅用于本地开发启动
- 数据库初始化脚本（SQL脚本）

---

## 版本约束原则

### 版本固定规则
1. **Spring Boot版本**: 固定在2.4.4，不允许升级或降级
2. **Java版本**: 固定在8，不允许升级
3. **依赖版本**: 遵循以下原则
   - Spring Boot管理的依赖：使用Spring Boot Parent提供的版本
   - 第三方依赖：使用pom.xml中明确指定的版本
   - 禁止随意升级依赖版本

### 版本变更流程
- 如需升级任何依赖版本，必须先更新本文档并获得批准
- 禁止在生产代码中直接修改依赖版本

---

## 技术引入约束

### 新增技术审批流程
1. 如需引入任何新框架、库或工具，必须先：
   - 明确说明引入理由
   - 评估对现有架构的影响
   - 更新本文档
   - 获得项目负责人批准

### 默认禁止
- **默认禁止**引入任何本文档未明确列出的技术
- 未经批准的技术引入将被视为违反架构约束

---

## 性能优化策略

### 数据库优化
- **索引优化**: 关键字段建立索引提升查询性能
- **查询优化**: 避免全表扫描，使用分页查询
- **连接池配置**: HikariCP连接池参数调优
- **分页查询**: 大数据量查询必须使用分页

### 应用优化
- **缓存策略**: 合理使用缓存减少数据库访问
- **异步处理**: 耗时操作使用异步处理
- **连接池调优**: 根据实际负载调整连接池参数
- **JVM参数优化**: 根据服务器配置优化JVM参数

### 前端优化
- **资源压缩**: CSS和JavaScript文件压缩
- **缓存策略**: 合理设置浏览器缓存
- **懒加载**: 图片和内容懒加载
- **资源优化**: 减少HTTP请求数量

---

## 安全策略

### 数据安全
- **数据加密**: 敏感数据加密存储
- **SQL注入防护**: 使用参数化查询，禁止字符串拼接SQL
- **XSS防护**: 前端输入输出转义
- **CSRF防护**: Spring Security CSRF保护

### 访问控制
- **用户认证**: Spring Security用户认证
- **权限控制**: 基于角色的权限管理
- **会话管理**: 安全的会话管理机制
- **审计日志**: 关键操作记录审计日志

---

## 监控和运维

### 应用监控
- **Spring Actuator**: 应用健康检查和监控
- **健康检查**: `/actuator/health` 端点
- **性能指标**: `/actuator/metrics` 端点
- **日志监控**: 完整的日志记录和监控

### 数据库监控
- **连接池监控**: HikariCP连接池监控
- **查询性能**: 慢查询分析和优化
- **空间使用**: 数据库空间使用监控

---

## 技术栈清单总结

### 后端技术（白名单）
```
Spring Boot 2.4.4
Java 8
Maven 3.6+
MySQL 8.0+
MyBatis 2.2.0
Spring Security (内置)
Spring Actuator (内置)
Thymeleaf (内置)
Jackson (内置)
Lombok 1.18.32
Google Gson 2.8.9
Alibaba FastJSON 1.2.83
Google Guava 30.1.1-jre
Apache Commons (Lang3, IO, FileUpload)
Apache POI 5.1.0
POI-TL 1.12.0
Apache PDFBox 2.0.20
Joda-Time 2.10.10
JFreeChart 1.5.3
Jsoup 1.15.4
OpenCV Java 4.5.1-2
Selenium 4.15.0
阿里云NLS SDK 4.6.4
```

### 前端技术（白名单）
```
Bootstrap 5.1.3 (CSS + JS)
Bootstrap Icons (字体图标)
原生JavaScript (ES5/ES6)
原生CSS3
```

### 构建与工具（白名单）
```
Apache Maven 3.6+
```

---

## 违规处理

违反本文档规定的技术约束将导致：
1. 代码审查不通过
2. 要求立即移除违规技术
3. 可能影响项目进度和质量评估

---

## 重要提醒

1. **本文档是技术选型的唯一依据**
2. **任何技术变更必须先更新本文档**
3. **禁止在代码中引入本文档未明确允许的技术**
4. **定期审查依赖，确保符合约束**

---

**本约束文档具有最高优先级，所有开发人员必须严格遵守！**
