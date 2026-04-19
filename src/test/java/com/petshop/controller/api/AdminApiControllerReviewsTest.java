package com.petshop.controller.api;

import com.petshop.entity.Merchant;
import com.petshop.entity.Review;
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

@DisplayName("管理员评价管理API测试")
public class AdminApiControllerReviewsTest extends AdminApiControllerTestBase {

    private AdminApiController controller;

    @BeforeEach
    public void setupController() {
        controller = new AdminApiController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "reviewService", reviewService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("GET /api/admin/reviews - 获取评价列表")
    class GetReviewsTests {

        @Test
        @DisplayName("成功获取评价列表")
        void testGetReviews_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Review review1 = TestDataFactory.createReview(1, merchant, 5, "非常好的服务");
            Review review2 = TestDataFactory.createReview(2, merchant, 4, "服务不错");
            List<Review> reviews = Arrays.asList(review1, review2);
            Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(0, 10), 2);

            when(reviewService.getAllReviews(0, 10)).thenReturn(reviewPage);

            var result = performGet("/api/admin/reviews");

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.data.length()").value(2));

            verify(reviewService).getAllReviews(0, 10);
        }

        @Test
        @DisplayName("带搜索条件获取评价列表")
        void testGetReviews_WithSearch() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Review review = TestDataFactory.createReview(1, merchant, 5, "非常好的服务");
            List<Review> reviews = Collections.singletonList(review);
            Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(0, 10), 1);

            when(reviewService.searchAdminReviews(eq(5), any(), any(), any(), eq(0), eq(10)))
                    .thenReturn(reviewPage);

            var result = mockMvc.perform(get("/api/admin/reviews")
                    .sessionAttr("admin", testAdmin)
                    .param("rating", "5")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(res -> {});

            assertPaginatedResponse(result);
            verify(reviewService).searchAdminReviews(eq(5), any(), any(), any(), eq(0), eq(10));
        }

        @Test
        @DisplayName("按商家筛选")
        void testGetReviews_ByMerchant() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Review review = TestDataFactory.createReview(1, merchant, 5, "非常好的服务");
            List<Review> reviews = Collections.singletonList(review);
            Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(0, 10), 1);

            when(reviewService.searchAdminReviews(any(), any(), eq(1), any(), eq(0), eq(10)))
                    .thenReturn(reviewPage);

            var result = mockMvc.perform(get("/api/admin/reviews")
                    .sessionAttr("admin", testAdmin)
                    .param("merchantId", "1")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(res -> {});

            assertPaginatedResponse(result);
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetReviews_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/reviews")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(reviewService, never()).getAllReviews(anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("GET /api/admin/reviews/pending - 获取待审核评价")
    class GetPendingReviewsTests {

        @Test
        @DisplayName("成功获取待审核评价列表")
        void testGetPendingReviews_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Review review1 = TestDataFactory.createReview(1, merchant, 5, "待审核评价1");
            Review review2 = TestDataFactory.createReview(2, merchant, 4, "待审核评价2");
            List<Review> reviews = Arrays.asList(review1, review2);
            Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(0, 10), 2);

            when(reviewService.getPendingReviews(any(), eq(0), eq(10))).thenReturn(reviewPage);

            var result = performGet("/api/admin/reviews/pending");

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.data.length()").value(2));

            verify(reviewService).getPendingReviews(any(), eq(0), eq(10));
        }

        @Test
        @DisplayName("带关键字搜索")
        void testGetPendingReviews_WithKeyword() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Review review = TestDataFactory.createReview(1, merchant, 5, "搜索评价");
            List<Review> reviews = Collections.singletonList(review);
            Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(0, 10), 1);

            when(reviewService.getPendingReviews(eq("搜索"), eq(0), eq(10))).thenReturn(reviewPage);

            var result = mockMvc.perform(get("/api/admin/reviews/pending")
                    .sessionAttr("admin", testAdmin)
                    .param("keyword", "搜索")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(res -> {});

            assertPaginatedResponse(result);
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetPendingReviews_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/reviews/pending")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(reviewService, never()).getPendingReviews(any(), anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/reviews/{id}/audit - 审核评价")
    class AuditReviewTests {

        @Test
        @DisplayName("成功审核通过评价")
        void testAuditReview_Approve() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "approved");

            Merchant merchant = TestDataFactory.createMerchant(1);
            Review approvedReview = TestDataFactory.createReview(1, merchant, 5, "测试评价");

            when(reviewService.auditReview(eq(1), eq("approved"), any())).thenReturn(approvedReview);

            var result = performPut("/api/admin/reviews/{id}/audit", request, 1);

            assertSuccess(result, "Review audit completed");

            verify(reviewService).auditReview(eq(1), eq("approved"), any());
        }

        @Test
        @DisplayName("成功拒绝评价")
        void testAuditReview_Reject() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "rejected");
            request.put("reason", "内容违规");

            Merchant merchant = TestDataFactory.createMerchant(1);
            Review rejectedReview = TestDataFactory.createReview(1, merchant, 5, "测试评价");

            when(reviewService.auditReview(eq(1), eq("rejected"), eq("内容违规"))).thenReturn(rejectedReview);

            var result = performPut("/api/admin/reviews/{id}/audit", request, 1);

            assertSuccess(result, "Review audit completed");

            verify(reviewService).auditReview(eq(1), eq("rejected"), eq("内容违规"));
        }

        @Test
        @DisplayName("无效状态返回400")
        void testAuditReview_InvalidStatus() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "invalid_status");

            var result = performPut("/api/admin/reviews/{id}/audit", request, 1);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Invalid audit status. Must be 'approved' or 'rejected'"));

            verify(reviewService, never()).auditReview(anyInt(), anyString(), any());
        }

        @Test
        @DisplayName("评价不存在返回404")
        void testAuditReview_NotFound() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "approved");

            when(reviewService.auditReview(eq(999), eq("approved"), any())).thenReturn(null);

            var result = performPut("/api/admin/reviews/{id}/audit", request, 999);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("Review not found"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testAuditReview_Unauthorized() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("status", "approved");

            mockMvc.perform(put("/api/admin/reviews/1/audit")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(reviewService, never()).auditReview(anyInt(), anyString(), any());
        }
    }

    @Nested
    @DisplayName("DELETE /api/admin/reviews/{id} - 删除评价")
    class DeleteReviewTests {

        @Test
        @DisplayName("成功删除评价")
        void testDeleteReview_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Review review = TestDataFactory.createReview(1, merchant, 5, "测试评价");

            when(reviewService.findById(1)).thenReturn(review);
            doNothing().when(reviewService).delete(1);

            var result = performDelete("/api/admin/reviews/{id}", 1);

            assertNoContent(result);

            verify(reviewService).findById(1);
            verify(reviewService).delete(1);
        }

        @Test
        @DisplayName("评价不存在返回404")
        void testDeleteReview_NotFound() throws Exception {
            when(reviewService.findById(999)).thenReturn(null);

            var result = performDelete("/api/admin/reviews/{id}", 999);

            assertNotFound(result);

            verify(reviewService).findById(999);
            verify(reviewService, never()).delete(anyInt());
        }

        @Test
        @DisplayName("未登录返回401")
        void testDeleteReview_Unauthorized() throws Exception {
            mockMvc.perform(delete("/api/admin/reviews/1")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(reviewService, never()).findById(anyInt());
            verify(reviewService, never()).delete(anyInt());
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/reviews/batch/status - 批量更新评价状态")
    class BatchUpdateReviewStatusTests {

        @Test
        @DisplayName("成功批量更新评价状态")
        void testBatchUpdateReviewStatus_Success() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));
            request.put("status", "approved");

            when(reviewService.batchUpdateStatus(anyList(), eq("approved"))).thenReturn(3);

            var result = performPut("/api/admin/reviews/batch/status", request);

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.updatedCount").value(3));

            verify(reviewService).batchUpdateStatus(anyList(), eq("approved"));
        }

        @Test
        @DisplayName("空ID列表返回400")
        void testBatchUpdateReviewStatus_EmptyIds() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Collections.emptyList());
            request.put("status", "approved");

            var result = performPut("/api/admin/reviews/batch/status", request);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Review IDs cannot be empty"));

            verify(reviewService, never()).batchUpdateStatus(anyList(), anyString());
        }

        @Test
        @DisplayName("无效状态值返回400")
        void testBatchUpdateReviewStatus_InvalidStatus() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));
            request.put("status", "invalid");

            var result = performPut("/api/admin/reviews/batch/status", request);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Invalid status. Must be 'approved' or 'rejected'"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testBatchUpdateReviewStatus_Unauthorized() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));
            request.put("status", "approved");

            mockMvc.perform(put("/api/admin/reviews/batch/status")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(reviewService, never()).batchUpdateStatus(anyList(), anyString());
        }
    }

    @Nested
    @DisplayName("DELETE /api/admin/reviews/batch - 批量删除评价")
    class BatchDeleteReviewsTests {

        @Test
        @DisplayName("成功批量删除评价")
        void testBatchDeleteReviews_Success() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));

            when(reviewService.batchDelete(anyList())).thenReturn(3);

            var result = mockMvc.perform(delete("/api/admin/reviews/batch")
                    .sessionAttr("admin", testAdmin)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(res -> {});

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.deletedCount").value(3));

            verify(reviewService).batchDelete(anyList());
        }

        @Test
        @DisplayName("空ID列表返回400")
        void testBatchDeleteReviews_EmptyIds() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Collections.emptyList());

            var result = mockMvc.perform(delete("/api/admin/reviews/batch")
                    .sessionAttr("admin", testAdmin)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(res -> {});

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Review IDs cannot be empty"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testBatchDeleteReviews_Unauthorized() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));

            mockMvc.perform(delete("/api/admin/reviews/batch")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(res -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(reviewService, never()).batchDelete(anyList());
        }
    }
}
