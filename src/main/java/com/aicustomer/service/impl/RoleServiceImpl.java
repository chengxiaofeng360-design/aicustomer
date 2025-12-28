package com.aicustomer.service.impl;

import com.aicustomer.entity.Role;
import com.aicustomer.mapper.RoleMapper;
import com.aicustomer.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色服务实现类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    @Override
    public Role getById(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    public List<Role> getList(Role role) {
        return roleMapper.selectList(role);
    }

    @Override
    public List<Role> getByUserId(Long userId) {
        return roleMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(Role role) {
        // 检查角色名或编码是否存在
        if (roleMapper.selectByName(role.getRoleName()) != null) {
            throw new RuntimeException("角色名称已存在");
        }
        if (roleMapper.selectByCode(role.getRoleCode()) != null) {
            throw new RuntimeException("角色编码已存在");
        }

        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        role.setDeleted(0);
        role.setVersion(1);
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        return roleMapper.insert(role) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Role role) {
        return roleMapper.update(role) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        // 删除角色前应该检查是否有用户关联（此处暂略，直接删除）
        // 同时删除角色关联的权限
        roleMapper.deleteRolePermissions(id);
        return roleMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissions(Long roleId, List<Long> permissionIds) {
        // 先删除原有权限
        roleMapper.deleteRolePermissions(roleId);

        // 批量插入新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                roleMapper.insertRolePermission(roleId, permissionId);
            }
        }
        return true;
    }
}
