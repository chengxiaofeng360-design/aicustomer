package com.aicustomer.service.impl;

import com.aicustomer.dto.UserPermissionDTO;
import com.aicustomer.entity.User;
import com.aicustomer.mapper.UserMapper;
import com.aicustomer.service.UserPermissionService;
import com.fasterxml.jackson.core.type.TypeReference;
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
            // 1. 检查用户名是否存在
            if (userMapper.findByUsername(dto.getUsername()) != null) {
                throw new RuntimeException("用户名已存在: " + dto.getUsername());
            }

            // 1. 创建用户基本信息
            User user = new User();
            user.setUsername(dto.getUsername());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            // 真实姓名默认为用户名
            user.setRealName(dto.getRealName() != null ? dto.getRealName() : dto.getUsername());
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

            // 2. 更新基本信息 (如果传了就更新)
            if (dto.getRealName() != null)
                user.setRealName(dto.getRealName());
            if (dto.getEmail() != null)
                user.setEmail(dto.getEmail());
            if (dto.getPhone() != null)
                user.setPhone(dto.getPhone());
            if (dto.getStatus() != null)
                user.setStatus(dto.getStatus());
            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            // 3. 强制覆写权限配置 (不再进行不可靠的合并，以前端传的数据为准)
            // 只要前端传了菜单权限或数据权限，就进行保存
            if (dto.getMenuPermissions() != null || dto.getDataPermission() != null) {
                Map<String, Object> permissionMap = new HashMap<>();

                // 菜单权限：如果没有传，则保留原有的；如果传了空列表，也视为更新
                if (dto.getMenuPermissions() != null) {
                    permissionMap.put("menuPermissions", dto.getMenuPermissions());
                } else {
                    // 尝试保留原有的菜单权限
                    try {
                        Map<String, Object> oldMap = objectMapper.readValue(user.getPermissionSettings(),
                                new TypeReference<Map<String, Object>>() {
                                });
                        permissionMap.put("menuPermissions", oldMap.get("menuPermissions"));
                    } catch (Exception e) {
                    }
                }

                // 数据权限：核心修复点，直接转换 DTO 为 Map 存储，避免 Jackson 序列化嵌套问题
                if (dto.getDataPermission() != null) {
                    permissionMap.put("dataPermission", dto.getDataPermission());
                } else {
                    // 尝试保留原有的数据权限
                    try {
                        Map<String, Object> oldMap = objectMapper.readValue(user.getPermissionSettings(),
                                new TypeReference<Map<String, Object>>() {
                                });
                        permissionMap.put("dataPermission", oldMap.get("dataPermission"));
                    } catch (Exception e) {
                    }
                }

                user.setPermissionSettings(objectMapper.writeValueAsString(permissionMap));
            }

            user.setUpdateTime(LocalDateTime.now());

            // 4. 更新数据库
            int result = userMapper.updateById(user);
            log.info("更新用户记录成功: userId={}, 影响行数={}", user.getId(), result);
            return result > 0;
        } catch (Exception e) {
            log.error("更新用户权限致命失败", e);
            throw new RuntimeException("更新失败: " + e.getMessage());
        }
    }

    @Override
    public UserPermissionDTO getUserPermission(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return convertToDTO(user);
    }

    @Override
    public UserPermissionDTO getUserPermissionByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return convertToDTO(user);
    }

    private UserPermissionDTO convertToDTO(User user) {
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
                Map<String, Object> map = objectMapper.readValue(permissionJson,
                        new TypeReference<Map<String, Object>>() {
                        });

                // 转换菜单权限
                Object menuPerms = map.get("menuPermissions");
                if (menuPerms instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> perms = (List<String>) menuPerms;
                    dto.setMenuPermissions(perms);
                }

                // 转换数据权限 (手动映射每个字段，确保稳定性)
                Object dataPerm = map.get("dataPermission");
                if (dataPerm instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> dataMap = (Map<String, Object>) dataPerm;
                    UserPermissionDTO.DataPermissionConfig config = new UserPermissionDTO.DataPermissionConfig();

                    config.setCanViewSensitive(getBoolean(dataMap, "canViewSensitive"));
                    config.setCanExport(getBoolean(dataMap, "canExport"));
                    config.setCanDelete(getBoolean(dataMap, "canDelete"));
                    config.setCanAccessVip(getBoolean(dataMap, "canAccessVip"));
                    config.setCanAccessDiamond(getBoolean(dataMap, "canAccessDiamond"));
                    config.setCanViewAllData(getBoolean(dataMap, "canViewAllData"));
                    config.setCanViewDepartmentData(getBoolean(dataMap, "canViewDepartmentData"));

                    dto.setDataPermission(config);
                }
            } catch (Exception e) {
                log.error("解析用户权限配置失败: userId={}", user.getId(), e);
            }
        }

        // 兜底逻辑：只有在完全没有解析到权限配置时（null），才应用默认值
        // 注意：不要在权限列表为空时也覆盖，因为空列表可能是用户的真实配置
        if (dto.getMenuPermissions() == null) {
            if ("admin".equals(user.getUsername())) {
                dto.setMenuPermissions(
                        java.util.Arrays.asList("customer", "message", "team", "ai-analysis", "ai-recommendations",
                                "knowledge", "ai-chat", "system"));
                UserPermissionDTO.DataPermissionConfig adminConfig = new UserPermissionDTO.DataPermissionConfig();
                adminConfig.setCanViewSensitive(true);
                adminConfig.setCanExport(true);
                adminConfig.setCanDelete(true);
                adminConfig.setCanAccessVip(true);
                adminConfig.setCanAccessDiamond(true);
                adminConfig.setCanViewAllData(true);
                adminConfig.setCanViewDepartmentData(true);
                dto.setDataPermission(adminConfig);
            } else {
                dto.setMenuPermissions(getDefaultMenuPermissions());
            }
        }

        // 确保数据权限不为null
        if (dto.getDataPermission() == null) {
            dto.setDataPermission(new UserPermissionDTO.DataPermissionConfig());
        }

        return dto;
    }

    private Boolean getBoolean(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Boolean) {
            return (Boolean) val;
        }
        return false;
    }

    @Override
    public Object getAllMenus() {
        return com.aicustomer.dto.MenuDefinition.getAllMenus();
    }

    @Override
    public Object getRoleTemplates() {
        List<Map<String, String>> templates = new ArrayList<>();

        Map<String, String> admin = new HashMap<>();
        admin.put("label", "管理员");
        admin.put("value", "admin");
        admin.put("description", "拥有系统所有功能权限");
        templates.add(admin);

        Map<String, String> manager = new HashMap<>();
        manager.put("label", "经理");
        manager.put("value", "manager");
        manager.put("description", "拥有业务管理权限，无系统配置权限");
        templates.add(manager);

        Map<String, String> employee = new HashMap<>();
        employee.put("label", "普通员工");
        employee.put("value", "employee");
        employee.put("description", "拥有基础业务操作权限");
        templates.add(employee);

        Map<String, String> readonly = new HashMap<>();
        readonly.put("label", "只读用户");
        readonly.put("value", "readonly");
        readonly.put("description", "仅拥有数据查看权限");
        templates.add(readonly);

        Map<String, String> custom = new HashMap<>();
        custom.put("label", "自定义");
        custom.put("value", "custom");
        custom.put("description", "手动配置详细权限");
        templates.add(custom);

        return templates;
    }

    @Override
    public Object getRoleTemplateMenus(String template) {
        return com.aicustomer.dto.MenuDefinition.getMenuIdsByRoleTemplate(template);
    }

    @Override
    public Object getCustomerLevels() {
        List<Map<String, Object>> levels = new ArrayList<>();

        Map<String, Object> l1 = new HashMap<>(); // 1=普通
        l1.put("label", "普通客户");
        l1.put("value", 1);
        l1.put("description", "基础等级");
        levels.add(l1);

        Map<String, Object> l2 = new HashMap<>(); // 2=VIP
        l2.put("label", "VIP客户");
        l2.put("value", 2);
        l2.put("description", "中级等级");
        levels.add(l2);

        Map<String, Object> l3 = new HashMap<>(); // 3=钻石
        l3.put("label", "钻石客户");
        l3.put("value", 3);
        l3.put("description", "高级等级");
        levels.add(l3);

        return levels;
    }

    /**
     * 获取默认菜单权限
     */
    private List<String> getDefaultMenuPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add("customer");
        permissions.add("message");
        permissions.add("knowledge");
        permissions.add("ai-chat");
        return permissions;
    }
}
