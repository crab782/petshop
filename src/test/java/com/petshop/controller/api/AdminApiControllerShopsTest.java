package com.petshop.controller.api;

import com.petshop.entity.Merchant;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("管理员店铺审核API测试")
public class AdminApiControllerShopsTest extends AdminApiControllerTestBase {

    private AdminApiController controller;

    @BeforeEach
    public void setupController() {
        controller = new AdminApiController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "merchantService", merchantService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("GET /api/admin/shops/pending - 获取待审核店铺列表")
    class GetPendingShopsTests {

        @Test
        @DisplayName("成功获取待审核店铺列表")
        void testGetPendingShops_Success() throws Exception {
            Merchant merchant1 = TestDataFactory.createMerchant(1, "待审核店铺1", "pending1@test.com");
            merchant1.setStatus("pending");
            Merchant merchant2 = TestDataFactory.createMerchant(2, "待审核店铺2", "pending2@test.com");
            merchant2.setStatus("pending");
            List<Merchant> merchants = Arrays.asList(merchant1, merchant2);
            Page<Merchant> merchantPage = new PageImpl<>(merchants, PageRequest.of(0, 10), 2);

            when(merchantService.getPendingMerchants(any(), eq(0), eq(10))).thenReturn(merchantPage);

            var result = performGet("/api/admin/shops/pending");

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.data.length()").value(2));

            verify(merchantService).getPendingMerchants(any(), eq(0), eq(10));
        }

        @Test
        @DisplayName("成功获取空待审核店铺列表")
        void testGetPendingShops_EmptyList() throws Exception {
            Page<Merchant> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

            when(merchantService.getPendingMerchants(any(), eq(0), eq(10))).thenReturn(emptyPage);

            var result = performGet("/api/admin/shops/pending");

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.data.length()").value(0));
        }

        @Test
        @DisplayName("带关键字搜索")
        void testGetPendingShops_WithKeyword() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "搜索店铺", "search@test.com");
            merchant.setStatus("pending");
            List<Merchant> merchants = Collections.singletonList(merchant);
            Page<Merchant> merchantPage = new PageImpl<>(merchants, PageRequest.of(0, 10), 1);

            when(merchantService.getPendingMerchants(eq("搜索"), eq(0), eq(10))).thenReturn(merchantPage);

            var result = mockMvc.perform(get("/api/admin/shops/pending")
                    .sessionAttr("admin", testAdmin)
                    .param("keyword", "搜索")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(res -> {});

            assertPaginatedResponse(result);
            verify(merchantService).getPendingMerchants(eq("搜索"), eq(0), eq(10));
        }

        @Test
        @DisplayName("分页参数正确传递")
        void testGetPendingShops_Pagination() throws Exception {
            Page<Merchant> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(2, 20), 0);

            when(merchantService.getPendingMerchants(any(), eq(2), eq(20))).thenReturn(emptyPage);

            var result = mockMvc.perform(get("/api/admin/shops/pending")
                    .sessionAttr("admin", testAdmin)
                    .param("page", "2")
                    .param("pageSize", "20")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(res -> {});

            assertPaginatedResponse(result);
            verify(merchantService).getPendingMerchants(any(), eq(2), eq(20));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetPendingShops_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/shops/pending")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(merchantService, never()).getPendingMerchants(any(), anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/shops/{id}/audit - 审核店铺")
    class AuditShopTests {

        @Test
        @DisplayName("成功审核通过店铺")
        void testAuditShop_Approve() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "approved");

            Merchant approvedMerchant = TestDataFactory.createMerchant(1, "测试店铺", "test@shop.com");
            approvedMerchant.setStatus("approved");

            when(merchantService.auditMerchant(eq(1), eq("approved"), any())).thenReturn(approvedMerchant);

            var result = performPut("/api/admin/shops/{id}/audit", request, 1);

            assertSuccess(result, "Shop audit completed");
            result.andExpect(jsonPath("$.data.status").value("approved"));

            verify(merchantService).auditMerchant(eq(1), eq("approved"), any());
        }

        @Test
        @DisplayName("成功审核通过店铺带备注")
        void testAuditShop_ApproveWithNote() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "approved");
            request.put("reason", "资质齐全，审核通过");

            Merchant approvedMerchant = TestDataFactory.createMerchant(1, "测试店铺", "test@shop.com");
            approvedMerchant.setStatus("approved");

            when(merchantService.auditMerchant(eq(1), eq("approved"), eq("资质齐全，审核通过")))
                    .thenReturn(approvedMerchant);

            var result = performPut("/api/admin/shops/{id}/audit", request, 1);

            assertSuccess(result, "Shop audit completed");
            result.andExpect(jsonPath("$.data.status").value("approved"));

            verify(merchantService).auditMerchant(eq(1), eq("approved"), eq("资质齐全，审核通过"));
        }

        @Test
        @DisplayName("成功拒绝店铺")
        void testAuditShop_Reject() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "rejected");
            request.put("reason", "资质不符合要求，请补充材料");

            Merchant rejectedMerchant = TestDataFactory.createMerchant(1, "测试店铺", "test@shop.com");
            rejectedMerchant.setStatus("rejected");

            when(merchantService.auditMerchant(eq(1), eq("rejected"), eq("资质不符合要求，请补充材料")))
                    .thenReturn(rejectedMerchant);

            var result = performPut("/api/admin/shops/{id}/audit", request, 1);

            assertSuccess(result, "Shop audit completed");
            result.andExpect(jsonPath("$.data.status").value("rejected"));

            verify(merchantService).auditMerchant(eq(1), eq("rejected"), eq("资质不符合要求，请补充材料"));
        }

        @Test
        @DisplayName("无效状态返回400")
        void testAuditShop_InvalidStatus() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "invalid_status");

            var result = performPut("/api/admin/shops/{id}/audit", request, 1);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Invalid audit status. Must be 'approved' or 'rejected'"));

            verify(merchantService, never()).auditMerchant(anyInt(), anyString(), any());
        }

        @Test
        @DisplayName("空状态返回400")
        void testAuditShop_EmptyStatus() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "");

            var result = performPut("/api/admin/shops/{id}/audit", request, 1);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Invalid audit status. Must be 'approved' or 'rejected'"));
        }

        @Test
        @DisplayName("null状态返回400")
        void testAuditShop_NullStatus() throws Exception {
            Map<String, String> request = new HashMap<>();

            var result = performPut("/api/admin/shops/{id}/audit", request, 1);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Invalid audit status. Must be 'approved' or 'rejected'"));
        }

        @Test
        @DisplayName("店铺不存在返回404")
        void testAuditShop_NotFound() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "approved");

            when(merchantService.auditMerchant(eq(999), eq("approved"), any()))
                    .thenThrow(new IllegalArgumentException("Merchant not found with id: 999"));

            var result = performPut("/api/admin/shops/{id}/audit", request, 999);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("Merchant not found with id: 999"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testAuditShop_Unauthorized() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "approved");

            mockMvc.perform(put("/api/admin/shops/1/audit")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(merchantService, never()).auditMerchant(anyInt(), anyString(), any());
        }
    }
}
