package com.petshop.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.dto.*;
import com.petshop.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("认证API测试")
public class AuthApiControllerTest {

    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper;

    {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Mock
    protected AuthService authService;

    @InjectMocks
    private AuthApiController authApiController;

    protected String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @BeforeEach
    public void setupAuthTest() {
        mockMvc = MockMvcBuilders.standaloneSetup(authApiController).build();
    }

    @Nested
    @DisplayName("登录接口测试")
    class LoginTests {

        @Test
        @DisplayName("登录成功 - 使用用户名登录")
        void testLogin_Success_WithUsername() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .username("testuser")
                    .password("password123")
                    .build();

            UserDTO userDTO = UserDTO.builder()
                    .id(1)
                    .username("testuser")
                    .email("test@user.com")
                    .phone("13900139000")
                    .role("user")
                    .build();

            LoginResponse response = LoginResponse.builder()
                    .token("jwt-token-123")
                    .user(userDTO)
                    .build();

            when(authService.login(any(LoginRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Login successful"))
                    .andExpect(jsonPath("$.data.token").value("jwt-token-123"))
                    .andExpect(jsonPath("$.data.user.username").value("testuser"))
                    .andExpect(jsonPath("$.data.user.email").value("test@user.com"));

            verify(authService, times(1)).login(any(LoginRequest.class));
        }

        @Test
        @DisplayName("登录成功 - 使用邮箱登录")
        void testLogin_Success_WithEmail() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .username("test@user.com")
                    .password("password123")
                    .build();

            UserDTO userDTO = UserDTO.builder()
                    .id(1)
                    .username("testuser")
                    .email("test@user.com")
                    .role("user")
                    .build();

            LoginResponse response = LoginResponse.builder()
                    .token("jwt-token-123")
                    .user(userDTO)
                    .build();

            when(authService.login(any(LoginRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.token").value("jwt-token-123"));

            verify(authService, times(1)).login(any(LoginRequest.class));
        }

        @Test
        @DisplayName("登录成功 - 使用手机号登录")
        void testLogin_Success_WithPhone() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .phone("13900139000")
                    .password("password123")
                    .build();

            UserDTO userDTO = UserDTO.builder()
                    .id(1)
                    .username("testuser")
                    .email("test@user.com")
                    .phone("13900139000")
                    .role("user")
                    .build();

            LoginResponse response = LoginResponse.builder()
                    .token("jwt-token-123")
                    .user(userDTO)
                    .build();

            when(authService.login(any(LoginRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Login successful"))
                    .andExpect(jsonPath("$.data.token").value("jwt-token-123"))
                    .andExpect(jsonPath("$.data.user.phone").value("13900139000"));

            verify(authService, times(1)).login(any(LoginRequest.class));
        }

        @Test
        @DisplayName("登录失败 - 手机号和用户名为空")
        void testLogin_Fail_PhoneAndUsernameEmpty() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .username("")
                    .phone("")
                    .password("password123")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Phone number is required"));

            verify(authService, never()).login(any());
        }

        @Test
        @DisplayName("登录失败 - 手机号和用户名为null")
        void testLogin_Fail_PhoneAndUsernameNull() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .username(null)
                    .phone(null)
                    .password("password123")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Phone number is required"));

            verify(authService, never()).login(any());
        }

        @Test
        @DisplayName("登录失败 - 密码为空")
        void testLogin_Fail_PasswordEmpty() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .username("testuser")
                    .password("")
                    .build();

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Password is required"));

            verify(authService, never()).login(any());
        }

        @Test
        @DisplayName("登录失败 - 密码为null")
        void testLogin_Fail_PasswordNull() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .username("testuser")
                    .password(null)
                    .build();

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Password is required"));

            verify(authService, never()).login(any());
        }

        @Test
        @DisplayName("登录失败 - 用户不存在")
        void testLogin_Fail_UserNotFound() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .username("nonexistent")
                    .password("password123")
                    .build();

            when(authService.login(any(LoginRequest.class)))
                    .thenThrow(new RuntimeException("User not found"));

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("User not found"));

            verify(authService, times(1)).login(any(LoginRequest.class));
        }

        @Test
        @DisplayName("登录失败 - 密码错误")
        void testLogin_Fail_InvalidPassword() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .username("testuser")
                    .password("wrongpassword")
                    .build();

            when(authService.login(any(LoginRequest.class)))
                    .thenThrow(new RuntimeException("Invalid password"));

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("Invalid password"));

            verify(authService, times(1)).login(any(LoginRequest.class));
        }

        @Test
        @DisplayName("登录失败 - 服务异常")
        void testLogin_Fail_ServiceException() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .username("testuser")
                    .password("password123")
                    .build();

            when(authService.login(any(LoginRequest.class)))
                    .thenThrow(new Exception("Database connection failed"));

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));

            verify(authService, times(1)).login(any(LoginRequest.class));
        }
    }

    @Nested
    @DisplayName("注册接口测试")
    class RegisterTests {

        @Test
        @DisplayName("注册成功")
        void testRegister_Success() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password("password123")
                    .email("new@user.com")
                    .phone("13900139001")
                    .build();

            UserDTO userDTO = UserDTO.builder()
                    .id(2)
                    .username("newuser")
                    .email("new@user.com")
                    .phone("13900139001")
                    .role("user")
                    .build();

            LoginResponse response = LoginResponse.builder()
                    .token("jwt-token-new")
                    .user(userDTO)
                    .build();

            when(authService.register(any(RegisterRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Registration successful"))
                    .andExpect(jsonPath("$.data.token").value("jwt-token-new"))
                    .andExpect(jsonPath("$.data.user.username").value("newuser"));

            verify(authService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        @DisplayName("注册成功 - 不带手机号")
        void testRegister_Success_WithoutPhone() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password("password123")
                    .email("new@user.com")
                    .build();

            UserDTO userDTO = UserDTO.builder()
                    .id(2)
                    .username("newuser")
                    .email("new@user.com")
                    .phone("")
                    .role("user")
                    .build();

            LoginResponse response = LoginResponse.builder()
                    .token("jwt-token-new")
                    .user(userDTO)
                    .build();

            when(authService.register(any(RegisterRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(200));

            verify(authService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        @DisplayName("注册失败 - 用户名为空")
        void testRegister_Fail_UsernameEmpty() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("")
                    .password("password123")
                    .email("new@user.com")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Username is required"));

            verify(authService, never()).register(any());
        }

        @Test
        @DisplayName("注册失败 - 用户名为null")
        void testRegister_Fail_UsernameNull() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username(null)
                    .password("password123")
                    .email("new@user.com")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Username is required"));

            verify(authService, never()).register(any());
        }

        @Test
        @DisplayName("注册失败 - 密码为空")
        void testRegister_Fail_PasswordEmpty() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password("")
                    .email("new@user.com")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Password must be at least 6 characters"));

            verify(authService, never()).register(any());
        }

        @Test
        @DisplayName("注册失败 - 密码太短(少于6位)")
        void testRegister_Fail_PasswordTooShort() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password("12345")
                    .email("new@user.com")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Password must be at least 6 characters"));

            verify(authService, never()).register(any());
        }

        @Test
        @DisplayName("注册失败 - 密码为null")
        void testRegister_Fail_PasswordNull() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password(null)
                    .email("new@user.com")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Password must be at least 6 characters"));

            verify(authService, never()).register(any());
        }

        @Test
        @DisplayName("注册失败 - 邮箱为空")
        void testRegister_Fail_EmailEmpty() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password("password123")
                    .email("")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Email is required"));

            verify(authService, never()).register(any());
        }

        @Test
        @DisplayName("注册失败 - 邮箱为null")
        void testRegister_Fail_EmailNull() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password("password123")
                    .email(null)
                    .build();

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Email is required"));

            verify(authService, never()).register(any());
        }

        @Test
        @DisplayName("注册失败 - 邮箱已被使用")
        void testRegister_Fail_EmailAlreadyInUse() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password("password123")
                    .email("existing@user.com")
                    .build();

            when(authService.register(any(RegisterRequest.class)))
                    .thenThrow(new RuntimeException("Email already in use"));

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Email already in use"));

            verify(authService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        @DisplayName("注册失败 - 用户名已被使用")
        void testRegister_Fail_UsernameAlreadyInUse() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("existinguser")
                    .password("password123")
                    .email("new@user.com")
                    .build();

            when(authService.register(any(RegisterRequest.class)))
                    .thenThrow(new RuntimeException("Username already in use"));

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Username already in use"));

            verify(authService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        @DisplayName("注册失败 - 服务异常")
        void testRegister_Fail_ServiceException() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password("password123")
                    .email("new@user.com")
                    .build();

            when(authService.register(any(RegisterRequest.class)))
                    .thenThrow(new Exception("Database error"));

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));

            verify(authService, times(1)).register(any(RegisterRequest.class));
        }
    }

    @Nested
    @DisplayName("登出接口测试")
    class LogoutTests {

        @Test
        @DisplayName("登出成功")
        void testLogout_Success() throws Exception {
            doNothing().when(authService).logout();

            mockMvc.perform(post("/api/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Logout successful"))
                    .andExpect(jsonPath("$.data.success").value(true));

            verify(authService, times(1)).logout();
        }

        @Test
        @DisplayName("登出失败 - 服务异常")
        void testLogout_Fail_ServiceException() throws Exception {
            doThrow(new Exception("Session error")).when(authService).logout();

            mockMvc.perform(post("/api/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));

            verify(authService, times(1)).logout();
        }
    }

    @Nested
    @DisplayName("获取用户信息接口测试")
    class GetUserInfoTests {

        @Test
        @DisplayName("获取用户信息成功")
        void testGetUserInfo_Success() throws Exception {
            UserDTO userDTO = UserDTO.builder()
                    .id(1)
                    .username("testuser")
                    .email("test@user.com")
                    .phone("13900139000")
                    .avatar("http://example.com/avatar.png")
                    .role("user")
                    .build();

            when(authService.getCurrentUser()).thenReturn(userDTO);

            mockMvc.perform(get("/api/auth/userinfo")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.username").value("testuser"))
                    .andExpect(jsonPath("$.data.email").value("test@user.com"));

            verify(authService, times(1)).getCurrentUser();
        }

        @Test
        @DisplayName("获取用户信息失败 - 未认证")
        void testGetUserInfo_Fail_Unauthenticated() throws Exception {
            when(authService.getCurrentUser())
                    .thenThrow(new RuntimeException("Not authenticated"));

            mockMvc.perform(get("/api/auth/userinfo")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("Not authenticated"));

            verify(authService, times(1)).getCurrentUser();
        }

        @Test
        @DisplayName("获取用户信息失败 - 用户不存在")
        void testGetUserInfo_Fail_UserNotFound() throws Exception {
            when(authService.getCurrentUser())
                    .thenThrow(new RuntimeException("User not found"));

            mockMvc.perform(get("/api/auth/userinfo")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("User not found"));

            verify(authService, times(1)).getCurrentUser();
        }

        @Test
        @DisplayName("获取用户信息失败 - 服务异常")
        void testGetUserInfo_Fail_ServiceException() throws Exception {
            when(authService.getCurrentUser())
                    .thenThrow(new Exception("Database error"));

            mockMvc.perform(get("/api/auth/userinfo")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));

            verify(authService, times(1)).getCurrentUser();
        }
    }

    @Nested
    @DisplayName("更新用户信息接口测试")
    class UpdateUserInfoTests {

        @Test
        @DisplayName("更新用户信息成功 - 全部字段")
        void testUpdateUserInfo_Success_AllFields() throws Exception {
            UserDTO request = UserDTO.builder()
                    .username("newusername")
                    .email("new@user.com")
                    .phone("13900139001")
                    .avatar("http://example.com/new-avatar.png")
                    .build();

            UserDTO response = UserDTO.builder()
                    .id(1)
                    .username("newusername")
                    .email("new@user.com")
                    .phone("13900139001")
                    .avatar("http://example.com/new-avatar.png")
                    .role("user")
                    .build();

            when(authService.updateUserInfo(any(UserDTO.class))).thenReturn(response);

            mockMvc.perform(put("/api/auth/userinfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("User info updated successfully"))
                    .andExpect(jsonPath("$.data.username").value("newusername"))
                    .andExpect(jsonPath("$.data.email").value("new@user.com"));

            verify(authService, times(1)).updateUserInfo(any(UserDTO.class));
        }

        @Test
        @DisplayName("更新用户信息成功 - 仅更新用户名")
        void testUpdateUserInfo_Success_OnlyUsername() throws Exception {
            UserDTO request = UserDTO.builder()
                    .username("newusername")
                    .build();

            UserDTO response = UserDTO.builder()
                    .id(1)
                    .username("newusername")
                    .email("test@user.com")
                    .phone("13900139000")
                    .role("user")
                    .build();

            when(authService.updateUserInfo(any(UserDTO.class))).thenReturn(response);

            mockMvc.perform(put("/api/auth/userinfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.username").value("newusername"));

            verify(authService, times(1)).updateUserInfo(any(UserDTO.class));
        }

        @Test
        @DisplayName("更新用户信息成功 - 仅更新头像")
        void testUpdateUserInfo_Success_OnlyAvatar() throws Exception {
            UserDTO request = UserDTO.builder()
                    .avatar("http://example.com/new-avatar.png")
                    .build();

            UserDTO response = UserDTO.builder()
                    .id(1)
                    .username("testuser")
                    .email("test@user.com")
                    .avatar("http://example.com/new-avatar.png")
                    .role("user")
                    .build();

            when(authService.updateUserInfo(any(UserDTO.class))).thenReturn(response);

            mockMvc.perform(put("/api/auth/userinfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(authService, times(1)).updateUserInfo(any(UserDTO.class));
        }

        @Test
        @DisplayName("更新用户信息失败 - 未认证")
        void testUpdateUserInfo_Fail_Unauthenticated() throws Exception {
            UserDTO request = UserDTO.builder()
                    .username("newusername")
                    .build();

            when(authService.updateUserInfo(any(UserDTO.class)))
                    .thenThrow(new RuntimeException("Not authenticated"));

            mockMvc.perform(put("/api/auth/userinfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Not authenticated"));

            verify(authService, times(1)).updateUserInfo(any(UserDTO.class));
        }

        @Test
        @DisplayName("更新用户信息失败 - 用户名已被使用")
        void testUpdateUserInfo_Fail_UsernameAlreadyInUse() throws Exception {
            UserDTO request = UserDTO.builder()
                    .username("existinguser")
                    .build();

            when(authService.updateUserInfo(any(UserDTO.class)))
                    .thenThrow(new RuntimeException("Username already in use"));

            mockMvc.perform(put("/api/auth/userinfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Username already in use"));

            verify(authService, times(1)).updateUserInfo(any(UserDTO.class));
        }

        @Test
        @DisplayName("更新用户信息失败 - 邮箱已被使用")
        void testUpdateUserInfo_Fail_EmailAlreadyInUse() throws Exception {
            UserDTO request = UserDTO.builder()
                    .email("existing@user.com")
                    .build();

            when(authService.updateUserInfo(any(UserDTO.class)))
                    .thenThrow(new RuntimeException("Email already in use"));

            mockMvc.perform(put("/api/auth/userinfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Email already in use"));

            verify(authService, times(1)).updateUserInfo(any(UserDTO.class));
        }

        @Test
        @DisplayName("更新用户信息失败 - 服务异常")
        void testUpdateUserInfo_Fail_ServiceException() throws Exception {
            UserDTO request = UserDTO.builder()
                    .username("newusername")
                    .build();

            when(authService.updateUserInfo(any(UserDTO.class)))
                    .thenThrow(new Exception("Database error"));

            mockMvc.perform(put("/api/auth/userinfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));

            verify(authService, times(1)).updateUserInfo(any(UserDTO.class));
        }
    }

    @Nested
    @DisplayName("修改密码接口测试")
    class ChangePasswordTests {

        @Test
        @DisplayName("修改密码成功")
        void testChangePassword_Success() throws Exception {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword("oldpassword")
                    .newPassword("newpassword123")
                    .build();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Password changed successfully");

            when(authService.changePassword(any(ChangePasswordRequest.class))).thenReturn(result);

            mockMvc.perform(put("/api/auth/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Password changed successfully"))
                    .andExpect(jsonPath("$.data.success").value(true));

            verify(authService, times(1)).changePassword(any(ChangePasswordRequest.class));
        }

        @Test
        @DisplayName("修改密码失败 - 旧密码为空")
        void testChangePassword_Fail_OldPasswordEmpty() throws Exception {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword("")
                    .newPassword("newpassword123")
                    .build();

            mockMvc.perform(put("/api/auth/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Old password is required"));

            verify(authService, never()).changePassword(any());
        }

        @Test
        @DisplayName("修改密码失败 - 旧密码为null")
        void testChangePassword_Fail_OldPasswordNull() throws Exception {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword(null)
                    .newPassword("newpassword123")
                    .build();

            mockMvc.perform(put("/api/auth/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Old password is required"));

            verify(authService, never()).changePassword(any());
        }

        @Test
        @DisplayName("修改密码失败 - 新密码为空")
        void testChangePassword_Fail_NewPasswordEmpty() throws Exception {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword("oldpassword")
                    .newPassword("")
                    .build();

            mockMvc.perform(put("/api/auth/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("New password must be at least 6 characters"));

            verify(authService, never()).changePassword(any());
        }

        @Test
        @DisplayName("修改密码失败 - 新密码太短")
        void testChangePassword_Fail_NewPasswordTooShort() throws Exception {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword("oldpassword")
                    .newPassword("12345")
                    .build();

            mockMvc.perform(put("/api/auth/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("New password must be at least 6 characters"));

            verify(authService, never()).changePassword(any());
        }

        @Test
        @DisplayName("修改密码失败 - 新密码为null")
        void testChangePassword_Fail_NewPasswordNull() throws Exception {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword("oldpassword")
                    .newPassword(null)
                    .build();

            mockMvc.perform(put("/api/auth/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("New password must be at least 6 characters"));

            verify(authService, never()).changePassword(any());
        }

        @Test
        @DisplayName("修改密码失败 - 旧密码错误")
        void testChangePassword_Fail_IncorrectOldPassword() throws Exception {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword("wrongpassword")
                    .newPassword("newpassword123")
                    .build();

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "Old password is incorrect");

            when(authService.changePassword(any(ChangePasswordRequest.class))).thenReturn(result);

            mockMvc.perform(put("/api/auth/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Old password is incorrect"));

            verify(authService, times(1)).changePassword(any(ChangePasswordRequest.class));
        }

        @Test
        @DisplayName("修改密码失败 - 未认证")
        void testChangePassword_Fail_Unauthenticated() throws Exception {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword("oldpassword")
                    .newPassword("newpassword123")
                    .build();

            when(authService.changePassword(any(ChangePasswordRequest.class)))
                    .thenThrow(new RuntimeException("Not authenticated"));

            mockMvc.perform(put("/api/auth/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("Not authenticated"));

            verify(authService, times(1)).changePassword(any(ChangePasswordRequest.class));
        }

        @Test
        @DisplayName("修改密码失败 - 服务异常")
        void testChangePassword_Fail_ServiceException() throws Exception {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword("oldpassword")
                    .newPassword("newpassword123")
                    .build();

            when(authService.changePassword(any(ChangePasswordRequest.class)))
                    .thenThrow(new Exception("Database error"));

            mockMvc.perform(put("/api/auth/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));

            verify(authService, times(1)).changePassword(any(ChangePasswordRequest.class));
        }
    }

    @Nested
    @DisplayName("发送验证码接口测试")
    class SendVerifyCodeTests {

        @Test
        @DisplayName("发送验证码成功")
        void testSendVerifyCode_Success() throws Exception {
            SendVerifyCodeRequest request = SendVerifyCodeRequest.builder()
                    .email("test@user.com")
                    .build();

            doNothing().when(authService).sendVerifyCode(any(SendVerifyCodeRequest.class));

            mockMvc.perform(post("/api/auth/sendVerifyCode")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Verification code sent successfully"))
                    .andExpect(jsonPath("$.data.success").value(true));

            verify(authService, times(1)).sendVerifyCode(any(SendVerifyCodeRequest.class));
        }

        @Test
        @DisplayName("发送验证码失败 - 邮箱为空")
        void testSendVerifyCode_Fail_EmailEmpty() throws Exception {
            SendVerifyCodeRequest request = SendVerifyCodeRequest.builder()
                    .email("")
                    .build();

            mockMvc.perform(post("/api/auth/sendVerifyCode")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Email is required"));

            verify(authService, never()).sendVerifyCode(any());
        }

        @Test
        @DisplayName("发送验证码失败 - 邮箱为null")
        void testSendVerifyCode_Fail_EmailNull() throws Exception {
            SendVerifyCodeRequest request = SendVerifyCodeRequest.builder()
                    .email(null)
                    .build();

            mockMvc.perform(post("/api/auth/sendVerifyCode")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Email is required"));

            verify(authService, never()).sendVerifyCode(any());
        }

        @Test
        @DisplayName("发送验证码失败 - 邮箱不存在")
        void testSendVerifyCode_Fail_EmailNotExists() throws Exception {
            SendVerifyCodeRequest request = SendVerifyCodeRequest.builder()
                    .email("nonexistent@user.com")
                    .build();

            doThrow(new RuntimeException("Email not found"))
                    .when(authService).sendVerifyCode(any(SendVerifyCodeRequest.class));

            mockMvc.perform(post("/api/auth/sendVerifyCode")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Email not found"));

            verify(authService, times(1)).sendVerifyCode(any(SendVerifyCodeRequest.class));
        }

        @Test
        @DisplayName("发送验证码失败 - 服务异常")
        void testSendVerifyCode_Fail_ServiceException() throws Exception {
            SendVerifyCodeRequest request = SendVerifyCodeRequest.builder()
                    .email("test@user.com")
                    .build();

            doThrow(new Exception("SMTP error"))
                    .when(authService).sendVerifyCode(any(SendVerifyCodeRequest.class));

            mockMvc.perform(post("/api/auth/sendVerifyCode")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));

            verify(authService, times(1)).sendVerifyCode(any(SendVerifyCodeRequest.class));
        }
    }

    @Nested
    @DisplayName("重置密码接口测试")
    class ResetPasswordTests {

        @Test
        @DisplayName("重置密码成功")
        void testResetPassword_Success() throws Exception {
            ResetPasswordRequest request = ResetPasswordRequest.builder()
                    .email("test@user.com")
                    .verifyCode("123456")
                    .password("newpassword123")
                    .build();

            doNothing().when(authService).resetPassword(any(ResetPasswordRequest.class));

            mockMvc.perform(post("/api/auth/resetPassword")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Password reset successfully"))
                    .andExpect(jsonPath("$.data.success").value(true));

            verify(authService, times(1)).resetPassword(any(ResetPasswordRequest.class));
        }

        @Test
        @DisplayName("重置密码失败 - 邮箱为空")
        void testResetPassword_Fail_EmailEmpty() throws Exception {
            ResetPasswordRequest request = ResetPasswordRequest.builder()
                    .email("")
                    .verifyCode("123456")
                    .password("newpassword123")
                    .build();

            mockMvc.perform(post("/api/auth/resetPassword")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Email is required"));

            verify(authService, never()).resetPassword(any());
        }

        @Test
        @DisplayName("重置密码失败 - 邮箱为null")
        void testResetPassword_Fail_EmailNull() throws Exception {
            ResetPasswordRequest request = ResetPasswordRequest.builder()
                    .email(null)
                    .verifyCode("123456")
                    .password("newpassword123")
                    .build();

            mockMvc.perform(post("/api/auth/resetPassword")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Email is required"));

            verify(authService, never()).resetPassword(any());
        }

        @Test
        @DisplayName("重置密码失败 - 验证码为空")
        void testResetPassword_Fail_VerifyCodeEmpty() throws Exception {
            ResetPasswordRequest request = ResetPasswordRequest.builder()
                    .email("test@user.com")
                    .verifyCode("")
                    .password("newpassword123")
                    .build();

            mockMvc.perform(post("/api/auth/resetPassword")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Verification code is required"));

            verify(authService, never()).resetPassword(any());
        }

        @Test
        @DisplayName("重置密码失败 - 验证码为null")
        void testResetPassword_Fail_VerifyCodeNull() throws Exception {
            ResetPasswordRequest request = ResetPasswordRequest.builder()
                    .email("test@user.com")
                    .verifyCode(null)
                    .password("newpassword123")
                    .build();

            mockMvc.perform(post("/api/auth/resetPassword")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Verification code is required"));

            verify(authService, never()).resetPassword(any());
        }

        @Test
        @DisplayName("重置密码失败 - 密码为空")
        void testResetPassword_Fail_PasswordEmpty() throws Exception {
            ResetPasswordRequest request = ResetPasswordRequest.builder()
                    .email("test@user.com")
                    .verifyCode("123456")
                    .password("")
                    .build();

            mockMvc.perform(post("/api/auth/resetPassword")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Password must be at least 6 characters"));

            verify(authService, never()).resetPassword(any());
        }

        @Test
        @DisplayName("重置密码失败 - 密码太短")
        void testResetPassword_Fail_PasswordTooShort() throws Exception {
            ResetPasswordRequest request = ResetPasswordRequest.builder()
                    .email("test@user.com")
                    .verifyCode("123456")
                    .password("12345")
                    .build();

            mockMvc.perform(post("/api/auth/resetPassword")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Password must be at least 6 characters"));

            verify(authService, never()).resetPassword(any());
        }

        @Test
        @DisplayName("重置密码失败 - 密码为null")
        void testResetPassword_Fail_PasswordNull() throws Exception {
            ResetPasswordRequest request = ResetPasswordRequest.builder()
                    .email("test@user.com")
                    .verifyCode("123456")
                    .password(null)
                    .build();

            mockMvc.perform(post("/api/auth/resetPassword")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Password must be at least 6 characters"));

            verify(authService, never()).resetPassword(any());
        }

        @Test
        @DisplayName("重置密码失败 - 验证码错误")
        void testResetPassword_Fail_InvalidVerifyCode() throws Exception {
            ResetPasswordRequest request = ResetPasswordRequest.builder()
                    .email("test@user.com")
                    .verifyCode("000000")
                    .password("newpassword123")
                    .build();

            doThrow(new RuntimeException("Invalid verification code"))
                    .when(authService).resetPassword(any(ResetPasswordRequest.class));

            mockMvc.perform(post("/api/auth/resetPassword")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Invalid verification code"));

            verify(authService, times(1)).resetPassword(any(ResetPasswordRequest.class));
        }

        @Test
        @DisplayName("重置密码失败 - 验证码过期")
        void testResetPassword_Fail_VerifyCodeExpired() throws Exception {
            ResetPasswordRequest request = ResetPasswordRequest.builder()
                    .email("test@user.com")
                    .verifyCode("123456")
                    .password("newpassword123")
                    .build();

            doThrow(new RuntimeException("Verification code has expired"))
                    .when(authService).resetPassword(any(ResetPasswordRequest.class));

            mockMvc.perform(post("/api/auth/resetPassword")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Verification code has expired"));

            verify(authService, times(1)).resetPassword(any(ResetPasswordRequest.class));
        }

        @Test
        @DisplayName("重置密码失败 - 用户不存在")
        void testResetPassword_Fail_UserNotFound() throws Exception {
            ResetPasswordRequest request = ResetPasswordRequest.builder()
                    .email("nonexistent@user.com")
                    .verifyCode("123456")
                    .password("newpassword123")
                    .build();

            doThrow(new RuntimeException("User not found"))
                    .when(authService).resetPassword(any(ResetPasswordRequest.class));

            mockMvc.perform(post("/api/auth/resetPassword")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("User not found"));

            verify(authService, times(1)).resetPassword(any(ResetPasswordRequest.class));
        }

        @Test
        @DisplayName("重置密码失败 - 服务异常")
        void testResetPassword_Fail_ServiceException() throws Exception {
            ResetPasswordRequest request = ResetPasswordRequest.builder()
                    .email("test@user.com")
                    .verifyCode("123456")
                    .password("newpassword123")
                    .build();

            doThrow(new Exception("Database error"))
                    .when(authService).resetPassword(any(ResetPasswordRequest.class));

            mockMvc.perform(post("/api/auth/resetPassword")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));

            verify(authService, times(1)).resetPassword(any(ResetPasswordRequest.class));
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("登录 - 用户名正好100字符")
        void testLogin_UsernameExactly100Chars() throws Exception {
            String username = "a".repeat(100);
            LoginRequest request = LoginRequest.builder()
                    .username(username)
                    .password("password123")
                    .build();

            LoginResponse response = LoginResponse.builder()
                    .token("jwt-token")
                    .user(UserDTO.builder().id(1).username(username).build())
                    .build();

            when(authService.login(any(LoginRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(authService, times(1)).login(any(LoginRequest.class));
        }

        @Test
        @DisplayName("注册 - 密码正好6字符")
        void testRegister_PasswordExactly6Chars() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password("123456")
                    .email("new@user.com")
                    .build();

            LoginResponse response = LoginResponse.builder()
                    .token("jwt-token")
                    .user(UserDTO.builder().id(1).username("newuser").build())
                    .build();

            when(authService.register(any(RegisterRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(200));

            verify(authService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        @DisplayName("修改密码 - 新密码正好6字符")
        void testChangePassword_NewPasswordExactly6Chars() throws Exception {
            ChangePasswordRequest request = ChangePasswordRequest.builder()
                    .oldPassword("oldpassword")
                    .newPassword("123456")
                    .build();

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Password changed successfully");

            when(authService.changePassword(any(ChangePasswordRequest.class))).thenReturn(result);

            mockMvc.perform(put("/api/auth/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(authService, times(1)).changePassword(any(ChangePasswordRequest.class));
        }

        @Test
        @DisplayName("重置密码 - 密码正好6字符")
        void testResetPassword_PasswordExactly6Chars() throws Exception {
            ResetPasswordRequest request = ResetPasswordRequest.builder()
                    .email("test@user.com")
                    .verifyCode("123456")
                    .password("123456")
                    .build();

            doNothing().when(authService).resetPassword(any(ResetPasswordRequest.class));

            mockMvc.perform(post("/api/auth/resetPassword")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(authService, times(1)).resetPassword(any(ResetPasswordRequest.class));
        }

        @Test
        @DisplayName("更新用户信息 - 空请求体")
        void testUpdateUserInfo_EmptyBody() throws Exception {
            UserDTO request = UserDTO.builder().build();

            UserDTO response = UserDTO.builder()
                    .id(1)
                    .username("testuser")
                    .email("test@user.com")
                    .role("user")
                    .build();

            when(authService.updateUserInfo(any(UserDTO.class))).thenReturn(response);

            mockMvc.perform(put("/api/auth/userinfo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(authService, times(1)).updateUserInfo(any(UserDTO.class));
        }
    }
}
