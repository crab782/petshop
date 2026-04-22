package com.petshop.controller.api;

import com.petshop.entity.Merchant;
import com.petshop.entity.Review;
import com.petshop.entity.Service;
import com.petshop.entity.User;
import com.petshop.factory.TestDataFactory;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("商家评价API测试")
public class MerchantApiControllerReviewsTest extends MerchantApiControllerTestBase {

    private MerchantApiController controller;

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        controller = new MerchantApiController();
        injectMocks();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private void injectMocks() {
        try {
            var merchantServiceField = MerchantApiController.class.getDeclaredField("merchantService");
            merchantServiceField.setAccessible(true);
            merchantServiceField.set(controller, merchantService);

            var reviewServiceField = MerchantApiController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(controller, reviewService);

            var serviceServiceField = MerchantApiController.class.getDeclaredField("serviceService");
            serviceServiceField.setAccessible(true);
            serviceServiceField.set(controller, serviceService);
        } catch (Exception e) {
            throw new RuntimeException("注入mock失败", e);
        }
    }

    @Nested
    @DisplayName("获取评价列表测试")
    class GetReviewsTests {

        @Test
        @DisplayName("成功获取评价列表（分页）")
        void testGetReviews_Success() throws Exception {
            mockMerchantSession();

            List<Review> reviews = createTestReviews(3);
            Page<Review> reviewPage = new Page<>(0, 10);
            reviewPage.setRecords(reviews);
            reviewPage.setTotal(3);

            when(reviewService.getReviewsWithPaging(eq(testMerchantId), isNull(), isNull(), eq(0), eq(10), eq("createdAt"), eq("desc")))
                    .thenReturn(reviewPage);

            var result = performGet("/api/merchant/reviews");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(3))
                    .andExpect(jsonPath("$.data.totalElements").value(3))
                    .andExpect(jsonPath("$.data.totalPages").value(1))
                    .andExpect(jsonPath("$.data.currentPage").value(0))
                    .andExpect(jsonPath("$.data.pageSize").value(10));

            verify(reviewService).getReviewsWithPaging(testMerchantId, null, null, 0, 10, "createdAt", "desc");
        }

        @Test
        @DisplayName("成功获取评价列表（带评分筛选）")
        void testGetReviews_WithRatingFilter() throws Exception {
            mockMerchantSession();

            List<Review> reviews = createTestReviewsWithRating(2, 5);
            Page<Review> reviewPage = new Page<>(0, 10);
            reviewPage.setRecords(reviews);
            reviewPage.setTotal(2);

            when(reviewService.getReviewsWithPaging(eq(testMerchantId), eq(5), isNull(), eq(0), eq(10), eq("createdAt"), eq("desc")))
                    .thenReturn(reviewPage);

            var result = mockMvc.perform(get("/api/merchant/reviews?rating=5")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.content.length()").value(2));

            verify(reviewService).getReviewsWithPaging(testMerchantId, 5, null, 0, 10, "createdAt", "desc");
        }

        @Test
        @DisplayName("成功获取评价列表（带关键字搜索）")
        void testGetReviews_WithKeyword() throws Exception {
            mockMerchantSession();

            List<Review> reviews = createTestReviews(1);
            Page<Review> reviewPage = new Page<>(0, 10);
            reviewPage.setRecords(reviews);
            reviewPage.setTotal(1);

            when(reviewService.getReviewsWithPaging(eq(testMerchantId), isNull(), eq("很好"), eq(0), eq(10), eq("createdAt"), eq("desc")))
                    .thenReturn(reviewPage);

            var result = mockMvc.perform(get("/api/merchant/reviews?keyword=很好")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.content.length()").value(1));

            verify(reviewService).getReviewsWithPaging(testMerchantId, null, "很好", 0, 10, "createdAt", "desc");
        }

        @Test
        @DisplayName("成功获取评价列表（自定义分页参数）")
        void testGetReviews_WithCustomPaging() throws Exception {
            mockMerchantSession();

            List<Review> reviews = createTestReviews(5);
            Page<Review> reviewPage = new Page<>(1, 5);
            reviewPage.setRecords(reviews);
            reviewPage.setTotal(15);

            when(reviewService.getReviewsWithPaging(eq(testMerchantId), isNull(), isNull(), eq(1), eq(5), eq("rating"), eq("asc")))
                    .thenReturn(reviewPage);

            var result = mockMvc.perform(get("/api/merchant/reviews?page=1&size=5&sortBy=rating&sortDir=asc")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.currentPage").value(1))
                    .andExpect(jsonPath("$.data.pageSize").value(5));

            verify(reviewService).getReviewsWithPaging(testMerchantId, null, null, 1, 5, "rating", "asc");
        }

        @Test
        @DisplayName("空评价列表")
        void testGetReviews_EmptyList() throws Exception {
            mockMerchantSession();

            Page<Review> emptyPage = new Page<>(0, 10);
            emptyPage.setRecords(Collections.emptyList());
            emptyPage.setTotal(0);

            when(reviewService.getReviewsWithPaging(eq(testMerchantId), isNull(), isNull(), eq(0), eq(10), eq("createdAt"), eq("desc")))
                    .thenReturn(emptyPage);

            var result = performGet("/api/merchant/reviews");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(0))
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetReviews_Unauthorized() throws Exception {
            var result = mockMvc.perform(get("/api/merchant/reviews")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetReviews_ServiceException() throws Exception {
            mockMerchantSession();

            when(reviewService.getReviewsWithPaging(anyInt(), any(), any(), anyInt(), anyInt(), anyString(), anyString()))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            var result = performGet("/api/merchant/reviews");

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("获取评价列表失败：数据库连接失败"));
        }
    }

    @Nested
    @DisplayName("获取评价统计测试")
    class GetReviewStatisticsTests {

        @Test
        @DisplayName("成功获取评价统计信息")
        void testGetReviewStatistics_Success() throws Exception {
            mockMerchantSession();

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("averageRating", 4.5);
            statistics.put("totalReviews", 100L);
            Map<Integer, Long> distribution = new HashMap<>();
            distribution.put(5, 60L);
            distribution.put(4, 25L);
            distribution.put(3, 10L);
            distribution.put(2, 3L);
            distribution.put(1, 2L);
            statistics.put("ratingDistribution", distribution);

            when(reviewService.getReviewStatistics(testMerchantId)).thenReturn(statistics);

            var result = performGet("/api/merchant/reviews/statistics");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.averageRating").value(4.5))
                    .andExpect(jsonPath("$.data.totalReviews").value(100))
                    .andExpect(jsonPath("$.data.ratingDistribution.5").value(60))
                    .andExpect(jsonPath("$.data.ratingDistribution.4").value(25));

            verify(reviewService).getReviewStatistics(testMerchantId);
        }

        @Test
        @DisplayName("无评价时的统计信息")
        void testGetReviewStatistics_NoReviews() throws Exception {
            mockMerchantSession();

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("averageRating", 0.0);
            statistics.put("totalReviews", 0L);
            Map<Integer, Long> distribution = new HashMap<>();
            for (int i = 1; i <= 5; i++) {
                distribution.put(i, 0L);
            }
            statistics.put("ratingDistribution", distribution);

            when(reviewService.getReviewStatistics(testMerchantId)).thenReturn(statistics);

            var result = performGet("/api/merchant/reviews/statistics");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.averageRating").value(0.0))
                    .andExpect(jsonPath("$.data.totalReviews").value(0));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetReviewStatistics_Unauthorized() throws Exception {
            var result = mockMvc.perform(get("/api/merchant/reviews/statistics")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetReviewStatistics_ServiceException() throws Exception {
            mockMerchantSession();

            when(reviewService.getReviewStatistics(testMerchantId))
                    .thenThrow(new RuntimeException("统计查询失败"));

            var result = performGet("/api/merchant/reviews/statistics");

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }

    @Nested
    @DisplayName("获取评价详情测试")
    class GetReviewByIdTests {

        @Test
        @DisplayName("成功获取评价详情")
        void testGetReviewById_Success() throws Exception {
            mockMerchantSession();

            Review review = TestDataFactory.createReview(1, testMerchant);
            when(reviewService.findById(1)).thenReturn(review);

            var result = performGet("/api/merchant/reviews/1");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.rating").value(5))
                    .andExpect(jsonPath("$.data.comment").value("测试评价内容"));

            verify(reviewService).findById(1);
        }

        @Test
        @DisplayName("评价不存在返回404")
        void testGetReviewById_NotFound() throws Exception {
            mockMerchantSession();

            when(reviewService.findById(999)).thenReturn(null);

            var result = performGet("/api/merchant/reviews/999");

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("评价不存在"));
        }

        @Test
        @DisplayName("无权限查看返回403")
        void testGetReviewById_Forbidden() throws Exception {
            mockMerchantSession();

            Merchant otherMerchant = TestDataFactory.createMerchant(2, "其他商家", "other@test.com");
            Review review = TestDataFactory.createReview(1, otherMerchant);
            when(reviewService.findById(1)).thenReturn(review);

            var result = performGet("/api/merchant/reviews/1");

            result.andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(403))
                    .andExpect(jsonPath("$.message").value("无权查看此评价"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetReviewById_Unauthorized() throws Exception {
            var result = mockMvc.perform(get("/api/merchant/reviews/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetReviewById_ServiceException() throws Exception {
            mockMerchantSession();

            when(reviewService.findById(1)).thenThrow(new RuntimeException("查询失败"));

            var result = performGet("/api/merchant/reviews/1");

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }

    @Nested
    @DisplayName("回复评价测试")
    class ReplyToReviewTests {

        @Test
        @DisplayName("成功回复评价")
        void testReplyToReview_Success() throws Exception {
            mockMerchantSession();

            Review review = TestDataFactory.createReview(1, testMerchant);
            Review updatedReview = TestDataFactory.createReview(1, testMerchant);
            updatedReview.setReplyContent("感谢您的好评！");
            updatedReview.setReplyTime(LocalDateTime.now());

            when(reviewService.findById(1)).thenReturn(review);
            when(reviewService.replyToReview(1, "感谢您的好评！")).thenReturn(updatedReview);

            Map<String, String> request = new HashMap<>();
            request.put("replyContent", "感谢您的好评！");

            var result = performPut("/api/merchant/reviews/1/reply", request, 1);

            assertSuccess(result, "回复成功");
            result.andExpect(jsonPath("$.data.replyContent").value("感谢您的好评！"));

            verify(reviewService).replyToReview(1, "感谢您的好评！");
        }

        @Test
        @DisplayName("评价不存在返回404")
        void testReplyToReview_NotFound() throws Exception {
            mockMerchantSession();

            when(reviewService.findById(999)).thenReturn(null);

            Map<String, String> request = new HashMap<>();
            request.put("replyContent", "感谢评价");

            var result = performPut("/api/merchant/reviews/999/reply", request, 999);

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("无权限回复返回403")
        void testReplyToReview_Forbidden() throws Exception {
            mockMerchantSession();

            Merchant otherMerchant = TestDataFactory.createMerchant(2, "其他商家", "other@test.com");
            Review review = TestDataFactory.createReview(1, otherMerchant);
            when(reviewService.findById(1)).thenReturn(review);

            Map<String, String> request = new HashMap<>();
            request.put("replyContent", "感谢评价");

            var result = performPut("/api/merchant/reviews/1/reply", request, 1);

            result.andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(403))
                    .andExpect(jsonPath("$.message").value("无权回复此评价"));
        }

        @Test
        @DisplayName("空回复内容返回400")
        void testReplyToReview_EmptyContent() throws Exception {
            mockMerchantSession();

            Review review = TestDataFactory.createReview(1, testMerchant);
            when(reviewService.findById(1)).thenReturn(review);

            Map<String, String> request = new HashMap<>();
            request.put("replyContent", "");

            var result = performPut("/api/merchant/reviews/1/reply", request, 1);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("回复内容不能为空"));
        }

        @Test
        @DisplayName("null回复内容返回400")
        void testReplyToReview_NullContent() throws Exception {
            mockMerchantSession();

            Review review = TestDataFactory.createReview(1, testMerchant);
            when(reviewService.findById(1)).thenReturn(review);

            Map<String, String> request = new HashMap<>();

            var result = performPut("/api/merchant/reviews/1/reply", request, 1);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("回复内容不能为空"));
        }

        @Test
        @DisplayName("只有空格的回复内容返回400")
        void testReplyToReview_WhitespaceContent() throws Exception {
            mockMerchantSession();

            Review review = TestDataFactory.createReview(1, testMerchant);
            when(reviewService.findById(1)).thenReturn(review);

            Map<String, String> request = new HashMap<>();
            request.put("replyContent", "   ");

            var result = performPut("/api/merchant/reviews/1/reply", request, 1);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("回复内容不能为空"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testReplyToReview_Unauthorized() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("replyContent", "感谢评价");

            var result = mockMvc.perform(put("/api/merchant/reviews/1/reply")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print());

            result.andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testReplyToReview_ServiceException() throws Exception {
            mockMerchantSession();

            Review review = TestDataFactory.createReview(1, testMerchant);
            when(reviewService.findById(1)).thenReturn(review);
            when(reviewService.replyToReview(anyInt(), anyString()))
                    .thenThrow(new RuntimeException("保存失败"));

            Map<String, String> request = new HashMap<>();
            request.put("replyContent", "感谢评价");

            var result = performPut("/api/merchant/reviews/1/reply", request, 1);

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }

    @Nested
    @DisplayName("删除评价测试")
    class DeleteReviewTests {

        @Test
        @DisplayName("成功删除评价")
        void testDeleteReview_Success() throws Exception {
            mockMerchantSession();

            Review review = TestDataFactory.createReview(1, testMerchant);
            when(reviewService.findById(1)).thenReturn(review);
            doNothing().when(reviewService).delete(1);

            var result = performDelete("/api/merchant/reviews/1", 1);

            assertSuccess(result, "评价删除成功");

            verify(reviewService).delete(1);
        }

        @Test
        @DisplayName("评价不存在返回404")
        void testDeleteReview_NotFound() throws Exception {
            mockMerchantSession();

            when(reviewService.findById(999)).thenReturn(null);

            var result = performDelete("/api/merchant/reviews/999", 999);

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("评价不存在"));
        }

        @Test
        @DisplayName("无权限删除返回403")
        void testDeleteReview_Forbidden() throws Exception {
            mockMerchantSession();

            Merchant otherMerchant = TestDataFactory.createMerchant(2, "其他商家", "other@test.com");
            Review review = TestDataFactory.createReview(1, otherMerchant);
            when(reviewService.findById(1)).thenReturn(review);

            var result = performDelete("/api/merchant/reviews/1", 1);

            result.andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(403))
                    .andExpect(jsonPath("$.message").value("无权删除此评价"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testDeleteReview_Unauthorized() throws Exception {
            var result = mockMvc.perform(delete("/api/merchant/reviews/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testDeleteReview_ServiceException() throws Exception {
            mockMerchantSession();

            Review review = TestDataFactory.createReview(1, testMerchant);
            when(reviewService.findById(1)).thenReturn(review);
            doThrow(new RuntimeException("删除失败")).when(reviewService).delete(1);

            var result = performDelete("/api/merchant/reviews/1", 1);

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }

    @Nested
    @DisplayName("获取最近评价测试")
    class GetRecentReviewsTests {

        @Test
        @DisplayName("成功获取最近评价列表")
        void testGetRecentReviews_Success() throws Exception {
            mockMerchantSession();

            List<Review> reviews = createTestReviews(5);
            when(reviewService.getRecentReviews(testMerchantId, 5)).thenReturn(reviews);

            var result = performGet("/api/merchant/reviews/recent");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(5));

            verify(reviewService).getRecentReviews(testMerchantId, 5);
        }

        @Test
        @DisplayName("成功获取指定数量的最近评价")
        void testGetRecentReviews_WithCustomLimit() throws Exception {
            mockMerchantSession();

            List<Review> reviews = createTestReviews(3);
            when(reviewService.getRecentReviews(testMerchantId, 3)).thenReturn(reviews);

            var result = mockMvc.perform(get("/api/merchant/reviews/recent?limit=3")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.length()").value(3));

            verify(reviewService).getRecentReviews(testMerchantId, 3);
        }

        @Test
        @DisplayName("空评价列表")
        void testGetRecentReviews_EmptyList() throws Exception {
            mockMerchantSession();

            when(reviewService.getRecentReviews(testMerchantId, 5)).thenReturn(Collections.emptyList());

            var result = performGet("/api/merchant/reviews/recent");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetRecentReviews_Unauthorized() throws Exception {
            var result = mockMvc.perform(get("/api/merchant/reviews/recent")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetRecentReviews_ServiceException() throws Exception {
            mockMerchantSession();

            when(reviewService.getRecentReviews(testMerchantId, 5))
                    .thenThrow(new RuntimeException("查询失败"));

            var result = performGet("/api/merchant/reviews/recent");

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("分页参数边界-第0页")
        void testGetReviews_FirstPage() throws Exception {
            mockMerchantSession();

            List<Review> reviews = createTestReviews(10);
            Page<Review> reviewPage = new Page<>(0, 10);
            reviewPage.setRecords(reviews);
            reviewPage.setTotal(50);

            when(reviewService.getReviewsWithPaging(eq(testMerchantId), isNull(), isNull(), eq(0), eq(10), anyString(), anyString()))
                    .thenReturn(reviewPage);

            var result = mockMvc.perform(get("/api/merchant/reviews?page=0&size=10")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.currentPage").value(0))
                    .andExpect(jsonPath("$.data.hasPrevious").value(false));
        }

        @Test
        @DisplayName("分页参数边界-最后一页")
        void testGetReviews_LastPage() throws Exception {
            mockMerchantSession();

            List<Review> reviews = createTestReviews(5);
            Page<Review> reviewPage = new Page<>(4, 10);
            reviewPage.setRecords(reviews);
            reviewPage.setTotal(45);

            when(reviewService.getReviewsWithPaging(eq(testMerchantId), isNull(), isNull(), eq(4), eq(10), anyString(), anyString()))
                    .thenReturn(reviewPage);

            var result = mockMvc.perform(get("/api/merchant/reviews?page=4&size=10")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.currentPage").value(4))
                    .andExpect(jsonPath("$.data.hasNext").value(false));
        }

        @Test
        @DisplayName("评分筛选边界-最低评分")
        void testGetReviews_MinRating() throws Exception {
            mockMerchantSession();

            List<Review> reviews = createTestReviewsWithRating(1, 1);
            Page<Review> reviewPage = new Page<>(0, 10);
            reviewPage.setRecords(reviews);
            reviewPage.setTotal(1);

            when(reviewService.getReviewsWithPaging(eq(testMerchantId), eq(1), isNull(), anyInt(), anyInt(), anyString(), anyString()))
                    .thenReturn(reviewPage);

            var result = mockMvc.perform(get("/api/merchant/reviews?rating=1")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            verify(reviewService).getReviewsWithPaging(testMerchantId, 1, null, 0, 10, "createdAt", "desc");
        }

        @Test
        @DisplayName("评分筛选边界-最高评分")
        void testGetReviews_MaxRating() throws Exception {
            mockMerchantSession();

            List<Review> reviews = createTestReviewsWithRating(1, 5);
            Page<Review> reviewPage = new Page<>(0, 10);
            reviewPage.setRecords(reviews);
            reviewPage.setTotal(1);

            when(reviewService.getReviewsWithPaging(eq(testMerchantId), eq(5), isNull(), anyInt(), anyInt(), anyString(), anyString()))
                    .thenReturn(reviewPage);

            var result = mockMvc.perform(get("/api/merchant/reviews?rating=5")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            verify(reviewService).getReviewsWithPaging(testMerchantId, 5, null, 0, 10, "createdAt", "desc");
        }

        @Test
        @DisplayName("回复内容边界-超长内容")
        void testReplyToReview_LongContent() throws Exception {
            mockMerchantSession();

            Review review = TestDataFactory.createReview(1, testMerchant);
            String longContent = "a".repeat(1000);
            Review updatedReview = TestDataFactory.createReview(1, testMerchant);
            updatedReview.setReplyContent(longContent);

            when(reviewService.findById(1)).thenReturn(review);
            when(reviewService.replyToReview(1, longContent)).thenReturn(updatedReview);

            Map<String, String> request = new HashMap<>();
            request.put("replyContent", longContent);

            var result = performPut("/api/merchant/reviews/1/reply", request, 1);

            assertSuccess(result, "回复成功");
        }

        @Test
        @DisplayName("最近评价数量边界-limit=1")
        void testGetRecentReviews_MinLimit() throws Exception {
            mockMerchantSession();

            List<Review> reviews = createTestReviews(1);
            when(reviewService.getRecentReviews(testMerchantId, 1)).thenReturn(reviews);

            var result = mockMvc.perform(get("/api/merchant/reviews/recent?limit=1")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.length()").value(1));
        }

        @Test
        @DisplayName("最近评价数量边界-大limit值")
        void testGetRecentReviews_LargeLimit() throws Exception {
            mockMerchantSession();

            List<Review> reviews = createTestReviews(10);
            when(reviewService.getRecentReviews(testMerchantId, 100)).thenReturn(reviews);

            var result = mockMvc.perform(get("/api/merchant/reviews/recent?limit=100")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            verify(reviewService).getRecentReviews(testMerchantId, 100);
        }
    }

    private List<Review> createTestReviews(int count) {
        List<Review> reviews = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            reviews.add(TestDataFactory.createReview(i, testMerchant, i % 5 + 1, "评价内容" + i));
        }
        return reviews;
    }

    private List<Review> createTestReviewsWithRating(int count, int rating) {
        List<Review> reviews = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            reviews.add(TestDataFactory.createReview(i, testMerchant, rating, "评分" + rating + "的评价" + i));
        }
        return reviews;
    }
}
