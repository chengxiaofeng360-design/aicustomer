package com.aicustomer.service;

import com.aicustomer.entity.User;
import java.util.List;

/**
 * 用户服务接口
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
public interface UserService {

    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户信息
     */
    User findByUsername(String username);

    /**
     * 获取用户列表（用于下拉选择等场景）
     * 
     * @return 用户列表
     */
    List<User> getUserList();

    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    User getById(Long id);

    /**
     * 查询用户列表
     * 
     * @param user 查询条件
     * @return 用户列表
     */
    List<User> getList(User user);

    /**
     * 创建用户
     * 
     * @param user 用户信息
     * @return true:成功 false:失败
     */
    boolean create(User user);

    /**
     * 更新用户
     * 
     * @param user 用户信息
     * @return true:成功 false:失败
     */
    boolean update(User user);

    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return true:成功 false:失败
     */
    boolean delete(Long id);

    /**
     * 分配角色
     * 
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return true:成功 false:失败
     */
    boolean assignRoles(Long userId, List<Long> roleIds);

    /**
     * 初始化管理员账号
     */
    void initAdmin();
}