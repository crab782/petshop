package com.petshop.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.config.TestSecurityConfig;
import com.petshop.dto.ApiResponse;
import com.petshop.entity.*;
import com.petshop.mapper.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@Transactional
@DisplayName("管理员API集成测试")
public class AdminApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private ServiceMapper serviceMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Autowired
    private SystemSettingsMapper systemSettingsMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ScheduledTaskMapper scheduledTaskMapper;

    @Autowired
    private PetMapper petMapper;

    private User testUser;
    private Merchant testMerchant;
    private Service testService;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = createAndSaveUser();
        testMerchant = createAndSaveMerchant();
        testService = createAndSaveService(testMerchant);
        testProduct = createAndSaveProduct(testMerchant);
    }

    private User createAndSaveUser() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        User user = new User();
        user.setUsername("testuser_" + uniqueId);
        user.setEmail("test_" + uniqueId + "@user.com");
        user.setPhone("13900139000");
        user.setPassword("password123");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }

    private Merchant createAndSaveMerchant() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        Merchant merchant = new Merchant();
        merchant.setName("测试商家_" + uniqueId);
        merchant.setEmail("merchant_" + uniqueId + "@test.com");
        merchant.setPhone("13800138000");
        merchant.setPassword("password123");
        merchant.setAddress("测试地址");
        merchant.setContactPerson("联系人");
        merchant.setStatus("approved");
        merchant.setCreatedAt(LocalDateTime.now());
        merchant.setUpdatedAt(LocalDateTime.now());
        merchantMapper.insert(merchant);
        return merchant;
    }

    private Merchant createAndSaveMerchant(String status) {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        Merchant merchant = new Merchant();
        merchant.setName("商家_" + status + "_" + uniqueId);
        merchant.setEmail("merchant_" + status + "_" + uniqueId + "@test.com");
        merchant.setPhone("13800138000");
        merchant.setPassword("password123");
        merchant.setAddress("测试地址");
        merchant.setContactPerson("联系人");
        merchant.setStatus(status);
        merchant.setCreatedAt(LocalDateTime.now());
        merchant.setUpdatedAt(LocalDateTime.now());
        merchantMapper.insert(merchant);
        return merchant;
    }

    private Service createAndSaveService(Merchant merchant) {
        Service service = new Service();
        service.setMerchantId(merchant.getId());
        service.setName("测试服务_" + System.currentTimeMillis());
        service.setDescription("测试服务描述");
        service.setPrice(new BigDecimal("99.99"));
        service.setDuration(60);
        service.setStatus("enabled");
        service.setCreatedAt(LocalDateTime.now());
        service.setUpdatedAt(LocalDateTime.now());
        serviceMapper.insert(service);
        return service;
    }

    private Product createAndSaveProduct(Merchant merchant) {
        Product product = new Product();
        product.setMerchantId(merchant.getId());
        product.setName("测试商品_" + System.currentTimeMillis());
        product.setDescription("测试商品描述");
        product.setPrice(new BigDecimal("199.99"));
        product.setStock(100);
        product.setStatus("enabled");
        product.setLowStockThreshold(10);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.insert(product);
        return product;
    }

    private Review createAndSaveReview(Merchant merchant, User user, int rating) {
        Pet pet = new Pet();
        pet.setUserId(user.getId());
        pet.setName("测试宠物");
        pet.setType("dog");
        pet.setAge(2);
        pet.setGender("male");
        pet.setCreatedAt(LocalDateTime.now());
        pet.setUpdatedAt(LocalDateTime.now());
        petMapper.insert(pet);

        Appointment appointment = new Appointment();
        appointment.setUserId(user.getId());
        appointment.setMerchantId(merchant.getId());
        appointment.setServiceId(testService.getId());
        appointment.setPetId(pet.getId());
        appointment.setAppointmentTime(LocalDateTime.now().plusDays(1));
        appointment.setStatus("completed");
        appointment.setTotalPrice(new BigDecimal("99.99"));
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentMapper.insert(appointment);

        Review review = new Review();
        review.setMerchantId(merchant.getId());
        review.setUserId(user.getId());
        review.setServiceId(testService.getId());
        review.setAppointmentId(appointment.getId());
        review.setRating(rating);
        review.setComment("测试评价内容");
        review.setStatus("approved");
        review.setCreatedAt(LocalDateTime.now());
        reviewMapper.insert(review);
        return review;
    }

    private Announcement createAndSaveAnnouncement() {
        Announcement announcement = new Announcement();
        announcement.setTitle("测试公告_" + System.currentTimeMillis());
        announcement.setContent("测试公告内容");
        announcement.setStatus("published");
        announcement.setCreatedAt(LocalDateTime.now());
        announcementMapper.insert(announcement);
        return announcement;
    }

    private Activity createAndSaveActivity() {
        Activity activity = new Activity();
        activity.setName("测试活动_" + System.currentTimeMillis());
        activity.setType("promotion");
        activity.setDescription("测试活动描述");
        activity.setStartTime(LocalDateTime.now());
        activity.setEndTime(LocalDateTime.now().plusDays(7));
        activity.setStatus("enabled");
        activityMapper.insert(activity);
        return activity;
    }

    private ScheduledTask createAndSaveScheduledTask() {
        ScheduledTask task = new ScheduledTask();
        task.setName("测试任务_" + System.currentTimeMillis());
        task.setType("cleanup");
        task.setCronExpression("0 0 0 * * ?");
        task.setDescription("测试任务描述");
        task.setStatus("enabled");
        scheduledTaskMapper.insert(task);
        return task;
    }

    @Nested
    @DisplayName("登录验证测试")
    class AuthenticationTests {

        @Test
        @DisplayName("未登录访问用户列表返回401")
        void testGetUsers_Unauthorized_Returns401() throws Exception {
            mockMvc.perform(get("/api/admin/users")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("未登录访问商家列表返回401")
        void testGetMerchants_Unauthorized_Returns401() throws Exception {
            mockMvc.perform(get("/api/admin/merchants")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("登录后访问用户列表返回200")
        void testGetUsers_Authorized_Returns200() throws Exception {
            mockMvc.perform(get("/api/admin/users")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("登录后访问商家列表返回200")
        void testGetMerchants_Authorized_Returns200() throws Exception {
            mockMvc.perform(get("/api/admin/merchants")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("未登录访问Dashboard返回401")
        void testGetDashboard_Unauthorized_Returns401() throws Exception {
            mockMvc.perform(get("/api/admin/dashboard")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("登录后访问Dashboard返回200")
        void testGetDashboard_Authorized_Returns200() throws Exception {
            mockMvc.perform(get("/api/admin/dashboard")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.userCount").exists())
                    .andExpect(jsonPath("$.data.merchantCount").exists())
                    .andExpect(jsonPath("$.data.orderCount").exists())
                    .andExpect(jsonPath("$.data.serviceCount").exists());
        }
    }

    @Nested
    @DisplayName("用户CRUD操作测试")
    class UserCrudTests {

        @Test
        @DisplayName("获取用户列表成功")
        void testGetUsers_Success() throws Exception {
            mockMvc.perform(get("/api/admin/users")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        @DisplayName("删除用户成功")
        void testDeleteUser_Success() throws Exception {
            User newUser = createAndSaveUser();
            Integer userId = newUser.getId();

            mockMvc.perform(delete("/api/admin/users/{id}", userId)
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            Assertions.assertNull(userMapper.selectById(userId));
        }

        @Test
        @DisplayName("删除不存在的用户返回正常响应")
        void testDeleteUser_NonExistent_ReturnsNoContent() throws Exception {
            mockMvc.perform(delete("/api/admin/users/{id}", 99999)
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("商家CRUD操作测试")
    class MerchantCrudTests {

        @Test
        @DisplayName("获取商家列表成功")
        void testGetMerchants_Success() throws Exception {
            mockMvc.perform(get("/api/admin/merchants")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        @DisplayName("获取商家详情成功")
        void testGetMerchantDetail_Success() throws Exception {
            mockMvc.perform(get("/api/admin/merchants/{id}", testMerchant.getId())
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.id").value(testMerchant.getId()))
                    .andExpect(jsonPath("$.data.name").value(testMerchant.getName()));
        }

        @Test
        @DisplayName("获取不存在的商家详情返回404")
        void testGetMerchantDetail_NotFound_Returns404() throws Exception {
            mockMvc.perform(get("/api/admin/merchants/{id}", 99999)
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("更新商家状态成功")
        void testUpdateMerchantStatus_Success() throws Exception {
            mockMvc.perform(put("/api/admin/merchants/{id}/status", testMerchant.getId())
                    .sessionAttr("admin", "admin")
                    .param("status", "disabled")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testMerchant.getId()))
                    .andExpect(jsonPath("$.status").value("disabled"));

            Merchant updated = merchantMapper.selectById(testMerchant.getId());
            Assertions.assertNotNull(updated);
            Assertions.assertEquals("disabled", updated.getStatus());
        }

        @Test
        @DisplayName("更新不存在的商家状态返回404")
        void testUpdateMerchantStatus_NotFound_Returns404() throws Exception {
            mockMvc.perform(put("/api/admin/merchants/{id}/status", 99999)
                    .sessionAttr("admin", "admin")
                    .param("status", "disabled")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("删除商家成功")
        void testDeleteMerchant_Success() throws Exception {
            Merchant newMerchant = createAndSaveMerchant();
            Integer merchantId = newMerchant.getId();

            mockMvc.perform(delete("/api/admin/merchants/{id}", merchantId)
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            Assertions.assertNull(merchantMapper.selectById(merchantId));
        }
    }

    @Nested
    @DisplayName("批量操作测试")
    class BatchOperationTests {

        @Test
        @DisplayName("批量更新商家状态成功")
        void testBatchUpdateMerchantStatus_Success() throws Exception {
            Merchant merchant1 = createAndSaveMerchant("approved");
            Merchant merchant2 = createAndSaveMerchant("approved");

            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(merchant1.getId(), merchant2.getId()));
            request.put("status", "disabled");

            mockMvc.perform(put("/api/admin/merchants/batch/status")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.updatedCount").value(2));

            Merchant updated1 = merchantMapper.selectById(merchant1.getId());
            Merchant updated2 = merchantMapper.selectById(merchant2.getId());
            Assertions.assertNotNull(updated1);
            Assertions.assertNotNull(updated2);
            Assertions.assertEquals("disabled", updated1.getStatus());
            Assertions.assertEquals("disabled", updated2.getStatus());
        }

        @Test
        @DisplayName("批量更新商家状态 - 空ID列表返回400")
        void testBatchUpdateMerchantStatus_EmptyIds_Returns400() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Collections.emptyList());
            request.put("status", "disabled");

            mockMvc.perform(put("/api/admin/merchants/batch/status")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        }

        @Test
        @DisplayName("批量删除商家成功")
        void testBatchDeleteMerchants_Success() throws Exception {
            Merchant merchant1 = createAndSaveMerchant("approved");
            Merchant merchant2 = createAndSaveMerchant("approved");

            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(merchant1.getId(), merchant2.getId()));

            mockMvc.perform(delete("/api/admin/merchants/batch")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.deletedCount").value(2));

            Assertions.assertNull(merchantMapper.selectById(merchant1.getId()));
            Assertions.assertNull(merchantMapper.selectById(merchant2.getId()));
        }

        @Test
        @DisplayName("批量更新商品状态成功")
        void testBatchUpdateProductStatus_Success() throws Exception {
            Product product1 = createAndSaveProduct(testMerchant);
            Product product2 = createAndSaveProduct(testMerchant);

            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(product1.getId(), product2.getId()));
            request.put("status", "disabled");

            mockMvc.perform(put("/api/admin/products/batch/status")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.updatedCount").value(2));
        }

        @Test
        @DisplayName("批量删除商品成功")
        void testBatchDeleteProducts_Success() throws Exception {
            Product product1 = createAndSaveProduct(testMerchant);
            Product product2 = createAndSaveProduct(testMerchant);

            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(product1.getId(), product2.getId()));

            mockMvc.perform(delete("/api/admin/products/batch")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.deletedCount").value(2));
        }
    }

    @Nested
    @DisplayName("分页测试")
    class PaginationTests {

        @Test
        @DisplayName("获取待审核商家列表 - 分页参数验证")
        void testGetPendingMerchants_Pagination_Success() throws Exception {
            for (int i = 0; i < 15; i++) {
                createAndSaveMerchant("pending");
            }

            mockMvc.perform(get("/api/admin/merchants/pending")
                    .sessionAttr("admin", "admin")
                    .param("page", "0")
                    .param("pageSize", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.data").isArray())
                    .andExpect(jsonPath("$.data.data.length()").value(10))
                    .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(15)))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.pageSize").value(10));
        }

        @Test
        @DisplayName("获取商品列表 - 分页参数验证")
        void testGetProducts_Pagination_Success() throws Exception {
            for (int i = 0; i < 15; i++) {
                createAndSaveProduct(testMerchant);
            }

            mockMvc.perform(get("/api/admin/products")
                    .sessionAttr("admin", "admin")
                    .param("page", "0")
                    .param("pageSize", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.data").isArray())
                    .andExpect(jsonPath("$.data.data.length()").value(lessThanOrEqualTo(10)))
                    .andExpect(jsonPath("$.data.total").value(greaterThanOrEqualTo(15)))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.pageSize").value(10));
        }

        @Test
        @DisplayName("获取评价列表 - 分页参数验证")
        void testGetReviews_Pagination_Success() throws Exception {
            for (int i = 0; i < 15; i++) {
                createAndSaveReview(testMerchant, testUser, 5);
            }

            mockMvc.perform(get("/api/admin/reviews")
                    .sessionAttr("admin", "admin")
                    .param("page", "0")
                    .param("pageSize", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.data").isArray())
                    .andExpect(jsonPath("$.data.data.length()").value(lessThanOrEqualTo(10)))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.pageSize").value(10));
        }

        @Test
        @DisplayName("获取最近用户 - 分页参数验证")
        void testGetRecentUsers_Pagination_Success() throws Exception {
            for (int i = 0; i < 15; i++) {
                createAndSaveUser();
            }

            mockMvc.perform(get("/api/admin/dashboard/recent-users")
                    .sessionAttr("admin", "admin")
                    .param("page", "0")
                    .param("pageSize", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.data").isArray())
                    .andExpect(jsonPath("$.data.data.length()").value(lessThanOrEqualTo(10)));
        }
    }

    @Nested
    @DisplayName("商品管理测试")
    class ProductManagementTests {

        @Test
        @DisplayName("获取商品列表成功")
        void testGetProducts_Success() throws Exception {
            mockMvc.perform(get("/api/admin/products")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.data").isArray());
        }

        @Test
        @DisplayName("获取商品详情成功")
        void testGetProductDetail_Success() throws Exception {
            mockMvc.perform(get("/api/admin/products/{id}", testProduct.getId())
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.id").value(testProduct.getId()));
        }

        @Test
        @DisplayName("获取不存在的商品详情返回404")
        void testGetProductDetail_NotFound_Returns404() throws Exception {
            mockMvc.perform(get("/api/admin/products/{id}", 99999)
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("更新商品成功")
        void testUpdateProduct_Success() throws Exception {
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("name", "更新后的商品名称");
            updateData.put("price", 299.99);
            updateData.put("stock", 50);

            mockMvc.perform(put("/api/admin/products/{id}", testProduct.getId())
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateData)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.name").value("更新后的商品名称"));
        }

        @Test
        @DisplayName("更新商品状态成功")
        void testUpdateProductStatus_Success() throws Exception {
            mockMvc.perform(put("/api/admin/products/{id}/status", testProduct.getId())
                    .sessionAttr("admin", "admin")
                    .param("status", "disabled")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.status").value("disabled"));
        }

        @Test
        @DisplayName("删除商品成功")
        void testDeleteProduct_Success() throws Exception {
            Product newProduct = createAndSaveProduct(testMerchant);
            Integer productId = newProduct.getId();

            mockMvc.perform(delete("/api/admin/products/{id}", productId)
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            Assertions.assertNull(productMapper.selectById(productId));
        }

        @Test
        @DisplayName("按关键字搜索商品")
        void testSearchProducts_ByKeyword_Success() throws Exception {
            Product searchProduct = createAndSaveProduct(testMerchant);
            searchProduct.setName("特殊搜索关键字商品");
            productMapper.updateById(searchProduct);

            mockMvc.perform(get("/api/admin/products")
                    .sessionAttr("admin", "admin")
                    .param("keyword", "特殊搜索关键字")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.data").isArray());
        }
    }

    @Nested
    @DisplayName("评价管理测试")
    class ReviewManagementTests {

        @Test
        @DisplayName("获取评价列表成功")
        void testGetReviews_Success() throws Exception {
            createAndSaveReview(testMerchant, testUser, 5);

            mockMvc.perform(get("/api/admin/reviews")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.data").isArray());
        }

        @Test
        @DisplayName("审核评价成功")
        void testAuditReview_Success() throws Exception {
            Review review = createAndSaveReview(testMerchant, testUser, 5);
            review.setStatus("pending");
            reviewMapper.updateById(review);

            Map<String, String> request = new HashMap<>();
            request.put("status", "approved");

            mockMvc.perform(put("/api/admin/reviews/{id}/audit", review.getId())
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.status").value("approved"));
        }

        @Test
        @DisplayName("删除评价成功")
        void testDeleteReview_Success() throws Exception {
            Review review = createAndSaveReview(testMerchant, testUser, 5);
            Integer reviewId = review.getId();

            mockMvc.perform(delete("/api/admin/reviews/{id}", reviewId)
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            Assertions.assertNull(reviewMapper.selectById(reviewId));
        }

        @Test
        @DisplayName("批量更新评价状态成功")
        void testBatchUpdateReviewStatus_Success() throws Exception {
            Review review1 = createAndSaveReview(testMerchant, testUser, 5);
            Review review2 = createAndSaveReview(testMerchant, testUser, 4);
            review1.setStatus("pending");
            review2.setStatus("pending");
            reviewMapper.updateById(review1);
            reviewMapper.updateById(review2);

            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(review1.getId(), review2.getId()));
            request.put("status", "approved");

            mockMvc.perform(put("/api/admin/reviews/batch/status")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.updatedCount").value(2));
        }
    }

    @Nested
    @DisplayName("商家审核测试")
    class MerchantAuditTests {

        @Test
        @DisplayName("获取待审核商家列表成功")
        void testGetPendingMerchants_Success() throws Exception {
            createAndSaveMerchant("pending");

            mockMvc.perform(get("/api/admin/merchants/pending")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.data").isArray());
        }

        @Test
        @DisplayName("审核商家通过成功")
        void testAuditMerchant_Approve_Success() throws Exception {
            Merchant pendingMerchant = createAndSaveMerchant("pending");

            Map<String, String> request = new HashMap<>();
            request.put("status", "approved");

            mockMvc.perform(put("/api/admin/merchants/{id}/audit", pendingMerchant.getId())
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.status").value("approved"));

            Merchant updated = merchantMapper.selectById(pendingMerchant.getId());
            Assertions.assertNotNull(updated);
            Assertions.assertEquals("approved", updated.getStatus());
        }

        @Test
        @DisplayName("审核商家拒绝成功")
        void testAuditMerchant_Reject_Success() throws Exception {
            Merchant pendingMerchant = createAndSaveMerchant("pending");

            Map<String, String> request = new HashMap<>();
            request.put("status", "rejected");
            request.put("reason", "资质不符合要求");

            mockMvc.perform(put("/api/admin/merchants/{id}/audit", pendingMerchant.getId())
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.status").value("rejected"));
        }

        @Test
        @DisplayName("审核商家 - 无效状态返回400")
        void testAuditMerchant_InvalidStatus_Returns400() throws Exception {
            Merchant pendingMerchant = createAndSaveMerchant("pending");

            Map<String, String> request = new HashMap<>();
            request.put("status", "invalid_status");

            mockMvc.perform(put("/api/admin/merchants/{id}/audit", pendingMerchant.getId())
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        }
    }

    @Nested
    @DisplayName("系统配置测试")
    class SystemConfigTests {

        @Test
        @DisplayName("获取系统配置成功")
        void testGetSystemConfig_Success() throws Exception {
            mockMvc.perform(get("/api/admin/system/config")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.siteName").exists());
        }

        @Test
        @DisplayName("更新系统配置成功")
        void testUpdateSystemConfig_Success() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("siteName", "新网站名称");
            request.put("contactEmail", "new@test.com");
            request.put("contactPhone", "400-123-4567");

            mockMvc.perform(put("/api/admin/system/config")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.siteName").value("新网站名称"));
        }

        @Test
        @DisplayName("获取系统设置成功")
        void testGetSystemSettings_Success() throws Exception {
            mockMvc.perform(get("/api/admin/system/settings")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.emailSettings").exists())
                    .andExpect(jsonPath("$.data.smsSettings").exists())
                    .andExpect(jsonPath("$.data.uploadSettings").exists());
        }

        @Test
        @DisplayName("更新系统设置成功")
        void testUpdateSystemSettings_Success() throws Exception {
            Map<String, Object> emailSettings = new HashMap<>();
            emailSettings.put("smtp", "smtp.test.com");
            emailSettings.put("port", 587);

            Map<String, Object> uploadSettings = new HashMap<>();
            uploadSettings.put("maxFileSize", 20971520);
            uploadSettings.put("allowedFileTypes", "jpg,png,pdf");

            Map<String, Object> request = new HashMap<>();
            request.put("emailSettings", emailSettings);
            request.put("uploadSettings", uploadSettings);

            mockMvc.perform(put("/api/admin/system/settings")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    @Nested
    @DisplayName("活动管理测试")
    class ActivityTests {

        @Test
        @DisplayName("获取活动列表成功")
        void testGetActivities_Success() throws Exception {
            createAndSaveActivity();

            mockMvc.perform(get("/api/admin/activities")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.data").isArray());
        }

        @Test
        @DisplayName("创建活动成功")
        void testCreateActivity_Success() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("name", "新活动");
            request.put("type", "promotion");
            request.put("description", "活动描述");
            request.put("startTime", LocalDateTime.now().toString());
            request.put("endTime", LocalDateTime.now().plusDays(7).toString());

            mockMvc.perform(post("/api/admin/activities")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.name").value("新活动"));
        }

        @Test
        @DisplayName("创建活动 - 名称为空返回400")
        void testCreateActivity_EmptyName_Returns400() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("type", "promotion");

            mockMvc.perform(post("/api/admin/activities")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        }

        @Test
        @DisplayName("更新活动成功")
        void testUpdateActivity_Success() throws Exception {
            Activity activity = createAndSaveActivity();

            Map<String, Object> request = new HashMap<>();
            request.put("name", "更新后的活动");
            request.put("type", "discount");

            mockMvc.perform(put("/api/admin/activities/{id}", activity.getId())
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.name").value("更新后的活动"));
        }

        @Test
        @DisplayName("更新活动状态成功")
        void testUpdateActivityStatus_Success() throws Exception {
            Activity activity = createAndSaveActivity();

            mockMvc.perform(put("/api/admin/activities/{id}/status", activity.getId())
                    .sessionAttr("admin", "admin")
                    .param("status", "disabled")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.status").value("disabled"));
        }

        @Test
        @DisplayName("删除活动成功")
        void testDeleteActivity_Success() throws Exception {
            Activity activity = createAndSaveActivity();
            Integer activityId = activity.getId();

            mockMvc.perform(delete("/api/admin/activities/{id}", activityId)
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            Assertions.assertNull(activityMapper.selectById(activityId));
        }
    }

    @Nested
    @DisplayName("定时任务管理测试")
    class ScheduledTaskTests {

        @Test
        @DisplayName("获取任务列表成功")
        void testGetTasks_Success() throws Exception {
            createAndSaveScheduledTask();

            mockMvc.perform(get("/api/admin/tasks")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.data").isArray());
        }

        @Test
        @DisplayName("创建任务成功")
        void testCreateTask_Success() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("name", "新任务");
            request.put("type", "backup");
            request.put("cronExpression", "0 0 2 * * ?");
            request.put("description", "每日备份任务");

            mockMvc.perform(post("/api/admin/tasks")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.name").value("新任务"));
        }

        @Test
        @DisplayName("创建任务 - 名称为空返回400")
        void testCreateTask_EmptyName_Returns400() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("type", "backup");

            mockMvc.perform(post("/api/admin/tasks")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        }

        @Test
        @DisplayName("创建任务 - 类型为空返回400")
        void testCreateTask_EmptyType_Returns400() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("name", "新任务");

            mockMvc.perform(post("/api/admin/tasks")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        }

        @Test
        @DisplayName("更新任务成功")
        void testUpdateTask_Success() throws Exception {
            ScheduledTask task = createAndSaveScheduledTask();

            Map<String, Object> request = new HashMap<>();
            request.put("name", "更新后的任务");
            request.put("cronExpression", "0 30 3 * * ?");

            mockMvc.perform(put("/api/admin/tasks/{id}", task.getId())
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.name").value("更新后的任务"));
        }

        @Test
        @DisplayName("执行任务成功")
        void testExecuteTask_Success() throws Exception {
            ScheduledTask task = createAndSaveScheduledTask();

            mockMvc.perform(post("/api/admin/tasks/{id}/execute", task.getId())
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.status").value("success"));
        }

        @Test
        @DisplayName("删除任务成功")
        void testDeleteTask_Success() throws Exception {
            ScheduledTask task = createAndSaveScheduledTask();
            Integer taskId = task.getId();

            mockMvc.perform(delete("/api/admin/tasks/{id}", taskId)
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            Assertions.assertNull(scheduledTaskMapper.selectById(taskId));
        }
    }

    @Nested
    @DisplayName("Dashboard数据测试")
    class DashboardTests {

        @Test
        @DisplayName("获取Dashboard统计数据成功")
        void testGetDashboardStats_Success() throws Exception {
            mockMvc.perform(get("/api/admin/dashboard")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.userCount").isNumber())
                    .andExpect(jsonPath("$.data.merchantCount").isNumber())
                    .andExpect(jsonPath("$.data.orderCount").isNumber())
                    .andExpect(jsonPath("$.data.serviceCount").isNumber());
        }

        @Test
        @DisplayName("获取待审核商家Dashboard数据成功")
        void testGetPendingMerchantsDashboard_Success() throws Exception {
            createAndSaveMerchant("pending");

            mockMvc.perform(get("/api/admin/dashboard/pending-merchants")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.data").isArray());
        }

        @Test
        @DisplayName("获取公告Dashboard数据成功")
        void testGetAnnouncementsDashboard_Success() throws Exception {
            createAndSaveAnnouncement();

            mockMvc.perform(get("/api/admin/dashboard/announcements")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.data").isArray());
        }
    }

    @Nested
    @DisplayName("店铺审核测试")
    class ShopAuditTests {

        @Test
        @DisplayName("获取待审核店铺列表成功")
        void testGetPendingShops_Success() throws Exception {
            createAndSaveMerchant("pending");

            mockMvc.perform(get("/api/admin/shops/pending")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.data").isArray());
        }

        @Test
        @DisplayName("审核店铺通过成功")
        void testAuditShop_Approve_Success() throws Exception {
            Merchant pendingMerchant = createAndSaveMerchant("pending");

            Map<String, String> request = new HashMap<>();
            request.put("status", "approved");

            mockMvc.perform(put("/api/admin/shops/{id}/audit", pendingMerchant.getId())
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.status").value("approved"));
        }
    }

    @Nested
    @DisplayName("数据持久化验证测试")
    class DataPersistenceTests {

        @Test
        @DisplayName("创建商家后数据持久化验证")
        void testMerchantPersistence() throws Exception {
            String uniqueName = "持久化测试商家_" + System.currentTimeMillis();
            String uniqueEmail = "persist_" + System.currentTimeMillis() + "@test.com";

            Merchant merchant = new Merchant();
            merchant.setName(uniqueName);
            merchant.setEmail(uniqueEmail);
            merchant.setPhone("13800138000");
            merchant.setPassword("password123");
            merchant.setAddress("测试地址");
            merchant.setContactPerson("联系人");
            merchant.setStatus("approved");
            merchantMapper.insert(merchant);

            Merchant found = merchantMapper.selectById(merchant.getId());
            Assertions.assertNotNull(found);
            Assertions.assertEquals(uniqueName, found.getName());
            Assertions.assertEquals(uniqueEmail, found.getEmail());
        }

        @Test
        @DisplayName("更新商品后数据持久化验证")
        void testProductPersistence() throws Exception {
            String newName = "更新后的商品_" + System.currentTimeMillis();

            Map<String, Object> updateData = new HashMap<>();
            updateData.put("name", newName);
            updateData.put("price", 399.99);

            mockMvc.perform(put("/api/admin/products/{id}", testProduct.getId())
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateData)))
                    .andDo(print())
                    .andExpect(status().isOk());

            Product updated = productMapper.selectById(testProduct.getId());
            Assertions.assertNotNull(updated);
            Assertions.assertEquals(newName, updated.getName());
            Assertions.assertEquals(new BigDecimal("399.99"), updated.getPrice());
        }
    }

    @Nested
    @DisplayName("事务回滚测试")
    @Transactional
    class TransactionRollbackTests {

        @Test
        @DisplayName("批量操作失败时事务回滚验证")
        void testBatchOperationRollback() throws Exception {
            Long initialCount = merchantMapper.selectCount(null);

            Map<String, Object> request = new HashMap<>();
            request.put("ids", Collections.emptyList());
            request.put("status", "disabled");

            mockMvc.perform(put("/api/admin/merchants/batch/status")
                    .sessionAttr("admin", "admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            Long finalCount = merchantMapper.selectCount(null);
            Assertions.assertEquals(initialCount, finalCount);
        }
    }
}
