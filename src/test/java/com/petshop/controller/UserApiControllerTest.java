package com.petshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.dto.*;
import com.petshop.entity.*;
import com.petshop.mapper.*;
import com.petshop.security.JwtUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductOrderMapper productOrderMapper;

    @Autowired
    private ProductOrderItemMapper productOrderItemMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private User testUser;
    private Merchant testMerchant;
    private Service testService;
    private Pet testPet;
    private Product testProduct;
    private Address testAddress;
    private String testUserToken;

    private static final String TEST_USER_PHONE = "13900000001";
    private static final String TEST_USER_PASSWORD = "password123";
    private static final String TEST_MERCHANT_PHONE = "13900000002";

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        testMerchant = createTestMerchant();
        testService = createTestService(testMerchant);
        testPet = createTestPet(testUser);
        testProduct = createTestProduct(testMerchant);
        testAddress = createTestAddress(testUser);
        testUserToken = generateTestToken(testUser);
    }

    private User createTestUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword(passwordEncoder.encode(TEST_USER_PASSWORD));
        user.setPhone(TEST_USER_PHONE);
        user.setStatus("active");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }

    private Merchant createTestMerchant() {
        Merchant merchant = new Merchant();
        merchant.setName("Test Merchant");
        merchant.setContactPerson("Test Contact");
        merchant.setEmail("merchant@example.com");
        merchant.setPassword(passwordEncoder.encode("merchant123"));
        merchant.setPhone(TEST_MERCHANT_PHONE);
        merchant.setAddress("Test Address 123");
        merchant.setStatus("approved");
        merchant.setCreatedAt(LocalDateTime.now());
        merchant.setUpdatedAt(LocalDateTime.now());
        merchantMapper.insert(merchant);
        return merchant;
    }

    private Service createTestService(Merchant merchant) {
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
        return service;
    }

    private Pet createTestPet(User user) {
        Pet pet = new Pet();
        pet.setUserId(user.getId());
        pet.setName("Test Pet");
        pet.setType("dog");
        pet.setBreed("Golden Retriever");
        pet.setAge(2);
        pet.setGender("male");
        pet.setCreatedAt(LocalDateTime.now());
        pet.setUpdatedAt(LocalDateTime.now());
        petMapper.insert(pet);
        pet.setUser(user);
        return pet;
    }

    private Product createTestProduct(Merchant merchant) {
        Product product = new Product();
        product.setMerchantId(merchant.getId());
        product.setName("Test Product");
        product.setDescription("Test product description");
        product.setPrice(new BigDecimal("49.99"));
        product.setStock(100);
        product.setStatus("enabled");
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.insert(product);
        return product;
    }

    private Address createTestAddress(User user) {
        Address address = new Address();
        address.setUserId(user.getId());
        address.setContactName("Test Receiver");
        address.setPhone("13900000003");
        address.setProvince("Beijing");
        address.setCity("Beijing");
        address.setDistrict("Chaoyang");
        address.setDetailAddress("Test Street 123");
        address.setIsDefault(true);
        address.setCreatedAt(LocalDateTime.now());
        address.setUpdatedAt(LocalDateTime.now());
        addressMapper.insert(address);
        address.setUser(user);
        return address;
    }

    private String generateTestToken(User user) {
        return jwtUtils.generateTokenFromUsername(user.getPhone());
    }

    private Appointment createTestAppointment(User user, Service service, Pet pet) {
        Appointment appointment = new Appointment();
        appointment.setUserId(user.getId());
        appointment.setMerchantId(service.getMerchantId());
        appointment.setServiceId(service.getId());
        appointment.setPetId(pet.getId());
        appointment.setAppointmentTime(LocalDateTime.now().plusDays(1));
        appointment.setStatus("pending");
        appointment.setTotalPrice(service.getPrice());
        appointment.setNotes("Test appointment");
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentMapper.insert(appointment);
        return appointment;
    }

    private ProductOrder createTestOrder(User user, Product product) {
        ProductOrder order = new ProductOrder();
        order.setOrderNo("ORD" + System.currentTimeMillis());
        order.setUserId(user.getId());
        order.setMerchantId(product.getMerchantId());
        order.setTotalPrice(product.getPrice());
        order.setFreight(BigDecimal.ZERO);
        order.setStatus("pending");
        order.setShippingAddress("Test Address");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        productOrderMapper.insert(order);

        ProductOrderItem item = new ProductOrderItem();
        item.setOrderId(order.getId());
        item.setProductId(product.getId());
        item.setQuantity(1);
        item.setPrice(product.getPrice());
        item.setProduct(product);
        productOrderItemMapper.insert(item);

        return order;
    }

    @Nested
    @DisplayName("用户认证接口测试")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class AuthApiTests {

        @Test
        @Order(1)
        @DisplayName("测试用户注册接口 - 成功注册")
        void testRegister_Success() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password("newpassword123")
                    .email("newuser@example.com")
                    .phone("13900000099")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.token").exists())
                    .andExpect(jsonPath("$.data.user.phone").value("13900000099"));
        }

        @Test
        @Order(2)
        @DisplayName("测试用户注册接口 - 手机号为空")
        void testRegister_PhoneRequired() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password("newpassword123")
                    .email("newuser@example.com")
                    .phone(null)
                    .build();

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Phone number is required"));
        }

        @Test
        @Order(3)
        @DisplayName("测试用户注册接口 - 密码太短")
        void testRegister_PasswordTooShort() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password("12345")
                    .email("newuser@example.com")
                    .phone("13900000098")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Password must be at least 6 characters"));
        }

        @Test
        @Order(4)
        @DisplayName("测试用户登录接口 - 成功登录")
        void testLogin_Success() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .phone(TEST_USER_PHONE)
                    .password(TEST_USER_PASSWORD)
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.token").exists())
                    .andExpect(jsonPath("$.data.user.phone").value(TEST_USER_PHONE));
        }

        @Test
        @Order(5)
        @DisplayName("测试用户登录接口 - 用户不存在")
        void testLogin_UserNotFound() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .phone("99999999999")
                    .password(TEST_USER_PASSWORD)
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @Order(6)
        @DisplayName("测试用户登录接口 - 密码错误")
        void testLogin_WrongPassword() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .phone(TEST_USER_PHONE)
                    .password("wrongpassword")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @Order(7)
        @DisplayName("测试用户登出接口 - 成功登出")
        void testLogout_Success() throws Exception {
            mockMvc.perform(post("/api/auth/logout")
                            .header("Authorization", "Bearer " + testUserToken)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.success").value(true));
        }
    }

    @Nested
    @DisplayName("用户信息接口测试")
    class UserInfoApiTests {

        @Test
        @DisplayName("测试获取当前用户信息 - 成功获取")
        void testGetCurrentUser_Success() throws Exception {
            mockMvc.perform(get("/api/auth/userinfo")
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.phone").value(TEST_USER_PHONE))
                    .andExpect(jsonPath("$.data.username").value("testuser"));
        }

        @Test
        @DisplayName("测试获取当前用户信息 - 未授权")
        void testGetCurrentUser_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/auth/userinfo"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("测试更新用户信息 - 成功更新")
        void testUpdateUserInfo_Success() throws Exception {
            UserDTO userDTO = UserDTO.builder()
                    .username("updateduser")
                    .email("updated@example.com")
                    .build();

            mockMvc.perform(put("/api/auth/userinfo")
                            .header("Authorization", "Bearer " + testUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDTO)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.username").value("updateduser"));
        }

        @Test
        @DisplayName("测试修改密码 - 成功修改")
        void testChangePassword_Success() throws Exception {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword(TEST_USER_PASSWORD)
                    .newPassword("newpassword123")
                    .build();

            mockMvc.perform(put("/api/auth/password")
                            .header("Authorization", "Bearer " + testUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.success").value(true));
        }

        @Test
        @DisplayName("测试修改密码 - 原密码错误")
        void testChangePassword_WrongOldPassword() throws Exception {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword("wrongpassword")
                    .newPassword("newpassword123")
                    .build();

            mockMvc.perform(put("/api/auth/password")
                            .header("Authorization", "Bearer " + testUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        }

        @Test
        @DisplayName("测试修改密码 - 新密码太短")
        void testChangePassword_NewPasswordTooShort() throws Exception {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword(TEST_USER_PASSWORD)
                    .newPassword("12345")
                    .build();

            mockMvc.perform(put("/api/auth/password")
                            .header("Authorization", "Bearer " + testUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        }
    }

    @Nested
    @DisplayName("宠物管理接口测试")
    class PetApiTests {

        @Test
        @DisplayName("测试获取宠物列表 - 成功获取")
        void testGetPets_Success() throws Exception {
            mockMvc.perform(get("/api/user/pets")
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                    .andExpect(jsonPath("$.data[0].name").value("Test Pet"));
        }

        @Test
        @DisplayName("测试创建宠物 - 成功创建")
        void testCreatePet_Success() throws Exception {
            Pet pet = new Pet();
            pet.setName("New Pet");
            pet.setType("cat");
            pet.setBreed("Persian");
            pet.setAge(1);
            pet.setGender("female");

            mockMvc.perform(post("/api/user/pets")
                            .header("Authorization", "Bearer " + testUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pet)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("New Pet"))
                    .andExpect(jsonPath("$.type").value("cat"));
        }

        @Test
        @DisplayName("测试获取单个宠物 - 成功获取")
        void testGetPetById_Success() throws Exception {
            mockMvc.perform(get("/api/user/pets/{id}", testPet.getId())
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Test Pet"));
        }

        @Test
        @DisplayName("测试获取单个宠物 - 不存在")
        void testGetPetById_NotFound() throws Exception {
            mockMvc.perform(get("/api/user/pets/{id}", 99999)
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("测试更新宠物 - 成功更新")
        void testUpdatePet_Success() throws Exception {
            Pet pet = new Pet();
            pet.setName("Updated Pet");
            pet.setType("dog");
            pet.setBreed("Labrador");
            pet.setAge(3);
            pet.setGender("male");

            mockMvc.perform(put("/api/user/pets/{id}", testPet.getId())
                            .header("Authorization", "Bearer " + testUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pet)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated Pet"));
        }

        @Test
        @DisplayName("测试删除宠物 - 成功删除")
        void testDeletePet_Success() throws Exception {
            mockMvc.perform(delete("/api/user/pets/{id}", testPet.getId())
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("测试删除宠物 - 不存在")
        void testDeletePet_NotFound() throws Exception {
            mockMvc.perform(delete("/api/user/pets/{id}", 99999)
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("预约管理接口测试")
    class AppointmentApiTests {

        @Test
        @DisplayName("测试获取预约列表 - 成功获取")
        void testGetAppointments_Success() throws Exception {
            createTestAppointment(testUser, testService, testPet);

            mockMvc.perform(get("/api/user/appointments")
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.data", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        @DisplayName("测试获取预约列表 - 按状态筛选")
        void testGetAppointments_FilterByStatus() throws Exception {
            createTestAppointment(testUser, testService, testPet);

            mockMvc.perform(get("/api/user/appointments")
                            .header("Authorization", "Bearer " + testUserToken)
                            .param("status", "pending"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("测试创建预约 - 成功创建")
        void testCreateAppointment_Success() throws Exception {
            CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                    .serviceId(testService.getId())
                    .petId(testPet.getId())
                    .appointmentTime(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .remark("Test appointment remark")
                    .build();

            mockMvc.perform(post("/api/user/appointments")
                            .header("Authorization", "Bearer " + testUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.id").exists());
        }

        @Test
        @DisplayName("测试创建预约 - 服务不存在")
        void testCreateAppointment_ServiceNotFound() throws Exception {
            CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                    .serviceId(99999)
                    .petId(testPet.getId())
                    .appointmentTime(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .remark("Test appointment remark")
                    .build();

            mockMvc.perform(post("/api/user/appointments")
                            .header("Authorization", "Bearer " + testUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Service not found"));
        }

        @Test
        @DisplayName("测试创建预约 - 宠物不存在")
        void testCreateAppointment_PetNotFound() throws Exception {
            CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                    .serviceId(testService.getId())
                    .petId(99999)
                    .appointmentTime(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .remark("Test appointment remark")
                    .build();

            mockMvc.perform(post("/api/user/appointments")
                            .header("Authorization", "Bearer " + testUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }

        @Test
        @DisplayName("测试取消预约 - 成功取消")
        void testCancelAppointment_Success() throws Exception {
            Appointment appointment = createTestAppointment(testUser, testService, testPet);

            mockMvc.perform(put("/api/user/appointments/{id}/cancel", appointment.getId())
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("测试取消预约 - 不存在")
        void testCancelAppointment_NotFound() throws Exception {
            mockMvc.perform(put("/api/user/appointments/{id}/cancel", 99999)
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("测试获取预约统计")
        void testGetAppointmentStats_Success() throws Exception {
            createTestAppointment(testUser, testService, testPet);

            mockMvc.perform(get("/api/user/appointments/stats")
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.total").exists())
                    .andExpect(jsonPath("$.pending").exists());
        }
    }

    @Nested
    @DisplayName("订单管理接口测试")
    class OrderApiTests {

        @Test
        @DisplayName("测试获取订单列表 - 成功获取")
        void testGetOrders_Success() throws Exception {
            createTestOrder(testUser, testProduct);

            mockMvc.perform(get("/api/user/orders")
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.data", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        @DisplayName("测试获取订单列表 - 按状态筛选")
        void testGetOrders_FilterByStatus() throws Exception {
            createTestOrder(testUser, testProduct);

            mockMvc.perform(get("/api/user/orders")
                            .header("Authorization", "Bearer " + testUserToken)
                            .param("status", "pending"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("测试创建订单 - 成功创建")
        void testCreateOrder_Success() throws Exception {
            CreateOrderRequest.OrderItemRequest item = new CreateOrderRequest.OrderItemRequest(
                    testProduct.getId(), 1);
            List<CreateOrderRequest.OrderItemRequest> items = new ArrayList<>();
            items.add(item);

            CreateOrderRequest request = new CreateOrderRequest();
            request.setItems(items);
            request.setAddressId(testAddress.getId());
            request.setPaymentMethod("wechat");
            request.setRemark("Test order remark");

            mockMvc.perform(post("/api/user/orders")
                            .header("Authorization", "Bearer " + testUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.orderId").exists())
                    .andExpect(jsonPath("$.orderNo").exists());
        }

        @Test
        @DisplayName("测试创建订单 - 地址不存在")
        void testCreateOrder_AddressNotFound() throws Exception {
            CreateOrderRequest.OrderItemRequest item = new CreateOrderRequest.OrderItemRequest(
                    testProduct.getId(), 1);
            List<CreateOrderRequest.OrderItemRequest> items = new ArrayList<>();
            items.add(item);

            CreateOrderRequest request = new CreateOrderRequest();
            request.setItems(items);
            request.setAddressId(99999);
            request.setPaymentMethod("wechat");

            mockMvc.perform(post("/api/user/orders")
                            .header("Authorization", "Bearer " + testUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("测试创建订单 - 商品列表为空")
        void testCreateOrder_EmptyItems() throws Exception {
            CreateOrderRequest request = new CreateOrderRequest();
            request.setItems(new ArrayList<>());
            request.setAddressId(testAddress.getId());
            request.setPaymentMethod("wechat");

            mockMvc.perform(post("/api/user/orders")
                            .header("Authorization", "Bearer " + testUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("测试取消订单 - 成功取消")
        void testCancelOrder_Success() throws Exception {
            ProductOrder order = createTestOrder(testUser, testProduct);

            mockMvc.perform(put("/api/user/orders/{id}/cancel", order.getId())
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("测试取消订单 - 不存在")
        void testCancelOrder_NotFound() throws Exception {
            mockMvc.perform(put("/api/user/orders/{id}/cancel", 99999)
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("测试获取订单详情 - 成功获取")
        void testGetOrderDetail_Success() throws Exception {
            ProductOrder order = createTestOrder(testUser, testProduct);

            mockMvc.perform(get("/api/user/orders/{id}", order.getId())
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(order.getId()))
                    .andExpect(jsonPath("$.orderNo").value(order.getOrderNo()));
        }

        @Test
        @DisplayName("测试获取订单详情 - 不存在")
        void testGetOrderDetail_NotFound() throws Exception {
            mockMvc.perform(get("/api/user/orders/{id}", 99999)
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("测试支付订单 - 成功支付")
        void testPayOrder_Success() throws Exception {
            ProductOrder order = createTestOrder(testUser, testProduct);

            PayOrderRequest request = new PayOrderRequest();
            request.setPayMethod("wechat");

            mockMvc.perform(post("/api/user/orders/{id}/pay", order.getId())
                            .header("Authorization", "Bearer " + testUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.payId").value(order.getId()));
        }

        @Test
        @DisplayName("测试确认收货 - 成功确认")
        void testConfirmReceive_Success() throws Exception {
            ProductOrder order = createTestOrder(testUser, testProduct);
            order.setStatus("shipped");
            productOrderMapper.updateById(order);

            mockMvc.perform(put("/api/user/orders/{id}/confirm", order.getId())
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("测试删除订单 - 成功删除")
        void testDeleteOrder_Success() throws Exception {
            ProductOrder order = createTestOrder(testUser, testProduct);
            order.setStatus("completed");
            productOrderMapper.updateById(order);

            mockMvc.perform(delete("/api/user/orders/{id}", order.getId())
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("用户个人资料接口测试")
    class ProfileApiTests {

        @Test
        @DisplayName("测试获取个人资料 - 成功获取")
        void testGetProfile_Success() throws Exception {
            mockMvc.perform(get("/api/user/profile")
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.phone").value(TEST_USER_PHONE))
                    .andExpect(jsonPath("$.username").value("testuser"));
        }

        @Test
        @DisplayName("测试更新个人资料 - 成功更新")
        void testUpdateProfile_Success() throws Exception {
            User user = new User();
            user.setUsername("updateduser");
            user.setEmail("updated@example.com");

            mockMvc.perform(put("/api/user/profile")
                            .header("Authorization", "Bearer " + testUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(user)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("updateduser"));
        }
    }

    @Nested
    @DisplayName("地址管理接口测试")
    class AddressApiTests {

        @Test
        @DisplayName("测试获取地址列表 - 成功获取")
        void testGetAddresses_Success() throws Exception {
            mockMvc.perform(get("/api/user/addresses")
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        @DisplayName("测试创建地址 - 成功创建")
        void testCreateAddress_Success() throws Exception {
            Address address = new Address();
            address.setContactName("New Receiver");
            address.setPhone("13900000004");
            address.setProvince("Shanghai");
            address.setCity("Shanghai");
            address.setDistrict("Pudong");
            address.setDetailAddress("New Street 456");

            mockMvc.perform(post("/api/user/addresses")
                            .header("Authorization", "Bearer " + testUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(address)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.contactName").value("New Receiver"));
        }

        @Test
        @DisplayName("测试更新地址 - 成功更新")
        void testUpdateAddress_Success() throws Exception {
            Address address = new Address();
            address.setContactName("Updated Receiver");
            address.setPhone("13900000005");
            address.setProvince("Guangdong");
            address.setCity("Shenzhen");
            address.setDistrict("Nanshan");
            address.setDetailAddress("Updated Street 789");

            mockMvc.perform(put("/api/user/addresses/{id}", testAddress.getId())
                            .header("Authorization", "Bearer " + testUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(address)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.contactName").value("Updated Receiver"));
        }

        @Test
        @DisplayName("测试删除地址 - 成功删除")
        void testDeleteAddress_Success() throws Exception {
            mockMvc.perform(delete("/api/user/addresses/{id}", testAddress.getId())
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("测试设置默认地址 - 成功设置")
        void testSetDefaultAddress_Success() throws Exception {
            Address newAddress = new Address();
            newAddress.setUserId(testUser.getId());
            newAddress.setContactName("Another Receiver");
            newAddress.setPhone("13900000006");
            newAddress.setProvince("Jiangsu");
            newAddress.setCity("Nanjing");
            newAddress.setDistrict("Xuanwu");
            newAddress.setDetailAddress("Another Street 111");
            newAddress.setIsDefault(false);
            newAddress.setCreatedAt(LocalDateTime.now());
            newAddress.setUpdatedAt(LocalDateTime.now());
            addressMapper.insert(newAddress);

            mockMvc.perform(put("/api/user/addresses/{id}/default", newAddress.getId())
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    @Nested
    @DisplayName("首页统计接口测试")
    class HomeStatsApiTests {

        @Test
        @DisplayName("测试获取首页统计 - 成功获取")
        void testGetHomeStats_Success() throws Exception {
            mockMvc.perform(get("/api/user/home/stats")
                            .header("Authorization", "Bearer " + testUserToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("测试获取最近活动 - 成功获取")
        void testGetRecentActivities_Success() throws Exception {
            mockMvc.perform(get("/api/user/home/activities")
                            .header("Authorization", "Bearer " + testUserToken)
                            .param("limit", "10"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }
}
