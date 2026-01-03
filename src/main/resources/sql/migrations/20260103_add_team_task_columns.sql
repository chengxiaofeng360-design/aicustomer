-- Add missing columns to team_task table
-- Use separate statements to ensure partially successful execution if some columns exist (with continue-on-error)

-- work_mode
ALTER TABLE team_task ADD COLUMN work_mode TINYINT DEFAULT 1 COMMENT '工作模式(1:办公室,2:远程,3:混合)';

-- work_duration
ALTER TABLE team_task ADD COLUMN work_duration INT DEFAULT 0 COMMENT '工作时长(分钟)';

-- actual_start_time
ALTER TABLE team_task ADD COLUMN actual_start_time DATETIME COMMENT '实际开始时间';

-- actual_end_time
ALTER TABLE team_task ADD COLUMN actual_end_time DATETIME COMMENT '实际结束时间';

-- work_status
ALTER TABLE team_task ADD COLUMN work_status TINYINT DEFAULT 1 COMMENT '工作状态(1:工作中,2:休息中,3:会议中,4:离线,5:异常)';

-- last_active_time
ALTER TABLE team_task ADD COLUMN last_active_time DATETIME COMMENT '最后活跃时间';

-- work_location
ALTER TABLE team_task ADD COLUMN work_location VARCHAR(200) COMMENT '工作地点';

-- work_evidence
ALTER TABLE team_task ADD COLUMN work_evidence VARCHAR(500) COMMENT '工作佐证';

-- work_log
ALTER TABLE team_task ADD COLUMN work_log TEXT COMMENT '工作日志';

-- supervisor_id
ALTER TABLE team_task ADD COLUMN supervisor_id BIGINT COMMENT '监督人ID';

-- supervisor_name
ALTER TABLE team_task ADD COLUMN supervisor_name VARCHAR(100) COMMENT '监督人姓名';

-- need_supervision
ALTER TABLE team_task ADD COLUMN need_supervision TINYINT DEFAULT 0 COMMENT '是否需要监督(0:否,1:是)';

-- supervision_interval
ALTER TABLE team_task ADD COLUMN supervision_interval INT COMMENT '监督间隔(分钟)';

-- last_supervision_time
ALTER TABLE team_task ADD COLUMN last_supervision_time DATETIME COMMENT '最后监督时间';

-- quality_score
ALTER TABLE team_task ADD COLUMN quality_score TINYINT COMMENT '质量评分(1-5)';

-- efficiency_score
ALTER TABLE team_task ADD COLUMN efficiency_score TINYINT COMMENT '效率评分(1-5)';

-- attitude_score
ALTER TABLE team_task ADD COLUMN attitude_score TINYINT COMMENT '态度评分(1-5)';

-- supervision_note
ALTER TABLE team_task ADD COLUMN supervision_note TEXT COMMENT '监督备注';
