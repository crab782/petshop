package com.petshop.controller.api;

import com.petshop.test.BaseTest;
import com.petshop.test.JwtUtil;
import com.petshop.test.TestDataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AdminApiControllerTest extends BaseTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private String adminToken;
    private TestDataGenerator testDataGenerator;

    @BeforeEach
    public void setUp() {
        super.setUp();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        adminToken = JwtUtil.generateAdminToken();
        testDataGenerator = new TestDataGenerator();
    }

    // 用户管理接口测试
    @Test
    public void testGetUserList() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetUserDetail() throws Exception {
        // 生成测试用户
        var user = testDataGenerator.generateUser(1, "testuser", "test@example.com");
        
        mockMvc.perform(get("/api/admin/users/" + user.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testUpdateUserStatus() throws Exception {
        // 生成测试用户
        var user = testDataGenerator.generateUser(2, "testuser2", "test2@example.com");
        
        mockMvc.perform(put("/api/admin/users/" + user.getId() + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"ACTIVE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteUser() throws Exception {
        // 生成测试用户
        var user = testDataGenerator.generateUser(3, "testuser3", "test3@example.com");
        
        mockMvc.perform(delete("/api/admin/users/" + user.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // 商家管理接口测试
    @Test
    public void testGetMerchantList() throws Exception {
        mockMvc.perform(get("/api/admin/merchants")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetPendingMerchants() throws Exception {
        mockMvc.perform(get("/api/admin/merchants/pending")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testApproveMerchant() throws Exception {
        // 生成测试商家
        var merchant = testDataGenerator.generateMerchant(1, "Test Merchant", "merchant@example.com");
        
        mockMvc.perform(put("/api/admin/merchants/" + merchant.getId() + "/approve")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateMerchantStatus() throws Exception {
        // 生成测试商家
        var merchant = testDataGenerator.generateMerchant(2, "Test Merchant 2", "merchant2@example.com");
        
        mockMvc.perform(put("/api/admin/merchants/" + merchant.getId() + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"ACTIVE\"}"))
                .andExpect(status().isOk());
    }

    // 服务管理接口测试
    @Test
    public void testGetServiceList() throws Exception {
        mockMvc.perform(get("/api/admin/services")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testUpdateServiceStatus() throws Exception {
        // 生成测试商家
        var merchant = testDataGenerator.generateMerchant(3, "Test Merchant 3", "merchant3@example.com");
        // 生成测试服务
        var service = testDataGenerator.generateService(1, merchant);
        
        mockMvc.perform(put("/api/admin/services/" + service.getId() + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"ACTIVE\"}"))
                .andExpect(status().isOk());
    }

    // 商品管理接口测试
    @Test
    public void testGetProductList() throws Exception {
        mockMvc.perform(get("/api/admin/products")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testUpdateProductStatus() throws Exception {
        // 生成测试商家
        var merchant = testDataGenerator.generateMerchant(4, "Test Merchant 4", "merchant4@example.com");
        // 生成测试商品
        var product = testDataGenerator.generateProduct(1, merchant);
        
        mockMvc.perform(put("/api/admin/products/" + product.getId() + "/status")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"ACTIVE\"}"))
                .andExpect(status().isOk());
    }

    // 评价管理接口测试
    @Test
    public void testGetReviewList() throws Exception {
        mockMvc.perform(get("/api/admin/reviews")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetPendingReviews() throws Exception {
        mockMvc.perform(get("/api/admin/reviews/pending")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testApproveReview() throws Exception {
        // 生成测试商家
        var merchant = testDataGenerator.generateMerchant(5, "Test Merchant 5", "merchant5@example.com");
        // 生成测试评价
        var review = testDataGenerator.generateReview(1, merchant);
        
        mockMvc.perform(put("/api/admin/reviews/" + review.getId() + "/approve")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // 系统设置接口测试
    @Test
    public void testGetSystemConfig() throws Exception {
        mockMvc.perform(get("/api/admin/system/config")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testUpdateSystemConfig() throws Exception {
        mockMvc.perform(put("/api/admin/system/config")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"key\": \"site.name\", \"value\": \"Pet Shop System\"}"))
                .andExpect(status().isOk());
    }

    // 角色权限管理接口测试
    @Test
    public void testGetRoleList() throws Exception {
        mockMvc.perform(get("/api/admin/roles")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testAddRole() throws Exception {
        mockMvc.perform(post("/api/admin/roles")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Test Role\", \"description\": \"Test role description\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateRole() throws Exception {
        mockMvc.perform(put("/api/admin/roles/1")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Updated Role\", \"description\": \"Updated role description\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteRole() throws Exception {
        mockMvc.perform(delete("/api/admin/roles/1")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPermissionList() throws Exception {
        mockMvc.perform(get("/api/admin/permissions")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // 操作日志接口测试
    @Test
    public void testGetOperationLogList() throws Exception {
        mockMvc.perform(get("/api/admin/logs/operations")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "1")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testDeleteOperationLog() throws Exception {
        mockMvc.perform(delete("/api/admin/logs/operations/1")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testClearOperationLogs() throws Exception {
        mockMvc.perform(delete("/api/admin/logs/operations")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Override
    public void tearDown() {
        super.tearDown();
        if (testDataGenerator != null) {
            testDataGenerator.cleanup();
        }
    }
}