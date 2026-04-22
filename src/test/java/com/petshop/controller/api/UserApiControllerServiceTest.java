package com.petshop.controller.api;

import com.petshop.dto.UserPurchasedServiceDTO;
import com.petshop.entity.Appointment;
import com.petshop.entity.Merchant;
import com.petshop.entity.Pet;
import com.petshop.entity.Service;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("用户服务API测试")
public class UserApiControllerServiceTest extends UserApiControllerTestBase {

    private UserApiController controller;

    @BeforeEach
    public void setupController() {
        controller = new UserApiController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "userService", userService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "petService", petService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "appointmentService", appointmentService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "serviceService", serviceService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "productOrderService", productOrderService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("GET /api/user/services - 获取已购买服务列表")
    class GetPurchasedServicesTests {

        @Test
        @DisplayName("成功获取已购买服务列表")
        void testGetPurchasedServices_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物店A", "shop@test.com");
            Service service1 = TestDataFactory.createService(1, merchant, "宠物洗澡", new BigDecimal("99.00"));
            Service service2 = TestDataFactory.createService(2, merchant, "宠物美容", new BigDecimal("199.00"));
            Pet pet = TestDataFactory.createPet(testUser);

            Appointment appointment1 = createTestAppointment(1, testUser, merchant, service1, pet, "completed", LocalDateTime.now().minusDays(5));
            Appointment appointment2 = createTestAppointment(2, testUser, merchant, service2, pet, "confirmed", LocalDateTime.now().plusDays(1));

            UserPurchasedServiceDTO dto1 = createPurchasedServiceDTO(appointment1, "used");
            UserPurchasedServiceDTO dto2 = createPurchasedServiceDTO(appointment2, "active");

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", Arrays.asList(dto1, dto2));
            resultMap.put("total", 2L);
            resultMap.put("page", 0);
            resultMap.put("pageSize", 10);
            resultMap.put("totalPages", 1);

            when(appointmentService.findPurchasedServices(eq(testUserId), anyString(), anyString(), anyInt(), anyInt()))
                    .thenReturn(resultMap);

            var result = performGet("/api/user/services");

            assertSuccess(result);
            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.content.length()").value(2))
                    .andExpect(jsonPath("$.data.content[0].name").value("宠物洗澡"))
                    .andExpect(jsonPath("$.data.content[0].status").value("used"))
                    .andExpect(jsonPath("$.data.content[1].name").value("宠物美容"))
                    .andExpect(jsonPath("$.data.content[1].status").value("active"));

            verify(appointmentService).findPurchasedServices(eq(testUserId), anyString(), anyString(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("成功获取空服务列表")
        void testGetPurchasedServices_EmptyList() throws Exception {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", Collections.emptyList());
            resultMap.put("total", 0L);
            resultMap.put("page", 0);
            resultMap.put("pageSize", 10);
            resultMap.put("totalPages", 0);

            when(appointmentService.findPurchasedServices(eq(testUserId), anyString(), anyString(), anyInt(), anyInt()))
                    .thenReturn(resultMap);

            var result = performGet("/api/user/services");

            assertSuccess(result);
            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.content.length()").value(0))
                    .andExpect(jsonPath("$.data.totalElements").value(0));

            verify(appointmentService).findPurchasedServices(eq(testUserId), anyString(), anyString(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetPurchasedServices_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/user/services")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(appointmentService, never()).findPurchasedServices(anyInt(), anyString(), anyString(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("按状态筛选 - active状态")
        void testGetPurchasedServices_FilterByActiveStatus() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物店A", "shop@test.com");
            Service service = TestDataFactory.createService(1, merchant, "宠物洗澡", new BigDecimal("99.00"));
            Pet pet = TestDataFactory.createPet(testUser);

            Appointment appointment = createTestAppointment(1, testUser, merchant, service, pet, "confirmed", LocalDateTime.now().plusDays(1));
            UserPurchasedServiceDTO dto = createPurchasedServiceDTO(appointment, "active");

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", Collections.singletonList(dto));
            resultMap.put("total", 1L);
            resultMap.put("page", 0);
            resultMap.put("pageSize", 10);
            resultMap.put("totalPages", 1);

            when(appointmentService.findPurchasedServices(eq(testUserId), anyString(), eq("active"), anyInt(), anyInt()))
                    .thenReturn(resultMap);

            var result = performGet("/api/user/services?status=active");

            assertSuccess(result);
            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.content.length()").value(1))
                    .andExpect(jsonPath("$.data.content[0].status").value("active"));

            verify(appointmentService).findPurchasedServices(eq(testUserId), anyString(), eq("active"), anyInt(), anyInt());
        }

        @Test
        @DisplayName("按状态筛选 - used状态")
        void testGetPurchasedServices_FilterByUsedStatus() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物店A", "shop@test.com");
            Service service = TestDataFactory.createService(1, merchant, "宠物洗澡", new BigDecimal("99.00"));
            Pet pet = TestDataFactory.createPet(testUser);

            Appointment appointment = createTestAppointment(1, testUser, merchant, service, pet, "completed", LocalDateTime.now().minusDays(5));
            UserPurchasedServiceDTO dto = createPurchasedServiceDTO(appointment, "used");

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", Collections.singletonList(dto));
            resultMap.put("total", 1L);
            resultMap.put("page", 0);
            resultMap.put("pageSize", 10);
            resultMap.put("totalPages", 1);

            when(appointmentService.findPurchasedServices(eq(testUserId), anyString(), eq("used"), anyInt(), anyInt()))
                    .thenReturn(resultMap);

            var result = performGet("/api/user/services?status=used");

            assertSuccess(result);
            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.content.length()").value(1))
                    .andExpect(jsonPath("$.data.content[0].status").value("used"));

            verify(appointmentService).findPurchasedServices(eq(testUserId), anyString(), eq("used"), anyInt(), anyInt());
        }

        @Test
        @DisplayName("按状态筛选 - expired状态")
        void testGetPurchasedServices_FilterByExpiredStatus() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物店A", "shop@test.com");
            Service service = TestDataFactory.createService(1, merchant, "宠物洗澡", new BigDecimal("99.00"));
            Pet pet = TestDataFactory.createPet(testUser);

            Appointment appointment = createTestAppointment(1, testUser, merchant, service, pet, "cancelled", LocalDateTime.now().minusDays(10));
            UserPurchasedServiceDTO dto = createPurchasedServiceDTO(appointment, "expired");

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", Collections.singletonList(dto));
            resultMap.put("total", 1L);
            resultMap.put("page", 0);
            resultMap.put("pageSize", 10);
            resultMap.put("totalPages", 1);

            when(appointmentService.findPurchasedServices(eq(testUserId), anyString(), eq("expired"), anyInt(), anyInt()))
                    .thenReturn(resultMap);

            var result = performGet("/api/user/services?status=expired");

            assertSuccess(result);
            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.content.length()").value(1))
                    .andExpect(jsonPath("$.data.content[0].status").value("expired"));

            verify(appointmentService).findPurchasedServices(eq(testUserId), anyString(), eq("expired"), anyInt(), anyInt());
        }

        @Test
        @DisplayName("按关键字搜索")
        void testGetPurchasedServices_FilterByKeyword() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物店A", "shop@test.com");
            Service service = TestDataFactory.createService(1, merchant, "宠物洗澡", new BigDecimal("99.00"));
            Pet pet = TestDataFactory.createPet(testUser);

            Appointment appointment = createTestAppointment(1, testUser, merchant, service, pet, "confirmed", LocalDateTime.now().plusDays(1));
            UserPurchasedServiceDTO dto = createPurchasedServiceDTO(appointment, "active");

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", Collections.singletonList(dto));
            resultMap.put("total", 1L);
            resultMap.put("page", 0);
            resultMap.put("pageSize", 10);
            resultMap.put("totalPages", 1);

            when(appointmentService.findPurchasedServices(eq(testUserId), eq("洗澡"), anyString(), anyInt(), anyInt()))
                    .thenReturn(resultMap);

            var result = performGet("/api/user/services?keyword=洗澡");

            assertSuccess(result);
            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.content.length()").value(1))
                    .andExpect(jsonPath("$.data.content[0].name").value("宠物洗澡"));

            verify(appointmentService).findPurchasedServices(eq(testUserId), eq("洗澡"), anyString(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("分页 - 第1页")
        void testGetPurchasedServices_FirstPage() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物店A", "shop@test.com");
            Service service = TestDataFactory.createService(1, merchant, "宠物洗澡", new BigDecimal("99.00"));
            Pet pet = TestDataFactory.createPet(testUser);

            Appointment appointment = createTestAppointment(1, testUser, merchant, service, pet, "confirmed", LocalDateTime.now().plusDays(1));
            UserPurchasedServiceDTO dto = createPurchasedServiceDTO(appointment, "active");

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", Collections.singletonList(dto));
            resultMap.put("total", 15L);
            resultMap.put("page", 0);
            resultMap.put("pageSize", 5);
            resultMap.put("totalPages", 3);

            when(appointmentService.findPurchasedServices(eq(testUserId), anyString(), anyString(), eq(0), eq(5)))
                    .thenReturn(resultMap);

            var result = performGet("/api/user/services?page=0&pageSize=5");

            assertSuccess(result);
            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.content.length()").value(1))
                    .andExpect(jsonPath("$.data.totalElements").value(15))
                    .andExpect(jsonPath("$.data.totalPages").value(3))
                    .andExpect(jsonPath("$.data.pageSize").value(5))
                    .andExpect(jsonPath("$.data.currentPage").value(0));

            verify(appointmentService).findPurchasedServices(eq(testUserId), anyString(), anyString(), eq(0), eq(5));
        }

        @Test
        @DisplayName("分页 - 第2页")
        void testGetPurchasedServices_SecondPage() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物店A", "shop@test.com");
            Service service = TestDataFactory.createService(2, merchant, "宠物美容", new BigDecimal("199.00"));
            Pet pet = TestDataFactory.createPet(testUser);

            Appointment appointment = createTestAppointment(2, testUser, merchant, service, pet, "confirmed", LocalDateTime.now().plusDays(2));
            UserPurchasedServiceDTO dto = createPurchasedServiceDTO(appointment, "active");

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", Collections.singletonList(dto));
            resultMap.put("total", 15L);
            resultMap.put("page", 1);
            resultMap.put("pageSize", 5);
            resultMap.put("totalPages", 3);

            when(appointmentService.findPurchasedServices(eq(testUserId), anyString(), anyString(), eq(1), eq(5)))
                    .thenReturn(resultMap);

            var result = performGet("/api/user/services?page=1&pageSize=5");

            assertSuccess(result);
            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.currentPage").value(1));

            verify(appointmentService).findPurchasedServices(eq(testUserId), anyString(), anyString(), eq(1), eq(5));
        }

        @Test
        @DisplayName("分页 - 默认分页参数")
        void testGetPurchasedServices_DefaultPagination() throws Exception {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", Collections.emptyList());
            resultMap.put("total", 0L);
            resultMap.put("page", 0);
            resultMap.put("pageSize", 10);
            resultMap.put("totalPages", 0);

            when(appointmentService.findPurchasedServices(eq(testUserId), anyString(), anyString(), eq(0), eq(10)))
                    .thenReturn(resultMap);

            var result = performGet("/api/user/services");

            assertSuccess(result);
            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.pageSize").value(10))
                    .andExpect(jsonPath("$.data.currentPage").value(0));

            verify(appointmentService).findPurchasedServices(eq(testUserId), anyString(), anyString(), eq(0), eq(10));
        }

        @Test
        @DisplayName("分页 - 自定义分页大小")
        void testGetPurchasedServices_CustomPageSize() throws Exception {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", Collections.emptyList());
            resultMap.put("total", 0L);
            resultMap.put("page", 0);
            resultMap.put("pageSize", 20);
            resultMap.put("totalPages", 0);

            when(appointmentService.findPurchasedServices(eq(testUserId), anyString(), anyString(), eq(0), eq(20)))
                    .thenReturn(resultMap);

            var result = performGet("/api/user/services?pageSize=20");

            assertSuccess(result);
            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.pageSize").value(20));

            verify(appointmentService).findPurchasedServices(eq(testUserId), anyString(), anyString(), eq(0), eq(20));
        }

        @Test
        @DisplayName("组合筛选 - 状态和关键字")
        void testGetPurchasedServices_CombinedFilters() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物店A", "shop@test.com");
            Service service = TestDataFactory.createService(1, merchant, "宠物洗澡", new BigDecimal("99.00"));
            Pet pet = TestDataFactory.createPet(testUser);

            Appointment appointment = createTestAppointment(1, testUser, merchant, service, pet, "confirmed", LocalDateTime.now().plusDays(1));
            UserPurchasedServiceDTO dto = createPurchasedServiceDTO(appointment, "active");

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", Collections.singletonList(dto));
            resultMap.put("total", 1L);
            resultMap.put("page", 0);
            resultMap.put("pageSize", 10);
            resultMap.put("totalPages", 1);

            when(appointmentService.findPurchasedServices(eq(testUserId), eq("洗澡"), eq("active"), eq(0), eq(10)))
                    .thenReturn(resultMap);

            var result = performGet("/api/user/services?keyword=洗澡&status=active&page=0&pageSize=10");

            assertSuccess(result);
            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.content.length()").value(1))
                    .andExpect(jsonPath("$.data.content[0].name").value("宠物洗澡"))
                    .andExpect(jsonPath("$.data.content[0].status").value("active"));

            verify(appointmentService).findPurchasedServices(eq(testUserId), eq("洗澡"), eq("active"), eq(0), eq(10));
        }

        @Test
        @DisplayName("验证返回数据结构")
        void testGetPurchasedServices_ResponseStructure() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物店A", "shop@test.com");
            Service service = TestDataFactory.createService(1, merchant, "宠物洗澡", new BigDecimal("99.00"));
            Pet pet = TestDataFactory.createPet(testUser);

            Appointment appointment = createTestAppointment(1, testUser, merchant, service, pet, "confirmed", LocalDateTime.now().plusDays(1));
            UserPurchasedServiceDTO dto = UserPurchasedServiceDTO.builder()
                    .id(1)
                    .name("宠物洗澡")
                    .merchant("宠物店A")
                    .merchantId(1)
                    .price(new BigDecimal("99.00"))
                    .purchaseDate(LocalDateTime.now().minusDays(1))
                    .expiryDate(LocalDateTime.now().plusDays(1))
                    .status("active")
                    .category("General")
                    .serviceId(1)
                    .build();

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", Collections.singletonList(dto));
            resultMap.put("total", 1L);
            resultMap.put("page", 0);
            resultMap.put("pageSize", 10);
            resultMap.put("totalPages", 1);

            when(appointmentService.findPurchasedServices(eq(testUserId), anyString(), anyString(), anyInt(), anyInt()))
                    .thenReturn(resultMap);

            var result = performGet("/api/user/services");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.content[0].id").value(1))
                    .andExpect(jsonPath("$.data.content[0].name").value("宠物洗澡"))
                    .andExpect(jsonPath("$.data.content[0].merchant").value("宠物店A"))
                    .andExpect(jsonPath("$.data.content[0].merchantId").value(1))
                    .andExpect(jsonPath("$.data.content[0].price").value(99.00))
                    .andExpect(jsonPath("$.data.content[0].status").value("active"))
                    .andExpect(jsonPath("$.data.content[0].category").value("General"))
                    .andExpect(jsonPath("$.data.content[0].serviceId").value(1));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetPurchasedServices_ServiceError() throws Exception {
            when(appointmentService.findPurchasedServices(eq(testUserId), anyString(), anyString(), anyInt(), anyInt()))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            var result = performGet("/api/user/services");

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));
        }

        @Test
        @DisplayName("多个服务 - 不同状态")
        void testGetPurchasedServices_MultipleServicesWithDifferentStatuses() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物店A", "shop@test.com");
            Service service1 = TestDataFactory.createService(1, merchant, "宠物洗澡", new BigDecimal("99.00"));
            Service service2 = TestDataFactory.createService(2, merchant, "宠物美容", new BigDecimal("199.00"));
            Service service3 = TestDataFactory.createService(3, merchant, "宠物寄养", new BigDecimal("299.00"));
            Pet pet = TestDataFactory.createPet(testUser);

            Appointment appointment1 = createTestAppointment(1, testUser, merchant, service1, pet, "confirmed", LocalDateTime.now().plusDays(1));
            Appointment appointment2 = createTestAppointment(2, testUser, merchant, service2, pet, "completed", LocalDateTime.now().minusDays(5));
            Appointment appointment3 = createTestAppointment(3, testUser, merchant, service3, pet, "cancelled", LocalDateTime.now().minusDays(10));

            UserPurchasedServiceDTO dto1 = createPurchasedServiceDTO(appointment1, "active");
            UserPurchasedServiceDTO dto2 = createPurchasedServiceDTO(appointment2, "used");
            UserPurchasedServiceDTO dto3 = createPurchasedServiceDTO(appointment3, "expired");

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", Arrays.asList(dto1, dto2, dto3));
            resultMap.put("total", 3L);
            resultMap.put("page", 0);
            resultMap.put("pageSize", 10);
            resultMap.put("totalPages", 1);

            when(appointmentService.findPurchasedServices(eq(testUserId), anyString(), anyString(), anyInt(), anyInt()))
                    .thenReturn(resultMap);

            var result = performGet("/api/user/services");

            assertSuccess(result);
            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.content.length()").value(3))
                    .andExpect(jsonPath("$.data.totalElements").value(3));
        }
    }

    private Appointment createTestAppointment(Integer id, User user, Merchant merchant, Service service, Pet pet, String status, LocalDateTime appointmentTime) {
        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setUser(user);
        appointment.setMerchant(merchant);
        appointment.setService(service);
        appointment.setPet(pet);
        appointment.setStatus(status);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setTotalPrice(service.getPrice());
        appointment.setCreatedAt(LocalDateTime.now().minusDays(1));
        appointment.setUpdatedAt(LocalDateTime.now());
        return appointment;
    }

    private UserPurchasedServiceDTO createPurchasedServiceDTO(Appointment appointment, String status) {
        return UserPurchasedServiceDTO.builder()
                .id(appointment.getId())
                .name(appointment.getService().getName())
                .merchant(appointment.getMerchant().getName())
                .merchantId(appointment.getMerchant().getId())
                .price(appointment.getTotalPrice())
                .purchaseDate(appointment.getCreatedAt())
                .expiryDate(appointment.getAppointmentTime())
                .status(status)
                .category("General")
                .serviceId(appointment.getService().getId())
                .build();
    }
}
