package com.aicustomer.service;

import com.aicustomer.entity.User;

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
}





