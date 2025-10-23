package com.aicustomer.service.impl;

import com.aicustomer.entity.CustomerAccount;
import com.aicustomer.mapper.CustomerAccountMapper;
import com.aicustomer.service.CustomerAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户账号服务实现类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Service
public class CustomerAccountServiceImpl implements CustomerAccountService {

    @Autowired
    private CustomerAccountMapper customerAccountMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public CustomerAccount register(CustomerAccount customerAccount) {
        // 检查用户名是否已存在
        CustomerAccount existing = customerAccountMapper.findByUsername(customerAccount.getUsername());
        if (existing != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 加密密码
        customerAccount.setPassword(passwordEncoder.encode(customerAccount.getPassword()));
        customerAccount.setStatus(1);
        customerAccount.setCreateTime(LocalDateTime.now());
        customerAccount.setUpdateTime(LocalDateTime.now());

        customerAccountMapper.insert(customerAccount);
        return customerAccount;
    }

    @Override
    public CustomerAccount login(String username, String password) {
        CustomerAccount account = customerAccountMapper.findByUsername(username);
        if (account != null && passwordEncoder.matches(password, account.getPassword())) {
            // 更新最后登录时间
            account.setLastLoginTime(LocalDateTime.now());
            customerAccountMapper.updateLastLoginTime(account.getId());
            return account;
        }
        return null;
    }

    @Override
    public CustomerAccount getAccountById(Long id) {
        return customerAccountMapper.findById(id);
    }

    @Override
    public CustomerAccount findByUsername(String username) {
        return customerAccountMapper.findByUsername(username);
    }

    @Override
    public CustomerAccount findById(Long id) {
        return customerAccountMapper.findById(id);
    }

    @Override
    public CustomerAccount save(CustomerAccount customerAccount) {
        // 设置默认客户ID
        if (customerAccount.getCustomerId() == null) {
            customerAccount.setCustomerId(1L);
        }
        customerAccountMapper.insert(customerAccount);
        return customerAccount;
    }

    @Override
    public CustomerAccount update(CustomerAccount customerAccount) {
        customerAccount.setUpdateTime(LocalDateTime.now());
        customerAccountMapper.update(customerAccount);
        return customerAccount;
    }

    @Override
    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取用户动态数
        Integer postCount = customerAccountMapper.getUserPostCount(userId);
        stats.put("postCount", postCount != null ? postCount : 0);
        
        // 获取用户获赞数
        Integer likeCount = customerAccountMapper.getUserLikeCount(userId);
        stats.put("likeCount", likeCount != null ? likeCount : 0);
        
        // 获取用户评论数
        Integer commentCount = customerAccountMapper.getUserCommentCount(userId);
        stats.put("commentCount", commentCount != null ? commentCount : 0);
        
        return stats;
    }

}
