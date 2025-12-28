package com.aicustomer.controller;

import com.aicustomer.common.Result;
import com.aicustomer.dto.MenuDefinition;
import com.aicustomer.dto.UserPermissionDTO;
import com.aicustomer.service.UserPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简化的用户权限管理控制器
 * 提供更简单直观的权限配置方式
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
     * 获取所有菜单定义（用于权限配置）
     */
    @GetMapping("/menus")
    public Result<List<MenuDefinition>> getMenus() {
        try {
            return Result.success(MenuDefinition.getAllMenus());
        } catch (Exception e) {
            return Result.error("获取菜单列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取角色模板列表
     */
    @GetMapping("/role-templates")
    public Result<List<Map<String, Object>>> getRoleTemplates() {
        try {
            List<Map<String, Object>> templates = List.of(
                    createTemplate("admin", "管理员", "拥有所有权限，可以管理用户和系统配置"),
                    createTemplate("manager", "经理", "拥有大部分权限，可以查看报表和导出数据，但不能管理用户"),
                    createTemplate("employee", "普通员工", "拥有基础权限，可以管理客户和沟通记录"),
                    createTemplate("readonly", "只读用户", "只能查看数据，不能进行任何修改操作"),
                    createTemplate("custom", "自定义", "手动选择菜单权限"));
            return Result.success(templates);
        } catch (Exception e) {
            return Result.error("获取角色模板失败: " + e.getMessage());
        }
    }

    /**
     * 根据角色模板获取默认菜单权限
     */
    @GetMapping("/role-template/{template}/menus")
    public Result<List<String>> getMenusByTemplate(@PathVariable String template) {
        try {
            return Result.success(MenuDefinition.getMenuIdsByRoleTemplate(template));
        } catch (Exception e) {
            return Result.error("获取模板菜单失败: " + e.getMessage());
        }
    }

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
     * 获取客户等级选项
     */
    @GetMapping("/customer-levels")
    public Result<List<Map<String, Object>>> getCustomerLevels() {
        try {
            List<Map<String, Object>> levels = List.of(
                    createOption(0, "所有等级", "可以访问所有等级的客户"),
                    createOption(1, "普通客户", "只能访问普通客户"),
                    createOption(2, "VIP客户", "只能访问VIP客户"),
                    createOption(3, "钻石客户", "只能访问钻石客户"));
            return Result.success(levels);
        } catch (Exception e) {
            return Result.error("获取客户等级失败: " + e.getMessage());
        }
    }

    /**
     * 获取数据范围选项
     */
    @GetMapping("/data-scopes")
    public Result<List<Map<String, Object>>> getDataScopes() {
        try {
            List<Map<String, Object>> scopes = List.of(
                    createOption("all", "所有数据", "可以查看和管理所有数据"),
                    createOption("department", "本部门数据", "只能查看和管理本部门的数据"),
                    createOption("self", "仅自己的数据", "只能查看和管理自己创建的数据"));
            return Result.success(scopes);
        } catch (Exception e) {
            return Result.error("获取数据范围失败: " + e.getMessage());
        }
    }

    /**
     * 辅助方法：创建模板对象
     */
    private Map<String, Object> createTemplate(String value, String label, String description) {
        Map<String, Object> template = new HashMap<>();
        template.put("value", value);
        template.put("label", label);
        template.put("description", description);
        return template;
    }

    /**
     * 辅助方法：创建选项对象
     */
    private Map<String, Object> createOption(Object value, String label, String description) {
        Map<String, Object> option = new HashMap<>();
        option.put("value", value);
        option.put("label", label);
        option.put("description", description);
        return option;
    }
}
