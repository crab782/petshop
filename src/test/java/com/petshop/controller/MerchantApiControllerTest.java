package com.petshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.dto.ApiResponse;
import com.petshop.dto.LoginRequest;
import com.petshop.dto.MerchantRegisterRequest;
import com.petshop.entity.*;
import com.petshop.mapper.*;
import com.petshop.security.JwtUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("商家端 API 接口测试")
class MerchantApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private Merchant testMerchant;
    private Service testService;
    private Product testProduct;
    private ProductOrder testOrder;
    private Review testReview;
    private User testUser;
    private String merchantToken;

    @BeforeEach
    void setUp() throws Exception {
        testMerchant = createTestMerchant();
        testService = createTestService();
        testProduct = createTestProduct();
        testUser = createTestUser();
        testOrder = createTestOrder();
        testReview = createTestReview();
        merchantToken = jwtUtils.generateTokenFromUsername("13900000001");
    }

    private Merchant createTestMerchant() {
        Merchant merchant = new Merchant();
        merchant.setName("测试商家");
        merchant.setContactPerson("张三");
        merchant.setPhone("13900000001");
        merchant.setEmail("merchant@test.com");
        merchant.setPassword(passwordEncoder.encode("password123"));
        merchant.setAddress("测试地址123号");
        merchant.setStatus("approved");
        merchant.setCreatedAt(LocalDateTime.now());
        merchant.setUpdatedAt(LocalDateTime.now());
        merchantMapper.insert(merchant);
        return merchant;
    }

    private Service createTestService() {
        Service service = new Service();
        service.setMerchantId(testMerchant.getId());
        service.setName("宠物美容");
        service.setDescription("专业宠物美容服务");
        service.setPrice(new BigDecimal("99.99"));
        service.setDuration(60);
        service.setCategory("grooming");
        service.setStatus("enabled");
        service.setCreatedAt(LocalDateTime.now());
        service.setUpdatedAt(LocalDateTime.now());
        serviceMapper.insert(service);
        return service;
    }

    private Product createTestProduct() {
        Product product = new Product();
        product.setMerchantId(testMerchant.getId());
        product.setName("宠物粮食");
        product.setDescription("优质宠物粮食");
        product.setPrice(new BigDecimal("59.99"));
        product.setStock(100);
        product.setStatus("enabled");
        product.setCategory("food");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.insert(product);
        return product;
    }

    private User createTestUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("user@test.com");
        user.setPhone("13800000001");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setStatus("active");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }

    private ProductOrder createTestOrder() {
        ProductOrder order = new ProductOrder();
        order.setOrderNo("ORD" + System.currentTimeMillis());
        order.setUserId(testUser.getId());
        order.setMerchantId(testMerchant.getId());
        order.setTotalPrice(new BigDecimal("119.98"));
        order.setFreight(BigDecimal.ZERO);
        order.setStatus("pending");
        order.setShippingAddress("测试收货地址");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        productOrderMapper.insert(order);
        return order;
    }

    private Review createTestReview() {
        Review review = new Review();
        review.setUserId(testUser.getId());
        review.setMerchantId(testMerchant.getId());
        review.setServiceId(testService.getId());
        review.setRating(5);
        review.setComment("非常满意的服务！");
        review.setStatus("approved");
        review.setCreatedAt(LocalDateTime.now());
        reviewMapper.insert(review);
        return review;
    }

    private String loginAsMerchant() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .phone("13900000001")
                .password("password123")
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/merchant/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ApiResponse response = objectMapper.readValue(responseBody, ApiResponse.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.getData();
        return (String) data.get("token");
    }

    @Nested
    @DisplayName("1. 商家认证接口测试")
    @Order(1)
    class MerchantAuthTests {

        @Test
        @DisplayName("1.1 商家注册 - 成功")
        void testMerchantRegister_Success() throws Exception {
            MerchantRegisterRequest request = MerchantRegisterRequest.builder()
                    .name("新商家")
                    .phone("13900000002")
                    .password("password123")
                    .email("newmerchant@test.com")
                    .contact_person("李四")
                    .address("新地址456号")
                    .build();

            mockMvc.perform(post("/api/auth/merchant/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Merchant registration successful"))
                    .andExpect(jsonPath("$.data").exists());
        }

        @Test
        @DisplayName("1.2 商家注册 - 密码过短")
        void testMerchantRegister_PasswordTooShort() throws Exception {
            MerchantRegisterRequest request = MerchantRegisterRequest.builder()
                    .name("测试商家")
                    .phone("13900000003")
                    .password("12345")
                    .email("shortpass@test.com")
                    .contact_person("赵六")
                    .address("地址000号")
                    .build();

            mockMvc.perform(post("/api/auth/merchant/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value(containsString("Password must be at least 6 characters")));
        }

        @Test
        @DisplayName("1.3 商家注册 - 缺少必填字段")
        void testMerchantRegister_MissingRequiredFields() throws Exception {
            MerchantRegisterRequest request = new MerchantRegisterRequest();
            request.setName("测试商家");

            mockMvc.perform(post("/api/auth/merchant/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        }

        @Test
        @DisplayName("1.4 商家登录 - 成功")
        void testMerchantLogin_Success() throws Exception {
            LoginRequest loginRequest = LoginRequest.builder()
                    .phone("13900000001")
                    .password("password123")
                    .build();

            mockMvc.perform(post("/api/auth/merchant/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Merchant login successful"))
                    .andExpect(jsonPath("$.data.token").exists())
                    .andExpect(jsonPath("$.data.user.role").value("merchant"));
        }

        @Test
        @DisplayName("1.5 商家登录 - 密码错误")
        void testMerchantLogin_WrongPassword() throws Exception {
            LoginRequest loginRequest = LoginRequest.builder()
                    .phone("13900000001")
                    .password("wrongpassword")
                    .build();

            mockMvc.perform(post("/api/auth/merchant/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("1.6 商家登录 - 商家不存在")
        void testMerchantLogin_MerchantNotFound() throws Exception {
            LoginRequest loginRequest = LoginRequest.builder()
                    .phone("19999999999")
                    .password("password123")
                    .build();

            mockMvc.perform(post("/api/auth/merchant/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("1.7 商家登录 - 商家未审核")
        void testMerchantLogin_MerchantNotApproved() throws Exception {
            Merchant pendingMerchant = new Merchant();
            pendingMerchant.setName("待审核商家");
            pendingMerchant.setPhone("13900000099");
            pendingMerchant.setEmail("pending@test.com");
            pendingMerchant.setPassword(passwordEncoder.encode("password123"));
            pendingMerchant.setStatus("pending");
            pendingMerchant.setContactPerson("测试");
            pendingMerchant.setAddress("测试地址");
            pendingMerchant.setCreatedAt(LocalDateTime.now());
            pendingMerchant.setUpdatedAt(LocalDateTime.now());
            merchantMapper.insert(pendingMerchant);

            LoginRequest loginRequest = LoginRequest.builder()
                    .phone("13900000099")
                    .password("password123")
                    .build();

            mockMvc.perform(post("/api/auth/merchant/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }

    @Nested
    @DisplayName("2. 服务管理接口测试")
    @Order(2)
    class ServiceManagementTests {

        @Test
        @DisplayName("2.1 获取服务列表 - 成功")
        void testGetServices_Success() throws Exception {
            mockMvc.perform(get("/api/merchant/services")
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].name").value("宠物美容"));
        }

        @Test
        @DisplayName("2.2 创建服务 - 成功")
        void testCreateService_Success() throws Exception {
            Service service = new Service();
            service.setName("宠物洗澡");
            service.setDescription("专业宠物洗澡服务");
            service.setPrice(new BigDecimal("49.99"));
            service.setDuration(30);
            service.setCategory("bath");

            mockMvc.perform(post("/api/merchant/services")
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(service)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("服务添加成功"))
                    .andExpect(jsonPath("$.data.name").value("宠物洗澡"));
        }

        @Test
        @DisplayName("2.3 创建服务 - 名称不能为空")
        void testCreateService_EmptyName() throws Exception {
            Service service = new Service();
            service.setName("");
            service.setPrice(new BigDecimal("49.99"));
            service.setDuration(30);

            mockMvc.perform(post("/api/merchant/services")
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(service)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value(containsString("服务名称不能为空")));
        }

        @Test
        @DisplayName("2.4 创建服务 - 价格无效")
        void testCreateService_InvalidPrice() throws Exception {
            Service service = new Service();
            service.setName("测试服务");
            service.setPrice(new BigDecimal("-10.00"));
            service.setDuration(30);

            mockMvc.perform(post("/api/merchant/services")
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(service)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value(containsString("服务价格无效")));
        }

        @Test
        @DisplayName("2.5 创建服务 - 时长无效")
        void testCreateService_InvalidDuration() throws Exception {
            Service service = new Service();
            service.setName("测试服务");
            service.setPrice(new BigDecimal("99.99"));
            service.setDuration(0);

            mockMvc.perform(post("/api/merchant/services")
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(service)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value(containsString("服务时长无效")));
        }

        @Test
        @DisplayName("2.6 更新服务 - 成功")
        void testUpdateService_Success() throws Exception {
            Service service = new Service();
            service.setName("高级宠物美容");
            service.setDescription("高级宠物美容服务");
            service.setPrice(new BigDecimal("199.99"));
            service.setDuration(90);
            service.setCategory("grooming");

            mockMvc.perform(put("/api/merchant/services/" + testService.getId())
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(service)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("服务更新成功"))
                    .andExpect(jsonPath("$.data.name").value("高级宠物美容"));
        }

        @Test
        @DisplayName("2.7 更新服务 - 服务不存在")
        void testUpdateService_NotFound() throws Exception {
            Service service = new Service();
            service.setName("测试服务");
            service.setPrice(new BigDecimal("99.99"));
            service.setDuration(60);

            mockMvc.perform(put("/api/merchant/services/99999")
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(service)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("2.8 删除服务 - 成功")
        void testDeleteService_Success() throws Exception {
            Service service = new Service();
            service.setMerchantId(testMerchant.getId());
            service.setName("待删除服务");
            service.setPrice(new BigDecimal("99.99"));
            service.setDuration(60);
            service.setCreatedAt(LocalDateTime.now());
            service.setUpdatedAt(LocalDateTime.now());
            serviceMapper.insert(service);

            mockMvc.perform(delete("/api/merchant/services/" + service.getId())
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("服务删除成功"));
        }

        @Test
        @DisplayName("2.9 删除服务 - 服务不存在")
        void testDeleteService_NotFound() throws Exception {
            mockMvc.perform(delete("/api/merchant/services/99999")
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("2.10 获取服务详情 - 成功")
        void testGetServiceById_Success() throws Exception {
            mockMvc.perform(get("/api/merchant/services/" + testService.getId())
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.id").value(testService.getId()))
                    .andExpect(jsonPath("$.data.name").value("宠物美容"));
        }
    }

    @Nested
    @DisplayName("3. 商品管理接口测试")
    @Order(3)
    class ProductManagementTests {

        @Test
        @DisplayName("3.1 获取商品列表 - 成功")
        void testGetProducts_Success() throws Exception {
            mockMvc.perform(get("/api/merchant/products")
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].name").value("宠物粮食"));
        }

        @Test
        @DisplayName("3.2 创建商品 - 成功")
        void testCreateProduct_Success() throws Exception {
            Product product = new Product();
            product.setName("宠物玩具");
            product.setDescription("优质宠物玩具");
            product.setPrice(new BigDecimal("29.99"));
            product.setStock(50);
            product.setCategory("toy");

            mockMvc.perform(post("/api/merchant/products")
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(product)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("商品添加成功"))
                    .andExpect(jsonPath("$.data.name").value("宠物玩具"));
        }

        @Test
        @DisplayName("3.3 获取商品详情 - 成功")
        void testGetProductById_Success() throws Exception {
            mockMvc.perform(get("/api/merchant/products/" + testProduct.getId())
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.id").value(testProduct.getId()))
                    .andExpect(jsonPath("$.data.name").value("宠物粮食"));
        }

        @Test
        @DisplayName("3.4 获取商品详情 - 商品不存在")
        void testGetProductById_NotFound() throws Exception {
            mockMvc.perform(get("/api/merchant/products/99999")
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("3.5 更新商品 - 成功")
        void testUpdateProduct_Success() throws Exception {
            Product product = new Product();
            product.setName("高级宠物粮食");
            product.setDescription("高级优质宠物粮食");
            product.setPrice(new BigDecimal("89.99"));
            product.setStock(200);
            product.setCategory("food");

            mockMvc.perform(put("/api/merchant/products/" + testProduct.getId())
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(product)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("商品更新成功"))
                    .andExpect(jsonPath("$.data.name").value("高级宠物粮食"));
        }

        @Test
        @DisplayName("3.6 更新商品 - 商品不存在")
        void testUpdateProduct_NotFound() throws Exception {
            Product product = new Product();
            product.setName("测试商品");
            product.setPrice(new BigDecimal("99.99"));
            product.setStock(100);

            mockMvc.perform(put("/api/merchant/products/99999")
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(product)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("3.7 删除商品 - 成功")
        void testDeleteProduct_Success() throws Exception {
            Product product = new Product();
            product.setMerchantId(testMerchant.getId());
            product.setName("待删除商品");
            product.setPrice(new BigDecimal("99.99"));
            product.setStock(10);
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());
            productMapper.insert(product);

            mockMvc.perform(delete("/api/merchant/products/" + product.getId())
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("商品删除成功"));
        }

        @Test
        @DisplayName("3.8 删除商品 - 商品不存在")
        void testDeleteProduct_NotFound() throws Exception {
            mockMvc.perform(delete("/api/merchant/products/99999")
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("3.9 更新商品状态 - 成功")
        void testUpdateProductStatus_Success() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "disabled");

            mockMvc.perform(put("/api/merchant/products/" + testProduct.getId() + "/status")
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("商品状态更新成功"));
        }

        @Test
        @DisplayName("3.10 分页获取商品 - 成功")
        void testGetProductsPaged_Success() throws Exception {
            mockMvc.perform(get("/api/merchant/products/paged")
                            .header("Authorization", "Bearer " + merchantToken)
                            .param("page", "0")
                            .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.list").isArray())
                    .andExpect(jsonPath("$.data.total").exists());
        }
    }

    @Nested
    @DisplayName("4. 订单处理接口测试")
    @Order(4)
    class OrderManagementTests {

        @Test
        @DisplayName("4.1 获取订单列表 - 成功")
        void testGetOrders_Success() throws Exception {
            mockMvc.perform(get("/api/merchant/orders")
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray());
        }

        @Test
        @DisplayName("4.2 更新订单状态 - 成功")
        void testUpdateOrderStatus_Success() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "paid");

            mockMvc.perform(put("/api/merchant/orders/" + testOrder.getId() + "/status")
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("订单状态更新成功"));
        }

        @Test
        @DisplayName("4.3 更新订单状态 - 订单不存在")
        void testUpdateOrderStatus_NotFound() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "paid");

            mockMvc.perform(put("/api/merchant/orders/99999/status")
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("4.4 更新订单状态 - 无效状态")
        void testUpdateOrderStatus_InvalidStatus() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "invalid_status");

            mockMvc.perform(put("/api/merchant/orders/" + testOrder.getId() + "/status")
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        }

        @Test
        @DisplayName("4.5 更新订单状态 - 状态为空")
        void testUpdateOrderStatus_EmptyStatus() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "");

            mockMvc.perform(put("/api/merchant/orders/" + testOrder.getId() + "/status")
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        }

        @Test
        @DisplayName("4.6 获取商品订单列表 - 成功")
        void testGetProductOrders_Success() throws Exception {
            mockMvc.perform(get("/api/merchant/product-orders")
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray());
        }

        @Test
        @DisplayName("4.7 更新物流信息 - 成功")
        void testUpdateLogisticsInfo_Success() throws Exception {
            ProductOrder order = new ProductOrder();
            order.setOrderNo("ORD" + System.currentTimeMillis());
            order.setUserId(testUser.getId());
            order.setMerchantId(testMerchant.getId());
            order.setTotalPrice(new BigDecimal("99.99"));
            order.setStatus("paid");
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            productOrderMapper.insert(order);

            Map<String, String> request = new HashMap<>();
            request.put("logisticsCompany", "顺丰快递");
            request.put("trackingNumber", "SF1234567890");

            mockMvc.perform(put("/api/merchant/product-orders/" + order.getId() + "/logistics")
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("物流信息更新成功"));
        }
    }

    @Nested
    @DisplayName("5. 评价管理接口测试")
    @Order(5)
    class ReviewManagementTests {

        @Test
        @DisplayName("5.1 获取评价列表 - 成功")
        void testGetReviews_Success() throws Exception {
            mockMvc.perform(get("/api/merchant/reviews")
                            .header("Authorization", "Bearer " + merchantToken)
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("5.2 获取评价统计 - 成功")
        void testGetReviewsStatistics_Success() throws Exception {
            mockMvc.perform(get("/api/merchant/reviews/statistics")
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").exists());
        }

        @Test
        @DisplayName("5.3 获取评价详情 - 成功")
        void testGetReviewById_Success() throws Exception {
            mockMvc.perform(get("/api/merchant/reviews/" + testReview.getId())
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.id").value(testReview.getId()));
        }

        @Test
        @DisplayName("5.4 获取评价详情 - 评价不存在")
        void testGetReviewById_NotFound() throws Exception {
            mockMvc.perform(get("/api/merchant/reviews/99999")
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("5.5 回复评价 - 成功")
        void testReplyToReview_Success() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("reply", "感谢您的好评，我们会继续努力！");

            mockMvc.perform(put("/api/merchant/reviews/" + testReview.getId() + "/reply")
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("回复成功"));
        }

        @Test
        @DisplayName("5.6 回复评价 - 评价不存在")
        void testReplyToReview_NotFound() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("reply", "测试回复");

            mockMvc.perform(put("/api/merchant/reviews/99999/reply")
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("5.7 回复评价 - 回复内容为空")
        void testReplyToReview_EmptyReply() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("reply", "");

            mockMvc.perform(put("/api/merchant/reviews/" + testReview.getId() + "/reply")
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value(containsString("回复内容不能为空")));
        }

        @Test
        @DisplayName("5.8 删除评价 - 成功")
        void testDeleteReview_Success() throws Exception {
            Review review = new Review();
            review.setUserId(testUser.getId());
            review.setMerchantId(testMerchant.getId());
            review.setRating(4);
            review.setComment("测试评价");
            review.setStatus("approved");
            review.setCreatedAt(LocalDateTime.now());
            reviewMapper.insert(review);

            mockMvc.perform(delete("/api/merchant/reviews/" + review.getId())
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("删除评价成功"));
        }

        @Test
        @DisplayName("5.9 删除评价 - 评价不存在")
        void testDeleteReview_NotFound() throws Exception {
            mockMvc.perform(delete("/api/merchant/reviews/99999")
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("5.10 获取最近评价 - 成功")
        void testGetRecentReviews_Success() throws Exception {
            mockMvc.perform(get("/api/merchant/reviews/recent")
                            .header("Authorization", "Bearer " + merchantToken)
                            .param("limit", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("6. 商家资料接口测试")
    @Order(6)
    class MerchantProfileTests {

        @Test
        @DisplayName("6.1 获取商家资料 - 成功")
        void testGetProfile_Success() throws Exception {
            mockMvc.perform(get("/api/merchant/profile")
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.name").value("测试商家"));
        }

        @Test
        @DisplayName("6.2 获取商家信息 - 成功")
        void testGetInfo_Success() throws Exception {
            mockMvc.perform(get("/api/merchant/info")
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.name").value("测试商家"));
        }

        @Test
        @DisplayName("6.3 更新商家资料 - 成功")
        void testUpdateProfile_Success() throws Exception {
            Merchant merchant = new Merchant();
            merchant.setName("更新后的商家名");
            merchant.setContactPerson("李四");
            merchant.setPhone("13900000001");
            merchant.setEmail("updated@test.com");
            merchant.setAddress("更新后的地址");

            mockMvc.perform(put("/api/merchant/profile")
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(merchant)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("商家资料更新成功"))
                    .andExpect(jsonPath("$.data.name").value("更新后的商家名"));
        }

        @Test
        @DisplayName("6.4 获取首页统计数据 - 成功")
        void testGetDashboardStats_Success() throws Exception {
            mockMvc.perform(get("/api/merchant/dashboard")
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").exists());
        }

        @Test
        @DisplayName("6.5 获取预约列表 - 成功")
        void testGetAppointments_Success() throws Exception {
            mockMvc.perform(get("/api/merchant/appointments")
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray());
        }

        @Test
        @DisplayName("6.6 获取店铺设置 - 成功")
        void testGetSettings_Success() throws Exception {
            mockMvc.perform(get("/api/merchant/settings")
                            .header("Authorization", "Bearer " + merchantToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    @Nested
    @DisplayName("7. 权限验证测试")
    @Order(7)
    class AuthorizationTests {

        @Test
        @DisplayName("7.1 无Token访问接口 - 应返回401")
        void testAccessWithoutToken() throws Exception {
            mockMvc.perform(get("/api/merchant/services"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("7.2 无效Token访问接口 - 应返回401")
        void testAccessWithInvalidToken() throws Exception {
            mockMvc.perform(get("/api/merchant/services")
                            .header("Authorization", "Bearer invalid_token"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("7.3 访问其他商家的资源 - 应返回403")
        void testAccessOtherMerchantResource() throws Exception {
            Merchant otherMerchant = new Merchant();
            otherMerchant.setName("其他商家");
            otherMerchant.setPhone("13900000088");
            otherMerchant.setEmail("other@test.com");
            otherMerchant.setPassword(passwordEncoder.encode("password123"));
            otherMerchant.setStatus("approved");
            otherMerchant.setContactPerson("测试");
            otherMerchant.setAddress("测试地址");
            otherMerchant.setCreatedAt(LocalDateTime.now());
            otherMerchant.setUpdatedAt(LocalDateTime.now());
            merchantMapper.insert(otherMerchant);

            Service otherService = new Service();
            otherService.setMerchantId(otherMerchant.getId());
            otherService.setName("其他商家的服务");
            otherService.setPrice(new BigDecimal("99.99"));
            otherService.setDuration(60);
            otherService.setCreatedAt(LocalDateTime.now());
            otherService.setUpdatedAt(LocalDateTime.now());
            serviceMapper.insert(otherService);

            Service service = new Service();
            service.setName("测试服务");
            service.setPrice(new BigDecimal("99.99"));
            service.setDuration(60);

            mockMvc.perform(put("/api/merchant/services/" + otherService.getId())
                            .header("Authorization", "Bearer " + merchantToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(service)))
                    .andExpect(status().isForbidden());
        }
    }
}
