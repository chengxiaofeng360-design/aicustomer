#!/bin/bash

# Configuration
SOURCE_DIR="/Users/zuozuo/Downloads/cxf/aicustomer"
TARGET_DIR="${SOURCE_DIR}/migration_package"

# Create directories
echo "Creating directory structure..."
mkdir -p "${TARGET_DIR}/backend/src/main/java/com/aicustomer/controller"
mkdir -p "${TARGET_DIR}/backend/src/main/java/com/aicustomer/service/impl"
mkdir -p "${TARGET_DIR}/backend/src/main/java/com/aicustomer/entity"
mkdir -p "${TARGET_DIR}/backend/src/main/java/com/aicustomer/mapper"
mkdir -p "${TARGET_DIR}/backend/src/main/java/com/aicustomer/config"
mkdir -p "${TARGET_DIR}/backend/src/main/resources/mapper"
mkdir -p "${TARGET_DIR}/frontend/js"
mkdir -p "${TARGET_DIR}/frontend/css"
mkdir -p "${TARGET_DIR}/frontend/lib"
mkdir -p "${TARGET_DIR}/database"
mkdir -p "${TARGET_DIR}/embedding-service"

# Copy Java Files
echo "Copying Java files..."
cp "${SOURCE_DIR}/src/main/java/com/aicustomer/controller/AiChatController.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/controller/"
cp "${SOURCE_DIR}/src/main/java/com/aicustomer/controller/KnowledgeDocumentController.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/controller/"
cp "${SOURCE_DIR}/src/main/java/com/aicustomer/controller/FaqQaController.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/controller/"

cp "${SOURCE_DIR}/src/main/java/com/aicustomer/service/AiChatService.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/service/"
cp "${SOURCE_DIR}/src/main/java/com/aicustomer/service/KnowledgeDocumentService.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/service/"
cp "${SOURCE_DIR}/src/main/java/com/aicustomer/service/VectorSearchService.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/service/"
cp "${SOURCE_DIR}/src/main/java/com/aicustomer/service/DeepSeekService.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/service/"
cp "${SOURCE_DIR}/src/main/java/com/aicustomer/service/FaqQaService.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/service/"

cp "${SOURCE_DIR}/src/main/java/com/aicustomer/service/impl/AiChatServiceImpl.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/service/impl/"
cp "${SOURCE_DIR}/src/main/java/com/aicustomer/service/impl/KnowledgeDocumentServiceImpl.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/service/impl/"
cp "${SOURCE_DIR}/src/main/java/com/aicustomer/service/impl/VectorSearchServiceImpl.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/service/impl/"
cp "${SOURCE_DIR}/src/main/java/com/aicustomer/service/impl/FaqQaServiceImpl.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/service/impl/"

cp "${SOURCE_DIR}/src/main/java/com/aicustomer/entity/AiChat.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/entity/"
cp "${SOURCE_DIR}/src/main/java/com/aicustomer/entity/KnowledgeDocument.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/entity/"
cp "${SOURCE_DIR}/src/main/java/com/aicustomer/entity/FaqQa.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/entity/"

cp "${SOURCE_DIR}/src/main/java/com/aicustomer/mapper/AiChatMapper.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/mapper/"
cp "${SOURCE_DIR}/src/main/java/com/aicustomer/mapper/KnowledgeDocumentMapper.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/mapper/"
cp "${SOURCE_DIR}/src/main/java/com/aicustomer/mapper/FaqQaMapper.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/mapper/"

cp "${SOURCE_DIR}/src/main/java/com/aicustomer/config/DeepSeekConfig.java" "${TARGET_DIR}/backend/src/main/java/com/aicustomer/config/"

# Copy Resource Files
echo "Copying Resource files..."
cp "${SOURCE_DIR}/src/main/resources/mapper/AiChatMapper.xml" "${TARGET_DIR}/backend/src/main/resources/mapper/"
cp "${SOURCE_DIR}/src/main/resources/mapper/KnowledgeDocumentMapper.xml" "${TARGET_DIR}/backend/src/main/resources/mapper/"
cp "${SOURCE_DIR}/src/main/resources/mapper/FaqQaMapper.xml" "${TARGET_DIR}/backend/src/main/resources/mapper/"

# Copy Frontend Files
echo "Copying Frontend files..."
cp "${SOURCE_DIR}/src/main/resources/static/ai-chat.html" "${TARGET_DIR}/frontend/"
cp "${SOURCE_DIR}/src/main/resources/static/js/ai-chat.js" "${TARGET_DIR}/frontend/js/"
cp "${SOURCE_DIR}/src/main/resources/static/css/ai-chat.css" "${TARGET_DIR}/frontend/css/"
cp "${SOURCE_DIR}/src/main/resources/static/css/common.css" "${TARGET_DIR}/frontend/css/"
cp -r "${SOURCE_DIR}/src/main/resources/static/lib/"* "${TARGET_DIR}/frontend/lib/"

# Copy Embedding Service
echo "Copying Embedding service..."
cp -r "${SOURCE_DIR}/embedding-service/"* "${TARGET_DIR}/embedding-service/"

# Create Database SQL
echo "Creating SQL schema..."
cat > "${TARGET_DIR}/database/schema.sql" << 'EOF'
-- 1. AI Chat History Table
CREATE TABLE IF NOT EXISTS ai_chat (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT COMMENT '用户ID',
    user_name VARCHAR(100) COMMENT '用户姓名',
    customer_id BIGINT COMMENT '关联客户ID',
    customer_name VARCHAR(100) COMMENT '关联客户姓名',
    message_type TINYINT NOT NULL COMMENT '消息类型(1:用户消息,2:AI回复,3:系统消息)',
    content TEXT COMMENT '消息内容',
    reply_content TEXT COMMENT 'AI回复内容',
    reply_time DATETIME COMMENT '回复时间',
    session_id VARCHAR(100) NOT NULL COMMENT '会话ID',
    context TEXT COMMENT '对话上下文',
    intent VARCHAR(200) COMMENT '意图识别结果',
    confidence DECIMAL(5,2) COMMENT '置信度',
    sentiment TINYINT COMMENT '情感分析结果(1:积极,2:中性,3:消极)',
    keywords VARCHAR(500) COMMENT '关键词',
    is_satisfied TINYINT COMMENT '是否满意(0:否,1:是)',
    satisfaction_score TINYINT COMMENT '满意度评分',
    feedback TEXT COMMENT '用户反馈',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI聊天记录表';

-- 2. Knowledge Document Table
CREATE TABLE IF NOT EXISTS knowledge_document (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(255) NOT NULL COMMENT '文档标题',
    content LONGTEXT COMMENT '文档内容',
    file_name VARCHAR(255) COMMENT '文件名',
    file_type VARCHAR(50) COMMENT '文件类型(pdf/word/excel/txt)',
    file_size BIGINT COMMENT '文件大小(字节)',
    file_path VARCHAR(500) COMMENT '文件路径',
    document_type VARCHAR(50) COMMENT '文档类型',
    tags VARCHAR(500) COMMENT '标签(逗号分隔)',
    category VARCHAR(100) COMMENT '分类',
    summary TEXT COMMENT '摘要',
    keywords VARCHAR(500) COMMENT '关键词',
    view_count INT DEFAULT 0 COMMENT '查看次数',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    status TINYINT DEFAULT 1 COMMENT '状态(1:启用 0:禁用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_by VARCHAR(50),
    deleted TINYINT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    INDEX idx_title (title),
    INDEX idx_category (category),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库文档表';

-- 3. FAQ Table
CREATE TABLE IF NOT EXISTS faq_qa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    question TEXT NOT NULL COMMENT '问题',
    answer TEXT NOT NULL COMMENT '答案',
    keywords VARCHAR(500) COMMENT '关键词',
    category VARCHAR(100) COMMENT '分类',
    priority INT DEFAULT 0 COMMENT '优先级',
    hit_count INT DEFAULT 0 COMMENT '命中次数',
    status TINYINT DEFAULT 1 COMMENT '状态(1:启用 0:禁用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_by VARCHAR(50),
    deleted TINYINT NOT NULL DEFAULT 0,
    FULLTEXT INDEX idx_question (question)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FAQ问答表';
EOF

# Create README
echo "Creating README..."
cat > "${TARGET_DIR}/README.txt" << 'EOF'
# AI Chat & Knowledge Base Migration Package

This package contains all the necessary files to migrate the AI Chat and Knowledge Base functionality.

## Folder Structure

- **backend/**: Contains Java source code.
    - `src/main/java`: Backend Controllers, Services, Entities, Mappers, and Configs.
    - `src/main/resources`: Mapper XML files.
- **frontend/**: Contains Frontend assets.
    - `ai-chat.html`: Main chat interface.
    - `js/`, `css/`: Scripts and styles.
    - `lib/`: Third-party libraries (Bootstrap, etc.).
- **database/**: Contains `schema.sql` to create necessary tables.
- **embedding-service/**: Python-based vector embedding service.

## Instructions

1. **Database**: Run `database/schema.sql` in your MySQL database.
2. **Backend**:
   - Copy `backend/src` content to your Spring Boot project.
   - Add necessary dependencies to your `pom.xml` (see implementation_plan.md).
   - Configure `application.yml` with your DeepSeek API key and DB connection.
3. **Frontend**:
   - Copy `frontend` content to your web server's static directory.
4. **Embedding Service**:
   - Run `pip install -r embedding-service/requirements.txt`.
   - Start with `python embedding-service/app.py`.

For more detailed steps, refer to the provided `implementation_plan.md` artifact from the AI interaction.
EOF

echo "Migration package created successfully at: ${TARGET_DIR}"
