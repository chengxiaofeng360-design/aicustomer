# 知识管理系统导出包 - 使用说明

## 📦 导出包内容

本导出包包含完整的 **知识管理系统** 功能模块，可以直接集成到 Spring Boot 项目中。

### 包含模块
1. **知识库管理** (`KnowledgeDocument`) - 文档上传、管理、全文搜索
2. **FAQ问答管理** (`FaqQa`) - 常见问题库、问答管理
3. **系统管理界面** - 统一的管理入口页面

## 📂 目录结构

```
knowledge_management_system/
│
├── README.md                          ← 📖 本说明文档
├── 快速开始.md                         ← 🚀 集成指南
├── pom.xml依赖说明.md                  ← 📋 Maven依赖
├── application.yml配置说明.md          ← ⚙️ 配置说明
│
└── src/
    └── main/
        ├── java/com/aicustomer/
        │   ├── common/                  # 通用类 (Result)
        │   ├── controller/              # 控制器 (KnowledgeDocumentController, FaqQaController)
        │   ├── entity/                  # 实体类 (KnowledgeDocument, FaqQa)
        │   ├── mapper/                  # Mapper接口 (KnowledgeDocumentMapper, FaqQaMapper)
        │   └── service/                 # 服务层 (接口与实现)
        │
        └── resources/
            ├── mapper/                  # SQL映射文件 (*.xml)
            ├── sql/                     # 数据库脚本 (schema.sql)
            └── static/                  # 前端页面
                ├── system-management.html       # 系统管理主页 (入口)
                ├── knowledge-base.html          # 知识库独立页
                ├── knowledge-base-iframe.html   # 知识库内嵌页
                └── faq-management-iframe.html   # FAQ管理内嵌页
```

## 🚀 集成步骤

### 1. 复制代码
将 `src` 目录下的所有文件复制到你的项目中。

### 2. 添加依赖
参考 `pom.xml依赖说明.md` 添加 Maven 依赖。

### 3. 初始化数据库
运行 `src/main/resources/sql/schema.sql` 中的 SQL 脚本创建表：
- `knowledge_document` 表
- `faq_qa` 表

### 4. 配置文件
参考 `application.yml配置说明.md` 配置数据库和文件上传路径。

### 5. 访问系统
启动项目后，访问：
`http://localhost:8080/system-management.html`

## 🌟 功能特性

### 📚 知识库管理
- 支持 PDF/Word/TXT 文件上传与自动解析
- 多维度分类管理
- 全文检索
- 阅读与下载统计

### 💬 FAQ问答管理
- 常见问题增删改查
- 问题分类管理
- 关键词匹配
- 优先级排序

## 🔧 技术栈
- Spring Boot 3.x
- MyBatis
- MySQL 5.7+
- Bootstrap 5
