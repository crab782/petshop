package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.entity.Permission;
import com.petshop.entity.Role;
import com.petshop.mapper.PermissionMapper;
import com.petshop.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService {
    @Autowired
    private RoleMapper roleRepository;
    
    @Autowired
    private PermissionMapper permissionRepository;

    public Page<Role> getRoles(int page, int pageSize) {
        Page<Role> rolePage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Role::getId);
        return roleRepository.selectPage(rolePage, wrapper);
    }

    public Role findById(Integer id) {
        return roleRepository.selectById(id);
    }

    @Transactional
    public Role createRole(String name, String description, List<Integer> permissionIds) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        
        if (permissionIds != null && !permissionIds.isEmpty()) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.selectList(null));
            role.setPermissions(permissions);
        }
        
        roleRepository.insert(role);
        return role;
    }

    @Transactional
    public Role updateRole(Integer id, String name, String description, List<Integer> permissionIds) {
        Role role = roleRepository.selectById(id);
        if (role == null) {
            return null;
        }
        
        role.setName(name);
        role.setDescription(description);
        role.setUpdatedAt(LocalDateTime.now());
        
        if (permissionIds != null) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.selectList(null));
            role.setPermissions(permissions);
        } else {
            role.getPermissions().clear();
        }
        
        roleRepository.updateById(role);
        return role;
    }

    @Transactional
    public void deleteRole(Integer id) {
        roleRepository.deleteById(id);
    }

    @Transactional
    public int batchDeleteRoles(List<Integer> ids) {
        int count = 0;
        for (Integer id : ids) {
            if (roleRepository.existsById(id)) {
                roleRepository.deleteById(id);
                count++;
            }
        }
        return count;
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.selectList(null);
    }
}
