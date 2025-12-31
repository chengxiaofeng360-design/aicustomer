package com.aicustomer.service;

import com.aicustomer.dto.UserPermissionDTO;

/**
 * 用户权限服务接口
 */
public interface UserPermissionService {

    /**
     * 创建用户并配置权限
     */
    Boolean createUserWithPermission(UserPermissionDTO dto);

    /**
     * 更新用户权限
     */
    Boolean updateUserPermission(UserPermissionDTO dto);

    /**
     * 获取用户权限配置
     */
    UserPermissionDTO getUserPermission(Long userId);

    /**
     * 根据用户名获取权限配置
     */
    UserPermissionDTO getUserPermissionByUsername(String username);

    /**
     * 获取所有可用菜单
     */
    Object getAllMenus();

    /**
     * 获取所有角色模板
     */
    Object getRoleTemplates();

    /**
     * 获取角色模板对应的默认菜单ID
     */
    Object getRoleTemplateMenus(String template);

    /**
     * 获取客户等级定义
     */
    Object getCustomerLevels();
}
