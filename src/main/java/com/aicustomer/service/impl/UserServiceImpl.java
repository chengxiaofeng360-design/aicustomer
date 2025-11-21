package com.aicustomer.service.impl;

import com.aicustomer.entity.User;
import com.aicustomer.mapper.UserMapper;
import com.aicustomer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现类
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public List<User> getUserList() {
        // 查询所有正常状态的用户（status=1, deleted=0）
        User query = new User();
        query.setStatus(1);
        return userMapper.selectList(query);
    }
}