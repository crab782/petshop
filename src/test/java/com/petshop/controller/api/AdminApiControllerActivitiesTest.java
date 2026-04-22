package com.petshop.controller.api;

import com.petshop.entity.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("管理员活动管理API测试")
public class AdminApiControllerActivitiesTest extends AdminApiControllerTestBase {

    private AdminApiController controller;

    @BeforeEach
    public void setupController() {
        controller = new AdminApiController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "activityService", activityService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private Activity createTestActivity(Integer id, String name, String type, String status) {
        Activity activity = new Activity();
        activity.setId(id);
        activity.setName(name);
        activity.setType(type);
        activity.setStatus(status);
        activity.setDescription("测试活动描述");
        activity.setStartTime(LocalDateTime.now().plusDays(1));
        activity.setEndTime(LocalDateTime.now().plusDays(7));
        activity.setCreatedAt(LocalDateTime.now());
        activity.setUpdatedAt(LocalDateTime.now());
        return activity;
    }

    @Nested
    @DisplayName("GET /api/admin/activities - 获取活动列表")
    class GetActivitiesTests {

        @Test
        @DisplayName("成功获取活动列表")
        void testGetActivities_Success() throws Exception {
            Activity activity1 = createTestActivity(1, "活动1", "promotion", "enabled");
            Activity activity2 = createTestActivity(2, "活动2", "discount", "enabled");
            List<Activity> activities = Arrays.asList(activity1, activity2);
            Page<Activity> activityPage = new PageImpl<>(activities, PageRequest.of(0, 10), 2);

            when(activityService.findAll(anyInt(), anyInt())).thenReturn(activityPage);

            var result = performGet("/api/admin/activities");

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.data.length()").value(2));

            verify(activityService).findAll(anyInt(), anyInt());
        }

        @Test
        @DisplayName("带搜索条件获取活动列表")
        void testGetActivities_WithSearch() throws Exception {
            Activity activity = createTestActivity(1, "搜索活动", "promotion", "enabled");
            List<Activity> activities = Collections.singletonList(activity);
            Page<Activity> activityPage = new PageImpl<>(activities, PageRequest.of(0, 10), 1);

            when(activityService.searchActivities(eq("搜索"), any(), any(), any(), any(), anyInt(), anyInt()))
                    .thenReturn(activityPage);

            var result = mockMvc.perform(get("/api/admin/activities")
                    .sessionAttr("admin", testAdmin)
                    .param("keyword", "搜索")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(res -> {});

            assertPaginatedResponse(result);
            verify(activityService).searchActivities(eq("搜索"), any(), any(), any(), any(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("按类型筛选")
        void testGetActivities_ByType() throws Exception {
            Activity activity = createTestActivity(1, "活动", "promotion", "enabled");
            List<Activity> activities = Collections.singletonList(activity);
            Page<Activity> activityPage = new PageImpl<>(activities, PageRequest.of(0, 10), 1);

            when(activityService.searchActivities(any(), eq("promotion"), any(), any(), any(), anyInt(), anyInt()))
                    .thenReturn(activityPage);

            var result = mockMvc.perform(get("/api/admin/activities")
                    .sessionAttr("admin", testAdmin)
                    .param("type", "promotion")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(res -> {});

            assertPaginatedResponse(result);
        }

        @Test
        @DisplayName("按状态筛选")
        void testGetActivities_ByStatus() throws Exception {
            Activity activity = createTestActivity(1, "活动", "promotion", "enabled");
            List<Activity> activities = Collections.singletonList(activity);
            Page<Activity> activityPage = new PageImpl<>(activities, PageRequest.of(0, 10), 1);

            when(activityService.searchActivities(any(), any(), eq("enabled"), any(), any(), anyInt(), anyInt()))
                    .thenReturn(activityPage);

            var result = mockMvc.perform(get("/api/admin/activities")
                    .sessionAttr("admin", testAdmin)
                    .param("status", "enabled")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(res -> {});

            assertPaginatedResponse(result);
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetActivities_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/activities")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(activityService, never()).findAll(anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("POST /api/admin/activities - 创建活动")
    class CreateActivityTests {

        @Test
        @DisplayName("成功创建活动")
        void testCreateActivity_Success() throws Exception {
            Activity newActivity = new Activity();
            newActivity.setName("新活动");
            newActivity.setType("promotion");
            newActivity.setDescription("活动描述");
            newActivity.setStartTime(LocalDateTime.now().plusDays(1));
            newActivity.setEndTime(LocalDateTime.now().plusDays(7));

            Activity savedActivity = createTestActivity(1, "新活动", "promotion", "enabled");

            when(activityService.create(any(Activity.class))).thenReturn(savedActivity);

            var result = performPost("/api/admin/activities", newActivity);

            assertSuccess(result, "Activity created successfully");
            result.andExpect(jsonPath("$.data.name").value("新活动"));

            verify(activityService).create(any(Activity.class));
        }

        @Test
        @DisplayName("空活动名称返回400")
        void testCreateActivity_EmptyName() throws Exception {
            Activity newActivity = new Activity();
            newActivity.setName("");
            newActivity.setType("promotion");

            var result = performPost("/api/admin/activities", newActivity);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Activity name cannot be empty"));

            verify(activityService, never()).create(any(Activity.class));
        }

        @Test
        @DisplayName("null活动名称返回400")
        void testCreateActivity_NullName() throws Exception {
            Activity newActivity = new Activity();
            newActivity.setName(null);
            newActivity.setType("promotion");

            var result = performPost("/api/admin/activities", newActivity);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Activity name cannot be empty"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testCreateActivity_Unauthorized() throws Exception {
            Activity newActivity = new Activity();
            newActivity.setName("新活动");
            newActivity.setType("promotion");

            mockMvc.perform(post("/api/admin/activities")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(newActivity)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(activityService, never()).create(any(Activity.class));
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/activities/{id} - 更新活动")
    class UpdateActivityTests {

        @Test
        @DisplayName("成功更新活动")
        void testUpdateActivity_Success() throws Exception {
            Activity existingActivity = createTestActivity(1, "原活动", "promotion", "enabled");

            Activity updateData = new Activity();
            updateData.setName("更新活动");
            updateData.setType("discount");
            updateData.setDescription("更新描述");

            Activity updatedActivity = createTestActivity(1, "更新活动", "discount", "enabled");

            when(activityService.findById(1)).thenReturn(existingActivity);
            when(activityService.update(eq(1), any(Activity.class))).thenReturn(updatedActivity);

            var result = performPut("/api/admin/activities/{id}", updateData, 1);

            assertSuccess(result, "Activity updated successfully");
            result.andExpect(jsonPath("$.data.name").value("更新活动"));

            verify(activityService).findById(1);
            verify(activityService).update(eq(1), any(Activity.class));
        }

        @Test
        @DisplayName("活动不存在返回404")
        void testUpdateActivity_NotFound() throws Exception {
            when(activityService.findById(999)).thenReturn(null);

            Activity updateData = new Activity();
            updateData.setName("更新活动");

            var result = performPut("/api/admin/activities/{id}", updateData, 999);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("Activity not found"));

            verify(activityService).findById(999);
            verify(activityService, never()).update(anyInt(), any(Activity.class));
        }

        @Test
        @DisplayName("未登录返回401")
        void testUpdateActivity_Unauthorized() throws Exception {
            Activity updateData = new Activity();
            updateData.setName("更新活动");

            mockMvc.perform(put("/api/admin/activities/1")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(updateData)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(activityService, never()).findById(anyInt());
        }
    }

    @Nested
    @DisplayName("DELETE /api/admin/activities/{id} - 删除活动")
    class DeleteActivityTests {

        @Test
        @DisplayName("成功删除活动")
        void testDeleteActivity_Success() throws Exception {
            Activity existingActivity = createTestActivity(1, "活动", "promotion", "enabled");

            when(activityService.findById(1)).thenReturn(existingActivity);
            doNothing().when(activityService).delete(1);

            var result = performDelete("/api/admin/activities/{id}", 1);

            assertNoContent(result);

            verify(activityService).findById(1);
            verify(activityService).delete(1);
        }

        @Test
        @DisplayName("活动不存在返回404")
        void testDeleteActivity_NotFound() throws Exception {
            when(activityService.findById(999)).thenReturn(null);

            var result = performDelete("/api/admin/activities/{id}", 999);

            assertNotFound(result);

            verify(activityService).findById(999);
            verify(activityService, never()).delete(anyInt());
        }

        @Test
        @DisplayName("未登录返回401")
        void testDeleteActivity_Unauthorized() throws Exception {
            mockMvc.perform(delete("/api/admin/activities/1")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(activityService, never()).findById(anyInt());
            verify(activityService, never()).delete(anyInt());
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/activities/{id}/status - 更新活动状态")
    class UpdateActivityStatusTests {

        @Test
        @DisplayName("成功启用活动")
        void testUpdateActivityStatus_Enable() throws Exception {
            Activity activity = createTestActivity(1, "活动", "promotion", "disabled");

            Activity updatedActivity = createTestActivity(1, "活动", "promotion", "enabled");

            when(activityService.findById(1)).thenReturn(activity);
            when(activityService.updateStatus(1, "enabled")).thenReturn(updatedActivity);

            var result = performPutWithParam("/api/admin/activities/{id}/status", "status", "enabled", 1);

            assertSuccess(result, "Activity status updated");
            result.andExpect(jsonPath("$.data.status").value("enabled"));

            verify(activityService).findById(1);
            verify(activityService).updateStatus(1, "enabled");
        }

        @Test
        @DisplayName("成功禁用活动")
        void testUpdateActivityStatus_Disable() throws Exception {
            Activity activity = createTestActivity(1, "活动", "promotion", "enabled");

            Activity updatedActivity = createTestActivity(1, "活动", "promotion", "disabled");

            when(activityService.findById(1)).thenReturn(activity);
            when(activityService.updateStatus(1, "disabled")).thenReturn(updatedActivity);

            var result = performPutWithParam("/api/admin/activities/{id}/status", "status", "disabled", 1);

            assertSuccess(result, "Activity status updated");
            result.andExpect(jsonPath("$.data.status").value("disabled"));
        }

        @Test
        @DisplayName("活动不存在返回404")
        void testUpdateActivityStatus_NotFound() throws Exception {
            when(activityService.findById(999)).thenReturn(null);

            var result = performPutWithParam("/api/admin/activities/{id}/status", "status", "enabled", 999);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("Activity not found"));
        }

        @Test
        @DisplayName("无效状态返回400")
        void testUpdateActivityStatus_InvalidStatus() throws Exception {
            Activity activity = createTestActivity(1, "活动", "promotion", "enabled");

            when(activityService.findById(1)).thenReturn(activity);

            var result = performPutWithParam("/api/admin/activities/{id}/status", "status", "invalid", 1);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Invalid status. Must be 'enabled' or 'disabled'"));

            verify(activityService, never()).updateStatus(anyInt(), anyString());
        }

        @Test
        @DisplayName("未登录返回401")
        void testUpdateActivityStatus_Unauthorized() throws Exception {
            mockMvc.perform(put("/api/admin/activities/1/status")
                    .param("status", "enabled")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(activityService, never()).findById(anyInt());
        }
    }
}
