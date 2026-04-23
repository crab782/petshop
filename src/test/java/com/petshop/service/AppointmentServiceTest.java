package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.dto.AppointmentDTO;
import com.petshop.entity.Appointment;
import com.petshop.entity.Merchant;
import com.petshop.entity.Pet;
import com.petshop.entity.Service;
import com.petshop.entity.User;
import com.petshop.mapper.AppointmentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentService 单元测试")
class AppointmentServiceTest {

    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment testAppointment;
    private User testUser;
    private Merchant testMerchant;
    private Service testService;
    private Pet testPet;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testMerchant = new Merchant();
        testMerchant.setId(1);
        testMerchant.setName("Test Merchant");

        testService = new Service();
        testService.setId(1);
        testService.setName("Test Service");
        testService.setPrice(new BigDecimal("99.99"));
        testService.setMerchant(testMerchant);

        testPet = new Pet();
        testPet.setId(1);
        testPet.setName("Test Pet");
        testPet.setType("Dog");

        testAppointment = new Appointment();
        testAppointment.setId(1);
        testAppointment.setUserId(1);
        testAppointment.setMerchantId(1);
        testAppointment.setServiceId(1);
        testAppointment.setPetId(1);
        testAppointment.setAppointmentTime(LocalDateTime.now().plusDays(1));
        testAppointment.setTotalPrice(new BigDecimal("99.99"));
        testAppointment.setNotes("Test notes");
        testAppointment.setUser(testUser);
        testAppointment.setMerchant(testMerchant);
        testAppointment.setService(testService);
        testAppointment.setPet(testPet);
    }

    @Nested
    @DisplayName("预约创建功能测试")
    class CreateAppointmentTests {

        @Test
        @DisplayName("正常创建预约")
        void testCreate_Success() {
            Appointment newAppointment = new Appointment();
            newAppointment.setUserId(1);
            newAppointment.setMerchantId(1);
            newAppointment.setServiceId(1);
            newAppointment.setTotalPrice(new BigDecimal("99.99"));

            when(appointmentMapper.insert(any(Appointment.class))).thenAnswer(invocation -> {
                Appointment appointment = invocation.getArgument(0);
                appointment.setId(1);
                return 1;
            });

            Appointment result = appointmentService.create(newAppointment);

            assertNotNull(result);
            assertEquals("pending", result.getStatus());
            assertEquals(1, result.getId());
            verify(appointmentMapper, times(1)).insert(any(Appointment.class));
        }

        @Test
        @DisplayName("创建预约时默认状态为 pending")
        void testCreate_DefaultStatusIsPending() {
            Appointment newAppointment = new Appointment();
            newAppointment.setUserId(1);
            newAppointment.setMerchantId(1);

            ArgumentCaptor<Appointment> appointmentCaptor = ArgumentCaptor.forClass(Appointment.class);
            when(appointmentMapper.insert(appointmentCaptor.capture())).thenReturn(1);

            appointmentService.create(newAppointment);

            Appointment capturedAppointment = appointmentCaptor.getValue();
            assertEquals("pending", capturedAppointment.getStatus());
        }

        @Test
        @DisplayName("使用完整信息创建预约")
        void testCreateAppointment_WithFullInfo() {
            LocalDateTime appointmentTime = LocalDateTime.now().plusDays(1);
            String remark = "Please take care of my pet";

            when(appointmentMapper.insert(any(Appointment.class))).thenAnswer(invocation -> {
                Appointment appointment = invocation.getArgument(0);
                appointment.setId(1);
                return 1;
            });

            Appointment result = appointmentService.createAppointment(
                    testUser, testService, testPet, appointmentTime, remark);

            assertNotNull(result);
            assertEquals(testUser, result.getUser());
            assertEquals(testService, result.getService());
            assertEquals(testMerchant, result.getMerchant());
            assertEquals(testPet, result.getPet());
            assertEquals(appointmentTime, result.getAppointmentTime());
            assertEquals(remark, result.getNotes());
            assertEquals(testService.getPrice(), result.getTotalPrice());
            assertEquals("pending", result.getStatus());
            verify(appointmentMapper, times(1)).insert(any(Appointment.class));
        }
    }

    @Nested
    @DisplayName("预约查询功能测试")
    class FindAppointmentTests {

        @Test
        @DisplayName("根据 ID 查询预约 - 存在")
        void testFindById_Exists() {
            when(appointmentMapper.selectById(1)).thenReturn(testAppointment);

            Appointment result = appointmentService.findById(1);

            assertNotNull(result);
            assertEquals(1, result.getId());
            assertEquals("pending", result.getStatus());
            verify(appointmentMapper, times(1)).selectById(1);
        }

        @Test
        @DisplayName("根据 ID 查询预约 - 不存在")
        void testFindById_NotExists() {
            when(appointmentMapper.selectById(999)).thenReturn(null);

            Appointment result = appointmentService.findById(999);

            assertNull(result);
            verify(appointmentMapper, times(1)).selectById(999);
        }

        @Test
        @DisplayName("根据用户 ID 查询预约列表")
        void testFindByUserId() {
            List<Appointment> appointments = Arrays.asList(testAppointment);
            when(appointmentMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(appointments);

            List<Appointment> result = appointmentService.findByUserId(1);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(1, result.get(0).getUserId());
            verify(appointmentMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("根据用户 ID 查询预约列表 - 空列表")
        void testFindByUserId_EmptyList() {
            when(appointmentMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            List<Appointment> result = appointmentService.findByUserId(999);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(appointmentMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("根据商家 ID 查询预约列表")
        void testFindByMerchantId() {
            List<Appointment> appointments = Arrays.asList(testAppointment);
            when(appointmentMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(appointments);

            List<Appointment> result = appointmentService.findByMerchantId(1);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(1, result.get(0).getMerchantId());
            verify(appointmentMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("根据 ID 查询预约（带关联信息）")
        void testFindByIdWithRelations() {
            when(appointmentMapper.selectByIdWithRelations(1)).thenReturn(testAppointment);

            Appointment result = appointmentService.findByIdWithRelations(1);

            assertNotNull(result);
            assertEquals(1, result.getId());
            assertNotNull(result.getUser());
            assertNotNull(result.getService());
            assertNotNull(result.getMerchant());
            assertNotNull(result.getPet());
            verify(appointmentMapper, times(1)).selectByIdWithRelations(1);
        }

        @Test
        @DisplayName("根据 ID 和用户 ID 查询预约 - 存在且属于该用户")
        void testFindByIdAndUserId_ExistsAndBelongsToUser() {
            when(appointmentMapper.selectById(1)).thenReturn(testAppointment);

            AppointmentDTO result = appointmentService.findByIdAndUserId(1, 1);

            assertNotNull(result);
            assertEquals(1, result.getId());
            assertEquals(1, result.getUserId());
            verify(appointmentMapper, times(1)).selectById(1);
        }

        @Test
        @DisplayName("根据 ID 和用户 ID 查询预约 - 不存在")
        void testFindByIdAndUserId_NotExists() {
            when(appointmentMapper.selectById(999)).thenReturn(null);

            AppointmentDTO result = appointmentService.findByIdAndUserId(999, 1);

            assertNull(result);
            verify(appointmentMapper, times(1)).selectById(999);
        }

        @Test
        @DisplayName("根据 ID 和用户 ID 查询预约 - 不属于该用户")
        void testFindByIdAndUserId_NotBelongToUser() {
            when(appointmentMapper.selectById(1)).thenReturn(testAppointment);

            AppointmentDTO result = appointmentService.findByIdAndUserId(1, 999);

            assertNull(result);
            verify(appointmentMapper, times(1)).selectById(1);
        }

        @Test
        @DisplayName("带过滤条件查询预约 - 按状态筛选")
        void testFindByUserIdWithFilters_ByStatus() {
            Page<Appointment> page = new Page<>(1, 10);
            page.setRecords(Arrays.asList(testAppointment));
            page.setTotal(1);

            when(appointmentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            Map<String, Object> result = appointmentService.findByUserIdWithFilters(
                    1, "pending", null, null, null, 1, 10);

            assertNotNull(result);
            assertEquals(1L, result.get("total"));
            verify(appointmentMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("带过滤条件查询预约 - 按关键字筛选")
        void testFindByUserIdWithFilters_ByKeyword() {
            Page<Appointment> page = new Page<>(1, 10);
            page.setRecords(Arrays.asList(testAppointment));
            page.setTotal(1);

            when(appointmentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            Map<String, Object> result = appointmentService.findByUserIdWithFilters(
                    1, null, "test", null, null, 1, 10);

            assertNotNull(result);
            assertEquals(1L, result.get("total"));
            verify(appointmentMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("带过滤条件查询预约 - 按日期范围筛选")
        void testFindByUserIdWithFilters_ByDateRange() {
            Page<Appointment> page = new Page<>(1, 10);
            page.setRecords(Arrays.asList(testAppointment));
            page.setTotal(1);

            LocalDateTime startDate = LocalDateTime.now().minusDays(7);
            LocalDateTime endDate = LocalDateTime.now();

            when(appointmentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            Map<String, Object> result = appointmentService.findByUserIdWithFilters(
                    1, null, null, startDate, endDate, 1, 10);

            assertNotNull(result);
            assertEquals(1L, result.get("total"));
            verify(appointmentMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("带过滤条件查询预约 - 无过滤条件")
        void testFindByUserIdWithFilters_NoFilters() {
            Page<Appointment> page = new Page<>(1, 10);
            page.setRecords(Arrays.asList(testAppointment));
            page.setTotal(1);

            when(appointmentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            Map<String, Object> result = appointmentService.findByUserIdWithFilters(
                    1, null, null, null, null, 1, 10);

            assertNotNull(result);
            assertEquals(1L, result.get("total"));
            assertEquals(1, result.get("page"));
            assertEquals(10, result.get("pageSize"));
            verify(appointmentMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }
    }

    @Nested
    @DisplayName("预约取消功能测试")
    class CancelAppointmentTests {

        @Test
        @DisplayName("正常取消预约")
        void testCancelAppointment_Success() {
            testAppointment.setStatus("pending");
            when(appointmentMapper.selectById(1)).thenReturn(testAppointment);
            when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

            boolean result = appointmentService.cancelAppointment(1, 1);

            assertTrue(result);
            verify(appointmentMapper, times(1)).selectById(1);
            verify(appointmentMapper, times(1)).updateById(any(Appointment.class));
        }

        @Test
        @DisplayName("取消预约 - 预约不存在")
        void testCancelAppointment_NotExists() {
            when(appointmentMapper.selectById(999)).thenReturn(null);

            boolean result = appointmentService.cancelAppointment(999, 1);

            assertFalse(result);
            verify(appointmentMapper, times(1)).selectById(999);
            verify(appointmentMapper, never()).updateById(any(Appointment.class));
        }

        @Test
        @DisplayName("取消预约 - 不属于该用户")
        void testCancelAppointment_NotBelongToUser() {
            when(appointmentMapper.selectById(1)).thenReturn(testAppointment);

            boolean result = appointmentService.cancelAppointment(1, 999);

            assertFalse(result);
            verify(appointmentMapper, times(1)).selectById(1);
            verify(appointmentMapper, never()).updateById(any(Appointment.class));
        }

        @Test
        @DisplayName("取消预约 - 已取消的预约")
        void testCancelAppointment_AlreadyCancelled() {
            testAppointment.setStatus("cancelled");
            when(appointmentMapper.selectById(1)).thenReturn(testAppointment);

            boolean result = appointmentService.cancelAppointment(1, 1);

            assertFalse(result);
            verify(appointmentMapper, times(1)).selectById(1);
            verify(appointmentMapper, never()).updateById(any(Appointment.class));
        }

        @Test
        @DisplayName("取消预约 - 已完成的预约")
        void testCancelAppointment_AlreadyCompleted() {
            testAppointment.setStatus("completed");
            when(appointmentMapper.selectById(1)).thenReturn(testAppointment);

            boolean result = appointmentService.cancelAppointment(1, 1);

            assertFalse(result);
            verify(appointmentMapper, times(1)).selectById(1);
            verify(appointmentMapper, never()).updateById(any(Appointment.class));
        }

        @Test
        @DisplayName("取消预约 - 已确认的预约可以取消")
        void testCancelAppointment_ConfirmedCanBeCancelled() {
            testAppointment.setStatus("confirmed");
            when(appointmentMapper.selectById(1)).thenReturn(testAppointment);
            when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

            boolean result = appointmentService.cancelAppointment(1, 1);

            assertTrue(result);
            verify(appointmentMapper, times(1)).selectById(1);
            verify(appointmentMapper, times(1)).updateById(any(Appointment.class));
        }
    }

    @Nested
    @DisplayName("预约统计功能测试")
    class GetAppointmentStatsTests {

        @Test
        @DisplayName("统计各状态预约数量")
        void testGetAppointmentStats() {
            when(appointmentMapper.selectCount(any(LambdaQueryWrapper.class)))
                    .thenReturn(10L)
                    .thenReturn(3L)
                    .thenReturn(2L)
                    .thenReturn(4L)
                    .thenReturn(1L);

            Map<String, Long> stats = appointmentService.getAppointmentStats(1);

            assertNotNull(stats);
            assertEquals(10L, stats.get("total"));
            assertEquals(3L, stats.get("pending"));
            assertEquals(2L, stats.get("confirmed"));
            assertEquals(4L, stats.get("completed"));
            assertEquals(1L, stats.get("cancelled"));
            verify(appointmentMapper, times(5)).selectCount(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("统计预约数量 - 无预约")
        void testGetAppointmentStats_NoAppointments() {
            when(appointmentMapper.selectCount(any(LambdaQueryWrapper.class)))
                    .thenReturn(0L)
                    .thenReturn(0L)
                    .thenReturn(0L)
                    .thenReturn(0L)
                    .thenReturn(0L);

            Map<String, Long> stats = appointmentService.getAppointmentStats(999);

            assertNotNull(stats);
            assertEquals(0L, stats.get("total"));
            assertEquals(0L, stats.get("pending"));
            assertEquals(0L, stats.get("confirmed"));
            assertEquals(0L, stats.get("completed"));
            assertEquals(0L, stats.get("cancelled"));
        }
    }

    @Nested
    @DisplayName("预约更新功能测试")
    class UpdateAppointmentTests {

        @Test
        @DisplayName("正常更新预约")
        void testUpdate_Success() {
            testAppointment.setNotes("Updated notes");
            when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

            Appointment result = appointmentService.update(testAppointment);

            assertNotNull(result);
            assertEquals("Updated notes", result.getNotes());
            verify(appointmentMapper, times(1)).updateById(testAppointment);
        }

        @Test
        @DisplayName("更新预约状态")
        void testUpdate_Status() {
            testAppointment.setStatus("confirmed");
            when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

            Appointment result = appointmentService.update(testAppointment);

            assertNotNull(result);
            assertEquals("confirmed", result.getStatus());
            verify(appointmentMapper, times(1)).updateById(testAppointment);
        }

        @Test
        @DisplayName("更新预约时间")
        void testUpdate_AppointmentTime() {
            LocalDateTime newTime = LocalDateTime.now().plusDays(3);
            testAppointment.setAppointmentTime(newTime);
            when(appointmentMapper.updateById(any(Appointment.class))).thenReturn(1);

            Appointment result = appointmentService.update(testAppointment);

            assertNotNull(result);
            assertEquals(newTime, result.getAppointmentTime());
            verify(appointmentMapper, times(1)).updateById(testAppointment);
        }
    }

    @Nested
    @DisplayName("预约删除功能测试")
    class DeleteAppointmentTests {

        @Test
        @DisplayName("正常删除预约")
        void testDelete_Success() {
            when(appointmentMapper.deleteById(1)).thenReturn(1);

            appointmentService.delete(1);

            verify(appointmentMapper, times(1)).deleteById(1);
        }

        @Test
        @DisplayName("删除不存在的预约")
        void testDelete_NotExists() {
            when(appointmentMapper.deleteById(999)).thenReturn(0);

            appointmentService.delete(999);

            verify(appointmentMapper, times(1)).deleteById(999);
        }
    }

    @Nested
    @DisplayName("已购服务查询功能测试")
    class FindPurchasedServicesTests {

        @Test
        @DisplayName("查询已购服务列表")
        void testFindPurchasedServices() {
            Page<Appointment> page = new Page<>(1, 10);
            page.setRecords(Arrays.asList(testAppointment));
            page.setTotal(1);

            when(appointmentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            Map<String, Object> result = appointmentService.findPurchasedServices(1, null, null, 1, 10);

            assertNotNull(result);
            assertNotNull(result.get("data"));
            assertEquals(1L, result.get("total"));
            assertEquals(1, result.get("page"));
            assertEquals(10, result.get("pageSize"));
            verify(appointmentMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("查询已购服务 - 按关键字筛选")
        void testFindPurchasedServices_WithKeyword() {
            Page<Appointment> page = new Page<>(1, 10);
            page.setRecords(Arrays.asList(testAppointment));
            page.setTotal(1);

            when(appointmentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            Map<String, Object> result = appointmentService.findPurchasedServices(1, "test", null, 1, 10);

            assertNotNull(result);
            assertEquals(1L, result.get("total"));
            verify(appointmentMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("查询已购服务 - 按状态筛选")
        void testFindPurchasedServices_WithStatus() {
            Page<Appointment> page = new Page<>(1, 10);
            page.setRecords(Arrays.asList(testAppointment));
            page.setTotal(1);

            when(appointmentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            Map<String, Object> result = appointmentService.findPurchasedServices(1, null, "pending", 1, 10);

            assertNotNull(result);
            assertEquals(1L, result.get("total"));
            verify(appointmentMapper, times(1)).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
        }
    }
}
