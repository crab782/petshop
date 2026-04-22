package com.petshop.controller.api;

import com.petshop.entity.Announcement;
import com.petshop.entity.Merchant;
import com.petshop.entity.User;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "userMapper", userMapper);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "merchantMapper", merchantMapper);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "appointmentMapper", appointmentMapper);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "serviceMapper", serviceMapper);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "announcementMapper", announcementMapper);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "announcementService", announcementService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("GET /api/admin/dashboard - 获取仪表盘统计")
    class GetDashboardStatsTests {

        @Test
        @DisplayName("成功获取仪表盘统计数据")
        void testGetDashboardStats_Success() throws Exception {
            when(userMapper.selectCount(null)).thenReturn(100L);
            when(merchantMapper.selectCount(null)).thenReturn(50L);
            when(appointmentMapper.selectCount(null)).thenReturn(500L);
            when(serviceMapper.selectCount(null)).thenReturn(200L);

            var result = performGet("/api/admin/dashboard");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.userCount").value(100))
                    .andExpect(jsonPath("$.data.merchantCount").value(50))
                    .andExpect(jsonPath("$.data.orderCount").value(500))
                    .andExpect(jsonPath("$.data.serviceCount").value(200));

            verify(userMapper).selectCount(null);
            verify(merchantMapper).selectCount(null);
            verify(appointmentMapper).selectCount(null);
            verify(serviceMapper).selectCount(null);
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

            verify(userMapper, never()).selectCount(null);
        }

        @Test
        @DisplayName("零数据统计")
        void testGetDashboardStats_ZeroCounts() throws Exception {
            when(userMapper.selectCount(null)).thenReturn(0L);
            when(merchantMapper.selectCount(null)).thenReturn(0L);
            when(appointmentMapper.selectCount(null)).thenReturn(0L);
            when(serviceMapper.selectCount(null)).thenReturn(0L);

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
            Page<User> userPage = new Page<>(0, 10);
            userPage.setRecords(users);
            userPage.setTotal(2);

            when(userService.findAll(any(Pageable.class))).thenReturn(userPage);

            var result = performGet("/api/admin/dashboard/recent-users");

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.data.length()").value(2));

            verify(userService).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("成功获取空用户列表")
        void testGetRecentUsers_EmptyList() throws Exception {
            Page<User> emptyPage = new Page<>(0, 10);
            emptyPage.setRecords(Collections.emptyList());
            emptyPage.setTotal(0);

            when(userService.findAll(any(Pageable.class))).thenReturn(emptyPage);

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

            verify(userService, never()).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("分页参数正确传递")
        void testGetRecentUsers_Pagination() throws Exception {
            Page<User> userPage = new Page<>(1, 20);
            userPage.setRecords(Collections.emptyList());
            userPage.setTotal(0);

            when(userService.findAll(any(Pageable.class))).thenReturn(userPage);

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
            Page<Merchant> merchantPage = new Page<>(0, 10);
            merchantPage.setRecords(merchants);
            merchantPage.setTotal(2);

            when(merchantService.getPendingMerchants(anyString(), anyInt(), anyInt())).thenReturn(merchantPage);

            var result = performGet("/api/admin/dashboard/pending-merchants");

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.data.length()").value(2));

            verify(merchantService).getPendingMerchants(anyString(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("成功获取空待审核商家列表")
        void testGetPendingMerchants_EmptyList() throws Exception {
            Page<Merchant> emptyPage = new Page<>(0, 10);
            emptyPage.setRecords(Collections.emptyList());
            emptyPage.setTotal(0);

            when(merchantService.getPendingMerchants(anyString(), anyInt(), anyInt())).thenReturn(emptyPage);

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

            verify(merchantService, never()).getPendingMerchants(anyString(), anyInt(), anyInt());
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
            org.springframework.data.domain.Page<Announcement> announcementPage = new PageImpl<>(announcements, PageRequest.of(0, 10), 2);

            when(announcementService.findAll(any(Pageable.class))).thenReturn(announcementPage);

            var result = performGet("/api/admin/dashboard/announcements");

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.data.length()").value(2));

            verify(announcementService).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("成功获取空公告列表")
        void testGetAnnouncements_EmptyList() throws Exception {
            org.springframework.data.domain.Page<Announcement> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

            when(announcementService.findAll(any(Pageable.class))).thenReturn(emptyPage);

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

            verify(announcementService, never()).findAll(any(Pageable.class));
        }
    }
}
