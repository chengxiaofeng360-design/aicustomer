package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.User;
import com.aicustomer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取用户列表（带查询条件）
     */
    @GetMapping("/list")
    public Result<List<User>> list(User user) {
        try {
            return Result.success(userService.getList(user));
        } catch (Exception e) {
            return Result.error("获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        try {
            return Result.success(userService.getById(id));
        } catch (Exception e) {
            return Result.error("获取用户详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建用户
     */
    @PostMapping
    public Result<Boolean> create(@RequestBody User user) {
        try {
            return Result.success(userService.create(user));
        } catch (Exception e) {
            return Result.error("创建用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody User user) {
        try {
            return Result.success(userService.update(user));
        } catch (Exception e) {
            return Result.error("更新用户失败: " + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        try {
            return Result.success(userService.delete(id));
        } catch (Exception e) {
            return Result.error("删除用户失败: " + e.getMessage());
        }
    }

    /**
     * 分配角色
     */
    @PostMapping("/{id}/roles")
    public Result<Boolean> assignRoles(@PathVariable Long id, @RequestBody Map<String, List<Long>> params) {
        try {
            List<Long> roleIds = params.get("roleIds");
            return Result.success(userService.assignRoles(id, roleIds));
        } catch (Exception e) {
            return Result.error("分配角色失败: " + e.getMessage());
        }
    }
}