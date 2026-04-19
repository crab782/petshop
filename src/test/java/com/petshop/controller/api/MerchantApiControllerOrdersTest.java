package com.petshop.controller.api;

import com.petshop.entity.Merchant;
import com.petshop.entity.ProductOrder;
import com.petshop.entity.User;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("商家商品订单API测试")
public class MerchantApiControllerOrdersTest extends MerchantApiControllerTestBase {

    @BeforeEach
    void setUpController() {
        MerchantApiController controller = new MerchantApiController();
        injectDependencies(controller);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private void injectDependencies(MerchantApiController controller) {
        try {
            setField(controller, "productOrderService", productOrderService);
        } catch (Exception e) {
            throw new RuntimeException("注入依赖失败", e);
        }
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Nested
    @DisplayName("GET /api/merchant/orders - 获取商品订单列表")
    class GetOrdersTests {

        @Test
        @DisplayName("成功获取订单列表 - 返回多个订单")
        void testGetOrders_Success_WithOrders() throws Exception {
            ProductOrder order1 = TestDataFactory.createProductOrder(1, testMerchant, "pending");
            ProductOrder order2 = TestDataFactory.createProductOrder(2, testMerchant, "paid");
            ProductOrder order3 = TestDataFactory.createProductOrder(3, testMerchant, "shipped");
            List<ProductOrder> orders = Arrays.asList(order1, order2, order3);

            when(productOrderService.findByMerchantId(testMerchantId)).thenReturn(orders);

            var result = performGet("/api/merchant/orders");

            assertSuccess(result);
            assertListResponse(result, 3);
            verify(productOrderService).findByMerchantId(testMerchantId);
        }

        @Test
        @DisplayName("成功获取订单列表 - 返回空列表")
        void testGetOrders_Success_EmptyList() throws Exception {
            when(productOrderService.findByMerchantId(testMerchantId)).thenReturn(Collections.emptyList());

            var result = performGet("/api/merchant/orders");

            assertSuccess(result);
            assertListResponse(result, 0);
            verify(productOrderService).findByMerchantId(testMerchantId);
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetOrders_Unauthorized() throws Exception {
            var result = mockMvc.perform(get("/api/merchant/orders")
                    .contentType("application/json"))
                    .andDo(print());

            result.andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));
            verify(productOrderService, never()).findByMerchantId(anyInt());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetOrders_ServiceException() throws Exception {
            when(productOrderService.findByMerchantId(testMerchantId))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            var result = performGet("/api/merchant/orders");

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("获取订单列表失败：数据库连接失败"));
        }
    }

    @Nested
    @DisplayName("PUT /api/merchant/orders/{id}/status - 更新商品订单状态")
    class UpdateOrderStatusTests {

        @Test
        @DisplayName("成功更新订单状态 - pending -> paid")
        void testUpdateOrderStatus_Success_PendingToPaid() throws Exception {
            ProductOrder order = TestDataFactory.createProductOrder(1, testMerchant, "pending");
            ProductOrder updatedOrder = TestDataFactory.createProductOrder(1, testMerchant, "paid");

            when(productOrderService.findById(1)).thenReturn(order);
            when(productOrderService.update(any(ProductOrder.class))).thenReturn(updatedOrder);

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "paid")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType("application/json"))
                    .andDo(print());

            assertSuccess(result, "订单状态更新成功");
            result.andExpect(jsonPath("$.data.status").value("paid"));
            verify(productOrderService).findById(1);
            verify(productOrderService).update(any(ProductOrder.class));
        }

        @Test
        @DisplayName("成功更新订单状态 - paid -> shipped")
        void testUpdateOrderStatus_Success_PaidToShipped() throws Exception {
            ProductOrder order = TestDataFactory.createProductOrder(1, testMerchant, "paid");
            ProductOrder updatedOrder = TestDataFactory.createProductOrder(1, testMerchant, "shipped");

            when(productOrderService.findById(1)).thenReturn(order);
            when(productOrderService.update(any(ProductOrder.class))).thenReturn(updatedOrder);

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "shipped")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType("application/json"))
                    .andDo(print());

            assertSuccess(result, "订单状态更新成功");
            result.andExpect(jsonPath("$.data.status").value("shipped"));
        }

        @Test
        @DisplayName("成功更新订单状态 - shipped -> completed")
        void testUpdateOrderStatus_Success_ShippedToCompleted() throws Exception {
            ProductOrder order = TestDataFactory.createProductOrder(1, testMerchant, "shipped");
            ProductOrder updatedOrder = TestDataFactory.createProductOrder(1, testMerchant, "completed");

            when(productOrderService.findById(1)).thenReturn(order);
            when(productOrderService.update(any(ProductOrder.class))).thenReturn(updatedOrder);

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "completed")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType("application/json"))
                    .andDo(print());

            assertSuccess(result, "订单状态更新成功");
            result.andExpect(jsonPath("$.data.status").value("completed"));
        }

        @Test
        @DisplayName("成功更新订单状态 - pending -> cancelled")
        void testUpdateOrderStatus_Success_PendingToCancelled() throws Exception {
            ProductOrder order = TestDataFactory.createProductOrder(1, testMerchant, "pending");
            ProductOrder updatedOrder = TestDataFactory.createProductOrder(1, testMerchant, "cancelled");

            when(productOrderService.findById(1)).thenReturn(order);
            when(productOrderService.update(any(ProductOrder.class))).thenReturn(updatedOrder);

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "cancelled")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType("application/json"))
                    .andDo(print());

            assertSuccess(result, "订单状态更新成功");
            result.andExpect(jsonPath("$.data.status").value("cancelled"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testUpdateOrderStatus_Unauthorized() throws Exception {
            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "paid")
                    .contentType("application/json"))
                    .andDo(print());

            result.andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));
            verify(productOrderService, never()).findById(anyInt());
            verify(productOrderService, never()).update(any());
        }

        @Test
        @DisplayName("订单不存在返回404")
        void testUpdateOrderStatus_OrderNotFound() throws Exception {
            when(productOrderService.findById(999)).thenReturn(null);

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 999)
                    .param("status", "paid")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType("application/json"))
                    .andDo(print());

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("订单不存在"));
            verify(productOrderService).findById(999);
            verify(productOrderService, never()).update(any());
        }

        @Test
        @DisplayName("无权限操作返回403")
        void testUpdateOrderStatus_Forbidden() throws Exception {
            Merchant otherMerchant = TestDataFactory.createMerchant(2, "其他商家", "other@test.com");
            ProductOrder order = TestDataFactory.createProductOrder(1, otherMerchant, "pending");

            when(productOrderService.findById(1)).thenReturn(order);

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "paid")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType("application/json"))
                    .andDo(print());

            result.andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(403))
                    .andExpect(jsonPath("$.message").value("无权操作此订单"));
            verify(productOrderService).findById(1);
            verify(productOrderService, never()).update(any());
        }

        @Test
        @DisplayName("无效状态值返回400")
        void testUpdateOrderStatus_InvalidStatus() throws Exception {
            ProductOrder order = TestDataFactory.createProductOrder(1, testMerchant, "pending");

            when(productOrderService.findById(1)).thenReturn(order);

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "invalid_status")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType("application/json"))
                    .andDo(print());

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("无效的订单状态"));
            verify(productOrderService).findById(1);
            verify(productOrderService, never()).update(any());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testUpdateOrderStatus_ServiceException() throws Exception {
            ProductOrder order = TestDataFactory.createProductOrder(1, testMerchant, "pending");

            when(productOrderService.findById(1)).thenReturn(order);
            when(productOrderService.update(any(ProductOrder.class)))
                    .thenThrow(new RuntimeException("数据库更新失败"));

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "paid")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType("application/json"))
                    .andDo(print());

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("更新订单状态失败：数据库更新失败"));
        }
    }

    @Nested
    @DisplayName("状态流转验证测试")
    class StatusTransitionTests {

        @Test
        @DisplayName("完整状态流转 - pending -> paid -> shipped -> completed")
        void testStatusTransition_FullFlow() throws Exception {
            ProductOrder order = TestDataFactory.createProductOrder(1, testMerchant, "pending");
            when(productOrderService.findById(1)).thenReturn(order);
            when(productOrderService.update(any(ProductOrder.class))).thenAnswer(invocation -> {
                ProductOrder updatedOrder = invocation.getArgument(0);
                return updatedOrder;
            });

            var result1 = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "paid")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType("application/json"))
                    .andDo(print());
            assertSuccess(result1, "订单状态更新成功");
            result1.andExpect(jsonPath("$.data.status").value("paid"));

            order.setStatus("paid");
            var result2 = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "shipped")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType("application/json"))
                    .andDo(print());
            assertSuccess(result2, "订单状态更新成功");
            result2.andExpect(jsonPath("$.data.status").value("shipped"));

            order.setStatus("shipped");
            var result3 = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "completed")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType("application/json"))
                    .andDo(print());
            assertSuccess(result3, "订单状态更新成功");
            result3.andExpect(jsonPath("$.data.status").value("completed"));
        }

        @Test
        @DisplayName("取消订单流程 - pending -> cancelled")
        void testStatusTransition_CancelFromPending() throws Exception {
            ProductOrder order = TestDataFactory.createProductOrder(1, testMerchant, "pending");
            when(productOrderService.findById(1)).thenReturn(order);
            when(productOrderService.update(any(ProductOrder.class))).thenAnswer(invocation -> {
                ProductOrder updatedOrder = invocation.getArgument(0);
                return updatedOrder;
            });

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "cancelled")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType("application/json"))
                    .andDo(print());

            assertSuccess(result, "订单状态更新成功");
            result.andExpect(jsonPath("$.data.status").value("cancelled"));
        }

        @Test
        @DisplayName("更新相同状态 - 状态不变")
        void testStatusTransition_SameStatus() throws Exception {
            ProductOrder order = TestDataFactory.createProductOrder(1, testMerchant, "pending");
            when(productOrderService.findById(1)).thenReturn(order);
            when(productOrderService.update(any(ProductOrder.class))).thenAnswer(invocation -> {
                ProductOrder updatedOrder = invocation.getArgument(0);
                return updatedOrder;
            });

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "pending")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType("application/json"))
                    .andDo(print());

            assertSuccess(result, "订单状态更新成功");
            result.andExpect(jsonPath("$.data.status").value("pending"));
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("空状态参数")
        void testUpdateOrderStatus_EmptyStatus() throws Exception {
            ProductOrder order = TestDataFactory.createProductOrder(1, testMerchant, "pending");
            when(productOrderService.findById(1)).thenReturn(order);

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 1)
                    .param("status", "")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType("application/json"))
                    .andDo(print());

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("无效的订单状态"));
        }

        @Test
        @DisplayName("订单ID不存在")
        void testUpdateOrderStatus_NonExistentId() throws Exception {
            when(productOrderService.findById(99999)).thenReturn(null);

            var result = mockMvc.perform(put("/api/merchant/orders/{id}/status", 99999)
                    .param("status", "paid")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType("application/json"))
                    .andDo(print());

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("多个订单获取验证")
        void testGetOrders_MultipleOrders() throws Exception {
            User user1 = TestDataFactory.createUser(1);
            User user2 = TestDataFactory.createUser(2);

            ProductOrder order1 = TestDataFactory.createProductOrder(1, testMerchant, user1,
                    new BigDecimal("99.99"), "pending", "地址1");
            ProductOrder order2 = TestDataFactory.createProductOrder(2, testMerchant, user2,
                    new BigDecimal("199.99"), "paid", "地址2");

            when(productOrderService.findByMerchantId(testMerchantId))
                    .thenReturn(Arrays.asList(order1, order2));

            var result = performGet("/api/merchant/orders");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].id").value(1))
                    .andExpect(jsonPath("$.data[1].id").value(2));
        }
    }
}
