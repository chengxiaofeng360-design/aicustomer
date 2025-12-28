package com.aicustomer.mapper;

import com.aicustomer.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色Mapper接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface RoleMapper {

    /**
     * 根据ID查询角色
     * 
     * @param id 角色ID
     * @return 角色信息
     */
    Role selectById(Long id);

    /**
     * 根据角色名查询角色
     * 
     * @param roleName 角色名称
     * @return 角色信息
     */
    Role selectByName(@Param("roleName") String roleName);

    /**
     * 根据角色编码查询角色
     * 
     * @param roleCode 角色编码
     * @return 角色信息
     */
    Role selectByCode(@Param("roleCode") String roleCode);

    /**
     * 查询角色列表
     * 
     * @param role 查询条件
     * @return 角色列表
     */
    List<Role> selectList(Role role);

    /**
     * 根据用户ID查询角色列表
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> selectByUserId(Long userId);

    /**
     * 新增角色
     * 
     * @param role 角色信息
     * @return 影响行数
     */
    int insert(Role role);

    /**
     * 更新角色
     * 
     * @param role 角色信息
     * @return 影响行数
     */
    int update(Role role);

    /**
     * 删除角色
     * 
     * @param id 角色ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 绑定角色权限
     * 
     * @param roleId       角色ID
     * @param permissionId 权限ID
     */
    int insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    /**
     * 删除角色所有权限
     * 
     * @param roleId 角色ID
     */
    int deleteRolePermissions(@Param("roleId") Long roleId);
}
