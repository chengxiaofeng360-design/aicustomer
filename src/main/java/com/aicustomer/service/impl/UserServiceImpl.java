package com.aicustomer.service.impl;

import com.aicustomer.entity.User;
import com.aicustomer.mapper.RoleMapper;
import com.aicustomer.mapper.UserMapper;
import com.aicustomer.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
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
@org.springframework.context.annotation.DependsOn({ "databaseInitializer", "databaseMigrationConfig" })
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper; // Injected RoleMapper
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

        // 密码加密
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 默认密码
            user.setPassword(passwordEncoder.encode("123456"));
        }

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

    @Override
    @PostConstruct
    @Transactional(rollbackFor = Exception.class)
    public void initAdmin() {
        String adminUsername = "admin";
        User admin = userMapper.findByUsername(adminUsername);

        if (admin == null) {
            log.info("正在初始化超级管理员账号...");
            User newAdmin = new User();
            newAdmin.setUsername(adminUsername);
            newAdmin.setPassword(passwordEncoder.encode("123456"));
            newAdmin.setRealName("超级管理员");
            newAdmin.setStatus(1);
            newAdmin.setUserType(1); // 1:管理员
            newAdmin.setCreateTime(LocalDateTime.now());
            newAdmin.setUpdateTime(LocalDateTime.now());
            newAdmin.setCreateBy("system");
            newAdmin.setUpdateBy("system");
            newAdmin.setDeleted(0);
            newAdmin.setVersion(1);

            userMapper.insert(newAdmin);
            log.info("超级管理员初始化完成: admin / 123456");
        } else {
            // 检查现有管理员密码是否为有效的 BCrypt 格式
            String currentPwd = admin.getPassword();
            if (currentPwd == null || !currentPwd.startsWith("$2a$")) {
                log.info("检测到管理员账号存在但密码未加密，正在重置密码为 123456...");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setUpdateTime(LocalDateTime.now());
                userMapper.updateById(admin);
                log.info("管理员密码重置完成");
            }
        }

        // 初始化 staff 账号
        String staffUsername = "staff";
        User staff = userMapper.findByUsername(staffUsername);
        if (staff == null) {
            log.info("正在初始化演示业务员账号...");
            User newStaff = new User();
            newStaff.setUsername(staffUsername);
            newStaff.setPassword(passwordEncoder.encode("123456"));
            newStaff.setRealName("演示业务员");
            newStaff.setStatus(1);
            newStaff.setUserType(2); // 2:业务员
            newStaff.setCreateTime(LocalDateTime.now());
            newStaff.setUpdateTime(LocalDateTime.now());
            newStaff.setCreateBy("system");
            newStaff.setUpdateBy("system");
            newStaff.setDeleted(0);
            newStaff.setVersion(1);

            // 为 staff 账号设置默认权限 (消息中心, 客户管理, AI聊天, 知识库)
            newStaff.setPermissionSettings(
                    "{\"menuPermissions\":[\"customer\",\"message\",\"ai-chat\",\"knowledge\"],\"dataPermission\":{\"canViewSensitive\":false,\"canImport\":false,\"canDelete\":false}}");

            userMapper.insert(newStaff);
            log.info("演示业务员初始化完成: staff / 123456");
        } else {
            String currentPwd = staff.getPassword();
            if (currentPwd == null || !currentPwd.startsWith("$2a$")) {
                log.info("检测到 staff 账号存在但密码未加密，正在重置密码为 123456...");
                staff.setPassword(passwordEncoder.encode("123456"));
                staff.setUpdateTime(LocalDateTime.now());
                userMapper.updateById(staff);
                log.info("staff 密码重置完成");
            }
            // 确保现有 staff 账号也有正确的权限配置
            if (staff.getPermissionSettings() == null || !staff.getPermissionSettings().contains("canImport")) {
                log.info("检测到 staff 权限配置缺失或版本过旧，正在更新...");
                staff.setPermissionSettings(
                        "{\"menuPermissions\":[\"customer\",\"message\",\"ai-chat\",\"knowledge\"],\"dataPermission\":{\"canViewSensitive\":false,\"canImport\":false,\"canDelete\":false}}");
                staff.setUpdateTime(LocalDateTime.now());
                userMapper.updateById(staff);
            }
        }
    }
}