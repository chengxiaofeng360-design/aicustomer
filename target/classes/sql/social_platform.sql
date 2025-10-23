-- 社交平台数据库表结构

-- 客户账号表（扩展客户信息，支持社交功能）
CREATE TABLE IF NOT EXISTS customer_account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    customer_id BIGINT NOT NULL COMMENT '关联客户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(100) COMMENT '昵称',
    avatar VARCHAR(500) COMMENT '头像URL',
    bio TEXT COMMENT '个人简介',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:正常,2:禁用)',
    last_login_time DATETIME COMMENT '最后登录时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_customer_id (customer_id),
    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户账号表';

-- 社交动态表
CREATE TABLE IF NOT EXISTS social_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    customer_account_id BIGINT NOT NULL COMMENT '发布者账号ID',
    customer_id BIGINT NOT NULL COMMENT '发布者客户ID',
    content TEXT NOT NULL COMMENT '动态内容',
    images TEXT COMMENT '图片URLs(JSON格式)',
    post_type TINYINT NOT NULL DEFAULT 1 COMMENT '动态类型(1:文字,2:图片,3:图文)',
    visibility TINYINT NOT NULL DEFAULT 1 COMMENT '可见性(1:公开,2:仅好友,3:私密)',
    like_count INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    comment_count INT NOT NULL DEFAULT 0 COMMENT '评论数',
    share_count INT NOT NULL DEFAULT 0 COMMENT '分享数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:正常,2:删除,3:隐藏)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_customer_account_id (customer_account_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社交动态表';

-- 动态评论表
CREATE TABLE IF NOT EXISTS social_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    post_id BIGINT NOT NULL COMMENT '动态ID',
    customer_account_id BIGINT NOT NULL COMMENT '评论者账号ID',
    customer_id BIGINT NOT NULL COMMENT '评论者客户ID',
    parent_id BIGINT COMMENT '父评论ID(回复评论时使用)',
    content TEXT NOT NULL COMMENT '评论内容',
    like_count INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:正常,2:删除)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_post_id (post_id),
    INDEX idx_customer_account_id (customer_account_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='动态评论表';

-- 点赞表
CREATE TABLE IF NOT EXISTS social_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    target_type TINYINT NOT NULL COMMENT '点赞目标类型(1:动态,2:评论)',
    target_id BIGINT NOT NULL COMMENT '目标ID',
    customer_account_id BIGINT NOT NULL COMMENT '点赞者账号ID',
    customer_id BIGINT NOT NULL COMMENT '点赞者客户ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY uk_like (target_type, target_id, customer_account_id),
    INDEX idx_customer_account_id (customer_account_id),
    INDEX idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞表';

-- 客户个人资料表
CREATE TABLE IF NOT EXISTS customer_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    customer_account_id BIGINT NOT NULL COMMENT '客户账号ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    real_name VARCHAR(100) COMMENT '真实姓名',
    gender TINYINT COMMENT '性别(1:男,2:女,3:其他)',
    birthday DATE COMMENT '生日',
    location VARCHAR(200) COMMENT '所在地',
    company VARCHAR(200) COMMENT '公司',
    position VARCHAR(100) COMMENT '职位',
    website VARCHAR(500) COMMENT '个人网站',
    interests TEXT COMMENT '兴趣爱好(JSON格式)',
    background_image VARCHAR(500) COMMENT '背景图片',
    privacy_settings TEXT COMMENT '隐私设置(JSON格式)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_customer_account_id (customer_account_id),
    INDEX idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户个人资料表';

-- 插入测试数据
INSERT INTO customer_account (customer_id, username, password, nickname, avatar, bio, phone, email, status) VALUES
(1, 'customer001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '张三', '/images/avatar1.jpg', '热爱生活，喜欢分享', '13800000001', 'zhangsan@example.com', 1),
(2, 'customer002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '李四', '/images/avatar2.jpg', '努力工作，享受生活', '13800000002', 'lisi@example.com', 1),
(3, 'customer003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '王五', '/images/avatar3.jpg', '追求梦想，永不放弃', '13800000003', 'wangwu@example.com', 1);

INSERT INTO customer_profile (customer_account_id, customer_id, real_name, gender, location, company, position, interests) VALUES
(1, 1, '张三', 1, '北京市朝阳区', '科技公司', '产品经理', '["旅游", "摄影", "美食"]'),
(2, 2, '李四', 2, '上海市浦东新区', '互联网公司', '设计师', '["设计", "艺术", "音乐"]'),
(3, 3, '王五', 1, '深圳市南山区', '创业公司', 'CEO', '["创业", "投资", "运动"]');

INSERT INTO social_post (customer_account_id, customer_id, content, post_type, like_count, comment_count) VALUES
(1, 1, '今天天气真好，适合出去走走！', 1, 5, 3),
(2, 2, '刚完成了一个新设计，很有成就感！', 1, 8, 2),
(3, 3, '创业路上虽然辛苦，但很有意义！', 1, 12, 5);

INSERT INTO social_comment (post_id, customer_account_id, customer_id, content, like_count) VALUES
(1, 2, 2, '确实不错，我也想去！', 2),
(1, 3, 3, '一起吧！', 1),
(2, 1, 1, '设计得很棒！', 3),
(3, 1, 1, '加油！支持你！', 4),
(3, 2, 2, '创业精神值得学习！', 2);

INSERT INTO social_like (target_type, target_id, customer_account_id, customer_id) VALUES
(1, 1, 2, 2),
(1, 1, 3, 3),
(1, 2, 1, 1),
(1, 2, 3, 3),
(1, 3, 1, 1),
(1, 3, 2, 2),
(2, 1, 3, 3),
(2, 3, 1, 1),
(2, 4, 2, 2);
