package com.aicustomer.mapper;

import com.aicustomer.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 权限Mapper接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface PermissionMapper {

    /**
     * 根据ID查询权限
     * 
     * @param id 权限ID
     * @return 权限信息
     */
    Permission selectById(Long id);

    /**
     * 查询权限列表
     * 
     * @param permission 查询条件
     * @return 权限列表
     */
    List<Permission> selectList(Permission permission);

    /**
     * 根据角色ID查询权限列表
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> selectByRoleId(Long roleId);

    /**
     * 根据用户ID查询权限列表
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> selectByUserId(Long userId);

    /**
     * 新增权限
     * 
     * @param permission 权限信息
     * @return 影响行数
     */
    int insert(Permission permission);

    /**
     * 更新权限
     * 
     * @param permission 权限信息
     * @return 影响行数
     */
    int update(Permission permission);

    /**
     * 删除权限
     * 
     * @param id 权限ID
     * @return 影响行数
     */
    int deleteById(Long id);
}
