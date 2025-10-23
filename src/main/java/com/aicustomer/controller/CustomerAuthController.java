package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.CustomerAccount;
import com.aicustomer.service.CustomerAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
public class CustomerAuthController {

    @Autowired
    private CustomerAccountService customerAccountService;

    /**
     * 客户注册
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            String nickname = request.get("nickname");
            String email = request.get("email");
            String phone = request.get("phone");
            String bio = request.get("bio");

            // 检查用户名是否已存在
            CustomerAccount existingUser = customerAccountService.findByUsername(username);
            if (existingUser != null) {
                return Result.error("用户名已存在");
            }

            // 创建新用户
            CustomerAccount newUser = new CustomerAccount();
            newUser.setCustomerId(1L); // 设置默认客户ID
            newUser.setUsername(username);
            newUser.setPassword(password); // 在实际应用中应该加密
            newUser.setNickname(nickname);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setBio(bio);
            newUser.setStatus(1);
            newUser.setCreateTime(LocalDateTime.now());
            newUser.setUpdateTime(LocalDateTime.now());

            // 保存用户
            customerAccountService.save(newUser);

            Map<String, Object> result = new HashMap<>();
            result.put("id", newUser.getId());
            result.put("username", newUser.getUsername());
            result.put("nickname", newUser.getNickname());

            return Result.success(result);
        } catch (Exception e) {
            return Result.error("注册失败: " + e.getMessage());
        }
    }

    /**
     * 客户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, Object> request) {
        try {
            String username = (String) request.get("username");
            String password = (String) request.get("password");

            // 查找用户
            CustomerAccount user = customerAccountService.findByUsername(username);
            if (user == null) {
                return Result.error("用户名或密码错误");
            }

            // 验证密码（在实际应用中应该使用加密比较）
            if (!password.equals(user.getPassword())) {
                return Result.error("用户名或密码错误");
            }

            // 检查用户状态
            if (user.getStatus() != 1) {
                return Result.error("账户已被禁用");
            }

            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            customerAccountService.update(user);

            Map<String, Object> result = new HashMap<>();
            result.put("id", user.getId());
            result.put("username", user.getUsername());
            result.put("nickname", user.getNickname());
            result.put("email", user.getEmail());
            result.put("phone", user.getPhone());
            result.put("bio", user.getBio());
            result.put("avatar", user.getAvatar());

            return Result.success(result);
        } catch (Exception e) {
            return Result.error("登录失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/profile/{id}")
    public Result<CustomerAccount> getProfile(@PathVariable Long id) {
        try {
            CustomerAccount user = customerAccountService.findById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile/{id}")
    public Result<String> updateProfile(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            CustomerAccount user = customerAccountService.findById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 更新用户信息
            if (request.containsKey("nickname")) {
                user.setNickname(request.get("nickname"));
            }
            if (request.containsKey("email")) {
                user.setEmail(request.get("email"));
            }
            if (request.containsKey("phone")) {
                user.setPhone(request.get("phone"));
            }
            if (request.containsKey("bio")) {
                user.setBio(request.get("bio"));
            }
            if (request.containsKey("location")) {
                user.setLocation(request.get("location"));
            }
            if (request.containsKey("interests")) {
                user.setInterests(request.get("interests"));
            }
            if (request.containsKey("company")) {
                user.setCompany(request.get("company"));
            }
            if (request.containsKey("position")) {
                user.setPosition(request.get("position"));
            }
            if (request.containsKey("website")) {
                user.setWebsite(request.get("website"));
            }

            user.setUpdateTime(LocalDateTime.now());
            customerAccountService.update(user);

            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password/{id}")
    public Result<String> changePassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");

            CustomerAccount user = customerAccountService.findById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }

            // 验证当前密码
            if (!currentPassword.equals(user.getPassword())) {
                return Result.error("当前密码错误");
            }

            // 更新密码
            user.setPassword(newPassword);
            user.setUpdateTime(LocalDateTime.now());
            customerAccountService.update(user);

            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error("密码修改失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户统计信息
     */
    @GetMapping("/stats/{id}")
    public Result<Map<String, Object>> getUserStats(@PathVariable Long id) {
        try {
            Map<String, Object> stats = customerAccountService.getUserStats(id);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取统计信息失败: " + e.getMessage());
        }
    }
}
