package com.petshop.service;

import com.petshop.entity.Permission;
import com.petshop.entity.Role;
import com.petshop.repository.PermissionRepository;
import com.petshop.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;

    public Page<Role> getRoles(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return roleRepository.findAllByOrderByIdDesc(pageable);
    }

    public Role findById(Integer id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Transactional
    public Role createRole(String name, String description, List<Integer> permissionIds) {
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        
        if (permissionIds != null && !permissionIds.isEmpty()) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
            role.setPermissions(permissions);
        }
        
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Integer id, String name, String description, List<Integer> permissionIds) {
        Role role = roleRepository.findById(id).orElse(null);
        if (role == null) {
            return null;
        }
        
        role.setName(name);
        role.setDescription(description);
        role.setUpdatedAt(LocalDateTime.now());
        
        if (permissionIds != null) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
            role.setPermissions(permissions);
        } else {
            role.getPermissions().clear();
        }
        
        return roleRepository.save(role);
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
        return permissionRepository.findAll();
    }
}
