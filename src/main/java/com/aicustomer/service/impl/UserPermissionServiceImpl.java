package com.aicustomer.service.impl;

import com.aicustomer.dto.UserPermissionDTO;
import com.aicustomer.entity.User;
import com.aicustomer.mapper.UserMapper;
import com.aicustomer.service.UserPermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户权限服务实现 - 简化版
 * 直接管理菜单权限和数据权限，不使用角色模板
 * 权限配置以JSON格式存储在用户的 permissionSettings 字段中
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
            user.setUserType(3); // 默认普通用户

            // 2. 序列化权限配置
            Map<String, Object> permissionMap = new HashMap<>();
            permissionMap.put("menuPermissions", dto.getMenuPermissions());
            permissionMap.put("dataPermission", dto.getDataPermission());
            String permissionJson = objectMapper.writeValueAsString(permissionMap);
            user.setPermissionSettings(permissionJson);

            // 3. 插入用户
            int result = userMapper.insert(user);

            log.info("创建用户成功: userId={}, username={}", user.getId(), user.getUsername());
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

            // 1. 获取现有用户
            User user = userMapper.selectById(dto.getId());
            if (user == null) {
                throw new IllegalArgumentException("用户不存在");
            }

            // 2. 更新基本信息
            if (dto.getRealName() != null)
                user.setRealName(dto.getRealName());
            if (dto.getEmail() != null)
                user.setEmail(dto.getEmail());
            if (dto.getPhone() != null)
                user.setPhone(dto.getPhone());
            if (dto.getStatus() != null)
                user.setStatus(dto.getStatus());
            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            user.setUpdateTime(LocalDateTime.now());

            // 3. 更新权限配置
            if (dto.getMenuPermissions() != null || dto.getDataPermission() != null) {
                Map<String, Object> permissionMap = new HashMap<>();
                // 如果只传了一部分，应该先反序列化旧的再合并？简化版直接覆盖，假定前端传的是全量
                permissionMap.put("menuPermissions", dto.getMenuPermissions());
                permissionMap.put("dataPermission", dto.getDataPermission());
                String permissionJson = objectMapper.writeValueAsString(permissionMap);
                user.setPermissionSettings(permissionJson);
            }

            // 4. 更新数据库
            int result = userMapper.updateById(user);
            log.info("更新用户权限成功: userId={}", user.getId());
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

            // 反序列化权限配置
            String permissionJson = user.getPermissionSettings();
            if (permissionJson != null && !permissionJson.isEmpty()) {
                try {
                    Map<String, Object> map = objectMapper.readValue(permissionJson, Map.class);

                    // 提取菜单权限
                    if (map.containsKey("menuPermissions")) {
                        dto.setMenuPermissions((List<String>) map.get("menuPermissions"));
                    }

                    // 提取数据权限
                    if (map.containsKey("dataPermission")) {
                        // Jackson会将内嵌对象转为Map
                        Map<String, Object> dataMap = (Map<String, Object>) map.get("dataPermission");
                        UserPermissionDTO.DataPermissionConfig config = new UserPermissionDTO.DataPermissionConfig();

                        // 安全地获取Boolean值
                        config.setCanExport(getBoolean(dataMap, "canExport"));
                        config.setCanDelete(getBoolean(dataMap, "canDelete"));
                        // 兼容旧字段，默认为false
                        config.setCanViewSensitive(getBoolean(dataMap, "canViewSensitive"));
                        config.setCanAccessVip(getBoolean(dataMap, "canAccessVip"));
                        config.setCanAccessDiamond(getBoolean(dataMap, "canAccessDiamond"));
                        config.setCanViewAllData(getBoolean(dataMap, "canViewAllData"));
                        config.setCanViewDepartmentData(getBoolean(dataMap, "canViewDepartmentData"));

                        dto.setDataPermission(config);
                    }
                } catch (Exception e) {
                    log.error("解析权限配置失败: " + permissionJson, e);
                    // 解析失败时返回默认
                    dto.setMenuPermissions(getDefaultMenuPermissions());
                    dto.setDataPermission(new UserPermissionDTO.DataPermissionConfig());
                }
            } else {
                // 无配置时返回默认
                dto.setMenuPermissions(getDefaultMenuPermissions());
                dto.setDataPermission(new UserPermissionDTO.DataPermissionConfig());
            }

            return dto;
        } catch (Exception e) {
            log.error("获取用户权限失败", e);
            throw new RuntimeException("获取用户权限失败: " + e.getMessage());
        }
    }

    private Boolean getBoolean(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Boolean) {
            return (Boolean) val;
        }
        return false;
    }

    /**
     * 获取默认菜单权限
     */
    private List<String> getDefaultMenuPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add("home");
        return permissions;
    }
}
