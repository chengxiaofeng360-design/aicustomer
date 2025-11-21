package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.User;
import com.aicustomer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户控制器
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户列表（用于下拉选择等场景）
     *
     * @return 用户列表
     */
    @GetMapping("/list")
    public Result<List<User>> getUserList() {
        try {
            List<User> users = userService.getUserList();
            return Result.success(users);
        } catch (Exception e) {
            return Result.error("获取用户列表失败: " + e.getMessage());
        }
    }
}