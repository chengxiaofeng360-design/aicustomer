-- AI客户管理系统数据库初始化脚本 (MySQL版本)

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
    
    -- 新增字段
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
    version INT NOT NULL DEFAULT 1 COMMENT '版本号'
);

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
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    INDEX idx_customer_id (customer_id),
    INDEX idx_tag_name (tag_name),
    INDEX idx_tag_type (tag_type),
    INDEX idx_deleted (deleted)
) COMMENT='客户标签表';

-- 客户详细信息表
CREATE TABLE IF NOT EXISTS customer_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    customer_id BIGINT NOT NULL UNIQUE COMMENT '客户ID',
    customer_code VARCHAR(50) COMMENT '客户编号',
    industry_category VARCHAR(100) COMMENT '行业分类',
    business_type VARCHAR(100) COMMENT '业务类型',
    importance TINYINT COMMENT '重要程度(1:一般,2:重要,3:非常重要)',
    tags VARCHAR(500) COMMENT '客户标签(多个标签用逗号分隔)',
    value_score INT COMMENT '客户价值评分(0-100分)',
    lifecycle_stage TINYINT COMMENT '生命周期阶段(1:潜在,2:接触,3:合作,4:维护,5:流失)',
    total_amount DECIMAL(18,2) DEFAULT 0 COMMENT '累计消费金额',
    cooperation_count INT DEFAULT 0 COMMENT '合作次数',
    credit_level TINYINT COMMENT '信用评估等级(1:A,2:B,3:C,4:D)',
    satisfaction_score TINYINT COMMENT '客户满意度评分(1-5分)',
    completeness INT DEFAULT 0 COMMENT '信息完整度(0-100%)',
    customer_create_time TIMESTAMP COMMENT '客户创建时间',
    first_cooperation_time TIMESTAMP COMMENT '首次合作时间',
    last_cooperation_time TIMESTAMP COMMENT '最后合作时间',
    churn_time TIMESTAMP COMMENT '客户流失时间',
    churn_reason VARCHAR(500) COMMENT '流失原因',
    company_size TINYINT COMMENT '公司规模(1:微型,2:小型,3:中型,4:大型)',
    annual_revenue DECIMAL(18,2) COMMENT '年营业额',
    employee_count INT COMMENT '员工数量',
    primary_contact VARCHAR(100) COMMENT '主要联系人',
    decision_maker VARCHAR(100) COMMENT '决策人',
    purchase_cycle TINYINT COMMENT '采购周期(1:月度,2:季度,3:半年度,4:年度)',
    budget_range VARCHAR(200) COMMENT '预算范围',
    competitors VARCHAR(500) COMMENT '竞争对手',
    special_requirements TEXT COMMENT '特殊要求',
    preferences TEXT COMMENT '客户偏好',
    risk_level TINYINT COMMENT '风险等级(1:低,2:中,3:高)',
    risk_description VARCHAR(500) COMMENT '风险描述',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)',
    version INT NOT NULL DEFAULT 1 COMMENT '版本号',
    INDEX idx_customer_id (customer_id),
    INDEX idx_customer_code (customer_code),
    INDEX idx_lifecycle_stage (lifecycle_stage),
    INDEX idx_deleted (deleted)
) COMMENT='客户详细信息表';

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
    postal_code, fax, organization_code, nationality,
    agency_name, agency_code, agency_address, agency_postal_code,
    agent_name, agent_phone, agent_fax, agent_mobile, agent_email,
    is_sensitive, protection_password,
    position, qq_weixin, cooperation_content,
    customer_level, status, remark, source, last_contact_time, create_by, update_by
) VALUES
('CUST001', 'ABC科技有限公司', '张伟', 2, '13812345678', 'zhangwei@abctech.com', '广东省深圳市南山区科技园',
 '518000', '0755-12345678', '91440300123456789X', '中国',
 '深圳知识产权代理有限公司', '91440300987654321Y', '深圳市福田区知识产权大厦', '518000',
 '王律师', '0755-87654321', '0755-87654322', '13987654321', 'wang@ipagency.com',
 TRUE, '$2a$10$Ll/hmxK4s3sZJj8JNSQeBufrsBqHipuRJmzq.mNIy3sTx6HA5HJ3i',
 '总经理', 'QQ:123456789', '新品种保护申请、技术咨询服务',
 2, 1, '重要客户，需要重点关注', 1, '2024-01-15 10:30:00', 'admin', 'admin'),

('CUST002', 'XYZ咨询公司', '李娜', 2, '13987654321', 'lina@xyzconsult.com', '北京市朝阳区国贸大厦',
 '100000', '010-12345678', '91110000123456789X', '中国',
 '北京知识产权代理事务所', '91110000987654321Y', '北京市海淀区中关村大街', '100000',
 '刘律师', '010-87654321', '010-87654322', '13812345678', 'liu@bjip.com',
 FALSE, NULL,
 '市场总监', '微信:lina_xyz', '品种审定、市场推广合作',
 1, 1, '咨询类客户', 2, '2024-01-14 15:20:00', 'admin', 'admin'),

('CUST003', '张三', '张三', 1, '13612345678', 'zhangsan@email.com', '上海市浦东新区陆家嘴',
 '200000', '', '310101199001011234', '中国',
 '', '', '', '',
 '', '', '', '', '',
 TRUE, '$2a$10$Ll/hmxK4s3sZJj8JNSQeBufrsBqHipuRJmzq.mNIy3sTx6HA5HJ3i',
 '研究员', 'QQ:987654321', '个人新品种保护申请',
 1, 1, '个人发明者', 1, '2024-01-13 09:15:00', 'admin', 'admin');

-- 社交平台相关表
-- 客户账号表（扩展客户信息，支持社交功能）
CREATE TABLE IF NOT EXISTS customer_account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(100),
    avatar VARCHAR(500),
    bio TEXT,
    phone VARCHAR(20),
    email VARCHAR(100),
    status TINYINT NOT NULL DEFAULT 1,
    last_login_time TIMESTAMP,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 社交动态表
CREATE TABLE IF NOT EXISTS social_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_account_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    images TEXT,
    tags TEXT,
    post_type TINYINT NOT NULL DEFAULT 1,
    visibility TINYINT NOT NULL DEFAULT 1,
    like_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    share_count INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 动态评论表
CREATE TABLE IF NOT EXISTS social_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    customer_account_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    parent_id BIGINT,
    content TEXT NOT NULL,
    images TEXT,
    tags TEXT,
    like_count INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 点赞表
CREATE TABLE IF NOT EXISTS social_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    target_type TINYINT NOT NULL,
    target_id BIGINT NOT NULL,
    customer_account_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_like UNIQUE (target_type, target_id, customer_account_id)
);

-- 客户个人资料表
CREATE TABLE IF NOT EXISTS customer_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_account_id BIGINT NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    real_name VARCHAR(100),
    gender TINYINT,
    birthday DATE,
    location VARCHAR(200),
    company VARCHAR(200),
    position VARCHAR(100),
    website VARCHAR(500),
    interests TEXT,
    background_image VARCHAR(500),
    privacy_settings TEXT,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 插入社交平台假数据
INSERT INTO customer_account (customer_id, username, password, nickname, avatar, bio, phone, email, status) VALUES
(1, 'customer001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '张三', '/images/avatar1.jpg', '热爱生活，喜欢分享', '13800000001', 'zhangsan@example.com', 1),
(2, 'customer002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '李四', '/images/avatar2.jpg', '努力工作，享受生活', '13800000002', 'lisi@example.com', 1),
(3, 'customer003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '王五', '/images/avatar3.jpg', '追求梦想，永不放弃', '13800000003', 'wangwu@example.com', 1),
(4, 'alice', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '爱丽丝', '/images/avatar4.jpg', '热爱生活，喜欢分享美好时光', '13800000004', 'alice@example.com', 1),
(5, 'bob', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '鲍勃', '/images/avatar5.jpg', '技术控，喜欢探索新事物', '13800000005', 'bob@example.com', 1),
(6, 'charlie', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '查理', '/images/avatar6.jpg', '摄影师，记录生活的美好', '13800000006', 'charlie@example.com', 1);

INSERT INTO customer_profile (customer_account_id, customer_id, real_name, gender, location, company, position, interests) VALUES
(1, 1, '张三', 1, '北京市朝阳区', '科技公司', '产品经理', '["旅游", "摄影", "美食"]'),
(2, 2, '李四', 2, '上海市浦东新区', '互联网公司', '设计师', '["设计", "艺术", "音乐"]'),
(3, 3, '王五', 1, '深圳市南山区', '创业公司', 'CEO', '["创业", "投资", "运动"]'),
(4, 4, '爱丽丝', 2, '上海市徐汇区', '创意设计公司', 'UI设计师', '["设计", "艺术", "旅行", "摄影"]'),
(5, 5, '鲍勃', 1, '深圳市福田区', '科技公司', '前端工程师', '["编程", "游戏", "音乐", "运动"]'),
(6, 6, '查理', 1, '杭州市西湖区', '摄影工作室', '摄影师', '["摄影", "旅行", "美食", "电影"]');

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

INSERT INTO social_like (target_type, target_id, customer_account_id, customer_id) VALUES
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






