-- AI客户管理系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS zqgl CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE zqgl;

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
    
    -- 联系方式与业务扩展
    position VARCHAR(100) COMMENT '职务',
    qq_weixin VARCHAR(100) COMMENT 'QQ/微信',
    cooperation_content TEXT COMMENT '合作内容',
    region VARCHAR(50) COMMENT '地区',
    
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
    
    -- 数据保护
    is_sensitive TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否为敏感数据',
    protection_password VARCHAR(255) COMMENT '保护密码(加密后存储)',
    
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
    position, qq_weixin, cooperation_content, region,
    agency_name, agency_code, agency_address, agency_postal_code,
    agent_name, agent_phone, agent_fax, agent_mobile, agent_email,
    is_sensitive, protection_password,
    customer_level, status, remark, source, last_contact_time, create_by, update_by
) VALUES
('CUST001', 'ABC科技有限公司', '张伟', 2, '13812345678', 'zhangwei@abctech.com', '广东省深圳市南山区科技园',
 '518000', '0755-12345678', '91440300123456789X', '中国', 2,
 '总经理', 'QQ:123456789', '新品种保护申请、技术咨询服务', '广东省深圳市',
 '深圳知识产权代理有限公司', '91440300987654321Y', '深圳市福田区知识产权大厦', '518000',
 '王律师', '0755-87654321', '0755-87654322', '13987654321', 'wang@ipagency.com',
 1, '$2a$10$Ll/hmxK4s3sZJj8JNSQeBufrsBqHipuRJmzq.mNIy3sTx6HA5HJ3i',
 2, 1, '重要客户，需要重点关注', 1, '2024-01-15 10:30:00', 'admin', 'admin'),

('CUST002', 'XYZ咨询公司', '李娜', 2, '13987654321', 'lina@xyzconsult.com', '北京市朝阳区国贸大厦',
 '100000', '010-12345678', '91110000123456789X', '中国', 2,
 '市场总监', '微信:lina_xyz', '品种审定、市场推广合作', '北京市',
 '北京知识产权代理事务所', '91110000987654321Y', '北京市海淀区中关村大街', '100000',
 '刘律师', '010-87654321', '010-87654322', '13812345678', 'liu@bjip.com',
 0, NULL,
 1, 1, '咨询类客户', 2, '2024-01-14 15:20:00', 'admin', 'admin'),

('CUST003', '张三', '张三', 1, '13612345678', 'zhangsan@email.com', '上海市浦东新区陆家嘴',
 '200000', '', '310101199001011234', '中国', 1,
 '研究员', 'QQ:987654321', '个人新品种保护申请', '上海市',
 '', '', '', '',
 '', '', '', '', '',
 1, '$2a$10$Ll/hmxK4s3sZJj8JNSQeBufrsBqHipuRJmzq.mNIy3sTx6HA5HJ3i',
 1, 1, '个人发明者', 1, '2024-01-13 09:15:00', 'admin', 'admin'),

('CUST004', '中科院植物研究所', '李教授', 2, '010-12345678', 'li@cas.cn', '北京市海淀区中关村南大街',
 '100000', '010-12345679', '12100000400000000X', '中国', 3,
 '课题负责人', 'WeChat:prof_li', '国家级课题合作及技术评审', '北京市',
 '中科院知识产权代理中心', '91110000123456789X', '北京市海淀区中关村', '100000',
 '陈博士', '010-87654321', '010-87654322', '13712345678', 'chen@casip.com',
 1, '$2a$10$Ll/hmxK4s3sZJj8JNSQeBufrsBqHipuRJmzq.mNIy3sTx6HA5HJ3i',
 3, 1, '国家级科研院所，重点客户', 3, '2024-01-10 14:45:00', 'admin', 'admin'),

('CUST005', '钱七', '钱七', 2, '13800138005', 'qianqi@company.com', '杭州市西湖区',
 '310000', '0571-12345678', '91330100123456789X', '中国', 2,
 '项目经理', 'WeChat:qianqi', '企业技术服务与新品种孵化', '浙江省杭州市',
 '杭州知识产权代理有限公司', '91330100987654321Y', '杭州市西湖区文三路', '310000',
 '孙律师', '0571-87654321', '0571-87654322', '13812345678', 'sun@hzip.com',
 0, NULL,
 2, 1, '企业VIP客户', 2, '2024-01-12 11:30:00', 'admin', 'admin'),

('CUST006', 'ABC科技有限公司', '张伟', 2, '13812345678', 'zhangwei@abctech.com', '广东省深圳市南山区科技园',
 '518000', '0755-12345678', '91440300123456789X', '中国', 2,
 '产品总监', 'QQ:22334455', '海外专利布局与培训', '广东省深圳市',
 '深圳知识产权代理有限公司', '91440300987654321Y', '深圳市福田区知识产权大厦', '518000',
 '王律师', '0755-87654321', '0755-87654322', '13987654321', 'wang@ipagency.com',
 1, '$2a$10$Ll/hmxK4s3sZJj8JNSQeBufrsBqHipuRJmzq.mNIy3sTx6HA5HJ3i',
 2, 1, '重点跟进客户', 1, '2024-01-08 09:30:00', 'admin', 'admin'),

('CUST007', 'XYZ咨询公司', '李娜', 2, '13987654321', 'lina@xyzconsult.com', '北京市朝阳区国贸大厦',
 '100000', '010-12345678', '91110000123456789X', '中国', 2,
 '运营副总裁', 'WeChat:consult_lina', '市场扩展与合规支持', '北京市',
 '北京知识产权代理事务所', '91110000987654321Y', '北京市海淀区中关村大街', '100000',
 '刘律师', '010-87654321', '010-87654322', '13812345678', 'liu@bjip.com',
 0, NULL,
 1, 1, '咨询类客户', 2, '2024-01-05 11:15:00', 'admin', 'admin'),

('CUST008', '张三', '张三', 1, '13612345678', 'zhangsan@email.com', '上海市浦东新区陆家嘴',
 '200000', '', '310101199001011234', '中国', 1,
 '研究员', 'QQ:56789012', '个人育种项目', '上海市',
 '', '', '', '',
 '', '', '', '', '',
 1, '$2a$10$Ll/hmxK4s3sZJj8JNSQeBufrsBqHipuRJmzq.mNIy3sTx6HA5HJ3i',
 1, 1, '个人发明者', 1, '2024-01-03 10:00:00', 'admin', 'admin'),

('CUST009', '中科院植物研究所', '李教授', 2, '010-12345678', 'li@cas.cn', '北京市海淀区中关村南大街',
 '100000', '010-12345679', '12100000400000000X', '中国', 3,
 '首席科学家', 'WeChat:cas_li', '品种审定+联合实验室', '北京市',
 '中科院知识产权代理中心', '91110000123456789X', '北京市海淀区中关村', '100000',
 '陈博士', '010-87654321', '010-87654322', '13712345678', 'chen@casip.com',
 1, '$2a$10$Ll/hmxK4s3sZJj8JNSQeBufrsBqHipuRJmzq.mNIy3sTx6HA5HJ3i',
 3, 1, '国家级科研院所，重点客户', 3, '2024-01-02 14:20:00', 'admin', 'admin'),

('CUST010', '钱七', '钱七', 2, '13800138005', 'qianqi@company.com', '杭州市西湖区',
 '310000', '0571-12345678', '91330100123456789X', '中国', 2,
 '副总经理', 'WeChat:qianqi_pro', '企业合作与培训', '浙江省杭州市',
 '杭州知识产权代理有限公司', '91330100987654321Y', '杭州市西湖区文三路', '310000',
 '孙律师', '0571-87654321', '0571-87654322', '13812345678', 'sun@hzip.com',
 0, NULL,
 2, 1, '企业VIP客户', 2, '2023-12-28 16:10:00', 'admin', 'admin');

-- 客户表结构补丁（兼容历史数据）
ALTER TABLE customer ADD COLUMN IF NOT EXISTS contact_person VARCHAR(100) COMMENT '联系人' AFTER customer_name;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS postal_code VARCHAR(10) COMMENT '邮政编码' AFTER address;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS fax VARCHAR(20) COMMENT '传真' AFTER postal_code;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS organization_code VARCHAR(50) COMMENT '机构代码或身份证号码' AFTER fax;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS nationality VARCHAR(50) COMMENT '国籍或所在国（地区）' AFTER organization_code;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS applicant_nature TINYINT COMMENT '申请人性质(1:个人,2:企业,3:科研院所,4:其他)' AFTER nationality;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS position VARCHAR(100) COMMENT '职务' AFTER applicant_nature;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS qq_weixin VARCHAR(100) COMMENT 'QQ/微信' AFTER position;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS cooperation_content TEXT COMMENT '合作内容' AFTER qq_weixin;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS region VARCHAR(50) COMMENT '地区' AFTER cooperation_content;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS agency_name VARCHAR(200) COMMENT '代理机构名称' AFTER region;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS agency_code VARCHAR(50) COMMENT '代理机构组织机构代码' AFTER agency_name;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS agency_address VARCHAR(500) COMMENT '代理机构地址' AFTER agency_code;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS agency_postal_code VARCHAR(10) COMMENT '代理机构邮政编码' AFTER agency_address;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS agent_name VARCHAR(100) COMMENT '代理人姓名' AFTER agency_postal_code;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS agent_phone VARCHAR(20) COMMENT '代理人电话' AFTER agent_name;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS agent_fax VARCHAR(20) COMMENT '代理人传真' AFTER agent_phone;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS agent_mobile VARCHAR(20) COMMENT '代理人手机' AFTER agent_fax;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS agent_email VARCHAR(100) COMMENT '代理人邮箱' AFTER agent_mobile;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS is_sensitive TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否敏感数据' AFTER agent_email;
ALTER TABLE customer ADD COLUMN IF NOT EXISTS protection_password VARCHAR(255) COMMENT '保护密码(加密)' AFTER is_sensitive;
ALTER TABLE customer ADD INDEX IF NOT EXISTS idx_contact_person (contact_person);
ALTER TABLE customer ADD INDEX IF NOT EXISTS idx_applicant_nature (applicant_nature);
ALTER TABLE customer ADD INDEX IF NOT EXISTS idx_agency_name (agency_name);
ALTER TABLE customer ADD INDEX IF NOT EXISTS idx_agent_name (agent_name);
ALTER TABLE customer ADD INDEX IF NOT EXISTS idx_position (position);
ALTER TABLE customer ADD INDEX IF NOT EXISTS idx_region (region);

UPDATE customer
SET contact_person = customer_name
WHERE contact_person IS NULL;

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

-- AI 聊天记录表
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
    INDEX idx_customer_id (customer_id),
    INDEX idx_message_type (message_type),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI聊天记录表';

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

-- 团队任务测试数据
INSERT INTO team_task (
    title, description, task_type, status, priority,
    creator_id, creator_name, assignee_id, assignee_name,
    start_time, deadline, progress, deleted, version,
    create_time, update_time, create_by, update_by
) VALUES
('客户需求分析', '分析客户需求，制定解决方案，完成需求文档编写', 1, 2, 3,
 1, '系统管理员', 1, '张三',
 '2024-01-15 09:00:00', '2024-01-30 18:00:00', 60, 0, 1,
 NOW(), NOW(), '系统管理员', '系统管理员'),
('系统性能优化', '优化系统性能，提升用户体验，包括数据库查询优化和前端加载优化', 2, 4, 2,
 1, '系统管理员', 2, '李四',
 '2024-01-10 09:00:00', '2024-01-25 18:00:00', 100, 0, 1,
 NOW(), NOW(), '系统管理员', '系统管理员'),
('客户产品培训', '为客户提供产品使用培训，包括功能演示和操作指导', 1, 2, 1,
 1, '系统管理员', 3, '王五',
 '2024-01-20 09:00:00', '2024-02-05 18:00:00', 30, 0, 1,
 NOW(), NOW(), '系统管理员', '系统管理员'),
('数据库查询优化', '优化数据库查询性能，提升系统响应速度，包括索引优化和查询语句优化', 2, 2, 3,
 1, '系统管理员', 4, '赵六',
 '2024-01-12 09:00:00', '2024-01-28 18:00:00', 90, 0, 1,
 NOW(), NOW(), '系统管理员', '系统管理员'),
('测试用例编写', '编写用户管理模块的测试用例，包括单元测试和集成测试', 2, 2, 2,
 1, '系统管理员', 5, '孙七',
 '2024-01-18 09:00:00', '2024-02-01 18:00:00', 50, 0, 1,
 NOW(), NOW(), '系统管理员', '系统管理员'),
('代码审查', '审查前端和后端代码，确保代码质量，包括代码规范和安全性检查', 2, 2, 2,
 1, '系统管理员', 7, '吴九',
 '2024-01-16 09:00:00', '2024-01-29 18:00:00', 75, 0, 1,
 NOW(), NOW(), '系统管理员', '系统管理员'),
('项目管理会议', '组织项目进度评审会议，协调团队工作，讨论项目进展和问题', 4, 4, 3,
 1, '系统管理员', 3, '王五',
 '2024-01-15 14:00:00', '2024-01-20 17:00:00', 100, 0, 1,
 NOW(), NOW(), '系统管理员', '系统管理员'),
('UI设计优化', '优化用户界面设计，提升用户体验，包括界面布局和交互优化', 2, 2, 2,
 1, '系统管理员', 2, '李四',
 '2024-01-22 09:00:00', '2024-02-03 18:00:00', 70, 0, 1,
 NOW(), NOW(), '系统管理员', '系统管理员'),
('紧急问题处理', '处理客户反馈的紧急问题，需要立即响应和解决', 3, 1, 4,
 1, '系统管理员', NULL, NULL,
 '2024-01-25 09:00:00', '2024-01-26 18:00:00', 0, 0, 1,
 NOW(), NOW(), '系统管理员', '系统管理员'),
('功能测试报告', '完成功能测试并提交测试报告，等待审核', 2, 3, 2,
 1, '系统管理员', 5, '孙七',
 '2024-01-10 09:00:00', '2024-01-24 18:00:00', 100, 0, 1,
 NOW(), NOW(), '系统管理员', '系统管理员');

-- 任务进度汇报表
CREATE TABLE IF NOT EXISTS task_progress_report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    task_id BIGINT COMMENT '任务ID',
    task_name VARCHAR(200) COMMENT '任务名称',
    employee_id BIGINT COMMENT '员工ID',
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
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
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

-- 社交平台假数据
-- 插入客户账号
INSERT INTO customer_account (customer_id, username, password, nickname, avatar, bio, phone, email, status) VALUES
(1, 'customer001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '张三', '/images/avatar1.jpg', '热爱生活，喜欢分享', '13800000001', 'zhangsan@example.com', 1),
(2, 'customer002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '李四', '/images/avatar2.jpg', '努力工作，享受生活', '13800000002', 'lisi@example.com', 1),
(3, 'customer003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '王五', '/images/avatar3.jpg', '追求梦想，永不放弃', '13800000003', 'wangwu@example.com', 1),
(4, 'alice', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '爱丽丝', '/images/avatar4.jpg', '热爱生活，喜欢分享美好时光', '13800000004', 'alice@example.com', 1),
(5, 'bob', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '鲍勃', '/images/avatar5.jpg', '技术控，喜欢探索新事物', '13800000005', 'bob@example.com', 1),
(6, 'charlie', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '查理', '/images/avatar6.jpg', '摄影师，记录生活的美好', '13800000006', 'charlie@example.com', 1);

-- 插入客户资料
INSERT INTO customer_profile (customer_account_id, customer_id, real_name, gender, location, company, position, interests) VALUES
(1, 1, '张三', 1, '北京市朝阳区', '科技公司', '产品经理', '["旅游", "摄影", "美食"]'),
(2, 2, '李四', 2, '上海市浦东新区', '互联网公司', '设计师', '["设计", "艺术", "音乐"]'),
(3, 3, '王五', 1, '深圳市南山区', '创业公司', 'CEO', '["创业", "投资", "运动"]'),
(4, 4, '爱丽丝', 2, '上海市徐汇区', '创意设计公司', 'UI设计师', '["设计", "艺术", "旅行", "摄影"]'),
(5, 5, '鲍勃', 1, '深圳市福田区', '科技公司', '前端工程师', '["编程", "游戏", "音乐", "运动"]'),
(6, 6, '查理', 1, '杭州市西湖区', '摄影工作室', '摄影师', '["摄影", "旅行", "美食", "电影"]');

-- 插入社交动态
INSERT INTO social_post (customer_account_id, customer_id, content, images, tags, post_type, like_count, comment_count) VALUES
(1, 1, '今天天气真好，适合出去走走！', '', '', 1, 5, 3),
(2, 2, '刚完成了一个新设计，很有成就感！', '', '', 1, 8, 2),
(3, 3, '创业路上虽然辛苦，但很有意义！', '', '', 1, 12, 5),
(1, 1, '今天去了一个新的咖啡厅，环境很棒！咖啡也很好喝，推荐给大家！', '/images/coffee1.jpg,/images/coffee2.jpg', '咖啡,美食,生活', 1, 8, 5),
(2, 2, '刚完成了一个新项目，很有成就感！感谢团队的支持！', '/images/project1.jpg', '工作,项目,团队', 1, 12, 8),
(3, 3, '周末去爬山了，山顶的风景真美！', '/images/mountain1.jpg,/images/mountain2.jpg,/images/mountain3.jpg', '爬山,风景,周末', 1, 15, 6),
(4, 4, '分享一些设计灵感，希望能给大家带来启发！', '/images/design1.jpg,/images/design2.jpg', '设计,灵感,创意', 1, 20, 10),
(5, 5, '今天学习了一个新的前端框架，感觉很不错！', '', '编程,学习,前端', 1, 6, 3),
(6, 6, '拍了一组人像照片，模特表现很棒！', '/images/portrait1.jpg,/images/portrait2.jpg', '摄影,人像,艺术', 1, 18, 7),
(1, 1, '最近在读一本好书，推荐给大家！', '/images/book1.jpg', '读书,推荐,学习', 1, 9, 4),
(2, 2, '今天天气真好，适合出去走走！', '/images/weather1.jpg', '天气,生活,心情', 1, 7, 2),
(3, 3, '尝试做了一道新菜，味道还不错！', '/images/food1.jpg', '美食,烹饪,生活', 1, 11, 5),
(4, 4, '参加了一个设计展览，收获很多！', '/images/exhibition1.jpg,/images/exhibition2.jpg', '设计,展览,学习', 1, 14, 6);

-- 插入社交评论
INSERT INTO social_comment (post_id, customer_account_id, customer_id, content, images, tags, like_count) VALUES
(1, 2, 2, '确实不错，我也想去！', '', '', 2),
(1, 3, 3, '一起吧！', '', '', 1),
(2, 1, 1, '设计得很棒！', '', '', 3),
(3, 1, 1, '加油！支持你！', '', '', 4),
(3, 2, 2, '创业精神值得学习！', '', '', 2),
(4, 2, 2, '看起来很不错！在哪里呀？', '', '咖啡,询问', 2),
(4, 3, 3, '我也想去试试！', '', '咖啡,兴趣', 1),
(4, 4, 4, '环境确实很棒，我也去过！', '/images/coffee3.jpg', '咖啡,环境', 3),
(5, 1, 1, '恭喜恭喜！项目很成功！', '', '恭喜,项目', 4),
(5, 3, 3, '团队合作很重要！', '', '团队,合作', 2),
(5, 4, 4, '期待下次合作！', '', '合作,期待', 1),
(6, 1, 1, '风景真美！我也想去爬山！', '', '爬山,风景', 3),
(6, 2, 2, '爬山对身体很好！', '', '爬山,健康', 2),
(6, 4, 4, '照片拍得真好！', '', '摄影,赞美', 1),
(7, 1, 1, '设计很有创意！', '', '设计,创意', 2),
(7, 2, 2, '学到了很多！', '', '学习,设计', 1),
(7, 3, 3, '感谢分享！', '', '感谢,分享', 1),
(8, 1, 1, '什么框架呀？', '', '框架,询问', 1),
(8, 3, 3, '我也在学习前端！', '', '前端,学习', 1),
(9, 1, 1, '照片拍得真棒！', '', '摄影,赞美', 2),
(9, 2, 2, '模特很漂亮！', '', '模特,赞美', 1),
(9, 3, 3, '技术很好！', '', '技术,赞美', 1),
(10, 2, 2, '什么书呀？', '', '书籍,询问', 1),
(10, 3, 3, '我也喜欢读书！', '', '读书,兴趣', 1),
(11, 1, 1, '确实天气很好！', '', '天气,赞同', 1),
(11, 3, 3, '适合出去走走！', '', '天气,活动', 1),
(12, 1, 1, '看起来很好吃！', '', '美食,赞美', 2),
(12, 2, 2, '我也想做！', '', '烹饪,兴趣', 1),
(13, 1, 1, '展览怎么样？', '', '展览,询问', 1),
(13, 2, 2, '我也想去看看！', '', '展览,兴趣', 1);

-- 插入社交点赞
INSERT INTO social_like (target_type, target_id, customer_account_id, customer_id) VALUES
-- 动态点赞
(1, 1, 2, 2), (1, 1, 3, 3), (1, 1, 4, 4), (1, 1, 5, 5), (1, 1, 6, 6),
(1, 2, 1, 1), (1, 2, 3, 3), (1, 2, 4, 4), (1, 2, 5, 5), (1, 2, 6, 6),
(1, 3, 1, 1), (1, 3, 2, 2), (1, 3, 4, 4), (1, 3, 5, 5), (1, 3, 6, 6),
(1, 4, 1, 1), (1, 4, 2, 2), (1, 4, 3, 3), (1, 4, 5, 5), (1, 4, 6, 6),
(1, 5, 1, 1), (1, 5, 2, 2), (1, 5, 3, 3), (1, 5, 4, 4), (1, 5, 6, 6),
(1, 6, 1, 1), (1, 6, 2, 2), (1, 6, 3, 3), (1, 6, 4, 4), (1, 6, 5, 5),
(1, 7, 1, 1), (1, 7, 2, 2), (1, 7, 3, 3), (1, 7, 4, 4), (1, 7, 5, 5),
(1, 8, 1, 1), (1, 8, 2, 2), (1, 8, 3, 3), (1, 8, 4, 4), (1, 8, 5, 5),
(1, 9, 1, 1), (1, 9, 2, 2), (1, 9, 3, 3), (1, 9, 4, 4), (1, 9, 5, 5),
(1, 10, 1, 1), (1, 10, 2, 2), (1, 10, 3, 3), (1, 10, 4, 4), (1, 10, 5, 5),
(1, 11, 1, 1), (1, 11, 2, 2), (1, 11, 3, 3), (1, 11, 4, 4), (1, 11, 5, 5),
(1, 12, 1, 1), (1, 12, 2, 2), (1, 12, 3, 3), (1, 12, 4, 4), (1, 12, 5, 5),
(1, 13, 1, 1), (1, 13, 2, 2), (1, 13, 3, 3), (1, 13, 4, 4), (1, 13, 5, 5),
-- 评论点赞
(2, 1, 1, 1), (2, 1, 3, 3), (2, 1, 4, 4),
(2, 2, 1, 1), (2, 2, 2, 2), (2, 2, 4, 4),
(2, 3, 1, 1), (2, 3, 2, 2), (2, 3, 3, 3),
(2, 4, 2, 2), (2, 4, 3, 3), (2, 4, 5, 5),
(2, 5, 1, 1), (2, 5, 3, 3), (2, 5, 4, 4),
(2, 6, 1, 1), (2, 6, 2, 2), (2, 6, 3, 3),
(2, 7, 2, 2), (2, 7, 3, 3), (2, 7, 4, 4),
(2, 8, 1, 1), (2, 8, 2, 2), (2, 8, 3, 3),
(2, 9, 1, 1), (2, 9, 2, 2), (2, 9, 3, 3),
(2, 10, 1, 1), (2, 10, 2, 2), (2, 10, 3, 3);

-- 消息表
CREATE TABLE IF NOT EXISTS message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '消息标题',
    content TEXT COMMENT '消息内容',
    message_type TINYINT NOT NULL COMMENT '消息类型(1:系统通知,2:业务提醒,3:任务通知,4:客户消息)',
    importance TINYINT DEFAULT 1 COMMENT '消息重要性(1:普通,2:重要,3:紧急)',
    business_type VARCHAR(50) COMMENT '关联业务类型(customer,communication,task,ai_analysis,team_task,system)',
    business_id BIGINT COMMENT '关联业务ID',
    business_name VARCHAR(200) COMMENT '关联业务名称',
    receiver_id BIGINT NOT NULL COMMENT '接收用户ID',
    receiver_name VARCHAR(100) COMMENT '接收用户姓名',
    sender_id BIGINT COMMENT '发送用户ID(系统消息为null)',
    sender_name VARCHAR(100) COMMENT '发送用户姓名',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读(0:未读,1:已读)',
    read_time DATETIME COMMENT '已读时间',
    is_processed TINYINT DEFAULT 0 COMMENT '是否已处理(0:未处理,1:已处理)',
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
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
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
