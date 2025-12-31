package com.aicustomer.config;

import com.aicustomer.entity.User;
import com.aicustomer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 数据库表及初始数据验证运行器
 */
@Component
public class TableVerificationRunner implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // 管理员账号初始化现在由 UserServiceImpl.initAdmin 统一处理
        User admin = userService.findByUsername("admin");
        if (admin != null) {
            System.out.println("系统管理员已就绪: admin");
        }
    }
}
