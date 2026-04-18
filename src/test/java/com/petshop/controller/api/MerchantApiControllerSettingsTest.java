package com.petshop.controller.api;

import com.petshop.entity.Merchant;
import com.petshop.entity.MerchantSettings;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("商家设置API测试")
public class MerchantApiControllerSettingsTest extends MerchantApiControllerTestBase {

    @BeforeEach
    @Override
    protected void setUp() {
        MerchantApiController controller = new MerchantApiController();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .build();
        testMerchant = TestDataFactory.createMerchant(testMerchantId);
    }

    @Nested
    @DisplayName("获取店铺设置测试")
    class GetSettingsTests {

        @Test
        @DisplayName("成功获取店铺设置")
        void testGetSettings_Success() throws Exception {
            Map<String, Object> settingsOverview = new HashMap<>();
            settingsOverview.put("merchantId", testMerchantId);
            settingsOverview.put("merchantName", testMerchant.getName());
            settingsOverview.put("isOpen", true);
            settingsOverview.put("appointmentNotification", true);
            settingsOverview.put("orderNotification", true);
            settingsOverview.put("reviewNotification", true);
            settingsOverview.put("notificationType", "email");

            when(merchantSettingsService.getSettingsOverview(testMerchantId)).thenReturn(settingsOverview);

            mockMvc.perform(get("/api/merchant/settings")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.merchantId").value(testMerchantId))
                    .andExpect(jsonPath("$.data.isOpen").value(true));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetSettings_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/merchant/settings")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetSettings_ServiceError() throws Exception {
            when(merchantSettingsService.getSettingsOverview(testMerchantId))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            mockMvc.perform(get("/api/merchant/settings")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("获取店铺设置失败：数据库连接失败"));
        }
    }

    @Nested
    @DisplayName("更新店铺设置测试")
    class UpdateSettingsTests {

        @Test
        @DisplayName("成功更新店铺设置")
        void testUpdateSettings_Success() throws Exception {
            MerchantSettings settings = TestDataFactory.createMerchantSettings(testMerchant);
            settings.setIsOpen(false);
            settings.setAppointmentNotification(false);
            settings.setNotificationType("sms");

            MerchantSettings updatedSettings = TestDataFactory.createMerchantSettings(testMerchant);
            updatedSettings.setIsOpen(false);
            updatedSettings.setAppointmentNotification(false);
            updatedSettings.setNotificationType("sms");

            when(merchantSettingsService.updateSettings(eq(testMerchantId), any(MerchantSettings.class)))
                    .thenReturn(updatedSettings);

            mockMvc.perform(put("/api/merchant/settings")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(settings)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("店铺设置更新成功"))
                    .andExpect(jsonPath("$.data.isOpen").value(false))
                    .andExpect(jsonPath("$.data.notificationType").value("sms"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testUpdateSettings_Unauthorized() throws Exception {
            MerchantSettings settings = TestDataFactory.createMerchantSettings(testMerchant);

            mockMvc.perform(put("/api/merchant/settings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(settings)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testUpdateSettings_ServiceError() throws Exception {
            MerchantSettings settings = TestDataFactory.createMerchantSettings(testMerchant);

            when(merchantSettingsService.updateSettings(eq(testMerchantId), any(MerchantSettings.class)))
                    .thenThrow(new RuntimeException("更新失败"));

            mockMvc.perform(put("/api/merchant/settings")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(settings)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("更新店铺设置失败：更新失败"));
        }
    }

    @Nested
    @DisplayName("切换店铺营业状态测试")
    class ToggleShopStatusTests {

        @Test
        @DisplayName("成功切换为营业状态")
        void testToggleShopStatus_Open() throws Exception {
            when(merchantSettingsService.toggleShopStatus(testMerchantId)).thenReturn(true);

            mockMvc.perform(post("/api/merchant/settings/toggle-status")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("店铺已开启营业"))
                    .andExpect(jsonPath("$.data.isOpen").value(true));
        }

        @Test
        @DisplayName("成功切换为休息状态")
        void testToggleShopStatus_Closed() throws Exception {
            when(merchantSettingsService.toggleShopStatus(testMerchantId)).thenReturn(false);

            mockMvc.perform(post("/api/merchant/settings/toggle-status")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("店铺已关闭休息"))
                    .andExpect(jsonPath("$.data.isOpen").value(false));
        }

        @Test
        @DisplayName("未登录返回401")
        void testToggleShopStatus_Unauthorized() throws Exception {
            mockMvc.perform(post("/api/merchant/settings/toggle-status")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testToggleShopStatus_ServiceError() throws Exception {
            when(merchantSettingsService.toggleShopStatus(testMerchantId))
                    .thenThrow(new RuntimeException("切换失败"));

            mockMvc.perform(post("/api/merchant/settings/toggle-status")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("切换店铺状态失败：切换失败"));
        }
    }

    @Nested
    @DisplayName("修改密码测试")
    class ChangePasswordTests {

        @Test
        @DisplayName("成功修改密码")
        void testChangePassword_Success() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("oldPassword", "oldPassword123");
            request.put("newPassword", "newPassword123");

            when(merchantSettingsService.changePassword(testMerchantId, "oldPassword123", "newPassword123"))
                    .thenReturn(true);

            mockMvc.perform(post("/api/merchant/change-password")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("密码修改成功"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testChangePassword_Unauthorized() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("oldPassword", "oldPassword123");
            request.put("newPassword", "newPassword123");

            mockMvc.perform(post("/api/merchant/change-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));
        }

        @Test
        @DisplayName("空原密码返回400")
        void testChangePassword_EmptyOldPassword() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("oldPassword", "");
            request.put("newPassword", "newPassword123");

            mockMvc.perform(post("/api/merchant/change-password")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("原密码不能为空"));
        }

        @Test
        @DisplayName("空新密码返回400")
        void testChangePassword_EmptyNewPassword() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("oldPassword", "oldPassword123");
            request.put("newPassword", "");

            mockMvc.perform(post("/api/merchant/change-password")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("新密码不能为空"));
        }

        @Test
        @DisplayName("密码长度不足返回400")
        void testChangePassword_PasswordTooShort() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("oldPassword", "oldPassword123");
            request.put("newPassword", "12345");

            mockMvc.perform(post("/api/merchant/change-password")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("新密码长度不能少于6位"));
        }

        @Test
        @DisplayName("原密码错误返回400")
        void testChangePassword_WrongOldPassword() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("oldPassword", "wrongPassword");
            request.put("newPassword", "newPassword123");

            when(merchantSettingsService.changePassword(testMerchantId, "wrongPassword", "newPassword123"))
                    .thenReturn(false);

            mockMvc.perform(post("/api/merchant/change-password")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("原密码错误"));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testChangePassword_ServiceError() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("oldPassword", "oldPassword123");
            request.put("newPassword", "newPassword123");

            when(merchantSettingsService.changePassword(testMerchantId, "oldPassword123", "newPassword123"))
                    .thenThrow(new RuntimeException("密码修改失败"));

            mockMvc.perform(post("/api/merchant/change-password")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("修改密码失败：密码修改失败"));
        }
    }

    @Nested
    @DisplayName("绑定手机号测试")
    class BindPhoneTests {

        @Test
        @DisplayName("成功绑定手机号")
        void testBindPhone_Success() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("phone", "13800138001");
            request.put("verifyCode", "123456");

            when(merchantSettingsService.bindPhone(testMerchantId, "13800138001", "123456"))
                    .thenReturn(true);
            when(merchantService.findById(testMerchantId)).thenReturn(testMerchant);

            mockMvc.perform(post("/api/merchant/bind-phone")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("手机号绑定成功"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testBindPhone_Unauthorized() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("phone", "13800138001");
            request.put("verifyCode", "123456");

            mockMvc.perform(post("/api/merchant/bind-phone")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));
        }

        @Test
        @DisplayName("空手机号返回400")
        void testBindPhone_EmptyPhone() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("phone", "");
            request.put("verifyCode", "123456");

            mockMvc.perform(post("/api/merchant/bind-phone")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("手机号不能为空"));
        }

        @Test
        @DisplayName("无效手机号格式返回400")
        void testBindPhone_InvalidPhoneFormat() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("phone", "1234567890");
            request.put("verifyCode", "123456");

            mockMvc.perform(post("/api/merchant/bind-phone")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("手机号格式不正确"));
        }

        @Test
        @DisplayName("空验证码返回400")
        void testBindPhone_EmptyVerifyCode() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("phone", "13800138001");
            request.put("verifyCode", "");

            mockMvc.perform(post("/api/merchant/bind-phone")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("验证码不能为空"));
        }

        @Test
        @DisplayName("绑定失败返回400")
        void testBindPhone_BindFailed() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("phone", "13800138001");
            request.put("verifyCode", "123456");

            when(merchantSettingsService.bindPhone(testMerchantId, "13800138001", "123456"))
                    .thenReturn(false);

            mockMvc.perform(post("/api/merchant/bind-phone")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("绑定失败"));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testBindPhone_ServiceError() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("phone", "13800138001");
            request.put("verifyCode", "123456");

            when(merchantSettingsService.bindPhone(testMerchantId, "13800138001", "123456"))
                    .thenThrow(new RuntimeException("绑定手机号失败"));

            mockMvc.perform(post("/api/merchant/bind-phone")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("绑定手机号失败：绑定手机号失败"));
        }
    }

    @Nested
    @DisplayName("绑定邮箱测试")
    class BindEmailTests {

        @Test
        @DisplayName("成功绑定邮箱")
        void testBindEmail_Success() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("email", "newemail@test.com");
            request.put("verifyCode", "123456");

            when(merchantSettingsService.bindEmail(testMerchantId, "newemail@test.com", "123456"))
                    .thenReturn(true);
            when(merchantService.findById(testMerchantId)).thenReturn(testMerchant);

            mockMvc.perform(post("/api/merchant/bind-email")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("邮箱绑定成功"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testBindEmail_Unauthorized() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("email", "newemail@test.com");
            request.put("verifyCode", "123456");

            mockMvc.perform(post("/api/merchant/bind-email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));
        }

        @Test
        @DisplayName("空邮箱返回400")
        void testBindEmail_EmptyEmail() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("email", "");
            request.put("verifyCode", "123456");

            mockMvc.perform(post("/api/merchant/bind-email")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("邮箱不能为空"));
        }

        @Test
        @DisplayName("无效邮箱格式返回400")
        void testBindEmail_InvalidEmailFormat() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("email", "invalid-email");
            request.put("verifyCode", "123456");

            mockMvc.perform(post("/api/merchant/bind-email")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("邮箱格式不正确"));
        }

        @Test
        @DisplayName("空验证码返回400")
        void testBindEmail_EmptyVerifyCode() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("email", "newemail@test.com");
            request.put("verifyCode", "");

            mockMvc.perform(post("/api/merchant/bind-email")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("验证码不能为空"));
        }

        @Test
        @DisplayName("邮箱已被使用返回400")
        void testBindEmail_EmailAlreadyUsed() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("email", "used@test.com");
            request.put("verifyCode", "123456");

            when(merchantSettingsService.bindEmail(testMerchantId, "used@test.com", "123456"))
                    .thenReturn(false);

            mockMvc.perform(post("/api/merchant/bind-email")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("该邮箱已被其他商家使用"));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testBindEmail_ServiceError() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("email", "newemail@test.com");
            request.put("verifyCode", "123456");

            when(merchantSettingsService.bindEmail(testMerchantId, "newemail@test.com", "123456"))
                    .thenThrow(new RuntimeException("绑定邮箱失败"));

            mockMvc.perform(post("/api/merchant/bind-email")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("绑定邮箱失败：绑定邮箱失败"));
        }
    }

    @Nested
    @DisplayName("发送验证码测试")
    class SendVerifyCodeTests {

        @Test
        @DisplayName("成功发送手机验证码")
        void testSendVerifyCode_Phone_Success() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("type", "phone");
            request.put("target", "13800138001");

            when(merchantSettingsService.generateVerifyCode()).thenReturn("123456");

            mockMvc.perform(post("/api/merchant/send-verify-code")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("验证码发送成功"))
                    .andExpect(jsonPath("$.data.code").value("123456"))
                    .andExpect(jsonPath("$.data.type").value("phone"));
        }

        @Test
        @DisplayName("成功发送邮箱验证码")
        void testSendVerifyCode_Email_Success() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("type", "email");
            request.put("target", "test@example.com");

            when(merchantSettingsService.generateVerifyCode()).thenReturn("654321");

            mockMvc.perform(post("/api/merchant/send-verify-code")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("验证码发送成功"))
                    .andExpect(jsonPath("$.data.code").value("654321"))
                    .andExpect(jsonPath("$.data.type").value("email"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testSendVerifyCode_Unauthorized() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("type", "phone");
            request.put("target", "13800138001");

            mockMvc.perform(post("/api/merchant/send-verify-code")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));
        }

        @Test
        @DisplayName("无效验证码类型返回400")
        void testSendVerifyCode_InvalidType() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("type", "invalid");
            request.put("target", "13800138001");

            mockMvc.perform(post("/api/merchant/send-verify-code")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("验证码类型无效"));
        }

        @Test
        @DisplayName("空验证码类型返回400")
        void testSendVerifyCode_EmptyType() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("type", "");
            request.put("target", "13800138001");

            mockMvc.perform(post("/api/merchant/send-verify-code")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("验证码类型无效"));
        }

        @Test
        @DisplayName("空目标地址返回400")
        void testSendVerifyCode_EmptyTarget() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("type", "phone");
            request.put("target", "");

            mockMvc.perform(post("/api/merchant/send-verify-code")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("目标地址不能为空"));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testSendVerifyCode_ServiceError() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("type", "phone");
            request.put("target", "13800138001");

            when(merchantSettingsService.generateVerifyCode())
                    .thenThrow(new RuntimeException("验证码生成失败"));

            mockMvc.perform(post("/api/merchant/send-verify-code")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("发送验证码失败：验证码生成失败"));
        }
    }
}
