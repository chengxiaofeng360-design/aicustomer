package com.aicustomer.mapper;

import com.aicustomer.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户Mapper接口
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Mapper
public interface UserMapper {

    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    User selectById(Long id);

    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(@Param("username") String username);

    /**
     * 查询用户列表
     * 
     * @param user 查询条件
     * @return 用户列表
     */
    List<User> selectList(@Param("user") User user);

    /**
     * 分页查询用户
     * 
     * @param user   查询条件
     * @param offset 偏移量
     * @param limit  条数
     * @return 用户列表
     */
    List<User> selectPage(@Param("user") User user, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 查询总数
     * 
     * @param user 查询条件
     * @return 总数
     */
    Long selectCount(@Param("user") User user);

    /**
     * 新增用户
     * 
     * @param user 用户信息
     * @return 影响行数
     */
    int insert(User user);

    /**
     * 更新用户
     * 
     * @param user 用户信息
     * @return 影响行数
     */
    int updateById(User user);

    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 影响行数
     */
    int deleteById(Long id);

    /**
     * 更新最后登录时间
     * 
     * @param id            用户ID
     * @param lastLoginTime 最后登录时间
     * @return 影响行数
     */
    int updateLastLoginTime(@Param("id") Long id, @Param("lastLoginTime") LocalDateTime lastLoginTime);

    /**
     * 更新密码
     * 
     * @param id       用户ID
     * @param password 新密码
     * @return 影响行数
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 更新状态
     * 
     * @param id     用户ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 插入用户角色关联
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响行数
     */
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 删除用户所有角色关联
     * 
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteUserRoles(@Param("userId") Long userId);
}
