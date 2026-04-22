package com.petshop.controller.api;

import com.petshop.dto.AppointmentDTO;
import com.petshop.dto.CreateAppointmentRequest;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("用户预约API测试")
public class UserApiControllerAppointmentTest extends UserApiControllerTestBase {

    @BeforeEach
    void setUpController() {
        UserApiController controller = new UserApiController();
        injectDependencies(controller);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private void injectDependencies(UserApiController controller) {
        try {
            setField(controller, "appointmentService", appointmentService);
            setField(controller, "serviceService", serviceService);
            setField(controller, "petService", petService);
        } catch (Exception e) {
            throw new RuntimeException("注入依赖失败", e);
        }
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private AppointmentDTO createAppointmentDTO(Integer id, String status, String serviceName, String merchantName) {
        return AppointmentDTO.builder()
                .id(id)
                .userId(testUserId)
                .serviceId(1)
                .merchantId(1)
                .serviceName(serviceName)
                .merchantName(merchantName)
                .appointmentTime(LocalDateTime.now().plusDays(1))
                .status(status)
                .remark("测试备注")
                .totalPrice(new BigDecimal("99.99"))
                .petId(1)
                .petName("测试宠物")
                .petType("狗")
                .merchantPhone("13800138000")
                .merchantAddress("测试地址")
                .servicePrice(new BigDecimal("99.99"))
                .serviceDuration(60)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("GET /api/user/appointments - 获取预约列表")
    class GetAppointmentsTests {

        @Test
        @DisplayName("成功获取预约列表 - 返回多个预约")
        void testGetAppointments_Success_WithAppointments() throws Exception {
            AppointmentDTO dto1 = createAppointmentDTO(1, "pending", "洗澡服务", "宠物店A");
            AppointmentDTO dto2 = createAppointmentDTO(2, "confirmed", "美容服务", "宠物店B");
            AppointmentDTO dto3 = createAppointmentDTO(3, "completed", "寄养服务", "宠物店C");
            List<AppointmentDTO> appointments = Arrays.asList(dto1, dto2, dto3);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", appointments);
            resultMap.put("total", 3);
            resultMap.put("page", 0);
            resultMap.put("pageSize", 10);
            resultMap.put("totalPages", 1);

            when(appointmentService.findByUserIdWithFilters(eq(testUserId), any(), any(), any(), any(), anyInt(), anyInt()))
                    .thenReturn(resultMap);

            var result = performGet("/api/user/appointments");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(3))
                    .andExpect(jsonPath("$.total").value(3));
            verify(appointmentService).findByUserIdWithFilters(eq(testUserId), any(), any(), any(), any(), eq(0), eq(10));
        }

        @Test
        @DisplayName("成功获取预约列表 - 返回空列表")
        void testGetAppointments_Success_EmptyList() throws Exception {
            Map<String, Object> emptyResultMap = new HashMap<>();
            emptyResultMap.put("data", Collections.emptyList());
            emptyResultMap.put("total", 0);
            emptyResultMap.put("page", 0);
            emptyResultMap.put("pageSize", 10);
            emptyResultMap.put("totalPages", 0);

            when(appointmentService.findByUserIdWithFilters(eq(testUserId), any(), any(), any(), any(), anyInt(), anyInt()))
                    .thenReturn(emptyResultMap);

            var result = performGet("/api/user/appointments");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0))
                    .andExpect(jsonPath("$.total").value(0));
        }

        @Test
        @DisplayName("按状态筛选预约 - pending")
        void testGetAppointments_FilterByStatus() throws Exception {
            AppointmentDTO dto = createAppointmentDTO(1, "pending", "洗澡服务", "宠物店A");
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", List.of(dto));
            resultMap.put("total", 1);
            resultMap.put("page", 0);
            resultMap.put("pageSize", 10);
            resultMap.put("totalPages", 1);

            when(appointmentService.findByUserIdWithFilters(eq(testUserId), eq("pending"), any(), any(), any(), anyInt(), anyInt()))
                    .thenReturn(resultMap);

            var result = mockMvc.perform(get("/api/user/appointments")
                    .param("status", "pending")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType("application/json"))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].status").value("pending"));
        }

        @Test
        @DisplayName("按关键字搜索预约")
        void testGetAppointments_FilterByKeyword() throws Exception {
            AppointmentDTO dto = createAppointmentDTO(1, "pending", "洗澡服务", "宠物店A");
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", List.of(dto));
            resultMap.put("total", 1);
            resultMap.put("page", 0);
            resultMap.put("pageSize", 10);
            resultMap.put("totalPages", 1);

            when(appointmentService.findByUserIdWithFilters(eq(testUserId), any(), eq("洗澡"), any(), any(), anyInt(), anyInt()))
                    .thenReturn(resultMap);

            var result = mockMvc.perform(get("/api/user/appointments")
                    .param("keyword", "洗澡")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType("application/json"))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.length()").value(1));
        }

        @Test
        @DisplayName("按日期范围筛选预约")
        void testGetAppointments_FilterByDateRange() throws Exception {
            AppointmentDTO dto = createAppointmentDTO(1, "pending", "洗澡服务", "宠物店A");
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", List.of(dto));
            resultMap.put("total", 1);
            resultMap.put("page", 0);
            resultMap.put("pageSize", 10);
            resultMap.put("totalPages", 1);

            when(appointmentService.findByUserIdWithFilters(eq(testUserId), any(), any(), any(LocalDateTime.class), any(LocalDateTime.class), anyInt(), anyInt()))
                    .thenReturn(resultMap);

            var result = mockMvc.perform(get("/api/user/appointments")
                    .param("startDate", "2024-01-01T00:00:00")
                    .param("endDate", "2024-12-31T23:59:59")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType("application/json"))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.length()").value(1));
        }

        @Test
        @DisplayName("分页参数测试")
        void testGetAppointments_Pagination() throws Exception {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", Collections.emptyList());
            resultMap.put("total", 0);
            resultMap.put("page", 2);
            resultMap.put("pageSize", 20);
            resultMap.put("totalPages", 0);

            when(appointmentService.findByUserIdWithFilters(eq(testUserId), any(), any(), any(), any(), eq(2), eq(20)))
                    .thenReturn(resultMap);

            var result = mockMvc.perform(get("/api/user/appointments")
                    .param("page", "2")
                    .param("pageSize", "20")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType("application/json"))
                    .andDo(print());

            result.andExpect(status().isOk());
            verify(appointmentService).findByUserIdWithFilters(eq(testUserId), any(), any(), any(), any(), eq(2), eq(20));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetAppointments_Unauthorized() throws Exception {
            var result = mockMvc.perform(get("/api/user/appointments")
                    .contentType("application/json"))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
            verify(appointmentService, never()).findByUserIdWithFilters(anyInt(), any(), any(), any(), any(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("服务层异常")
        void testGetAppointments_ServiceException() throws Exception {
            when(appointmentService.findByUserIdWithFilters(eq(testUserId), any(), any(), any(), any(), anyInt(), anyInt()))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            var result = performGet("/api/user/appointments");

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("GET /api/user/appointments/{id} - 获取预约详情")
    class GetAppointmentByIdTests {

        @Test
        @DisplayName("成功获取预约详情")
        void testGetAppointmentById_Success() throws Exception {
            AppointmentDTO dto = createAppointmentDTO(1, "pending", "洗澡服务", "宠物店A");

            when(appointmentService.findByIdAndUserId(1, testUserId)).thenReturn(dto);

            var result = performGet("/api/user/appointments/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.status").value("pending"))
                    .andExpect(jsonPath("$.serviceName").value("洗澡服务"))
                    .andExpect(jsonPath("$.merchantName").value("宠物店A"))
                    .andExpect(jsonPath("$.petName").value("测试宠物"));
            verify(appointmentService).findByIdAndUserId(1, testUserId);
        }

        @Test
        @DisplayName("预约不存在返回404")
        void testGetAppointmentById_NotFound() throws Exception {
            when(appointmentService.findByIdAndUserId(999, testUserId)).thenReturn(null);

            var result = performGet("/api/user/appointments/{id}", 999);

            result.andExpect(status().isNotFound());
            verify(appointmentService).findByIdAndUserId(999, testUserId);
        }

        @Test
        @DisplayName("所有权验证 - 不能查看其他用户的预约")
        void testGetAppointmentById_OwnershipValidation() throws Exception {
            when(appointmentService.findByIdAndUserId(1, testUserId)).thenReturn(null);

            var result = performGet("/api/user/appointments/{id}", 1);

            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetAppointmentById_Unauthorized() throws Exception {
            var result = mockMvc.perform(get("/api/user/appointments/{id}", 1)
                    .contentType("application/json"))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
            verify(appointmentService, never()).findByIdAndUserId(anyInt(), anyInt());
        }

        @Test
        @DisplayName("验证返回完整信息")
        void testGetAppointmentById_FullDetails() throws Exception {
            AppointmentDTO dto = AppointmentDTO.builder()
                    .id(1)
                    .userId(testUserId)
                    .serviceId(10)
                    .merchantId(20)
                    .serviceName("高级美容")
                    .merchantName("豪华宠物店")
                    .appointmentTime(LocalDateTime.now().plusDays(1))
                    .status("confirmed")
                    .remark("请小心护理")
                    .totalPrice(new BigDecimal("299.99"))
                    .petId(5)
                    .petName("小白")
                    .petType("猫")
                    .merchantPhone("13900139000")
                    .merchantAddress("详细地址123号")
                    .servicePrice(new BigDecimal("299.99"))
                    .serviceDuration(120)
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .updatedAt(LocalDateTime.now())
                    .build();

            when(appointmentService.findByIdAndUserId(1, testUserId)).thenReturn(dto);

            var result = performGet("/api/user/appointments/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.serviceId").value(10))
                    .andExpect(jsonPath("$.merchantId").value(20))
                    .andExpect(jsonPath("$.serviceName").value("高级美容"))
                    .andExpect(jsonPath("$.merchantName").value("豪华宠物店"))
                    .andExpect(jsonPath("$.status").value("confirmed"))
                    .andExpect(jsonPath("$.remark").value("请小心护理"))
                    .andExpect(jsonPath("$.totalPrice").value(299.99))
                    .andExpect(jsonPath("$.petId").value(5))
                    .andExpect(jsonPath("$.petName").value("小白"))
                    .andExpect(jsonPath("$.petType").value("猫"))
                    .andExpect(jsonPath("$.merchantPhone").value("13900139000"))
                    .andExpect(jsonPath("$.merchantAddress").value("详细地址123号"))
                    .andExpect(jsonPath("$.serviceDuration").value(120));
        }
    }

    @Nested
    @DisplayName("POST /api/user/appointments - 创建预约")
    class CreateAppointmentTests {

        @Test
        @DisplayName("成功创建预约")
        void testCreateAppointment_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Pet pet = TestDataFactory.createPet(1, testUser);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant, testUser, service, pet,
                    LocalDateTime.now().plusDays(1), "pending");

            CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                    .serviceId(1)
                    .petId(1)
                    .appointmentTime(LocalDateTime.now().plusDays(1).toString())
                    .remark("请小心护理")
                    .build();

            when(serviceService.findById(1)).thenReturn(service);
            when(petService.findById(1)).thenReturn(pet);
            when(appointmentService.createAppointment(any(User.class), any(Service.class), any(Pet.class),
                    any(LocalDateTime.class), anyString())).thenReturn(appointment);

            var result = performPost("/api/user/appointments", request);

            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.success").value(true));
            verify(serviceService).findById(1);
            verify(petService).findById(1);
            verify(appointmentService).createAppointment(any(User.class), any(Service.class), any(Pet.class),
                    any(LocalDateTime.class), eq("请小心护理"));
        }

        @Test
        @DisplayName("服务不存在返回400")
        void testCreateAppointment_ServiceNotFound() throws Exception {
            CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                    .serviceId(999)
                    .petId(1)
                    .appointmentTime(LocalDateTime.now().plusDays(1).toString())
                    .build();

            when(serviceService.findById(999)).thenReturn(null);

            var result = performPost("/api/user/appointments", request);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Service not found"));
            verify(serviceService).findById(999);
            verify(petService, never()).findById(anyInt());
            verify(appointmentService, never()).createAppointment(any(), any(), any(), any(), any());
        }

        @Test
        @DisplayName("宠物不存在返回400")
        void testCreateAppointment_PetNotFound() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);

            CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                    .serviceId(1)
                    .petId(999)
                    .appointmentTime(LocalDateTime.now().plusDays(1).toString())
                    .build();

            when(serviceService.findById(1)).thenReturn(service);
            when(petService.findById(999)).thenReturn(null);

            var result = performPost("/api/user/appointments", request);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Pet not found or does not belong to user"));
        }

        @Test
        @DisplayName("宠物不属于当前用户返回400")
        void testCreateAppointment_PetNotBelongToUser() throws Exception {
            User otherUser = TestDataFactory.createUser(2);
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Pet otherPet = TestDataFactory.createPet(1, otherUser);

            CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                    .serviceId(1)
                    .petId(1)
                    .appointmentTime(LocalDateTime.now().plusDays(1).toString())
                    .build();

            when(serviceService.findById(1)).thenReturn(service);
            when(petService.findById(1)).thenReturn(otherPet);

            var result = performPost("/api/user/appointments", request);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Pet not found or does not belong to user"));
        }

        @Test
        @DisplayName("无效的时间格式返回400")
        void testCreateAppointment_InvalidTimeFormat() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Pet pet = TestDataFactory.createPet(1, testUser);

            CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                    .serviceId(1)
                    .petId(1)
                    .appointmentTime("invalid-time-format")
                    .build();

            when(serviceService.findById(1)).thenReturn(service);
            when(petService.findById(1)).thenReturn(pet);

            var result = performPost("/api/user/appointments", request);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Invalid appointment time format"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testCreateAppointment_Unauthorized() throws Exception {
            CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                    .serviceId(1)
                    .petId(1)
                    .appointmentTime(LocalDateTime.now().plusDays(1).toString())
                    .build();

            var result = mockMvc.perform(post("/api/user/appointments")
                    .contentType("application/json")
                    .content(toJson(request)))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
            verify(serviceService, never()).findById(anyInt());
        }

        @Test
        @DisplayName("验证必填字段 - serviceId")
        void testCreateAppointment_RequiredServiceId() throws Exception {
            CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                    .petId(1)
                    .appointmentTime(LocalDateTime.now().plusDays(1).toString())
                    .build();

            when(serviceService.findById(null)).thenReturn(null);

            var result = performPost("/api/user/appointments", request);

            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("验证必填字段 - petId")
        void testCreateAppointment_RequiredPetId() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);

            CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                    .serviceId(1)
                    .appointmentTime(LocalDateTime.now().plusDays(1).toString())
                    .build();

            when(serviceService.findById(1)).thenReturn(service);
            when(petService.findById(null)).thenReturn(null);

            var result = performPost("/api/user/appointments", request);

            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("备注字段可选")
        void testCreateAppointment_OptionalRemark() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Pet pet = TestDataFactory.createPet(1, testUser);
            Appointment appointment = TestDataFactory.createAppointment(1, merchant, testUser, service, pet,
                    LocalDateTime.now().plusDays(1), "pending");

            CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                    .serviceId(1)
                    .petId(1)
                    .appointmentTime(LocalDateTime.now().plusDays(1).toString())
                    .build();

            when(serviceService.findById(1)).thenReturn(service);
            when(petService.findById(1)).thenReturn(pet);
            when(appointmentService.createAppointment(any(User.class), any(Service.class), any(Pet.class),
                    any(LocalDateTime.class), any())).thenReturn(appointment);

            var result = performPost("/api/user/appointments", request);

            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("PUT /api/user/appointments/{id}/cancel - 取消预约")
    class CancelAppointmentTests {

        @Test
        @DisplayName("成功取消预约 - pending状态")
        void testCancelAppointment_Success_Pending() throws Exception {
            when(appointmentService.cancelAppointment(1, testUserId)).thenReturn(true);

            var result = performPut("/api/user/appointments/{id}/cancel", null, 1);

            result.andExpect(status().isOk());
            verify(appointmentService).cancelAppointment(1, testUserId);
        }

        @Test
        @DisplayName("成功取消预约 - confirmed状态")
        void testCancelAppointment_Success_Confirmed() throws Exception {
            when(appointmentService.cancelAppointment(1, testUserId)).thenReturn(true);

            var result = performPut("/api/user/appointments/{id}/cancel", null, 1);

            result.andExpect(status().isOk());
        }

        @Test
        @DisplayName("不能取消已完成的预约")
        void testCancelAppointment_AlreadyCompleted() throws Exception {
            when(appointmentService.cancelAppointment(1, testUserId)).thenReturn(false);

            var result = performPut("/api/user/appointments/{id}/cancel", null, 1);

            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("不能取消已取消的预约")
        void testCancelAppointment_AlreadyCancelled() throws Exception {
            when(appointmentService.cancelAppointment(1, testUserId)).thenReturn(false);

            var result = performPut("/api/user/appointments/{id}/cancel", null, 1);

            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("预约不存在返回404")
        void testCancelAppointment_NotFound() throws Exception {
            when(appointmentService.cancelAppointment(999, testUserId)).thenReturn(false);

            var result = performPut("/api/user/appointments/{id}/cancel", null, 999);

            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("所有权验证 - 不能取消其他用户的预约")
        void testCancelAppointment_OwnershipValidation() throws Exception {
            when(appointmentService.cancelAppointment(1, testUserId)).thenReturn(false);

            var result = performPut("/api/user/appointments/{id}/cancel", null, 1);

            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("未登录返回401")
        void testCancelAppointment_Unauthorized() throws Exception {
            var result = mockMvc.perform(put("/api/user/appointments/{id}/cancel", 1)
                    .contentType("application/json"))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
            verify(appointmentService, never()).cancelAppointment(anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("GET /api/user/appointments/stats - 获取预约统计")
    class GetAppointmentStatsTests {

        @Test
        @DisplayName("成功获取统计数据")
        void testGetAppointmentStats_Success() throws Exception {
            Map<String, Long> stats = new HashMap<>();
            stats.put("total", 10L);
            stats.put("pending", 3L);
            stats.put("confirmed", 2L);
            stats.put("completed", 4L);
            stats.put("cancelled", 1L);

            when(appointmentService.getAppointmentStats(testUserId)).thenReturn(stats);

            var result = performGet("/api/user/appointments/stats");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.total").value(10))
                    .andExpect(jsonPath("$.pending").value(3))
                    .andExpect(jsonPath("$.confirmed").value(2))
                    .andExpect(jsonPath("$.completed").value(4))
                    .andExpect(jsonPath("$.cancelled").value(1));
            verify(appointmentService).getAppointmentStats(testUserId);
        }

        @Test
        @DisplayName("统计数据为空")
        void testGetAppointmentStats_EmptyStats() throws Exception {
            Map<String, Long> emptyStats = new HashMap<>();
            emptyStats.put("total", 0L);
            emptyStats.put("pending", 0L);
            emptyStats.put("confirmed", 0L);
            emptyStats.put("completed", 0L);
            emptyStats.put("cancelled", 0L);

            when(appointmentService.getAppointmentStats(testUserId)).thenReturn(emptyStats);

            var result = performGet("/api/user/appointments/stats");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.total").value(0))
                    .andExpect(jsonPath("$.pending").value(0));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetAppointmentStats_Unauthorized() throws Exception {
            var result = mockMvc.perform(get("/api/user/appointments/stats")
                    .contentType("application/json"))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
            verify(appointmentService, never()).getAppointmentStats(anyInt());
        }

        @Test
        @DisplayName("验证不同用户的统计数据隔离")
        void testGetAppointmentStats_UserIsolation() throws Exception {
            Map<String, Long> stats = new HashMap<>();
            stats.put("total", 5L);
            stats.put("pending", 2L);

            when(appointmentService.getAppointmentStats(testUserId)).thenReturn(stats);

            var result = performGet("/api/user/appointments/stats");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.total").value(5));

            verify(appointmentService).getAppointmentStats(testUserId);
            verify(appointmentService, never()).getAppointmentStats(eq(2));
        }
    }

    @Nested
    @DisplayName("状态流转验证测试")
    class StatusTransitionTests {

        @Test
        @DisplayName("完整预约流程 - 创建 -> 确认 -> 完成")
        void testStatusTransition_FullFlow() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Pet pet = TestDataFactory.createPet(1, testUser);

            Appointment pendingAppointment = TestDataFactory.createAppointment(1, merchant, testUser, service, pet,
                    LocalDateTime.now().plusDays(1), "pending");

            CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                    .serviceId(1)
                    .petId(1)
                    .appointmentTime(LocalDateTime.now().plusDays(1).toString())
                    .remark("测试预约")
                    .build();

            when(serviceService.findById(1)).thenReturn(service);
            when(petService.findById(1)).thenReturn(pet);
            when(appointmentService.createAppointment(any(User.class), any(Service.class), any(Pet.class),
                    any(LocalDateTime.class), anyString())).thenReturn(pendingAppointment);

            var createResult = performPost("/api/user/appointments", request);
            createResult.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1));

            AppointmentDTO confirmedDTO = createAppointmentDTO(1, "confirmed", "洗澡服务", "宠物店A");
            when(appointmentService.findByIdAndUserId(1, testUserId)).thenReturn(confirmedDTO);

            var getResult = performGet("/api/user/appointments/{id}", 1);
            getResult.andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("confirmed"));
        }

        @Test
        @DisplayName("取消流程 - 创建 -> 取消")
        void testStatusTransition_CancelFlow() throws Exception {
            when(appointmentService.cancelAppointment(1, testUserId)).thenReturn(true);

            var result = performPut("/api/user/appointments/{id}/cancel", null, 1);

            result.andExpect(status().isOk());

            AppointmentDTO cancelledDTO = createAppointmentDTO(1, "cancelled", "洗澡服务", "宠物店A");
            when(appointmentService.findByIdAndUserId(1, testUserId)).thenReturn(cancelledDTO);

            var getResult = performGet("/api/user/appointments/{id}", 1);
            getResult.andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("cancelled"));
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("预约ID为负数")
        void testGetAppointmentById_NegativeId() throws Exception {
            when(appointmentService.findByIdAndUserId(-1, testUserId)).thenReturn(null);

            var result = performGet("/api/user/appointments/{id}", -1);

            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("预约ID为零")
        void testGetAppointmentById_ZeroId() throws Exception {
            when(appointmentService.findByIdAndUserId(0, testUserId)).thenReturn(null);

            var result = performGet("/api/user/appointments/{id}", 0);

            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("大数值预约ID")
        void testGetAppointmentById_LargeId() throws Exception {
            when(appointmentService.findByIdAndUserId(Integer.MAX_VALUE, testUserId)).thenReturn(null);

            var result = performGet("/api/user/appointments/{id}", Integer.MAX_VALUE);

            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("分页边界 - 第一页")
        void testGetAppointments_FirstPage() throws Exception {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", Collections.emptyList());
            resultMap.put("total", 0);
            resultMap.put("page", 0);
            resultMap.put("pageSize", 10);
            resultMap.put("totalPages", 0);

            when(appointmentService.findByUserIdWithFilters(eq(testUserId), any(), any(), any(), any(), eq(0), eq(10)))
                    .thenReturn(resultMap);

            var result = mockMvc.perform(get("/api/user/appointments")
                    .param("page", "0")
                    .param("pageSize", "10")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType("application/json"))
                    .andDo(print());

            result.andExpect(status().isOk());
        }

        @Test
        @DisplayName("创建预约 - 过去的时间")
        void testCreateAppointment_PastTime() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant);
            Pet pet = TestDataFactory.createPet(1, testUser);

            CreateAppointmentRequest request = CreateAppointmentRequest.builder()
                    .serviceId(1)
                    .petId(1)
                    .appointmentTime(LocalDateTime.now().minusDays(1).toString())
                    .build();

            Appointment appointment = TestDataFactory.createAppointment(1, merchant, testUser, service, pet,
                    LocalDateTime.now().minusDays(1), "pending");

            when(serviceService.findById(1)).thenReturn(service);
            when(petService.findById(1)).thenReturn(pet);
            when(appointmentService.createAppointment(any(User.class), any(Service.class), any(Pet.class),
                    any(LocalDateTime.class), any())).thenReturn(appointment);

            var result = performPost("/api/user/appointments", request);

            result.andExpect(status().isCreated());
        }

        @Test
        @DisplayName("多个预约获取验证")
        void testGetAppointments_MultipleAppointments() throws Exception {
            AppointmentDTO dto1 = createAppointmentDTO(1, "pending", "服务A", "商家A");
            AppointmentDTO dto2 = createAppointmentDTO(2, "confirmed", "服务B", "商家B");
            AppointmentDTO dto3 = createAppointmentDTO(3, "completed", "服务C", "商家C");
            List<AppointmentDTO> appointments = Arrays.asList(dto1, dto2, dto3);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", appointments);
            resultMap.put("total", 3L);
            resultMap.put("page", 0);
            resultMap.put("pageSize", 10);
            resultMap.put("totalPages", 1);

            when(appointmentService.findByUserIdWithFilters(eq(testUserId), any(), any(), any(), any(), anyInt(), anyInt()))
                    .thenReturn(resultMap);

            var result = performGet("/api/user/appointments");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.length()").value(3))
                    .andExpect(jsonPath("$.data[0].id").value(1))
                    .andExpect(jsonPath("$.data[1].id").value(2))
                    .andExpect(jsonPath("$.data[2].id").value(3));
        }
    }

    @Nested
    @DisplayName("并发和安全性测试")
    class SecurityTests {

        @Test
        @DisplayName("不同用户不能访问彼此的预约")
        void testCrossUserAccess() throws Exception {
            User otherUser = TestDataFactory.createUser(2);
            when(appointmentService.findByIdAndUserId(1, testUserId)).thenReturn(null);

            var result = performGet("/api/user/appointments/{id}", 1);

            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("不同用户不能取消彼此的预约")
        void testCrossUserCancellation() throws Exception {
            when(appointmentService.cancelAppointment(1, testUserId)).thenReturn(false);

            var result = performPut("/api/user/appointments/{id}/cancel", null, 1);

            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("不同用户的统计数据隔离")
        void testStatsIsolation() throws Exception {
            Map<String, Long> userStats = new HashMap<>();
            userStats.put("total", 5L);

            Map<String, Long> otherUserStats = new HashMap<>();
            otherUserStats.put("total", 10L);

            when(appointmentService.getAppointmentStats(testUserId)).thenReturn(userStats);
            when(appointmentService.getAppointmentStats(2)).thenReturn(otherUserStats);

            var result = performGet("/api/user/appointments/stats");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.total").value(5));
        }
    }
}
