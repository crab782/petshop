package com.petshop.controller.api;

import com.petshop.entity.ScheduledTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("管理员任务管理API测试")
public class AdminApiControllerTasksTest extends AdminApiControllerTestBase {

    private AdminApiController controller;

    @BeforeEach
    public void setupController() {
        controller = new AdminApiController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "scheduledTaskService", scheduledTaskService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private ScheduledTask createTestTask(Integer id, String name, String type, String status) {
        ScheduledTask task = new ScheduledTask();
        task.setId(id);
        task.setName(name);
        task.setType(type);
        task.setStatus(status);
        task.setCronExpression("0 0 12 * * ?");
        task.setDescription("测试任务描述");
        task.setLastExecuteTime(LocalDateTime.now().minusHours(1));
        task.setNextExecuteTime(LocalDateTime.now().plusHours(23));
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return task;
    }

    @Nested
    @DisplayName("GET /api/admin/tasks - 获取任务列表")
    class GetTasksTests {

        @Test
        @DisplayName("成功获取任务列表")
        void testGetTasks_Success() throws Exception {
            ScheduledTask task1 = createTestTask(1, "任务1", "cleanup", "enabled");
            ScheduledTask task2 = createTestTask(2, "任务2", "backup", "enabled");
            List<ScheduledTask> tasks = Arrays.asList(task1, task2);
            Page<ScheduledTask> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 10), 2);

            when(scheduledTaskService.getTasks(any(), any(), any(), eq(0), eq(10))).thenReturn(taskPage);

            var result = performGet("/api/admin/tasks");

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.data.length()").value(2));

            verify(scheduledTaskService).getTasks(any(), any(), any(), eq(0), eq(10));
        }

        @Test
        @DisplayName("带搜索条件获取任务列表")
        void testGetTasks_WithSearch() throws Exception {
            ScheduledTask task = createTestTask(1, "搜索任务", "cleanup", "enabled");
            List<ScheduledTask> tasks = Collections.singletonList(task);
            Page<ScheduledTask> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 10), 1);

            when(scheduledTaskService.getTasks(eq("搜索"), any(), any(), eq(0), eq(10))).thenReturn(taskPage);

            var result = mockMvc.perform(get("/api/admin/tasks")
                    .sessionAttr("admin", testAdmin)
                    .param("keyword", "搜索")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(res -> {});

            assertPaginatedResponse(result);
            verify(scheduledTaskService).getTasks(eq("搜索"), any(), any(), eq(0), eq(10));
        }

        @Test
        @DisplayName("按类型筛选")
        void testGetTasks_ByType() throws Exception {
            ScheduledTask task = createTestTask(1, "任务", "cleanup", "enabled");
            List<ScheduledTask> tasks = Collections.singletonList(task);
            Page<ScheduledTask> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 10), 1);

            when(scheduledTaskService.getTasks(any(), eq("cleanup"), any(), eq(0), eq(10))).thenReturn(taskPage);

            var result = mockMvc.perform(get("/api/admin/tasks")
                    .sessionAttr("admin", testAdmin)
                    .param("type", "cleanup")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(res -> {});

            assertPaginatedResponse(result);
        }

        @Test
        @DisplayName("按状态筛选")
        void testGetTasks_ByStatus() throws Exception {
            ScheduledTask task = createTestTask(1, "任务", "cleanup", "enabled");
            List<ScheduledTask> tasks = Collections.singletonList(task);
            Page<ScheduledTask> taskPage = new PageImpl<>(tasks, PageRequest.of(0, 10), 1);

            when(scheduledTaskService.getTasks(any(), any(), eq("enabled"), eq(0), eq(10))).thenReturn(taskPage);

            var result = mockMvc.perform(get("/api/admin/tasks")
                    .sessionAttr("admin", testAdmin)
                    .param("status", "enabled")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(res -> {});

            assertPaginatedResponse(result);
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetTasks_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/tasks")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(scheduledTaskService, never()).getTasks(any(), any(), any(), anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("POST /api/admin/tasks - 创建任务")
    class CreateTaskTests {

        @Test
        @DisplayName("成功创建任务")
        void testCreateTask_Success() throws Exception {
            ScheduledTask newTask = new ScheduledTask();
            newTask.setName("新任务");
            newTask.setType("cleanup");
            newTask.setCronExpression("0 0 12 * * ?");
            newTask.setDescription("任务描述");

            ScheduledTask savedTask = createTestTask(1, "新任务", "cleanup", "enabled");

            when(scheduledTaskService.create(any(ScheduledTask.class))).thenReturn(savedTask);

            var result = performPost("/api/admin/tasks", newTask);

            assertSuccess(result, "Task created successfully");
            result.andExpect(jsonPath("$.data.name").value("新任务"));

            verify(scheduledTaskService).create(any(ScheduledTask.class));
        }

        @Test
        @DisplayName("空任务名称返回400")
        void testCreateTask_EmptyName() throws Exception {
            ScheduledTask newTask = new ScheduledTask();
            newTask.setName("");
            newTask.setType("cleanup");

            var result = performPost("/api/admin/tasks", newTask);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Task name cannot be empty"));

            verify(scheduledTaskService, never()).create(any(ScheduledTask.class));
        }

        @Test
        @DisplayName("空任务类型返回400")
        void testCreateTask_EmptyType() throws Exception {
            ScheduledTask newTask = new ScheduledTask();
            newTask.setName("新任务");
            newTask.setType("");

            var result = performPost("/api/admin/tasks", newTask);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Task type cannot be empty"));
        }

        @Test
        @DisplayName("null任务名称返回400")
        void testCreateTask_NullName() throws Exception {
            ScheduledTask newTask = new ScheduledTask();
            newTask.setName(null);
            newTask.setType("cleanup");

            var result = performPost("/api/admin/tasks", newTask);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Task name cannot be empty"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testCreateTask_Unauthorized() throws Exception {
            ScheduledTask newTask = new ScheduledTask();
            newTask.setName("新任务");
            newTask.setType("cleanup");

            mockMvc.perform(post("/api/admin/tasks")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(newTask)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(scheduledTaskService, never()).create(any(ScheduledTask.class));
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/tasks/{id} - 更新任务")
    class UpdateTaskTests {

        @Test
        @DisplayName("成功更新任务")
        void testUpdateTask_Success() throws Exception {
            ScheduledTask existingTask = createTestTask(1, "原任务", "cleanup", "enabled");

            ScheduledTask updateData = new ScheduledTask();
            updateData.setName("更新任务");
            updateData.setType("backup");
            updateData.setCronExpression("0 0 0 * * ?");
            updateData.setDescription("更新描述");

            ScheduledTask updatedTask = createTestTask(1, "更新任务", "backup", "enabled");

            when(scheduledTaskService.findById(1)).thenReturn(existingTask);
            when(scheduledTaskService.update(eq(1), any(ScheduledTask.class))).thenReturn(updatedTask);

            var result = performPut("/api/admin/tasks/{id}", updateData, 1);

            assertSuccess(result, "Task updated successfully");
            result.andExpect(jsonPath("$.data.name").value("更新任务"));

            verify(scheduledTaskService).findById(1);
            verify(scheduledTaskService).update(eq(1), any(ScheduledTask.class));
        }

        @Test
        @DisplayName("任务不存在返回404")
        void testUpdateTask_NotFound() throws Exception {
            when(scheduledTaskService.findById(999)).thenReturn(null);

            ScheduledTask updateData = new ScheduledTask();
            updateData.setName("更新任务");

            var result = performPut("/api/admin/tasks/{id}", updateData, 999);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("Task not found"));

            verify(scheduledTaskService).findById(999);
            verify(scheduledTaskService, never()).update(anyInt(), any(ScheduledTask.class));
        }

        @Test
        @DisplayName("未登录返回401")
        void testUpdateTask_Unauthorized() throws Exception {
            ScheduledTask updateData = new ScheduledTask();
            updateData.setName("更新任务");

            mockMvc.perform(put("/api/admin/tasks/1")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(updateData)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(scheduledTaskService, never()).findById(anyInt());
        }
    }

    @Nested
    @DisplayName("DELETE /api/admin/tasks/{id} - 删除任务")
    class DeleteTaskTests {

        @Test
        @DisplayName("成功删除任务")
        void testDeleteTask_Success() throws Exception {
            ScheduledTask existingTask = createTestTask(1, "任务", "cleanup", "enabled");

            when(scheduledTaskService.findById(1)).thenReturn(existingTask);
            doNothing().when(scheduledTaskService).delete(1);

            var result = performDelete("/api/admin/tasks/{id}", 1);

            assertNoContent(result);

            verify(scheduledTaskService).findById(1);
            verify(scheduledTaskService).delete(1);
        }

        @Test
        @DisplayName("任务不存在返回404")
        void testDeleteTask_NotFound() throws Exception {
            when(scheduledTaskService.findById(999)).thenReturn(null);

            var result = performDelete("/api/admin/tasks/{id}", 999);

            assertNotFound(result);

            verify(scheduledTaskService).findById(999);
            verify(scheduledTaskService, never()).delete(anyInt());
        }

        @Test
        @DisplayName("未登录返回401")
        void testDeleteTask_Unauthorized() throws Exception {
            mockMvc.perform(delete("/api/admin/tasks/1")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(scheduledTaskService, never()).findById(anyInt());
            verify(scheduledTaskService, never()).delete(anyInt());
        }
    }

    @Nested
    @DisplayName("POST /api/admin/tasks/{id}/execute - 执行任务")
    class ExecuteTaskTests {

        @Test
        @DisplayName("成功执行任务")
        void testExecuteTask_Success() throws Exception {
            ScheduledTask task = createTestTask(1, "任务", "cleanup", "enabled");

            Map<String, Object> executeResult = new HashMap<>();
            executeResult.put("taskId", 1);
            executeResult.put("taskName", "任务");
            executeResult.put("taskType", "cleanup");
            executeResult.put("executeTime", LocalDateTime.now());
            executeResult.put("status", "success");
            executeResult.put("message", "Task executed successfully");

            when(scheduledTaskService.findById(1)).thenReturn(task);
            when(scheduledTaskService.executeTask(1)).thenReturn(executeResult);

            var result = performPost("/api/admin/tasks/{id}/execute", null, 1);

            assertSuccess(result, "Task executed successfully");
            result.andExpect(jsonPath("$.data.taskId").value(1))
                    .andExpect(jsonPath("$.data.status").value("success"));

            verify(scheduledTaskService).findById(1);
            verify(scheduledTaskService).executeTask(1);
        }

        @Test
        @DisplayName("任务不存在返回404")
        void testExecuteTask_NotFound() throws Exception {
            when(scheduledTaskService.findById(999)).thenReturn(null);

            var result = performPost("/api/admin/tasks/{id}/execute", null, 999);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("Task not found"));
        }

        @Test
        @DisplayName("任务未启用返回400")
        void testExecuteTask_NotEnabled() throws Exception {
            ScheduledTask task = createTestTask(1, "任务", "cleanup", "disabled");

            when(scheduledTaskService.findById(1)).thenReturn(task);
            when(scheduledTaskService.executeTask(1))
                    .thenThrow(new IllegalStateException("Task is not enabled"));

            var result = performPost("/api/admin/tasks/{id}/execute", null, 1);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400));
        }

        @Test
        @DisplayName("未登录返回401")
        void testExecuteTask_Unauthorized() throws Exception {
            mockMvc.perform(post("/api/admin/tasks/1/execute")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(scheduledTaskService, never()).findById(anyInt());
            verify(scheduledTaskService, never()).executeTask(anyInt());
        }
    }
}
