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
            // 检查customer表是否存在
            boolean customerTableExists = checkTableExists("customer");
            boolean communicationTableExists = checkTableExists("communication_record");
            
            if (!customerTableExists) {
                log.info("检测到customer表不存在，开始初始化数据库表结构...");
                initializeDatabase();
            } else {
                log.info("数据库表已存在，检查并更新表结构...");
                updateTableStructure();
                
                // 检查并创建communication_record表（如果不存在）
                if (!communicationTableExists) {
                    log.info("检测到communication_record表不存在，开始创建...");
                    createCommunicationRecordTable();
                } else {
                    log.debug("communication_record表已存在，跳过创建");
                }
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
}

