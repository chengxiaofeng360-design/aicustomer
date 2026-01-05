package com.aicustomer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 数据库迁移配置
 * 用于在应用启动时自动执行数据库结构更新
 * 使用 InitializingBean 确保在其他 bean 初始化之前执行
 */
@Component("databaseMigrationConfig")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DatabaseMigrationConfig implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMigrationConfig.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("开始执行数据库迁移...");

        try {
            // 添加 sys_user 表的 real_name 字段（如果不存在）
            addRealNameColumn();

            // 添加 sys_user 表的 user_type 字段（如果不存在）
            addUserTypeColumn();

            // 添加 sys_user 表的 last_login_time 字段（如果不存在）
            addLastLoginTimeColumn();

            // 添加 sys_user 表的 permission_settings 字段（如果不存在）
            addPermissionSettingsColumn();

            // 修复密码加密
            fixPasswordEncryption();

            logger.info("数据库迁移完成！");
        } catch (Exception e) {
            logger.error("数据库迁移失败", e);
            // 不抛出异常，允许应用继续启动
        }
    }

    private void addPermissionSettingsColumn() {
        try {
            String checkSql = "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                    "WHERE TABLE_SCHEMA = 'zqgl' " +
                    "AND TABLE_NAME = 'sys_user' " +
                    "AND COLUMN_NAME = 'permission_settings'";

            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);

            if (count == null || count == 0) {
                logger.info("添加 sys_user.permission_settings 字段...");
                String alterSql = "ALTER TABLE sys_user " +
                        "ADD COLUMN permission_settings TEXT COMMENT '权限设置JSON'";
                jdbcTemplate.execute(alterSql);
                logger.info("成功添加 sys_user.permission_settings 字段");
            } else {
                logger.info("sys_user.permission_settings 字段已存在，跳过");
            }
        } catch (Exception e) {
            logger.warn("添加 permission_settings 字段时出错: " + e.getMessage());
        }
    }

    private void addRealNameColumn() {
        try {
            // 检查字段是否存在
            String checkSql = "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                    "WHERE TABLE_SCHEMA = 'zqgl' " +
                    "AND TABLE_NAME = 'sys_user' " +
                    "AND COLUMN_NAME = 'real_name'";

            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);

            if (count == null || count == 0) {
                logger.info("添加 sys_user.real_name 字段...");
                String alterSql = "ALTER TABLE sys_user " +
                        "ADD COLUMN real_name VARCHAR(100) COMMENT '真实姓名' AFTER password";
                jdbcTemplate.execute(alterSql);
                logger.info("成功添加 sys_user.real_name 字段");
            } else {
                logger.info("sys_user.real_name 字段已存在，跳过");
            }
        } catch (Exception e) {
            logger.warn("添加 real_name 字段时出错: " + e.getMessage());
        }
    }

    private void addUserTypeColumn() {
        try {
            String checkSql = "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                    "WHERE TABLE_SCHEMA = 'zqgl' " +
                    "AND TABLE_NAME = 'sys_user' " +
                    "AND COLUMN_NAME = 'user_type'";

            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);

            if (count == null || count == 0) {
                logger.info("添加 sys_user.user_type 字段...");
                String alterSql = "ALTER TABLE sys_user " +
                        "ADD COLUMN user_type TINYINT COMMENT '用户类型' AFTER version";
                jdbcTemplate.execute(alterSql);
                logger.info("成功添加 sys_user.user_type 字段");
            } else {
                logger.info("sys_user.user_type 字段已存在，跳过");
            }
        } catch (Exception e) {
            logger.warn("添加 user_type 字段时出错: " + e.getMessage());
        }
    }

    private void addLastLoginTimeColumn() {
        try {
            String checkSql = "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                    "WHERE TABLE_SCHEMA = 'zqgl' " +
                    "AND TABLE_NAME = 'sys_user' " +
                    "AND COLUMN_NAME = 'last_login_time'";

            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class);

            if (count == null || count == 0) {
                logger.info("添加 sys_user.last_login_time 字段...");
                String alterSql = "ALTER TABLE sys_user " +
                        "ADD COLUMN last_login_time DATETIME COMMENT '最后登录时间'";
                jdbcTemplate.execute(alterSql);
                logger.info("成功添加 sys_user.last_login_time 字段");
            } else {
                logger.info("sys_user.last_login_time 字段已存在，跳过");
            }
        } catch (Exception e) {
            logger.warn("添加 last_login_time 字段时出错: " + e.getMessage());
        }
    }

    private void fixPasswordEncryption() {
        try {
            logger.info("正在验证并修复账号密码...");

            // 1. 动态生成 123456 的正确加密串，确保和当前环境的 PasswordEncoder 100% 匹配
            String validHash = passwordEncoder.encode("123456");

            // 2. 强制重置 admin/staff 用户的密码，不进行任何条件判断
            String sql = "UPDATE sys_user SET password = ? WHERE username IN ('admin', 'staff')";

            int updated = jdbcTemplate.update(sql, validHash);

            if (updated > 0) {
                logger.info("✅ [强制重置] 已成功将 {} 个账号(admin/staff)的密码为重置为 123456", updated);
            } else {
                logger.warn("未找到 admin 或 staff 账号，无法重置");
            }
        } catch (Exception e) {
            logger.error("严重的密码修复错误: " + e.getMessage(), e);
        }
    }
}
