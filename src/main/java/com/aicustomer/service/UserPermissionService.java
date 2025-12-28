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
}
