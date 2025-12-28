package com.aicustomer.service;

import com.aicustomer.entity.Role;
import java.util.List;

/**
 * 角色服务接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface RoleService {

    /**
     * 根据ID查询角色
     * 
     * @param id 角色ID
     * @return 角色信息
     */
    Role getById(Long id);

    /**
     * 查询角色列表
     * 
     * @param role 查询条件
     * @return 角色列表
     */
    List<Role> getList(Role role);

    /**
     * 根据用户ID查询角色
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getByUserId(Long userId);

    /**
     * 新增角色
     * 
     * @param role 角色信息
     * @return true:成功 false:失败
     */
    boolean create(Role role);

    /**
     * 更新角色
     * 
     * @param role 角色信息
     * @return true:成功 false:失败
     */
    boolean update(Role role);

    /**
     * 删除角色
     * 
     * @param id 角色ID
     * @return true:成功 false:失败
     */
    boolean delete(Long id);

    /**
     * 分配权限
     * 
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     * @return true:成功 false:失败
     */
    boolean assignPermissions(Long roleId, List<Long> permissionIds);
}
