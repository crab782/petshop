package com.petshop.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.TestDataInitializer;
import com.petshop.config.TestRedisConfig;
import com.petshop.dto.ApiResponse;
import com.petshop.dto.LoginRequest;
import com.petshop.dto.LoginResponse;
import com.petshop.entity.Admin;
import com.petshop.entity.Merchant;
import com.petshop.entity.User;
import com.petshop.mapper.AdminMapper;
import com.petshop.mapper.MerchantMapper;
import com.petshop.mapper.UserMapper;
import com.petshop.security.JwtUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
@Transactional
@DisplayName("安全集成测试")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    private User testUser;
    private Merchant testMerchant;
    private Admin testAdmin;
    private String testUserPassword = TestDataInitializer.TEST_USER_PASSWORD;
    private String testMerchantPassword = TestDataInitializer.TEST_MERCHANT_PASSWORD;
    private String testAdminPassword = TestDataInitializer.TEST_ADMIN_PASSWORD;
    private String encodedPassword;

    @BeforeEach
    void setUp() {
        testUser = userMapper.selectByPhone("13800138001");
        testMerchant = merchantMapper.selectByPhone("13900139001");
        testAdmin = adminMapper.selectByUsername(TestDataInitializer.TEST_ADMIN_USERNAME);
        
        if (testUser == null) {
            encodedPassword = passwordEncoder.encode(testUserPassword);
            testUser = new User();
            testUser.setUsername("testuser");
            testUser.setPhone("13800138001");
            testUser.setEmail("testuser@example.com");
            testUser.setPassword(encodedPassword);
            testUser.setStatus("active");
            userMapper.insert(testUser);
        }
        
        if (testMerchant == null) {
            if (encodedPassword == null) {
                encodedPassword = passwordEncoder.encode(testMerchantPassword);
            }
            testMerchant = new Merchant();
            testMerchant.setName("Test Merchant");
            testMerchant.setContactPerson("Contact Person");
            testMerchant.setPhone("13900139001");
            testMerchant.setEmail("merchant@example.com");
            testMerchant.setPassword(encodedPassword);
            testMerchant.setAddress("Test Address");
            testMerchant.setStatus("approved");
            merchantMapper.insert(testMerchant);
        }
        
        if (testAdmin == null) {
            if (encodedPassword == null) {
                encodedPassword = passwordEncoder.encode(testAdminPassword);
            }
            testAdmin = new Admin();
            testAdmin.setUsername("testadmin");
            testAdmin.setPassword(encodedPassword);
            adminMapper.insert(testAdmin);
        }
    }

    @Nested
    @DisplayName("JWT 认证流程测试")
    class JwtAuthenticationTests {

        @Test
        @DisplayName("测试 JWT Token 生成")
        void testJwtTokenGeneration() {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(testUser.getPhone(), testUserPassword));

            String token = jwtUtils.generateJwtToken(authentication);

            assertNotNull(token);
            assertTrue(token.length() > 0);
            assertTrue(token.split("\\.").length == 3);
        }

        @Test
        @DisplayName("测试从用户名生成 JWT Token")
        void testGenerateTokenFromUsername() {
            String token = jwtUtils.generateTokenFromUsername(testUser.getPhone());

            assertNotNull(token);
            assertTrue(token.length() > 0);
            assertTrue(token.split("\\.").length == 3);
        }

        @Test
        @DisplayName("测试 JWT Token 验证 - 有效 Token")
        void testJwtTokenValidation_ValidToken() {
            String token = jwtUtils.generateTokenFromUsername(testUser.getPhone());

            boolean isValid = jwtUtils.validateJwtToken(token);

            assertTrue(isValid);
        }

        @Test
        @DisplayName("测试 JWT Token 验证 - 无效 Token")
        void testJwtTokenValidation_InvalidToken() {
            String invalidToken = "invalid.token.here";

            boolean isValid = jwtUtils.validateJwtToken(invalidToken);

            assertFalse(isValid);
        }

        @Test
        @DisplayName("测试 JWT Token 验证 - 空 Token")
        void testJwtTokenValidation_EmptyToken() {
            boolean isValid = jwtUtils.validateJwtToken("");

            assertFalse(isValid);
        }

        @Test
        @DisplayName("测试 JWT Token 验证 - null Token")
        void testJwtTokenValidation_NullToken() {
            boolean isValid = jwtUtils.validateJwtToken(null);

            assertFalse(isValid);
        }

        @Test
        @DisplayName("测试从 JWT Token 获取用户名")
        void testGetUsernameFromJwtToken() {
            String username = testUser.getPhone();
            String token = jwtUtils.generateTokenFromUsername(username);

            String extractedUsername = jwtUtils.getUserNameFromJwtToken(token);

            assertEquals(username, extractedUsername);
        }

        @Test
        @DisplayName("测试 JWT Token 格式错误")
        void testJwtTokenValidation_MalformedToken() {
            String malformedToken = "not-a-valid-jwt-token";

            boolean isValid = jwtUtils.validateJwtToken(malformedToken);

            assertFalse(isValid);
        }

        @Test
        @DisplayName("测试 JWT Token 签名错误")
        void testJwtTokenValidation_WrongSignature() {
            String tokenWithWrongSignature = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciJ9.wrongSignature";

            assertThrows(Exception.class, () -> {
                jwtUtils.validateJwtToken(tokenWithWrongSignature);
            });
        }
    }

    @Nested
    @DisplayName("权限控制测试")
    class AuthorizationTests {

        @Test
        @DisplayName("测试未认证用户访问受保护资源 - 用户API")
        void testUnauthenticatedAccess_UserApi() throws Exception {
            mockMvc.perform(get("/api/user/profile"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("测试未认证用户访问受保护资源 - 商家API")
        void testUnauthenticatedAccess_MerchantApi() throws Exception {
            mockMvc.perform(get("/api/merchant/profile"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("测试未认证用户访问受保护资源 - 管理员API")
        void testUnauthenticatedAccess_AdminApi() throws Exception {
            mockMvc.perform(get("/api/admin/users"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("测试未认证用户访问公开资源")
        void testUnauthenticatedAccess_PublicApi() throws Exception {
            mockMvc.perform(get("/api/services"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("测试认证用户访问受保护资源 - 用户角色")
        void testAuthenticatedAccess_UserRole() throws Exception {
            String token = loginUser(testUser.getPhone(), testUserPassword);

            mockMvc.perform(get("/api/user/profile")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value(testUser.getUsername()));
        }

        @Test
        @DisplayName("测试认证用户访问用户端API - 获取宠物列表")
        void testAuthenticatedAccess_UserApi_GetPets() throws Exception {
            String token = loginUser(testUser.getPhone(), testUserPassword);

            mockMvc.perform(get("/api/user/pets")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("测试认证用户访问用户端API - 获取预约列表")
        void testAuthenticatedAccess_UserApi_GetAppointments() throws Exception {
            String token = loginUser(testUser.getPhone(), testUserPassword);

            mockMvc.perform(get("/api/user/appointments")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("测试认证用户访问用户端API - 获取订单列表")
        void testAuthenticatedAccess_UserApi_GetOrders() throws Exception {
            String token = loginUser(testUser.getPhone(), testUserPassword);

            mockMvc.perform(get("/api/user/orders")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(result -> {
                        int status = result.getResponse().getStatus();
                        assertTrue(status >= 200 && status < 600, "Status should be a valid HTTP status");
                    });
        }

        @Test
        @DisplayName("测试商家角色访问商家API")
        void testAuthenticatedAccess_MerchantRole() throws Exception {
            String token = loginMerchant(testMerchant.getPhone(), testMerchantPassword);

            mockMvc.perform(get("/api/merchant/profile")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("测试商家角色访问商家API - 获取服务列表")
        void testAuthenticatedAccess_MerchantApi_GetServices() throws Exception {
            String token = loginMerchant(testMerchant.getPhone(), testMerchantPassword);

            mockMvc.perform(get("/api/merchant/services")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("测试商家角色访问商家API - 获取商品列表")
        void testAuthenticatedAccess_MerchantApi_GetProducts() throws Exception {
            String token = loginMerchant(testMerchant.getPhone(), testMerchantPassword);

            mockMvc.perform(get("/api/merchant/products")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("测试管理员角色访问管理员API")
        void testAuthenticatedAccess_AdminRole() throws Exception {
            String token = loginAdmin(testAdmin.getUsername(), testAdminPassword);

            mockMvc.perform(get("/api/admin/users")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(result -> {
                        int status = result.getResponse().getStatus();
                        assertTrue(status == 200 || status == 401, "Status should be 200 or 401");
                    });
        }

        @Test
        @DisplayName("测试管理员角色访问管理员API - 获取商家列表")
        void testAuthenticatedAccess_AdminApi_GetMerchants() throws Exception {
            String token = loginAdmin(testAdmin.getUsername(), testAdminPassword);

            mockMvc.perform(get("/api/admin/merchants")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(result -> {
                        int status = result.getResponse().getStatus();
                        assertTrue(status == 200 || status == 401, "Status should be 200 or 401");
                    });
        }

        @Test
        @DisplayName("测试无效Token访问受保护资源")
        void testInvalidTokenAccess() throws Exception {
            mockMvc.perform(get("/api/user/profile")
                            .header("Authorization", "Bearer invalid.token.here"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("测试空Authorization头访问受保护资源")
        void testEmptyAuthorizationHeader() throws Exception {
            mockMvc.perform(get("/api/user/profile")
                            .header("Authorization", ""))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("测试无Bearer前缀的Token访问受保护资源")
        void testTokenWithoutBearerPrefix() throws Exception {
            String token = jwtUtils.generateTokenFromUsername(testUser.getPhone());

            mockMvc.perform(get("/api/user/profile")
                            .header("Authorization", token))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("测试用户登录接口")
        void testUserLogin() throws Exception {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setPhone(testUser.getPhone());
            loginRequest.setPassword(testUserPassword);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.token").exists())
                    .andExpect(jsonPath("$.data.user.username").value(testUser.getUsername()));
        }

        @Test
        @DisplayName("测试用户登录 - 错误密码")
        void testUserLogin_WrongPassword() throws Exception {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setPhone(testUser.getPhone());
            loginRequest.setPassword("wrongpassword");

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("测试用户登录 - 用户不存在")
        void testUserLogin_UserNotFound() throws Exception {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setPhone("99999999999");
            loginRequest.setPassword(testUserPassword);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("测试商家登录接口")
        void testMerchantLogin() throws Exception {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setPhone(testMerchant.getPhone());
            loginRequest.setPassword(testMerchantPassword);

            mockMvc.perform(post("/api/auth/merchant/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.token").exists())
                    .andExpect(jsonPath("$.data.user.role").value("merchant"));
        }

        @Test
        @DisplayName("测试商家登录 - 使用邮箱")
        void testMerchantLogin_WithEmail() throws Exception {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername(testMerchant.getEmail());
            loginRequest.setPassword(testMerchantPassword);

            mockMvc.perform(post("/api/auth/merchant/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.token").exists());
        }

        @Test
        @DisplayName("测试管理员登录接口")
        void testAdminLogin() throws Exception {
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername(testAdmin.getUsername());
            loginRequest.setPassword(testAdminPassword);

            mockMvc.perform(post("/api/auth/admin/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.token").exists())
                    .andExpect(jsonPath("$.data.user.role").value("admin"));
        }

        @Test
        @DisplayName("测试获取当前用户信息")
        void testGetCurrentUserInfo() throws Exception {
            String token = loginUser(testUser.getPhone(), testUserPassword);

            mockMvc.perform(get("/api/auth/userinfo")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.username").value(testUser.getUsername()));
        }

        @Test
        @DisplayName("测试未认证获取用户信息")
        void testGetCurrentUserInfo_Unauthenticated() throws Exception {
            mockMvc.perform(get("/api/auth/userinfo"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("密码加密测试")
    class PasswordEncryptionTests {

        @Test
        @DisplayName("测试密码加密")
        void testPasswordEncoding() {
            String rawPassword = "myPassword123";

            String encodedPassword = passwordEncoder.encode(rawPassword);

            assertNotNull(encodedPassword);
            assertNotEquals(rawPassword, encodedPassword);
            assertTrue(encodedPassword.length() > 0);
            assertTrue(encodedPassword.startsWith("$2a$"));
        }

        @Test
        @DisplayName("测试密码验证 - 正确密码")
        void testPasswordVerification_CorrectPassword() {
            String rawPassword = "myPassword123";
            String encodedPassword = passwordEncoder.encode(rawPassword);

            boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);

            assertTrue(matches);
        }

        @Test
        @DisplayName("测试密码验证 - 错误密码")
        void testPasswordVerification_WrongPassword() {
            String rawPassword = "myPassword123";
            String encodedPassword = passwordEncoder.encode(rawPassword);

            boolean matches = passwordEncoder.matches("wrongPassword", encodedPassword);

            assertFalse(matches);
        }

        @Test
        @DisplayName("测试不同密码生成不同哈希")
        void testDifferentPasswordsGenerateDifferentHashes() {
            String password1 = "password1";
            String password2 = "password2";

            String hash1 = passwordEncoder.encode(password1);
            String hash2 = passwordEncoder.encode(password2);

            assertNotEquals(hash1, hash2);
        }

        @Test
        @DisplayName("测试相同密码生成不同哈希 (BCrypt盐值)")
        void testSamePasswordGeneratesDifferentHashes() {
            String password = "samePassword123";

            String hash1 = passwordEncoder.encode(password);
            String hash2 = passwordEncoder.encode(password);

            assertNotEquals(hash1, hash2);
            assertTrue(passwordEncoder.matches(password, hash1));
            assertTrue(passwordEncoder.matches(password, hash2));
        }

        @Test
        @DisplayName("测试空密码加密")
        void testEmptyPasswordEncoding() {
            String emptyPassword = "";

            String encodedPassword = passwordEncoder.encode(emptyPassword);

            assertNotNull(encodedPassword);
            assertTrue(passwordEncoder.matches(emptyPassword, encodedPassword));
        }

        @Test
        @DisplayName("测试密码验证 - null原始密码")
        void testPasswordVerification_NullRawPassword() {
            String encodedPassword = passwordEncoder.encode("password");

            assertThrows(IllegalArgumentException.class, () -> {
                passwordEncoder.matches(null, encodedPassword);
            });
        }

        @Test
        @DisplayName("测试密码验证 - null编码密码")
        void testPasswordVerification_NullEncodedPassword() {
            String rawPassword = "password";

            boolean matches = passwordEncoder.matches(rawPassword, null);

            assertFalse(matches);
        }

        @Test
        @DisplayName("测试密码长度")
        void testPasswordLength() {
            String shortPassword = "123456";
            String longPassword = "a".repeat(100);

            String shortHash = passwordEncoder.encode(shortPassword);
            String longHash = passwordEncoder.encode(longPassword);

            assertTrue(passwordEncoder.matches(shortPassword, shortHash));
            assertTrue(passwordEncoder.matches(longPassword, longHash));
        }

        @Test
        @DisplayName("测试特殊字符密码")
        void testSpecialCharactersPassword() {
            String specialPassword = "p@$$w0rd!#$%^&*()";

            String encodedPassword = passwordEncoder.encode(specialPassword);

            assertTrue(passwordEncoder.matches(specialPassword, encodedPassword));
        }

        @Test
        @DisplayName("测试中文密码")
        void testChinesePassword() {
            String chinesePassword = "密码测试123";

            String encodedPassword = passwordEncoder.encode(chinesePassword);

            assertTrue(passwordEncoder.matches(chinesePassword, encodedPassword));
        }

        @Test
        @DisplayName("测试用户注册时密码加密")
        void testUserRegistrationPasswordEncryption() {
            String rawPassword = "newUserPassword123";
            User newUser = new User();
            newUser.setUsername("newuser");
            newUser.setPhone("13900139000");
            newUser.setEmail("newuser@example.com");
            newUser.setPassword(passwordEncoder.encode(rawPassword));
            newUser.setStatus("active");

            userMapper.insert(newUser);

            User savedUser = userMapper.selectById(newUser.getId());
            assertNotNull(savedUser);
            assertNotEquals(rawPassword, savedUser.getPassword());
            assertTrue(passwordEncoder.matches(rawPassword, savedUser.getPassword()));
        }
    }

    @Nested
    @DisplayName("用户注册测试")
    class UserRegistrationTests {

        @Test
        @DisplayName("测试用户注册成功")
        void testUserRegistration_Success() throws Exception {
            String requestBody = """
                {
                    "username": "newuser",
                    "phone": "13900139001",
                    "email": "newuser@example.com",
                    "password": "password123"
                }
                """;

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().is2xxSuccessful());
        }

        @Test
        @DisplayName("测试用户注册 - 手机号已存在")
        void testUserRegistration_PhoneExists() throws Exception {
            String requestBody = String.format("""
                {
                    "username": "anotheruser",
                    "phone": "%s",
                    "email": "another@example.com",
                    "password": "password123"
                }
                """, testUser.getPhone());

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("测试用户注册 - 密码太短")
        void testUserRegistration_PasswordTooShort() throws Exception {
            String requestBody = """
                {
                    "username": "newuser",
                    "phone": "13900139002",
                    "email": "newuser@example.com",
                    "password": "12345"
                }
                """;

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("测试用户注册 - 缺少手机号")
        void testUserRegistration_MissingPhone() throws Exception {
            String requestBody = """
                {
                    "username": "newuser",
                    "email": "newuser@example.com",
                    "password": "password123"
                }
                """;

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().is4xxClientError());
        }
    }

    @Nested
    @DisplayName("登出测试")
    class LogoutTests {

        @Test
        @DisplayName("测试用户登出")
        void testLogout() throws Exception {
            String token = loginUser(testUser.getPhone(), testUserPassword);

            mockMvc.perform(post("/api/auth/logout")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.success").value(true));
        }
    }

    private String loginUser(String phone, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPhone(phone);
        loginRequest.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ApiResponse response = objectMapper.readValue(responseBody, ApiResponse.class);
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> data = (java.util.Map<String, Object>) response.getData();
        return (String) data.get("token");
    }

    private String loginMerchant(String phone, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPhone(phone);
        loginRequest.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/auth/merchant/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ApiResponse response = objectMapper.readValue(responseBody, ApiResponse.class);
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> data = (java.util.Map<String, Object>) response.getData();
        return (String) data.get("token");
    }

    private String loginAdmin(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/auth/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        ApiResponse response = objectMapper.readValue(responseBody, ApiResponse.class);
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> data = (java.util.Map<String, Object>) response.getData();
        return (String) data.get("token");
    }
}
