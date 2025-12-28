package com.aicustomer.service;

import com.aicustomer.entity.Permission;
import java.util.List;

/**
 * 权限服务接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface PermissionService {

    /**
     * 根据ID查询权限
     * 
     * @param id 权限ID
     * @return 权限信息
     */
    Permission getById(Long id);

    /**
     * 查询权限列表
     * 
     * @param permission 查询条件
     * @return 权限列表
     */
    List<Permission> getList(Permission permission);

    /**
     * 根据角色ID查询权限
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> getByRoleId(Long roleId);

    /**
     * 根据用户ID查询权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getByUserId(Long userId);

    /**
     * 新增权限
     * 
     * @param permission 权限信息
     * @return true:成功 false:失败
     */
    boolean create(Permission permission);

    /**
     * 更新权限
     * 
     * @param permission 权限信息
     * @return true:成功 false:失败
     */
    boolean update(Permission permission);

    /**
     * 删除权限
     * 
     * @param id 权限ID
     * @return true:成功 false:失败
     */
    boolean delete(Long id);

    /**
     * 获取权限树
     * 
     * @return 树形结构
     */
    List<Object> getTree();
}
