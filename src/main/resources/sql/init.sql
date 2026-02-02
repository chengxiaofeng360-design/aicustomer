-- AI客户管理系统数据库初始化脚本
-- 这个脚本定义了系统的核心数据结构
-- 包含：客户管理、系统权限、沟通记录、任务协作、知识库等模块
-- 详细注释了唯一约束 (UNIQUE) 和外键关系 (Foreign Key Logic)

-- 创建数据库
CREATE DATABASE IF NOT EXISTS zqgl CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE zqgl;

-- ==========================================
-- 1. 客户管理模块 (Customer Management)
-- ==========================================

-- 客户基本信息表
-- 核心实体表，存储客户的基础档案
CREATE TABLE IF NOT EXISTS customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    
    -- 【唯一约束】客户编号，系统内唯一标识
    customer_code VARCHAR(50) NOT NULL UNIQUE COMMENT '客户编号 (UNIQUE: 业务唯一标识)',
    
    customer_name VARCHAR(100) NOT NULL COMMENT '客户姓名/企业名称',
    contact_person VARCHAR(100) COMMENT '联系人',
    
    -- 枚举值说明
    customer_type TINYINT NOT NULL DEFAULT 1 COMMENT '客户类型(1:个人客户, 2:企业客户)',
    
    phone VARCHAR(20) COMMENT '手机号码',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(500) COMMENT '地址',
    
    -- 植物新品种保护申请相关字段 (业务特定)
    postal_code VARCHAR(10) COMMENT '邮政编码',
    fax VARCHAR(20) COMMENT '传真',
    organization_code VARCHAR(50) COMMENT '机构代码或身份证号码',
    nationality VARCHAR(50) COMMENT '国籍或所在国（地区）',
    
    -- 枚举值说明
    applicant_nature TINYINT COMMENT '申请人性质(1:个人, 2:企业, 3:科研院所, 4:其他)',
    
    -- 联系方式与业务扩展
    position VARCHAR(100) COMMENT '职务',
    qq_weixin VARCHAR(100) COMMENT 'QQ/微信',
    cooperation_content TEXT COMMENT '合作内容',
    region VARCHAR(50) COMMENT '地区',
    
    -- 代理机构信息 (用于三方代理业务)
    agency_name VARCHAR(200) COMMENT '代理机构名称',
    agency_code VARCHAR(50) COMMENT '代理机构组织机构代码',
    agency_address VARCHAR(500) COMMENT '代理机构地址',
    agency_postal_code VARCHAR(10) COMMENT '代理机构邮政编码',
    
    -- 代理人信息
    agent_name VARCHAR(100) COMMENT '代理人姓名',
    agent_phone VARCHAR(20) COMMENT '代理人电话',
    agent_fax VARCHAR(20) COMMENT '代理人传真',
    agent_mobile VARCHAR(20) COMMENT '代理人手机',
    agent_email VARCHAR(100) COMMENT '代理人邮箱',
    
    -- 数据保护
    is_sensitive TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否为敏感数据 (1:是, 0:否)',
    protection_password VARCHAR(255) COMMENT '保护密码(加密后存储, 用于查看敏感数据)',

    -- 业务状态流转
    progress TINYINT DEFAULT 0 COMMENT '进度(0:未开始, 1:进行中, 2:暂停中, 3:已成功, 4:放弃)',
    
    -- 核心业务分类
    business_type TINYINT DEFAULT 1 COMMENT '具体业务类型(1:品种权申请, 2:品种权转化, 3:知识产权协作, 4:科普教育, 5:景观设计, 6:图书出版)',
    
    -- 客户分级
    customer_level TINYINT NOT NULL DEFAULT 1 COMMENT '客户等级(1:普通, 2:VIP, 3:钻石)',
    
    -- 生命周期状态
    status TINYINT NOT NULL DEFAULT 1 COMMENT '客户状态(1:正常, 2:冻结, 3:注销)',
    source TINYINT NOT NULL DEFAULT 1 COMMENT '客户来源(1:线上, 2:线下, 3:推荐)',
    
    -- 【关联关系】负责业务员
    assigned_user_id BIGINT COMMENT '负责业务员ID (关联 sys_user.id)',
    assigned_user_name VARCHAR(100) COMMENT '负责业务员姓名 (冗余字段, 避免联表)',
    
    last_contact_time DATETIME COMMENT '最后联系时间',
    remark TEXT COMMENT '备注',
    
    -- 审计字段
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除, 1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号(乐观锁)',
    
    -- 索引定义
    INDEX idx_customer_code (customer_code),
    INDEX idx_customer_name (customer_name),
    INDEX idx_contact_person (contact_person),
    INDEX idx_phone (phone),
    INDEX idx_customer_type (customer_type),
    INDEX idx_applicant_nature (applicant_nature),
    INDEX idx_agency_name (agency_name),
    INDEX idx_agent_name (agent_name),
    INDEX idx_position (position),
    INDEX idx_region (region),
    INDEX idx_customer_level (customer_level),
    INDEX idx_status (status),
    INDEX idx_source (source),
    INDEX idx_assigned_user_id (assigned_user_id),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户基本信息表';

-- 客户详细信息表
-- 扩展表，存储分析类、统计类、长文本类数据，通过 ID 1:1 关联
CREATE TABLE IF NOT EXISTS customer_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    
    -- 【关联关系】关联主表
    customer_id BIGINT NOT NULL COMMENT '客户ID (关联 customer.id)',
    
    customer_code VARCHAR(50) NOT NULL COMMENT '客户编号 (冗余字段)',
    
    -- 业务信息
    industry_category VARCHAR(100) COMMENT '行业分类',
    business_type VARCHAR(100) COMMENT '业务类型(文本描述)',
    importance TINYINT DEFAULT 1 COMMENT '重要程度(1:一般, 2:重要, 3:非常重要)',
    
    -- 价值分析维度
    value_score INT DEFAULT 0 COMMENT '客户价值评分(0-100分)',
    lifecycle_stage TINYINT DEFAULT 1 COMMENT '生命周期阶段(1:潜在, 2:接触, 3:合作, 4:维护, 5:流失)',
    total_amount DECIMAL(15,2) DEFAULT 0.00 COMMENT '累计消费金额',
    cooperation_count INT DEFAULT 0 COMMENT '合作次数',
    credit_level TINYINT DEFAULT 1 COMMENT '信用评估等级(1:A, 2:B, 3:C, 4:D)',
    satisfaction_score TINYINT DEFAULT 0 COMMENT '客户满意度评分(1-5分)',
    completeness INT DEFAULT 0 COMMENT '信息完整度(0-100%)',
    
    -- 关键时间节点
    customer_create_time DATETIME COMMENT '客户创建时间',
    first_cooperation_time DATETIME COMMENT '首次合作时间',
    last_cooperation_time DATETIME COMMENT '最后合作时间',
    churn_time DATETIME COMMENT '客户流失时间',
    churn_reason VARCHAR(500) COMMENT '流失原因',
    
    -- 扩展画像信息
    company_size TINYINT COMMENT '公司规模(1:微型, 2:小型, 3:中型, 4:大型)',
    annual_revenue DECIMAL(15,2) COMMENT '年营业额',
    employee_count INT COMMENT '员工数量',
    primary_contact VARCHAR(100) COMMENT '主要联系人',
    decision_maker VARCHAR(100) COMMENT '决策人',
    purchase_cycle TINYINT COMMENT '采购周期(1:月度, 2:季度, 3:半年度, 4:年度)',
    budget_range VARCHAR(100) COMMENT '预算范围',
    competitors VARCHAR(500) COMMENT '竞争对手',
    special_requirements TEXT COMMENT '特殊要求',
    preferences TEXT COMMENT '客户偏好',
    risk_level TINYINT DEFAULT 1 COMMENT '风险等级(1:低, 2:中, 3:高)',
    risk_description TEXT COMMENT '风险描述',
    
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除, 1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    
    -- 【唯一约束】确保一对一关系
    UNIQUE KEY uk_customer_id (customer_id) COMMENT 'UNIQUE: 确保每个客户只有一条详情记录',
    
    INDEX idx_customer_code (customer_code),
    INDEX idx_importance (importance),
    INDEX idx_value_score (value_score),
    INDEX idx_lifecycle_stage (lifecycle_stage),
    INDEX idx_credit_level (credit_level),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户详细信息表';

-- 客户标签表
-- 关联表，多对多关系 (实体化为 1:N)
CREATE TABLE IF NOT EXISTS customer_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    
    -- 【关联关系】
    customer_id BIGINT NOT NULL COMMENT '客户ID (关联 customer.id)',
    
    tag_name VARCHAR(50) NOT NULL COMMENT '标签名称',
    tag_type TINYINT NOT NULL DEFAULT 2 COMMENT '标签类型(1:系统标签, 2:自定义标签, 3:AI标签)',
    tag_color VARCHAR(20) DEFAULT '#007bff' COMMENT '标签颜色 (Hex Code)',
    tag_description VARCHAR(200) COMMENT '标签描述',
    weight INT DEFAULT 0 COMMENT '标签权重 (用于排序或AI分析)',
    is_visible TINYINT DEFAULT 1 COMMENT '是否显示(0:隐藏, 1:显示)',
    
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除, 1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    
    INDEX idx_customer_id (customer_id),
    INDEX idx_tag_name (tag_name),
    INDEX idx_tag_type (tag_type),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户标签表';

-- ==========================================
-- 2. 系统权限模块 (RBAC)
-- ==========================================

-- 用户表（用于系统管理）
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    
    -- 【唯一约束】用户名不可重复
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名 (UNIQUE: 登录账号)',
    
    password VARCHAR(100) NOT NULL COMMENT '密码 (BCrypt加密)',
    real_name VARCHAR(100) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号码',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:正常, 2:禁用)',
    
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除, 1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    
    INDEX idx_username (username),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    
    -- 【唯一约束】
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称 (UNIQUE)',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码 (UNIQUE: 如 ADMIN, USER)',
    
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:正常, 2:禁用)',
    
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除, 1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    
    INDEX idx_role_code (role_code),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 用户角色关联表 (Many-to-Many)
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    
    -- 【关联关系】
    user_id BIGINT NOT NULL COMMENT '用户ID (关联 sys_user.id)',
    role_id BIGINT NOT NULL COMMENT '角色ID (关联 sys_role.id)',
    
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    
    -- 【唯一约束】防止重复授权
    UNIQUE KEY uk_user_role (user_id, role_id) COMMENT 'UNIQUE: 一个用户同一个角色只能有一条记录',
    
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 权限表 (菜单与按钮)
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    
    -- 【唯一约束】
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码 (UNIQUE: 如 user:list)',
    
    permission_type TINYINT NOT NULL DEFAULT 1 COMMENT '权限类型(1:菜单, 2:按钮, 3:接口)',
    
    -- 【自关联】
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID (关联 sys_permission.id)',
    
    path VARCHAR(200) COMMENT '前端路由路径',
    component VARCHAR(200) COMMENT '前端组件路径',
    icon VARCHAR(100) COMMENT '菜单图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:正常, 2:禁用)',
    
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除, 1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    
    INDEX idx_permission_code (permission_code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 角色权限关联表 (Many-to-Many)
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    
    -- 【关联关系】
    role_id BIGINT NOT NULL COMMENT '角色ID (关联 sys_role.id)',
    permission_id BIGINT NOT NULL COMMENT '权限ID (关联 sys_permission.id)',
    
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    
    -- 【唯一约束】
    UNIQUE KEY uk_role_permission (role_id, permission_id) COMMENT 'UNIQUE: 一个角色同一个权限只能有一条记录',
    
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ==========================================
-- 3. 沟通与交互模块 (Communication)
-- ==========================================

-- 沟通记录表
CREATE TABLE IF NOT EXISTS communication_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    
    -- 【关联关系】
    customer_id BIGINT NOT NULL COMMENT '客户ID (关联 customer.id)',
    customer_name VARCHAR(100) NOT NULL COMMENT '客户姓名 (冗余)',
    
    communication_type TINYINT NOT NULL COMMENT '沟通类型(1:微信, 2:电话, 3:邮件, 4:会面, 5:其他)',
    communication_time DATETIME NOT NULL COMMENT '沟通时间',
    content TEXT COMMENT '沟通内容 (详细记录)',
    summary VARCHAR(1000) COMMENT '沟通摘要 (AI生成或人工总结)',
    importance TINYINT DEFAULT 1 COMMENT '重要程度(1:一般, 2:重要, 3:非常重要)',
    sentiment TINYINT COMMENT '情感分析结果(1:积极, 2:中性, 3:消极)',
    satisfaction_score TINYINT COMMENT '满意度评分(1-5分)',
    keywords VARCHAR(500) COMMENT '关键词(逗号分隔)',
    important_info TEXT COMMENT '重要信息标记',
    
    -- 后续跟进计划
    follow_up_task VARCHAR(1000) COMMENT '后续任务',
    task_deadline DATETIME COMMENT '任务截止时间',
    task_status TINYINT DEFAULT 1 COMMENT '任务状态(1:待处理, 2:进行中, 3:已完成, 4:已取消)',
    
    -- 【关联关系】
    communicator_id BIGINT COMMENT '沟通人员ID (关联 sys_user.id)',
    communicator_name VARCHAR(100) COMMENT '沟通人员姓名 (冗余)',
    
    attachment_path VARCHAR(500) COMMENT '附件路径',
    channel_detail VARCHAR(200) COMMENT '沟通渠道详情 (如微信群名)',
    duration INT COMMENT '沟通时长(分钟)',
    response_time INT COMMENT '客户响应时间(分钟)',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读(0:未读, 1:已读)',
    is_processed TINYINT DEFAULT 0 COMMENT '是否已处理(0:未处理, 1:已处理)',
    process_result TEXT COMMENT '处理结果',
    process_time DATETIME COMMENT '处理时间',
    
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除, 1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    
    INDEX idx_customer_id (customer_id),
    INDEX idx_communication_type (communication_type),
    INDEX idx_communication_time (communication_time),
    INDEX idx_importance (importance),
    INDEX idx_communicator_id (communicator_id),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='沟通记录表';

-- AI 聊天记录表
-- 存储系统内 AI 助手的问答历史
CREATE TABLE IF NOT EXISTS ai_chat (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    
    -- 【关联关系】
    user_id BIGINT COMMENT '用户ID (关联 sys_user.id)',
    user_name VARCHAR(100) COMMENT '用户姓名',
    customer_id BIGINT COMMENT '关联客户ID (关联 customer.id)',
    customer_name VARCHAR(100) COMMENT '关联客户姓名',
    
    message_type TINYINT NOT NULL COMMENT '消息类型(1:用户消息, 2:AI回复, 3:系统消息)',
    content TEXT COMMENT '消息内容',
    reply_content TEXT COMMENT 'AI回复内容',
    reply_time DATETIME COMMENT '回复时间',
    session_id VARCHAR(100) NOT NULL COMMENT '会话ID (Dify或其他AI平台的Session标识)',
    context TEXT COMMENT '对话上下文 (JSON字符串)',
    intent VARCHAR(200) COMMENT '意图识别结果',
    confidence DECIMAL(5,2) COMMENT '置信度',
    sentiment TINYINT COMMENT '情感分析结果(1:积极, 2:中性, 3:消极)',
    keywords VARCHAR(500) COMMENT '关键词',
    is_satisfied TINYINT COMMENT '是否满意(0:否, 1:是)',
    satisfaction_score TINYINT COMMENT '满意度评分',
    feedback TEXT COMMENT '用户反馈',
    
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除, 1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_message_type (message_type),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI聊天记录表';

-- ==========================================
-- 4. 任务与提醒模块 (Task & Reminder)
-- ==========================================

-- 任务提醒表
CREATE TABLE IF NOT EXISTS task_reminder (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '提醒标题',
    content TEXT COMMENT '提醒内容',
    reminder_type TINYINT NOT NULL COMMENT '提醒类型(1:关键日期, 2:跟进提醒, 3:机会提醒, 4:风险预警)',
    
    -- 【关联关系】
    customer_id BIGINT COMMENT '关联客户ID',
    customer_name VARCHAR(100) COMMENT '关联客户姓名',
    
    reminder_time DATETIME NOT NULL COMMENT '提醒时间',
    deadline DATETIME COMMENT '截止时间',
    priority TINYINT DEFAULT 2 COMMENT '优先级(1:低, 2:中, 3:高, 4:紧急)',
    status TINYINT DEFAULT 1 COMMENT '提醒状态(1:待提醒, 2:已提醒, 3:已完成, 4:已取消, 5:已过期)',
    
    -- 【关联关系】
    assigned_user_id BIGINT COMMENT '负责人员ID (关联 sys_user.id)',
    assigned_user_name VARCHAR(100) COMMENT '负责人员姓名',
    
    reminder_method TINYINT DEFAULT 1 COMMENT '提醒方式(1:系统内, 2:邮件, 3:短信, 4:微信, 5:全部)',
    frequency TINYINT DEFAULT 1 COMMENT '提醒频率(1:一次性, 2:每日, 3:每周, 4:每月)',
    repeat_count INT DEFAULT 0 COMMENT '重复次数',
    repeated_count INT DEFAULT 0 COMMENT '已重复次数',
    next_reminder_time DATETIME COMMENT '下次提醒时间',
    completed_time DATETIME COMMENT '完成时间',
    completion_result TEXT COMMENT '完成结果',
    customer_response_time DATETIME COMMENT '客户响应时间',
    customer_response TEXT COMMENT '客户响应内容',
    effectiveness_score TINYINT COMMENT '提醒效果评分(1-5分)',
    
    -- 通用业务关联
    business_id BIGINT COMMENT '关联业务ID (如 team_task.id)',
    business_type VARCHAR(100) COMMENT '关联业务类型 (如 task)',
    rule_id BIGINT COMMENT '提醒规则ID',
    is_auto_generated TINYINT DEFAULT 0 COMMENT '是否自动生成(0:否, 1:是)',
    generated_from VARCHAR(100) COMMENT '生成来源',
    
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除, 1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    
    INDEX idx_customer_id (customer_id),
    INDEX idx_reminder_type (reminder_type),
    INDEX idx_reminder_time (reminder_time),
    INDEX idx_priority (priority),
    INDEX idx_status (status),
    INDEX idx_assigned_user_id (assigned_user_id),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务提醒表';

-- 团队任务表
CREATE TABLE IF NOT EXISTS team_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '任务标题',
    description TEXT COMMENT '任务描述',
    task_type TINYINT NOT NULL COMMENT '任务类型(1:客户跟进, 2:项目推进, 3:问题处理, 4:会议安排, 5:其他)',
    status TINYINT DEFAULT 1 COMMENT '任务状态(1:待分配, 2:进行中, 3:待审核, 4:已完成, 5:已取消)',
    priority TINYINT DEFAULT 2 COMMENT '优先级(1:低, 2:中, 3:高, 4:紧急)',
    
    -- 【关联关系】针对客户的任务
    customer_id BIGINT COMMENT '关联客户ID',
    customer_name VARCHAR(100) COMMENT '关联客户姓名',
    
    -- 【关联关系】创建人
    creator_id BIGINT NOT NULL COMMENT '创建人ID (关联 sys_user.id)',
    creator_name VARCHAR(100) NOT NULL COMMENT '创建人姓名',
    
    -- 【关联关系】负责人
    assignee_id BIGINT COMMENT '负责人ID (关联 sys_user.id)',
    assignee_name VARCHAR(100) COMMENT '负责人姓名',
    
    start_time DATETIME COMMENT '开始时间',
    deadline DATETIME COMMENT '截止时间',
    work_mode TINYINT DEFAULT 1 COMMENT '工作模式(1:办公室, 2:远程, 3:混合)',
    work_duration INT DEFAULT 0 COMMENT '工作时长(分钟)',
    actual_start_time DATETIME COMMENT '实际开始时间',
    actual_end_time DATETIME COMMENT '实际结束时间',
    work_status TINYINT DEFAULT 1 COMMENT '工作状态(1:工作中, 2:休息中, 3:会议中, 4:离线, 5:异常)',
    last_active_time DATETIME COMMENT '最后活跃时间',
    work_location VARCHAR(200) COMMENT '工作地点',
    work_evidence VARCHAR(500) COMMENT '工作佐证',
    work_log TEXT COMMENT '工作日志',
    completed_time DATETIME COMMENT '完成时间',
    estimated_hours INT COMMENT '预计工时(小时)',
    actual_hours INT COMMENT '实际工时(小时)',
    progress INT DEFAULT 0 COMMENT '任务进度(0-100%)',
    result TEXT COMMENT '完成结果',
    remark TEXT COMMENT '备注',
    
    -- 父子任务关系
    parent_task_id BIGINT COMMENT '父任务ID (关联 team_task.id)',
    level INT DEFAULT 1 COMMENT '任务层级',
    
    -- 审批流程
    need_approval TINYINT DEFAULT 0 COMMENT '是否需要审批(0:否, 1:是)',
    approver_id BIGINT COMMENT '审批人ID',
    approver_name VARCHAR(100) COMMENT '审批人姓名',
    approval_time DATETIME COMMENT '审批时间',
    approval_result TINYINT COMMENT '审批结果(1:通过, 2:拒绝, 3:待审批)',
    approval_comment TEXT COMMENT '审批意见',
    
    tags VARCHAR(500) COMMENT '任务标签',
    attachment_path VARCHAR(500) COMMENT '关联文件路径',
    
    -- 监督机制
    supervisor_id BIGINT COMMENT '监督人ID',
    supervisor_name VARCHAR(100) COMMENT '监督人姓名',
    need_supervision TINYINT DEFAULT 0 COMMENT '是否需要监督(0:否, 1:是)',
    supervision_interval INT COMMENT '监督间隔(分钟)',
    last_supervision_time DATETIME COMMENT '最后监督时间',
    quality_score TINYINT COMMENT '质量评分(1-5)',
    efficiency_score TINYINT COMMENT '效率评分(1-5)',
    attitude_score TINYINT COMMENT '态度评分(1-5)',
    supervision_note TEXT COMMENT '监督备注',
    
    is_public TINYINT DEFAULT 1 COMMENT '是否公开(0:否, 1:是)',
    visible_user_ids VARCHAR(1000) COMMENT '可见用户ID列表',
    
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除, 1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    
    INDEX idx_customer_id (customer_id),
    INDEX idx_task_type (task_type),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_creator_id (creator_id),
    INDEX idx_assignee_id (assignee_id),
    INDEX idx_deadline (deadline),
    INDEX idx_parent_task_id (parent_task_id),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队任务表';

-- 任务进度汇报表
CREATE TABLE IF NOT EXISTS task_progress_report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    
    -- 【关联关系】
    task_id BIGINT COMMENT '任务ID (关联 team_task.id)',
    task_name VARCHAR(200) COMMENT '任务名称',
    
    -- 【关联关系】
    employee_id BIGINT COMMENT '员工ID (关联 sys_user.id)',
    employee_name VARCHAR(100) COMMENT '员工姓名',
    
    report_type TINYINT COMMENT '汇报类型(1:日报, 2:周报, 3:月报, 4:项目汇报, 5:紧急汇报)',
    report_title VARCHAR(200) COMMENT '汇报标题',
    report_content TEXT COMMENT '汇报内容',
    progress INT DEFAULT 0 COMMENT '完成进度(0-100%)',
    work_duration INT COMMENT '工作时长(分钟)',
    work_results TEXT COMMENT '工作成果',
    problems TEXT COMMENT '遇到的问题',
    solutions TEXT COMMENT '解决方案',
    next_plan TEXT COMMENT '下一步计划',
    support_needed TEXT COMMENT '需要支持',
    work_location VARCHAR(200) COMMENT '工作地点',
    work_mode TINYINT COMMENT '工作模式(1:办公室, 2:远程, 3:混合)',
    attachments VARCHAR(500) COMMENT '附件文件',
    report_status TINYINT DEFAULT 1 COMMENT '汇报状态(1:待审核, 2:已通过, 3:需修改, 4:已驳回)',
    
    reviewer_id BIGINT COMMENT '审核人ID',
    reviewer_name VARCHAR(100) COMMENT '审核人姓名',
    review_time DATETIME COMMENT '审核时间',
    review_comment TEXT COMMENT '审核意见',
    quality_score TINYINT COMMENT '质量评分(1-5分)',
    efficiency_score TINYINT COMMENT '效率评分(1-5分)',
    attitude_score TINYINT COMMENT '态度评分(1-5分)',
    overall_score DECIMAL(3,1) COMMENT '综合评分',
    is_abnormal TINYINT DEFAULT 0 COMMENT '是否异常(0:否, 1:是)',
    abnormal_reason TEXT COMMENT '异常原因',
    supervision_note TEXT COMMENT '监督备注',
    
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除, 1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    
    INDEX idx_task_id (task_id),
    INDEX idx_employee_id (employee_id),
    INDEX idx_report_type (report_type),
    INDEX idx_report_status (report_status),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务进度汇报表';

-- 部门表
CREATE TABLE IF NOT EXISTS department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    department_name VARCHAR(100) NOT NULL COMMENT '部门名称',
    
    -- 【唯一约束】
    department_code VARCHAR(50) UNIQUE COMMENT '部门编码 (UNIQUE: 如 HR, DEV)',
    
    -- 【自关联】
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID (关联 department.id)',
    
    level INT DEFAULT 1 COMMENT '部门层级',
    sort_order INT DEFAULT 0 COMMENT '排序',
    
    -- 【关联关系】
    leader_id BIGINT COMMENT '部门负责人ID (关联 sys_user.id)',
    leader_name VARCHAR(100) COMMENT '部门负责人姓名',
    
    description TEXT COMMENT '部门描述',
    status TINYINT DEFAULT 1 COMMENT '状态(1:正常, 2:禁用)',
    
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除, 1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    
    INDEX idx_department_code (department_code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';


-- 消息表
CREATE TABLE IF NOT EXISTS message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '消息标题',
    content TEXT COMMENT '消息内容',
    message_type TINYINT NOT NULL COMMENT '消息类型(1:系统通知, 2:业务提醒, 3:任务通知, 4:客户消息)',
    importance TINYINT DEFAULT 1 COMMENT '消息重要性(1:普通, 2:重要, 3:紧急)',
    
    -- 泛型关联
    business_type VARCHAR(50) COMMENT '关联业务类型(customer, communication, task, ai_analysis, team_task, system)',
    business_id BIGINT COMMENT '关联业务ID',
    business_name VARCHAR(200) COMMENT '关联业务名称',
    
    -- 【关联关系】
    receiver_id BIGINT NOT NULL COMMENT '接收用户ID (关联 sys_user.id)',
    receiver_name VARCHAR(100) COMMENT '接收用户姓名',
    sender_id BIGINT COMMENT '发送用户ID (系统消息为null)',
    sender_name VARCHAR(100) COMMENT '发送用户姓名',
    
    is_read TINYINT DEFAULT 0 COMMENT '是否已读(0:未读, 1:已读)',
    read_time DATETIME COMMENT '已读时间',
    is_processed TINYINT DEFAULT 0 COMMENT '是否已处理(0:未处理, 1:已处理)',
    processed_time DATETIME COMMENT '处理时间',
    process_result TEXT COMMENT '处理结果',
    link_url VARCHAR(500) COMMENT '跳转链接',
    link_params VARCHAR(1000) COMMENT '跳转参数(JSON格式)',
    expire_time DATETIME COMMENT '过期时间',
    icon VARCHAR(100) COMMENT '消息图标',
    color VARCHAR(50) COMMENT '消息颜色',
    extra_info TEXT COMMENT '扩展信息(JSON格式)',
    
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除, 1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_message_type (message_type),
    INDEX idx_importance (importance),
    INDEX idx_is_read (is_read),
    INDEX idx_business_type (business_type),
    INDEX idx_business_id (business_id),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- ==========================================
-- 5. 知识库模块 (Knowledge Base)
-- ==========================================

-- 知识库文档表 (Legacy)
-- 旧版本表，建议使用 kb_document
CREATE TABLE IF NOT EXISTS knowledge_document (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(255) NOT NULL COMMENT '文档标题',
    content LONGTEXT COMMENT '文档内容',
    file_name VARCHAR(255) COMMENT '文件名',
    file_type VARCHAR(50) COMMENT '文件类型(pdf/word/excel/txt)',
    file_size BIGINT COMMENT '文件大小(字节)',
    file_path VARCHAR(500) COMMENT '文件路径',
    document_type VARCHAR(100) COMMENT '文档类型',
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
    INDEX idx_create_time (create_time),
    FULLTEXT INDEX ft_kd_content (title, content, summary)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库文档表';

-- 知识库文档表 (New)
-- 【核心表】存储所有业务文档、知识条目
CREATE TABLE IF NOT EXISTS kb_document (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '文档标题 (支持全文检索)',
    content LONGTEXT COMMENT '文档解析后的纯文本内容',
    original_content LONGTEXT COMMENT '原始MD/HTML内容',
    file_path VARCHAR(500) COMMENT '源文件物理路径',
    file_name VARCHAR(255) COMMENT '源文件名',
    file_type VARCHAR(50) COMMENT '文件后缀',
    file_mime VARCHAR(100) COMMENT 'MIME类型',
    file_size BIGINT COMMENT '文件大小',
    
    -- 【关联关系】
    category_id BIGINT COMMENT '分类ID',
    
    tags VARCHAR(500) COMMENT '人工标签',
    auto_tags VARCHAR(500) COMMENT 'AI自动提取标签',
    keywords VARCHAR(500) COMMENT '关键词',
    summary TEXT COMMENT 'AI生成的摘要',
    priority INT DEFAULT 0 COMMENT '置顶优先级',
    view_count INT DEFAULT 0 COMMENT '阅读量',
    download_count INT DEFAULT 0 COMMENT '下载量',
    like_count INT DEFAULT 0 COMMENT '点赞量',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否生效',
    is_public TINYINT(1) DEFAULT 1 COMMENT '是否公开',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_by VARCHAR(50),
    update_by VARCHAR(50),
    deleted TINYINT(1) DEFAULT 0,
    version INT DEFAULT 1,
    
    -- 全文索引 (Title, Content, Keywords)
    FULLTEXT INDEX ft_kb_content (title, content, keywords)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='新版知识库文档表';

-- FAQ问答表
-- 【核心表】存储问答对，用于精确匹配和相似度检索
CREATE TABLE IF NOT EXISTS faq_qa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    question VARCHAR(500) NOT NULL COMMENT '问题 (Standard Question)',
    answer TEXT NOT NULL COMMENT '答案 (Standard Answer)',
    keywords VARCHAR(500) COMMENT '关键词(逗号分隔)',
    category VARCHAR(50) COMMENT '分类',
    priority INT DEFAULT 0 COMMENT '优先级(数字越大优先级越高)',
    hit_count INT DEFAULT 0 COMMENT '命中次数 (热门问题依据)',
    status TINYINT DEFAULT 1 COMMENT '状态(1:启用 0:禁用)',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标志',
    
    INDEX idx_category (category),
    INDEX idx_priority (priority),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted),
    
    -- 全文索引 (Question, Keywords)
    FULLTEXT INDEX ft_question (question, keywords)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FAQ问答表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    
    -- 【唯一约束】
    config_key VARCHAR(200) NOT NULL UNIQUE COMMENT '配置键 (UNIQUE: 系统级唯一)',
    
    config_value TEXT NOT NULL COMMENT '配置值',
    config_type VARCHAR(20) NOT NULL DEFAULT 'STRING' COMMENT '配置类型(STRING, NUMBER, BOOLEAN, JSON)',
    description VARCHAR(500) COMMENT '配置描述',
    config_group VARCHAR(100) DEFAULT '其他' COMMENT '配置分组',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除(0:未删除, 1:已删除)',
    INDEX idx_config_key (config_key),
    INDEX idx_config_group (config_group),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';
