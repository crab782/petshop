package com.petshop.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.dto.*;
import com.petshop.service.AuthService;
import com.petshop.test.BaseTest;
import com.petshop.test.JwtUtil;
import com.petshop.test.TestDataGenerator;
import org.junit.jupiter.api.AfterEach;
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
public class AuthApiControllerTest extends BaseTest {

    protected MockMvc mockMvc;
    protected ObjectMapper objectMapper;
    protected TestDataGenerator testDataGenerator;

    {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        testDataGenerator = new TestDataGenerator();
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
        super.setUp();
        mockMvc = MockMvcBuilders.standaloneSetup(authApiController).build();
        log("AuthApiControllerTest setup completed");
    }

    @AfterEach
    public void cleanup() {
        testDataGenerator.cleanup();
        super.tearDown();
        log("AuthApiControllerTest cleanup completed");
    }

    @Nested
    @DisplayName("登录接口测试")
    class LoginTests {

        @Test
        @DisplayName("正常登录场景 - 正确的手机号和密码")
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
                    .token(JwtUtil.generateUserToken())
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
                    .andExpect(jsonPath("$.data.token").isNotEmpty())
                    .andExpect(jsonPath("$.data.user.phone").value("13900139000"));

            verify(authService, times(1)).login(any(LoginRequest.class));
        }

        @Test
        @DisplayName("登录失败场景 - 错误的手机号或密码")
        void testLogin_Fail_InvalidCredentials() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .phone("13900139000")
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
        @DisplayName("登录参数缺失场景")
        void testLogin_Fail_MissingParameters() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .phone(null)
                    .password(null)
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
    }

    @Nested
    @DisplayName("注册接口测试")
    class RegisterTests {

        @Test
        @DisplayName("正常注册场景")
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
                    .token(JwtUtil.generateUserToken())
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
                    .andExpect(jsonPath("$.data.token").isNotEmpty())
                    .andExpect(jsonPath("$.data.user.username").value("newuser"));

            verify(authService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        @DisplayName("注册失败场景 - 手机号已存在")
        void testRegister_Fail_PhoneAlreadyInUse() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .password("password123")
                    .email("new@user.com")
                    .phone("13900139000")
                    .build();

            when(authService.register(any(RegisterRequest.class)))
                    .thenThrow(new RuntimeException("Phone number already in use"));

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Phone number already in use"));

            verify(authService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        @DisplayName("注册参数缺失场景")
        void testRegister_Fail_MissingParameters() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username(null)
                    .password(null)
                    .email(null)
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
    }

    @Nested
    @DisplayName("商家注册接口测试")
    class MerchantRegisterTests {

        @Test
        @DisplayName("正常注册场景")
        void testMerchantRegister_Success() throws Exception {
            MerchantRegisterRequest request = MerchantRegisterRequest.builder()
                    .username("newmerchant")
                    .password("password123")
                    .email("merchant@example.com")
                    .phone("13900139002")
                    .businessName("Pet Shop Test")
                    .businessAddress("Test Address")
                    .build();

            MerchantDTO merchantDTO = MerchantDTO.builder()
                    .id(1)
                    .username("newmerchant")
                    .email("merchant@example.com")
                    .phone("13900139002")
                    .businessName("Pet Shop Test")
                    .role("merchant")
                    .build();

            LoginResponse response = LoginResponse.builder()
                    .token(JwtUtil.generateMerchantToken())
                    .user(merchantDTO)
                    .build();

            when(authService.registerMerchant(any(MerchantRegisterRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/auth/merchant/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Merchant registration successful"))
                    .andExpect(jsonPath("$.data.token").isNotEmpty())
                    .andExpect(jsonPath("$.data.user.username").value("newmerchant"));

            verify(authService, times(1)).registerMerchant(any(MerchantRegisterRequest.class));
        }

        @Test
        @DisplayName("注册失败场景 - 手机号已存在")
        void testMerchantRegister_Fail_PhoneAlreadyInUse() throws Exception {
            MerchantRegisterRequest request = MerchantRegisterRequest.builder()
                    .username("newmerchant")
                    .password("password123")
                    .email("merchant@example.com")
                    .phone("13900139000")
                    .businessName("Pet Shop Test")
                    .businessAddress("Test Address")
                    .build();

            when(authService.registerMerchant(any(MerchantRegisterRequest.class)))
                    .thenThrow(new RuntimeException("Phone number already in use"));

            mockMvc.perform(post("/api/auth/merchant/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Phone number already in use"));

            verify(authService, times(1)).registerMerchant(any(MerchantRegisterRequest.class));
        }
    }

    @Nested
    @DisplayName("密码重置接口测试")
    class PasswordResetTests {

        @Test
        @DisplayName("发送验证码")
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
        @DisplayName("重置密码")
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
    }

    @Nested
    @DisplayName("用户信息更新接口测试")
    class UpdateUserInfoTests {

        @Test
        @DisplayName("更新用户信息成功")
        void testUpdateUserInfo_Success() throws Exception {
            UserDTO request = UserDTO.builder()
                    .username("updateduser")
                    .email("updated@user.com")
                    .phone("13900139003")
                    .avatar("http://example.com/avatar.png")
                    .build();

            UserDTO response = UserDTO.builder()
                    .id(1)
                    .username("updateduser")
                    .email("updated@user.com")
                    .phone("13900139003")
                    .avatar("http://example.com/avatar.png")
                    .role("user")
                    .build();

            when(authService.updateUserInfo(any(UserDTO.class))).thenReturn(response);

            mockMvc.perform(put("/api/auth/userinfo")
                    .header("Authorization", "Bearer " + JwtUtil.generateUserToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("User info updated successfully"))
                    .andExpect(jsonPath("$.data.username").value("updateduser"))
                    .andExpect(jsonPath("$.data.email").value("updated@user.com"));

            verify(authService, times(1)).updateUserInfo(any(UserDTO.class));
        }
    }
}
