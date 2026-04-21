package com.petshop.factory;

import com.petshop.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class TestDataFactory {

    private static final String DEFAULT_MERCHANT_NAME = "测试商家";
    private static final String DEFAULT_MERCHANT_EMAIL = "test@merchant.com";
    private static final String DEFAULT_MERCHANT_PHONE = "13800138000";
    private static final String DEFAULT_MERCHANT_PASSWORD = "password123";
    private static final String DEFAULT_MERCHANT_ADDRESS = "测试地址123号";
    private static final String DEFAULT_MERCHANT_CONTACT_PERSON = "测试联系人";
    private static final String DEFAULT_MERCHANT_LOGO = "http://example.com/logo.png";

    private static final String DEFAULT_SERVICE_NAME = "测试服务";
    private static final String DEFAULT_SERVICE_DESCRIPTION = "测试服务描述";
    private static final BigDecimal DEFAULT_SERVICE_PRICE = new BigDecimal("99.99");
    private static final Integer DEFAULT_SERVICE_DURATION = 60;
    private static final String DEFAULT_SERVICE_IMAGE = "http://example.com/service.png";

    private static final String DEFAULT_PRODUCT_NAME = "测试商品";
    private static final String DEFAULT_PRODUCT_DESCRIPTION = "测试商品描述";
    private static final BigDecimal DEFAULT_PRODUCT_PRICE = new BigDecimal("199.99");
    private static final Integer DEFAULT_PRODUCT_STOCK = 100;
    private static final String DEFAULT_PRODUCT_IMAGE = "http://example.com/product.png";

    private static final String DEFAULT_CATEGORY_NAME = "测试分类";
    private static final String DEFAULT_CATEGORY_ICON = "http://example.com/icon.png";
    private static final String DEFAULT_CATEGORY_DESCRIPTION = "测试分类描述";

    private static final String DEFAULT_USER_USERNAME = "测试用户";
    private static final String DEFAULT_USER_EMAIL = "test@user.com";
    private static final String DEFAULT_USER_PHONE = "13900139000";
    private static final String DEFAULT_USER_PASSWORD = "user123";

    private static final String DEFAULT_PET_NAME = "测试宠物";
    private static final String DEFAULT_PET_TYPE = "狗";
    private static final String DEFAULT_PET_BREED = "金毛";
    private static final Integer DEFAULT_PET_AGE = 3;

    private static final Integer DEFAULT_RATING = 5;
    private static final String DEFAULT_COMMENT = "测试评价内容";

    private TestDataFactory() {
    }

    public static Merchant createMerchant() {
        Merchant merchant = new Merchant();
        merchant.setId(1);
        merchant.setName(DEFAULT_MERCHANT_NAME);
        merchant.setEmail(DEFAULT_MERCHANT_EMAIL);
        merchant.setPhone(DEFAULT_MERCHANT_PHONE);
        merchant.setPassword(DEFAULT_MERCHANT_PASSWORD);
        merchant.setAddress(DEFAULT_MERCHANT_ADDRESS);
        merchant.setContactPerson(DEFAULT_MERCHANT_CONTACT_PERSON);
        merchant.setLogo(DEFAULT_MERCHANT_LOGO);
        merchant.setStatus("approved");
        merchant.setCreatedAt(LocalDateTime.now());
        merchant.setUpdatedAt(LocalDateTime.now());
        return merchant;
    }

    public static Merchant createMerchant(Integer id) {
        Merchant merchant = createMerchant();
        merchant.setId(id);
        return merchant;
    }

    public static Merchant createMerchant(String name, String email) {
        Merchant merchant = createMerchant();
        merchant.setName(name);
        merchant.setEmail(email);
        return merchant;
    }

    public static Merchant createMerchant(Integer id, String name, String email) {
        Merchant merchant = createMerchant();
        merchant.setId(id);
        merchant.setName(name);
        merchant.setEmail(email);
        return merchant;
    }

    public static Merchant createMerchant(Integer id, String name, String email, String phone, String address) {
        Merchant merchant = createMerchant();
        merchant.setId(id);
        merchant.setName(name);
        merchant.setEmail(email);
        merchant.setPhone(phone);
        merchant.setAddress(address);
        return merchant;
    }

    public static Merchant createMerchant(Integer id, String name, String email, String phone, 
                                          String address, String contactPerson, String status) {
        Merchant merchant = createMerchant();
        merchant.setId(id);
        merchant.setName(name);
        merchant.setEmail(email);
        merchant.setPhone(phone);
        merchant.setAddress(address);
        merchant.setContactPerson(contactPerson);
        merchant.setStatus(status);
        return merchant;
    }

    public static Service createService(Merchant merchant) {
        Service service = new Service();
        service.setId(1);
        service.setMerchant(merchant);
        service.setName(DEFAULT_SERVICE_NAME);
        service.setDescription(DEFAULT_SERVICE_DESCRIPTION);
        service.setPrice(DEFAULT_SERVICE_PRICE);
        service.setDuration(DEFAULT_SERVICE_DURATION);
        service.setImage(DEFAULT_SERVICE_IMAGE);
        service.setStatus("enabled");
        service.setCreatedAt(LocalDateTime.now());
        service.setUpdatedAt(LocalDateTime.now());
        return service;
    }

    public static Service createService(Integer id, Merchant merchant) {
        Service service = createService(merchant);
        service.setId(id);
        return service;
    }

    public static Service createService(Integer id, Merchant merchant, String name) {
        Service service = createService(merchant);
        service.setId(id);
        service.setName(name);
        return service;
    }

    public static Service createService(Integer id, Merchant merchant, String name, BigDecimal price) {
        Service service = createService(merchant);
        service.setId(id);
        service.setName(name);
        service.setPrice(price);
        return service;
    }

    public static Service createService(Integer id, Merchant merchant, String name, String description, 
                                        BigDecimal price, Integer duration) {
        Service service = createService(merchant);
        service.setId(id);
        service.setName(name);
        service.setDescription(description);
        service.setPrice(price);
        service.setDuration(duration);
        return service;
    }

    public static Service createService(Integer id, Merchant merchant, String name, String description, 
                                        BigDecimal price, Integer duration, String status) {
        Service service = createService(merchant);
        service.setId(id);
        service.setName(name);
        service.setDescription(description);
        service.setPrice(price);
        service.setDuration(duration);
        service.setStatus(status);
        return service;
    }

    public static Product createProduct(Merchant merchant) {
        Product product = new Product();
        product.setId(1);
        product.setMerchant(merchant);
        product.setName(DEFAULT_PRODUCT_NAME);
        product.setDescription(DEFAULT_PRODUCT_DESCRIPTION);
        product.setPrice(DEFAULT_PRODUCT_PRICE);
        product.setStock(DEFAULT_PRODUCT_STOCK);
        product.setImage(DEFAULT_PRODUCT_IMAGE);
        product.setStatus("enabled");
        product.setLowStockThreshold(10);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }

    public static Product createProduct(Integer id, Merchant merchant) {
        Product product = createProduct(merchant);
        product.setId(id);
        return product;
    }

    public static Product createProduct(Integer id, Merchant merchant, String name) {
        Product product = createProduct(merchant);
        product.setId(id);
        product.setName(name);
        return product;
    }

    public static Product createProduct(Integer id, Merchant merchant, String name, BigDecimal price, Integer stock) {
        Product product = createProduct(merchant);
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        return product;
    }

    public static Product createProduct(Integer id, Merchant merchant, String name, String description, 
                                        BigDecimal price, Integer stock, String status) {
        Product product = createProduct(merchant);
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setStatus(status);
        return product;
    }

    public static Category createCategory(Integer merchantId) {
        Category category = new Category();
        category.setId(1);
        category.setMerchantId(merchantId);
        category.setName(DEFAULT_CATEGORY_NAME);
        category.setIcon(DEFAULT_CATEGORY_ICON);
        category.setDescription(DEFAULT_CATEGORY_DESCRIPTION);
        category.setSort(0);
        category.setStatus("enabled");
        category.setProductCount(0);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return category;
    }

    public static Category createCategory(Integer id, Integer merchantId) {
        Category category = createCategory(merchantId);
        category.setId(id);
        return category;
    }

    public static Category createCategory(Integer id, Integer merchantId, String name) {
        Category category = createCategory(merchantId);
        category.setId(id);
        category.setName(name);
        return category;
    }

    public static Category createCategory(Integer id, Integer merchantId, String name, String icon, Integer sort) {
        Category category = createCategory(merchantId);
        category.setId(id);
        category.setName(name);
        category.setIcon(icon);
        category.setSort(sort);
        return category;
    }

    public static Category createCategory(Integer id, Integer merchantId, String name, String icon, 
                                          String description, Integer sort, String status) {
        Category category = createCategory(merchantId);
        category.setId(id);
        category.setName(name);
        category.setIcon(icon);
        category.setDescription(description);
        category.setSort(sort);
        category.setStatus(status);
        return category;
    }

    public static User createUser() {
        User user = new User();
        user.setId(1);
        user.setUsername(DEFAULT_USER_USERNAME);
        user.setEmail(DEFAULT_USER_EMAIL);
        user.setPhone(DEFAULT_USER_PHONE);
        user.setPassword(DEFAULT_USER_PASSWORD);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    public static User createUser(Integer id) {
        User user = createUser();
        user.setId(id);
        return user;
    }

    public static User createUser(Integer id, String username, String email) {
        User user = createUser();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }

    public static User createUser(Integer id, String username, String email, String phone) {
        User user = createUser();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        return user;
    }

    public static Pet createPet(User user) {
        Pet pet = new Pet();
        pet.setId(1);
        pet.setUser(user);
        pet.setName(DEFAULT_PET_NAME);
        pet.setType(DEFAULT_PET_TYPE);
        pet.setBreed(DEFAULT_PET_BREED);
        pet.setAge(DEFAULT_PET_AGE);
        pet.setGender("male");
        pet.setCreatedAt(LocalDateTime.now());
        pet.setUpdatedAt(LocalDateTime.now());
        return pet;
    }

    public static Pet createPet(Integer id, User user) {
        Pet pet = createPet(user);
        pet.setId(id);
        return pet;
    }

    public static Pet createPet(Integer id, User user, String name, String type) {
        Pet pet = createPet(user);
        pet.setId(id);
        pet.setName(name);
        pet.setType(type);
        return pet;
    }

    public static Pet createPet(Integer id, User user, String name, String type, String breed, Integer age) {
        Pet pet = createPet(user);
        pet.setId(id);
        pet.setName(name);
        pet.setType(type);
        pet.setBreed(breed);
        pet.setAge(age);
        return pet;
    }

    public static Appointment createAppointment(Merchant merchant) {
        User user = createUser();
        Service service = createService(merchant);
        Pet pet = createPet(user);

        Appointment appointment = new Appointment();
        appointment.setId(1);
        appointment.setUser(user);
        appointment.setMerchant(merchant);
        appointment.setService(service);
        appointment.setPet(pet);
        appointment.setAppointmentTime(LocalDateTime.now().plusDays(1));
        appointment.setStatus("pending");
        appointment.setTotalPrice(DEFAULT_SERVICE_PRICE);
        appointment.setNotes("测试预约备注");
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        return appointment;
    }

    public static Appointment createAppointment(Integer id, Merchant merchant) {
        Appointment appointment = createAppointment(merchant);
        appointment.setId(id);
        return appointment;
    }

    public static Appointment createAppointment(Integer id, Merchant merchant, String status) {
        Appointment appointment = createAppointment(merchant);
        appointment.setId(id);
        appointment.setStatus(status);
        return appointment;
    }

    public static Appointment createAppointment(Integer id, Merchant merchant, User user, Service service, 
                                                Pet pet, LocalDateTime appointmentTime, String status) {
        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setUser(user);
        appointment.setMerchant(merchant);
        appointment.setService(service);
        appointment.setPet(pet);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setStatus(status);
        appointment.setTotalPrice(service.getPrice());
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        return appointment;
    }

    public static Appointment createAppointment(Integer id, Merchant merchant, User user, Service service, 
                                                Pet pet, LocalDateTime appointmentTime, String status, 
                                                BigDecimal totalPrice, String notes) {
        Appointment appointment = createAppointment(id, merchant, user, service, pet, appointmentTime, status);
        appointment.setTotalPrice(totalPrice);
        appointment.setNotes(notes);
        return appointment;
    }

    public static ProductOrder createProductOrder(Merchant merchant) {
        User user = createUser();

        ProductOrder order = new ProductOrder();
        order.setId(1);
        order.setUserId(user.getId());
        order.setMerchantId(merchant.getId());
        order.setTotalPrice(DEFAULT_PRODUCT_PRICE);
        order.setStatus("pending");
        order.setShippingAddress("测试收货地址");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    public static ProductOrder createProductOrder(Integer id, Merchant merchant) {
        ProductOrder order = createProductOrder(merchant);
        order.setId(id);
        return order;
    }

    public static ProductOrder createProductOrder(Integer id, Merchant merchant, String status) {
        ProductOrder order = createProductOrder(merchant);
        order.setId(id);
        order.setStatus(status);
        return order;
    }

    public static ProductOrder createProductOrder(Integer id, Merchant merchant, User user, 
                                                  BigDecimal totalPrice, String status, String shippingAddress) {
        ProductOrder order = new ProductOrder();
        order.setId(id);
        order.setUserId(user.getId());
        order.setMerchantId(merchant.getId());
        order.setTotalPrice(totalPrice);
        order.setStatus(status);
        order.setShippingAddress(shippingAddress);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    public static ProductOrder createProductOrder(Integer id, Merchant merchant, User user, BigDecimal totalPrice, 
                                                  String status, String shippingAddress, String logisticsCompany, 
                                                  String logisticsNumber) {
        ProductOrder order = createProductOrder(id, merchant, user, totalPrice, status, shippingAddress);
        order.setLogisticsCompany(logisticsCompany);
        order.setLogisticsNumber(logisticsNumber);
        return order;
    }

    public static Review createReview(Merchant merchant) {
        User user = createUser();
        Service service = createService(merchant);
        Appointment appointment = createAppointment(merchant);

        Review review = new Review();
        review.setId(1);
        review.setUser(user);
        review.setMerchant(merchant);
        review.setService(service);
        review.setAppointment(appointment);
        review.setRating(DEFAULT_RATING);
        review.setComment(DEFAULT_COMMENT);
        review.setCreatedAt(LocalDateTime.now());
        return review;
    }

    public static Review createReview(Integer id, Merchant merchant) {
        Review review = createReview(merchant);
        review.setId(id);
        return review;
    }

    public static Review createReview(Integer id, Merchant merchant, Integer rating, String comment) {
        Review review = createReview(merchant);
        review.setId(id);
        review.setRating(rating);
        review.setComment(comment);
        return review;
    }

    public static Review createReview(Integer id, Merchant merchant, User user, Service service, 
                                      Appointment appointment, Integer rating, String comment) {
        Review review = new Review();
        review.setId(id);
        review.setUser(user);
        review.setMerchant(merchant);
        review.setService(service);
        review.setAppointment(appointment);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        return review;
    }

    public static Review createReview(Integer id, Merchant merchant, User user, Service service, 
                                      Appointment appointment, Integer rating, String comment, 
                                      String replyContent, LocalDateTime replyTime) {
        Review review = createReview(id, merchant, user, service, appointment, rating, comment);
        review.setReplyContent(replyContent);
        review.setReplyTime(replyTime);
        return review;
    }

    public static MerchantSettings createMerchantSettings(Merchant merchant) {
        MerchantSettings settings = new MerchantSettings();
        settings.setId(1);
        // settings.setMerchant(merchant); // 移除这个方法调用
        settings.setIsOpen(true);
        settings.setAppointmentNotification(true);
        settings.setOrderNotification(true);
        settings.setReviewNotification(true);
        settings.setNotificationType("email");
        settings.setCreatedAt(LocalDateTime.now());
        settings.setUpdatedAt(LocalDateTime.now());
        return settings;
    }

    public static MerchantSettings createMerchantSettings(Integer id, Merchant merchant) {
        MerchantSettings settings = createMerchantSettings(merchant);
        settings.setId(id);
        return settings;
    }

    public static MerchantSettings createMerchantSettings(Integer id, Merchant merchant, Boolean isOpen) {
        MerchantSettings settings = createMerchantSettings(merchant);
        settings.setId(id);
        settings.setIsOpen(isOpen);
        return settings;
    }

    public static MerchantSettings createMerchantSettings(Integer id, Merchant merchant, Boolean isOpen, 
                                                          Boolean appointmentNotification, 
                                                          Boolean orderNotification, 
                                                          Boolean reviewNotification, 
                                                          String notificationType) {
        MerchantSettings settings = new MerchantSettings();
        settings.setId(id);
        settings.setMerchant(merchant);
        settings.setIsOpen(isOpen);
        settings.setAppointmentNotification(appointmentNotification);
        settings.setOrderNotification(orderNotification);
        settings.setReviewNotification(reviewNotification);
        settings.setNotificationType(notificationType);
        settings.setCreatedAt(LocalDateTime.now());
        settings.setUpdatedAt(LocalDateTime.now());
        return settings;
    }
}
