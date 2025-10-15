-- AI客户管理系统数据库初始化脚本 (H2版本)

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
    last_contact_time TIMESTAMP COMMENT '最后联系时间',
    remark TEXT COMMENT '备注',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号'
);

-- 用户表（用于系统管理）
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(100) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号码',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:正常,2:禁用)',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号'
);

-- 插入默认管理员用户（密码：admin123）
INSERT INTO sys_user (username, password, real_name, email, phone, status, create_by, update_by) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '系统管理员', 'admin@example.com', '13800000000', 1, 'system', 'system');

-- 插入示例客户数据
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
 1, 1, '个人发明者', 1, '2024-01-13 09:15:00', 'admin', 'admin');




