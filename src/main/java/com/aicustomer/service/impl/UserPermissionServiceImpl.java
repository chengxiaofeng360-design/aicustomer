package com.aicustomer.service.impl;

import com.aicustomer.dto.UserPermissionDTO;
import com.aicustomer.entity.User;
import com.aicustomer.mapper.UserMapper;
import com.aicustomer.service.UserPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户权限服务实现 - 简化版
 * 直接管理菜单权限和数据权限，不使用角色模板
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPermissionServiceImpl implements UserPermissionService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Boolean createUserWithPermission(UserPermissionDTO dto) {
        try {
            // 1. 创建用户基本信息
            User user = new User();
            user.setUsername(dto.getUsername());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setRealName(dto.getRealName());
            user.setEmail(dto.getEmail());
            user.setPhone(dto.getPhone());
            user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());
            user.setDeleted(0);
            user.setVersion(1);
            user.setUserType(3); // 默认普通用户

            // 2. 插入用户
            int result = userMapper.insert(user);

            // 3. 记录权限配置（实际应用中应该保存到权限表）
            log.info("创建用户成功: userId={}, username={}, menuPermissions={}, dataPermission={}",
                    user.getId(), user.getUsername(), dto.getMenuPermissions(), dto.getDataPermission());

            // TODO: 将权限配置保存到用户权限表

            return result > 0;
        } catch (Exception e) {
            log.error("创建用户失败", e);
            throw new RuntimeException("创建用户失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Boolean updateUserPermission(UserPermissionDTO dto) {
        try {
            if (dto.getId() == null) {
                throw new IllegalArgumentException("用户ID不能为空");
            }

            // 1. 更新用户基本信息
            User user = userMapper.selectById(dto.getId());
            if (user == null) {
                throw new IllegalArgumentException("用户不存在");
            }

            if (dto.getRealName() != null) {
                user.setRealName(dto.getRealName());
            }
            if (dto.getEmail() != null) {
                user.setEmail(dto.getEmail());
            }
            if (dto.getPhone() != null) {
                user.setPhone(dto.getPhone());
            }
            if (dto.getStatus() != null) {
                user.setStatus(dto.getStatus());
            }
            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            user.setUpdateTime(LocalDateTime.now());

            // 2. 更新用户
            int result = userMapper.updateById(user);

            // 3. 记录权限配置
            log.info("更新用户权限成功: userId={}, username={}, menuPermissions={}, dataPermission={}",
                    user.getId(), user.getUsername(), dto.getMenuPermissions(), dto.getDataPermission());

            // TODO: 更新权限表

            return result > 0;
        } catch (Exception e) {
            log.error("更新用户权限失败", e);
            throw new RuntimeException("更新用户权限失败: " + e.getMessage());
        }
    }

    @Override
    public UserPermissionDTO getUserPermission(Long userId) {
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new IllegalArgumentException("用户不存在");
            }

            UserPermissionDTO dto = new UserPermissionDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setRealName(user.getRealName());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhone());
            dto.setStatus(user.getStatus());

            // TODO: 从权限表获取实际的权限配置
            // 目前返回默认配置
            dto.setMenuPermissions(getDefaultMenuPermissions());

            UserPermissionDTO.DataPermissionConfig dataPermission = new UserPermissionDTO.DataPermissionConfig();
            dataPermission.setCanViewSensitive(false);
            dataPermission.setCanAccessVip(false);
            dataPermission.setCanAccessDiamond(false);
            dataPermission.setCanExport(false);
            dataPermission.setCanDelete(false);
            dataPermission.setCanViewAllData(false);
            dataPermission.setCanViewDepartmentData(false);
            dto.setDataPermission(dataPermission);

            return dto;
        } catch (Exception e) {
            log.error("获取用户权限失败", e);
            throw new RuntimeException("获取用户权限失败: " + e.getMessage());
        }
    }

    /**
     * 获取默认菜单权限
     */
    private List<String> getDefaultMenuPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add("home");
        permissions.add("customer-list");
        permissions.add("communication-list");
        permissions.add("message");
        return permissions;
    }
}
