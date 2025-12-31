package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.dto.UserPermissionDTO;
import com.aicustomer.service.UserPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 简化的用户权限管理控制器
 * 直接管理菜单权限和数据权限
 *
 * @author AI Customer Management System
 * @version 2.0.0
 */
@RestController
@RequestMapping("/api/user-permission")
@RequiredArgsConstructor
public class UserPermissionController {

    private final UserPermissionService userPermissionService;

    /**
     * 创建用户并配置权限
     */
    @PostMapping("/create")
    public Result<Boolean> createUserWithPermission(@RequestBody UserPermissionDTO dto) {
        try {
            return Result.success(userPermissionService.createUserWithPermission(dto));
        } catch (Exception e) {
            return Result.error("创建用户失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户权限
     */
    @PutMapping("/update")
    public Result<Boolean> updateUserPermission(@RequestBody UserPermissionDTO dto) {
        try {
            return Result.success(userPermissionService.updateUserPermission(dto));
        } catch (Exception e) {
            return Result.error("更新用户权限失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户权限配置
     */
    @GetMapping("/{userId}")
    public Result<UserPermissionDTO> getUserPermission(@PathVariable Long userId) {
        try {
            return Result.success(userPermissionService.getUserPermission(userId));
        } catch (Exception e) {
            return Result.error("获取用户权限失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有可用菜单
     */
    @GetMapping("/menus")
    public Result<Object> getAllMenus() {
        return Result.success(userPermissionService.getAllMenus());
    }

    /**
     * 获取所有角色模板
     */
    @GetMapping("/role-templates")
    public Result<Object> getRoleTemplates() {
        return Result.success(userPermissionService.getRoleTemplates());
    }

    /**
     * 获取角色模板对应的默认菜单ID
     */
    @GetMapping("/role-template/{template}/menus")
    public Result<Object> getRoleTemplateMenus(@PathVariable String template) {
        return Result.success(userPermissionService.getRoleTemplateMenus(template));
    }

    /**
     * 获取客户等级定义
     */
    @GetMapping("/customer-levels")
    public Result<Object> getCustomerLevels() {
        return Result.success(userPermissionService.getCustomerLevels());
    }

    /**
     * 获取当前登录用户的权限配置
     */
    @GetMapping("/me")
    public Result<UserPermissionDTO> getCurrentUserPermission() {
        try {
            String username = org.springframework.security.core.context.SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            return Result.success(userPermissionService.getUserPermissionByUsername(username));
        } catch (Exception e) {
            return Result.error("获取当前权限失败: " + e.getMessage());
        }
    }
}
