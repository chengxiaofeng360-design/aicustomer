-- 植物新品种保护申请字段迁移脚本
-- 用于向现有customer表添加新字段

USE ai_customer_db;

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

-- 更新现有数据（为现有客户添加默认值）
UPDATE customer SET 
    contact_person = customer_name,
    applicant_nature = CASE 
        WHEN customer_type = 1 THEN 1  -- 个人客户
        WHEN customer_type = 2 THEN 2  -- 企业客户
        ELSE 1
    END
WHERE contact_person IS NULL;

-- 插入新的示例数据
INSERT INTO customer (
    customer_code, customer_name, contact_person, customer_type, phone, email, address,
    postal_code, fax, organization_code, nationality, applicant_nature,
    agency_name, agency_code, agency_address, agency_postal_code,
    agent_name, agent_phone, agent_fax, agent_mobile, agent_email,
    customer_level, status, remark, source, last_contact_time, create_by, update_by
) VALUES
('CUST006', 'ABC科技有限公司', '张伟', 2, '13812345678', 'zhangwei@abctech.com', '广东省深圳市南山区科技园',
 '518000', '0755-12345678', '91440300123456789X', '中国', 2,
 '深圳知识产权代理有限公司', '91440300987654321Y', '深圳市福田区知识产权大厦', '518000',
 '王律师', '0755-87654321', '0755-87654322', '13987654321', 'wang@ipagency.com',
 2, 1, '重要客户，需要重点关注', 1, NOW(), 'admin', 'admin'),

('CUST007', 'XYZ咨询公司', '李娜', 2, '13987654321', 'lina@xyzconsult.com', '北京市朝阳区国贸大厦',
 '100000', '010-12345678', '91110000123456789X', '中国', 2,
 '北京知识产权代理事务所', '91110000987654321Y', '北京市海淀区中关村大街', '100000',
 '刘律师', '010-87654321', '010-87654322', '13812345678', 'liu@bjip.com',
 1, 1, '咨询类客户', 2, NOW(), 'admin', 'admin'),

('CUST008', '张三', '张三', 1, '13612345678', 'zhangsan@email.com', '上海市浦东新区陆家嘴',
 '200000', '', '310101199001011234', '中国', 1,
 '', '', '', '',
 '', '', '', '', '',
 1, 1, '个人发明者', 1, NOW(), 'admin', 'admin'),

('CUST009', '中科院植物研究所', '李教授', 2, '010-12345678', 'li@cas.cn', '北京市海淀区中关村南大街',
 '100000', '010-12345679', '12100000400000000X', '中国', 3,
 '中科院知识产权代理中心', '91110000123456789X', '北京市海淀区中关村', '100000',
 '陈博士', '010-87654321', '010-87654322', '13712345678', 'chen@casip.com',
 3, 1, '国家级科研院所，重点客户', 3, NOW(), 'admin', 'admin'),

('CUST010', '钱七', '钱七', 2, '13800138005', 'qianqi@company.com', '杭州市西湖区',
 '310000', '0571-12345678', '91330100123456789X', '中国', 2,
 '杭州知识产权代理有限公司', '91330100987654321Y', '杭州市西湖区文三路', '310000',
 '孙律师', '0571-87654321', '0571-87654322', '13812345678', 'sun@hzip.com',
 2, 1, '企业VIP客户', 2, NOW(), 'admin', 'admin');

-- 显示更新后的表结构
DESCRIBE customer;



