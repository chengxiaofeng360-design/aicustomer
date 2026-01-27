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
        encryptPlaintextPassword("admin");
        encryptPlaintextPassword("staff");
    }

    private void encryptPlaintextPassword(String username) {
        try {
            User user = userService.findByUsername(username);
            // 检测是否为明文弱密码 "123456"
            if (user != null && "123456".equals(user.getPassword())) {
                // 直接调用 update，UserServiceImpl 会自动检测并加密当前密码
                System.out.println("⚠️ 检测到 " + username + " 使用明文密码，正在进行加密处理...");
                userService.update(user);
                System.out.println("✅ " + username + " 密码已完成加密存储");
            }
        } catch (Exception e) {
            System.err.println("❌ 加密 " + username + " 密码失败: " + e.getMessage());
        }
    }
}
