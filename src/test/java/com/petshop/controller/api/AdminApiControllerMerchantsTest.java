package com.petshop.controller.api;

import com.petshop.dto.MerchantDetailDTO;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("管理员商家管理API测试")
public class AdminApiControllerMerchantsTest extends AdminApiControllerTestBase {

    private AdminApiController controller;

    @BeforeEach
    public void setupController() {
        controller = new AdminApiController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "merchantService", merchantService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("GET /api/admin/merchants - 获取商家列表")
    class GetMerchantsTests {

        @Test
        @DisplayName("成功获取商家列表")
        void testGetMerchants_Success() throws Exception {
            Merchant merchant1 = TestDataFactory.createMerchant(1, "商家1", "merchant1@test.com");
            Merchant merchant2 = TestDataFactory.createMerchant(2, "商家2", "merchant2@test.com");
            List<Merchant> merchants = Arrays.asList(merchant1, merchant2);

            when(merchantService.findAll()).thenReturn(merchants);

            var result = performGet("/api/admin/merchants");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].name").value("商家1"))
                    .andExpect(jsonPath("$[1].name").value("商家2"));

            verify(merchantService).findAll();
        }

        @Test
        @DisplayName("成功获取空商家列表")
        void testGetMerchants_EmptyList() throws Exception {
            when(merchantService.findAll()).thenReturn(Collections.emptyList());

            var result = performGet("/api/admin/merchants");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(merchantService).findAll();
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetMerchants_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/merchants")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(merchantService, never()).findAll();
        }
    }

    @Nested
    @DisplayName("GET /api/admin/merchants/{id} - 获取商家详情")
    class GetMerchantDetailTests {

        @Test
        @DisplayName("成功获取商家详情")
        void testGetMerchantDetail_Success() throws Exception {
            MerchantDetailDTO detailDTO = MerchantDetailDTO.builder()
                    .id(1)
                    .name("测试商家")
                    .email("test@merchant.com")
                    .phone("13800138000")
                    .address("测试地址")
                    .status("approved")
                    .serviceCount(10L)
                    .productCount(20L)
                    .appointmentCount(100L)
                    .reviewCount(50L)
                    .averageRating(4.5)
                    .build();

            when(merchantService.getMerchantDetail(1)).thenReturn(detailDTO);

            var result = performGet("/api/admin/merchants/{id}", 1);

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.name").value("测试商家"))
                    .andExpect(jsonPath("$.data.serviceCount").value(10))
                    .andExpect(jsonPath("$.data.averageRating").value(4.5));

            verify(merchantService).getMerchantDetail(1);
        }

        @Test
        @DisplayName("商家不存在返回404")
        void testGetMerchantDetail_NotFound() throws Exception {
            when(merchantService.getMerchantDetail(999)).thenReturn(null);

            var result = performGet("/api/admin/merchants/{id}", 999);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("Merchant not found"));

            verify(merchantService).getMerchantDetail(999);
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetMerchantDetail_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/merchants/1")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(merchantService, never()).getMerchantDetail(anyInt());
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/merchants/{id}/status - 更新商家状态")
    class UpdateMerchantStatusTests {

        @Test
        @DisplayName("成功更新商家状态")
        void testUpdateMerchantStatus_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "测试商家", "test@merchant.com");
            merchant.setStatus("pending");

            Merchant updatedMerchant = TestDataFactory.createMerchant(1, "测试商家", "test@merchant.com");
            updatedMerchant.setStatus("approved");

            when(merchantService.findById(1)).thenReturn(merchant);
            when(merchantService.update(any(Merchant.class))).thenReturn(updatedMerchant);

            var result = performPutWithParam("/api/admin/merchants/{id}/status", "status", "approved", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("approved"));

            verify(merchantService).findById(1);
            verify(merchantService).update(any(Merchant.class));
        }

        @Test
        @DisplayName("商家不存在返回404")
        void testUpdateMerchantStatus_NotFound() throws Exception {
            when(merchantService.findById(999)).thenReturn(null);

            var result = performPutWithParam("/api/admin/merchants/{id}/status", "status", "approved", 999);

            assertNotFound(result);

            verify(merchantService).findById(999);
            verify(merchantService, never()).update(any(Merchant.class));
        }

        @Test
        @DisplayName("未登录返回401")
        void testUpdateMerchantStatus_Unauthorized() throws Exception {
            mockMvc.perform(put("/api/admin/merchants/1/status")
                    .param("status", "approved")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(merchantService, never()).findById(anyInt());
        }
    }

    @Nested
    @DisplayName("DELETE /api/admin/merchants/{id} - 删除商家")
    class DeleteMerchantTests {

        @Test
        @DisplayName("成功删除商家")
        void testDeleteMerchant_Success() throws Exception {
            doNothing().when(merchantService).delete(1);

            var result = performDelete("/api/admin/merchants/{id}", 1);

            assertNoContent(result);

            verify(merchantService).delete(1);
        }

        @Test
        @DisplayName("未登录返回401")
        void testDeleteMerchant_Unauthorized() throws Exception {
            mockMvc.perform(delete("/api/admin/merchants/1")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(merchantService, never()).delete(anyInt());
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/merchants/batch/status - 批量更新商家状态")
    class BatchUpdateMerchantStatusTests {

        @Test
        @DisplayName("成功批量更新商家状态")
        void testBatchUpdateMerchantStatus_Success() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));
            request.put("status", "approved");

            when(merchantService.batchUpdateStatus(anyList(), eq("approved"))).thenReturn(3);

            var result = performPut("/api/admin/merchants/batch/status", request);

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.updatedCount").value(3));

            verify(merchantService).batchUpdateStatus(anyList(), eq("approved"));
        }

        @Test
        @DisplayName("空ID列表返回400")
        void testBatchUpdateMerchantStatus_EmptyIds() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Collections.emptyList());
            request.put("status", "approved");

            var result = performPut("/api/admin/merchants/batch/status", request);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Merchant IDs cannot be empty"));

            verify(merchantService, never()).batchUpdateStatus(anyList(), anyString());
        }

        @Test
        @DisplayName("null ID列表返回400")
        void testBatchUpdateMerchantStatus_NullIds() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", null);
            request.put("status", "approved");

            var result = performPut("/api/admin/merchants/batch/status", request);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Merchant IDs cannot be empty"));
        }

        @Test
        @DisplayName("空状态值返回400")
        void testBatchUpdateMerchantStatus_EmptyStatus() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));
            request.put("status", "");

            var result = performPut("/api/admin/merchants/batch/status", request);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Status cannot be empty"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testBatchUpdateMerchantStatus_Unauthorized() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));
            request.put("status", "approved");

            mockMvc.perform(put("/api/admin/merchants/batch/status")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(merchantService, never()).batchUpdateStatus(anyList(), anyString());
        }
    }

    @Nested
    @DisplayName("DELETE /api/admin/merchants/batch - 批量删除商家")
    class BatchDeleteMerchantsTests {

        @Test
        @DisplayName("成功批量删除商家")
        void testBatchDeleteMerchants_Success() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));

            when(merchantService.batchDelete(anyList())).thenReturn(3);

            var result = performDelete("/api/admin/merchants/batch")
                    .andDo(r -> mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .delete("/api/admin/merchants/batch")
                            .sessionAttr("admin", testAdmin)
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                            .content(toJson(request)))
                            .andDo(res -> {}));

            Map<String, Object> request2 = new HashMap<>();
            request2.put("ids", Arrays.asList(1, 2, 3));

            var result2 = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .delete("/api/admin/merchants/batch")
                    .sessionAttr("admin", testAdmin)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request2)))
                    .andDo(res -> {});

            result2.andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.deletedCount").value(3));
        }

        @Test
        @DisplayName("空ID列表返回400")
        void testBatchDeleteMerchants_EmptyIds() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Collections.emptyList());

            var result = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .delete("/api/admin/merchants/batch")
                    .sessionAttr("admin", testAdmin)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(res -> {});

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Merchant IDs cannot be empty"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testBatchDeleteMerchants_Unauthorized() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));

            mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .delete("/api/admin/merchants/batch")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(res -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }
    }

    @Nested
    @DisplayName("GET /api/admin/merchants/pending - 获取待审核商家列表")
    class GetPendingMerchantsListTests {

        @Test
        @DisplayName("成功获取待审核商家列表")
        void testGetPendingMerchantsList_Success() throws Exception {
            Merchant merchant1 = TestDataFactory.createMerchant(1, "待审核商家1", "pending1@test.com");
            merchant1.setStatus("pending");
            Merchant merchant2 = TestDataFactory.createMerchant(2, "待审核商家2", "pending2@test.com");
            merchant2.setStatus("pending");
            List<Merchant> merchants = Arrays.asList(merchant1, merchant2);
            Page<Merchant> merchantPage = new PageImpl<>(merchants, PageRequest.of(0, 10), 2);

            when(merchantService.getPendingMerchants(any(), eq(0), eq(10))).thenReturn(merchantPage);

            var result = performGet("/api/admin/merchants/pending");

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.data.length()").value(2));

            verify(merchantService).getPendingMerchants(any(), eq(0), eq(10));
        }

        @Test
        @DisplayName("带关键字搜索")
        void testGetPendingMerchantsList_WithKeyword() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "待审核商家", "pending@test.com");
            merchant.setStatus("pending");
            List<Merchant> merchants = Collections.singletonList(merchant);
            Page<Merchant> merchantPage = new PageImpl<>(merchants, PageRequest.of(0, 10), 1);

            when(merchantService.getPendingMerchants(eq("测试"), eq(0), eq(10))).thenReturn(merchantPage);

            var result = mockMvc.perform(get("/api/admin/merchants/pending")
                    .sessionAttr("admin", testAdmin)
                    .param("keyword", "测试")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(res -> {});

            assertPaginatedResponse(result);
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetPendingMerchantsList_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/merchants/pending")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(merchantService, never()).getPendingMerchants(any(), anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/merchants/{id}/audit - 审核商家")
    class AuditMerchantTests {

        @Test
        @DisplayName("成功审核通过商家")
        void testAuditMerchant_Approve() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "approved");

            Merchant approvedMerchant = TestDataFactory.createMerchant(1, "测试商家", "test@merchant.com");
            approvedMerchant.setStatus("approved");

            when(merchantService.auditMerchant(eq(1), eq("approved"), any())).thenReturn(approvedMerchant);

            var result = performPut("/api/admin/merchants/{id}/audit", request, 1);

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.status").value("approved"));

            verify(merchantService).auditMerchant(eq(1), eq("approved"), any());
        }

        @Test
        @DisplayName("成功拒绝商家")
        void testAuditMerchant_Reject() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "rejected");
            request.put("reason", "资质不符合要求");

            Merchant rejectedMerchant = TestDataFactory.createMerchant(1, "测试商家", "test@merchant.com");
            rejectedMerchant.setStatus("rejected");

            when(merchantService.auditMerchant(eq(1), eq("rejected"), eq("资质不符合要求"))).thenReturn(rejectedMerchant);

            var result = performPut("/api/admin/merchants/{id}/audit", request, 1);

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.status").value("rejected"));

            verify(merchantService).auditMerchant(eq(1), eq("rejected"), eq("资质不符合要求"));
        }

        @Test
        @DisplayName("无效状态返回400")
        void testAuditMerchant_InvalidStatus() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "invalid_status");

            var result = performPut("/api/admin/merchants/{id}/audit", request, 1);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Invalid audit status. Must be 'approved' or 'rejected'"));

            verify(merchantService, never()).auditMerchant(anyInt(), anyString(), any());
        }

        @Test
        @DisplayName("空状态返回400")
        void testAuditMerchant_EmptyStatus() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "");

            var result = performPut("/api/admin/merchants/{id}/audit", request, 1);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Invalid audit status. Must be 'approved' or 'rejected'"));
        }

        @Test
        @DisplayName("商家不存在返回404")
        void testAuditMerchant_NotFound() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "approved");

            when(merchantService.auditMerchant(eq(999), eq("approved"), any()))
                    .thenThrow(new IllegalArgumentException("Merchant not found"));

            var result = performPut("/api/admin/merchants/{id}/audit", request, 999);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("未登录返回401")
        void testAuditMerchant_Unauthorized() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "approved");

            mockMvc.perform(put("/api/admin/merchants/1/audit")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(merchantService, never()).auditMerchant(anyInt(), anyString(), any());
        }
    }
}
