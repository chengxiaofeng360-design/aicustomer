package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.CustomerAccount;
import com.aicustomer.service.CustomerAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 客户账号控制器
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/social")
public class CustomerAccountController {

    @Autowired
    private CustomerAccountService customerAccountService;

    /**
     * 客户注册
     */
    @PostMapping("/register")
    public Result<CustomerAccount> register(@RequestBody CustomerAccount customerAccount) {
        try {
            CustomerAccount result = customerAccountService.register(customerAccount);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("注册失败: " + e.getMessage());
        }
    }

    /**
     * 客户登录
     */
    @PostMapping("/login")
    public Result<CustomerAccount> login(@RequestParam String username,
                                        @RequestParam String password) {
        try {
            CustomerAccount result = customerAccountService.login(username, password);
            if (result != null) {
                return Result.success(result);
            } else {
                return Result.error("用户名或密码错误");
            }
        } catch (Exception e) {
            return Result.error("登录失败: " + e.getMessage());
        }
    }

    /**
     * 获取客户账号信息
     */
    @GetMapping("/account/{id}")
    public Result<CustomerAccount> getAccount(@PathVariable Long id) {
        try {
            CustomerAccount account = customerAccountService.getAccountById(id);
            if (account != null) {
                return Result.success(account);
            } else {
                return Result.error("账号不存在");
            }
        } catch (Exception e) {
            return Result.error("获取账号信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新客户账号信息
     */
    @PutMapping("/account/{id}")
    public Result<CustomerAccount> updateAccount(@PathVariable Long id,
                                               @RequestBody CustomerAccount customerAccount) {
        try {
            customerAccount.setId(id);
            CustomerAccount result = customerAccountService.update(customerAccount);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("更新账号信息失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/account/{id}/stats")
    public Result<Object> getUserStats(@PathVariable Long id) {
        try {
            Object stats = customerAccountService.getUserStats(id);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取用户统计信息失败: " + e.getMessage());
        }
    }
}
