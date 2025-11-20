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
- **Spring Boot**: 3.2.x（固定版本，不允许升级或降级）
- **Spring AI**: 1.0.0-M3
- **Java**: Temurin JDK 17（固定版本，不允许降级）
- **构建工具**: Apache Maven 3.9.6（固定版本）

### 数据库与持久层
- **数据库**: MySQL 8.0+
- **ORM框架**: MyBatis 3.0.3
- **连接池**: HikariCP

### 安全框架
- **Spring Security**: 基于 `SecurityFilterChain` 配置

### Web框架
- **Thymeleaf**: 3.1.x

### 监控与管理
- **Spring Actuator**: 包含 Micrometer 指标

### 数据验证
- **Spring Validation**: Jakarta Validation 3.x / Hibernate Validator 8.x

### JSON处理
- **Jackson**: Jakarta JSON 支持
- **Google Gson**: 2.10.1
- **Alibaba FastJSON**: 2.0.43

### 工具库
- **Lombok**: 1.18.32
- **Google Guava**: 32.1.3-jre
- **Commons IO**: 2.11.0
- **Commons FileUpload**: 1.4

### 文档处理
- **Apache POI**: 5.2.5
- **POI-TL**: 1.12.0
- **Apache PDFBox**: 2.0.29

### 时间处理
- **Joda-Time**: 2.12.5

### 图表生成
- **JFreeChart**: 1.5.3

### Web爬虫
- **Jsoup**: 1.17.2

### 计算机视觉
- **OpenCV Java**: 4.5.1-2

### 浏览器自动化
- **Selenium**: 4.15.0

### AI服务
- **阿里云NLS SDK**: 4.6.4
- **OpenAI Java SDK**: 0.18.0
- **DeepSeek API**: 通过OpenAI兼容接口调用，模型：deepseek-chat
- **字节跳动火山引擎·豆包**: 通过自研 `Spring AI` 适配层 `doubaoChatModel` 调用 `doubao-seed-1-6-251015`

### 测试框架
- **JUnit**: JUnit 5.10.x

---

## 允许的前端技术栈

### UI框架
- **Bootstrap**: 5.1.3

### 图标库
- **Bootstrap Icons**: 字体图标

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

#### 模态框（Modal）规范
- **最大宽度限制**: 所有模态框的最大宽度不得超过窗口宽度的70%
- **响应式处理**: 在小屏幕设备（宽度≤768px）上，模态框最大宽度可调整为95%
- **实现方式**: 使用自定义CSS类 `.modal-dialog-max-70` 替代Bootstrap默认的 `modal-xl`、`modal-lg` 等类
- **适用场景**: 客户详情、客户编辑、沟通记录等所有模态框

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

#### 统一脚本策略
- **唯一入口**：所有建库、建表、迁移、回滚与初始化数据统一维护在 `src/main/resources/sql/init.sql`
- **分节管理**：每个变更必须以 `-- [模块名称]` 形式标记，便于审查与回滚
- **幂等执行**：DDL/DML 必须可重复执行（`IF EXISTS / IF NOT EXISTS` 或幂等更新语句）

#### 执行流程
1. 在 `init.sql` 中新增/调整对应区块，并在文件顶部追加本次变更说明
2. 本地或测试库整份执行 `init.sql`，确认无错误
3. 评审通过后合入主干，生产环境仅执行该文件

#### 回滚要求
- 回滚语句与正向语句成对记录并使用 `-- ROLLBACK` 标注
- 禁止新增零散 `.sql` 文件，历史脚本已全部并入 `init.sql`


## 构建与部署约束

### 构建工具
- **仅允许**: Apache Maven 3.9.6（统一安装于系统级 `~/opt/apache-maven-3.9.6`）
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
- **JDK**: Temurin 17（必须）
- **Maven**: 3.9.6（由 `setup_maven.sh` 安装并配置）
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
1. **Spring Boot版本**: 固定在 3.2.x，未经审批不得升级或降级
2. **Java版本**: 固定在 Temurin JDK 17，不允许降级
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
Spring Boot 3.2.x
Spring AI 1.0.0-M3
Temurin JDK 17
Apache Maven 3.9.6
MySQL 8.0+
MyBatis 3.0.3
Spring Security / Spring MVC / Spring Actuator / Jackson
Thymeleaf 3.1.x
Lombok 1.18.32
Google Gson 2.10.1
Alibaba FastJSON 2.0.43
Google Guava 32.1.3-jre
Apache Commons (Lang3, IO, FileUpload)
Apache POI 5.2.5
POI-TL 1.12.0
Apache PDFBox 2.0.29
Joda-Time 2.12.5
JFreeChart 1.5.3
Jsoup 1.17.2
OpenCV Java 4.5.1-2
Selenium 4.15.0
阿里云NLS SDK 4.6.4
OpenAI Java SDK 0.18.0 / Doubao 模型（Spring AI）
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
Apache Maven 3.9.6
setup_maven.sh（统一安装脚本）
```

---

## 违规处理

违反本文档规定的技术约束将导致：
1. 代码审查不通过
2. 要求立即移除违规技术
3. 可能影响项目进度和质量评估

---

## 重要提醒a

1. **本文档是技术选型的唯一依据**
2. **任何技术变更必须先更新本文档**
3. **禁止在代码中引入本文档未明确允许的技术**
4. **定期审查依赖，确保符合约束**

---

**本约束文档具有最高优先级，所有开发人员必须严格遵守！**
