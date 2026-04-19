package com.petshop.controller.api;

import com.petshop.entity.User;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("管理员用户管理API测试")
public class AdminApiControllerUsersTest extends AdminApiControllerTestBase {

    private AdminApiController controller;

    @BeforeEach
    public void setupController() {
        controller = new AdminApiController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "userService", userService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("GET /api/admin/users - 获取用户列表")
    class GetUsersTests {

        @Test
        @DisplayName("成功获取用户列表")
        void testGetUsers_Success() throws Exception {
            User user1 = TestDataFactory.createUser(1, "用户1", "user1@test.com");
            User user2 = TestDataFactory.createUser(2, "用户2", "user2@test.com");
            User user3 = TestDataFactory.createUser(3, "用户3", "user3@test.com");
            List<User> users = Arrays.asList(user1, user2, user3);

            when(userService.findAll()).thenReturn(users);

            var result = performGet("/api/admin/users");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].username").value("用户1"))
                    .andExpect(jsonPath("$[1].username").value("用户2"))
                    .andExpect(jsonPath("$[2].username").value("用户3"));

            verify(userService).findAll();
        }

        @Test
        @DisplayName("成功获取空用户列表")
        void testGetUsers_EmptyList() throws Exception {
            when(userService.findAll()).thenReturn(Collections.emptyList());

            var result = performGet("/api/admin/users");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(userService).findAll();
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetUsers_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/users")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(userService, never()).findAll();
        }
    }

    @Nested
    @DisplayName("DELETE /api/admin/users/{id} - 删除用户")
    class DeleteUserTests {

        @Test
        @DisplayName("成功删除用户")
        void testDeleteUser_Success() throws Exception {
            doNothing().when(userService).delete(1);

            var result = performDelete("/api/admin/users/{id}", 1);

            assertNoContent(result);

            verify(userService).delete(1);
        }

        @Test
        @DisplayName("未登录返回401")
        void testDeleteUser_Unauthorized() throws Exception {
            mockMvc.perform(delete("/api/admin/users/1")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(userService, never()).delete(anyInt());
        }

        @Test
        @DisplayName("删除不存在的用户")
        void testDeleteUser_NotFound() throws Exception {
            doNothing().when(userService).delete(999);

            var result = performDelete("/api/admin/users/{id}", 999);

            assertNoContent(result);

            verify(userService).delete(999);
        }
    }
}
