package com.petshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.config.TestRedisConfig;
import com.petshop.dto.ApiResponse;
import com.petshop.dto.LoginRequest;
import com.petshop.entity.Admin;
import com.petshop.entity.Announcement;
import com.petshop.entity.Merchant;
import com.petshop.entity.User;
import com.petshop.mapper.AdminMapper;
import com.petshop.mapper.AnnouncementMapper;
import com.petshop.mapper.MerchantMapper;
import com.petshop.mapper.UserMapper;
import com.petshop.TestDataInitializer;
import com.petshop.security.JwtUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
@Transactional
class AdminApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private String adminToken;
    private Admin testAdmin;
    private User testUser;
    private Merchant testMerchant;
    private Merchant pendingMerchant;

    @BeforeEach
    void setUp() throws Exception {
        testAdmin = new Admin();
        testAdmin.setUsername("admin_test");
        testAdmin.setPassword(passwordEncoder.encode("admin123"));
        testAdmin.setCreatedAt(LocalDateTime.now());
        testAdmin.setUpdatedAt(LocalDateTime.now());
        adminMapper.insert(testAdmin);

        testUser = new User();
        testUser.setUsername("user_test");
        testUser.setEmail("user_test@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setPhone("13800138000");
        testUser.setStatus("active");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        userMapper.insert(testUser);

        testMerchant = new Merchant();
        testMerchant.setName("Approved Merchant");
        testMerchant.setContactPerson("Contact Person");
        testMerchant.setEmail("approved@example.com");
        testMerchant.setPassword(passwordEncoder.encode("password123"));
        testMerchant.setPhone("13900139000");
        testMerchant.setAddress("Test Address");
        testMerchant.setStatus("approved");
        testMerchant.setCreatedAt(LocalDateTime.now());
        testMerchant.setUpdatedAt(LocalDateTime.now());
        merchantMapper.insert(testMerchant);

        pendingMerchant = new Merchant();
        pendingMerchant.setName("Pending Merchant");
        pendingMerchant.setContactPerson("Pending Contact");
        pendingMerchant.setEmail("pending@example.com");
        pendingMerchant.setPassword(passwordEncoder.encode("password123"));
        pendingMerchant.setPhone("13900139001");
        pendingMerchant.setAddress("Pending Address");
        pendingMerchant.setStatus("pending");
        pendingMerchant.setCreatedAt(LocalDateTime.now());
        pendingMerchant.setUpdatedAt(LocalDateTime.now());
        merchantMapper.insert(pendingMerchant);

        adminToken = jwtUtils.generateTokenFromUsername("admin_test");
    }

    private String getAdminToken(String username, String password) throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .username(username)
                .password(password)
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/admin/login")
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
    @DisplayName("管理员认证接口测试")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class AdminAuthenticationTests {

        @Test
        @Order(1)
        @DisplayName("管理员登录成功")
        void testAdminLoginSuccess() throws Exception {
            LoginRequest loginRequest = LoginRequest.builder()
                    .username("admin_test")
                    .password("admin123")
                    .build();

            mockMvc.perform(post("/api/auth/admin/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Admin login successful"))
                    .andExpect(jsonPath("$.data.token").exists())
                    .andExpect(jsonPath("$.data.user.username").value("admin_test"));
        }

        @Test
        @Order(2)
        @DisplayName("管理员登录失败 - 用户名不存在")
        void testAdminLoginFailUserNotFound() throws Exception {
            LoginRequest loginRequest = LoginRequest.builder()
                    .username("nonexistent")
                    .password("password123")
                    .build();

            mockMvc.perform(post("/api/auth/admin/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @Order(3)
        @DisplayName("管理员登录失败 - 密码错误")
        void testAdminLoginFailWrongPassword() throws Exception {
            LoginRequest loginRequest = LoginRequest.builder()
                    .username("admin_test")
                    .password("wrongpassword")
                    .build();

            mockMvc.perform(post("/api/auth/admin/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @Order(4)
        @DisplayName("管理员登录失败 - 缺少用户名")
        void testAdminLoginFailMissingUsername() throws Exception {
            LoginRequest loginRequest = LoginRequest.builder()
                    .password("password123")
                    .build();

            mockMvc.perform(post("/api/auth/admin/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Username is required"));
        }

        @Test
        @Order(5)
        @DisplayName("管理员登录失败 - 缺少密码")
        void testAdminLoginFailMissingPassword() throws Exception {
            LoginRequest loginRequest = LoginRequest.builder()
                    .username("admin_test")
                    .build();

            mockMvc.perform(post("/api/auth/admin/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Password is required"));
        }
    }

    @Nested
    @DisplayName("用户管理接口测试")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class UserManagementTests {

        @Test
        @Order(1)
        @DisplayName("获取用户列表成功")
        void testGetUsersSuccess() throws Exception {
            mockMvc.perform(get("/api/admin/users")
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                    .andExpect(jsonPath("$[?(@.username == 'user_test')]").exists());
        }

        @Test
        @Order(2)
        @DisplayName("获取用户列表失败 - 未授权")
        void testGetUsersUnauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @Order(3)
        @DisplayName("获取用户详情成功")
        void testGetUserDetailSuccess() throws Exception {
            mockMvc.perform(get("/api/admin/users/" + testUser.getId())
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.user.username").value("user_test"))
                    .andExpect(jsonPath("$.data.user.email").value("user_test@example.com"))
                    .andExpect(jsonPath("$.data.user.phone").value("13800138000"))
                    .andExpect(jsonPath("$.data.stats").exists());
        }

        @Test
        @Order(4)
        @DisplayName("获取用户详情失败 - 用户不存在")
        void testGetUserDetailNotFound() throws Exception {
            mockMvc.perform(get("/api/admin/users/99999")
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("User not found"));
        }

        @Test
        @Order(5)
        @DisplayName("获取用户详情失败 - 未授权")
        void testGetUserDetailUnauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/users/" + testUser.getId()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @Order(6)
        @DisplayName("禁用用户成功")
        void testDisableUserSuccess() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "disabled");

            mockMvc.perform(put("/api/admin/users/" + testUser.getId() + "/status")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.status").value("disabled"));

            User updatedUser = userMapper.selectById(testUser.getId());
            Assertions.assertEquals("disabled", updatedUser.getStatus());
        }

        @Test
        @Order(7)
        @DisplayName("启用用户成功")
        void testEnableUserSuccess() throws Exception {
            testUser.setStatus("disabled");
            userMapper.updateById(testUser);

            Map<String, String> request = new HashMap<>();
            request.put("status", "active");

            mockMvc.perform(put("/api/admin/users/" + testUser.getId() + "/status")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.status").value("active"));

            User updatedUser = userMapper.selectById(testUser.getId());
            Assertions.assertEquals("active", updatedUser.getStatus());
        }

        @Test
        @Order(8)
        @DisplayName("更新用户状态失败 - 无效状态")
        void testUpdateUserStatusInvalidStatus() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "invalid_status");

            mockMvc.perform(put("/api/admin/users/" + testUser.getId() + "/status")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Invalid status. Must be 'active' or 'disabled'"));
        }

        @Test
        @Order(9)
        @DisplayName("更新用户状态失败 - 用户不存在")
        void testUpdateUserStatusUserNotFound() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "active");

            mockMvc.perform(put("/api/admin/users/99999/status")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @Order(10)
        @DisplayName("更新用户状态失败 - 未授权")
        void testUpdateUserStatusUnauthorized() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "active");

            mockMvc.perform(put("/api/admin/users/" + testUser.getId() + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("商家审核接口测试")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class MerchantAuditTests {

        @Test
        @Order(1)
        @DisplayName("获取待审核商家列表成功")
        void testGetPendingMerchantsSuccess() throws Exception {
            mockMvc.perform(get("/api/admin/merchants/pending")
                            .header("Authorization", "Bearer " + adminToken)
                            .param("page", "0")
                            .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.data").isArray())
                    .andExpect(jsonPath("$.data.data[?(@.status == 'pending')]").exists());
        }

        @Test
        @Order(2)
        @DisplayName("获取待审核商家列表失败 - 未授权")
        void testGetPendingMerchantsUnauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/merchants/pending"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @Order(3)
        @DisplayName("获取待审核商家列表 - 按关键字搜索")
        void testGetPendingMerchantsWithKeyword() throws Exception {
            mockMvc.perform(get("/api/admin/merchants/pending")
                            .header("Authorization", "Bearer " + adminToken)
                            .param("page", "0")
                            .param("pageSize", "10")
                            .param("keyword", "Pending"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.data").isArray());
        }

        @Test
        @Order(4)
        @DisplayName("审核商家通过成功")
        void testAuditMerchantApproveSuccess() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "approved");
            request.put("reason", "资质审核通过");

            mockMvc.perform(put("/api/admin/merchants/" + pendingMerchant.getId() + "/audit")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.status").value("approved"));

            Merchant updatedMerchant = merchantMapper.selectById(pendingMerchant.getId());
            Assertions.assertEquals("approved", updatedMerchant.getStatus());
        }

        @Test
        @Order(5)
        @DisplayName("审核商家拒绝成功")
        void testAuditMerchantRejectSuccess() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "rejected");
            request.put("reason", "资质不符合要求");

            mockMvc.perform(put("/api/admin/merchants/" + pendingMerchant.getId() + "/audit")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.status").value("rejected"));

            Merchant updatedMerchant = merchantMapper.selectById(pendingMerchant.getId());
            Assertions.assertEquals("rejected", updatedMerchant.getStatus());
        }

        @Test
        @Order(6)
        @DisplayName("审核商家失败 - 无效状态")
        void testAuditMerchantInvalidStatus() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "invalid_status");
            request.put("reason", "测试");

            mockMvc.perform(put("/api/admin/merchants/" + pendingMerchant.getId() + "/audit")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Invalid audit status. Must be 'approved' or 'rejected'"));
        }

        @Test
        @Order(7)
        @DisplayName("审核商家失败 - 商家不存在")
        void testAuditMerchantNotFound() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "approved");
            request.put("reason", "测试");

            mockMvc.perform(put("/api/admin/merchants/99999/audit")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @Order(8)
        @DisplayName("审核商家失败 - 未授权")
        void testAuditMerchantUnauthorized() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "approved");
            request.put("reason", "测试");

            mockMvc.perform(put("/api/admin/merchants/" + pendingMerchant.getId() + "/audit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @Order(9)
        @DisplayName("获取商家详情成功")
        void testGetMerchantDetailSuccess() throws Exception {
            mockMvc.perform(get("/api/admin/merchants/" + testMerchant.getId())
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.name").value("Approved Merchant"))
                    .andExpect(jsonPath("$.data.contactPerson").value("Contact Person"))
                    .andExpect(jsonPath("$.data.email").value("approved@example.com"));
        }

        @Test
        @Order(10)
        @DisplayName("获取商家详情失败 - 商家不存在")
        void testGetMerchantDetailNotFound() throws Exception {
            mockMvc.perform(get("/api/admin/merchants/99999")
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @Order(11)
        @DisplayName("获取商家详情失败 - 未授权")
        void testGetMerchantDetailUnauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/merchants/" + testMerchant.getId()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("系统管理接口测试")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class SystemManagementTests {

        @Test
        @Order(1)
        @DisplayName("获取系统配置成功")
        void testGetSystemConfigSuccess() throws Exception {
            mockMvc.perform(get("/api/admin/system/config")
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.websiteName").exists());
        }

        @Test
        @Order(2)
        @DisplayName("获取系统配置失败 - 未授权")
        void testGetSystemConfigUnauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/system/config"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @Order(3)
        @DisplayName("更新系统配置成功")
        void testUpdateSystemConfigSuccess() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("websiteName", "宠物家园");
            request.put("contactEmail", "support@petshop.com");
            request.put("contactPhone", "400-123-4567");

            mockMvc.perform(put("/api/admin/system/config")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.websiteName").value("宠物家园"))
                    .andExpect(jsonPath("$.data.contactEmail").value("support@petshop.com"))
                    .andExpect(jsonPath("$.data.contactPhone").value("400-123-4567"));
        }

        @Test
        @Order(4)
        @DisplayName("更新系统配置失败 - 未授权")
        void testUpdateSystemConfigUnauthorized() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("websiteName", "宠物家园");

            mockMvc.perform(put("/api/admin/system/config")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @Order(5)
        @DisplayName("发布公告成功")
        void testCreateAnnouncementSuccess() throws Exception {
            Announcement announcement = new Announcement();
            announcement.setTitle("系统维护通知");
            announcement.setContent("系统将于今晚进行维护，届时服务将暂停。");
            announcement.setStatus("published");

            mockMvc.perform(post("/api/admin/announcements")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(announcement)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.title").value("系统维护通知"))
                    .andExpect(jsonPath("$.data.content").value("系统将于今晚进行维护，届时服务将暂停。"))
                    .andExpect(jsonPath("$.data.status").value("published"));
        }

        @Test
        @Order(6)
        @DisplayName("发布公告失败 - 未授权")
        void testCreateAnnouncementUnauthorized() throws Exception {
            Announcement announcement = new Announcement();
            announcement.setTitle("测试公告");
            announcement.setContent("测试内容");
            announcement.setStatus("published");

            mockMvc.perform(post("/api/admin/announcements")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(announcement)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @Order(7)
        @DisplayName("发布公告成功 - 草稿状态")
        void testCreateAnnouncementDraftSuccess() throws Exception {
            Announcement announcement = new Announcement();
            announcement.setTitle("新功能预告");
            announcement.setContent("即将上线新功能，敬请期待！");
            announcement.setStatus("draft");

            mockMvc.perform(post("/api/admin/announcements")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(announcement)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.status").value("draft"));
        }
    }

    @Nested
    @DisplayName("综合场景测试")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class IntegrationTests {

        @Test
        @Order(1)
        @DisplayName("完整审核流程 - 商家注册到审核通过")
        void testCompleteMerchantAuditFlow() throws Exception {
            Merchant newMerchant = new Merchant();
            newMerchant.setName("New Test Merchant");
            newMerchant.setContactPerson("New Contact");
            newMerchant.setEmail("newmerchant@example.com");
            newMerchant.setPassword(passwordEncoder.encode("password123"));
            newMerchant.setPhone("13900139002");
            newMerchant.setAddress("New Address");
            newMerchant.setStatus("pending");
            newMerchant.setCreatedAt(LocalDateTime.now());
            newMerchant.setUpdatedAt(LocalDateTime.now());
            merchantMapper.insert(newMerchant);

            mockMvc.perform(get("/api/admin/merchants/pending")
                            .header("Authorization", "Bearer " + adminToken)
                            .param("page", "0")
                            .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.data[?(@.name == 'New Test Merchant')]").exists());

            Map<String, String> auditRequest = new HashMap<>();
            auditRequest.put("status", "approved");
            auditRequest.put("reason", "所有资质审核通过");

            mockMvc.perform(put("/api/admin/merchants/" + newMerchant.getId() + "/audit")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(auditRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value("approved"));

            mockMvc.perform(get("/api/admin/merchants/" + newMerchant.getId())
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value("approved"));
        }

        @Test
        @Order(2)
        @DisplayName("完整用户管理流程 - 禁用再启用用户")
        void testCompleteUserManagementFlow() throws Exception {
            User newUser = new User();
            newUser.setUsername("flow_test_user");
            newUser.setEmail("flowtest@example.com");
            newUser.setPassword(passwordEncoder.encode("password123"));
            newUser.setPhone("13800138002");
            newUser.setStatus("active");
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedAt(LocalDateTime.now());
            userMapper.insert(newUser);

            mockMvc.perform(get("/api/admin/users/" + newUser.getId())
                            .header("Authorization", "Bearer " + adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.user.status").value("active"));

            Map<String, String> disableRequest = new HashMap<>();
            disableRequest.put("status", "disabled");

            mockMvc.perform(put("/api/admin/users/" + newUser.getId() + "/status")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(disableRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value("disabled"));

            Map<String, String> enableRequest = new HashMap<>();
            enableRequest.put("status", "active");

            mockMvc.perform(put("/api/admin/users/" + newUser.getId() + "/status")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(enableRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value("active"));
        }

        @Test
        @Order(3)
        @DisplayName("完整公告管理流程 - 创建和查看公告")
        void testCompleteAnnouncementFlow() throws Exception {
            Announcement announcement = new Announcement();
            announcement.setTitle("重要通知");
            announcement.setContent("这是一条重要的系统通知。");
            announcement.setStatus("published");

            mockMvc.perform(post("/api/admin/announcements")
                            .header("Authorization", "Bearer " + adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(announcement)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.title").value("重要通知"));

            mockMvc.perform(get("/api/admin/announcements")
                            .header("Authorization", "Bearer " + adminToken)
                            .param("page", "0")
                            .param("pageSize", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.data[?(@.title == '重要通知')]").exists());
        }
    }
}
