package com.petshop.controller.api;

import com.petshop.entity.Appointment;
import com.petshop.entity.Review;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("商家统计API测试")
public class MerchantApiControllerStatsTest extends MerchantApiControllerTestBase {

    private MerchantApiController controller;

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        controller = new MerchantApiController();
        injectDependencies();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private void injectDependencies() {
        try {
            setField(controller, "merchantStatsService", merchantStatsService);
            setField(controller, "merchantService", merchantService);
            setField(controller, "serviceService", serviceService);
            setField(controller, "productService", productService);
            setField(controller, "categoryService", categoryService);
            setField(controller, "appointmentService", appointmentService);
            setField(controller, "productOrderService", productOrderService);
            setField(controller, "reviewService", reviewService);
            setField(controller, "merchantSettingsService", merchantSettingsService);
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
    class GetDashboardStatsTests {

        @Test
        @DisplayName("成功获取首页统计数据")
        void testGetDashboardStats_Success() throws Exception {
            mockMerchantSession();

            Map<String, Object> dashboardStats = new HashMap<>();
            dashboardStats.put("todayAppointments", 10);
            dashboardStats.put("pendingAppointments", 3L);
            dashboardStats.put("todayRevenue", new BigDecimal("1500.00"));
            dashboardStats.put("avgRating", 4.5);
            dashboardStats.put("totalReviews", 50L);

            List<Appointment> recentAppointments = Arrays.asList(
                    TestDataFactory.createAppointment(1, testMerchant),
                    TestDataFactory.createAppointment(2, testMerchant)
            );

            List<Review> recentReviews = Arrays.asList(
                    TestDataFactory.createReview(1, testMerchant),
                    TestDataFactory.createReview(2, testMerchant)
            );

            when(merchantStatsService.getDashboardStats(testMerchantId)).thenReturn(dashboardStats);
            when(merchantStatsService.getRecentAppointments(eq(testMerchantId), eq(5))).thenReturn(recentAppointments);
            when(merchantStatsService.getRecentReviews(eq(testMerchantId), eq(5))).thenReturn(recentReviews);

            mockMvc.perform(get("/api/merchant/dashboard")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.todayAppointments").value(10))
                    .andExpect(jsonPath("$.data.pendingAppointments").value(3))
                    .andExpect(jsonPath("$.data.avgRating").value(4.5))
                    .andExpect(jsonPath("$.data.totalReviews").value(50))
                    .andExpect(jsonPath("$.data.recentAppointments").isArray())
                    .andExpect(jsonPath("$.data.recentReviews").isArray());

            verify(merchantStatsService).getDashboardStats(testMerchantId);
            verify(merchantStatsService).getRecentAppointments(testMerchantId, 5);
            verify(merchantStatsService).getRecentReviews(testMerchantId, 5);
        }

        @Test
        @DisplayName("无数据情况")
        void testGetDashboardStats_NoData() throws Exception {
            mockMerchantSession();

            Map<String, Object> emptyStats = new HashMap<>();
            emptyStats.put("todayAppointments", 0);
            emptyStats.put("pendingAppointments", 0L);
            emptyStats.put("todayRevenue", BigDecimal.ZERO);
            emptyStats.put("avgRating", 0.0);
            emptyStats.put("totalReviews", 0L);

            when(merchantStatsService.getDashboardStats(testMerchantId)).thenReturn(emptyStats);
            when(merchantStatsService.getRecentAppointments(eq(testMerchantId), eq(5))).thenReturn(Collections.emptyList());
            when(merchantStatsService.getRecentReviews(eq(testMerchantId), eq(5))).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/merchant/dashboard")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.todayAppointments").value(0))
                    .andExpect(jsonPath("$.data.pendingAppointments").value(0))
                    .andExpect(jsonPath("$.data.todayRevenue").value(0))
                    .andExpect(jsonPath("$.data.avgRating").value(0.0))
                    .andExpect(jsonPath("$.data.totalReviews").value(0))
                    .andExpect(jsonPath("$.data.recentAppointments").isEmpty())
                    .andExpect(jsonPath("$.data.recentReviews").isEmpty());
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetDashboardStats_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/merchant/dashboard"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));

            verify(merchantStatsService, never()).getDashboardStats(anyInt());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetDashboardStats_ServiceException() throws Exception {
            mockMerchantSession();

            when(merchantStatsService.getDashboardStats(testMerchantId))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            mockMvc.perform(get("/api/merchant/dashboard")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("获取首页统计数据失败：数据库连接失败"));
        }
    }

    @Nested
    @DisplayName("获取营收统计测试")
    class GetRevenueStatsTests {

        @Test
        @DisplayName("成功获取营收统计")
        void testGetRevenueStats_Success() throws Exception {
            mockMerchantSession();

            Map<String, Object> revenueStats = new HashMap<>();
            revenueStats.put("totalRevenue", new BigDecimal("50000.00"));
            revenueStats.put("serviceRevenue", new BigDecimal("30000.00"));
            revenueStats.put("productRevenue", new BigDecimal("20000.00"));
            revenueStats.put("serviceOrderCount", 150L);
            revenueStats.put("productOrderCount", 80L);

            Map<LocalDate, BigDecimal> dailyRevenue = new TreeMap<>();
            dailyRevenue.put(LocalDate.now().minusDays(2), new BigDecimal("1000.00"));
            dailyRevenue.put(LocalDate.now().minusDays(1), new BigDecimal("1500.00"));
            dailyRevenue.put(LocalDate.now(), new BigDecimal("2000.00"));
            revenueStats.put("dailyRevenue", dailyRevenue);

            when(merchantStatsService.getRevenueStats(eq(testMerchantId), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(revenueStats);

            mockMvc.perform(get("/api/merchant/revenue-stats")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.totalRevenue").value(50000))
                    .andExpect(jsonPath("$.data.serviceRevenue").value(30000))
                    .andExpect(jsonPath("$.data.productRevenue").value(20000))
                    .andExpect(jsonPath("$.data.serviceOrderCount").value(150))
                    .andExpect(jsonPath("$.data.productOrderCount").value(80));

            verify(merchantStatsService).getRevenueStats(eq(testMerchantId), any(LocalDate.class), any(LocalDate.class));
        }

        @Test
        @DisplayName("日期范围筛选")
        void testGetRevenueStats_WithDateRange() throws Exception {
            mockMerchantSession();

            String startDate = "2024-01-01";
            String endDate = "2024-01-31";

            Map<String, Object> revenueStats = new HashMap<>();
            revenueStats.put("totalRevenue", new BigDecimal("10000.00"));
            revenueStats.put("serviceRevenue", new BigDecimal("6000.00"));
            revenueStats.put("productRevenue", new BigDecimal("4000.00"));
            revenueStats.put("dailyRevenue", new TreeMap<>());

            when(merchantStatsService.getRevenueStats(eq(testMerchantId), eq(LocalDate.parse(startDate)), eq(LocalDate.parse(endDate))))
                    .thenReturn(revenueStats);

            mockMvc.perform(get("/api/merchant/revenue-stats")
                    .param("startDate", startDate)
                    .param("endDate", endDate)
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.totalRevenue").value(10000));

            verify(merchantStatsService).getRevenueStats(testMerchantId, LocalDate.parse(startDate), LocalDate.parse(endDate));
        }

        @Test
        @DisplayName("无数据情况")
        void testGetRevenueStats_NoData() throws Exception {
            mockMerchantSession();

            Map<String, Object> emptyStats = new HashMap<>();
            emptyStats.put("totalRevenue", BigDecimal.ZERO);
            emptyStats.put("serviceRevenue", BigDecimal.ZERO);
            emptyStats.put("productRevenue", BigDecimal.ZERO);
            emptyStats.put("serviceOrderCount", 0L);
            emptyStats.put("productOrderCount", 0L);
            emptyStats.put("dailyRevenue", new TreeMap<>());

            when(merchantStatsService.getRevenueStats(eq(testMerchantId), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(emptyStats);

            mockMvc.perform(get("/api/merchant/revenue-stats")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.totalRevenue").value(0))
                    .andExpect(jsonPath("$.data.serviceOrderCount").value(0));
        }

        @Test
        @DisplayName("无效日期格式")
        void testGetRevenueStats_InvalidDateFormat() throws Exception {
            mockMerchantSession();

            mockMvc.perform(get("/api/merchant/revenue-stats")
                    .param("startDate", "invalid-date")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetRevenueStats_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/merchant/revenue-stats"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));

            verify(merchantStatsService, never()).getRevenueStats(anyInt(), any(LocalDate.class), any(LocalDate.class));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetRevenueStats_ServiceException() throws Exception {
            mockMerchantSession();

            when(merchantStatsService.getRevenueStats(eq(testMerchantId), any(LocalDate.class), any(LocalDate.class)))
                    .thenThrow(new RuntimeException("统计服务异常"));

            mockMvc.perform(get("/api/merchant/revenue-stats")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("获取营收统计失败：统计服务异常"));
        }
    }

    @Nested
    @DisplayName("导出营收统计测试")
    class ExportRevenueStatsTests {

        @Test
        @DisplayName("成功导出营收统计")
        void testExportRevenueStats_Success() throws Exception {
            mockMerchantSession();

            List<Map<String, Object>> exportData = new ArrayList<>();
            Map<String, Object> row1 = new HashMap<>();
            row1.put("date", "2024-01-01");
            row1.put("revenue", new BigDecimal("1000.00"));
            exportData.add(row1);

            Map<String, Object> row2 = new HashMap<>();
            row2.put("date", "2024-01-02");
            row2.put("revenue", new BigDecimal("1500.00"));
            exportData.add(row2);

            when(merchantStatsService.getRevenueStatsForExport(eq(testMerchantId), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(exportData);

            mockMvc.perform(get("/api/merchant/revenue-stats/export")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("导出成功"))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2));

            verify(merchantStatsService).getRevenueStatsForExport(eq(testMerchantId), any(LocalDate.class), any(LocalDate.class));
        }

        @Test
        @DisplayName("导出空数据")
        void testExportRevenueStats_EmptyData() throws Exception {
            mockMerchantSession();

            when(merchantStatsService.getRevenueStatsForExport(eq(testMerchantId), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/merchant/revenue-stats/export")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @Test
        @DisplayName("带日期范围导出")
        void testExportRevenueStats_WithDateRange() throws Exception {
            mockMerchantSession();

            String startDate = "2024-01-01";
            String endDate = "2024-01-31";

            List<Map<String, Object>> exportData = new ArrayList<>();
            Map<String, Object> row = new HashMap<>();
            row.put("date", "2024-01-15");
            row.put("revenue", new BigDecimal("2000.00"));
            exportData.add(row);

            when(merchantStatsService.getRevenueStatsForExport(eq(testMerchantId), eq(LocalDate.parse(startDate)), eq(LocalDate.parse(endDate))))
                    .thenReturn(exportData);

            mockMvc.perform(get("/api/merchant/revenue-stats/export")
                    .param("startDate", startDate)
                    .param("endDate", endDate)
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.length()").value(1));

            verify(merchantStatsService).getRevenueStatsForExport(testMerchantId, LocalDate.parse(startDate), LocalDate.parse(endDate));
        }

        @Test
        @DisplayName("未登录返回401")
        void testExportRevenueStats_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/merchant/revenue-stats/export"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(merchantStatsService, never()).getRevenueStatsForExport(anyInt(), any(LocalDate.class), any(LocalDate.class));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testExportRevenueStats_ServiceException() throws Exception {
            mockMerchantSession();

            when(merchantStatsService.getRevenueStatsForExport(eq(testMerchantId), any(LocalDate.class), any(LocalDate.class)))
                    .thenThrow(new RuntimeException("导出失败"));

            mockMvc.perform(get("/api/merchant/revenue-stats/export")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("导出营收统计失败：导出失败"));
        }
    }

    @Nested
    @DisplayName("获取预约统计测试")
    class GetAppointmentStatsTests {

        @Test
        @DisplayName("成功获取预约统计")
        void testGetAppointmentStats_Success() throws Exception {
            mockMerchantSession();

            Map<String, Object> appointmentStats = new HashMap<>();
            appointmentStats.put("totalAppointments", 100);
            appointmentStats.put("completedCount", 80L);
            appointmentStats.put("cancelledCount", 10L);
            appointmentStats.put("pendingCount", 5L);
            appointmentStats.put("confirmedCount", 5L);
            appointmentStats.put("cancelRate", 10.0);
            appointmentStats.put("completeRate", 80.0);
            appointmentStats.put("dailyStats", new TreeMap<>());
            appointmentStats.put("hourlyDistribution", new TreeMap<>());

            when(merchantStatsService.getAppointmentStats(eq(testMerchantId), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(appointmentStats);

            mockMvc.perform(get("/api/merchant/appointment-stats")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.totalAppointments").value(100))
                    .andExpect(jsonPath("$.data.completedCount").value(80))
                    .andExpect(jsonPath("$.data.cancelledCount").value(10))
                    .andExpect(jsonPath("$.data.pendingCount").value(5))
                    .andExpect(jsonPath("$.data.confirmedCount").value(5))
                    .andExpect(jsonPath("$.data.cancelRate").value(10.0))
                    .andExpect(jsonPath("$.data.completeRate").value(80.0));

            verify(merchantStatsService).getAppointmentStats(eq(testMerchantId), any(LocalDate.class), any(LocalDate.class));
        }

        @Test
        @DisplayName("日期范围筛选")
        void testGetAppointmentStats_WithDateRange() throws Exception {
            mockMerchantSession();

            String startDate = "2024-02-01";
            String endDate = "2024-02-28";

            Map<String, Object> appointmentStats = new HashMap<>();
            appointmentStats.put("totalAppointments", 50);
            appointmentStats.put("completedCount", 40L);
            appointmentStats.put("cancelledCount", 5L);
            appointmentStats.put("pendingCount", 3L);
            appointmentStats.put("confirmedCount", 2L);
            appointmentStats.put("cancelRate", 10.0);
            appointmentStats.put("completeRate", 80.0);
            appointmentStats.put("dailyStats", new TreeMap<>());
            appointmentStats.put("hourlyDistribution", new TreeMap<>());

            when(merchantStatsService.getAppointmentStats(eq(testMerchantId), eq(LocalDate.parse(startDate)), eq(LocalDate.parse(endDate))))
                    .thenReturn(appointmentStats);

            mockMvc.perform(get("/api/merchant/appointment-stats")
                    .param("startDate", startDate)
                    .param("endDate", endDate)
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.totalAppointments").value(50));

            verify(merchantStatsService).getAppointmentStats(testMerchantId, LocalDate.parse(startDate), LocalDate.parse(endDate));
        }

        @Test
        @DisplayName("无数据情况")
        void testGetAppointmentStats_NoData() throws Exception {
            mockMerchantSession();

            Map<String, Object> emptyStats = new HashMap<>();
            emptyStats.put("totalAppointments", 0);
            emptyStats.put("completedCount", 0L);
            emptyStats.put("cancelledCount", 0L);
            emptyStats.put("pendingCount", 0L);
            emptyStats.put("confirmedCount", 0L);
            emptyStats.put("cancelRate", 0.0);
            emptyStats.put("completeRate", 0.0);
            emptyStats.put("dailyStats", new TreeMap<>());
            emptyStats.put("hourlyDistribution", new TreeMap<>());

            when(merchantStatsService.getAppointmentStats(eq(testMerchantId), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(emptyStats);

            mockMvc.perform(get("/api/merchant/appointment-stats")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.totalAppointments").value(0))
                    .andExpect(jsonPath("$.data.cancelRate").value(0.0));
        }

        @Test
        @DisplayName("无效日期格式")
        void testGetAppointmentStats_InvalidDateFormat() throws Exception {
            mockMerchantSession();

            mockMvc.perform(get("/api/merchant/appointment-stats")
                    .param("startDate", "2024/01/01")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetAppointmentStats_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/merchant/appointment-stats"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));

            verify(merchantStatsService, never()).getAppointmentStats(anyInt(), any(LocalDate.class), any(LocalDate.class));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetAppointmentStats_ServiceException() throws Exception {
            mockMerchantSession();

            when(merchantStatsService.getAppointmentStats(eq(testMerchantId), any(LocalDate.class), any(LocalDate.class)))
                    .thenThrow(new RuntimeException("预约统计服务异常"));

            mockMvc.perform(get("/api/merchant/appointment-stats")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("获取预约统计失败：预约统计服务异常"));
        }
    }

    @Nested
    @DisplayName("导出预约统计测试")
    class ExportAppointmentStatsTests {

        @Test
        @DisplayName("成功导出预约统计")
        void testExportAppointmentStats_Success() throws Exception {
            mockMerchantSession();

            List<Map<String, Object>> exportData = new ArrayList<>();
            Map<String, Object> row1 = new HashMap<>();
            row1.put("date", "2024-01-01");
            row1.put("total", 10L);
            row1.put("completed", 8L);
            row1.put("cancelled", 2L);
            exportData.add(row1);

            Map<String, Object> row2 = new HashMap<>();
            row2.put("date", "2024-01-02");
            row2.put("total", 15L);
            row2.put("completed", 12L);
            row2.put("cancelled", 3L);
            exportData.add(row2);

            when(merchantStatsService.getAppointmentStatsForExport(eq(testMerchantId), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(exportData);

            mockMvc.perform(get("/api/merchant/appointment-stats/export")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("导出成功"))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2));

            verify(merchantStatsService).getAppointmentStatsForExport(eq(testMerchantId), any(LocalDate.class), any(LocalDate.class));
        }

        @Test
        @DisplayName("导出空数据")
        void testExportAppointmentStats_EmptyData() throws Exception {
            mockMerchantSession();

            when(merchantStatsService.getAppointmentStatsForExport(eq(testMerchantId), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/merchant/appointment-stats/export")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @Test
        @DisplayName("带日期范围导出")
        void testExportAppointmentStats_WithDateRange() throws Exception {
            mockMerchantSession();

            String startDate = "2024-03-01";
            String endDate = "2024-03-31";

            List<Map<String, Object>> exportData = new ArrayList<>();
            Map<String, Object> row = new HashMap<>();
            row.put("date", "2024-03-15");
            row.put("total", 20L);
            row.put("completed", 18L);
            row.put("cancelled", 2L);
            exportData.add(row);

            when(merchantStatsService.getAppointmentStatsForExport(eq(testMerchantId), eq(LocalDate.parse(startDate)), eq(LocalDate.parse(endDate))))
                    .thenReturn(exportData);

            mockMvc.perform(get("/api/merchant/appointment-stats/export")
                    .param("startDate", startDate)
                    .param("endDate", endDate)
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.length()").value(1));

            verify(merchantStatsService).getAppointmentStatsForExport(testMerchantId, LocalDate.parse(startDate), LocalDate.parse(endDate));
        }

        @Test
        @DisplayName("未登录返回401")
        void testExportAppointmentStats_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/merchant/appointment-stats/export"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(merchantStatsService, never()).getAppointmentStatsForExport(anyInt(), any(LocalDate.class), any(LocalDate.class));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testExportAppointmentStats_ServiceException() throws Exception {
            mockMerchantSession();

            when(merchantStatsService.getAppointmentStatsForExport(eq(testMerchantId), any(LocalDate.class), any(LocalDate.class)))
                    .thenThrow(new RuntimeException("导出预约统计失败"));

            mockMvc.perform(get("/api/merchant/appointment-stats/export")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("导出预约统计失败：导出预约统计失败"));
        }
    }
}
