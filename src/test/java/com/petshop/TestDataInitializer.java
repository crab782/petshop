package com.petshop;

import com.petshop.entity.Admin;
import com.petshop.entity.Merchant;
import com.petshop.entity.Service;
import com.petshop.entity.User;
import com.petshop.mapper.AdminMapper;
import com.petshop.mapper.MerchantMapper;
import com.petshop.mapper.ServiceMapper;
import com.petshop.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Profile("test")
public class TestDataInitializer implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static final String TEST_USER_USERNAME = "testuser";
    public static final String TEST_USER_PASSWORD = "password123";
    public static final String TEST_MERCHANT_NAME = "Test Merchant";
    public static final String TEST_MERCHANT_PASSWORD = "merchant123";
    public static final String TEST_ADMIN_USERNAME = "testadmin";
    public static final String TEST_ADMIN_PASSWORD = "admin123";

    @Override
    public void run(String... args) throws Exception {
        initTestUser();
        initTestMerchant();
        initTestService();
        initTestAdmin();
    }

    private void initTestUser() {
        User user = new User();
        user.setUsername(TEST_USER_USERNAME);
        user.setEmail("testuser@example.com");
        user.setPassword(passwordEncoder.encode(TEST_USER_PASSWORD));
        user.setPhone("13800138001");
        user.setStatus("active");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
    }

    private void initTestMerchant() {
        Merchant merchant = new Merchant();
        merchant.setName(TEST_MERCHANT_NAME);
        merchant.setContactPerson("Test Contact");
        merchant.setEmail("merchant@example.com");
        merchant.setPassword(passwordEncoder.encode(TEST_MERCHANT_PASSWORD));
        merchant.setPhone("13900139001");
        merchant.setAddress("Test Address 123");
        merchant.setStatus("approved");
        merchant.setCreatedAt(LocalDateTime.now());
        merchant.setUpdatedAt(LocalDateTime.now());
        merchantMapper.insert(merchant);
    }

    private void initTestService() {
        Merchant merchant = merchantMapper.selectList(null).stream()
                .findFirst()
                .orElse(null);

        if (merchant != null) {
            Service service = new Service();
            service.setMerchantId(merchant.getId());
            service.setName("Test Pet Grooming");
            service.setDescription("Professional pet grooming service");
            service.setPrice(new BigDecimal("99.99"));
            service.setDuration(60);
            service.setCategory("grooming");
            service.setStatus("enabled");
            service.setCreatedAt(LocalDateTime.now());
            service.setUpdatedAt(LocalDateTime.now());
            serviceMapper.insert(service);
        }
    }

    private void initTestAdmin() {
        Admin admin = new Admin();
        admin.setUsername(TEST_ADMIN_USERNAME);
        admin.setPassword(passwordEncoder.encode(TEST_ADMIN_PASSWORD));
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        adminMapper.insert(admin);
    }
}
