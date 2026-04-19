package com.petshop.controller.api;

import com.petshop.dto.NotificationDTO;
import com.petshop.entity.Notification;
import com.petshop.entity.User;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("用户通知API测试")
public class UserApiControllerNotificationTest extends UserApiControllerTestBase {

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
        } catch (Exception e) {
            throw new RuntimeException("注入mock失败", e);
        }
    }

    @Nested
    @DisplayName("获取通知列表测试")
    class GetNotificationsTests {

        @Test
        @DisplayName("成功获取通知列表")
        void testGetNotifications_Success() throws Exception {
            mockUserSession();

            List<NotificationDTO> notifications = createTestNotificationDTOs(3);
            when(notificationService.findByUserId(eq(testUserId), isNull(), isNull())).thenReturn(notifications);

            var result = performGet("/api/user/notifications");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(3));

            verify(notificationService).findByUserId(testUserId, null, null);
        }

        @Test
        @DisplayName("成功获取通知列表（按类型筛选）")
        void testGetNotifications_WithTypeFilter() throws Exception {
            mockUserSession();

            List<NotificationDTO> notifications = createTestNotificationDTOs(2);
            when(notificationService.findByUserId(eq(testUserId), eq("order"), isNull())).thenReturn(notifications);

            var result = mockMvc.perform(get("/api/user/notifications?type=order")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.length()").value(2));

            verify(notificationService).findByUserId(testUserId, "order", null);
        }

        @Test
        @DisplayName("成功获取通知列表（按已读状态筛选）")
        void testGetNotifications_WithReadStatusFilter() throws Exception {
            mockUserSession();

            List<NotificationDTO> notifications = createTestNotificationDTOs(2);
            when(notificationService.findByUserId(eq(testUserId), isNull(), eq(false))).thenReturn(notifications);

            var result = mockMvc.perform(get("/api/user/notifications?isRead=false")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.length()").value(2));

            verify(notificationService).findByUserId(testUserId, null, false);
        }

        @Test
        @DisplayName("成功获取通知列表（同时按类型和已读状态筛选）")
        void testGetNotifications_WithTypeAndReadStatusFilter() throws Exception {
            mockUserSession();

            List<NotificationDTO> notifications = createTestNotificationDTOs(1);
            when(notificationService.findByUserId(eq(testUserId), eq("appointment"), eq(false))).thenReturn(notifications);

            var result = mockMvc.perform(get("/api/user/notifications?type=appointment&isRead=false")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.length()").value(1));

            verify(notificationService).findByUserId(testUserId, "appointment", false);
        }

        @Test
        @DisplayName("空通知列表")
        void testGetNotifications_EmptyList() throws Exception {
            mockUserSession();

            when(notificationService.findByUserId(eq(testUserId), isNull(), isNull())).thenReturn(Collections.emptyList());

            var result = performGet("/api/user/notifications");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetNotifications_Unauthorized() throws Exception {
            var result = mockMvc.perform(get("/api/user/notifications")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetNotifications_ServiceException() throws Exception {
            mockUserSession();

            when(notificationService.findByUserId(anyInt(), any(), any()))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            var result = performGet("/api/user/notifications");

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("标记单个通知已读测试")
    class MarkAsReadTests {

        @Test
        @DisplayName("成功标记通知为已读")
        void testMarkAsRead_Success() throws Exception {
            mockUserSession();

            Notification notification = createTestNotification(1, testUserId);
            when(notificationService.findById(1)).thenReturn(notification);
            when(notificationService.isOwner(1, testUserId)).thenReturn(true);
            doNothing().when(notificationService).markAsRead(1);

            var result = performPut("/api/user/notifications/1/read", new HashMap<>(), 1);

            assertSuccess(result);

            verify(notificationService).markAsRead(1);
        }

        @Test
        @DisplayName("通知不存在返回404")
        void testMarkAsRead_NotFound() throws Exception {
            mockUserSession();

            when(notificationService.findById(999)).thenReturn(null);

            var result = performPut("/api/user/notifications/999/read", new HashMap<>(), 999);

            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("无权限标记返回403")
        void testMarkAsRead_Forbidden() throws Exception {
            mockUserSession();

            Notification notification = createTestNotification(1, 999);
            when(notificationService.findById(1)).thenReturn(notification);
            when(notificationService.isOwner(1, testUserId)).thenReturn(false);

            var result = performPut("/api/user/notifications/1/read", new HashMap<>(), 1);

            result.andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("未登录返回401")
        void testMarkAsRead_Unauthorized() throws Exception {
            var result = mockMvc.perform(put("/api/user/notifications/1/read")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testMarkAsRead_ServiceException() throws Exception {
            mockUserSession();

            when(notificationService.findById(1)).thenThrow(new RuntimeException("查询失败"));

            var result = performPut("/api/user/notifications/1/read", new HashMap<>(), 1);

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("标记所有通知已读测试")
    class MarkAllAsReadTests {

        @Test
        @DisplayName("成功标记所有通知为已读")
        void testMarkAllAsRead_Success() throws Exception {
            mockUserSession();

            doNothing().when(notificationService).markAllAsRead(testUserId);

            var result = performPut("/api/user/notifications/read-all", new HashMap<>());

            assertSuccess(result);

            verify(notificationService).markAllAsRead(testUserId);
        }

        @Test
        @DisplayName("未登录返回401")
        void testMarkAllAsRead_Unauthorized() throws Exception {
            var result = mockMvc.perform(put("/api/user/notifications/read-all")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testMarkAllAsRead_ServiceException() throws Exception {
            mockUserSession();

            doThrow(new RuntimeException("更新失败")).when(notificationService).markAllAsRead(testUserId);

            var result = performPut("/api/user/notifications/read-all", new HashMap<>());

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("批量标记已读测试")
    class BatchMarkAsReadTests {

        @Test
        @DisplayName("成功批量标记通知为已读")
        void testBatchMarkAsRead_Success() throws Exception {
            mockUserSession();

            List<Integer> ids = Arrays.asList(1, 2, 3);
            Map<String, Object> request = new HashMap<>();
            request.put("ids", ids);

            doNothing().when(notificationService).markBatchAsRead(ids, testUserId);

            var result = performPut("/api/user/notifications/batch-read", request);

            assertSuccess(result);

            verify(notificationService).markBatchAsRead(ids, testUserId);
        }

        @Test
        @DisplayName("空ID列表返回400")
        void testBatchMarkAsRead_EmptyIds() throws Exception {
            mockUserSession();

            Map<String, Object> request = new HashMap<>();
            request.put("ids", Collections.emptyList());

            var result = performPut("/api/user/notifications/batch-read", request);

            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("null ID列表返回400")
        void testBatchMarkAsRead_NullIds() throws Exception {
            mockUserSession();

            Map<String, Object> request = new HashMap<>();

            var result = performPut("/api/user/notifications/batch-read", request);

            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("未登录返回401")
        void testBatchMarkAsRead_Unauthorized() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));

            var result = mockMvc.perform(put("/api/user/notifications/batch-read")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testBatchMarkAsRead_ServiceException() throws Exception {
            mockUserSession();

            List<Integer> ids = Arrays.asList(1, 2, 3);
            Map<String, Object> request = new HashMap<>();
            request.put("ids", ids);

            doThrow(new RuntimeException("更新失败")).when(notificationService).markBatchAsRead(ids, testUserId);

            var result = performPut("/api/user/notifications/batch-read", request);

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("删除单个通知测试")
    class DeleteNotificationTests {

        @Test
        @DisplayName("成功删除通知")
        void testDeleteNotification_Success() throws Exception {
            mockUserSession();

            Notification notification = createTestNotification(1, testUserId);
            when(notificationService.findById(1)).thenReturn(notification);
            when(notificationService.isOwner(1, testUserId)).thenReturn(true);
            doNothing().when(notificationService).deleteNotification(1);

            var result = performDelete("/api/user/notifications/1", 1);

            assertSuccess(result);

            verify(notificationService).deleteNotification(1);
        }

        @Test
        @DisplayName("通知不存在返回404")
        void testDeleteNotification_NotFound() throws Exception {
            mockUserSession();

            when(notificationService.findById(999)).thenReturn(null);

            var result = performDelete("/api/user/notifications/999", 999);

            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("无权限删除返回403")
        void testDeleteNotification_Forbidden() throws Exception {
            mockUserSession();

            Notification notification = createTestNotification(1, 999);
            when(notificationService.findById(1)).thenReturn(notification);
            when(notificationService.isOwner(1, testUserId)).thenReturn(false);

            var result = performDelete("/api/user/notifications/1", 1);

            result.andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("未登录返回401")
        void testDeleteNotification_Unauthorized() throws Exception {
            var result = mockMvc.perform(delete("/api/user/notifications/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testDeleteNotification_ServiceException() throws Exception {
            mockUserSession();

            when(notificationService.findById(1)).thenThrow(new RuntimeException("查询失败"));

            var result = performDelete("/api/user/notifications/1", 1);

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("批量删除通知测试")
    class BatchDeleteNotificationsTests {

        @Test
        @DisplayName("成功批量删除通知")
        void testBatchDelete_Success() throws Exception {
            mockUserSession();

            List<Integer> ids = Arrays.asList(1, 2, 3);
            Map<String, Object> request = new HashMap<>();
            request.put("ids", ids);

            doNothing().when(notificationService).deleteBatch(ids, testUserId);

            var result = mockMvc.perform(delete("/api/user/notifications/batch")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print());

            result.andExpect(status().isOk());

            verify(notificationService).deleteBatch(ids, testUserId);
        }

        @Test
        @DisplayName("空ID列表返回400")
        void testBatchDelete_EmptyIds() throws Exception {
            mockUserSession();

            Map<String, Object> request = new HashMap<>();
            request.put("ids", Collections.emptyList());

            var result = mockMvc.perform(delete("/api/user/notifications/batch")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print());

            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("null ID列表返回400")
        void testBatchDelete_NullIds() throws Exception {
            mockUserSession();

            Map<String, Object> request = new HashMap<>();

            var result = mockMvc.perform(delete("/api/user/notifications/batch")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print());

            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("未登录返回401")
        void testBatchDelete_Unauthorized() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));

            var result = mockMvc.perform(delete("/api/user/notifications/batch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testBatchDelete_ServiceException() throws Exception {
            mockUserSession();

            List<Integer> ids = Arrays.asList(1, 2, 3);
            Map<String, Object> request = new HashMap<>();
            request.put("ids", ids);

            doThrow(new RuntimeException("删除失败")).when(notificationService).deleteBatch(ids, testUserId);

            var result = mockMvc.perform(delete("/api/user/notifications/batch")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print());

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("获取未读通知数量测试")
    class GetUnreadCountTests {

        @Test
        @DisplayName("成功获取未读通知数量")
        void testGetUnreadCount_Success() throws Exception {
            mockUserSession();

            Map<String, Object> response = new HashMap<>();
            response.put("count", 5L);
            when(notificationService.getUnreadCountResponse(testUserId)).thenReturn(response);

            var result = performGet("/api/user/notifications/unread-count");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.count").value(5));

            verify(notificationService).getUnreadCountResponse(testUserId);
        }

        @Test
        @DisplayName("无未读通知时返回0")
        void testGetUnreadCount_Zero() throws Exception {
            mockUserSession();

            Map<String, Object> response = new HashMap<>();
            response.put("count", 0L);
            when(notificationService.getUnreadCountResponse(testUserId)).thenReturn(response);

            var result = performGet("/api/user/notifications/unread-count");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.count").value(0));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetUnreadCount_Unauthorized() throws Exception {
            var result = mockMvc.perform(get("/api/user/notifications/unread-count")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetUnreadCount_ServiceException() throws Exception {
            mockUserSession();

            when(notificationService.getUnreadCountResponse(testUserId))
                    .thenThrow(new RuntimeException("查询失败"));

            var result = performGet("/api/user/notifications/unread-count");

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("通知类型边界-系统通知")
        void testGetNotifications_SystemType() throws Exception {
            mockUserSession();

            List<NotificationDTO> notifications = createTestNotificationDTOsWithType(1, "system");
            when(notificationService.findByUserId(eq(testUserId), eq("system"), isNull())).thenReturn(notifications);

            var result = mockMvc.perform(get("/api/user/notifications?type=system")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data[0].type").value("system"));
        }

        @Test
        @DisplayName("通知类型边界-订单通知")
        void testGetNotifications_OrderType() throws Exception {
            mockUserSession();

            List<NotificationDTO> notifications = createTestNotificationDTOsWithType(1, "order");
            when(notificationService.findByUserId(eq(testUserId), eq("order"), isNull())).thenReturn(notifications);

            var result = mockMvc.perform(get("/api/user/notifications?type=order")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data[0].type").value("order"));
        }

        @Test
        @DisplayName("通知类型边界-预约通知")
        void testGetNotifications_AppointmentType() throws Exception {
            mockUserSession();

            List<NotificationDTO> notifications = createTestNotificationDTOsWithType(1, "appointment");
            when(notificationService.findByUserId(eq(testUserId), eq("appointment"), isNull())).thenReturn(notifications);

            var result = mockMvc.perform(get("/api/user/notifications?type=appointment")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            assertSuccess(result);
            result.andExpect(jsonPath("$.data[0].type").value("appointment"));
        }

        @Test
        @DisplayName("批量操作边界-单个ID")
        void testBatchMarkAsRead_SingleId() throws Exception {
            mockUserSession();

            List<Integer> ids = Collections.singletonList(1);
            Map<String, Object> request = new HashMap<>();
            request.put("ids", ids);

            doNothing().when(notificationService).markBatchAsRead(ids, testUserId);

            var result = performPut("/api/user/notifications/batch-read", request);

            assertSuccess(result);

            verify(notificationService).markBatchAsRead(ids, testUserId);
        }

        @Test
        @DisplayName("批量操作边界-大量ID")
        void testBatchMarkAsRead_LargeIds() throws Exception {
            mockUserSession();

            List<Integer> ids = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                ids.add(i);
            }
            Map<String, Object> request = new HashMap<>();
            request.put("ids", ids);

            doNothing().when(notificationService).markBatchAsRead(ids, testUserId);

            var result = performPut("/api/user/notifications/batch-read", request);

            assertSuccess(result);

            verify(notificationService).markBatchAsRead(ids, testUserId);
        }

        @Test
        @DisplayName("未读数量边界-大量未读")
        void testGetUnreadCount_LargeCount() throws Exception {
            mockUserSession();

            Map<String, Object> response = new HashMap<>();
            response.put("count", 999999L);
            when(notificationService.getUnreadCountResponse(testUserId)).thenReturn(response);

            var result = performGet("/api/user/notifications/unread-count");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.count").value(999999));
        }

        @Test
        @DisplayName("通知内容边界-超长标题")
        void testGetNotifications_LongTitle() throws Exception {
            mockUserSession();

            String longTitle = "a".repeat(255);
            List<NotificationDTO> notifications = createTestNotificationDTOs(1);
            notifications.get(0).setTitle(longTitle);
            when(notificationService.findByUserId(eq(testUserId), isNull(), isNull())).thenReturn(notifications);

            var result = performGet("/api/user/notifications");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data[0].title").value(longTitle));
        }

        @Test
        @DisplayName("通知内容边界-超长内容")
        void testGetNotifications_LongContent() throws Exception {
            mockUserSession();

            String longContent = "a".repeat(5000);
            List<NotificationDTO> notifications = createTestNotificationDTOs(1);
            notifications.get(0).setContent(longContent);
            when(notificationService.findByUserId(eq(testUserId), isNull(), isNull())).thenReturn(notifications);

            var result = performGet("/api/user/notifications");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data[0].content").value(longContent));
        }
    }

    @Nested
    @DisplayName("所有权验证测试")
    class OwnershipValidationTests {

        @Test
        @DisplayName("标记已读时验证所有权-其他用户的通知")
        void testMarkAsRead_OtherUserNotification() throws Exception {
            mockUserSession();

            Notification notification = createTestNotification(1, 999);
            when(notificationService.findById(1)).thenReturn(notification);
            when(notificationService.isOwner(1, testUserId)).thenReturn(false);

            var result = performPut("/api/user/notifications/1/read", new HashMap<>(), 1);

            result.andExpect(status().isForbidden());

            verify(notificationService, never()).markAsRead(anyInt());
        }

        @Test
        @DisplayName("删除时验证所有权-其他用户的通知")
        void testDelete_OtherUserNotification() throws Exception {
            mockUserSession();

            Notification notification = createTestNotification(1, 999);
            when(notificationService.findById(1)).thenReturn(notification);
            when(notificationService.isOwner(1, testUserId)).thenReturn(false);

            var result = performDelete("/api/user/notifications/1", 1);

            result.andExpect(status().isForbidden());

            verify(notificationService, never()).deleteNotification(anyInt());
        }

        @Test
        @DisplayName("批量删除时验证所有权-混合ID列表")
        void testBatchDelete_MixedOwnership() throws Exception {
            mockUserSession();

            List<Integer> ids = Arrays.asList(1, 2, 3);
            Map<String, Object> request = new HashMap<>();
            request.put("ids", ids);

            doNothing().when(notificationService).deleteBatch(ids, testUserId);

            var result = mockMvc.perform(delete("/api/user/notifications/batch")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print());

            result.andExpect(status().isOk());

            verify(notificationService).deleteBatch(ids, testUserId);
        }
    }

    @Nested
    @DisplayName("并发操作测试")
    class ConcurrencyTests {

        @Test
        @DisplayName("重复标记已读")
        void testMarkAsRead_AlreadyRead() throws Exception {
            mockUserSession();

            Notification notification = createTestNotification(1, testUserId);
            notification.setIsRead(true);
            when(notificationService.findById(1)).thenReturn(notification);
            when(notificationService.isOwner(1, testUserId)).thenReturn(true);
            doNothing().when(notificationService).markAsRead(1);

            var result = performPut("/api/user/notifications/1/read", new HashMap<>(), 1);

            assertSuccess(result);

            verify(notificationService).markAsRead(1);
        }

        @Test
        @DisplayName("重复删除通知")
        void testDeleteNotification_Twice() throws Exception {
            mockUserSession();

            Notification notification = createTestNotification(1, testUserId);
            when(notificationService.findById(1)).thenReturn(notification);
            when(notificationService.isOwner(1, testUserId)).thenReturn(true);
            doNothing().when(notificationService).deleteNotification(1);

            var result = performDelete("/api/user/notifications/1", 1);

            assertSuccess(result);

            verify(notificationService).deleteNotification(1);
        }
    }

    private Notification createTestNotification(Integer id, Integer userId) {
        Notification notification = new Notification();
        notification.setId(id);
        notification.setUserId(userId);
        notification.setType("system");
        notification.setTitle("测试通知标题" + id);
        notification.setSummary("测试通知摘要" + id);
        notification.setContent("测试通知内容" + id);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        return notification;
    }

    private List<NotificationDTO> createTestNotificationDTOs(int count) {
        List<NotificationDTO> dtos = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            NotificationDTO dto = NotificationDTO.builder()
                    .id(i)
                    .type("system")
                    .title("测试通知标题" + i)
                    .summary("测试通知摘要" + i)
                    .content("测试通知内容" + i)
                    .isRead(false)
                    .createTime(LocalDateTime.now().minusHours(i))
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    private List<NotificationDTO> createTestNotificationDTOsWithType(int count, String type) {
        List<NotificationDTO> dtos = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            NotificationDTO dto = NotificationDTO.builder()
                    .id(i)
                    .type(type)
                    .title(type + "通知标题" + i)
                    .summary(type + "通知摘要" + i)
                    .content(type + "通知内容" + i)
                    .isRead(false)
                    .createTime(LocalDateTime.now().minusHours(i))
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }
}
