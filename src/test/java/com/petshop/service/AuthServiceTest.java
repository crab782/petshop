package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.dto.*;
import com.petshop.entity.Admin;
import com.petshop.entity.Merchant;
import com.petshop.entity.User;
import com.petshop.exception.BadRequestException;
import com.petshop.exception.ResourceNotFoundException;
import com.petshop.mapper.AdminMapper;
import com.petshop.mapper.MerchantMapper;
import com.petshop.mapper.UserMapper;
import com.petshop.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private MerchantMapper merchantMapper;

    @Mock
    private AdminMapper adminMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Merchant testMerchant;
    private Admin testAdmin;
    private final String testPhone = "13800138000";
    private final String testPassword = "password123";
    private final String testEncodedPassword = "encodedPassword123";
    private final String testJwtToken = "test.jwt.token";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setPhone(testPhone);
        testUser.setEmail("test@example.com");
        testUser.setPassword(testEncodedPassword);
        testUser.setAvatar("avatar.png");

        testMerchant = new Merchant();
        testMerchant.setId(1);
        testMerchant.setName("Test Merchant");
        testMerchant.setPhone(testPhone);
        testMerchant.setEmail("merchant@example.com");
        testMerchant.setPassword(testEncodedPassword);
        testMerchant.setLogo("logo.png");
        testMerchant.setAddress("Test Address");
        testMerchant.setContactPerson("Contact Person");
        testMerchant.setStatus("approved");

        testAdmin = new Admin();
        testAdmin.setId(1);
        testAdmin.setUsername("admin");
        testAdmin.setPassword(testEncodedPassword);
    }

    @Nested
    @DisplayName("用户登录功能测试")
    class UserLoginTests {

        @Test
        @DisplayName("正常登录 - 使用手机号登录成功")
        void testLogin_Success() {
            LoginRequest request = LoginRequest.builder()
                    .phone(testPhone)
                    .password(testPassword)
                    .build();

            when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
            when(passwordEncoder.matches(testPassword, testEncodedPassword)).thenReturn(true);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
            when(jwtUtils.generateJwtToken(authentication)).thenReturn(testJwtToken);

            LoginResponse response = authService.login(request);

            assertNotNull(response);
            assertEquals(testJwtToken, response.getToken());
            assertNotNull(response.getUser());
            assertEquals(testUser.getId(), response.getUser().getId());
            assertEquals(testUser.getUsername(), response.getUser().getUsername());
            assertEquals("user", response.getUser().getRole());

            verify(userMapper).selectOne(any(LambdaQueryWrapper.class));
            verify(passwordEncoder).matches(testPassword, testEncodedPassword);
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtUtils).generateJwtToken(authentication);
        }

        @Test
        @DisplayName("登录失败 - 用户不存在")
        void testLogin_UserNotFound() {
            LoginRequest request = LoginRequest.builder()
                    .phone("99999999999")
                    .password(testPassword)
                    .build();

            when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> authService.login(request)
            );

            assertTrue(exception.getMessage().contains("User not found"));
            verify(userMapper).selectOne(any(LambdaQueryWrapper.class));
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("登录失败 - 密码错误")
        void testLogin_InvalidPassword() {
            LoginRequest request = LoginRequest.builder()
                    .phone(testPhone)
                    .password("wrongpassword")
                    .build();

            when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
            when(passwordEncoder.matches("wrongpassword", testEncodedPassword)).thenReturn(false);

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> authService.login(request)
            );

            assertEquals("Invalid password", exception.getMessage());
            verify(userMapper).selectOne(any(LambdaQueryWrapper.class));
            verify(passwordEncoder).matches("wrongpassword", testEncodedPassword);
            verify(authenticationManager, never()).authenticate(any());
        }
    }

    @Nested
    @DisplayName("用户注册功能测试")
    class UserRegisterTests {

        @Test
        @DisplayName("正常注册 - 注册成功")
        void testRegister_Success() {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password(testPassword)
                    .email("newuser@example.com")
                    .phone("13900139000")
                    .build();

            when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(passwordEncoder.encode(testPassword)).thenReturn(testEncodedPassword);
            when(userMapper.insert(any(User.class))).thenReturn(1);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
            when(jwtUtils.generateJwtToken(authentication)).thenReturn(testJwtToken);

            LoginResponse response = authService.register(request);

            assertNotNull(response);
            assertEquals(testJwtToken, response.getToken());
            assertNotNull(response.getUser());

            verify(userMapper).selectCount(any(LambdaQueryWrapper.class));
            verify(passwordEncoder).encode(testPassword);
            verify(userMapper).insert(any(User.class));
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }

        @Test
        @DisplayName("注册失败 - 手机号为空")
        void testRegister_PhoneRequired() {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password(testPassword)
                    .email("newuser@example.com")
                    .phone(null)
                    .build();

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> authService.register(request)
            );

            assertEquals("Phone number is required", exception.getMessage());
            verify(userMapper, never()).selectCount(any());
        }

        @Test
        @DisplayName("注册失败 - 手机号为空字符串")
        void testRegister_EmptyPhone() {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password(testPassword)
                    .email("newuser@example.com")
                    .phone("")
                    .build();

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> authService.register(request)
            );

            assertEquals("Phone number is required", exception.getMessage());
        }

        @Test
        @DisplayName("注册失败 - 手机号已存在")
        void testRegister_PhoneAlreadyExists() {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password(testPassword)
                    .email("newuser@example.com")
                    .phone(testPhone)
                    .build();

            when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> authService.register(request)
            );

            assertEquals("Phone number already registered", exception.getMessage());
            verify(userMapper).selectCount(any(LambdaQueryWrapper.class));
            verify(userMapper, never()).insert(any());
        }

        @Test
        @DisplayName("注册成功 - 用户名为空时使用手机号作为用户名")
        void testRegister_UsePhoneAsUsername() {
            RegisterRequest request = RegisterRequest.builder()
                    .username(null)
                    .password(testPassword)
                    .email("newuser@example.com")
                    .phone("13900139000")
                    .build();

            when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(passwordEncoder.encode(testPassword)).thenReturn(testEncodedPassword);
            when(userMapper.insert(any(User.class))).thenReturn(1);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
            when(jwtUtils.generateJwtToken(authentication)).thenReturn(testJwtToken);

            LoginResponse response = authService.register(request);

            assertNotNull(response);
            verify(userMapper).insert(argThat(user -> 
                user.getUsername().equals("13900139000")
            ));
        }

        @Test
        @DisplayName("注册成功 - 邮箱为空时设置为空字符串")
        void testRegister_EmptyEmail() {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password(testPassword)
                    .email(null)
                    .phone("13900139000")
                    .build();

            when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(passwordEncoder.encode(testPassword)).thenReturn(testEncodedPassword);
            when(userMapper.insert(any(User.class))).thenReturn(1);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
            when(jwtUtils.generateJwtToken(authentication)).thenReturn(testJwtToken);

            LoginResponse response = authService.register(request);

            assertNotNull(response);
            verify(userMapper).insert(argThat(user -> 
                user.getEmail().equals("")
            ));
        }
    }

    @Nested
    @DisplayName("商家登录功能测试")
    class MerchantLoginTests {

        @Test
        @DisplayName("正常登录 - 使用手机号登录成功")
        void testMerchantLogin_Success_WithPhone() {
            LoginRequest request = LoginRequest.builder()
                    .phone(testPhone)
                    .password(testPassword)
                    .build();

            when(merchantMapper.selectByPhone(testPhone)).thenReturn(testMerchant);
            when(passwordEncoder.matches(testPassword, testEncodedPassword)).thenReturn(true);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
            when(jwtUtils.generateJwtToken(authentication)).thenReturn(testJwtToken);

            LoginResponse response = authService.merchantLogin(request);

            assertNotNull(response);
            assertEquals(testJwtToken, response.getToken());
            assertNotNull(response.getUser());
            assertEquals(testMerchant.getId(), response.getUser().getId());
            assertEquals(testMerchant.getName(), response.getUser().getUsername());
            assertEquals("merchant", response.getUser().getRole());

            verify(merchantMapper).selectByPhone(testPhone);
            verify(passwordEncoder).matches(testPassword, testEncodedPassword);
        }

        @Test
        @DisplayName("正常登录 - 使用邮箱登录成功")
        void testMerchantLogin_Success_WithEmail() {
            LoginRequest request = LoginRequest.builder()
                    .username("merchant@example.com")
                    .password(testPassword)
                    .build();

            when(merchantMapper.selectByPhone("merchant@example.com")).thenReturn(null);
            when(merchantMapper.selectByEmail("merchant@example.com")).thenReturn(testMerchant);
            when(passwordEncoder.matches(testPassword, testEncodedPassword)).thenReturn(true);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
            when(jwtUtils.generateJwtToken(authentication)).thenReturn(testJwtToken);

            LoginResponse response = authService.merchantLogin(request);

            assertNotNull(response);
            assertEquals(testJwtToken, response.getToken());
            assertEquals("merchant", response.getUser().getRole());

            verify(merchantMapper).selectByPhone("merchant@example.com");
            verify(merchantMapper).selectByEmail("merchant@example.com");
        }

        @Test
        @DisplayName("登录失败 - 商家不存在")
        void testMerchantLogin_MerchantNotFound() {
            LoginRequest request = LoginRequest.builder()
                    .phone("99999999999")
                    .password(testPassword)
                    .build();

            when(merchantMapper.selectByPhone("99999999999")).thenReturn(null);
            when(merchantMapper.selectByEmail("99999999999")).thenReturn(null);

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> authService.merchantLogin(request)
            );

            assertEquals("Merchant not found", exception.getMessage());
        }

        @Test
        @DisplayName("登录失败 - 商家未审核")
        void testMerchantLogin_MerchantNotApproved() {
            testMerchant.setStatus("pending");
            LoginRequest request = LoginRequest.builder()
                    .phone(testPhone)
                    .password(testPassword)
                    .build();

            when(merchantMapper.selectByPhone(testPhone)).thenReturn(testMerchant);

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> authService.merchantLogin(request)
            );

            assertTrue(exception.getMessage().contains("Merchant account is not approved"));
            assertTrue(exception.getMessage().contains("pending"));
        }

        @Test
        @DisplayName("登录失败 - 商家被拒绝")
        void testMerchantLogin_MerchantRejected() {
            testMerchant.setStatus("rejected");
            LoginRequest request = LoginRequest.builder()
                    .phone(testPhone)
                    .password(testPassword)
                    .build();

            when(merchantMapper.selectByPhone(testPhone)).thenReturn(testMerchant);

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> authService.merchantLogin(request)
            );

            assertTrue(exception.getMessage().contains("rejected"));
        }

        @Test
        @DisplayName("登录失败 - 密码错误")
        void testMerchantLogin_InvalidPassword() {
            LoginRequest request = LoginRequest.builder()
                    .phone(testPhone)
                    .password("wrongpassword")
                    .build();

            when(merchantMapper.selectByPhone(testPhone)).thenReturn(testMerchant);
            when(passwordEncoder.matches("wrongpassword", testEncodedPassword)).thenReturn(false);

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> authService.merchantLogin(request)
            );

            assertEquals("Invalid password", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("商家注册功能测试")
    class MerchantRegisterTests {

        @Test
        @DisplayName("正常注册 - 注册成功")
        void testMerchantRegister_Success() {
            MerchantRegisterRequest request = MerchantRegisterRequest.builder()
                    .name("New Merchant")
                    .password(testPassword)
                    .email("newmerchant@example.com")
                    .phone("13900139000")
                    .contact_person("Contact")
                    .address("Address")
                    .build();

            when(merchantMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(passwordEncoder.encode(testPassword)).thenReturn(testEncodedPassword);
            when(merchantMapper.insert(any(Merchant.class))).thenReturn(1);

            Map<String, String> result = authService.merchantRegister(request);

            assertNotNull(result);
            assertTrue(result.containsKey("message"));
            assertTrue(result.get("message").contains("registration successful"));

            verify(merchantMapper).selectCount(any(LambdaQueryWrapper.class));
            verify(passwordEncoder).encode(testPassword);
            verify(merchantMapper).insert(argThat(merchant -> 
                merchant.getStatus().equals("pending") &&
                merchant.getName().equals("New Merchant")
            ));
        }

        @Test
        @DisplayName("注册失败 - 手机号已存在")
        void testMerchantRegister_PhoneAlreadyExists() {
            MerchantRegisterRequest request = MerchantRegisterRequest.builder()
                    .name("New Merchant")
                    .password(testPassword)
                    .email("newmerchant@example.com")
                    .phone(testPhone)
                    .build();

            when(merchantMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> authService.merchantRegister(request)
            );

            assertEquals("Phone number already registered", exception.getMessage());
            verify(merchantMapper, never()).insert(any());
        }

        @Test
        @DisplayName("注册成功 - 名称为空时使用默认名称")
        void testMerchantRegister_DefaultName() {
            MerchantRegisterRequest request = MerchantRegisterRequest.builder()
                    .name(null)
                    .password(testPassword)
                    .email("newmerchant@example.com")
                    .phone("13900139000")
                    .build();

            when(merchantMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(passwordEncoder.encode(testPassword)).thenReturn(testEncodedPassword);
            when(merchantMapper.insert(any(Merchant.class))).thenReturn(1);

            Map<String, String> result = authService.merchantRegister(request);

            assertNotNull(result);
            verify(merchantMapper).insert(argThat(merchant -> 
                merchant.getName().equals("商家13900139000")
            ));
        }

        @Test
        @DisplayName("注册成功 - 邮箱为空时设置为空字符串")
        void testMerchantRegister_EmptyEmail() {
            MerchantRegisterRequest request = MerchantRegisterRequest.builder()
                    .name("New Merchant")
                    .password(testPassword)
                    .email("")
                    .phone("13900139000")
                    .build();

            when(merchantMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(passwordEncoder.encode(testPassword)).thenReturn(testEncodedPassword);
            when(merchantMapper.insert(any(Merchant.class))).thenReturn(1);

            Map<String, String> result = authService.merchantRegister(request);

            assertNotNull(result);
            verify(merchantMapper).insert(argThat(merchant -> 
                merchant.getEmail().equals("")
            ));
        }

        @Test
        @DisplayName("注册成功 - 状态默认为pending")
        void testMerchantRegister_DefaultStatus() {
            MerchantRegisterRequest request = MerchantRegisterRequest.builder()
                    .name("New Merchant")
                    .password(testPassword)
                    .phone("13900139000")
                    .build();

            when(merchantMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(passwordEncoder.encode(testPassword)).thenReturn(testEncodedPassword);
            when(merchantMapper.insert(any(Merchant.class))).thenReturn(1);

            authService.merchantRegister(request);

            verify(merchantMapper).insert(argThat(merchant -> 
                merchant.getStatus().equals("pending")
            ));
        }
    }

    @Nested
    @DisplayName("管理员登录功能测试")
    class AdminLoginTests {

        @Test
        @DisplayName("正常登录 - 登录成功")
        void testAdminLogin_Success() {
            LoginRequest request = LoginRequest.builder()
                    .username("admin")
                    .password(testPassword)
                    .build();

            when(adminMapper.selectByUsername("admin")).thenReturn(testAdmin);
            when(passwordEncoder.matches(testPassword, testEncodedPassword)).thenReturn(true);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
            when(jwtUtils.generateJwtToken(authentication)).thenReturn(testJwtToken);

            LoginResponse response = authService.adminLogin(request);

            assertNotNull(response);
            assertEquals(testJwtToken, response.getToken());
            assertNotNull(response.getUser());
            assertEquals(testAdmin.getId(), response.getUser().getId());
            assertEquals(testAdmin.getUsername(), response.getUser().getUsername());
            assertEquals("admin", response.getUser().getRole());

            verify(adminMapper).selectByUsername("admin");
            verify(passwordEncoder).matches(testPassword, testEncodedPassword);
        }

        @Test
        @DisplayName("登录失败 - 管理员不存在")
        void testAdminLogin_AdminNotFound() {
            LoginRequest request = LoginRequest.builder()
                    .username("nonexistent")
                    .password(testPassword)
                    .build();

            when(adminMapper.selectByUsername("nonexistent")).thenReturn(null);

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> authService.adminLogin(request)
            );

            assertEquals("Admin not found", exception.getMessage());
        }

        @Test
        @DisplayName("登录失败 - 密码错误")
        void testAdminLogin_InvalidPassword() {
            LoginRequest request = LoginRequest.builder()
                    .username("admin")
                    .password("wrongpassword")
                    .build();

            when(adminMapper.selectByUsername("admin")).thenReturn(testAdmin);
            when(passwordEncoder.matches("wrongpassword", testEncodedPassword)).thenReturn(false);

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> authService.adminLogin(request)
            );

            assertEquals("Invalid password", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("管理员注册功能测试")
    class AdminRegisterTests {

        @Test
        @DisplayName("正常注册 - 使用用户名注册成功")
        void testAdminRegister_Success_WithUsername() {
            AdminRegisterRequest request = AdminRegisterRequest.builder()
                    .username("newadmin")
                    .password(testPassword)
                    .build();

            when(adminMapper.selectByUsername("newadmin")).thenReturn(null);
            when(passwordEncoder.encode(testPassword)).thenReturn(testEncodedPassword);
            when(adminMapper.insert(any(Admin.class))).thenReturn(1);

            Map<String, String> result = authService.adminRegister(request);

            assertNotNull(result);
            assertTrue(result.containsKey("message"));
            assertEquals("Admin registration successful", result.get("message"));

            verify(adminMapper).selectByUsername("newadmin");
            verify(passwordEncoder).encode(testPassword);
            verify(adminMapper).insert(any(Admin.class));
        }

        @Test
        @DisplayName("正常注册 - 用户名为空时使用手机号")
        void testAdminRegister_UsePhoneAsUsername() {
            AdminRegisterRequest request = AdminRegisterRequest.builder()
                    .username(null)
                    .password(testPassword)
                    .phone("13900139000")
                    .build();

            when(adminMapper.selectByUsername("13900139000")).thenReturn(null);
            when(passwordEncoder.encode(testPassword)).thenReturn(testEncodedPassword);
            when(adminMapper.insert(any(Admin.class))).thenReturn(1);

            Map<String, String> result = authService.adminRegister(request);

            assertNotNull(result);
            verify(adminMapper).selectByUsername("13900139000");
            verify(adminMapper).insert(argThat(admin -> 
                admin.getUsername().equals("13900139000")
            ));
        }

        @Test
        @DisplayName("注册失败 - 用户名和手机号都为空")
        void testAdminRegister_NoUsernameOrPhone() {
            AdminRegisterRequest request = AdminRegisterRequest.builder()
                    .username(null)
                    .password(testPassword)
                    .phone(null)
                    .build();

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> authService.adminRegister(request)
            );

            assertEquals("Username or phone is required", exception.getMessage());
        }

        @Test
        @DisplayName("注册失败 - 用户名已存在")
        void testAdminRegister_UsernameAlreadyExists() {
            AdminRegisterRequest request = AdminRegisterRequest.builder()
                    .username("admin")
                    .password(testPassword)
                    .build();

            when(adminMapper.selectByUsername("admin")).thenReturn(testAdmin);

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> authService.adminRegister(request)
            );

            assertEquals("Username already exists", exception.getMessage());
            verify(adminMapper, never()).insert(any());
        }
    }

    @Nested
    @DisplayName("密码修改功能测试")
    class ChangePasswordTests {

        @Test
        @DisplayName("正常修改 - 密码修改成功")
        void testChangePassword_Success() {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword(testPassword)
                    .newPassword("newpassword123")
                    .build();

            try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.isAuthenticated()).thenReturn(true);
                when(authentication.getPrincipal()).thenReturn(userDetails);
                when(userDetails.getUsername()).thenReturn(testPhone);
                when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
                when(passwordEncoder.matches(testPassword, testEncodedPassword)).thenReturn(true);
                when(passwordEncoder.encode("newpassword123")).thenReturn("newEncodedPassword");
                when(userMapper.updateById(any(User.class))).thenReturn(1);

                Map<String, Object> result = authService.changePassword(request);

                assertTrue((Boolean) result.get("success"));
                assertEquals("Password changed successfully", result.get("message"));

                verify(userMapper).updateById(any(User.class));
                verify(passwordEncoder).encode("newpassword123");
            }
        }

        @Test
        @DisplayName("修改失败 - 未认证")
        void testChangePassword_NotAuthenticated() {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword(testPassword)
                    .newPassword("newpassword123")
                    .build();

            try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(null);

                BadRequestException exception = assertThrows(
                        BadRequestException.class,
                        () -> authService.changePassword(request)
                );

                assertEquals("Not authenticated", exception.getMessage());
            }
        }

        @Test
        @DisplayName("修改失败 - 原密码错误")
        void testChangePassword_OldPasswordIncorrect() {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword("wrongpassword")
                    .newPassword("newpassword123")
                    .build();

            try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.isAuthenticated()).thenReturn(true);
                when(authentication.getPrincipal()).thenReturn(userDetails);
                when(userDetails.getUsername()).thenReturn(testPhone);
                when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
                when(passwordEncoder.matches("wrongpassword", testEncodedPassword)).thenReturn(false);

                Map<String, Object> result = authService.changePassword(request);

                assertFalse((Boolean) result.get("success"));
                assertEquals("Old password is incorrect", result.get("message"));

                verify(userMapper, never()).updateById(any());
            }
        }

        @Test
        @DisplayName("修改失败 - 新密码为空")
        void testChangePassword_NewPasswordNull() {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword(testPassword)
                    .newPassword(null)
                    .build();

            try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.isAuthenticated()).thenReturn(true);
                when(authentication.getPrincipal()).thenReturn(userDetails);
                when(userDetails.getUsername()).thenReturn(testPhone);
                when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
                when(passwordEncoder.matches(testPassword, testEncodedPassword)).thenReturn(true);

                Map<String, Object> result = authService.changePassword(request);

                assertFalse((Boolean) result.get("success"));
                assertEquals("New password must be at least 6 characters", result.get("message"));
            }
        }

        @Test
        @DisplayName("修改失败 - 新密码长度不足6位")
        void testChangePassword_NewPasswordTooShort() {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword(testPassword)
                    .newPassword("12345")
                    .build();

            try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.isAuthenticated()).thenReturn(true);
                when(authentication.getPrincipal()).thenReturn(userDetails);
                when(userDetails.getUsername()).thenReturn(testPhone);
                when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
                when(passwordEncoder.matches(testPassword, testEncodedPassword)).thenReturn(true);

                Map<String, Object> result = authService.changePassword(request);

                assertFalse((Boolean) result.get("success"));
                assertEquals("New password must be at least 6 characters", result.get("message"));
            }
        }

        @Test
        @DisplayName("修改失败 - 用户不存在")
        void testChangePassword_UserNotFound() {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword(testPassword)
                    .newPassword("newpassword123")
                    .build();

            try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.isAuthenticated()).thenReturn(true);
                when(authentication.getPrincipal()).thenReturn(userDetails);
                when(userDetails.getUsername()).thenReturn(testPhone);
                when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

                ResourceNotFoundException exception = assertThrows(
                        ResourceNotFoundException.class,
                        () -> authService.changePassword(request)
                );

                assertEquals("User not found", exception.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("登出功能测试")
    class LogoutTests {

        @Test
        @DisplayName("登出成功 - 清除安全上下文")
        void testLogout_Success() {
            try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
                authService.logout();

                mockedStatic.verify(SecurityContextHolder::clearContext);
            }
        }
    }

    @Nested
    @DisplayName("获取当前用户功能测试")
    class GetCurrentUserTests {

        @Test
        @DisplayName("获取成功 - 返回当前用户信息")
        void testGetCurrentUser_Success() {
            try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.isAuthenticated()).thenReturn(true);
                when(authentication.getPrincipal()).thenReturn(userDetails);
                when(userDetails.getUsername()).thenReturn(testPhone);
                when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

                UserDTO userDTO = authService.getCurrentUser();

                assertNotNull(userDTO);
                assertEquals(testUser.getId(), userDTO.getId());
                assertEquals(testUser.getUsername(), userDTO.getUsername());
                assertEquals("user", userDTO.getRole());
            }
        }

        @Test
        @DisplayName("获取失败 - 未认证")
        void testGetCurrentUser_NotAuthenticated() {
            try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(null);

                BadRequestException exception = assertThrows(
                        BadRequestException.class,
                        () -> authService.getCurrentUser()
                );

                assertEquals("Not authenticated", exception.getMessage());
            }
        }

        @Test
        @DisplayName("获取失败 - 用户不存在")
        void testGetCurrentUser_UserNotFound() {
            try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.isAuthenticated()).thenReturn(true);
                when(authentication.getPrincipal()).thenReturn(userDetails);
                when(userDetails.getUsername()).thenReturn(testPhone);
                when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

                ResourceNotFoundException exception = assertThrows(
                        ResourceNotFoundException.class,
                        () -> authService.getCurrentUser()
                );

                assertEquals("User not found", exception.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("更新用户信息功能测试")
    class UpdateUserInfoTests {

        @Test
        @DisplayName("更新成功 - 更新用户名")
        void testUpdateUserInfo_UpdateUsername() {
            UserDTO userDTO = UserDTO.builder()
                    .username("newusername")
                    .build();

            try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.isAuthenticated()).thenReturn(true);
                when(authentication.getPrincipal()).thenReturn(userDetails);
                when(userDetails.getUsername()).thenReturn(testPhone);
                when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
                when(userMapper.updateById(any(User.class))).thenReturn(1);

                UserDTO result = authService.updateUserInfo(userDTO);

                assertNotNull(result);
                verify(userMapper).updateById(argThat(user -> 
                    user.getUsername().equals("newusername")
                ));
            }
        }

        @Test
        @DisplayName("更新成功 - 更新邮箱")
        void testUpdateUserInfo_UpdateEmail() {
            UserDTO userDTO = UserDTO.builder()
                    .email("newemail@example.com")
                    .build();

            try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.isAuthenticated()).thenReturn(true);
                when(authentication.getPrincipal()).thenReturn(userDetails);
                when(userDetails.getUsername()).thenReturn(testPhone);
                when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
                when(userMapper.updateById(any(User.class))).thenReturn(1);

                UserDTO result = authService.updateUserInfo(userDTO);

                assertNotNull(result);
                verify(userMapper).updateById(argThat(user -> 
                    user.getEmail().equals("newemail@example.com")
                ));
            }
        }

        @Test
        @DisplayName("更新失败 - 手机号已被其他用户使用")
        void testUpdateUserInfo_PhoneAlreadyInUse() {
            UserDTO userDTO = UserDTO.builder()
                    .phone("13900139000")
                    .build();

            try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(authentication);
                when(authentication.isAuthenticated()).thenReturn(true);
                when(authentication.getPrincipal()).thenReturn(userDetails);
                when(userDetails.getUsername()).thenReturn(testPhone);
                when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);
                when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

                BadRequestException exception = assertThrows(
                        BadRequestException.class,
                        () -> authService.updateUserInfo(userDTO)
                );

                assertEquals("Phone number already in use", exception.getMessage());
            }
        }

        @Test
        @DisplayName("更新失败 - 未认证")
        void testUpdateUserInfo_NotAuthenticated() {
            UserDTO userDTO = UserDTO.builder()
                    .username("newusername")
                    .build();

            try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
                mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
                when(securityContext.getAuthentication()).thenReturn(null);

                BadRequestException exception = assertThrows(
                        BadRequestException.class,
                        () -> authService.updateUserInfo(userDTO)
                );

                assertEquals("Not authenticated", exception.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("发送验证码功能测试")
    class SendVerifyCodeTests {

        @Test
        @DisplayName("发送成功 - 验证码生成成功")
        void testSendVerifyCode_Success() {
            SendVerifyCodeRequest request = new SendVerifyCodeRequest();
            request.setEmail("test@example.com");

            assertDoesNotThrow(() -> authService.sendVerifyCode(request));
        }

        @Test
        @DisplayName("发送失败 - 邮箱为空")
        void testSendVerifyCode_EmailRequired() {
            SendVerifyCodeRequest request = new SendVerifyCodeRequest();
            request.setEmail(null);

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> authService.sendVerifyCode(request)
            );

            assertEquals("Email is required", exception.getMessage());
        }

        @Test
        @DisplayName("发送失败 - 邮箱为空字符串")
        void testSendVerifyCode_EmptyEmail() {
            SendVerifyCodeRequest request = new SendVerifyCodeRequest();
            request.setEmail("");

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> authService.sendVerifyCode(request)
            );

            assertEquals("Email is required", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("重置密码功能测试")
    class ResetPasswordTests {

        @Test
        @DisplayName("重置失败 - 邮箱为空")
        void testResetPassword_EmailRequired() {
            ResetPasswordRequest request = new ResetPasswordRequest();
            request.setEmail(null);
            request.setVerifyCode("123456");
            request.setPassword("newpassword");

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> authService.resetPassword(request)
            );

            assertEquals("Email is required", exception.getMessage());
        }

        @Test
        @DisplayName("重置失败 - 验证码为空")
        void testResetPassword_VerifyCodeRequired() {
            ResetPasswordRequest request = new ResetPasswordRequest();
            request.setEmail("test@example.com");
            request.setVerifyCode(null);
            request.setPassword("newpassword");

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> authService.resetPassword(request)
            );

            assertEquals("Verification code is required", exception.getMessage());
        }

        @Test
        @DisplayName("重置失败 - 密码太短")
        void testResetPassword_PasswordTooShort() {
            ResetPasswordRequest request = new ResetPasswordRequest();
            request.setEmail("test@example.com");
            request.setVerifyCode("123456");
            request.setPassword("12345");

            BadRequestException exception = assertThrows(
                    BadRequestException.class,
                    () -> authService.resetPassword(request)
            );

            assertEquals("Password must be at least 6 characters", exception.getMessage());
        }
    }
}
