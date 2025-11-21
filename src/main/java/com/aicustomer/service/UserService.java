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
}