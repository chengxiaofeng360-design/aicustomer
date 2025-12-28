package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.Role;
import com.aicustomer.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 角色控制器
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * 获取角色列表
     */
    @GetMapping("/list")
    public Result<List<Role>> list(Role role) {
        try {
            return Result.success(roleService.getList(role));
        } catch (Exception e) {
            return Result.error("获取角色列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID获取角色
     */
    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        try {
            return Result.success(roleService.getById(id));
        } catch (Exception e) {
            return Result.error("获取角色详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建角色
     */
    @PostMapping
    public Result<Boolean> create(@RequestBody Role role) {
        try {
            return Result.success(roleService.create(role));
        } catch (Exception e) {
            return Result.error("创建角色失败: " + e.getMessage());
        }
    }

    /**
     * 更新角色
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody Role role) {
        try {
            return Result.success(roleService.update(role));
        } catch (Exception e) {
            return Result.error("更新角色失败: " + e.getMessage());
        }
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        try {
            return Result.success(roleService.delete(id));
        } catch (Exception e) {
            return Result.error("删除角色失败: " + e.getMessage());
        }
    }

    /**
     * 分配权限
     */
    @PostMapping("/{id}/permissions")
    public Result<Boolean> assignPermissions(@PathVariable Long id, @RequestBody Map<String, List<Long>> params) {
        try {
            List<Long> permissionIds = params.get("permissionIds");
            return Result.success(roleService.assignPermissions(id, permissionIds));
        } catch (Exception e) {
            return Result.error("分配权限失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID获取角色
     */
    @GetMapping("/user/{userId}")
    public Result<List<Role>> getByUserId(@PathVariable Long userId) {
        try {
            return Result.success(roleService.getByUserId(userId));
        } catch (Exception e) {
            return Result.error("获取用户角色失败: " + e.getMessage());
        }
    }

}
