package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.entity.Permission;
import com.aicustomer.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限控制器
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 获取权限列表
     */
    @GetMapping("/list")
    public Result<List<Permission>> list(Permission permission) {
        try {
            return Result.success(permissionService.getList(permission));
        } catch (Exception e) {
            return Result.error("获取权限列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取权限树
     */
    @GetMapping("/tree")
    public Result<List<Object>> tree() {
        try {
            return Result.success(permissionService.getTree());
        } catch (Exception e) {
            return Result.error("获取权限树失败: " + e.getMessage());
        }
    }

    /**
     * 创建权限
     */
    @PostMapping
    public Result<Boolean> create(@RequestBody Permission permission) {
        try {
            return Result.success(permissionService.create(permission));
        } catch (Exception e) {
            return Result.error("创建权限失败: " + e.getMessage());
        }
    }

    /**
     * 更新权限
     */
    @PutMapping
    public Result<Boolean> update(@RequestBody Permission permission) {
        try {
            return Result.success(permissionService.update(permission));
        } catch (Exception e) {
            return Result.error("更新权限失败: " + e.getMessage());
        }
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        try {
            return Result.success(permissionService.delete(id));
        } catch (Exception e) {
            return Result.error("删除权限失败: " + e.getMessage());
        }
    }

    /**
     * 根据角色ID获取权限
     */
    @GetMapping("/role/{roleId}")
    public Result<List<Permission>> getByRoleId(@PathVariable Long roleId) {
        try {
            return Result.success(permissionService.getByRoleId(roleId));
        } catch (Exception e) {
            return Result.error("获取角色权限失败: " + e.getMessage());
        }
    }

}
