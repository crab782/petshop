package com.petshop.test;

import com.petshop.entity.*;
import com.petshop.factory.TestDataFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestDataGenerator {

    private final List<Object> createdEntities = new ArrayList<>();

    // 生成测试用户
    public User generateUser() {
        User user = TestDataFactory.createUser();
        createdEntities.add(user);
        return user;
    }

    public User generateUser(Integer id) {
        User user = TestDataFactory.createUser(id);
        createdEntities.add(user);
        return user;
    }

    public User generateUser(Integer id, String username, String email) {
        User user = TestDataFactory.createUser(id, username, email);
        createdEntities.add(user);
        return user;
    }

    // 生成测试商家
    public Merchant generateMerchant() {
        Merchant merchant = TestDataFactory.createMerchant();
        createdEntities.add(merchant);
        return merchant;
    }

    public Merchant generateMerchant(Integer id) {
        Merchant merchant = TestDataFactory.createMerchant(id);
        createdEntities.add(merchant);
        return merchant;
    }

    public Merchant generateMerchant(String name, String email) {
        Merchant merchant = TestDataFactory.createMerchant(name, email);
        createdEntities.add(merchant);
        return merchant;
    }

    // 生成测试服务
    public Service generateService(Merchant merchant) {
        Service service = TestDataFactory.createService(merchant);
        createdEntities.add(service);
        return service;
    }

    public Service generateService(Integer id, Merchant merchant) {
        Service service = TestDataFactory.createService(id, merchant);
        createdEntities.add(service);
        return service;
    }

    public Service generateService(Integer id, Merchant merchant, String name, BigDecimal price) {
        Service service = TestDataFactory.createService(id, merchant, name, price);
        createdEntities.add(service);
        return service;
    }

    // 生成测试商品
    public Product generateProduct(Merchant merchant) {
        Product product = TestDataFactory.createProduct(merchant);
        createdEntities.add(product);
        return product;
    }

    public Product generateProduct(Integer id, Merchant merchant) {
        Product product = TestDataFactory.createProduct(id, merchant);
        createdEntities.add(product);
        return product;
    }

    public Product generateProduct(Integer id, Merchant merchant, String name, BigDecimal price, Integer stock) {
        Product product = TestDataFactory.createProduct(id, merchant, name, price, stock);
        createdEntities.add(product);
        return product;
    }

    // 生成测试分类
    public Category generateCategory(Integer merchantId) {
        Category category = TestDataFactory.createCategory(merchantId);
        createdEntities.add(category);
        return category;
    }

    public Category generateCategory(Integer id, Integer merchantId) {
        Category category = TestDataFactory.createCategory(id, merchantId);
        createdEntities.add(category);
        return category;
    }

    // 生成测试宠物
    public Pet generatePet(User user) {
        Pet pet = TestDataFactory.createPet(user);
        createdEntities.add(pet);
        return pet;
    }

    public Pet generatePet(Integer id, User user) {
        Pet pet = TestDataFactory.createPet(id, user);
        createdEntities.add(pet);
        return pet;
    }

    // 生成测试预约
    public Appointment generateAppointment(Merchant merchant) {
        Appointment appointment = TestDataFactory.createAppointment(merchant);
        createdEntities.add(appointment);
        return appointment;
    }

    public Appointment generateAppointment(Integer id, Merchant merchant) {
        Appointment appointment = TestDataFactory.createAppointment(id, merchant);
        createdEntities.add(appointment);
        return appointment;
    }

    // 生成测试订单
    public ProductOrder generateProductOrder(Merchant merchant) {
        ProductOrder order = TestDataFactory.createProductOrder(merchant);
        createdEntities.add(order);
        return order;
    }

    public ProductOrder generateProductOrder(Integer id, Merchant merchant) {
        ProductOrder order = TestDataFactory.createProductOrder(id, merchant);
        createdEntities.add(order);
        return order;
    }

    // 生成测试评价
    public Review generateReview(Merchant merchant) {
        Review review = TestDataFactory.createReview(merchant);
        createdEntities.add(review);
        return review;
    }

    public Review generateReview(Integer id, Merchant merchant) {
        Review review = TestDataFactory.createReview(id, merchant);
        createdEntities.add(review);
        return review;
    }

    // 生成测试商家设置
    public MerchantSettings generateMerchantSettings(Merchant merchant) {
        MerchantSettings settings = TestDataFactory.createMerchantSettings(merchant);
        createdEntities.add(settings);
        return settings;
    }

    // 清理测试数据
    public void cleanup() {
        // 这里可以添加清理测试数据的逻辑
        // 例如，通过Spring Data JPA删除创建的实体
        System.out.println("Cleaning up " + createdEntities.size() + " test entities");
        createdEntities.clear();
    }

    // 获取创建的实体数量
    public int getCreatedEntitiesCount() {
        return createdEntities.size();
    }

    // 获取创建的实体列表
    public List<Object> getCreatedEntities() {
        return new ArrayList<>(createdEntities);
    }
}
