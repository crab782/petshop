package com.petshop.controller.api;

import com.petshop.entity.Appointment;
import com.petshop.entity.Merchant;
import com.petshop.entity.ProductOrder;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("商家商品订单和预约API测试")
public class MerchantApiControllerProductOrdersTest extends MerchantApiControllerTestBase {

    private MerchantApiController controller;

    @BeforeEach
    public void setupController() {
        controller = new MerchantApiController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "productOrderService", productOrderService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "appointmentService", appointmentService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("GET /api/merchant/orders - 获取商品订单列表")
    class GetOrdersTests {

        @Test
        @DisplayName("成功获取商品订单列表")
        void testGetOrders_Success() throws Exception {
            ProductOrder order1 = TestDataFactory.createProductOrder(1, testMerchant, "pending");
            ProductOrder order2 = TestDataFactory.createProductOrder(2, testMerchant, "paid");
            List<ProductOrder> orders = Arrays.asList(order1, order2);

            when(productOrderService.findByMerchantId(testMerchantId)).thenReturn(orders);

            var result = performGet("/api/merchant/orders");

            assertSuccess(result);
            assertListResponse(result, 2);
            result.andExpect(jsonPath("$.data[0].id").value(1))
                    .andExpect(jsonPath("$.data[0].status").value("pending"))
                    .andExpect(jsonPath("$.data[1].id").value(2))
                    .andExpect(jsonPath("$.data[1].status").value("paid"));

            verify(productOrderService).findByMerchantId(testMerchantId);
        }

        @Test
        @DisplayName("获取商品订单列表 - 空列表")
        void testGetOrders_EmptyList() throws Exception {
            when(productOrderService.findByMerchantId(testMerchantId)).thenReturn(Collections.emptyList());

            var result = performGet("/api/merchant/orders");

            assertSuccess(result);
            assertListResponse(result, 0);

            verify(productOrderService).findByMerchantId(testMerchantId);
        }

        @Test
        @DisplayName("获取商品订单列表 - 未登录返回401")
        void testGetOrders_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/merchant/orders")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));

            verify(productOrderService, never()).findByMerchantId(anyInt());
        }

        @Test
        @DisplayName("获取商品订单列表 - 服务层异常返回500")
        void testGetOrders_ServiceException() throws Exception {
            when(productOrderService.findByMerchantId(testMerchantId))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            var result = performGet("/api/merchant/orders");

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("获取订单列表失败：数据库连接失败"));

            verify(productOrderService).findByMerchantId(testMerchantId);
        }
    }

    @Nested
    @DisplayName("PUT /api/merchant/orders/{id}/status - 更新商品订单状态")
    class UpdateOrderStatusTests {

        @Test
        @DisplayName("成功更新商品订单状态 - pending到paid")
        void testUpdateOrderStatus_Success_PendingToPaid() throws Exception {
            ProductOrder order = TestDataFactory.createProductOrder(1, testMerchant, "pending");
            ProductOrder updatedOrder = TestDataFactory.createProductOrder(1, testMerchant, "paid");

            when(productOrderService.findById(1)).thenReturn(order);
            when(productOrderService.update(any(ProductOrder.class))).thenReturn(updatedOrder);

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "paid")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            assertSuccess(result, "订单状态更新成功");
            result.andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.status").value("paid"));

            verify(productOrderService).findById(1);
            verify(productOrderService).update(any(ProductOrder.class));
        }

        @Test
        @DisplayName("成功更新商品订单状态 - paid到shipped")
        void testUpdateOrderStatus_Success_PaidToShipped() throws Exception {
            ProductOrder order = TestDataFactory.createProductOrder(1, testMerchant, "paid");
            ProductOrder updatedOrder = TestDataFactory.createProductOrder(1, testMerchant, "shipped");

            when(productOrderService.findById(1)).thenReturn(order);
            when(productOrderService.update(any(ProductOrder.class))).thenReturn(updatedOrder);

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "shipped")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            assertSuccess(result, "订单状态更新成功");
            result.andExpect(jsonPath("$.data.status").value("shipped"));

            verify(productOrderService).findById(1);
            verify(productOrderService).update(any(ProductOrder.class));
        }

        @Test
        @DisplayName("成功更新商品订单状态 - shipped到completed")
        void testUpdateOrderStatus_Success_ShippedToCompleted() throws Exception {
            ProductOrder order = TestDataFactory.createProductOrder(1, testMerchant, "shipped");
            ProductOrder updatedOrder = TestDataFactory.createProductOrder(1, testMerchant, "completed");

            when(productOrderService.findById(1)).thenReturn(order);
            when(productOrderService.update(any(ProductOrder.class))).thenReturn(updatedOrder);

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "completed")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            assertSuccess(result, "订单状态更新成功");
            result.andExpect(jsonPath("$.data.status").value("completed"));

            verify(productOrderService).findById(1);
            verify(productOrderService).update(any(ProductOrder.class));
        }

        @Test
        @DisplayName("更新商品订单状态 - 未登录返回401")
        void testUpdateOrderStatus_Unauthorized() throws Exception {
            mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "paid")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));

            verify(productOrderService, never()).findById(anyInt());
            verify(productOrderService, never()).update(any());
        }

        @Test
        @DisplayName("更新商品订单状态 - 订单不存在返回404")
        void testUpdateOrderStatus_NotFound() throws Exception {
            when(productOrderService.findById(999)).thenReturn(null);

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 999)
                    .param("status", "paid")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("订单不存在"));

            verify(productOrderService).findById(999);
            verify(productOrderService, never()).update(any());
        }

        @Test
        @DisplayName("更新商品订单状态 - 无权限操作返回403")
        void testUpdateOrderStatus_Forbidden() throws Exception {
            Merchant otherMerchant = TestDataFactory.createMerchant(2);
            ProductOrder order = TestDataFactory.createProductOrder(1, otherMerchant, "pending");

            when(productOrderService.findById(1)).thenReturn(order);

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "paid")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            result.andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(403))
                    .andExpect(jsonPath("$.message").value("无权操作此订单"));

            verify(productOrderService).findById(1);
            verify(productOrderService, never()).update(any());
        }

        @Test
        @DisplayName("更新商品订单状态 - 无效状态值返回400")
        void testUpdateOrderStatus_InvalidStatus() throws Exception {
            ProductOrder order = TestDataFactory.createProductOrder(1, testMerchant, "pending");

            when(productOrderService.findById(1)).thenReturn(order);

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "invalid_status")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("无效的订单状态"));

            verify(productOrderService).findById(1);
            verify(productOrderService, never()).update(any());
        }

        @Test
        @DisplayName("更新商品订单状态 - 服务层异常返回500")
        void testUpdateOrderStatus_ServiceException() throws Exception {
            ProductOrder order = TestDataFactory.createProductOrder(1, testMerchant, "pending");

            when(productOrderService.findById(1)).thenReturn(order);
            when(productOrderService.update(any(ProductOrder.class)))
                    .thenThrow(new RuntimeException("更新失败"));

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "paid")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("更新订单状态失败：更新失败"));

            verify(productOrderService).findById(1);
            verify(productOrderService).update(any(ProductOrder.class));
        }
    }

    @Nested
    @DisplayName("GET /api/merchant/appointments - 获取预约列表")
    class GetAppointmentsTests {

        @Test
        @DisplayName("成功获取预约列表")
        void testGetAppointments_Success() throws Exception {
            Appointment appointment1 = TestDataFactory.createAppointment(1, testMerchant, "pending");
            Appointment appointment2 = TestDataFactory.createAppointment(2, testMerchant, "confirmed");
            List<Appointment> appointments = Arrays.asList(appointment1, appointment2);

            when(appointmentService.findByMerchantId(testMerchantId)).thenReturn(appointments);

            var result = performGet("/api/merchant/appointments");

            assertSuccess(result);
            assertListResponse(result, 2);
            result.andExpect(jsonPath("$.data[0].id").value(1))
                    .andExpect(jsonPath("$.data[0].status").value("pending"))
                    .andExpect(jsonPath("$.data[1].id").value(2))
                    .andExpect(jsonPath("$.data[1].status").value("confirmed"));

            verify(appointmentService).findByMerchantId(testMerchantId);
        }

        @Test
        @DisplayName("获取预约列表 - 空列表")
        void testGetAppointments_EmptyList() throws Exception {
            when(appointmentService.findByMerchantId(testMerchantId)).thenReturn(Collections.emptyList());

            var result = performGet("/api/merchant/appointments");

            assertSuccess(result);
            assertListResponse(result, 0);

            verify(appointmentService).findByMerchantId(testMerchantId);
        }

        @Test
        @DisplayName("获取预约列表 - 未登录返回401")
        void testGetAppointments_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/merchant/appointments")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));

            verify(appointmentService, never()).findByMerchantId(anyInt());
        }

        @Test
        @DisplayName("获取预约列表 - 服务层异常返回500")
        void testGetAppointments_ServiceException() throws Exception {
            when(appointmentService.findByMerchantId(testMerchantId))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            var result = performGet("/api/merchant/appointments");

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("获取预约列表失败：数据库连接失败"));

            verify(appointmentService).findByMerchantId(testMerchantId);
        }
    }

    @Nested
    @DisplayName("PUT /api/merchant/appointments/{id}/status - 更新预约状态")
    class UpdateAppointmentStatusTests {

        @Test
        @DisplayName("成功更新预约状态 - pending到confirmed")
        void testUpdateAppointmentStatus_Success_PendingToConfirmed() throws Exception {
            Appointment appointment = TestDataFactory.createAppointment(1, testMerchant, "pending");
            Appointment updatedAppointment = TestDataFactory.createAppointment(1, testMerchant, "confirmed");

            when(appointmentService.findById(1)).thenReturn(appointment);
            when(appointmentService.update(any(Appointment.class))).thenReturn(updatedAppointment);

            var result = mockMvc.perform(put("/api/merchant/appointments/{id}/status", 1)
                    .param("status", "confirmed")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            assertSuccess(result, "预约状态更新成功");
            result.andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.status").value("confirmed"));

            verify(appointmentService).findById(1);
            verify(appointmentService).update(any(Appointment.class));
        }

        @Test
        @DisplayName("成功更新预约状态 - pending到cancelled")
        void testUpdateAppointmentStatus_Success_PendingToCancelled() throws Exception {
            Appointment appointment = TestDataFactory.createAppointment(1, testMerchant, "pending");
            Appointment updatedAppointment = TestDataFactory.createAppointment(1, testMerchant, "cancelled");

            when(appointmentService.findById(1)).thenReturn(appointment);
            when(appointmentService.update(any(Appointment.class))).thenReturn(updatedAppointment);

            var result = mockMvc.perform(put("/api/merchant/appointments/{id}/status", 1)
                    .param("status", "cancelled")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            assertSuccess(result, "预约状态更新成功");
            result.andExpect(jsonPath("$.data.status").value("cancelled"));

            verify(appointmentService).findById(1);
            verify(appointmentService).update(any(Appointment.class));
        }

        @Test
        @DisplayName("成功更新预约状态 - confirmed到completed")
        void testUpdateAppointmentStatus_Success_ConfirmedToCompleted() throws Exception {
            Appointment appointment = TestDataFactory.createAppointment(1, testMerchant, "confirmed");
            Appointment updatedAppointment = TestDataFactory.createAppointment(1, testMerchant, "completed");

            when(appointmentService.findById(1)).thenReturn(appointment);
            when(appointmentService.update(any(Appointment.class))).thenReturn(updatedAppointment);

            var result = mockMvc.perform(put("/api/merchant/appointments/{id}/status", 1)
                    .param("status", "completed")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            assertSuccess(result, "预约状态更新成功");
            result.andExpect(jsonPath("$.data.status").value("completed"));

            verify(appointmentService).findById(1);
            verify(appointmentService).update(any(Appointment.class));
        }

        @Test
        @DisplayName("成功更新预约状态 - confirmed到cancelled")
        void testUpdateAppointmentStatus_Success_ConfirmedToCancelled() throws Exception {
            Appointment appointment = TestDataFactory.createAppointment(1, testMerchant, "confirmed");
            Appointment updatedAppointment = TestDataFactory.createAppointment(1, testMerchant, "cancelled");

            when(appointmentService.findById(1)).thenReturn(appointment);
            when(appointmentService.update(any(Appointment.class))).thenReturn(updatedAppointment);

            var result = mockMvc.perform(put("/api/merchant/appointments/{id}/status", 1)
                    .param("status", "cancelled")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            assertSuccess(result, "预约状态更新成功");
            result.andExpect(jsonPath("$.data.status").value("cancelled"));

            verify(appointmentService).findById(1);
            verify(appointmentService).update(any(Appointment.class));
        }

        @Test
        @DisplayName("更新预约状态 - 未登录返回401")
        void testUpdateAppointmentStatus_Unauthorized() throws Exception {
            mockMvc.perform(put("/api/merchant/appointments/{id}/status", 1)
                    .param("status", "confirmed")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));

            verify(appointmentService, never()).findById(anyInt());
            verify(appointmentService, never()).update(any());
        }

        @Test
        @DisplayName("更新预约状态 - 预约不存在返回404")
        void testUpdateAppointmentStatus_NotFound() throws Exception {
            when(appointmentService.findById(999)).thenReturn(null);

            var result = mockMvc.perform(put("/api/merchant/appointments/{id}/status", 999)
                    .param("status", "confirmed")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("预约不存在"));

            verify(appointmentService).findById(999);
            verify(appointmentService, never()).update(any());
        }

        @Test
        @DisplayName("更新预约状态 - 无权限操作返回403")
        void testUpdateAppointmentStatus_Forbidden() throws Exception {
            Merchant otherMerchant = TestDataFactory.createMerchant(2);
            Appointment appointment = TestDataFactory.createAppointment(1, otherMerchant, "pending");

            when(appointmentService.findById(1)).thenReturn(appointment);

            var result = mockMvc.perform(put("/api/merchant/appointments/{id}/status", 1)
                    .param("status", "confirmed")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            result.andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(403))
                    .andExpect(jsonPath("$.message").value("无权操作此预约"));

            verify(appointmentService).findById(1);
            verify(appointmentService, never()).update(any());
        }

        @Test
        @DisplayName("更新预约状态 - 无效状态值返回400")
        void testUpdateAppointmentStatus_InvalidStatus() throws Exception {
            Appointment appointment = TestDataFactory.createAppointment(1, testMerchant, "pending");

            when(appointmentService.findById(1)).thenReturn(appointment);

            var result = mockMvc.perform(put("/api/merchant/appointments/{id}/status", 1)
                    .param("status", "invalid_status")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("无效的预约状态，只能是 pending、confirmed、completed 或 cancelled"));

            verify(appointmentService).findById(1);
            verify(appointmentService, never()).update(any());
        }

        @Test
        @DisplayName("更新预约状态 - completed不能再变更状态")
        void testUpdateAppointmentStatus_CompletedCannotChange() throws Exception {
            Appointment appointment = TestDataFactory.createAppointment(1, testMerchant, "completed");

            when(appointmentService.findById(1)).thenReturn(appointment);

            var result = mockMvc.perform(put("/api/merchant/appointments/{id}/status", 1)
                    .param("status", "pending")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("状态流转无效：不能从 completed 变更为 pending"));

            verify(appointmentService).findById(1);
            verify(appointmentService, never()).update(any());
        }

        @Test
        @DisplayName("更新预约状态 - cancelled不能再变更状态")
        void testUpdateAppointmentStatus_CancelledCannotChange() throws Exception {
            Appointment appointment = TestDataFactory.createAppointment(1, testMerchant, "cancelled");

            when(appointmentService.findById(1)).thenReturn(appointment);

            var result = mockMvc.perform(put("/api/merchant/appointments/{id}/status", 1)
                    .param("status", "pending")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("状态流转无效：不能从 cancelled 变更为 pending"));

            verify(appointmentService).findById(1);
            verify(appointmentService, never()).update(any());
        }

        @Test
        @DisplayName("更新预约状态 - pending不能直接到completed")
        void testUpdateAppointmentStatus_PendingCannotGoToCompleted() throws Exception {
            Appointment appointment = TestDataFactory.createAppointment(1, testMerchant, "pending");

            when(appointmentService.findById(1)).thenReturn(appointment);

            var result = mockMvc.perform(put("/api/merchant/appointments/{id}/status", 1)
                    .param("status", "completed")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("状态流转无效：不能从 pending 变更为 completed"));

            verify(appointmentService).findById(1);
            verify(appointmentService, never()).update(any());
        }

        @Test
        @DisplayName("更新预约状态 - confirmed不能回到pending")
        void testUpdateAppointmentStatus_ConfirmedCannotGoBackToPending() throws Exception {
            Appointment appointment = TestDataFactory.createAppointment(1, testMerchant, "confirmed");

            when(appointmentService.findById(1)).thenReturn(appointment);

            var result = mockMvc.perform(put("/api/merchant/appointments/{id}/status", 1)
                    .param("status", "pending")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("状态流转无效：不能从 confirmed 变更为 pending"));

            verify(appointmentService).findById(1);
            verify(appointmentService, never()).update(any());
        }

        @Test
        @DisplayName("更新预约状态 - 相同状态允许更新")
        void testUpdateAppointmentStatus_SameStatusAllowed() throws Exception {
            Appointment appointment = TestDataFactory.createAppointment(1, testMerchant, "pending");
            Appointment updatedAppointment = TestDataFactory.createAppointment(1, testMerchant, "pending");

            when(appointmentService.findById(1)).thenReturn(appointment);
            when(appointmentService.update(any(Appointment.class))).thenReturn(updatedAppointment);

            var result = mockMvc.perform(put("/api/merchant/appointments/{id}/status", 1)
                    .param("status", "pending")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            assertSuccess(result, "预约状态更新成功");
            result.andExpect(jsonPath("$.data.status").value("pending"));

            verify(appointmentService).findById(1);
            verify(appointmentService).update(any(Appointment.class));
        }

        @Test
        @DisplayName("更新预约状态 - 服务层异常返回500")
        void testUpdateAppointmentStatus_ServiceException() throws Exception {
            Appointment appointment = TestDataFactory.createAppointment(1, testMerchant, "pending");

            when(appointmentService.findById(1)).thenReturn(appointment);
            when(appointmentService.update(any(Appointment.class)))
                    .thenThrow(new RuntimeException("更新失败"));

            var result = mockMvc.perform(put("/api/merchant/appointments/{id}/status", 1)
                    .param("status", "confirmed")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("更新预约状态失败：更新失败"));

            verify(appointmentService).findById(1);
            verify(appointmentService).update(any(Appointment.class));
        }
    }

    @Nested
    @DisplayName("预约状态流转验证测试")
    class AppointmentStatusTransitionTests {

        @Test
        @DisplayName("状态流转验证 - pending -> confirmed -> completed")
        void testStatusTransition_PendingToConfirmedToCompleted() throws Exception {
            Appointment pendingAppointment = TestDataFactory.createAppointment(1, testMerchant, "pending");
            Appointment confirmedAppointment = TestDataFactory.createAppointment(1, testMerchant, "confirmed");
            Appointment completedAppointment = TestDataFactory.createAppointment(1, testMerchant, "completed");

            when(appointmentService.findById(1)).thenReturn(pendingAppointment);
            when(appointmentService.update(any(Appointment.class))).thenReturn(confirmedAppointment);

            var result1 = mockMvc.perform(put("/api/merchant/appointments/{id}/status", 1)
                    .param("status", "confirmed")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            assertSuccess(result1, "预约状态更新成功");
            result1.andExpect(jsonPath("$.data.status").value("confirmed"));

            reset(appointmentService);
            when(appointmentService.findById(1)).thenReturn(confirmedAppointment);
            when(appointmentService.update(any(Appointment.class))).thenReturn(completedAppointment);

            var result2 = mockMvc.perform(put("/api/merchant/appointments/{id}/status", 1)
                    .param("status", "completed")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            assertSuccess(result2, "预约状态更新成功");
            result2.andExpect(jsonPath("$.data.status").value("completed"));
        }

        @Test
        @DisplayName("状态流转验证 - pending -> cancelled")
        void testStatusTransition_PendingToCancelled() throws Exception {
            Appointment pendingAppointment = TestDataFactory.createAppointment(1, testMerchant, "pending");
            Appointment cancelledAppointment = TestDataFactory.createAppointment(1, testMerchant, "cancelled");

            when(appointmentService.findById(1)).thenReturn(pendingAppointment);
            when(appointmentService.update(any(Appointment.class))).thenReturn(cancelledAppointment);

            var result = mockMvc.perform(put("/api/merchant/appointments/{id}/status", 1)
                    .param("status", "cancelled")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            assertSuccess(result, "预约状态更新成功");
            result.andExpect(jsonPath("$.data.status").value("cancelled"));
        }

        @Test
        @DisplayName("状态流转验证 - confirmed -> cancelled")
        void testStatusTransition_ConfirmedToCancelled() throws Exception {
            Appointment confirmedAppointment = TestDataFactory.createAppointment(1, testMerchant, "confirmed");
            Appointment cancelledAppointment = TestDataFactory.createAppointment(1, testMerchant, "cancelled");

            when(appointmentService.findById(1)).thenReturn(confirmedAppointment);
            when(appointmentService.update(any(Appointment.class))).thenReturn(cancelledAppointment);

            var result = mockMvc.perform(put("/api/merchant/appointments/{id}/status", 1)
                    .param("status", "cancelled")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            assertSuccess(result, "预约状态更新成功");
            result.andExpect(jsonPath("$.data.status").value("cancelled"));
        }
    }
}
