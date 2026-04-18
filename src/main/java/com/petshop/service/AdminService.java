package com.petshop.service;

import com.petshop.entity.Admin;
import com.petshop.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Admin login(String email, String password) {
        Optional<Admin> adminOpt = adminRepository.findByUsername(email);
        if (adminOpt.isPresent() && passwordEncoder.matches(password, adminOpt.get().getPassword())) {
            return adminOpt.get();
        }
        return null;
    }

    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username).orElse(null);
    }

    public Admin create(Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setCreatedAt(java.time.LocalDateTime.now());
        admin.setUpdatedAt(java.time.LocalDateTime.now());
        return adminRepository.save(admin);
    }

    public Optional<Admin> findByUsernameOptional(String username) {
        return adminRepository.findByUsername(username);
    }
}
