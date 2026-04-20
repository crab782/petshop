package com.petshop.config;

import com.petshop.entity.Admin;
import com.petshop.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private AdminMapper adminMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // 检查是否已存在管理员账号
        Admin existingAdmin = adminMapper.selectByUsername("admin");
        if (existingAdmin == null) {
            // 创建默认管理员账号
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("251010"));
            admin.setCreatedAt(java.time.LocalDateTime.now());
            admin.setUpdatedAt(java.time.LocalDateTime.now());
            adminMapper.insert(admin);
            System.out.println("默认管理员账号已创建：admin / 251010");
        }
    }
}