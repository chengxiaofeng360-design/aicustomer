package com.aicustomer.service.impl;

import com.aicustomer.entity.User;
import com.aicustomer.mapper.UserMapper;
import com.aicustomer.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final com.aicustomer.mapper.RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public List<User> getUserList() {
        User query = new User();
        query.setStatus(1);
        return userMapper.selectList(query);
    }

    @Override
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            // 获取用户的角色ID列表
            List<com.aicustomer.entity.Role> roles = roleMapper.selectByUserId(id);
            if (roles != null) {
                user.setRoleIds(roles.stream().map(com.aicustomer.entity.Role::getId).collect(Collectors.toList()));
            }
        }
        return user;
    }

    @Override
    public List<User> getList(User user) {
        log.info("正在获取用户列表，查询条件: {}", user);
        List<User> list = userMapper.selectList(user);
        log.info("用户列表获取成功，记录数: {}", list.size());
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(User user) {
        // 检查用户名是否存在
        if (userMapper.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 密码加密 - 必须提供密码
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setDeleted(0);
        user.setVersion(1);
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        log.info("正在创建新用户: {}", user.getUsername());
        boolean success = userMapper.insert(user) > 0;
        log.info("用户创建{}, ID: {}", success ? "成功" : "失败", user.getId());

        // 如果成功且传了角色ID，则分配角色
        if (success && user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            assignRoles(user.getId(), user.getRoleIds());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(User user) {
        // 如果修改了密码，需要加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            // 只有当密码字段不为空时才更新密码
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null); // 防止将空密码更新到数据库
        }

        user.setUpdateTime(LocalDateTime.now());
        boolean success = userMapper.updateById(user) > 0;

        // 如果更新了角色列表（传了 roleIds），则同步更新角色关联
        if (user.getRoleIds() != null) {
            assignRoles(user.getId(), user.getRoleIds());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(Long userId, List<Long> roleIds) {
        // 先删除原有角色
        userMapper.deleteUserRoles(userId);

        // 批量插入新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                userMapper.insertUserRole(userId, roleId);
            }
        }
        return true;
    }

    // ⚠️ 已删除自动初始化 admin/staff 用户的方法
    // 此方法已禁用 - 防止自动创建账号
    // 如需创建初始管理员账号，请通过系统界面手动创建或使用SQL脚本

}
