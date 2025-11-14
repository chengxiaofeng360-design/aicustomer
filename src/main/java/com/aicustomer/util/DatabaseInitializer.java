package com.aicustomer.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * 数据库初始化工具类
 * 用于自动创建数据库表结构
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Slf4j
@Component
public class DatabaseInitializer {
    
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;
    
    @PostConstruct
    public void init() {
        if (jdbcTemplate == null) {
            log.warn("JdbcTemplate未配置，跳过数据库初始化");
            return;
        }
        
        try {
            // 检查主要表是否存在
            boolean customerTableExists = checkTableExists("customer");
            boolean communicationTableExists = checkTableExists("communication_record");
            boolean teamTaskTableExists = checkTableExists("team_task");
            boolean taskProgressReportTableExists = checkTableExists("task_progress_report");
            
            if (!customerTableExists) {
                log.info("检测到customer表不存在，开始初始化数据库表结构...");
                initializeDatabase();
            } else {
                log.info("数据库表已存在，检查并更新表结构...");
                updateTableStructure();
            }
            
            // 检查并创建communication_record表（如果不存在）
            if (!communicationTableExists) {
                log.info("检测到communication_record表不存在，开始创建...");
                createCommunicationRecordTable();
            } else {
                log.debug("communication_record表已存在，跳过创建");
            }
            
            // 检查并创建team_task表（如果不存在）
            if (!teamTaskTableExists) {
                log.info("检测到team_task表不存在，开始创建...");
                createTeamTaskTable();
            } else {
                log.debug("team_task表已存在，跳过创建");
            }
            
            // 检查并创建task_progress_report表（如果不存在）
            if (!taskProgressReportTableExists) {
                log.info("检测到task_progress_report表不存在，开始创建...");
                createTaskProgressReportTable();
            } else {
                log.debug("task_progress_report表已存在，跳过创建");
            }
        } catch (Exception e) {
            log.error("数据库初始化失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 检查表是否存在
     */
    private boolean checkTableExists(String tableName) {
        try {
            String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
            return count != null && count > 0;
        } catch (Exception e) {
            log.debug("检查表是否存在时出错: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 初始化数据库
     */
    private void initializeDatabase() {
        try {
            // 读取schema.sql文件
            ClassPathResource resource = new ClassPathResource("schema.sql");
            String sqlScript = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            ).lines().collect(Collectors.joining("\n"));
            
            // 移除注释和空行
            sqlScript = removeComments(sqlScript);
            
            // 按分号分割SQL语句，但要注意字符串中的分号
            java.util.List<String> statements = new java.util.ArrayList<>();
            StringBuilder currentStatement = new StringBuilder();
            boolean inString = false;
            char stringChar = 0;
            
            for (char c : sqlScript.toCharArray()) {
                if ((c == '\'' || c == '"') && (currentStatement.length() == 0 || currentStatement.charAt(currentStatement.length() - 1) != '\\')) {
                    if (!inString) {
                        inString = true;
                        stringChar = c;
                    } else if (c == stringChar) {
                        inString = false;
                    }
                }
                
                currentStatement.append(c);
                
                if (!inString && c == ';') {
                    String stmt = currentStatement.toString().trim();
                    if (!stmt.isEmpty() && stmt.length() > 5) { // 忽略太短的语句
                        statements.add(stmt);
                    }
                    currentStatement = new StringBuilder();
                }
            }
            
            // 执行SQL语句
            int successCount = 0;
            int skipCount = 0;
            for (String statement : statements) {
                String trimmed = statement.trim();
                if (trimmed.isEmpty() || trimmed.length() < 10) {
                    continue;
                }
                
                try {
                    // 执行SQL
                    jdbcTemplate.execute(trimmed);
                    successCount++;
                    String preview = trimmed.length() > 60 ? trimmed.substring(0, 60) + "..." : trimmed;
                    log.debug("执行SQL成功: {}", preview);
                } catch (Exception e) {
                    String errorMsg = e.getMessage();
                    // 忽略已存在的表/索引错误
                    if (errorMsg != null && (
                        errorMsg.contains("already exists") || 
                        errorMsg.contains("Duplicate") ||
                        errorMsg.contains("Table") && errorMsg.contains("already exists"))) {
                        skipCount++;
                        log.debug("表/索引已存在，跳过: {}", trimmed.substring(0, Math.min(50, trimmed.length())));
                    } else {
                        log.warn("执行SQL失败: {} - {}", trimmed.substring(0, Math.min(50, trimmed.length())), errorMsg);
                    }
                }
            }
            
            log.info("数据库初始化完成，成功执行 {} 条SQL语句，跳过 {} 条（已存在）", successCount, skipCount);
        } catch (Exception e) {
            log.error("读取或执行schema.sql失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 移除SQL注释
     */
    private String removeComments(String sql) {
        // 移除多行注释
        sql = sql.replaceAll("/\\*[\\s\\S]*?\\*/", "");
        // 移除单行注释（但保留字符串中的内容）
        StringBuilder result = new StringBuilder();
        String[] lines = sql.split("\n");
        for (String line : lines) {
            int commentIndex = line.indexOf("--");
            if (commentIndex >= 0) {
                // 检查是否在字符串中
                String beforeComment = line.substring(0, commentIndex);
                int singleQuoteCount = countChar(beforeComment, '\'');
                int doubleQuoteCount = countChar(beforeComment, '"');
                if (singleQuoteCount % 2 == 0 && doubleQuoteCount % 2 == 0) {
                    // 不在字符串中，移除注释
                    line = line.substring(0, commentIndex);
                }
            }
            result.append(line).append("\n");
        }
        return result.toString();
    }
    
    /**
     * 统计字符出现次数
     */
    private int countChar(String str, char ch) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (c == ch) count++;
        }
        return count;
    }
    
    /**
     * 更新表结构：添加缺失的字段
     */
    private void updateTableStructure() {
        try {
            log.info("开始检查并更新customer表结构...");
            
            // 检查并添加position字段
            if (!checkColumnExists("customer", "position")) {
                log.info("添加position字段到customer表...");
                try {
                    jdbcTemplate.execute("ALTER TABLE customer ADD COLUMN position VARCHAR(100) COMMENT '职务' AFTER nationality");
                    log.info("✅ position字段添加成功");
                } catch (Exception e) {
                    log.error("❌ 添加position字段失败: {}", e.getMessage());
                }
            } else {
                log.debug("position字段已存在，跳过");
            }
            
            // 检查并添加qq_weixin字段
            if (!checkColumnExists("customer", "qq_weixin")) {
                log.info("添加qq_weixin字段到customer表...");
                try {
                    jdbcTemplate.execute("ALTER TABLE customer ADD COLUMN qq_weixin VARCHAR(100) COMMENT 'QQ/微信' AFTER position");
                    log.info("✅ qq_weixin字段添加成功");
                } catch (Exception e) {
                    log.error("❌ 添加qq_weixin字段失败: {}", e.getMessage());
                }
            } else {
                log.debug("qq_weixin字段已存在，跳过");
            }
            
            // 检查并添加cooperation_content字段
            if (!checkColumnExists("customer", "cooperation_content")) {
                log.info("添加cooperation_content字段到customer表...");
                try {
                    jdbcTemplate.execute("ALTER TABLE customer ADD COLUMN cooperation_content TEXT COMMENT '合作内容' AFTER qq_weixin");
                    log.info("✅ cooperation_content字段添加成功");
                } catch (Exception e) {
                    log.error("❌ 添加cooperation_content字段失败: {}", e.getMessage());
                }
            } else {
                log.debug("cooperation_content字段已存在，跳过");
            }
            
            // 检查并添加region字段
            if (!checkColumnExists("customer", "region")) {
                log.info("添加region字段到customer表...");
                try {
                    jdbcTemplate.execute("ALTER TABLE customer ADD COLUMN region VARCHAR(50) COMMENT '地区' AFTER cooperation_content");
                    log.info("✅ region字段添加成功");
                } catch (Exception e) {
                    log.error("❌ 添加region字段失败: {}", e.getMessage());
                }
            } else {
                log.debug("region字段已存在，跳过");
            }
            
            // 检查并删除applicant_nature字段（如果存在）
            if (checkColumnExists("customer", "applicant_nature")) {
                log.info("删除applicant_nature字段从customer表...");
                try {
                    jdbcTemplate.execute("ALTER TABLE customer DROP COLUMN applicant_nature");
                    log.info("✅ applicant_nature字段删除成功");
                } catch (Exception e) {
                    log.warn("⚠️ 删除applicant_nature字段失败: {}", e.getMessage());
                }
            }
            
            log.info("✅ 表结构更新完成");
        } catch (Exception e) {
            log.error("❌ 更新表结构失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 检查列是否存在
     */
    private boolean checkColumnExists(String tableName, String columnName) {
        try {
            String sql = "SELECT COUNT(*) FROM information_schema.columns " +
                        "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName, columnName);
            return count != null && count > 0;
        } catch (Exception e) {
            log.debug("检查列是否存在时出错: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 创建communication_record表
     */
    private void createCommunicationRecordTable() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS communication_record (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID', " +
                    "customer_id BIGINT NOT NULL COMMENT '客户ID', " +
                    "customer_name VARCHAR(100) NOT NULL COMMENT '客户姓名', " +
                    "communication_type TINYINT NOT NULL COMMENT '沟通类型(1:微信,2:电话,3:邮件,4:会面,5:其他)', " +
                    "communication_time DATETIME NOT NULL COMMENT '沟通时间', " +
                    "content TEXT COMMENT '沟通内容', " +
                    "summary VARCHAR(1000) COMMENT '沟通摘要', " +
                    "importance TINYINT DEFAULT 1 COMMENT '重要程度(1:一般,2:重要,3:非常重要)', " +
                    "sentiment TINYINT COMMENT '情感分析结果(1:积极,2:中性,3:消极)', " +
                    "satisfaction_score TINYINT COMMENT '满意度评分(1-5分)', " +
                    "keywords VARCHAR(500) COMMENT '关键词(多个关键词用逗号分隔)', " +
                    "important_info TEXT COMMENT '重要信息标记', " +
                    "follow_up_task VARCHAR(1000) COMMENT '后续任务', " +
                    "task_deadline DATETIME COMMENT '任务截止时间', " +
                    "task_status TINYINT DEFAULT 1 COMMENT '任务状态(1:待处理,2:进行中,3:已完成,4:已取消)', " +
                    "communicator_id BIGINT COMMENT '沟通人员ID', " +
                    "communicator_name VARCHAR(100) COMMENT '沟通人员姓名', " +
                    "attachment_path VARCHAR(500) COMMENT '附件路径', " +
                    "channel_detail VARCHAR(200) COMMENT '沟通渠道详情', " +
                    "duration INT COMMENT '沟通时长(分钟)', " +
                    "response_time INT COMMENT '客户响应时间(分钟)', " +
                    "is_read TINYINT DEFAULT 0 COMMENT '是否已读(0:未读,1:已读)', " +
                    "is_processed TINYINT DEFAULT 0 COMMENT '是否已处理(0:未处理,1:已处理)', " +
                    "process_result TEXT COMMENT '处理结果', " +
                    "process_time DATETIME COMMENT '处理时间', " +
                    "create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', " +
                    "update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', " +
                    "create_by VARCHAR(50) COMMENT '创建人', " +
                    "update_by VARCHAR(50) COMMENT '更新人', " +
                    "deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)', " +
                    "version INT NOT NULL DEFAULT 1 COMMENT '版本号', " +
                    "INDEX idx_customer_id (customer_id), " +
                    "INDEX idx_communication_type (communication_type), " +
                    "INDEX idx_communication_time (communication_time), " +
                    "INDEX idx_importance (importance), " +
                    "INDEX idx_communicator_id (communicator_id), " +
                    "INDEX idx_create_time (create_time), " +
                    "INDEX idx_deleted (deleted)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='沟通记录表'";
            
            jdbcTemplate.execute(sql);
            log.info("✅ communication_record表创建成功");
        } catch (Exception e) {
            log.error("❌ 创建communication_record表失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 创建team_task表
     */
    private void createTeamTaskTable() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS team_task (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID', " +
                    "title VARCHAR(200) NOT NULL COMMENT '任务标题', " +
                    "description TEXT COMMENT '任务描述', " +
                    "task_type TINYINT NOT NULL COMMENT '任务类型(1:客户跟进,2:项目推进,3:问题处理,4:会议安排,5:其他)', " +
                    "status TINYINT DEFAULT 1 COMMENT '任务状态(1:待分配,2:进行中,3:待审核,4:已完成,5:已取消)', " +
                    "priority TINYINT DEFAULT 2 COMMENT '优先级(1:低,2:中,3:高,4:紧急)', " +
                    "customer_id BIGINT COMMENT '关联客户ID', " +
                    "customer_name VARCHAR(100) COMMENT '关联客户姓名', " +
                    "creator_id BIGINT NOT NULL COMMENT '创建人ID', " +
                    "creator_name VARCHAR(100) NOT NULL COMMENT '创建人姓名', " +
                    "assignee_id BIGINT COMMENT '负责人ID', " +
                    "assignee_name VARCHAR(100) COMMENT '负责人姓名', " +
                    "start_time DATETIME COMMENT '开始时间', " +
                    "deadline DATETIME COMMENT '截止时间', " +
                    "completed_time DATETIME COMMENT '完成时间', " +
                    "estimated_hours INT COMMENT '预计工时(小时)', " +
                    "actual_hours INT COMMENT '实际工时(小时)', " +
                    "progress INT DEFAULT 0 COMMENT '任务进度(0-100%)', " +
                    "result TEXT COMMENT '完成结果', " +
                    "remark TEXT COMMENT '备注', " +
                    "parent_task_id BIGINT COMMENT '父任务ID', " +
                    "level INT DEFAULT 1 COMMENT '任务层级', " +
                    "need_approval TINYINT DEFAULT 0 COMMENT '是否需要审批(0:否,1:是)', " +
                    "approver_id BIGINT COMMENT '审批人ID', " +
                    "approver_name VARCHAR(100) COMMENT '审批人姓名', " +
                    "approval_time DATETIME COMMENT '审批时间', " +
                    "approval_result TINYINT COMMENT '审批结果(1:通过,2:拒绝,3:待审批)', " +
                    "approval_comment TEXT COMMENT '审批意见', " +
                    "tags VARCHAR(500) COMMENT '任务标签', " +
                    "attachment_path VARCHAR(500) COMMENT '关联文件路径', " +
                    "is_public TINYINT DEFAULT 1 COMMENT '是否公开(0:否,1:是)', " +
                    "visible_user_ids VARCHAR(1000) COMMENT '可见用户ID列表', " +
                    "work_mode TINYINT COMMENT '工作模式(1:办公室,2:远程,3:混合)', " +
                    "work_duration INT COMMENT '工作时长(分钟)', " +
                    "actual_start_time DATETIME COMMENT '实际开始时间', " +
                    "actual_end_time DATETIME COMMENT '实际结束时间', " +
                    "work_status TINYINT COMMENT '工作状态(1:工作中,2:休息中,3:会议中,4:离线,5:异常)', " +
                    "last_active_time DATETIME COMMENT '最后活跃时间', " +
                    "work_location VARCHAR(200) COMMENT '工作地点', " +
                    "work_evidence VARCHAR(500) COMMENT '工作证据', " +
                    "work_log TEXT COMMENT '工作日志', " +
                    "supervisor_id BIGINT COMMENT '监督人ID', " +
                    "supervisor_name VARCHAR(100) COMMENT '监督人姓名', " +
                    "need_supervision TINYINT DEFAULT 0 COMMENT '是否需要监督(0:否,1:是)', " +
                    "supervision_interval INT COMMENT '监督间隔(分钟)', " +
                    "last_supervision_time DATETIME COMMENT '最后监督时间', " +
                    "quality_score TINYINT COMMENT '质量评分(1-5分)', " +
                    "efficiency_score TINYINT COMMENT '效率评分(1-5分)', " +
                    "attitude_score TINYINT COMMENT '态度评分(1-5分)', " +
                    "supervision_note TEXT COMMENT '监督备注', " +
                    "name VARCHAR(200) COMMENT '任务名称(兼容字段)', " +
                    "start_date DATE COMMENT '开始日期(兼容字段)', " +
                    "end_date DATE COMMENT '结束日期(兼容字段)', " +
                    "create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', " +
                    "update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', " +
                    "create_by VARCHAR(50) COMMENT '创建人', " +
                    "update_by VARCHAR(50) COMMENT '更新人', " +
                    "deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)', " +
                    "version INT NOT NULL DEFAULT 1 COMMENT '版本号', " +
                    "INDEX idx_customer_id (customer_id), " +
                    "INDEX idx_task_type (task_type), " +
                    "INDEX idx_status (status), " +
                    "INDEX idx_priority (priority), " +
                    "INDEX idx_creator_id (creator_id), " +
                    "INDEX idx_assignee_id (assignee_id), " +
                    "INDEX idx_deadline (deadline), " +
                    "INDEX idx_parent_task_id (parent_task_id), " +
                    "INDEX idx_create_time (create_time), " +
                    "INDEX idx_deleted (deleted)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队任务表'";
            
            jdbcTemplate.execute(sql);
            log.info("✅ team_task表创建成功");
        } catch (Exception e) {
            log.error("❌ 创建team_task表失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 创建task_progress_report表
     */
    private void createTaskProgressReportTable() {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS task_progress_report (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID', " +
                    "task_id BIGINT NOT NULL COMMENT '任务ID', " +
                    "task_name VARCHAR(200) NOT NULL COMMENT '任务名称', " +
                    "report_type TINYINT NOT NULL COMMENT '汇报类型(1:日报, 2:周报, 3:月报, 4:项目汇报, 5:紧急汇报)', " +
                    "report_title VARCHAR(200) NOT NULL COMMENT '汇报标题', " +
                    "report_content TEXT NOT NULL COMMENT '汇报内容', " +
                    "employee_name VARCHAR(100) NOT NULL COMMENT '员工姓名', " +
                    "create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', " +
                    "update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', " +
                    "create_by VARCHAR(50) COMMENT '创建人', " +
                    "update_by VARCHAR(50) COMMENT '更新人', " +
                    "deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志(0:未删除,1:已删除)', " +
                    "version INT NOT NULL DEFAULT 1 COMMENT '版本号', " +
                    "INDEX idx_task_id (task_id), " +
                    "INDEX idx_report_type (report_type), " +
                    "INDEX idx_create_time (create_time), " +
                    "INDEX idx_deleted (deleted)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务进度汇报表'";
            
            jdbcTemplate.execute(sql);
            log.info("✅ task_progress_report表创建成功");
        } catch (Exception e) {
            log.error("❌ 创建task_progress_report表失败: {}", e.getMessage(), e);
        }
    }
}

