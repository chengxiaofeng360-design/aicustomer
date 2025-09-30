-- AI客户管理系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS ai_customer_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ai_customer_db;

-- 客户基本信息表
CREATE TABLE IF NOT EXISTS customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    customer_code VARCHAR(50) NOT NULL UNIQUE COMMENT '客户编号',
    customer_name VARCHAR(100) NOT NULL COMMENT '客户姓名/企业名称',
    contact_person VARCHAR(100) COMMENT '联系人',
    customer_type TINYINT NOT NULL DEFAULT 1 COMMENT '客户类型(1:个人客户,2:企业客户)',
    phone VARCHAR(20) COMMENT '手机号码',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(500) COMMENT '地址',
    
    -- 植物新品种保护申请相关字段
    postal_code VARCHAR(10) COMMENT '邮政编码',
    fax VARCHAR(20) COMMENT '传真',
    organization_code VARCHAR(50) COMMENT '机构代码或身份证号码',
    nationality VARCHAR(50) COMMENT '国籍或所在国（地区）',
    applicant_nature TINYINT COMMENT '申请人性质(1:个人,2:企业,3:科研院所,4:其他)',
    
    -- 代理机构信息
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
    
    customer_level TINYINT NOT NULL DEFAULT 1 COMMENT '客户等级(1:普通,2:VIP,3:钻石)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '客户状态(1:正常,2:冻结,3:注销)',
    source TINYINT NOT NULL DEFAULT 1 COMMENT '客户来源(1:线上,2:线下,3:推荐)',
    assigned_user_id BIGINT COMMENT '负责业务员ID',
    assigned_user_name VARCHAR(100) COMMENT '负责业务员姓名',
    last_contact_time DATETIME COMMENT '最后联系时间',
    remark TEXT COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    INDEX idx_customer_code (customer_code),
    INDEX idx_customer_name (customer_name),
    INDEX idx_contact_person (contact_person),
    INDEX idx_phone (phone),
    INDEX idx_customer_type (customer_type),
    INDEX idx_applicant_nature (applicant_nature),
    INDEX idx_agency_name (agency_name),
    INDEX idx_agent_name (agent_name),
    INDEX idx_customer_level (customer_level),
    INDEX idx_status (status),
    INDEX idx_source (source),
    INDEX idx_assigned_user_id (assigned_user_id),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户基本信息表';

-- 客户详细信息表
CREATE TABLE IF NOT EXISTS customer_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    customer_code VARCHAR(50) NOT NULL COMMENT '客户编号',
    
    -- 业务信息
    industry_category VARCHAR(100) COMMENT '行业分类',
    business_type VARCHAR(100) COMMENT '业务类型',
    importance TINYINT DEFAULT 1 COMMENT '重要程度(1:一般,2:重要,3:非常重要)',
    
    -- 价值分析
    value_score INT DEFAULT 0 COMMENT '客户价值评分(0-100分)',
    lifecycle_stage TINYINT DEFAULT 1 COMMENT '生命周期阶段(1:潜在,2:接触,3:合作,4:维护,5:流失)',
    total_amount DECIMAL(15,2) DEFAULT 0.00 COMMENT '累计消费金额',
    cooperation_count INT DEFAULT 0 COMMENT '合作次数',
    credit_level TINYINT DEFAULT 1 COMMENT '信用评估等级(1:A,2:B,3:C,4:D)',
    satisfaction_score TINYINT DEFAULT 0 COMMENT '客户满意度评分(1-5分)',
    completeness INT DEFAULT 0 COMMENT '信息完整度(0-100%)',
    
    -- 时间记录
    customer_create_time DATETIME COMMENT '客户创建时间',
    first_cooperation_time DATETIME COMMENT '首次合作时间',
    last_cooperation_time DATETIME COMMENT '最后合作时间',
    churn_time DATETIME COMMENT '客户流失时间',
    churn_reason VARCHAR(500) COMMENT '流失原因',
    
    -- 扩展信息
    company_size TINYINT COMMENT '公司规模(1:微型,2:小型,3:中型,4:大型)',
    annual_revenue DECIMAL(15,2) COMMENT '年营业额',
    employee_count INT COMMENT '员工数量',
    primary_contact VARCHAR(100) COMMENT '主要联系人',
    decision_maker VARCHAR(100) COMMENT '决策人',
    purchase_cycle TINYINT COMMENT '采购周期(1:月度,2:季度,3:半年度,4:年度)',
    budget_range VARCHAR(100) COMMENT '预算范围',
    competitors VARCHAR(500) COMMENT '竞争对手',
    special_requirements TEXT COMMENT '特殊要求',
    preferences TEXT COMMENT '客户偏好',
    risk_level TINYINT DEFAULT 1 COMMENT '风险等级(1:低,2:中,3:高)',
    risk_description TEXT COMMENT '风险描述',
    
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    UNIQUE KEY uk_customer_id (customer_id),
    INDEX idx_customer_code (customer_code),
    INDEX idx_importance (importance),
    INDEX idx_value_score (value_score),
    INDEX idx_lifecycle_stage (lifecycle_stage),
    INDEX idx_credit_level (credit_level),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户详细信息表';

-- 客户标签表
CREATE TABLE IF NOT EXISTS customer_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    tag_name VARCHAR(50) NOT NULL COMMENT '标签名称',
    tag_type TINYINT NOT NULL DEFAULT 2 COMMENT '标签类型(1:系统标签,2:自定义标签,3:AI标签)',
    tag_color VARCHAR(20) DEFAULT '#007bff' COMMENT '标签颜色',
    tag_description VARCHAR(200) COMMENT '标签描述',
    weight INT DEFAULT 0 COMMENT '标签权重',
    is_visible TINYINT DEFAULT 1 COMMENT '是否显示(0:隐藏,1:显示)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    INDEX idx_customer_id (customer_id),
    INDEX idx_tag_name (tag_name),
    INDEX idx_tag_type (tag_type),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户标签表';

-- 插入示例数据
INSERT INTO customer (
    customer_code, customer_name, contact_person, customer_type, phone, email, address,
    postal_code, fax, organization_code, nationality, applicant_nature,
    agency_name, agency_code, agency_address, agency_postal_code,
    agent_name, agent_phone, agent_fax, agent_mobile, agent_email,
    customer_level, status, remark, source, last_contact_time, create_by, update_by
) VALUES
('CUST001', 'ABC科技有限公司', '张伟', 2, '13812345678', 'zhangwei@abctech.com', '广东省深圳市南山区科技园',
 '518000', '0755-12345678', '91440300123456789X', '中国', 2,
 '深圳知识产权代理有限公司', '91440300987654321Y', '深圳市福田区知识产权大厦', '518000',
 '王律师', '0755-87654321', '0755-87654322', '13987654321', 'wang@ipagency.com',
 2, 1, '重要客户，需要重点关注', 1, '2024-01-15 10:30:00', 'admin', 'admin'),

('CUST002', 'XYZ咨询公司', '李娜', 2, '13987654321', 'lina@xyzconsult.com', '北京市朝阳区国贸大厦',
 '100000', '010-12345678', '91110000123456789X', '中国', 2,
 '北京知识产权代理事务所', '91110000987654321Y', '北京市海淀区中关村大街', '100000',
 '刘律师', '010-87654321', '010-87654322', '13812345678', 'liu@bjip.com',
 1, 1, '咨询类客户', 2, '2024-01-14 15:20:00', 'admin', 'admin'),

('CUST003', '张三', '张三', 1, '13612345678', 'zhangsan@email.com', '上海市浦东新区陆家嘴',
 '200000', '', '310101199001011234', '中国', 1,
 '', '', '', '',
 '', '', '', '', '',
 1, 1, '个人发明者', 1, '2024-01-13 09:15:00', 'admin', 'admin'),

('CUST004', '中科院植物研究所', '李教授', 2, '010-12345678', 'li@cas.cn', '北京市海淀区中关村南大街',
 '100000', '010-12345679', '12100000400000000X', '中国', 3,
 '中科院知识产权代理中心', '91110000123456789X', '北京市海淀区中关村', '100000',
 '陈博士', '010-87654321', '010-87654322', '13712345678', 'chen@casip.com',
 3, 1, '国家级科研院所，重点客户', 3, '2024-01-10 14:45:00', 'admin', 'admin'),

('CUST005', '钱七', '钱七', 2, '13800138005', 'qianqi@company.com', '杭州市西湖区',
 '310000', '0571-12345678', '91330100123456789X', '中国', 2,
 '杭州知识产权代理有限公司', '91330100987654321Y', '杭州市西湖区文三路', '310000',
 '孙律师', '0571-87654321', '0571-87654322', '13812345678', 'sun@hzip.com',
 2, 1, '企业VIP客户', 2, '2024-01-12 11:30:00', 'admin', 'admin');

-- 用户表（用于系统管理）
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(100) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号码',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:正常,2:禁用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    INDEX idx_username (username),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 插入默认管理员用户（密码：admin123）
INSERT INTO sys_user (username, password, real_name, email, phone, status, create_by, update_by) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '系统管理员', 'admin@example.com', '13800000000', 1, 'system', 'system');

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:正常,2:禁用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    INDEX idx_role_code (role_code),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 插入默认角色
INSERT INTO sys_role (role_name, role_code, description, status, create_by, update_by) VALUES
('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1, 'system', 'system'),
('普通管理员', 'ADMIN', '普通管理员，拥有大部分权限', 1, 'system', 'system'),
('普通用户', 'USER', '普通用户，拥有基础权限', 1, 'system', 'system');

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 为管理员用户分配超级管理员角色
INSERT INTO sys_user_role (user_id, role_id, create_by) VALUES
(1, 1, 'system');

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    permission_type TINYINT NOT NULL DEFAULT 1 COMMENT '权限类型(1:菜单,2:按钮,3:接口)',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    path VARCHAR(200) COMMENT '路径',
    component VARCHAR(200) COMMENT '组件',
    icon VARCHAR(100) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:正常,2:禁用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    INDEX idx_permission_code (permission_code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 插入基础权限
INSERT INTO sys_permission (permission_name, permission_code, permission_type, parent_id, path, component, icon, sort_order, status, create_by, update_by) VALUES
('系统管理', 'system', 1, 0, '/system', 'Layout', 'setting', 1, 1, 'system', 'system'),
('用户管理', 'user', 1, 1, '/system/user', 'system/user/index', 'user', 1, 1, 'system', 'system'),
('角色管理', 'role', 1, 1, '/system/role', 'system/role/index', 'peoples', 2, 1, 'system', 'system'),
('权限管理', 'permission', 1, 1, '/system/permission', 'system/permission/index', 'tree', 3, 1, 'system', 'system'),
('客户管理', 'customer', 1, 0, '/customer', 'Layout', 'peoples', 2, 1, 'system', 'system'),
('客户列表', 'customer:list', 1, 5, '/customer/list', 'customer/list/index', 'list', 1, 1, 'system', 'system'),
('客户添加', 'customer:add', 2, 5, '', '', '', 1, 1, 'system', 'system'),
('客户编辑', 'customer:edit', 2, 5, '', '', '', 2, 1, 'system', 'system'),
('客户删除', 'customer:delete', 2, 5, '', '', '', 3, 1, 'system', 'system'),
('客户查看', 'customer:view', 2, 5, '', '', '', 4, 1, 'system', 'system');

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 为超级管理员角色分配所有权限
INSERT INTO sys_role_permission (role_id, permission_id, create_by) 
SELECT 1, id, 'system' FROM sys_permission WHERE deleted = 0;

-- 沟通记录表
CREATE TABLE IF NOT EXISTS communication_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    customer_name VARCHAR(100) NOT NULL COMMENT '客户姓名',
    communication_type TINYINT NOT NULL COMMENT '沟通类型(1:微信,2:电话,3:邮件,4:会面,5:其他)',
    communication_time DATETIME NOT NULL COMMENT '沟通时间',
    content TEXT COMMENT '沟通内容',
    summary VARCHAR(1000) COMMENT '沟通摘要',
    importance TINYINT DEFAULT 1 COMMENT '重要程度(1:一般,2:重要,3:非常重要)',
    sentiment TINYINT COMMENT '情感分析结果(1:积极,2:中性,3:消极)',
    satisfaction_score TINYINT COMMENT '满意度评分(1-5分)',
    keywords VARCHAR(500) COMMENT '关键词(多个关键词用逗号分隔)',
    important_info TEXT COMMENT '重要信息标记',
    follow_up_task VARCHAR(1000) COMMENT '后续任务',
    task_deadline DATETIME COMMENT '任务截止时间',
    task_status TINYINT DEFAULT 1 COMMENT '任务状态(1:待处理,2:进行中,3:已完成,4:已取消)',
    communicator_id BIGINT COMMENT '沟通人员ID',
    communicator_name VARCHAR(100) COMMENT '沟通人员姓名',
    attachment_path VARCHAR(500) COMMENT '附件路径',
    channel_detail VARCHAR(200) COMMENT '沟通渠道详情',
    duration INT COMMENT '沟通时长(分钟)',
    response_time INT COMMENT '客户响应时间(分钟)',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读(0:未读,1:已读)',
    is_processed TINYINT DEFAULT 0 COMMENT '是否已处理(0:未处理,1:已处理)',
    process_result TEXT COMMENT '处理结果',
    process_time DATETIME COMMENT '处理时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    INDEX idx_customer_id (customer_id),
    INDEX idx_communication_type (communication_type),
    INDEX idx_communication_time (communication_time),
    INDEX idx_importance (importance),
    INDEX idx_communicator_id (communicator_id),
    INDEX idx_create_time (create_time),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='沟通记录表';

-- 任务提醒表
CREATE TABLE IF NOT EXISTS task_reminder (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '提醒标题',
    content TEXT COMMENT '提醒内容',
    reminder_type TINYINT NOT NULL COMMENT '提醒类型(1:关键日期,2:跟进提醒,3:机会提醒,4:风险预警)',
    customer_id BIGINT COMMENT '关联客户ID',
    customer_name VARCHAR(100) COMMENT '关联客户姓名',
    reminder_time DATETIME NOT NULL COMMENT '提醒时间',
    deadline DATETIME COMMENT '截止时间',
    priority TINYINT DEFAULT 2 COMMENT '优先级(1:低,2:中,3:高,4:紧急)',
    status TINYINT DEFAULT 1 COMMENT '提醒状态(1:待提醒,2:已提醒,3:已完成,4:已取消,5:已过期)',
    assigned_user_id BIGINT COMMENT '负责人员ID',
    assigned_user_name VARCHAR(100) COMMENT '负责人员姓名',
    reminder_method TINYINT DEFAULT 1 COMMENT '提醒方式(1:系统内,2:邮件,3:短信,4:微信,5:全部)',
    frequency TINYINT DEFAULT 1 COMMENT '提醒频率(1:一次性,2:每日,3:每周,4:每月)',
    repeat_count INT DEFAULT 0 COMMENT '重复次数',
    repeated_count INT DEFAULT 0 COMMENT '已重复次数',
    next_reminder_time DATETIME COMMENT '下次提醒时间',
    completed_time DATETIME COMMENT '完成时间',
    completion_result TEXT COMMENT '完成结果',
    customer_response_time DATETIME COMMENT '客户响应时间',
    customer_response TEXT COMMENT '客户响应内容',
    effectiveness_score TINYINT COMMENT '提醒效果评分(1-5分)',
    business_id BIGINT COMMENT '关联业务ID',
    business_type VARCHAR(100) COMMENT '关联业务类型',
    rule_id BIGINT COMMENT '提醒规则ID',
    is_auto_generated TINYINT DEFAULT 0 COMMENT '是否自动生成(0:否,1:是)',
    generated_from VARCHAR(100) COMMENT '生成来源',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
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
    task_type TINYINT NOT NULL COMMENT '任务类型(1:客户跟进,2:项目推进,3:问题处理,4:会议安排,5:其他)',
    status TINYINT DEFAULT 1 COMMENT '任务状态(1:待分配,2:进行中,3:待审核,4:已完成,5:已取消)',
    priority TINYINT DEFAULT 2 COMMENT '优先级(1:低,2:中,3:高,4:紧急)',
    customer_id BIGINT COMMENT '关联客户ID',
    customer_name VARCHAR(100) COMMENT '关联客户姓名',
    creator_id BIGINT NOT NULL COMMENT '创建人ID',
    creator_name VARCHAR(100) NOT NULL COMMENT '创建人姓名',
    assignee_id BIGINT COMMENT '负责人ID',
    assignee_name VARCHAR(100) COMMENT '负责人姓名',
    start_time DATETIME COMMENT '开始时间',
    deadline DATETIME COMMENT '截止时间',
    completed_time DATETIME COMMENT '完成时间',
    estimated_hours INT COMMENT '预计工时(小时)',
    actual_hours INT COMMENT '实际工时(小时)',
    progress INT DEFAULT 0 COMMENT '任务进度(0-100%)',
    result TEXT COMMENT '完成结果',
    remark TEXT COMMENT '备注',
    parent_task_id BIGINT COMMENT '父任务ID',
    level INT DEFAULT 1 COMMENT '任务层级',
    need_approval TINYINT DEFAULT 0 COMMENT '是否需要审批(0:否,1:是)',
    approver_id BIGINT COMMENT '审批人ID',
    approver_name VARCHAR(100) COMMENT '审批人姓名',
    approval_time DATETIME COMMENT '审批时间',
    approval_result TINYINT COMMENT '审批结果(1:通过,2:拒绝,3:待审批)',
    approval_comment TEXT COMMENT '审批意见',
    tags VARCHAR(500) COMMENT '任务标签',
    attachment_path VARCHAR(500) COMMENT '关联文件路径',
    is_public TINYINT DEFAULT 1 COMMENT '是否公开(0:否,1:是)',
    visible_user_ids VARCHAR(1000) COMMENT '可见用户ID列表',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
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

-- 部门表
CREATE TABLE IF NOT EXISTS department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    department_name VARCHAR(100) NOT NULL COMMENT '部门名称',
    department_code VARCHAR(50) UNIQUE COMMENT '部门编码',
    parent_id BIGINT DEFAULT 0 COMMENT '父部门ID',
    level INT DEFAULT 1 COMMENT '部门层级',
    sort_order INT DEFAULT 0 COMMENT '排序',
    leader_id BIGINT COMMENT '部门负责人ID',
    leader_name VARCHAR(100) COMMENT '部门负责人姓名',
    description TEXT COMMENT '部门描述',
    status TINYINT DEFAULT 1 COMMENT '状态(1:正常,2:禁用)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    INDEX idx_department_code (department_code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- 插入默认部门
INSERT INTO department (department_name, department_code, description, status, create_by, update_by) VALUES
('技术部', 'TECH', '技术研发部门', 1, 'system', 'system'),
('销售部', 'SALES', '销售业务部门', 1, 'system', 'system'),
('客服部', 'SERVICE', '客户服务部门', 1, 'system', 'system'),
('管理部', 'ADMIN', '管理部门', 1, 'system', 'system');

-- 更新用户表结构
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS nickname VARCHAR(100) COMMENT '昵称';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS avatar VARCHAR(500) COMMENT '头像路径';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS user_type TINYINT DEFAULT 2 COMMENT '用户类型(1:管理员,2:业务员,3:助理,4:其他)';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS department_id BIGINT COMMENT '部门ID';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS department_name VARCHAR(100) COMMENT '部门名称';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS position VARCHAR(100) COMMENT '职位';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS join_time DATETIME COMMENT '入职时间';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS last_login_time DATETIME COMMENT '最后登录时间';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS last_login_ip VARCHAR(50) COMMENT '最后登录IP';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS login_count INT DEFAULT 0 COMMENT '登录次数';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS password_change_time DATETIME COMMENT '密码修改时间';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS is_first_login TINYINT DEFAULT 1 COMMENT '是否首次登录(0:否,1:是)';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS signature VARCHAR(500) COMMENT '个人签名';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS bio TEXT COMMENT '个人简介';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS work_location VARCHAR(200) COMMENT '工作地点';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS timezone VARCHAR(50) DEFAULT 'Asia/Shanghai' COMMENT '时区';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS language VARCHAR(10) DEFAULT 'zh-CN' COMMENT '语言偏好';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS theme VARCHAR(20) DEFAULT 'light' COMMENT '主题偏好';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS notification_settings TEXT COMMENT '通知设置(JSON格式)';
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS permission_settings TEXT COMMENT '权限设置(JSON格式)';

-- 插入客户详细信息
INSERT INTO customer_detail (customer_id, customer_code, industry_category, business_type, importance, value_score, lifecycle_stage, total_amount, cooperation_count, credit_level, satisfaction_score, completeness, customer_create_time, first_cooperation_time, last_cooperation_time, company_size, annual_revenue, employee_count, primary_contact, decision_maker, purchase_cycle, budget_range, risk_level, create_by, update_by) VALUES
(1, 'CUST001', '科技', '软件服务', 2, 85, 3, 50000.00, 5, 1, 5, 90, '2023-01-01 00:00:00', '2023-01-15 00:00:00', '2024-01-15 10:30:00', 3, 1000000.00, 50, '张三', '李总', 2, '50-100万', 1, 'admin', 'admin'),
(2, 'CUST002', '制造业', '生产制造', 1, 60, 2, 15000.00, 2, 2, 4, 75, '2023-06-01 00:00:00', '2023-06-15 00:00:00', '2024-01-14 15:20:00', 2, 500000.00, 20, '李四', '王总', 3, '20-50万', 2, 'admin', 'admin'),
(3, 'CUST003', '金融', '投资咨询', 3, 95, 4, 200000.00, 8, 1, 5, 95, '2022-03-01 00:00:00', '2022-03-20 00:00:00', '2024-01-13 09:15:00', 4, 5000000.00, 200, '王五', '赵总', 1, '200-500万', 1, 'admin', 'admin'),
(4, 'CUST004', '教育', '在线教育', 1, 40, 5, 0.00, 0, 3, 2, 60, '2023-12-01 00:00:00', NULL, NULL, 1, 100000.00, 5, '赵六', '孙总', 4, '10-20万', 3, 'admin', 'admin'),
(5, 'CUST005', '医疗', '医疗器械', 2, 75, 3, 80000.00, 3, 2, 4, 85, '2023-08-01 00:00:00', '2023-08-15 00:00:00', '2024-01-12 11:30:00', 3, 2000000.00, 100, '钱七', '周总', 2, '100-200万', 2, 'admin', 'admin');

-- 插入客户标签
INSERT INTO customer_tag (customer_id, tag_name, tag_type, tag_color, tag_description, weight, create_by, update_by) VALUES
(1, 'VIP客户', 1, '#ff6b6b', '高价值客户', 100, 'admin', 'admin'),
(1, '活跃客户', 1, '#4ecdc4', '经常合作的客户', 90, 'admin', 'admin'),
(1, '科技行业', 1, '#45b7d1', '科技行业客户', 80, 'admin', 'admin'),
(2, '普通客户', 1, '#96ceb4', '一般客户', 70, 'admin', 'admin'),
(2, '制造业', 1, '#feca57', '制造业客户', 60, 'admin', 'admin'),
(3, '钻石客户', 1, '#ff9ff3', '最高等级客户', 100, 'admin', 'admin'),
(3, '金融行业', 1, '#54a0ff', '金融行业客户', 90, 'admin', 'admin'),
(3, '大客户', 1, '#5f27cd', '大额订单客户', 95, 'admin', 'admin'),
(4, '流失客户', 1, '#ff6348', '已流失的客户', 30, 'admin', 'admin'),
(4, '教育行业', 1, '#ffa502', '教育行业客户', 50, 'admin', 'admin'),
(5, 'VIP客户', 1, '#ff6b6b', '高价值客户', 85, 'admin', 'admin'),
(5, '医疗行业', 1, '#2ed573', '医疗行业客户', 80, 'admin', 'admin');
