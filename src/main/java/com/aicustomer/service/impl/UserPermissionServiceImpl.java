package com.aicustomer.service.impl;

import com.aicustomer.dto.MenuDefinition;
import com.aicustomer.dto.UserPermissionDTO;
import com.aicustomer.entity.User;
import com.aicustomer.mapper.UserMapper;
import com.aicustomer.service.UserPermissionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户权限服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPermissionServiceImpl implements UserPermissionService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

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

            // 2. 设置用户类型（根据角色模板）
            user.setUserType(getUserTypeByTemplate(dto.getRoleTemplate()));

            // 3. 保存用户权限配置到扩展字段（使用JSON格式）
            Map<String, Object> permissionConfig = new HashMap<>();
            permissionConfig.put("roleTemplate", dto.getRoleTemplate());

            // 获取菜单权限
            List<String> menuPermissions = dto.getMenuPermissions();
            if (menuPermissions == null || menuPermissions.isEmpty()) {
                // 如果没有指定菜单权限，使用角色模板的默认权限
                menuPermissions = MenuDefinition.getMenuIdsByRoleTemplate(dto.getRoleTemplate());
            }
            permissionConfig.put("menuPermissions", menuPermissions);

            // 数据权限配置
            if (dto.getDataPermission() != null) {
                permissionConfig.put("dataPermission", dto.getDataPermission());
            } else {
                // 默认数据权限
                UserPermissionDTO.DataPermissionConfig defaultDataPermission = new UserPermissionDTO.DataPermissionConfig();
                defaultDataPermission.setCustomerLevels(List.of(0)); // 所有等级
                defaultDataPermission.setDataScope("self"); // 仅自己的数据
                defaultDataPermission.setCanExport(false);
                defaultDataPermission.setCanDelete(false);
                permissionConfig.put("dataPermission", defaultDataPermission);
            }

            // 将权限配置序列化为JSON（这里简化处理，实际应该存储到专门的权限表）
            log.info("用户权限配置: {}", permissionConfig);

            // 4. 插入用户
            int result = userMapper.insert(user);

            // TODO: 将权限配置保存到用户权限表（需要创建新表）
            // 目前先记录日志
            log.info("创建用户成功: userId={}, username={}, roleTemplate={}",
                    user.getId(), user.getUsername(), dto.getRoleTemplate());

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

            // 2. 更新用户类型
            if (dto.getRoleTemplate() != null) {
                user.setUserType(getUserTypeByTemplate(dto.getRoleTemplate()));
            }

            user.setUpdateTime(LocalDateTime.now());

            // 3. 更新用户
            int result = userMapper.updateById(user);

            // 4. 更新权限配置
            Map<String, Object> permissionConfig = new HashMap<>();
            permissionConfig.put("roleTemplate", dto.getRoleTemplate());
            permissionConfig.put("menuPermissions", dto.getMenuPermissions());
            permissionConfig.put("dataPermission", dto.getDataPermission());

            log.info("更新用户权限成功: userId={}, username={}, roleTemplate={}",
                    user.getId(), user.getUsername(), dto.getRoleTemplate());

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

            // 根据用户类型推断角色模板
            String roleTemplate = getRoleTemplateByUserType(user.getUserType());
            dto.setRoleTemplate(roleTemplate);

            // 获取菜单权限（从角色模板获取默认权限）
            dto.setMenuPermissions(MenuDefinition.getMenuIdsByRoleTemplate(roleTemplate));

            // TODO: 从权限表获取实际的权限配置
            // 目前返回默认配置
            UserPermissionDTO.DataPermissionConfig dataPermission = new UserPermissionDTO.DataPermissionConfig();
            dataPermission.setCustomerLevels(List.of(0));
            dataPermission.setDataScope("self");
            dataPermission.setCanExport(false);
            dataPermission.setCanDelete(false);
            dto.setDataPermission(dataPermission);

            return dto;
        } catch (Exception e) {
            log.error("获取用户权限失败", e);
            throw new RuntimeException("获取用户权限失败: " + e.getMessage());
        }
    }

    /**
     * 根据角色模板获取用户类型
     */
    private Integer getUserTypeByTemplate(String roleTemplate) {
        switch (roleTemplate) {
            case "admin":
                return 1; // 管理员
            case "manager":
                return 2; // 经理
            case "employee":
                return 3; // 普通员工
            case "readonly":
                return 4; // 只读用户
            default:
                return 5; // 自定义
        }
    }

    /**
     * 根据用户类型获取角色模板
     */
    private String getRoleTemplateByUserType(Integer userType) {
        if (userType == null) {
            return "employee";
        }
        switch (userType) {
            case 1:
                return "admin";
            case 2:
                return "manager";
            case 3:
                return "employee";
            case 4:
                return "readonly";
            default:
                return "custom";
        }
    }
}
