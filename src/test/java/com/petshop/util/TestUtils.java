package com.petshop.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.entity.*;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class TestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final String TEST_MERCHANT_NAME = "测试商家";
    public static final String TEST_MERCHANT_EMAIL = "test@merchant.com";
    public static final String TEST_MERCHANT_PHONE = "13800138000";
    public static final String TEST_MERCHANT_PASSWORD = "password123";
    public static final String TEST_MERCHANT_ADDRESS = "测试地址123号";
    public static final String TEST_MERCHANT_CONTACT_PERSON = "测试联系人";
    public static final String TEST_MERCHANT_LOGO = "http://example.com/logo.png";

    public static final String TEST_SERVICE_NAME = "测试服务";
    public static final String TEST_SERVICE_DESCRIPTION = "测试服务描述";
    public static final BigDecimal TEST_SERVICE_PRICE = new BigDecimal("99.99");
    public static final Integer TEST_SERVICE_DURATION = 60;
    public static final String TEST_SERVICE_IMAGE = "http://example.com/service.png";

    public static final String TEST_PRODUCT_NAME = "测试商品";
    public static final String TEST_PRODUCT_DESCRIPTION = "测试商品描述";
    public static final BigDecimal TEST_PRODUCT_PRICE = new BigDecimal("199.99");
    public static final Integer TEST_PRODUCT_STOCK = 100;
    public static final String TEST_PRODUCT_IMAGE = "http://example.com/product.png";

    public static final String TEST_CATEGORY_NAME = "测试分类";
    public static final String TEST_CATEGORY_ICON = "http://example.com/icon.png";
    public static final String TEST_CATEGORY_DESCRIPTION = "测试分类描述";

    public static final String TEST_USER_USERNAME = "测试用户";
    public static final String TEST_USER_EMAIL = "test@user.com";
    public static final String TEST_USER_PHONE = "13900139000";
    public static final String TEST_USER_PASSWORD = "user123";

    public static final String TEST_PET_NAME = "测试宠物";
    public static final String TEST_PET_TYPE = "狗";
    public static final String TEST_PET_BREED = "金毛";
    public static final Integer TEST_PET_AGE = 3;

    public static final Integer TEST_RATING = 5;
    public static final String TEST_COMMENT = "测试评价内容";

    private TestUtils() {
    }

    public static HttpSession createMockHttpSessionWithMerchant(Merchant merchant) {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("merchant")).thenReturn(merchant);
        when(session.getAttribute("merchantId")).thenReturn(merchant.getId());
        return session;
    }

    public static HttpSession createMockHttpSessionWithMerchantId(Integer merchantId) {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("merchantId")).thenReturn(merchantId);
        return session;
    }

    public static Merchant createMockMerchant() {
        Merchant merchant = new Merchant();
        merchant.setId(1);
        merchant.setName(TEST_MERCHANT_NAME);
        merchant.setEmail(TEST_MERCHANT_EMAIL);
        merchant.setPhone(TEST_MERCHANT_PHONE);
        merchant.setPassword(TEST_MERCHANT_PASSWORD);
        merchant.setAddress(TEST_MERCHANT_ADDRESS);
        merchant.setContactPerson(TEST_MERCHANT_CONTACT_PERSON);
        merchant.setLogo(TEST_MERCHANT_LOGO);
        merchant.setStatus("approved");
        merchant.setCreatedAt(LocalDateTime.now());
        merchant.setUpdatedAt(LocalDateTime.now());
        return merchant;
    }

    public static Merchant createMockMerchant(Integer id) {
        Merchant merchant = createMockMerchant();
        merchant.setId(id);
        return merchant;
    }

    public static Merchant createMockMerchant(Integer id, String name, String email) {
        Merchant merchant = createMockMerchant();
        merchant.setId(id);
        merchant.setName(name);
        merchant.setEmail(email);
        return merchant;
    }

    public static Service createMockService(Merchant merchant) {
        Service service = new Service();
        service.setId(1);
        service.setMerchant(merchant);
        service.setName(TEST_SERVICE_NAME);
        service.setDescription(TEST_SERVICE_DESCRIPTION);
        service.setPrice(TEST_SERVICE_PRICE);
        service.setDuration(TEST_SERVICE_DURATION);
        service.setImage(TEST_SERVICE_IMAGE);
        service.setStatus("enabled");
        service.setCreatedAt(LocalDateTime.now());
        service.setUpdatedAt(LocalDateTime.now());
        return service;
    }

    public static Service createMockService(Integer id, Merchant merchant) {
        Service service = createMockService(merchant);
        service.setId(id);
        return service;
    }

    public static Service createMockService(Integer id, Merchant merchant, String name, BigDecimal price) {
        Service service = createMockService(merchant);
        service.setId(id);
        service.setName(name);
        service.setPrice(price);
        return service;
    }

    public static Product createMockProduct(Merchant merchant) {
        Product product = new Product();
        product.setId(1);
        product.setMerchant(merchant);
        product.setName(TEST_PRODUCT_NAME);
        product.setDescription(TEST_PRODUCT_DESCRIPTION);
        product.setPrice(TEST_PRODUCT_PRICE);
        product.setStock(TEST_PRODUCT_STOCK);
        product.setImage(TEST_PRODUCT_IMAGE);
        product.setStatus("enabled");
        product.setLowStockThreshold(10);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }

    public static Product createMockProduct(Integer id, Merchant merchant) {
        Product product = createMockProduct(merchant);
        product.setId(id);
        return product;
    }

    public static Product createMockProduct(Integer id, Merchant merchant, String name, BigDecimal price, Integer stock) {
        Product product = createMockProduct(merchant);
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        return product;
    }

    public static Category createMockCategory(Integer merchantId) {
        Category category = new Category();
        category.setId(1);
        category.setMerchantId(merchantId);
        category.setName(TEST_CATEGORY_NAME);
        category.setIcon(TEST_CATEGORY_ICON);
        category.setDescription(TEST_CATEGORY_DESCRIPTION);
        category.setSort(0);
        category.setStatus("enabled");
        category.setProductCount(0);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return category;
    }

    public static Category createMockCategory(Integer id, Integer merchantId) {
        Category category = createMockCategory(merchantId);
        category.setId(id);
        return category;
    }

    public static Category createMockCategory(Integer id, Integer merchantId, String name) {
        Category category = createMockCategory(merchantId);
        category.setId(id);
        category.setName(name);
        return category;
    }

    public static User createMockUser() {
        User user = new User();
        user.setId(1);
        user.setUsername(TEST_USER_USERNAME);
        user.setEmail(TEST_USER_EMAIL);
        user.setPhone(TEST_USER_PHONE);
        user.setPassword(TEST_USER_PASSWORD);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    public static User createMockUser(Integer id) {
        User user = createMockUser();
        user.setId(id);
        return user;
    }

    public static User createMockUser(Integer id, String username, String email) {
        User user = createMockUser();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        return user;
    }

    public static Pet createMockPet(User user) {
        Pet pet = new Pet();
        pet.setId(1);
        pet.setUser(user);
        pet.setName(TEST_PET_NAME);
        pet.setType(TEST_PET_TYPE);
        pet.setBreed(TEST_PET_BREED);
        pet.setAge(TEST_PET_AGE);
        pet.setGender("male");
        pet.setCreatedAt(LocalDateTime.now());
        pet.setUpdatedAt(LocalDateTime.now());
        return pet;
    }

    public static Pet createMockPet(Integer id, User user) {
        Pet pet = createMockPet(user);
        pet.setId(id);
        return pet;
    }

    public static Appointment createMockAppointment(Merchant merchant) {
        User user = createMockUser();
        Service service = createMockService(merchant);
        Pet pet = createMockPet(user);

        Appointment appointment = new Appointment();
        appointment.setId(1);
        appointment.setUser(user);
        appointment.setMerchant(merchant);
        appointment.setService(service);
        appointment.setPet(pet);
        appointment.setAppointmentTime(LocalDateTime.now().plusDays(1));
        appointment.setStatus("pending");
        appointment.setTotalPrice(TEST_SERVICE_PRICE);
        appointment.setNotes("测试预约备注");
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        return appointment;
    }

    public static Appointment createMockAppointment(Integer id, Merchant merchant) {
        Appointment appointment = createMockAppointment(merchant);
        appointment.setId(id);
        return appointment;
    }

    public static Appointment createMockAppointment(Integer id, Merchant merchant, String status) {
        Appointment appointment = createMockAppointment(merchant);
        appointment.setId(id);
        appointment.setStatus(status);
        return appointment;
    }

    public static ProductOrder createMockProductOrder(Merchant merchant) {
        User user = createMockUser();

        ProductOrder order = new ProductOrder();
        order.setId(1);
        order.setUserId(user.getId());
        order.setMerchantId(merchant.getId());
        order.setTotalPrice(TEST_PRODUCT_PRICE);
        order.setStatus("pending");
        order.setShippingAddress("测试收货地址");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    public static ProductOrder createMockProductOrder(Integer id, Merchant merchant) {
        ProductOrder order = createMockProductOrder(merchant);
        order.setId(id);
        return order;
    }

    public static ProductOrder createMockProductOrder(Integer id, Merchant merchant, String status) {
        ProductOrder order = createMockProductOrder(merchant);
        order.setId(id);
        order.setStatus(status);
        return order;
    }

    public static Review createMockReview(Merchant merchant) {
        User user = createMockUser();
        Service service = createMockService(merchant);
        Appointment appointment = createMockAppointment(merchant);

        Review review = new Review();
        review.setId(1);
        review.setUser(user);
        review.setMerchant(merchant);
        review.setService(service);
        review.setAppointment(appointment);
        review.setRating(TEST_RATING);
        review.setComment(TEST_COMMENT);
        review.setCreatedAt(LocalDateTime.now());
        return review;
    }

    public static Review createMockReview(Integer id, Merchant merchant) {
        Review review = createMockReview(merchant);
        review.setId(id);
        return review;
    }

    public static Review createMockReview(Integer id, Merchant merchant, Integer rating, String comment) {
        Review review = createMockReview(merchant);
        review.setId(id);
        review.setRating(rating);
        review.setComment(comment);
        return review;
    }

    public static MerchantSettings createMockMerchantSettings(Merchant merchant) {
        MerchantSettings settings = new MerchantSettings();
        settings.setId(1);
        settings.setMerchantId(merchant.getId());
        settings.setIsOpen(true);
        settings.setAppointmentNotification(true);
        settings.setOrderNotification(true);
        settings.setReviewNotification(true);
        settings.setNotificationType("email");
        settings.setCreatedAt(LocalDateTime.now());
        settings.setUpdatedAt(LocalDateTime.now());
        return settings;
    }

    public static MerchantSettings createMockMerchantSettings(Integer id, Merchant merchant) {
        MerchantSettings settings = createMockMerchantSettings(merchant);
        settings.setId(id);
        return settings;
    }

    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }

    public static JsonNode parseJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("JSON解析失败", e);
        }
    }

    public static boolean jsonContains(String json, String key, String value) {
        JsonNode node = parseJson(json);
        JsonNode valueNode = node.get(key);
        return valueNode != null && valueNode.asText().equals(value);
    }

    public static boolean jsonContainsValue(String json, String value) {
        return json.contains(value);
    }
}
