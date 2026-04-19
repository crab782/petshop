package com.petshop.controller.api;

import com.petshop.entity.Announcement;
import com.petshop.entity.Merchant;
import com.petshop.entity.User;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("管理员仪表盘API测试")
public class AdminApiControllerDashboardTest extends AdminApiControllerTestBase {

    private AdminApiController controller;

    @BeforeEach
    public void setupController() {
        controller = new AdminApiController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "userService", userService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "merchantService", merchantService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "userRepository", userRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "merchantRepository", merchantRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "appointmentRepository", appointmentRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "serviceRepository", serviceRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "announcementRepository", announcementRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("GET /api/admin/dashboard - 获取仪表盘统计")
    class GetDashboardStatsTests {

        @Test
        @DisplayName("成功获取仪表盘统计数据")
        void testGetDashboardStats_Success() throws Exception {
            when(userRepository.count()).thenReturn(100L);
            when(merchantRepository.count()).thenReturn(50L);
            when(appointmentRepository.count()).thenReturn(500L);
            when(serviceRepository.count()).thenReturn(200L);

            var result = performGet("/api/admin/dashboard");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.userCount").value(100))
                    .andExpect(jsonPath("$.data.merchantCount").value(50))
                    .andExpect(jsonPath("$.data.orderCount").value(500))
                    .andExpect(jsonPath("$.data.serviceCount").value(200));

            verify(userRepository).count();
            verify(merchantRepository).count();
            verify(appointmentRepository).count();
            verify(serviceRepository).count();
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetDashboardStats_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/dashboard")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问"));

            verify(userRepository, never()).count();
        }

        @Test
        @DisplayName("零数据统计")
        void testGetDashboardStats_ZeroCounts() throws Exception {
            when(userRepository.count()).thenReturn(0L);
            when(merchantRepository.count()).thenReturn(0L);
            when(appointmentRepository.count()).thenReturn(0L);
            when(serviceRepository.count()).thenReturn(0L);

            var result = performGet("/api/admin/dashboard");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.userCount").value(0))
                    .andExpect(jsonPath("$.data.merchantCount").value(0))
                    .andExpect(jsonPath("$.data.orderCount").value(0))
                    .andExpect(jsonPath("$.data.serviceCount").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/admin/dashboard/recent-users - 获取最近注册用户")
    class GetRecentUsersTests {

        @Test
        @DisplayName("成功获取最近注册用户列表")
        void testGetRecentUsers_Success() throws Exception {
            User user1 = TestDataFactory.createUser(1, "用户1", "user1@test.com");
            User user2 = TestDataFactory.createUser(2, "用户2", "user2@test.com");
            List<User> users = Arrays.asList(user1, user2);
            Page<User> userPage = new PageImpl<>(users, PageRequest.of(0, 10), 2);

            when(userRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(userPage);

            var result = performGet("/api/admin/dashboard/recent-users");

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.data.length()").value(2));

            verify(userRepository).findAllByOrderByCreatedAtDesc(any(Pageable.class));
        }

        @Test
        @DisplayName("成功获取空用户列表")
        void testGetRecentUsers_EmptyList() throws Exception {
            Page<User> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

            when(userRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(emptyPage);

            var result = performGet("/api/admin/dashboard/recent-users");

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.data.length()").value(0));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetRecentUsers_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/dashboard/recent-users")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(userRepository, never()).findAllByOrderByCreatedAtDesc(any(Pageable.class));
        }

        @Test
        @DisplayName("分页参数正确传递")
        void testGetRecentUsers_Pagination() throws Exception {
            Page<User> userPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(1, 20), 0);

            when(userRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(userPage);

            var result = mockMvc.perform(get("/api/admin/dashboard/recent-users")
                    .sessionAttr("admin", testAdmin)
                    .param("page", "1")
                    .param("pageSize", "20")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result1 -> {});

            assertPaginatedResponse(result);
        }
    }

    @Nested
    @DisplayName("GET /api/admin/dashboard/pending-merchants - 获取待审核商家")
    class GetPendingMerchantsTests {

        @Test
        @DisplayName("成功获取待审核商家列表")
        void testGetPendingMerchants_Success() throws Exception {
            Merchant merchant1 = TestDataFactory.createMerchant(1, "待审核商家1", "pending1@test.com");
            merchant1.setStatus("pending");
            Merchant merchant2 = TestDataFactory.createMerchant(2, "待审核商家2", "pending2@test.com");
            merchant2.setStatus("pending");
            List<Merchant> merchants = Arrays.asList(merchant1, merchant2);
            Page<Merchant> merchantPage = new PageImpl<>(merchants, PageRequest.of(0, 10), 2);

            when(merchantRepository.findByStatus(eq("pending"), any(Pageable.class))).thenReturn(merchantPage);

            var result = performGet("/api/admin/dashboard/pending-merchants");

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.data.length()").value(2));

            verify(merchantRepository).findByStatus(eq("pending"), any(Pageable.class));
        }

        @Test
        @DisplayName("成功获取空待审核商家列表")
        void testGetPendingMerchants_EmptyList() throws Exception {
            Page<Merchant> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

            when(merchantRepository.findByStatus(eq("pending"), any(Pageable.class))).thenReturn(emptyPage);

            var result = performGet("/api/admin/dashboard/pending-merchants");

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.data.length()").value(0));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetPendingMerchants_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/dashboard/pending-merchants")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(merchantRepository, never()).findByStatus(anyString(), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("GET /api/admin/dashboard/announcements - 获取公告列表")
    class GetAnnouncementsTests {

        @Test
        @DisplayName("成功获取公告列表")
        void testGetAnnouncements_Success() throws Exception {
            Announcement announcement1 = new Announcement();
            announcement1.setId(1);
            announcement1.setTitle("公告1");
            Announcement announcement2 = new Announcement();
            announcement2.setId(2);
            announcement2.setTitle("公告2");
            List<Announcement> announcements = Arrays.asList(announcement1, announcement2);
            Page<Announcement> announcementPage = new PageImpl<>(announcements, PageRequest.of(0, 10), 2);

            when(announcementRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(announcementPage);

            var result = performGet("/api/admin/dashboard/announcements");

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.data.length()").value(2));

            verify(announcementRepository).findAllByOrderByCreatedAtDesc(any(Pageable.class));
        }

        @Test
        @DisplayName("成功获取空公告列表")
        void testGetAnnouncements_EmptyList() throws Exception {
            Page<Announcement> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

            when(announcementRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(emptyPage);

            var result = performGet("/api/admin/dashboard/announcements");

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.data.length()").value(0));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetAnnouncements_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/dashboard/announcements")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(announcementRepository, never()).findAllByOrderByCreatedAtDesc(any(Pageable.class));
        }
    }
}
