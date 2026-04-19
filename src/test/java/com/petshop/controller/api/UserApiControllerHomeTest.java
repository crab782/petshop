package com.petshop.controller.api;

import com.petshop.dto.ActivityDTO;
import com.petshop.dto.HomeStatsDTO;
import com.petshop.entity.Appointment;
import com.petshop.entity.Merchant;
import com.petshop.entity.ProductOrder;
import com.petshop.entity.Review;
import com.petshop.entity.Service;
import com.petshop.entity.User;
import com.petshop.exception.GlobalExceptionHandler;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("用户首页统计API测试")
public class UserApiControllerHomeTest extends UserApiControllerTestBase {

    private UserApiController controller;

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        controller = new UserApiController();
        injectDependencies();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private void injectDependencies() {
        try {
            setField(controller, "userHomeService", userHomeService);
            setField(controller, "userService", userService);
            setField(controller, "petService", petService);
            setField(controller, "appointmentService", appointmentService);
            setField(controller, "serviceService", serviceService);
            setField(controller, "productOrderService", productOrderService);
        } catch (Exception e) {
            throw new RuntimeException("注入依赖失败", e);
        }
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = findField(target.getClass(), fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private java.lang.reflect.Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    @Nested
    @DisplayName("获取首页统计数据测试")
    class GetHomeStatsTests {

        @Test
        @DisplayName("成功获取首页统计数据")
        void testGetHomeStats_Success() throws Exception {
            mockUserSession();

            HomeStatsDTO stats = new HomeStatsDTO(3L, 2L, 5L);

            when(userHomeService.getHomeStats(testUserId)).thenReturn(stats);

            mockMvc.perform(get("/api/user/home/stats")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.petCount").value(3))
                    .andExpect(jsonPath("$.data.pendingAppointments").value(2))
                    .andExpect(jsonPath("$.data.reviewCount").value(5));

            verify(userHomeService).getHomeStats(testUserId);
        }

        @Test
        @DisplayName("获取统计数据 - 无数据情况")
        void testGetHomeStats_NoData() throws Exception {
            mockUserSession();

            HomeStatsDTO emptyStats = new HomeStatsDTO(0L, 0L, 0L);

            when(userHomeService.getHomeStats(testUserId)).thenReturn(emptyStats);

            mockMvc.perform(get("/api/user/home/stats")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.petCount").value(0))
                    .andExpect(jsonPath("$.data.pendingAppointments").value(0))
                    .andExpect(jsonPath("$.data.reviewCount").value(0));

            verify(userHomeService).getHomeStats(testUserId);
        }

        @Test
        @DisplayName("获取统计数据 - 有宠物和预约")
        void testGetHomeStats_WithPetsAndAppointments() throws Exception {
            mockUserSession();

            HomeStatsDTO stats = new HomeStatsDTO(5L, 10L, 8L);

            when(userHomeService.getHomeStats(testUserId)).thenReturn(stats);

            mockMvc.perform(get("/api/user/home/stats")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.petCount").value(5))
                    .andExpect(jsonPath("$.data.pendingAppointments").value(10))
                    .andExpect(jsonPath("$.data.reviewCount").value(8));

            verify(userHomeService).getHomeStats(testUserId);
        }

        @Test
        @DisplayName("获取统计数据 - 大量数据")
        void testGetHomeStats_LargeNumbers() throws Exception {
            mockUserSession();

            HomeStatsDTO stats = new HomeStatsDTO(100L, 50L, 200L);

            when(userHomeService.getHomeStats(testUserId)).thenReturn(stats);

            mockMvc.perform(get("/api/user/home/stats")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.petCount").value(100))
                    .andExpect(jsonPath("$.data.pendingAppointments").value(50))
                    .andExpect(jsonPath("$.data.reviewCount").value(200));

            verify(userHomeService).getHomeStats(testUserId);
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetHomeStats_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/user/home/stats"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));

            verify(userHomeService, never()).getHomeStats(anyInt());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetHomeStats_ServiceException() throws Exception {
            mockUserSession();

            when(userHomeService.getHomeStats(testUserId))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            mockMvc.perform(get("/api/user/home/stats")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }

    @Nested
    @DisplayName("获取最近活动测试")
    class GetRecentActivitiesTests {

        @Test
        @DisplayName("成功获取最近活动")
        void testGetRecentActivities_Success() throws Exception {
            mockUserSession();

            List<ActivityDTO> activities = createTestActivities(5);

            when(userHomeService.getRecentActivities(eq(testUserId), eq(5))).thenReturn(activities);

            mockMvc.perform(get("/api/user/home/activities")
                    .param("limit", "5")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(5));

            verify(userHomeService).getRecentActivities(testUserId, 5);
        }

        @Test
        @DisplayName("获取活动 - 默认limit")
        void testGetRecentActivities_DefaultLimit() throws Exception {
            mockUserSession();

            List<ActivityDTO> activities = createTestActivities(10);

            when(userHomeService.getRecentActivities(eq(testUserId), eq(10))).thenReturn(activities);

            mockMvc.perform(get("/api/user/home/activities")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray());

            verify(userHomeService).getRecentActivities(testUserId, 10);
        }

        @Test
        @DisplayName("获取活动 - 自定义limit")
        void testGetRecentActivities_CustomLimit() throws Exception {
            mockUserSession();

            List<ActivityDTO> activities = createTestActivities(3);

            when(userHomeService.getRecentActivities(eq(testUserId), eq(3))).thenReturn(activities);

            mockMvc.perform(get("/api/user/home/activities")
                    .param("limit", "3")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.length()").value(3));

            verify(userHomeService).getRecentActivities(testUserId, 3);
        }

        @Test
        @DisplayName("获取活动 - 无活动数据")
        void testGetRecentActivities_NoActivities() throws Exception {
            mockUserSession();

            when(userHomeService.getRecentActivities(eq(testUserId), anyInt()))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/user/home/activities")
                    .param("limit", "5")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data").isEmpty());

            verify(userHomeService).getRecentActivities(testUserId, 5);
        }

        @Test
        @DisplayName("活动按时间倒序排列")
        void testGetRecentActivities_OrderedByTimeDesc() throws Exception {
            mockUserSession();

            List<ActivityDTO> activities = new ArrayList<>();
            activities.add(ActivityDTO.builder()
                    .id(1)
                    .type("appointment")
                    .title("预约服务: 宠物美容")
                    .time("2024-01-15 14:30")
                    .status("待处理")
                    .statusColor("orange")
                    .relatedId(1)
                    .build());
            activities.add(ActivityDTO.builder()
                    .id(2)
                    .type("review")
                    .title("评价服务: 宠物洗澡")
                    .time("2024-01-14 10:00")
                    .status("已评价")
                    .statusColor("green")
                    .relatedId(2)
                    .build());
            activities.add(ActivityDTO.builder()
                    .id(3)
                    .type("order")
                    .title("商品订单 #3")
                    .time("2024-01-13 09:00")
                    .status("已完成")
                    .statusColor("green")
                    .relatedId(3)
                    .build());

            when(userHomeService.getRecentActivities(eq(testUserId), eq(5))).thenReturn(activities);

            mockMvc.perform(get("/api/user/home/activities")
                    .param("limit", "5")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data[0].time").value("2024-01-15 14:30"))
                    .andExpect(jsonPath("$.data[1].time").value("2024-01-14 10:00"))
                    .andExpect(jsonPath("$.data[2].time").value("2024-01-13 09:00"));

            verify(userHomeService).getRecentActivities(testUserId, 5);
        }

        @Test
        @DisplayName("活动包含不同类型")
        void testGetRecentActivities_DifferentTypes() throws Exception {
            mockUserSession();

            List<ActivityDTO> activities = new ArrayList<>();
            activities.add(ActivityDTO.builder()
                    .id(1)
                    .type("appointment")
                    .title("预约服务: 宠物美容")
                    .time("2024-01-15 14:30")
                    .status("待处理")
                    .statusColor("orange")
                    .relatedId(1)
                    .build());
            activities.add(ActivityDTO.builder()
                    .id(2)
                    .type("review")
                    .title("评价服务: 宠物洗澡")
                    .time("2024-01-14 10:00")
                    .status("已评价")
                    .statusColor("green")
                    .relatedId(2)
                    .build());
            activities.add(ActivityDTO.builder()
                    .id(3)
                    .type("order")
                    .title("商品订单 #3")
                    .time("2024-01-13 09:00")
                    .status("已完成")
                    .statusColor("green")
                    .relatedId(3)
                    .build());

            when(userHomeService.getRecentActivities(eq(testUserId), eq(5))).thenReturn(activities);

            mockMvc.perform(get("/api/user/home/activities")
                    .param("limit", "5")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data[0].type").value("appointment"))
                    .andExpect(jsonPath("$.data[1].type").value("review"))
                    .andExpect(jsonPath("$.data[2].type").value("order"));

            verify(userHomeService).getRecentActivities(testUserId, 5);
        }

        @Test
        @DisplayName("活动状态颜色正确")
        void testGetRecentActivities_StatusColors() throws Exception {
            mockUserSession();

            List<ActivityDTO> activities = new ArrayList<>();
            activities.add(ActivityDTO.builder()
                    .id(1)
                    .type("appointment")
                    .title("预约服务: 宠物美容")
                    .time("2024-01-15 14:30")
                    .status("待处理")
                    .statusColor("orange")
                    .relatedId(1)
                    .build());
            activities.add(ActivityDTO.builder()
                    .id(2)
                    .type("appointment")
                    .title("预约服务: 宠物洗澡")
                    .time("2024-01-14 10:00")
                    .status("已确认")
                    .statusColor("blue")
                    .relatedId(2)
                    .build());
            activities.add(ActivityDTO.builder()
                    .id(3)
                    .type("appointment")
                    .title("预约服务: 宠物剪毛")
                    .time("2024-01-13 09:00")
                    .status("已完成")
                    .statusColor("green")
                    .relatedId(3)
                    .build());
            activities.add(ActivityDTO.builder()
                    .id(4)
                    .type("appointment")
                    .title("预约服务: 宠物体检")
                    .time("2024-01-12 09:00")
                    .status("已取消")
                    .statusColor("grey")
                    .relatedId(4)
                    .build());

            when(userHomeService.getRecentActivities(eq(testUserId), eq(5))).thenReturn(activities);

            mockMvc.perform(get("/api/user/home/activities")
                    .param("limit", "5")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data[0].statusColor").value("orange"))
                    .andExpect(jsonPath("$.data[1].statusColor").value("blue"))
                    .andExpect(jsonPath("$.data[2].statusColor").value("green"))
                    .andExpect(jsonPath("$.data[3].statusColor").value("grey"));

            verify(userHomeService).getRecentActivities(testUserId, 5);
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetRecentActivities_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/user/home/activities")
                    .param("limit", "5"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));

            verify(userHomeService, never()).getRecentActivities(anyInt(), anyInt());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetRecentActivities_ServiceException() throws Exception {
            mockUserSession();

            when(userHomeService.getRecentActivities(eq(testUserId), anyInt()))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            mockMvc.perform(get("/api/user/home/activities")
                    .param("limit", "5")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }

    @Nested
    @DisplayName("不同用户数据隔离测试")
    class UserDataIsolationTests {

        @Test
        @DisplayName("用户A获取自己的统计数据")
        void testGetHomeStats_UserA() throws Exception {
            User userA = TestDataFactory.createUser(100, "用户A", "userA@test.com");
            mockUserSession(userA);

            HomeStatsDTO statsA = new HomeStatsDTO(2L, 1L, 3L);

            when(userHomeService.getHomeStats(100)).thenReturn(statsA);

            mockMvc.perform(get("/api/user/home/stats")
                    .sessionAttr("user", userA)
                    .sessionAttr("userId", 100))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.petCount").value(2))
                    .andExpect(jsonPath("$.data.pendingAppointments").value(1))
                    .andExpect(jsonPath("$.data.reviewCount").value(3));

            verify(userHomeService).getHomeStats(100);
        }

        @Test
        @DisplayName("用户B获取自己的统计数据")
        void testGetHomeStats_UserB() throws Exception {
            User userB = TestDataFactory.createUser(200, "用户B", "userB@test.com");
            mockUserSession(userB);

            HomeStatsDTO statsB = new HomeStatsDTO(5L, 3L, 10L);

            when(userHomeService.getHomeStats(200)).thenReturn(statsB);

            mockMvc.perform(get("/api/user/home/stats")
                    .sessionAttr("user", userB)
                    .sessionAttr("userId", 200))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.petCount").value(5))
                    .andExpect(jsonPath("$.data.pendingAppointments").value(3))
                    .andExpect(jsonPath("$.data.reviewCount").value(10));

            verify(userHomeService).getHomeStats(200);
        }

        @Test
        @DisplayName("用户A获取自己的活动数据")
        void testGetRecentActivities_UserA() throws Exception {
            User userA = TestDataFactory.createUser(100, "用户A", "userA@test.com");
            mockUserSession(userA);

            List<ActivityDTO> activitiesA = new ArrayList<>();
            activitiesA.add(ActivityDTO.builder()
                    .id(1)
                    .type("appointment")
                    .title("用户A的预约")
                    .time("2024-01-15 14:30")
                    .status("待处理")
                    .statusColor("orange")
                    .relatedId(1)
                    .build());

            when(userHomeService.getRecentActivities(eq(100), anyInt())).thenReturn(activitiesA);

            mockMvc.perform(get("/api/user/home/activities")
                    .param("limit", "5")
                    .sessionAttr("user", userA)
                    .sessionAttr("userId", 100))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data[0].title").value("用户A的预约"));

            verify(userHomeService).getRecentActivities(eq(100), anyInt());
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("limit为0")
        void testGetRecentActivities_LimitZero() throws Exception {
            mockUserSession();

            when(userHomeService.getRecentActivities(eq(testUserId), eq(0)))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/user/home/activities")
                    .param("limit", "0")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isEmpty());

            verify(userHomeService).getRecentActivities(testUserId, 0);
        }

        @Test
        @DisplayName("limit为负数")
        void testGetRecentActivities_NegativeLimit() throws Exception {
            mockUserSession();

            when(userHomeService.getRecentActivities(eq(testUserId), eq(-1)))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/user/home/activities")
                    .param("limit", "-1")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(userHomeService).getRecentActivities(testUserId, -1);
        }

        @Test
        @DisplayName("limit为大数值")
        void testGetRecentActivities_LargeLimit() throws Exception {
            mockUserSession();

            List<ActivityDTO> activities = createTestActivities(100);

            when(userHomeService.getRecentActivities(eq(testUserId), eq(100))).thenReturn(activities);

            mockMvc.perform(get("/api/user/home/activities")
                    .param("limit", "100")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.length()").value(100));

            verify(userHomeService).getRecentActivities(testUserId, 100);
        }

        @Test
        @DisplayName("无效limit参数返回500")
        void testGetRecentActivities_InvalidLimit() throws Exception {
            mockUserSession();

            mockMvc.perform(get("/api/user/home/activities")
                    .param("limit", "invalid")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }

    private List<ActivityDTO> createTestActivities(int count) {
        List<ActivityDTO> activities = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String type = switch (i % 3) {
                case 0 -> "appointment";
                case 1 -> "review";
                default -> "order";
            };
            String status = switch (i % 4) {
                case 0 -> "待处理";
                case 1 -> "已确认";
                case 2 -> "已完成";
                default -> "已取消";
            };
            String statusColor = switch (i % 4) {
                case 0 -> "orange";
                case 1 -> "blue";
                case 2 -> "green";
                default -> "grey";
            };
            
            activities.add(ActivityDTO.builder()
                    .id(i)
                    .type(type)
                    .title("活动标题 " + i)
                    .time(String.format("2024-01-%02d 10:00", i))
                    .status(status)
                    .statusColor(statusColor)
                    .relatedId(i)
                    .build());
        }
        return activities;
    }
}
