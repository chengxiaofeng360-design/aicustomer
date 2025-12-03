-- 完整更新team_task表結構
USE zqgl;

-- 添加缺失的工作相關字段
ALTER TABLE team_task
    ADD COLUMN IF NOT EXISTS work_mode TINYINT DEFAULT 1 COMMENT '工作模式(1:办公室,2:远程,3:混合)',
    ADD COLUMN IF NOT EXISTS work_duration INT DEFAULT 0 COMMENT '工作时长(分钟)',
    ADD COLUMN IF NOT EXISTS actual_start_time DATETIME COMMENT '实际开始时间',
    ADD COLUMN IF NOT EXISTS actual_end_time DATETIME COMMENT '实际结束时间',
    ADD COLUMN IF NOT EXISTS work_status TINYINT DEFAULT 1 COMMENT '工作状态(1:工作中,2:休息中,3:会议中,4:离线,5:异常)',
    ADD COLUMN IF NOT EXISTS last_active_time DATETIME COMMENT '最后活跃时间',
    ADD COLUMN IF NOT EXISTS work_location VARCHAR(200) COMMENT '工作地点',
    ADD COLUMN IF NOT EXISTS work_evidence VARCHAR(500) COMMENT '工作佐证',
    ADD COLUMN IF NOT EXISTS work_log TEXT COMMENT '工作日志',
    ADD COLUMN IF NOT EXISTS supervisor_id BIGINT COMMENT '监督人ID',
    ADD COLUMN IF NOT EXISTS supervisor_name VARCHAR(100) COMMENT '监督人姓名',
    ADD COLUMN IF NOT EXISTS need_supervision TINYINT DEFAULT 0 COMMENT '是否需要监督(0:否,1:是)',
    ADD COLUMN IF NOT EXISTS supervision_interval INT COMMENT '监督间隔(分钟)',
    ADD COLUMN IF NOT EXISTS last_supervision_time DATETIME COMMENT '最后监督时间',
    ADD COLUMN IF NOT EXISTS quality_score TINYINT COMMENT '质量评分(1-5)',
    ADD COLUMN IF NOT EXISTS efficiency_score TINYINT COMMENT '效率评分(1-5)',
    ADD COLUMN IF NOT EXISTS attitude_score TINYINT COMMENT '态度评分(1-5)',
    ADD COLUMN IF NOT EXISTS supervision_note TEXT COMMENT '监督备注';

-- 確保客戶字段存在
ALTER TABLE team_task
    ADD COLUMN IF NOT EXISTS customer_id BIGINT COMMENT '关联客户ID',
    ADD COLUMN IF NOT EXISTS customer_name VARCHAR(100) COMMENT '关联客户姓名';
