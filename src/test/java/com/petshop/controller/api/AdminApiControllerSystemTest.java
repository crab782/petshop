package com.petshop.controller.api;

import com.petshop.entity.SystemConfig;
import com.petshop.entity.SystemSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("管理员系统设置API测试")
public class AdminApiControllerSystemTest extends AdminApiControllerTestBase {

    private AdminApiController controller;

    @BeforeEach
    public void setupController() {
        controller = new AdminApiController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "systemConfigRepository", systemConfigRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "systemSettingsRepository", systemSettingsRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("GET /api/admin/system/config - 获取系统配置")
    class GetSystemConfigTests {

        @Test
        @DisplayName("成功获取系统配置")
        void testGetSystemConfig_Success() throws Exception {
            SystemConfig config = new SystemConfig();
            config.setId(1);
            config.setSiteName("宠物服务平台");
            config.setLogo("http://example.com/logo.png");
            config.setContactEmail("contact@example.com");
            config.setContactPhone("400-123-4567");
            config.setIcpNumber("京ICP备12345678号");
            config.setSiteDescription("专业的宠物服务平台");
            config.setSiteKeywords("宠物,服务,美容");
            config.setFooterText("版权所有");

            when(systemConfigRepository.findById(1)).thenReturn(Optional.of(config));

            var result = performGet("/api/admin/system/config");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.siteName").value("宠物服务平台"))
                    .andExpect(jsonPath("$.data.contactEmail").value("contact@example.com"))
                    .andExpect(jsonPath("$.data.icpNumber").value("京ICP备12345678号"));

            verify(systemConfigRepository).findById(1);
        }

        @Test
        @DisplayName("配置不存在时创建默认配置")
        void testGetSystemConfig_CreateDefault() throws Exception {
            SystemConfig defaultConfig = new SystemConfig();
            defaultConfig.setId(1);
            defaultConfig.setSiteName("宠物服务平台");

            when(systemConfigRepository.findById(1)).thenReturn(Optional.empty());
            when(systemConfigRepository.save(any(SystemConfig.class))).thenReturn(defaultConfig);

            var result = performGet("/api/admin/system/config");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.siteName").value("宠物服务平台"));

            verify(systemConfigRepository).findById(1);
            verify(systemConfigRepository).save(any(SystemConfig.class));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetSystemConfig_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/system/config")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(systemConfigRepository, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/system/config - 更新系统配置")
    class UpdateSystemConfigTests {

        @Test
        @DisplayName("成功更新系统配置")
        void testUpdateSystemConfig_Success() throws Exception {
            SystemConfig existingConfig = new SystemConfig();
            existingConfig.setId(1);
            existingConfig.setSiteName("旧名称");

            SystemConfig updatedConfig = new SystemConfig();
            updatedConfig.setId(1);
            updatedConfig.setSiteName("新平台名称");
            updatedConfig.setLogo("http://example.com/new-logo.png");
            updatedConfig.setContactEmail("new@example.com");
            updatedConfig.setContactPhone("400-999-8888");
            updatedConfig.setIcpNumber("京ICP备87654321号");
            updatedConfig.setSiteDescription("新的平台描述");
            updatedConfig.setSiteKeywords("新关键词");
            updatedConfig.setFooterText("新版权信息");

            Map<String, Object> request = new HashMap<>();
            request.put("siteName", "新平台名称");
            request.put("logo", "http://example.com/new-logo.png");
            request.put("contactEmail", "new@example.com");
            request.put("contactPhone", "400-999-8888");
            request.put("icpNumber", "京ICP备87654321号");
            request.put("siteDescription", "新的平台描述");
            request.put("siteKeywords", "新关键词");
            request.put("footerText", "新版权信息");

            when(systemConfigRepository.findById(1)).thenReturn(Optional.of(existingConfig));
            when(systemConfigRepository.save(any(SystemConfig.class))).thenReturn(updatedConfig);

            var result = performPut("/api/admin/system/config", request);

            assertSuccess(result, "System config updated successfully");
            result.andExpect(jsonPath("$.data.siteName").value("新平台名称"))
                    .andExpect(jsonPath("$.data.contactEmail").value("new@example.com"));

            verify(systemConfigRepository).findById(1);
            verify(systemConfigRepository).save(any(SystemConfig.class));
        }

        @Test
        @DisplayName("配置不存在时创建新配置")
        void testUpdateSystemConfig_CreateNew() throws Exception {
            SystemConfig newConfig = new SystemConfig();
            newConfig.setId(1);
            newConfig.setSiteName("新平台");

            Map<String, Object> request = new HashMap<>();
            request.put("siteName", "新平台");

            when(systemConfigRepository.findById(1)).thenReturn(Optional.empty());
            when(systemConfigRepository.save(any(SystemConfig.class))).thenReturn(newConfig);

            var result = performPut("/api/admin/system/config", request);

            assertSuccess(result, "System config updated successfully");
            result.andExpect(jsonPath("$.data.siteName").value("新平台"));

            verify(systemConfigRepository).findById(1);
            verify(systemConfigRepository).save(any(SystemConfig.class));
        }

        @Test
        @DisplayName("部分更新配置")
        void testUpdateSystemConfig_PartialUpdate() throws Exception {
            SystemConfig existingConfig = new SystemConfig();
            existingConfig.setId(1);
            existingConfig.setSiteName("旧名称");
            existingConfig.setContactEmail("old@example.com");

            SystemConfig updatedConfig = new SystemConfig();
            updatedConfig.setId(1);
            updatedConfig.setSiteName("新名称");
            updatedConfig.setContactEmail("old@example.com");

            Map<String, Object> request = new HashMap<>();
            request.put("siteName", "新名称");

            when(systemConfigRepository.findById(1)).thenReturn(Optional.of(existingConfig));
            when(systemConfigRepository.save(any(SystemConfig.class))).thenReturn(updatedConfig);

            var result = performPut("/api/admin/system/config", request);

            assertSuccess(result, "System config updated successfully");
            result.andExpect(jsonPath("$.data.siteName").value("新名称"));

            verify(systemConfigRepository).save(any(SystemConfig.class));
        }

        @Test
        @DisplayName("未登录返回401")
        void testUpdateSystemConfig_Unauthorized() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("siteName", "新名称");

            mockMvc.perform(put("/api/admin/system/config")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(systemConfigRepository, never()).save(any(SystemConfig.class));
        }
    }

    @Nested
    @DisplayName("GET /api/admin/system/settings - 获取系统设置")
    class GetSystemSettingsTests {

        @Test
        @DisplayName("成功获取系统设置")
        void testGetSystemSettings_Success() throws Exception {
            SystemSettings settings = new SystemSettings();
            settings.setId(1);
            settings.setEmailSmtp("smtp.example.com");
            settings.setEmailPort(587);
            settings.setEmailUsername("user@example.com");
            settings.setEmailPassword("password");
            settings.setEmailFrom("noreply@example.com");
            settings.setSmsProvider("aliyun");
            settings.setSmsApiKey("api-key");
            settings.setSmsApiSecret("api-secret");
            settings.setSmsSignName("宠物平台");
            settings.setWechatAppId("wx-app-id");
            settings.setWechatAppSecret("wx-secret");
            settings.setWechatMchId("mch-id");
            settings.setWechatPayKey("pay-key");
            settings.setAlipayAppId("alipay-app-id");
            settings.setAlipayPrivateKey("private-key");
            settings.setAlipayPublicKey("public-key");
            settings.setAlipayNotifyUrl("http://example.com/notify");
            settings.setUploadPath("/uploads");
            settings.setMaxFileSize(10485760L);
            settings.setAllowedFileTypes("jpg,jpeg,png,gif,pdf");

            when(systemSettingsRepository.findById(1)).thenReturn(Optional.of(settings));

            var result = performGet("/api/admin/system/settings");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.emailSettings.smtp").value("smtp.example.com"))
                    .andExpect(jsonPath("$.data.smsSettings.provider").value("aliyun"))
                    .andExpect(jsonPath("$.data.uploadSettings.maxFileSize").value(10485760));

            verify(systemSettingsRepository).findById(1);
        }

        @Test
        @DisplayName("设置不存在时创建默认设置")
        void testGetSystemSettings_CreateDefault() throws Exception {
            SystemSettings defaultSettings = new SystemSettings();
            defaultSettings.setId(1);
            defaultSettings.setMaxFileSize(10485760L);
            defaultSettings.setAllowedFileTypes("jpg,jpeg,png,gif,pdf");

            when(systemSettingsRepository.findById(1)).thenReturn(Optional.empty());
            when(systemSettingsRepository.save(any(SystemSettings.class))).thenReturn(defaultSettings);

            var result = performGet("/api/admin/system/settings");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.uploadSettings.maxFileSize").value(10485760));

            verify(systemSettingsRepository).findById(1);
            verify(systemSettingsRepository).save(any(SystemSettings.class));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetSystemSettings_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/system/settings")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(systemSettingsRepository, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/system/settings - 更新系统设置")
    class UpdateSystemSettingsTests {

        @Test
        @DisplayName("成功更新邮件设置")
        void testUpdateSystemSettings_EmailSettings() throws Exception {
            SystemSettings existingSettings = new SystemSettings();
            existingSettings.setId(1);

            SystemSettings updatedSettings = new SystemSettings();
            updatedSettings.setId(1);
            updatedSettings.setEmailSmtp("new.smtp.com");
            updatedSettings.setEmailPort(465);
            updatedSettings.setEmailUsername("newuser@example.com");
            updatedSettings.setEmailPassword("newpassword");
            updatedSettings.setEmailFrom("newnoreply@example.com");

            Map<String, Object> emailSettings = new HashMap<>();
            emailSettings.put("smtp", "new.smtp.com");
            emailSettings.put("port", 465);
            emailSettings.put("username", "newuser@example.com");
            emailSettings.put("password", "newpassword");
            emailSettings.put("from", "newnoreply@example.com");

            Map<String, Object> request = new HashMap<>();
            request.put("emailSettings", emailSettings);

            when(systemSettingsRepository.findById(1)).thenReturn(Optional.of(existingSettings));
            when(systemSettingsRepository.save(any(SystemSettings.class))).thenReturn(updatedSettings);

            var result = performPut("/api/admin/system/settings", request);

            assertSuccess(result, "System settings updated successfully");
            result.andExpect(jsonPath("$.data.emailSettings.smtp").value("new.smtp.com"));

            verify(systemSettingsRepository).save(any(SystemSettings.class));
        }

        @Test
        @DisplayName("成功更新短信设置")
        void testUpdateSystemSettings_SmsSettings() throws Exception {
            SystemSettings existingSettings = new SystemSettings();
            existingSettings.setId(1);

            SystemSettings updatedSettings = new SystemSettings();
            updatedSettings.setId(1);
            updatedSettings.setSmsProvider("tencent");
            updatedSettings.setSmsApiKey("new-api-key");
            updatedSettings.setSmsApiSecret("new-api-secret");
            updatedSettings.setSmsSignName("新签名");

            Map<String, Object> smsSettings = new HashMap<>();
            smsSettings.put("provider", "tencent");
            smsSettings.put("apiKey", "new-api-key");
            smsSettings.put("apiSecret", "new-api-secret");
            smsSettings.put("signName", "新签名");

            Map<String, Object> request = new HashMap<>();
            request.put("smsSettings", smsSettings);

            when(systemSettingsRepository.findById(1)).thenReturn(Optional.of(existingSettings));
            when(systemSettingsRepository.save(any(SystemSettings.class))).thenReturn(updatedSettings);

            var result = performPut("/api/admin/system/settings", request);

            assertSuccess(result, "System settings updated successfully");
            result.andExpect(jsonPath("$.data.smsSettings.provider").value("tencent"));
        }

        @Test
        @DisplayName("成功更新上传设置")
        void testUpdateSystemSettings_UploadSettings() throws Exception {
            SystemSettings existingSettings = new SystemSettings();
            existingSettings.setId(1);

            SystemSettings updatedSettings = new SystemSettings();
            updatedSettings.setId(1);
            updatedSettings.setUploadPath("/new-uploads");
            updatedSettings.setMaxFileSize(20971520L);
            updatedSettings.setAllowedFileTypes("jpg,png,pdf,doc");

            Map<String, Object> uploadSettings = new HashMap<>();
            uploadSettings.put("path", "/new-uploads");
            uploadSettings.put("maxFileSize", 20971520);
            uploadSettings.put("allowedFileTypes", "jpg,png,pdf,doc");

            Map<String, Object> request = new HashMap<>();
            request.put("uploadSettings", uploadSettings);

            when(systemSettingsRepository.findById(1)).thenReturn(Optional.of(existingSettings));
            when(systemSettingsRepository.save(any(SystemSettings.class))).thenReturn(updatedSettings);

            var result = performPut("/api/admin/system/settings", request);

            assertSuccess(result, "System settings updated successfully");
            result.andExpect(jsonPath("$.data.uploadSettings.maxFileSize").value(20971520));
        }

        @Test
        @DisplayName("设置不存在时创建新设置")
        void testUpdateSystemSettings_CreateNew() throws Exception {
            SystemSettings newSettings = new SystemSettings();
            newSettings.setId(1);
            newSettings.setEmailSmtp("smtp.example.com");

            Map<String, Object> emailSettings = new HashMap<>();
            emailSettings.put("smtp", "smtp.example.com");

            Map<String, Object> request = new HashMap<>();
            request.put("emailSettings", emailSettings);

            when(systemSettingsRepository.findById(1)).thenReturn(Optional.empty());
            when(systemSettingsRepository.save(any(SystemSettings.class))).thenReturn(newSettings);

            var result = performPut("/api/admin/system/settings", request);

            assertSuccess(result, "System settings updated successfully");

            verify(systemSettingsRepository).save(any(SystemSettings.class));
        }

        @Test
        @DisplayName("未登录返回401")
        void testUpdateSystemSettings_Unauthorized() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("emailSettings", new HashMap<>());

            mockMvc.perform(put("/api/admin/system/settings")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(systemSettingsRepository, never()).save(any(SystemSettings.class));
        }
    }
}
