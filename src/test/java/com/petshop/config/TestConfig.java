package com.petshop.config;

import com.petshop.service.*;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestConfig {

    @Bean
    public MerchantService merchantService() {
        return Mockito.mock(MerchantService.class);
    }

    @Bean
    public ServiceService serviceService() {
        return Mockito.mock(ServiceService.class);
    }

    @Bean
    public ProductService productService() {
        return Mockito.mock(ProductService.class);
    }

    @Bean
    public CategoryService categoryService() {
        return Mockito.mock(CategoryService.class);
    }

    @Bean
    public AppointmentService appointmentService() {
        return Mockito.mock(AppointmentService.class);
    }

    @Bean
    public ProductOrderService productOrderService() {
        return Mockito.mock(ProductOrderService.class);
    }

    @Bean
    public ReviewService reviewService() {
        return Mockito.mock(ReviewService.class);
    }

    @Bean
    public MerchantSettingsService merchantSettingsService() {
        return Mockito.mock(MerchantSettingsService.class);
    }

    @Bean
    public MerchantStatsService merchantStatsService() {
        return Mockito.mock(MerchantStatsService.class);
    }

    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    public PetService petService() {
        return Mockito.mock(PetService.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Mockito.mock(PasswordEncoder.class);
    }
}
