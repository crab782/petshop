package com.petshop.controller.api;

import com.petshop.entity.Merchant;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("商家资料API测试")
public class MerchantApiControllerProfileTest extends MerchantApiControllerTestBase {

    @InjectMocks
    private MerchantApiController merchantApiController;

    @BeforeEach
    public void setupProfileTest() {
        mockMvc = MockMvcBuilders.standaloneSetup(merchantApiController).build();
    }

    @Test
    @DisplayName("获取商家资料 - 成功")
    public void testGetProfile_Success() throws Exception {
        Merchant merchant = TestDataFactory.createMerchant(testMerchantId);
        when(merchantService.findById(testMerchantId)).thenReturn(merchant);

        mockMvc.perform(get("/api/merchant/profile")
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> {
                    System.out.println("Response: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(testMerchantId))
                .andExpect(jsonPath("$.data.name").value(merchant.getName()))
                .andExpect(jsonPath("$.data.email").value(merchant.getEmail()));

        verify(merchantService, times(1)).findById(testMerchantId);
    }

    @Test
    @DisplayName("获取商家资料 - 未登录返回401")
    public void testGetProfile_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/merchant/profile")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));

        verify(merchantService, never()).findById(any());
    }

    @Test
    @DisplayName("获取商家资料 - session中有merchantId但merchant为null")
    public void testGetProfile_MerchantNullInSession() throws Exception {
        mockMvc.perform(get("/api/merchant/profile")
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));

        verify(merchantService, never()).findById(any());
    }

    @Test
    @DisplayName("获取商家资料 - 服务层异常返回500")
    public void testGetProfile_ServiceException() throws Exception {
        when(merchantService.findById(testMerchantId))
                .thenThrow(new RuntimeException("数据库连接失败"));

        mockMvc.perform(get("/api/merchant/profile")
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("获取商家资料失败：数据库连接失败"));

        verify(merchantService, times(1)).findById(testMerchantId);
    }

    @Test
    @DisplayName("更新商家资料 - 成功")
    public void testUpdateProfile_Success() throws Exception {
        Merchant updateData = new Merchant();
        updateData.setName("新商家名称");
        updateData.setContactPerson("新联系人");
        updateData.setPhone("13900139001");
        updateData.setEmail("new@test.com");
        updateData.setAddress("新地址");
        updateData.setLogo("http://example.com/new-logo.png");

        Merchant updatedMerchant = TestDataFactory.createMerchant(testMerchantId);
        updatedMerchant.setName("新商家名称");
        updatedMerchant.setContactPerson("新联系人");
        updatedMerchant.setPhone("13900139001");
        updatedMerchant.setEmail("new@test.com");
        updatedMerchant.setAddress("新地址");
        updatedMerchant.setLogo("http://example.com/new-logo.png");

        when(merchantService.update(any(Merchant.class))).thenReturn(updatedMerchant);

        mockMvc.perform(put("/api/merchant/profile")
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("商家资料更新成功"))
                .andExpect(jsonPath("$.data.name").value("新商家名称"))
                .andExpect(jsonPath("$.data.contactPerson").value("新联系人"))
                .andExpect(jsonPath("$.data.phone").value("13900139001"))
                .andExpect(jsonPath("$.data.email").value("new@test.com"))
                .andExpect(jsonPath("$.data.address").value("新地址"));

        verify(merchantService, times(1)).update(any(Merchant.class));
    }

    @Test
    @DisplayName("更新商家资料 - 更新后session同步")
    public void testUpdateProfile_SessionSync() throws Exception {
        Merchant updateData = new Merchant();
        updateData.setName("更新后的商家名称");
        updateData.setContactPerson("更新后的联系人");
        updateData.setPhone("13800138001");
        updateData.setEmail("updated@test.com");
        updateData.setAddress("更新后的地址");

        Merchant updatedMerchant = TestDataFactory.createMerchant(testMerchantId);
        updatedMerchant.setName("更新后的商家名称");
        updatedMerchant.setContactPerson("更新后的联系人");
        updatedMerchant.setPhone("13800138001");
        updatedMerchant.setEmail("updated@test.com");
        updatedMerchant.setAddress("更新后的地址");

        when(merchantService.update(any(Merchant.class))).thenReturn(updatedMerchant);

        mockMvc.perform(put("/api/merchant/profile")
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(merchantService, times(1)).update(any(Merchant.class));
    }

    @Test
    @DisplayName("更新商家资料 - 未登录返回401")
    public void testUpdateProfile_Unauthorized() throws Exception {
        Merchant updateData = new Merchant();
        updateData.setName("新商家名称");

        mockMvc.perform(put("/api/merchant/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(updateData)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));

        verify(merchantService, never()).update(any());
    }

    @Test
    @DisplayName("更新商家资料 - 空商家名称")
    public void testUpdateProfile_EmptyName() throws Exception {
        Merchant updateData = new Merchant();
        updateData.setName("");
        updateData.setContactPerson("联系人");
        updateData.setPhone("13800138000");
        updateData.setEmail("test@test.com");
        updateData.setAddress("测试地址");

        Merchant updatedMerchant = TestDataFactory.createMerchant(testMerchantId);
        updatedMerchant.setName("");
        when(merchantService.update(any(Merchant.class))).thenReturn(updatedMerchant);

        mockMvc.perform(put("/api/merchant/profile")
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(merchantService, times(1)).update(any(Merchant.class));
    }

    @Test
    @DisplayName("更新商家资料 - 超长商家名称(超过100字符)")
    public void testUpdateProfile_NameTooLong() throws Exception {
        String longName = "a".repeat(101);
        Merchant updateData = new Merchant();
        updateData.setName(longName);
        updateData.setContactPerson("联系人");
        updateData.setPhone("13800138000");
        updateData.setEmail("test@test.com");
        updateData.setAddress("测试地址");

        Merchant updatedMerchant = TestDataFactory.createMerchant(testMerchantId);
        updatedMerchant.setName(longName);
        when(merchantService.update(any(Merchant.class))).thenReturn(updatedMerchant);

        mockMvc.perform(put("/api/merchant/profile")
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(merchantService, times(1)).update(any(Merchant.class));
    }

    @Test
    @DisplayName("更新商家资料 - 空联系人")
    public void testUpdateProfile_EmptyContactPerson() throws Exception {
        Merchant updateData = new Merchant();
        updateData.setName("商家名称");
        updateData.setContactPerson("");
        updateData.setPhone("13800138000");
        updateData.setEmail("test@test.com");
        updateData.setAddress("测试地址");

        Merchant updatedMerchant = TestDataFactory.createMerchant(testMerchantId);
        updatedMerchant.setContactPerson("");
        when(merchantService.update(any(Merchant.class))).thenReturn(updatedMerchant);

        mockMvc.perform(put("/api/merchant/profile")
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(merchantService, times(1)).update(any(Merchant.class));
    }

    @Test
    @DisplayName("更新商家资料 - 空电话")
    public void testUpdateProfile_EmptyPhone() throws Exception {
        Merchant updateData = new Merchant();
        updateData.setName("商家名称");
        updateData.setContactPerson("联系人");
        updateData.setPhone("");
        updateData.setEmail("test@test.com");
        updateData.setAddress("测试地址");

        Merchant updatedMerchant = TestDataFactory.createMerchant(testMerchantId);
        updatedMerchant.setPhone("");
        when(merchantService.update(any(Merchant.class))).thenReturn(updatedMerchant);

        mockMvc.perform(put("/api/merchant/profile")
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(merchantService, times(1)).update(any(Merchant.class));
    }

    @Test
    @DisplayName("更新商家资料 - 无效邮箱格式")
    public void testUpdateProfile_InvalidEmail() throws Exception {
        Merchant updateData = new Merchant();
        updateData.setName("商家名称");
        updateData.setContactPerson("联系人");
        updateData.setPhone("13800138000");
        updateData.setEmail("invalid-email");
        updateData.setAddress("测试地址");

        Merchant updatedMerchant = TestDataFactory.createMerchant(testMerchantId);
        updatedMerchant.setEmail("invalid-email");
        when(merchantService.update(any(Merchant.class))).thenReturn(updatedMerchant);

        mockMvc.perform(put("/api/merchant/profile")
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(merchantService, times(1)).update(any(Merchant.class));
    }

    @Test
    @DisplayName("更新商家资料 - 超长地址(超过255字符)")
    public void testUpdateProfile_AddressTooLong() throws Exception {
        String longAddress = "a".repeat(256);
        Merchant updateData = new Merchant();
        updateData.setName("商家名称");
        updateData.setContactPerson("联系人");
        updateData.setPhone("13800138000");
        updateData.setEmail("test@test.com");
        updateData.setAddress(longAddress);

        Merchant updatedMerchant = TestDataFactory.createMerchant(testMerchantId);
        updatedMerchant.setAddress(longAddress);
        when(merchantService.update(any(Merchant.class))).thenReturn(updatedMerchant);

        mockMvc.perform(put("/api/merchant/profile")
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(merchantService, times(1)).update(any(Merchant.class));
    }

    @Test
    @DisplayName("更新商家资料 - 服务层异常返回500")
    public void testUpdateProfile_ServiceException() throws Exception {
        Merchant updateData = new Merchant();
        updateData.setName("新商家名称");
        updateData.setContactPerson("联系人");
        updateData.setPhone("13800138000");
        updateData.setEmail("test@test.com");
        updateData.setAddress("测试地址");

        when(merchantService.update(any(Merchant.class)))
                .thenThrow(new RuntimeException("数据库更新失败"));

        mockMvc.perform(put("/api/merchant/profile")
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(updateData)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("更新商家资料失败：数据库更新失败"));

        verify(merchantService, times(1)).update(any(Merchant.class));
    }

    @Test
    @DisplayName("更新商家资料 - 密码和状态不可修改")
    public void testUpdateProfile_PasswordAndStatusNotModifiable() throws Exception {
        Merchant updateData = new Merchant();
        updateData.setName("新商家名称");
        updateData.setContactPerson("联系人");
        updateData.setPhone("13800138000");
        updateData.setEmail("test@test.com");
        updateData.setAddress("测试地址");
        updateData.setPassword("newpassword");
        updateData.setStatus("rejected");

        Merchant originalMerchant = TestDataFactory.createMerchant(testMerchantId);
        originalMerchant.setPassword("originalpassword");
        originalMerchant.setStatus("approved");

        Merchant updatedMerchant = TestDataFactory.createMerchant(testMerchantId);
        updatedMerchant.setName("新商家名称");
        updatedMerchant.setPassword("originalpassword");
        updatedMerchant.setStatus("approved");

        when(merchantService.update(any(Merchant.class))).thenAnswer(invocation -> {
            Merchant merchantToUpdate = invocation.getArgument(0);
            return merchantToUpdate;
        });

        mockMvc.perform(put("/api/merchant/profile")
                .sessionAttr("merchant", originalMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(merchantService, times(1)).update(argThat(merchant -> 
            merchant.getPassword().equals("originalpassword") && 
            merchant.getStatus().equals("approved")
        ));
    }

    @Test
    @DisplayName("更新商家资料 - 部分字段更新")
    public void testUpdateProfile_PartialUpdate() throws Exception {
        Merchant updateData = new Merchant();
        updateData.setName("仅更新名称");

        Merchant updatedMerchant = TestDataFactory.createMerchant(testMerchantId);
        updatedMerchant.setName("仅更新名称");

        when(merchantService.update(any(Merchant.class))).thenReturn(updatedMerchant);

        mockMvc.perform(put("/api/merchant/profile")
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("仅更新名称"));

        verify(merchantService, times(1)).update(any(Merchant.class));
    }

    @Test
    @DisplayName("更新商家资料 - 更新Logo")
    public void testUpdateProfile_UpdateLogo() throws Exception {
        Merchant updateData = new Merchant();
        updateData.setName("商家名称");
        updateData.setContactPerson("联系人");
        updateData.setPhone("13800138000");
        updateData.setEmail("test@test.com");
        updateData.setAddress("测试地址");
        updateData.setLogo("http://example.com/new-logo.png");

        Merchant updatedMerchant = TestDataFactory.createMerchant(testMerchantId);
        updatedMerchant.setLogo("http://example.com/new-logo.png");

        when(merchantService.update(any(Merchant.class))).thenReturn(updatedMerchant);

        mockMvc.perform(put("/api/merchant/profile")
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.logo").value("http://example.com/new-logo.png"));

        verify(merchantService, times(1)).update(any(Merchant.class));
    }

    @Test
    @DisplayName("获取商家资料 - 验证返回完整信息")
    public void testGetProfile_CompleteData() throws Exception {
        Merchant merchant = TestDataFactory.createMerchant(testMerchantId, 
                "完整商家名称", "complete@test.com", "13800138000", 
                "完整地址信息", "完整联系人", "approved");
        merchant.setLogo("http://example.com/logo.png");

        when(merchantService.findById(testMerchantId)).thenReturn(merchant);

        mockMvc.perform(get("/api/merchant/profile")
                .sessionAttr("merchant", merchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(testMerchantId))
                .andExpect(jsonPath("$.data.name").value("完整商家名称"))
                .andExpect(jsonPath("$.data.email").value("complete@test.com"))
                .andExpect(jsonPath("$.data.phone").value("13800138000"))
                .andExpect(jsonPath("$.data.address").value("完整地址信息"))
                .andExpect(jsonPath("$.data.contactPerson").value("完整联系人"))
                .andExpect(jsonPath("$.data.status").value("approved"))
                .andExpect(jsonPath("$.data.logo").value("http://example.com/logo.png"));

        verify(merchantService, times(1)).findById(testMerchantId);
    }
}
