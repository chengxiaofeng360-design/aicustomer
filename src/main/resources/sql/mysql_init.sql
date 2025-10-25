-- MySQL数据库初始化脚本
-- AI客户管理系统数据库创建脚本

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS ai_customer CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE ai_customer;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    real_name VARCHAR(50) COMMENT '真实姓名',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:正常,2:禁用)',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

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

    -- 植物新品种保护申请详细信息
    variety_name VARCHAR(200) COMMENT '品种名称',
    variety_type VARCHAR(100) COMMENT '品种类型',
    application_number VARCHAR(50) COMMENT '申请号',
    application_date DATE COMMENT '申请日期',
    breeder_name VARCHAR(100) COMMENT '培育人姓名',
    breeder_unit VARCHAR(200) COMMENT '培育人单位',
    breeder_address VARCHAR(500) COMMENT '培育人地址',
    breeder_phone VARCHAR(20) COMMENT '培育人电话',
    applicant_name VARCHAR(100) COMMENT '申请人姓名',
    applicant_unit VARCHAR(200) COMMENT '申请人单位',
    applicant_address VARCHAR(500) COMMENT '申请人地址',
    applicant_phone VARCHAR(20) COMMENT '申请人电话',
    variety_description TEXT COMMENT '品种描述',
    technical_features TEXT COMMENT '技术特点',
    market_prospect TEXT COMMENT '市场前景',

    -- 敏感数据保护字段
    is_sensitive BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否为敏感数据',
    protection_password VARCHAR(255) COMMENT '保护密码(加密存储)',

    customer_level TINYINT NOT NULL DEFAULT 1 COMMENT '客户等级(1:普通,2:VIP,3:钻石)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '客户状态(1:正常,2:冻结,3:注销)',
    source TINYINT NOT NULL DEFAULT 1 COMMENT '客户来源(1:线上,2:线下,3:推荐)',
    assigned_user_id BIGINT COMMENT '负责业务员ID',
    assigned_user_name VARCHAR(100) COMMENT '负责业务员姓名',
    last_contact_time TIMESTAMP COMMENT '最后联系时间',
    remark TEXT COMMENT '备注',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    
    INDEX idx_customer_code (customer_code),
    INDEX idx_customer_name (customer_name),
    INDEX idx_contact_person (contact_person),
    INDEX idx_phone (phone),
    INDEX idx_customer_type (customer_type),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户基本信息表';

-- 客户账户表（社交平台）
CREATE TABLE IF NOT EXISTS customer_account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    account_type TINYINT NOT NULL DEFAULT 1 COMMENT '账户类型(1:个人,2:企业)',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    nickname VARCHAR(50) COMMENT '昵称',
    real_name VARCHAR(50) COMMENT '真实姓名',
    gender TINYINT COMMENT '性别(1:男,2:女,3:其他)',
    birthday DATE COMMENT '生日',
    location VARCHAR(100) COMMENT '所在地',
    bio TEXT COMMENT '个人简介',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:正常,2:禁用,3:待审核)',
    last_login_time TIMESTAMP COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户账户表';

-- 社交动态表
CREATE TABLE IF NOT EXISTS social_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    customer_account_id BIGINT NOT NULL COMMENT '客户账户ID',
    content TEXT NOT NULL COMMENT '动态内容',
    images TEXT COMMENT '图片URLs(JSON格式)',
    tags VARCHAR(500) COMMENT '标签(逗号分隔)',
    location VARCHAR(100) COMMENT '发布位置',
    post_type TINYINT NOT NULL DEFAULT 1 COMMENT '动态类型(1:普通,2:图片,3:视频,4:链接)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:正常,2:隐藏,3:删除)',
    like_count INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    comment_count INT NOT NULL DEFAULT 0 COMMENT '评论数',
    share_count INT NOT NULL DEFAULT 0 COMMENT '分享数',
    view_count INT NOT NULL DEFAULT 0 COMMENT '浏览数',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    
    FOREIGN KEY (customer_account_id) REFERENCES customer_account(id),
    INDEX idx_customer_account_id (customer_account_id),
    INDEX idx_post_type (post_type),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社交动态表';

-- 社交评论表
CREATE TABLE IF NOT EXISTS social_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    post_id BIGINT NOT NULL COMMENT '动态ID',
    customer_account_id BIGINT NOT NULL COMMENT '客户账户ID',
    parent_id BIGINT COMMENT '父评论ID',
    content TEXT NOT NULL COMMENT '评论内容',
    images TEXT COMMENT '图片URLs(JSON格式)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:正常,2:隐藏,3:删除)',
    like_count INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    
    FOREIGN KEY (post_id) REFERENCES social_post(id),
    FOREIGN KEY (customer_account_id) REFERENCES customer_account(id),
    FOREIGN KEY (parent_id) REFERENCES social_comment(id),
    INDEX idx_post_id (post_id),
    INDEX idx_customer_account_id (customer_account_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社交评论表';

-- 社交点赞表
CREATE TABLE IF NOT EXISTS social_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    customer_account_id BIGINT NOT NULL COMMENT '客户账户ID',
    target_type TINYINT NOT NULL COMMENT '目标类型(1:动态,2:评论)',
    target_id BIGINT NOT NULL COMMENT '目标ID',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    FOREIGN KEY (customer_account_id) REFERENCES customer_account(id),
    UNIQUE KEY uk_like (customer_account_id, target_type, target_id),
    INDEX idx_customer_account_id (customer_account_id),
    INDEX idx_target (target_type, target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社交点赞表';

-- 团队任务表
CREATE TABLE IF NOT EXISTS team_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    task_name VARCHAR(200) NOT NULL COMMENT '任务名称',
    task_description TEXT COMMENT '任务描述',
    task_type TINYINT NOT NULL DEFAULT 1 COMMENT '任务类型(1:开发,2:测试,3:文档,4:其他)',
    priority TINYINT NOT NULL DEFAULT 2 COMMENT '优先级(1:低,2:中,3:高,4:紧急)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:待开始,2:进行中,3:已完成,4:已取消)',
    assigned_to BIGINT COMMENT '分配给',
    assigned_to_name VARCHAR(100) COMMENT '分配给姓名',
    created_by BIGINT COMMENT '创建人ID',
    created_by_name VARCHAR(100) COMMENT '创建人姓名',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    estimated_hours DECIMAL(5,2) COMMENT '预估工时',
    actual_hours DECIMAL(5,2) COMMENT '实际工时',
    progress INT NOT NULL DEFAULT 0 COMMENT '进度百分比',
    work_mode TINYINT NOT NULL DEFAULT 1 COMMENT '工作模式(1:办公室,2:远程,3:混合)',
    work_duration INT COMMENT '工作时长(分钟)',
    actual_start_time TIMESTAMP COMMENT '实际开始时间',
    actual_end_time TIMESTAMP COMMENT '实际结束时间',
    work_status TINYINT COMMENT '工作状态(1:工作中,2:休息中,3:离线)',
    last_active_time TIMESTAMP COMMENT '最后活跃时间',
    work_location VARCHAR(200) COMMENT '工作地点',
    work_evidence TEXT COMMENT '工作证据',
    work_log TEXT COMMENT '工作日志',
    supervisor_id BIGINT COMMENT '监督人ID',
    supervisor_name VARCHAR(100) COMMENT '监督人姓名',
    need_supervision BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否需要监督',
    supervision_interval INT COMMENT '监督间隔(分钟)',
    last_supervision_time TIMESTAMP COMMENT '最后监督时间',
    quality_score DECIMAL(3,2) COMMENT '质量评分',
    efficiency_score DECIMAL(3,2) COMMENT '效率评分',
    attitude_score DECIMAL(3,2) COMMENT '态度评分',
    supervision_note TEXT COMMENT '监督备注',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    
    INDEX idx_task_name (task_name),
    INDEX idx_task_type (task_type),
    INDEX idx_status (status),
    INDEX idx_assigned_to (assigned_to),
    INDEX idx_created_by (created_by),
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队任务表';

-- 任务进度汇报表
CREATE TABLE IF NOT EXISTS task_progress_report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    task_name VARCHAR(200) COMMENT '任务名称',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    employee_name VARCHAR(100) COMMENT '员工姓名',
    report_type TINYINT NOT NULL DEFAULT 1 COMMENT '汇报类型(1:日报,2:周报,3:月报,4:专项汇报)',
    report_title VARCHAR(200) NOT NULL COMMENT '汇报标题',
    report_content TEXT NOT NULL COMMENT '汇报内容',
    progress INT NOT NULL DEFAULT 0 COMMENT '进度百分比',
    work_duration INT COMMENT '工作时长(分钟)',
    work_results TEXT COMMENT '工作成果',
    problems TEXT COMMENT '遇到的问题',
    solutions TEXT COMMENT '解决方案',
    next_plan TEXT COMMENT '下一步计划',
    support_needed TEXT COMMENT '需要的支持',
    work_location VARCHAR(200) COMMENT '工作地点',
    work_mode TINYINT COMMENT '工作模式(1:办公室,2:远程,3:混合)',
    attachments TEXT COMMENT '附件URLs(JSON格式)',
    report_status TINYINT NOT NULL DEFAULT 1 COMMENT '汇报状态(1:待审核,2:已通过,3:需修改)',
    reviewer_id BIGINT COMMENT '审核人ID',
    reviewer_name VARCHAR(100) COMMENT '审核人姓名',
    review_time TIMESTAMP COMMENT '审核时间',
    review_comment TEXT COMMENT '审核意见',
    quality_score DECIMAL(3,2) COMMENT '质量评分',
    efficiency_score DECIMAL(3,2) COMMENT '效率评分',
    attitude_score DECIMAL(3,2) COMMENT '态度评分',
    overall_score DECIMAL(3,2) COMMENT '综合评分',
    is_abnormal BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否异常',
    abnormal_reason TEXT COMMENT '异常原因',
    supervision_note TEXT COMMENT '监督备注',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    
    FOREIGN KEY (task_id) REFERENCES team_task(id),
    INDEX idx_task_id (task_id),
    INDEX idx_employee_id (employee_id),
    INDEX idx_report_type (report_type),
    INDEX idx_report_status (report_status),
    INDEX idx_reviewer_id (reviewer_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务进度汇报表';

-- 插入默认管理员用户
INSERT INTO user (username, password, email, real_name, role, status) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVqjJz4Kz8pSdQzJz4Kz8pSdQz', 'admin@aicustomer.com', '系统管理员', 'ADMIN', 1);

-- 插入示例客户数据
INSERT INTO customer (
    customer_code, customer_name, contact_person, customer_type, phone, email, address,
    postal_code, fax, organization_code, nationality, applicant_nature,
    agency_name, agency_code, agency_address, agency_postal_code,
    agent_name, agent_phone, agent_fax, agent_mobile, agent_email,
    variety_name, variety_type, application_number, application_date,
    breeder_name, breeder_unit, breeder_address, breeder_phone,
    applicant_name, applicant_unit, applicant_address, applicant_phone,
    variety_description, technical_features, market_prospect,
    is_sensitive, protection_password,
    customer_level, status, remark, source, last_contact_time, create_by, update_by
) VALUES
('CUST001', 'ABC科技有限公司', '张伟', 2, '13812345678', 'zhangwei@abctech.com', '广东省深圳市南山区科技园',
 '518000', '0755-12345678', '91440300123456789X', '中国', 2,
 '深圳知识产权代理有限公司', '91440300987654321Y', '深圳市福田区知识产权大厦', '518000',
 '王律师', '0755-87654321', '0755-87654322', '13987654321', 'wang@ipagency.com',
 'ABC科技产品', '科技产品', 'CNA2024001001', '2024-01-15',
 '张伟', 'ABC科技有限公司', '广东省深圳市南山区科技园', '13812345678',
 '张伟', 'ABC科技有限公司', '广东省深圳市南山区科技园', '13812345678',
 'ABC科技有限公司是一家专注于科技创新的企业，致力于为客户提供优质的科技产品和服务。公司拥有强大的研发团队和先进的技术设备。',
 '1. 技术创新：拥有多项专利技术和核心技术；2. 产品质量：严格的质量控制体系；3. 服务完善：提供全方位的技术支持；4. 市场领先：在行业内具有领先地位；5. 持续发展：不断投入研发，保持技术优势。',
 '随着科技行业的快速发展，ABC科技有限公司具有广阔的市场前景。预计未来几年内，公司将继续保持快速增长，为客户提供更多优质的科技产品和服务。',
 TRUE, '$2a$10$Ll/hmxK4s3sZJj8JNSQeBufrsBqHipuRJmzq.mNIy3sTx6HA5HJ3i',
 2, 1, '重要客户，需要重点关注', 1, '2024-01-15 10:30:00', 'admin', 'admin'),

('CUST002', 'XYZ咨询公司', '李娜', 2, '13987654321', 'lina@xyzconsult.com', '北京市朝阳区国贸大厦',
 '100000', '010-12345678', '91110000123456789X', '中国', 2,
 '北京知识产权代理事务所', '91110000987654321Y', '北京市海淀区中关村大街', '100000',
 '刘律师', '010-87654321', '010-87654322', '13812345678', 'liu@bjip.com',
 'XYZ咨询产品', '咨询产品', 'CNA2024002002', '2024-01-20',
 '李娜', 'XYZ咨询公司', '北京市朝阳区国贸大厦', '13987654321',
 '李娜', 'XYZ咨询公司', '北京市朝阳区国贸大厦', '13987654321',
 'XYZ咨询公司是一家专业的咨询服务公司，为客户提供全方位的咨询服务。',
 '1. 专业团队：拥有经验丰富的咨询团队；2. 服务全面：提供多领域咨询服务；3. 客户至上：以客户需求为导向；4. 质量保证：严格的质量控制体系。',
 '咨询行业市场需求稳定，XYZ咨询公司具有良好的发展前景。',
 FALSE, NULL,
 1, 1, '咨询类客户', 2, '2024-01-14 15:20:00', 'admin', 'admin'),

('CUST003', '张三', '张三', 1, '13612345678', 'zhangsan@email.com', '上海市浦东新区陆家嘴',
 '200000', '', '310101199001011234', '中国', 1,
 '', '', '', '',
 '', '', '', '', '',
 '张三产品', '个人产品', 'CNA2024003003', '2024-01-25',
 '张三', '个人', '上海市浦东新区陆家嘴', '13612345678',
 '张三', '个人', '上海市浦东新区陆家嘴', '13612345678',
 '个人发明者，专注于创新产品的研发。',
 '1. 创新思维：具有独特的创新思维；2. 技术实力：拥有扎实的技术基础；3. 市场敏感：对市场变化敏感；4. 持续学习：不断学习新技术。',
 '个人发明者在创新领域具有独特优势，市场前景良好。',
 TRUE, '$2a$10$Ll/hmxK4s3sZJj8JNSQeBufrsBqHipuRJmzq.mNIy3sTx6HA5HJ3i',
 1, 1, '个人发明者', 1, '2024-01-13 09:15:00', 'admin', 'admin');
