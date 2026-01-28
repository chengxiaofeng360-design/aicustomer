package com.aicustomer.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据库表及初始数据验证运行器
 */
@Component
public class TableVerificationRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // 启动验证逻辑已移除
        System.out.println("✅ 系统启动完成");
    }
}
