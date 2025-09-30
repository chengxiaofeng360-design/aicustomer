# 数据库迁移指南

## 植物新品种保护申请字段迁移

本指南说明如何将现有的客户管理系统数据库升级，添加植物新品种保护申请相关的字段。

### 迁移内容

新增以下字段到 `customer` 表：

#### 基本信息字段
- `contact_person` - 联系人
- `postal_code` - 邮政编码  
- `fax` - 传真

#### 申请人信息字段
- `organization_code` - 机构代码或身份证号码
- `nationality` - 国籍或所在国（地区）
- `applicant_nature` - 申请人性质（1:个人,2:企业,3:科研院所,4:其他）

#### 代理机构信息字段
- `agency_name` - 代理机构名称
- `agency_code` - 代理机构组织机构代码
- `agency_address` - 代理机构地址
- `agency_postal_code` - 代理机构邮政编码

#### 代理人信息字段
- `agent_name` - 代理人姓名
- `agent_phone` - 代理人电话
- `agent_fax` - 代理人传真
- `agent_mobile` - 代理人手机
- `agent_email` - 代理人邮箱

### 执行迁移

#### 方法1：使用迁移脚本（推荐）

1. 连接到MySQL数据库：
```bash
mysql -h bj-cynosdbmysql-grp-bbi2ygoo.sql.tencentcdb.com -P 26713 -u root -p
```

2. 执行迁移脚本：
```sql
source src/main/resources/sql/migration_add_plant_variety_fields.sql
```

#### 方法2：手动执行SQL

1. 连接到数据库
2. 执行以下SQL语句：

```sql
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
```

### 验证迁移

执行以下SQL验证字段是否添加成功：

```sql
-- 查看表结构
DESCRIBE customer;

-- 查看新增字段
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_COMMENT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'customer' 
AND TABLE_SCHEMA = 'ai_customer_db'
AND COLUMN_NAME IN ('contact_person', 'postal_code', 'fax', 'organization_code', 
                   'nationality', 'applicant_nature', 'agency_name', 'agency_code',
                   'agency_address', 'agency_postal_code', 'agent_name', 'agent_phone',
                   'agent_fax', 'agent_mobile', 'agent_email');
```

### 回滚迁移

如果需要回滚，可以执行以下SQL删除新增字段：

```sql
-- 删除新增字段
ALTER TABLE customer DROP COLUMN contact_person;
ALTER TABLE customer DROP COLUMN postal_code;
ALTER TABLE customer DROP COLUMN fax;
ALTER TABLE customer DROP COLUMN organization_code;
ALTER TABLE customer DROP COLUMN nationality;
ALTER TABLE customer DROP COLUMN applicant_nature;
ALTER TABLE customer DROP COLUMN agency_name;
ALTER TABLE customer DROP COLUMN agency_code;
ALTER TABLE customer DROP COLUMN agency_address;
ALTER TABLE customer DROP COLUMN agency_postal_code;
ALTER TABLE customer DROP COLUMN agent_name;
ALTER TABLE customer DROP COLUMN agent_phone;
ALTER TABLE customer DROP COLUMN agent_fax;
ALTER TABLE customer DROP COLUMN agent_mobile;
ALTER TABLE customer DROP COLUMN agent_email;
```

### 注意事项

1. 执行迁移前请备份数据库
2. 迁移过程中可能会锁表，建议在业务低峰期执行
3. 新增字段允许NULL值，不会影响现有数据
4. 现有客户数据会自动设置默认的申请人性质

### 测试数据

迁移完成后，系统会包含以下测试数据：

- **企业客户**：ABC科技有限公司、XYZ咨询公司
- **个人客户**：张三（个人发明者）
- **科研院所**：中科院植物研究所

这些数据包含了完整的植物新品种保护申请相关信息，可用于测试系统功能。



