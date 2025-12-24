-- AI客户管理系统 初始数据填充脚本
-- 使用数据库
USE zqgl;

-- 1. 初始化系统配置 (system_config)
TRUNCATE TABLE system_config;
INSERT INTO system_config (config_key, config_value, config_type, description, config_group) VALUES
('business.type.1', '品种权申请客户', 'STRING', '业务类型 - 品种权申请', '业务管理'),
('business.type.2', '品种权转化推广客户', 'STRING', '业务类型 - 品种权转化', '业务管理'),
('business.type.3', '知识产权互补协作客户', 'STRING', '业务类型 - 协作', '业务管理'),
('business.type.4', '科普教育合作客户', 'STRING', '业务类型 - 科普', '业务管理'),
('business.type.5', '景观设计服务客户', 'STRING', '业务类型 - 景观设计', '业务管理'),
('business.type.6', '图书出版客户', 'STRING', '业务类型 - 图书出版', '业务管理');

-- 2. 初始化一些示例客户 (customer)
INSERT INTO customer (customer_code, customer_name, contact_person, customer_type, phone, region, business_type, status) VALUES
('C001', '北京林业大学', '张教授', 2, '13800000001', '北京', 1, 1),
('C002', '中国农业科学院', '李研究员', 2, '13800000002', '北京', 1, 1),
('C003', '南京林业大学', '王老师', 2, '13800000003', '江苏', 1, 1),
('C004', '个人育种者-赵先生', '赵六', 1, '1390000004', '山东', 1, 1),
('C005', '棕绿谷园林科技公司', '陈经理', 2, '1350000005', '广东', 2, 1),
('C006', '上海植物园', '周工程师', 2, '1360000006', '上海', 3, 1),
('C007', '四川大学林学院', '刘博士', 2, '1370000007', '四川', 1, 1),
('C008', '北京园林绿化局', '孙处长', 2, '1380000008', '北京', 5, 1),
('C009', '广州华南植物园', '吴研究员', 2, '1390000009', '广东', 1, 1),
('C010', '西安植物园', '郑园长', 2, '1350000010', '陕西', 4, 1);

-- 3. 初始化知识库文档 (knowledge_document & kb_document)
-- 注意：这些数据对应 uploads/knowledge/ 目录下的文件
INSERT INTO knowledge_document (title, content, file_name, file_type, file_path, category, status) VALUES
('植物新品种保护条例（2024版）', '这是条例的全文内容...', '12b744af-e439-4576-aa5b-f412f50502e4.docx', 'docx', 'uploads/knowledge/12b744af-e439-4576-aa5b-f412f50502e4.docx', '法律法规', 1),
('DUS测试技术指南 - 蔷薇属', '这是DUS测试的详细指南...', '52c32190-605c-4672-8685-5fd978c48189.docx', 'docx', 'uploads/knowledge/52c32190-605c-4672-8685-5fd978c48189.docx', '技术规范', 1);

INSERT INTO kb_document (title, content, file_name, file_type, file_path, category_id, is_active) VALUES
('品种权申请操作流程', '第一步：准备材料... 第二步：在线申请...', 'dcbbc63a-aae2-46e8-a7ca-4a322caec14e.txt', 'txt', 'uploads/knowledge/dcbbc63a-aae2-46e8-a7ca-4a322caec14e.txt', 1, 1),
('品种权转让协议模板', '甲方、乙方...', 'e5abf78e-9490-47b6-98d1-cf7594fce753.txt', 'txt', 'uploads/knowledge/e5abf78e-9490-47b6-98d1-cf7594fce753.txt', 1, 1),
('常见侵权案例分析', '案例一：某育种者诉某公司...', 'e6dc5341-f948-4bd1-8cb5-4295a381e1ca.txt', 'txt', 'uploads/knowledge/e6dc5341-f948-4bd1-8cb5-4295a381e1ca.txt', 5, 1);

-- 4. 初始化 FAQ 数据 (部分摘自 import_faq_data.sql)
INSERT INTO faq_qa (question, answer, keywords, category, status) VALUES
('什么是植物新品种？', '植物新品种指经过人工培育或对发现的野生植物加以开发，具备新颖性、特异性、一致性、稳定性，并有适当命名的植物品种。', '植物新品种,新品种', '名词解释', 1),
('什么是DUS测试？', 'DUS测试是授予植物新品种权的实质性条件，指对申请品种的特异性(D)、一致性(U)、稳定性(S)进行的栽培鉴定试验。', 'DUS测试,特异性', '名词解释', 1),
('保护期限是多久？', '品种权的保护期限，自授权公告之日起，木本、藤本植物为25年，其他植物为20年。', '保护期限,25年,20年', '名词解释', 1);
