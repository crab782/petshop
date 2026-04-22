package com.petshop.controller.api;

import com.petshop.dto.AppointmentDTO;
import com.petshop.dto.ReviewDTO;
import com.petshop.entity.*;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

@DisplayName("用户评价API测试")
public class UserApiControllerReviewTest extends UserApiControllerTestBase {

    private UserApiController controller;

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        controller = new UserApiController();
        injectMocks();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private void injectMocks() {
        try {
            var userServiceField = UserApiController.class.getDeclaredField("userService");
            userServiceField.setAccessible(true);
            userServiceField.set(controller, userService);

            var petServiceField = UserApiController.class.getDeclaredField("petService");
            petServiceField.setAccessible(true);
            petServiceField.set(controller, petService);

            var appointmentServiceField = UserApiController.class.getDeclaredField("appointmentService");
            appointmentServiceField.setAccessible(true);
            appointmentServiceField.set(controller, appointmentService);

            var serviceServiceField = UserApiController.class.getDeclaredField("serviceService");
            serviceServiceField.setAccessible(true);
            serviceServiceField.set(controller, serviceService);

            var productOrderServiceField = UserApiController.class.getDeclaredField("productOrderService");
            productOrderServiceField.setAccessible(true);
            productOrderServiceField.set(controller, productOrderService);

            var reviewServiceField = UserApiController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(controller, reviewService);
        } catch (Exception e) {
            throw new RuntimeException("注入mock失败", e);
        }
    }

    @Nested
    @DisplayName("添加评价测试")
    class AddReviewTests {

        @Test
        @DisplayName("成功添加评价")
        void testAddReview_Success() throws Exception {
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            appointment.setUser(testUser);
            appointment.setService(service);
            appointment.setStatus("completed");

            Review savedReview = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 5, "非常满意的服务");

            when(appointmentService.findByIdAndUserId(1, testUserId)).thenReturn(createAppointmentDTO(appointment));
            when(reviewService.findByAppointmentId(1)).thenReturn(null);
            when(reviewService.create(any(Review.class))).thenReturn(savedReview);

            Map<String, Object> request = new HashMap<>();
            request.put("appointmentId", 1);
            request.put("rating", 5);
            request.put("comment", "非常满意的服务");

            var result = performPost("/api/user/reviews", request);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.rating").value(5))
                    .andExpect(jsonPath("$.data.comment").value("非常满意的服务"));

            verify(reviewService).create(any(Review.class));
        }

        @Test
        @DisplayName("成功添加最低评分(1分)")
        void testAddReview_MinRating() throws Exception {
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            appointment.setUser(testUser);
            appointment.setService(service);
            appointment.setStatus("completed");

            Review savedReview = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 1, "不太满意");

            when(appointmentService.findByIdAndUserId(1, testUserId)).thenReturn(createAppointmentDTO(appointment));
            when(reviewService.findByAppointmentId(1)).thenReturn(null);
            when(reviewService.create(any(Review.class))).thenReturn(savedReview);

            Map<String, Object> request = new HashMap<>();
            request.put("appointmentId", 1);
            request.put("rating", 1);
            request.put("comment", "不太满意");

            var result = performPost("/api/user/reviews", request);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.rating").value(1));
        }

        @Test
        @DisplayName("成功添加最高评分(5分)")
        void testAddReview_MaxRating() throws Exception {
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            appointment.setUser(testUser);
            appointment.setService(service);
            appointment.setStatus("completed");

            Review savedReview = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 5, "非常满意");

            when(appointmentService.findByIdAndUserId(1, testUserId)).thenReturn(createAppointmentDTO(appointment));
            when(reviewService.findByAppointmentId(1)).thenReturn(null);
            when(reviewService.create(any(Review.class))).thenReturn(savedReview);

            Map<String, Object> request = new HashMap<>();
            request.put("appointmentId", 1);
            request.put("rating", 5);
            request.put("comment", "非常满意");

            var result = performPost("/api/user/reviews", request);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.rating").value(5));
        }

        @Test
        @DisplayName("评分小于1返回400")
        void testAddReview_RatingTooLow() throws Exception {
            mockUserSession();

            Map<String, Object> request = new HashMap<>();
            request.put("appointmentId", 1);
            request.put("rating", 0);
            request.put("comment", "测试评价");

            var result = performPost("/api/user/reviews", request);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("评分必须在1-5之间"));
        }

        @Test
        @DisplayName("评分大于5返回400")
        void testAddReview_RatingTooHigh() throws Exception {
            mockUserSession();

            Map<String, Object> request = new HashMap<>();
            request.put("appointmentId", 1);
            request.put("rating", 6);
            request.put("comment", "测试评价");

            var result = performPost("/api/user/reviews", request);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("评分必须在1-5之间"));
        }

        @Test
        @DisplayName("评分为null返回400")
        void testAddReview_NullRating() throws Exception {
            mockUserSession();

            Map<String, Object> request = new HashMap<>();
            request.put("appointmentId", 1);
            request.put("comment", "测试评价");

            var result = performPost("/api/user/reviews", request);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("评分不能为空"));
        }

        @Test
        @DisplayName("预约不存在返回404")
        void testAddReview_AppointmentNotFound() throws Exception {
            mockUserSession();

            when(appointmentService.findByIdAndUserId(999, testUserId)).thenReturn(null);

            Map<String, Object> request = new HashMap<>();
            request.put("appointmentId", 999);
            request.put("rating", 5);
            request.put("comment", "测试评价");

            var result = performPost("/api/user/reviews", request);

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("预约不存在"));
        }

        @Test
        @DisplayName("预约未完成返回400")
        void testAddReview_AppointmentNotCompleted() throws Exception {
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            appointment.setUser(testUser);
            appointment.setStatus("pending");

            when(appointmentService.findByIdAndUserId(1, testUserId)).thenReturn(createAppointmentDTO(appointment));

            Map<String, Object> request = new HashMap<>();
            request.put("appointmentId", 1);
            request.put("rating", 5);
            request.put("comment", "测试评价");

            var result = performPost("/api/user/reviews", request);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("只能评价已完成的预约"));
        }

        @Test
        @DisplayName("重复评价返回400")
        void testAddReview_AlreadyReviewed() throws Exception {
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            appointment.setUser(testUser);
            appointment.setStatus("completed");

            Review existingReview = TestDataFactory.createReview(1, merchant);

            when(appointmentService.findByIdAndUserId(1, testUserId)).thenReturn(createAppointmentDTO(appointment));
            when(reviewService.findByAppointmentId(1)).thenReturn(existingReview);

            Map<String, Object> request = new HashMap<>();
            request.put("appointmentId", 1);
            request.put("rating", 5);
            request.put("comment", "测试评价");

            var result = performPost("/api/user/reviews", request);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("该预约已评价"));
        }

        @Test
        @DisplayName("评价内容为空可以提交")
        void testAddReview_EmptyComment() throws Exception {
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            appointment.setUser(testUser);
            appointment.setService(service);
            appointment.setStatus("completed");

            Review savedReview = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 5, "");

            when(appointmentService.findByIdAndUserId(1, testUserId)).thenReturn(createAppointmentDTO(appointment));
            when(reviewService.findByAppointmentId(1)).thenReturn(null);
            when(reviewService.create(any(Review.class))).thenReturn(savedReview);

            Map<String, Object> request = new HashMap<>();
            request.put("appointmentId", 1);
            request.put("rating", 5);
            request.put("comment", "");

            var result = performPost("/api/user/reviews", request);

            assertCreatedResponse(result);
        }

        @Test
        @DisplayName("未登录返回401")
        void testAddReview_Unauthorized() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("appointmentId", 1);
            request.put("rating", 5);
            request.put("comment", "测试评价");

            var result = performPostUnauthenticated("/api/user/reviews", request);

            assertUnauthorized(result);
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testAddReview_ServiceException() throws Exception {
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            appointment.setUser(testUser);
            appointment.setStatus("completed");

            when(appointmentService.findByIdAndUserId(1, testUserId)).thenReturn(createAppointmentDTO(appointment));
            when(reviewService.findByAppointmentId(1)).thenReturn(null);
            when(reviewService.create(any(Review.class))).thenThrow(new RuntimeException("数据库错误"));

            Map<String, Object> request = new HashMap<>();
            request.put("appointmentId", 1);
            request.put("rating", 5);
            request.put("comment", "测试评价");

            var result = performPost("/api/user/reviews", request);

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }

    @Nested
    @DisplayName("获取评价列表测试")
    class GetReviewsTests {

        @Test
        @DisplayName("成功获取评价列表（分页）")
        void testGetReviews_Success() throws Exception {
            mockUserSession();

            List<ReviewDTO> reviews = createTestReviewDTOs(3);
            Page<ReviewDTO> reviewPage = new Page<>(1, 10);
            reviewPage.setRecords(reviews);
            reviewPage.setTotal(3);

            when(reviewService.findByUserIdWithPaging(eq(testUserId), isNull(Integer.class), eq(0), eq(10)))
                    .thenReturn(reviewPage);

            var result = performGet("/api/user/reviews");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(3))
                    .andExpect(jsonPath("$.data.totalElements").value(3))
                    .andExpect(jsonPath("$.data.totalPages").value(1))
                    .andExpect(jsonPath("$.data.number").value(0))
                    .andExpect(jsonPath("$.data.size").value(10));

            verify(reviewService).findByUserIdWithPaging(testUserId, (Integer) null, 0, 10);
        }

        @Test
        @DisplayName("成功获取评价列表（带评分筛选）")
        void testGetReviews_WithRatingFilter() throws Exception {
            mockUserSession();

            List<ReviewDTO> reviews = createTestReviewDTOsWithRating(2, 5);
            Page<ReviewDTO> reviewPage = new Page<>(1, 10);
            reviewPage.setRecords(reviews);
            reviewPage.setTotal(2);

            when(reviewService.findByUserIdWithPaging(eq(testUserId), eq(5), eq(0), eq(10)))
                    .thenReturn(reviewPage);

            var result = mockMvc.perform(get("/api/user/reviews?rating=5")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.content.length()").value(2));

            verify(reviewService).findByUserIdWithPaging(testUserId, 5, 0, 10);
        }

        @Test
        @DisplayName("成功获取评价列表（自定义分页参数）")
        void testGetReviews_WithCustomPaging() throws Exception {
            mockUserSession();

            List<ReviewDTO> reviews = createTestReviewDTOs(5);
            Page<ReviewDTO> reviewPage = new Page<>(2, 5);
            reviewPage.setRecords(reviews);
            reviewPage.setTotal(15);

            when(reviewService.findByUserIdWithPaging(eq(testUserId), isNull(Integer.class), eq(1), eq(5)))
                    .thenReturn(reviewPage);

            var result = mockMvc.perform(get("/api/user/reviews?page=1&size=5")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.number").value(1))
                    .andExpect(jsonPath("$.data.size").value(5));

            verify(reviewService).findByUserIdWithPaging(testUserId, (Integer) null, 1, 5);
        }

        @Test
        @DisplayName("空评价列表")
        void testGetReviews_EmptyList() throws Exception {
            mockUserSession();

            Page<ReviewDTO> emptyPage = new Page<>(1, 10);
            emptyPage.setRecords(Collections.emptyList());
            emptyPage.setTotal(0);

            when(reviewService.findByUserIdWithPaging(eq(testUserId), isNull(Integer.class), eq(0), eq(10)))
                    .thenReturn(emptyPage);

            var result = performGet("/api/user/reviews");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(0))
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetReviews_Unauthorized() throws Exception {
            var result = performGetUnauthenticated("/api/user/reviews");

            assertUnauthorized(result);
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetReviews_ServiceException() throws Exception {
            mockUserSession();

            when(reviewService.findByUserIdWithPaging(anyInt(), any(), anyInt(), anyInt()))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            var result = performGet("/api/user/reviews");

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }

    @Nested
    @DisplayName("更新评价测试")
    class UpdateReviewTests {

        @Test
        @DisplayName("成功更新评价")
        void testUpdateReview_Success() throws Exception {
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            Review existingReview = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 4, "原来的评价");

            Review updatedReview = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 5, "更新后的评价");

            when(reviewService.findById(1)).thenReturn(existingReview);
            when(reviewService.update(any(Review.class))).thenReturn(updatedReview);

            Map<String, Object> request = new HashMap<>();
            request.put("rating", 5);
            request.put("comment", "更新后的评价");

            var result = performPut("/api/user/reviews/1", request, 1);

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.rating").value(5))
                    .andExpect(jsonPath("$.data.comment").value("更新后的评价"));

            verify(reviewService).update(any(Review.class));
        }

        @Test
        @DisplayName("成功更新评分")
        void testUpdateReview_OnlyRating() throws Exception {
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            Review existingReview = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 3, "评价内容");

            Review updatedReview = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 5, "评价内容");

            when(reviewService.findById(1)).thenReturn(existingReview);
            when(reviewService.update(any(Review.class))).thenReturn(updatedReview);

            Map<String, Object> request = new HashMap<>();
            request.put("rating", 5);

            var result = performPut("/api/user/reviews/1", request, 1);

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.rating").value(5));
        }

        @Test
        @DisplayName("成功更新评价内容")
        void testUpdateReview_OnlyComment() throws Exception {
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            Review existingReview = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 5, "原来的评价");

            Review updatedReview = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 5, "新的评价内容");

            when(reviewService.findById(1)).thenReturn(existingReview);
            when(reviewService.update(any(Review.class))).thenReturn(updatedReview);

            Map<String, Object> request = new HashMap<>();
            request.put("comment", "新的评价内容");

            var result = performPut("/api/user/reviews/1", request, 1);

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.comment").value("新的评价内容"));
        }

        @Test
        @DisplayName("更新评分小于1返回400")
        void testUpdateReview_RatingTooLow() throws Exception {
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            Review existingReview = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 5, "评价");

            when(reviewService.findById(1)).thenReturn(existingReview);

            Map<String, Object> request = new HashMap<>();
            request.put("rating", 0);
            request.put("comment", "测试评价");

            var result = performPut("/api/user/reviews/1", request, 1);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("评分必须在1-5之间"));
        }

        @Test
        @DisplayName("更新评分大于5返回400")
        void testUpdateReview_RatingTooHigh() throws Exception {
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            Review existingReview = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 5, "评价");

            when(reviewService.findById(1)).thenReturn(existingReview);

            Map<String, Object> request = new HashMap<>();
            request.put("rating", 6);
            request.put("comment", "测试评价");

            var result = performPut("/api/user/reviews/1", request, 1);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("评分必须在1-5之间"));
        }

        @Test
        @DisplayName("评价不存在返回404")
        void testUpdateReview_NotFound() throws Exception {
            mockUserSession();

            when(reviewService.findById(999)).thenReturn(null);

            Map<String, Object> request = new HashMap<>();
            request.put("rating", 5);
            request.put("comment", "测试评价");

            var result = performPut("/api/user/reviews/999", request, 999);

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("评价不存在"));
        }

        @Test
        @DisplayName("无权限更新返回403")
        void testUpdateReview_Forbidden() throws Exception {
            mockUserSession();

            User otherUser = TestDataFactory.createUser(2, "其他用户", "other@test.com");
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            Review review = TestDataFactory.createReview(1, merchant, otherUser, service, appointment, 5, "评价");

            when(reviewService.findById(1)).thenReturn(review);

            Map<String, Object> request = new HashMap<>();
            request.put("rating", 4);
            request.put("comment", "测试评价");

            var result = performPut("/api/user/reviews/1", request, 1);

            result.andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(403))
                    .andExpect(jsonPath("$.message").value("无权修改此评价"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testUpdateReview_Unauthorized() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("rating", 5);
            request.put("comment", "测试评价");

            var result = mockMvc.perform(put("/api/user/reviews/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print());

            assertUnauthorized(result);
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testUpdateReview_ServiceException() throws Exception {
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            Review existingReview = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 5, "评价");

            when(reviewService.findById(1)).thenReturn(existingReview);
            when(reviewService.update(any(Review.class))).thenThrow(new RuntimeException("更新失败"));

            Map<String, Object> request = new HashMap<>();
            request.put("rating", 5);
            request.put("comment", "测试评价");

            var result = performPut("/api/user/reviews/1", request, 1);

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
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            Review review = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 5, "评价");

            when(reviewService.findById(1)).thenReturn(review);
            doNothing().when(reviewService).delete(1);

            var result = performDelete("/api/user/reviews/1", 1);

            assertSuccess(result, "评价删除成功");

            verify(reviewService).delete(1);
        }

        @Test
        @DisplayName("评价不存在返回404")
        void testDeleteReview_NotFound() throws Exception {
            mockUserSession();

            when(reviewService.findById(999)).thenReturn(null);

            var result = performDelete("/api/user/reviews/999", 999);

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("评价不存在"));
        }

        @Test
        @DisplayName("无权限删除返回403")
        void testDeleteReview_Forbidden() throws Exception {
            mockUserSession();

            User otherUser = TestDataFactory.createUser(2, "其他用户", "other@test.com");
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            Review review = TestDataFactory.createReview(1, merchant, otherUser, service, appointment, 5, "评价");

            when(reviewService.findById(1)).thenReturn(review);

            var result = performDelete("/api/user/reviews/1", 1);

            result.andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(403))
                    .andExpect(jsonPath("$.message").value("无权删除此评价"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testDeleteReview_Unauthorized() throws Exception {
            var result = mockMvc.perform(delete("/api/user/reviews/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertUnauthorized(result);
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testDeleteReview_ServiceException() throws Exception {
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            Review review = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 5, "评价");

            when(reviewService.findById(1)).thenReturn(review);
            doThrow(new RuntimeException("删除失败")).when(reviewService).delete(1);

            var result = performDelete("/api/user/reviews/1", 1);

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
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            Review review = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 5, "非常满意");

            when(reviewService.findById(1)).thenReturn(review);

            var result = performGet("/api/user/reviews/1", 1);

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.rating").value(5))
                    .andExpect(jsonPath("$.data.comment").value("非常满意"));
        }

        @Test
        @DisplayName("评价不存在返回404")
        void testGetReviewById_NotFound() throws Exception {
            mockUserSession();

            when(reviewService.findById(999)).thenReturn(null);

            var result = performGet("/api/user/reviews/999", 999);

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("评价不存在"));
        }

        @Test
        @DisplayName("无权限查看返回403")
        void testGetReviewById_Forbidden() throws Exception {
            mockUserSession();

            User otherUser = TestDataFactory.createUser(2, "其他用户", "other@test.com");
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            Review review = TestDataFactory.createReview(1, merchant, otherUser, service, appointment, 5, "评价");

            when(reviewService.findById(1)).thenReturn(review);

            var result = performGet("/api/user/reviews/1", 1);

            result.andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(403))
                    .andExpect(jsonPath("$.message").value("无权查看此评价"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetReviewById_Unauthorized() throws Exception {
            var result = performGetUnauthenticated("/api/user/reviews/1", 1);

            assertUnauthorized(result);
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("分页参数边界-第0页")
        void testGetReviews_FirstPage() throws Exception {
            mockUserSession();

            List<ReviewDTO> reviews = createTestReviewDTOs(10);
            Page<ReviewDTO> reviewPage = new Page<>(1, 10);
            reviewPage.setRecords(reviews);
            reviewPage.setTotal(50);

            when(reviewService.findByUserIdWithPaging(eq(testUserId), isNull(Integer.class), eq(0), eq(10)))
                    .thenReturn(reviewPage);

            var result = mockMvc.perform(get("/api/user/reviews?page=0&size=10")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.number").value(0));
        }

        @Test
        @DisplayName("分页参数边界-最后一页")
        void testGetReviews_LastPage() throws Exception {
            mockUserSession();

            List<ReviewDTO> reviews = createTestReviewDTOs(5);
            Page<ReviewDTO> reviewPage = new Page<>(5, 10);
            reviewPage.setRecords(reviews);
            reviewPage.setTotal(45);

            when(reviewService.findByUserIdWithPaging(eq(testUserId), isNull(Integer.class), eq(4), eq(10)))
                    .thenReturn(reviewPage);

            var result = mockMvc.perform(get("/api/user/reviews?page=4&size=10")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.number").value(4));
        }

        @Test
        @DisplayName("评分边界-评分1")
        void testAddReview_RatingBoundary1() throws Exception {
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            appointment.setUser(testUser);
            appointment.setService(service);
            appointment.setStatus("completed");

            Review savedReview = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 1, "差评");

            when(appointmentService.findByIdAndUserId(1, testUserId)).thenReturn(createAppointmentDTO(appointment));
            when(reviewService.findByAppointmentId(1)).thenReturn(null);
            when(reviewService.create(any(Review.class))).thenReturn(savedReview);

            Map<String, Object> request = new HashMap<>();
            request.put("appointmentId", 1);
            request.put("rating", 1);
            request.put("comment", "差评");

            var result = performPost("/api/user/reviews", request);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.rating").value(1));
        }

        @Test
        @DisplayName("评分边界-评分5")
        void testAddReview_RatingBoundary5() throws Exception {
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            appointment.setUser(testUser);
            appointment.setService(service);
            appointment.setStatus("completed");

            Review savedReview = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 5, "好评");

            when(appointmentService.findByIdAndUserId(1, testUserId)).thenReturn(createAppointmentDTO(appointment));
            when(reviewService.findByAppointmentId(1)).thenReturn(null);
            when(reviewService.create(any(Review.class))).thenReturn(savedReview);

            Map<String, Object> request = new HashMap<>();
            request.put("appointmentId", 1);
            request.put("rating", 5);
            request.put("comment", "好评");

            var result = performPost("/api/user/reviews", request);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.rating").value(5));
        }

        @Test
        @DisplayName("评价内容边界-超长内容")
        void testAddReview_LongComment() throws Exception {
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            appointment.setUser(testUser);
            appointment.setService(service);
            appointment.setStatus("completed");

            String longComment = "a".repeat(1000);
            Review savedReview = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 5, longComment);

            when(appointmentService.findByIdAndUserId(1, testUserId)).thenReturn(createAppointmentDTO(appointment));
            when(reviewService.findByAppointmentId(1)).thenReturn(null);
            when(reviewService.create(any(Review.class))).thenReturn(savedReview);

            Map<String, Object> request = new HashMap<>();
            request.put("appointmentId", 1);
            request.put("rating", 5);
            request.put("comment", longComment);

            var result = performPost("/api/user/reviews", request);

            assertCreatedResponse(result);
        }

        @Test
        @DisplayName("分页大小边界-最小值")
        void testGetReviews_MinPageSize() throws Exception {
            mockUserSession();

            List<ReviewDTO> reviews = createTestReviewDTOs(1);
            Page<ReviewDTO> reviewPage = new Page<>(1, 1);
            reviewPage.setRecords(reviews);
            reviewPage.setTotal(10);

            when(reviewService.findByUserIdWithPaging(eq(testUserId), isNull(Integer.class), eq(0), eq(1)))
                    .thenReturn(reviewPage);

            var result = mockMvc.perform(get("/api/user/reviews?page=0&size=1")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.size").value(1))
                    .andExpect(jsonPath("$.data.content.length()").value(1));
        }

        @Test
        @DisplayName("分页大小边界-最大值")
        void testGetReviews_MaxPageSize() throws Exception {
            mockUserSession();

            List<ReviewDTO> reviews = createTestReviewDTOs(50);
            Page<ReviewDTO> reviewPage = new Page<>(1, 50);
            reviewPage.setRecords(reviews);
            reviewPage.setTotal(50);

            when(reviewService.findByUserIdWithPaging(eq(testUserId), isNull(Integer.class), eq(0), eq(50)))
                    .thenReturn(reviewPage);

            var result = mockMvc.perform(get("/api/user/reviews?page=0&size=50")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.size").value(50));
        }
    }

    @Nested
    @DisplayName("所有权验证测试")
    class OwnershipValidationTests {

        @Test
        @DisplayName("更新评价-验证所有权")
        void testUpdateReview_OwnershipValidation() throws Exception {
            mockUserSession();

            User otherUser = TestDataFactory.createUser(2, "其他用户", "other@test.com");
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            Review review = TestDataFactory.createReview(1, merchant, otherUser, service, appointment, 5, "评价");

            when(reviewService.findById(1)).thenReturn(review);

            Map<String, Object> request = new HashMap<>();
            request.put("rating", 4);

            var result = performPut("/api/user/reviews/1", request, 1);

            result.andExpect(status().isForbidden());
            verify(reviewService, never()).update(any());
        }

        @Test
        @DisplayName("删除评价-验证所有权")
        void testDeleteReview_OwnershipValidation() throws Exception {
            mockUserSession();

            User otherUser = TestDataFactory.createUser(2, "其他用户", "other@test.com");
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            Review review = TestDataFactory.createReview(1, merchant, otherUser, service, appointment, 5, "评价");

            when(reviewService.findById(1)).thenReturn(review);

            var result = performDelete("/api/user/reviews/1", 1);

            result.andExpect(status().isForbidden());
            verify(reviewService, never()).delete(anyInt());
        }

        @Test
        @DisplayName("查看评价详情-验证所有权")
        void testGetReviewById_OwnershipValidation() throws Exception {
            mockUserSession();

            User otherUser = TestDataFactory.createUser(2, "其他用户", "other@test.com");
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            Review review = TestDataFactory.createReview(1, merchant, otherUser, service, appointment, 5, "评价");

            when(reviewService.findById(1)).thenReturn(review);

            var result = performGet("/api/user/reviews/1", 1);

            result.andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("不同用户ID的评价访问被拒绝")
        void testAccessReview_DifferentUserId() throws Exception {
            mockUserSession(1);

            User user2 = TestDataFactory.createUser(2);
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            Review review = TestDataFactory.createReview(1, merchant, user2, service, appointment, 5, "评价");

            when(reviewService.findById(1)).thenReturn(review);

            Map<String, Object> request = new HashMap<>();
            request.put("rating", 4);

            var result = performPut("/api/user/reviews/1", request, 1);

            result.andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("并发和状态测试")
    class ConcurrencyAndStateTests {

        @Test
        @DisplayName("评价已删除后再次删除返回404")
        void testDeleteReview_AlreadyDeleted() throws Exception {
            mockUserSession();

            when(reviewService.findById(1)).thenReturn(null);

            var result = performDelete("/api/user/reviews/1", 1);

            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("评价已更新后再次更新")
        void testUpdateReview_Twice() throws Exception {
            mockUserSession();

            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant);
            Review existingReview = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 4, "第一次评价");

            Review updatedReview = TestDataFactory.createReview(1, merchant, testUser, service, appointment, 5, "第二次评价");

            when(reviewService.findById(1)).thenReturn(existingReview);
            when(reviewService.update(any(Review.class))).thenReturn(updatedReview);

            Map<String, Object> request = new HashMap<>();
            request.put("rating", 5);
            request.put("comment", "第二次评价");

            var result = performPut("/api/user/reviews/1", request, 1);

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.rating").value(5))
                    .andExpect(jsonPath("$.data.comment").value("第二次评价"));
        }
    }

    private List<ReviewDTO> createTestReviewDTOs(int count) {
        List<ReviewDTO> dtos = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            ReviewDTO dto = ReviewDTO.builder()
                    .id(i)
                    .userId(testUserId)
                    .userName("测试用户")
                    .merchantId(1)
                    .merchantName("测试商家")
                    .serviceId(1)
                    .serviceName("测试服务")
                    .appointmentId(i)
                    .rating(i % 5 + 1)
                    .comment("评价内容" + i)
                    .createTime(LocalDateTime.now().minusDays(i))
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    private List<ReviewDTO> createTestReviewDTOsWithRating(int count, int rating) {
        List<ReviewDTO> dtos = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            ReviewDTO dto = ReviewDTO.builder()
                    .id(i)
                    .userId(testUserId)
                    .userName("测试用户")
                    .merchantId(1)
                    .merchantName("测试商家")
                    .serviceId(1)
                    .serviceName("测试服务")
                    .appointmentId(i)
                    .rating(rating)
                    .comment("评分" + rating + "的评价" + i)
                    .createTime(LocalDateTime.now().minusDays(i))
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    private AppointmentDTO createAppointmentDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setUserId(appointment.getUser().getId());
        dto.setMerchantId(appointment.getMerchant().getId());
        dto.setServiceId(appointment.getService().getId());
        dto.setPetId(appointment.getPet().getId());
        dto.setStatus(appointment.getStatus());
        dto.setTotalPrice(appointment.getTotalPrice());
        dto.setAppointmentTime(appointment.getAppointmentTime());
        dto.setRemark(appointment.getNotes());
        return dto;
    }
}
