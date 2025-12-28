package com.aicustomer.service.impl;

import com.aicustomer.entity.Permission;
import com.aicustomer.mapper.PermissionMapper;
import com.aicustomer.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限服务实现类
 * 
 * @author AI Customer Management System
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;

    @Override
    public Permission getById(Long id) {
        return permissionMapper.selectById(id);
    }

    @Override
    public List<Permission> getList(Permission permission) {
        return permissionMapper.selectList(permission);
    }

    @Override
    public List<Permission> getByRoleId(Long roleId) {
        return permissionMapper.selectByRoleId(roleId);
    }

    @Override
    public List<Permission> getByUserId(Long userId) {
        return permissionMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(Permission permission) {
        permission.setCreateTime(LocalDateTime.now());
        permission.setUpdateTime(LocalDateTime.now());
        permission.setDeleted(0);
        permission.setVersion(1);
        if (permission.getStatus() == null) {
            permission.setStatus(1);
        }
        return permissionMapper.insert(permission) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Permission permission) {
        return permissionMapper.update(permission) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        return permissionMapper.deleteById(id) > 0;
    }

    @Override
    public List<Object> getTree() {
        List<Permission> allPermissions = permissionMapper.selectList(new Permission());
        return buildTree(allPermissions, 0L);
    }

    private List<Object> buildTree(List<Permission> all, Long parentId) {
        List<Object> tree = new ArrayList<>();
        for (Permission p : all) {
            if (parentId.equals(p.getParentId()) || (parentId == 0 && p.getParentId() == null)) {
                Map<String, Object> node = new HashMap<>();
                node.put("id", p.getId());
                node.put("label", p.getPermissionName());
                node.put("code", p.getPermissionCode());
                node.put("children", buildTree(all, p.getId()));
                tree.add(node);
            }
        }
        return tree;
    }
}
