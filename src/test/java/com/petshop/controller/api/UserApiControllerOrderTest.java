package com.petshop.controller.api;

import com.petshop.dto.*;
import com.petshop.entity.Merchant;
import com.petshop.entity.ProductOrder;
import com.petshop.entity.User;
import com.petshop.exception.BadRequestException;
import com.petshop.exception.ResourceNotFoundException;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("用户订单管理API测试")
public class UserApiControllerOrderTest extends UserApiControllerTestBase {

    private UserApiController controller;

    @BeforeEach
    public void setupController() {
        controller = new UserApiController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "productOrderService", productOrderService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("GET /api/user/orders - 获取订单列表")
    class GetOrdersTests {

        @Test
        @DisplayName("成功获取订单列表")
        void testGetOrders_Success() throws Exception {
            OrderDTO order1 = OrderDTO.builder()
                    .id(1)
                    .orderNo("ORD001")
                    .status("pending")
                    .totalPrice(new BigDecimal("199.99"))
                    .createTime(LocalDateTime.now())
                    .build();
            OrderDTO order2 = OrderDTO.builder()
                    .id(2)
                    .orderNo("ORD002")
                    .status("paid")
                    .totalPrice(new BigDecimal("299.99"))
                    .createTime(LocalDateTime.now())
                    .build();
            List<OrderDTO> orders = Arrays.asList(order1, order2);

            PageResponse<OrderDTO> pageResponse = PageResponse.<OrderDTO>builder()
                    .data(orders)
                    .total(2L)
                    .page(1)
                    .pageSize(10)
                    .totalPages(1)
                    .build();

            when(productOrderService.getUserOrders(eq(testUserId), any(), any(), any(), any(), anyInt(), anyInt()))
                    .thenReturn(pageResponse);

            var result = performGet("/api/user/orders");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.total").value(2))
                    .andExpect(jsonPath("$.page").value(1))
                    .andExpect(jsonPath("$.pageSize").value(10));

            verify(productOrderService).getUserOrders(eq(testUserId), any(), any(), any(), any(), eq(1), eq(10));
        }

        @Test
        @DisplayName("获取订单列表 - 带状态筛选")
        void testGetOrders_WithStatusFilter() throws Exception {
            OrderDTO order = OrderDTO.builder()
                    .id(1)
                    .orderNo("ORD001")
                    .status("pending")
                    .totalPrice(new BigDecimal("199.99"))
                    .createTime(LocalDateTime.now())
                    .build();

            PageResponse<OrderDTO> pageResponse = PageResponse.<OrderDTO>builder()
                    .data(Collections.singletonList(order))
                    .total(1L)
                    .page(1)
                    .pageSize(10)
                    .totalPages(1)
                    .build();

            when(productOrderService.getUserOrders(eq(testUserId), eq("pending"), any(), any(), any(), anyInt(), anyInt()))
                    .thenReturn(pageResponse);

            var result = mockMvc.perform(get("/api/user/orders")
                    .param("status", "pending")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].status").value("pending"));

            verify(productOrderService).getUserOrders(eq(testUserId), eq("pending"), any(), any(), any(), eq(1), eq(10));
        }

        @Test
        @DisplayName("获取订单列表 - 空列表")
        void testGetOrders_EmptyList() throws Exception {
            PageResponse<OrderDTO> pageResponse = PageResponse.<OrderDTO>builder()
                    .data(Collections.emptyList())
                    .total(0L)
                    .page(1)
                    .pageSize(10)
                    .totalPages(0)
                    .build();

            when(productOrderService.getUserOrders(eq(testUserId), any(), any(), any(), any(), anyInt(), anyInt()))
                    .thenReturn(pageResponse);

            var result = performGet("/api/user/orders");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0))
                    .andExpect(jsonPath("$.total").value(0));

            verify(productOrderService).getUserOrders(eq(testUserId), any(), any(), any(), any(), eq(1), eq(10));
        }

        @Test
        @DisplayName("获取订单列表 - 未登录返回401")
        void testGetOrders_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/user/orders")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(productOrderService, never()).getUserOrders(anyInt(), any(), any(), any(), any(), anyInt(), anyInt());
        }

        @Test
        @DisplayName("获取订单列表 - 分页参数")
        void testGetOrders_WithPagination() throws Exception {
            PageResponse<OrderDTO> pageResponse = PageResponse.<OrderDTO>builder()
                    .data(Collections.emptyList())
                    .total(25L)
                    .page(2)
                    .pageSize(20)
                    .totalPages(2)
                    .build();

            when(productOrderService.getUserOrders(eq(testUserId), any(), any(), any(), any(), eq(2), eq(20)))
                    .thenReturn(pageResponse);

            var result = mockMvc.perform(get("/api/user/orders")
                    .param("page", "2")
                    .param("pageSize", "20")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(r -> {});

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(2))
                    .andExpect(jsonPath("$.pageSize").value(20))
                    .andExpect(jsonPath("$.totalPages").value(2));

            verify(productOrderService).getUserOrders(eq(testUserId), any(), any(), any(), any(), eq(2), eq(20));
        }
    }

    @Nested
    @DisplayName("GET /api/user/orders/{id} - 获取订单详情")
    class GetOrderByIdTests {

        @Test
        @DisplayName("成功获取订单详情")
        void testGetOrderById_Success() throws Exception {
            OrderDTO order = OrderDTO.builder()
                    .id(1)
                    .orderNo("ORD001")
                    .status("pending")
                    .totalPrice(new BigDecimal("199.99"))
                    .createTime(LocalDateTime.now())
                    .remark("测试订单")
                    .build();

            when(productOrderService.getOrderDetail(1, testUserId)).thenReturn(order);

            var result = performGet("/api/user/orders/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.orderNo").value("ORD001"))
                    .andExpect(jsonPath("$.status").value("pending"))
                    .andExpect(jsonPath("$.totalPrice").value(199.99))
                    .andExpect(jsonPath("$.remark").value("测试订单"));

            verify(productOrderService).getOrderDetail(1, testUserId);
        }

        @Test
        @DisplayName("获取订单详情 - 未登录返回401")
        void testGetOrderById_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/user/orders/{id}", 1)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(productOrderService, never()).getOrderDetail(anyInt(), anyInt());
        }

        @Test
        @DisplayName("获取订单详情 - 订单不存在返回404")
        void testGetOrderById_NotFound() throws Exception {
            when(productOrderService.getOrderDetail(999, testUserId))
                    .thenThrow(new ResourceNotFoundException("Order not found"));

            var result = performGet("/api/user/orders/{id}", 999);

            result.andExpect(status().isNotFound());

            verify(productOrderService).getOrderDetail(999, testUserId);
        }

        @Test
        @DisplayName("获取订单详情 - 无权限访问返回400")
        void testGetOrderById_Forbidden() throws Exception {
            when(productOrderService.getOrderDetail(1, testUserId))
                    .thenThrow(new BadRequestException("You don't have permission to view this order"));

            var result = performGet("/api/user/orders/{id}", 1);

            result.andExpect(status().isBadRequest());

            verify(productOrderService).getOrderDetail(1, testUserId);
        }
    }

    @Nested
    @DisplayName("POST /api/user/orders - 创建订单")
    class CreateOrderTests {

        @Test
        @DisplayName("成功创建订单")
        void testCreateOrder_Success() throws Exception {
            CreateOrderRequest.OrderItemRequest item = new CreateOrderRequest.OrderItemRequest(1, 2);
            CreateOrderRequest request = new CreateOrderRequest(
                    Collections.singletonList(item),
                    1,
                    "alipay",
                    "测试订单备注"
            );

            CreateOrderResponse response = new CreateOrderResponse(1, "ORD202401010001");

            when(productOrderService.createOrder(eq(testUserId), any(CreateOrderRequest.class)))
                    .thenReturn(response);

            var result = performPost("/api/user/orders", request);

            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.orderId").value(1))
                    .andExpect(jsonPath("$.orderNo").value("ORD202401010001"));

            verify(productOrderService).createOrder(eq(testUserId), any(CreateOrderRequest.class));
        }

        @Test
        @DisplayName("创建订单 - 未登录返回401")
        void testCreateOrder_Unauthorized() throws Exception {
            CreateOrderRequest request = new CreateOrderRequest(
                    Collections.singletonList(new CreateOrderRequest.OrderItemRequest(1, 2)),
                    1,
                    "alipay",
                    null
            );

            mockMvc.perform(post("/api/user/orders")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(productOrderService, never()).createOrder(anyInt(), any());
        }

        @Test
        @DisplayName("创建订单 - 商品不存在返回400")
        void testCreateOrder_ProductNotFound() throws Exception {
            CreateOrderRequest request = new CreateOrderRequest(
                    Collections.singletonList(new CreateOrderRequest.OrderItemRequest(999, 2)),
                    1,
                    "alipay",
                    null
            );

            when(productOrderService.createOrder(eq(testUserId), any(CreateOrderRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Product not found"));

            var result = performPost("/api/user/orders", request);

            result.andExpect(status().isNotFound());

            verify(productOrderService).createOrder(eq(testUserId), any(CreateOrderRequest.class));
        }

        @Test
        @DisplayName("创建订单 - 库存不足返回400")
        void testCreateOrder_InsufficientStock() throws Exception {
            CreateOrderRequest request = new CreateOrderRequest(
                    Collections.singletonList(new CreateOrderRequest.OrderItemRequest(1, 1000)),
                    1,
                    "alipay",
                    null
            );

            when(productOrderService.createOrder(eq(testUserId), any(CreateOrderRequest.class)))
                    .thenThrow(new BadRequestException("Insufficient stock for product"));

            var result = performPost("/api/user/orders", request);

            result.andExpect(status().isBadRequest());

            verify(productOrderService).createOrder(eq(testUserId), any(CreateOrderRequest.class));
        }

        @Test
        @DisplayName("创建订单 - 地址不存在返回404")
        void testCreateOrder_AddressNotFound() throws Exception {
            CreateOrderRequest request = new CreateOrderRequest(
                    Collections.singletonList(new CreateOrderRequest.OrderItemRequest(1, 2)),
                    999,
                    "alipay",
                    null
            );

            when(productOrderService.createOrder(eq(testUserId), any(CreateOrderRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Address not found"));

            var result = performPost("/api/user/orders", request);

            result.andExpect(status().isNotFound());

            verify(productOrderService).createOrder(eq(testUserId), any(CreateOrderRequest.class));
        }

        @Test
        @DisplayName("创建订单 - 多商家商品返回400")
        void testCreateOrder_MultipleMerchants() throws Exception {
            CreateOrderRequest request = new CreateOrderRequest(
                    Arrays.asList(
                            new CreateOrderRequest.OrderItemRequest(1, 2),
                            new CreateOrderRequest.OrderItemRequest(2, 1)
                    ),
                    1,
                    "alipay",
                    null
            );

            when(productOrderService.createOrder(eq(testUserId), any(CreateOrderRequest.class)))
                    .thenThrow(new BadRequestException("All products must be from the same merchant"));

            var result = performPost("/api/user/orders", request);

            result.andExpect(status().isBadRequest());

            verify(productOrderService).createOrder(eq(testUserId), any(CreateOrderRequest.class));
        }
    }

    @Nested
    @DisplayName("POST /api/user/orders/preview - 预览订单")
    class PreviewOrderTests {

        @Test
        @DisplayName("成功预览订单")
        void testPreviewOrder_Success() throws Exception {
            PreviewOrderRequest.OrderItemRequest item = new PreviewOrderRequest.OrderItemRequest(1, 2);
            PreviewOrderRequest request = new PreviewOrderRequest(Collections.singletonList(item));

            OrderPreviewItemDTO previewItem = OrderPreviewItemDTO.builder()
                    .productId(1)
                    .productName("测试商品")
                    .productImage("http://example.com/product.png")
                    .price(new BigDecimal("99.99"))
                    .quantity(2)
                    .subtotal(new BigDecimal("199.98"))
                    .build();

            OrderPreviewDTO preview = OrderPreviewDTO.builder()
                    .items(Collections.singletonList(previewItem))
                    .productTotal(new BigDecimal("199.98"))
                    .shippingFee(BigDecimal.ZERO)
                    .discount(BigDecimal.ZERO)
                    .totalAmount(new BigDecimal("199.98"))
                    .build();

            when(productOrderService.previewOrder(eq(testUserId), any(PreviewOrderRequest.class)))
                    .thenReturn(preview);

            var result = performPost("/api/user/orders/preview", request);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.items").isArray())
                    .andExpect(jsonPath("$.items.length()").value(1))
                    .andExpect(jsonPath("$.productTotal").value(199.98))
                    .andExpect(jsonPath("$.shippingFee").value(0))
                    .andExpect(jsonPath("$.totalAmount").value(199.98));

            verify(productOrderService).previewOrder(eq(testUserId), any(PreviewOrderRequest.class));
        }

        @Test
        @DisplayName("预览订单 - 未登录返回401")
        void testPreviewOrder_Unauthorized() throws Exception {
            PreviewOrderRequest request = new PreviewOrderRequest(
                    Collections.singletonList(new PreviewOrderRequest.OrderItemRequest(1, 2))
            );

            mockMvc.perform(post("/api/user/orders/preview")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(productOrderService, never()).previewOrder(anyInt(), any());
        }

        @Test
        @DisplayName("预览订单 - 商品不存在返回404")
        void testPreviewOrder_ProductNotFound() throws Exception {
            PreviewOrderRequest request = new PreviewOrderRequest(
                    Collections.singletonList(new PreviewOrderRequest.OrderItemRequest(999, 2))
            );

            when(productOrderService.previewOrder(eq(testUserId), any(PreviewOrderRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Product not found"));

            var result = performPost("/api/user/orders/preview", request);

            result.andExpect(status().isNotFound());

            verify(productOrderService).previewOrder(eq(testUserId), any(PreviewOrderRequest.class));
        }

        @Test
        @DisplayName("预览订单 - 商品已下架返回400")
        void testPreviewOrder_ProductNotAvailable() throws Exception {
            PreviewOrderRequest request = new PreviewOrderRequest(
                    Collections.singletonList(new PreviewOrderRequest.OrderItemRequest(1, 2))
            );

            when(productOrderService.previewOrder(eq(testUserId), any(PreviewOrderRequest.class)))
                    .thenThrow(new BadRequestException("Product is not available"));

            var result = performPost("/api/user/orders/preview", request);

            result.andExpect(status().isBadRequest());

            verify(productOrderService).previewOrder(eq(testUserId), any(PreviewOrderRequest.class));
        }
    }

    @Nested
    @DisplayName("POST /api/user/orders/{id}/pay - 支付订单")
    class PayOrderTests {

        @Test
        @DisplayName("成功支付订单")
        void testPayOrder_Success() throws Exception {
            PayOrderRequest request = new PayOrderRequest("alipay");

            PayResponse response = PayResponse.builder()
                    .payId(1)
                    .qrCodeUrl(null)
                    .redirectUrl("http://pay.example.com/redirect")
                    .expireTime(LocalDateTime.now().plusMinutes(30))
                    .build();

            when(productOrderService.payOrder(1, testUserId, "alipay")).thenReturn(response);

            var result = mockMvc.perform(post("/api/user/orders/{id}/pay", 1)
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(r -> {});

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.payId").value(1))
                    .andExpect(jsonPath("$.redirectUrl").value("http://pay.example.com/redirect"));

            verify(productOrderService).payOrder(1, testUserId, "alipay");
        }

        @Test
        @DisplayName("支付订单 - 未登录返回401")
        void testPayOrder_Unauthorized() throws Exception {
            PayOrderRequest request = new PayOrderRequest("alipay");

            mockMvc.perform(post("/api/user/orders/{id}/pay", 1)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(productOrderService, never()).payOrder(anyInt(), anyInt(), anyString());
        }

        @Test
        @DisplayName("支付订单 - 订单不存在返回404")
        void testPayOrder_OrderNotFound() throws Exception {
            PayOrderRequest request = new PayOrderRequest("alipay");

            when(productOrderService.payOrder(999, testUserId, "alipay"))
                    .thenThrow(new ResourceNotFoundException("Order not found"));

            var result = mockMvc.perform(post("/api/user/orders/{id}/pay", 999)
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(r -> {});

            result.andExpect(status().isNotFound());

            verify(productOrderService).payOrder(999, testUserId, "alipay");
        }

        @Test
        @DisplayName("支付订单 - 无权限返回400")
        void testPayOrder_Forbidden() throws Exception {
            PayOrderRequest request = new PayOrderRequest("alipay");

            when(productOrderService.payOrder(1, testUserId, "alipay"))
                    .thenThrow(new BadRequestException("You don't have permission to pay this order"));

            var result = mockMvc.perform(post("/api/user/orders/{id}/pay", 1)
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(r -> {});

            result.andExpect(status().isBadRequest());

            verify(productOrderService).payOrder(1, testUserId, "alipay");
        }

        @Test
        @DisplayName("支付订单 - 订单状态不允许支付返回400")
        void testPayOrder_InvalidStatus() throws Exception {
            PayOrderRequest request = new PayOrderRequest("alipay");

            when(productOrderService.payOrder(1, testUserId, "alipay"))
                    .thenThrow(new BadRequestException("Order cannot be paid in current status: paid"));

            var result = mockMvc.perform(post("/api/user/orders/{id}/pay", 1)
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(r -> {});

            result.andExpect(status().isBadRequest());

            verify(productOrderService).payOrder(1, testUserId, "alipay");
        }
    }

    @Nested
    @DisplayName("GET /api/user/orders/{id}/pay/status - 获取支付状态")
    class GetPayStatusTests {

        @Test
        @DisplayName("成功获取支付状态 - 待支付")
        void testGetPayStatus_Pending() throws Exception {
            PayStatusResponse response = PayStatusResponse.builder()
                    .orderId(1)
                    .payStatus("pending")
                    .payTime(null)
                    .transactionId(null)
                    .build();

            when(productOrderService.getPayStatus(1, testUserId)).thenReturn(response);

            var result = performGet("/api/user/orders/{id}/pay/status", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderId").value(1))
                    .andExpect(jsonPath("$.payStatus").value("pending"))
                    .andExpect(jsonPath("$.payTime").doesNotExist());

            verify(productOrderService).getPayStatus(1, testUserId);
        }

        @Test
        @DisplayName("成功获取支付状态 - 支付成功")
        void testGetPayStatus_Success() throws Exception {
            PayStatusResponse response = PayStatusResponse.builder()
                    .orderId(1)
                    .payStatus("success")
                    .payTime(LocalDateTime.now())
                    .transactionId("TXN123456789")
                    .build();

            when(productOrderService.getPayStatus(1, testUserId)).thenReturn(response);

            var result = performGet("/api/user/orders/{id}/pay/status", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderId").value(1))
                    .andExpect(jsonPath("$.payStatus").value("success"))
                    .andExpect(jsonPath("$.transactionId").value("TXN123456789"));

            verify(productOrderService).getPayStatus(1, testUserId);
        }

        @Test
        @DisplayName("获取支付状态 - 未登录返回401")
        void testGetPayStatus_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/user/orders/{id}/pay/status", 1)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(productOrderService, never()).getPayStatus(anyInt(), anyInt());
        }

        @Test
        @DisplayName("获取支付状态 - 订单不存在返回404")
        void testGetPayStatus_OrderNotFound() throws Exception {
            when(productOrderService.getPayStatus(999, testUserId))
                    .thenThrow(new ResourceNotFoundException("Order not found"));

            var result = performGet("/api/user/orders/{id}/pay/status", 999);

            result.andExpect(status().isNotFound());

            verify(productOrderService).getPayStatus(999, testUserId);
        }
    }

    @Nested
    @DisplayName("PUT /api/user/orders/{id}/cancel - 取消订单")
    class CancelOrderTests {

        @Test
        @DisplayName("成功取消订单 - pending状态")
        void testCancelOrder_Success_Pending() throws Exception {
            doNothing().when(productOrderService).cancelOrder(1, testUserId);

            var result = performPut("/api/user/orders/{id}/cancel", null, 1);

            result.andExpect(status().isOk());

            verify(productOrderService).cancelOrder(1, testUserId);
        }

        @Test
        @DisplayName("成功取消订单 - paid状态")
        void testCancelOrder_Success_Paid() throws Exception {
            doNothing().when(productOrderService).cancelOrder(1, testUserId);

            var result = performPut("/api/user/orders/{id}/cancel", null, 1);

            result.andExpect(status().isOk());

            verify(productOrderService).cancelOrder(1, testUserId);
        }

        @Test
        @DisplayName("取消订单 - 未登录返回401")
        void testCancelOrder_Unauthorized() throws Exception {
            mockMvc.perform(put("/api/user/orders/{id}/cancel", 1)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(productOrderService, never()).cancelOrder(anyInt(), anyInt());
        }

        @Test
        @DisplayName("取消订单 - 订单不存在返回404")
        void testCancelOrder_OrderNotFound() throws Exception {
            doThrow(new ResourceNotFoundException("Order not found"))
                    .when(productOrderService).cancelOrder(999, testUserId);

            var result = performPut("/api/user/orders/{id}/cancel", null, 999);

            result.andExpect(status().isNotFound());

            verify(productOrderService).cancelOrder(999, testUserId);
        }

        @Test
        @DisplayName("取消订单 - 无权限返回400")
        void testCancelOrder_Forbidden() throws Exception {
            doThrow(new BadRequestException("You don't have permission to cancel this order"))
                    .when(productOrderService).cancelOrder(1, testUserId);

            var result = performPut("/api/user/orders/{id}/cancel", null, 1);

            result.andExpect(status().isBadRequest());

            verify(productOrderService).cancelOrder(1, testUserId);
        }

        @Test
        @DisplayName("取消订单 - 状态不允许取消返回400")
        void testCancelOrder_InvalidStatus() throws Exception {
            doThrow(new BadRequestException("Order cannot be cancelled in current status: shipped"))
                    .when(productOrderService).cancelOrder(1, testUserId);

            var result = performPut("/api/user/orders/{id}/cancel", null, 1);

            result.andExpect(status().isBadRequest());

            verify(productOrderService).cancelOrder(1, testUserId);
        }
    }

    @Nested
    @DisplayName("POST /api/user/orders/{id}/refund - 申请退款")
    class RefundOrderTests {

        @Test
        @DisplayName("成功申请退款 - paid状态")
        void testRefundOrder_Success_Paid() throws Exception {
            RefundOrderRequest request = new RefundOrderRequest("商品质量问题");

            doNothing().when(productOrderService).refundOrder(1, testUserId, "商品质量问题");

            var result = mockMvc.perform(post("/api/user/orders/{id}/refund", 1)
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(r -> {});

            result.andExpect(status().isOk());

            verify(productOrderService).refundOrder(1, testUserId, "商品质量问题");
        }

        @Test
        @DisplayName("成功申请退款 - shipped状态")
        void testRefundOrder_Success_Shipped() throws Exception {
            RefundOrderRequest request = new RefundOrderRequest("不想要了");

            doNothing().when(productOrderService).refundOrder(1, testUserId, "不想要了");

            var result = mockMvc.perform(post("/api/user/orders/{id}/refund", 1)
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(r -> {});

            result.andExpect(status().isOk());

            verify(productOrderService).refundOrder(1, testUserId, "不想要了");
        }

        @Test
        @DisplayName("申请退款 - 未登录返回401")
        void testRefundOrder_Unauthorized() throws Exception {
            RefundOrderRequest request = new RefundOrderRequest("退款原因");

            mockMvc.perform(post("/api/user/orders/{id}/refund", 1)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(productOrderService, never()).refundOrder(anyInt(), anyInt(), anyString());
        }

        @Test
        @DisplayName("申请退款 - 订单不存在返回404")
        void testRefundOrder_OrderNotFound() throws Exception {
            RefundOrderRequest request = new RefundOrderRequest("退款原因");

            doThrow(new ResourceNotFoundException("Order not found"))
                    .when(productOrderService).refundOrder(999, testUserId, "退款原因");

            var result = mockMvc.perform(post("/api/user/orders/{id}/refund", 999)
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(r -> {});

            result.andExpect(status().isNotFound());

            verify(productOrderService).refundOrder(999, testUserId, "退款原因");
        }

        @Test
        @DisplayName("申请退款 - 无权限返回400")
        void testRefundOrder_Forbidden() throws Exception {
            RefundOrderRequest request = new RefundOrderRequest("退款原因");

            doThrow(new BadRequestException("You don't have permission to refund this order"))
                    .when(productOrderService).refundOrder(1, testUserId, "退款原因");

            var result = mockMvc.perform(post("/api/user/orders/{id}/refund", 1)
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(r -> {});

            result.andExpect(status().isBadRequest());

            verify(productOrderService).refundOrder(1, testUserId, "退款原因");
        }

        @Test
        @DisplayName("申请退款 - 状态不允许退款返回400")
        void testRefundOrder_InvalidStatus() throws Exception {
            RefundOrderRequest request = new RefundOrderRequest("退款原因");

            doThrow(new BadRequestException("Order cannot be refunded in current status: pending"))
                    .when(productOrderService).refundOrder(1, testUserId, "退款原因");

            var result = mockMvc.perform(post("/api/user/orders/{id}/refund", 1)
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(r -> {});

            result.andExpect(status().isBadRequest());

            verify(productOrderService).refundOrder(1, testUserId, "退款原因");
        }
    }

    @Nested
    @DisplayName("PUT /api/user/orders/{id}/confirm - 确认收货")
    class ConfirmReceiveTests {

        @Test
        @DisplayName("成功确认收货")
        void testConfirmReceive_Success() throws Exception {
            doNothing().when(productOrderService).confirmReceive(1, testUserId);

            var result = performPut("/api/user/orders/{id}/confirm", null, 1);

            result.andExpect(status().isOk());

            verify(productOrderService).confirmReceive(1, testUserId);
        }

        @Test
        @DisplayName("确认收货 - 未登录返回401")
        void testConfirmReceive_Unauthorized() throws Exception {
            mockMvc.perform(put("/api/user/orders/{id}/confirm", 1)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(productOrderService, never()).confirmReceive(anyInt(), anyInt());
        }

        @Test
        @DisplayName("确认收货 - 订单不存在返回404")
        void testConfirmReceive_OrderNotFound() throws Exception {
            doThrow(new ResourceNotFoundException("Order not found"))
                    .when(productOrderService).confirmReceive(999, testUserId);

            var result = performPut("/api/user/orders/{id}/confirm", null, 999);

            result.andExpect(status().isNotFound());

            verify(productOrderService).confirmReceive(999, testUserId);
        }

        @Test
        @DisplayName("确认收货 - 无权限返回400")
        void testConfirmReceive_Forbidden() throws Exception {
            doThrow(new BadRequestException("You don't have permission to confirm this order"))
                    .when(productOrderService).confirmReceive(1, testUserId);

            var result = performPut("/api/user/orders/{id}/confirm", null, 1);

            result.andExpect(status().isBadRequest());

            verify(productOrderService).confirmReceive(1, testUserId);
        }

        @Test
        @DisplayName("确认收货 - 状态不允许确认返回400")
        void testConfirmReceive_InvalidStatus() throws Exception {
            doThrow(new BadRequestException("Order cannot be confirmed in current status: pending"))
                    .when(productOrderService).confirmReceive(1, testUserId);

            var result = performPut("/api/user/orders/{id}/confirm", null, 1);

            result.andExpect(status().isBadRequest());

            verify(productOrderService).confirmReceive(1, testUserId);
        }
    }

    @Nested
    @DisplayName("DELETE /api/user/orders/{id} - 删除订单")
    class DeleteOrderTests {

        @Test
        @DisplayName("成功删除订单 - completed状态")
        void testDeleteOrder_Success_Completed() throws Exception {
            doNothing().when(productOrderService).deleteOrder(1, testUserId);

            var result = performDelete("/api/user/orders/{id}", 1);

            result.andExpect(status().isNoContent());

            verify(productOrderService).deleteOrder(1, testUserId);
        }

        @Test
        @DisplayName("成功删除订单 - cancelled状态")
        void testDeleteOrder_Success_Cancelled() throws Exception {
            doNothing().when(productOrderService).deleteOrder(1, testUserId);

            var result = performDelete("/api/user/orders/{id}", 1);

            result.andExpect(status().isNoContent());

            verify(productOrderService).deleteOrder(1, testUserId);
        }

        @Test
        @DisplayName("删除订单 - 未登录返回401")
        void testDeleteOrder_Unauthorized() throws Exception {
            mockMvc.perform(delete("/api/user/orders/{id}", 1)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(productOrderService, never()).deleteOrder(anyInt(), anyInt());
        }

        @Test
        @DisplayName("删除订单 - 订单不存在返回404")
        void testDeleteOrder_OrderNotFound() throws Exception {
            doThrow(new ResourceNotFoundException("Order not found"))
                    .when(productOrderService).deleteOrder(999, testUserId);

            var result = performDelete("/api/user/orders/{id}", 999);

            result.andExpect(status().isNotFound());

            verify(productOrderService).deleteOrder(999, testUserId);
        }

        @Test
        @DisplayName("删除订单 - 无权限返回400")
        void testDeleteOrder_Forbidden() throws Exception {
            doThrow(new BadRequestException("You don't have permission to delete this order"))
                    .when(productOrderService).deleteOrder(1, testUserId);

            var result = performDelete("/api/user/orders/{id}", 1);

            result.andExpect(status().isBadRequest());

            verify(productOrderService).deleteOrder(1, testUserId);
        }

        @Test
        @DisplayName("删除订单 - 状态不允许删除返回400")
        void testDeleteOrder_InvalidStatus() throws Exception {
            doThrow(new BadRequestException("Only completed or cancelled orders can be deleted"))
                    .when(productOrderService).deleteOrder(1, testUserId);

            var result = performDelete("/api/user/orders/{id}", 1);

            result.andExpect(status().isBadRequest());

            verify(productOrderService).deleteOrder(1, testUserId);
        }
    }

    @Nested
    @DisplayName("PUT /api/user/orders/batch-cancel - 批量取消订单")
    class BatchCancelOrdersTests {

        @Test
        @DisplayName("成功批量取消订单")
        void testBatchCancelOrders_Success() throws Exception {
            BatchOperationRequest request = new BatchOperationRequest(Arrays.asList(1, 2, 3));

            doNothing().when(productOrderService).batchCancel(anyList(), eq(testUserId));

            var result = performPut("/api/user/orders/batch-cancel", request);

            result.andExpect(status().isOk());

            verify(productOrderService).batchCancel(anyList(), eq(testUserId));
        }

        @Test
        @DisplayName("批量取消订单 - 未登录返回401")
        void testBatchCancelOrders_Unauthorized() throws Exception {
            BatchOperationRequest request = new BatchOperationRequest(Arrays.asList(1, 2, 3));

            mockMvc.perform(put("/api/user/orders/batch-cancel")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(productOrderService, never()).batchCancel(anyList(), anyInt());
        }

        @Test
        @DisplayName("批量取消订单 - 空列表")
        void testBatchCancelOrders_EmptyList() throws Exception {
            BatchOperationRequest request = new BatchOperationRequest(Collections.emptyList());

            doNothing().when(productOrderService).batchCancel(anyList(), eq(testUserId));

            var result = performPut("/api/user/orders/batch-cancel", request);

            result.andExpect(status().isOk());

            verify(productOrderService).batchCancel(anyList(), eq(testUserId));
        }

        @Test
        @DisplayName("批量取消订单 - 部分成功")
        void testBatchCancelOrders_PartialSuccess() throws Exception {
            BatchOperationRequest request = new BatchOperationRequest(Arrays.asList(1, 2, 999));

            doNothing().when(productOrderService).batchCancel(anyList(), eq(testUserId));

            var result = performPut("/api/user/orders/batch-cancel", request);

            result.andExpect(status().isOk());

            verify(productOrderService).batchCancel(anyList(), eq(testUserId));
        }
    }

    @Nested
    @DisplayName("DELETE /api/user/orders/batch-delete - 批量删除订单")
    class BatchDeleteOrdersTests {

        @Test
        @DisplayName("成功批量删除订单")
        void testBatchDeleteOrders_Success() throws Exception {
            BatchOperationRequest request = new BatchOperationRequest(Arrays.asList(1, 2, 3));

            doNothing().when(productOrderService).batchDelete(anyList(), eq(testUserId));

            var result = mockMvc.perform(delete("/api/user/orders/batch-delete")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(r -> {});

            result.andExpect(status().isNoContent());

            verify(productOrderService).batchDelete(anyList(), eq(testUserId));
        }

        @Test
        @DisplayName("批量删除订单 - 未登录返回401")
        void testBatchDeleteOrders_Unauthorized() throws Exception {
            BatchOperationRequest request = new BatchOperationRequest(Arrays.asList(1, 2, 3));

            mockMvc.perform(delete("/api/user/orders/batch-delete")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(productOrderService, never()).batchDelete(anyList(), anyInt());
        }

        @Test
        @DisplayName("批量删除订单 - 空列表")
        void testBatchDeleteOrders_EmptyList() throws Exception {
            BatchOperationRequest request = new BatchOperationRequest(Collections.emptyList());

            doNothing().when(productOrderService).batchDelete(anyList(), eq(testUserId));

            var result = mockMvc.perform(delete("/api/user/orders/batch-delete")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(r -> {});

            result.andExpect(status().isNoContent());

            verify(productOrderService).batchDelete(anyList(), eq(testUserId));
        }

        @Test
        @DisplayName("批量删除订单 - 部分成功")
        void testBatchDeleteOrders_PartialSuccess() throws Exception {
            BatchOperationRequest request = new BatchOperationRequest(Arrays.asList(1, 2, 999));

            doNothing().when(productOrderService).batchDelete(anyList(), eq(testUserId));

            var result = mockMvc.perform(delete("/api/user/orders/batch-delete")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(r -> {});

            result.andExpect(status().isNoContent());

            verify(productOrderService).batchDelete(anyList(), eq(testUserId));
        }
    }

    @Nested
    @DisplayName("订单状态流转验证测试")
    class OrderStatusTransitionTests {

        @Test
        @DisplayName("状态流转验证 - pending -> paid -> shipped -> completed")
        void testStatusTransition_FullFlow() throws Exception {
            PayOrderRequest payRequest = new PayOrderRequest("alipay");
            PayResponse payResponse = PayResponse.builder()
                    .payId(1)
                    .expireTime(LocalDateTime.now().plusMinutes(30))
                    .build();

            when(productOrderService.payOrder(1, testUserId, "alipay")).thenReturn(payResponse);

            var payResult = mockMvc.perform(post("/api/user/orders/{id}/pay", 1)
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(payRequest)))
                    .andDo(r -> {});

            payResult.andExpect(status().isOk())
                    .andExpect(jsonPath("$.payId").value(1));

            verify(productOrderService).payOrder(1, testUserId, "alipay");
        }

        @Test
        @DisplayName("状态流转验证 - pending -> cancelled")
        void testStatusTransition_PendingToCancelled() throws Exception {
            doNothing().when(productOrderService).cancelOrder(1, testUserId);

            var result = performPut("/api/user/orders/{id}/cancel", null, 1);

            result.andExpect(status().isOk());

            verify(productOrderService).cancelOrder(1, testUserId);
        }

        @Test
        @DisplayName("状态流转验证 - paid -> cancelled (退款)")
        void testStatusTransition_PaidToCancelled() throws Exception {
            RefundOrderRequest request = new RefundOrderRequest("不想要了");

            doNothing().when(productOrderService).refundOrder(1, testUserId, "不想要了");

            var result = mockMvc.perform(post("/api/user/orders/{id}/refund", 1)
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(r -> {});

            result.andExpect(status().isOk());

            verify(productOrderService).refundOrder(1, testUserId, "不想要了");
        }

        @Test
        @DisplayName("状态流转验证 - shipped -> completed")
        void testStatusTransition_ShippedToCompleted() throws Exception {
            doNothing().when(productOrderService).confirmReceive(1, testUserId);

            var result = performPut("/api/user/orders/{id}/confirm", null, 1);

            result.andExpect(status().isOk());

            verify(productOrderService).confirmReceive(1, testUserId);
        }

        @Test
        @DisplayName("状态流转验证 - shipped -> cancelled (退款)")
        void testStatusTransition_ShippedToCancelled() throws Exception {
            RefundOrderRequest request = new RefundOrderRequest("商品损坏");

            doNothing().when(productOrderService).refundOrder(1, testUserId, "商品损坏");

            var result = mockMvc.perform(post("/api/user/orders/{id}/refund", 1)
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(r -> {});

            result.andExpect(status().isOk());

            verify(productOrderService).refundOrder(1, testUserId, "商品损坏");
        }
    }

    @Nested
    @DisplayName("所有权验证测试")
    class OwnershipValidationTests {

        @Test
        @DisplayName("所有权验证 - 用户只能查看自己的订单")
        void testOwnership_GetOrderDetail() throws Exception {
            User otherUser = TestDataFactory.createUser(2);
            
            when(productOrderService.getOrderDetail(1, testUserId))
                    .thenThrow(new BadRequestException("You don't have permission to view this order"));

            var result = performGet("/api/user/orders/{id}", 1);

            result.andExpect(status().isBadRequest());

            verify(productOrderService).getOrderDetail(1, testUserId);
        }

        @Test
        @DisplayName("所有权验证 - 用户只能支付自己的订单")
        void testOwnership_PayOrder() throws Exception {
            PayOrderRequest request = new PayOrderRequest("alipay");

            when(productOrderService.payOrder(1, testUserId, "alipay"))
                    .thenThrow(new BadRequestException("You don't have permission to pay this order"));

            var result = mockMvc.perform(post("/api/user/orders/{id}/pay", 1)
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(r -> {});

            result.andExpect(status().isBadRequest());

            verify(productOrderService).payOrder(1, testUserId, "alipay");
        }

        @Test
        @DisplayName("所有权验证 - 用户只能取消自己的订单")
        void testOwnership_CancelOrder() throws Exception {
            doThrow(new BadRequestException("You don't have permission to cancel this order"))
                    .when(productOrderService).cancelOrder(1, testUserId);

            var result = performPut("/api/user/orders/{id}/cancel", null, 1);

            result.andExpect(status().isBadRequest());

            verify(productOrderService).cancelOrder(1, testUserId);
        }

        @Test
        @DisplayName("所有权验证 - 用户只能删除自己的订单")
        void testOwnership_DeleteOrder() throws Exception {
            doThrow(new BadRequestException("You don't have permission to delete this order"))
                    .when(productOrderService).deleteOrder(1, testUserId);

            var result = performDelete("/api/user/orders/{id}", 1);

            result.andExpect(status().isBadRequest());

            verify(productOrderService).deleteOrder(1, testUserId);
        }

        @Test
        @DisplayName("所有权验证 - 用户只能确认自己的订单")
        void testOwnership_ConfirmReceive() throws Exception {
            doThrow(new BadRequestException("You don't have permission to confirm this order"))
                    .when(productOrderService).confirmReceive(1, testUserId);

            var result = performPut("/api/user/orders/{id}/confirm", null, 1);

            result.andExpect(status().isBadRequest());

            verify(productOrderService).confirmReceive(1, testUserId);
        }

        @Test
        @DisplayName("所有权验证 - 用户只能申请自己订单的退款")
        void testOwnership_RefundOrder() throws Exception {
            RefundOrderRequest request = new RefundOrderRequest("退款原因");

            doThrow(new BadRequestException("You don't have permission to refund this order"))
                    .when(productOrderService).refundOrder(1, testUserId, "退款原因");

            var result = mockMvc.perform(post("/api/user/orders/{id}/refund", 1)
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(r -> {});

            result.andExpect(status().isBadRequest());

            verify(productOrderService).refundOrder(1, testUserId, "退款原因");
        }
    }

    @Nested
    @DisplayName("批量操作测试")
    class BatchOperationTests {

        @Test
        @DisplayName("批量取消 - 混合状态订单")
        void testBatchCancel_MixedStatus() throws Exception {
            BatchOperationRequest request = new BatchOperationRequest(Arrays.asList(1, 2, 3, 4, 5));

            doNothing().when(productOrderService).batchCancel(anyList(), eq(testUserId));

            var result = performPut("/api/user/orders/batch-cancel", request);

            result.andExpect(status().isOk());

            verify(productOrderService).batchCancel(anyList(), eq(testUserId));
        }

        @Test
        @DisplayName("批量删除 - 混合状态订单")
        void testBatchDelete_MixedStatus() throws Exception {
            BatchOperationRequest request = new BatchOperationRequest(Arrays.asList(1, 2, 3, 4, 5));

            doNothing().when(productOrderService).batchDelete(anyList(), eq(testUserId));

            var result = mockMvc.perform(delete("/api/user/orders/batch-delete")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(r -> {});

            result.andExpect(status().isNoContent());

            verify(productOrderService).batchDelete(anyList(), eq(testUserId));
        }

        @Test
        @DisplayName("批量操作 - 包含不存在订单")
        void testBatchOperation_WithNonExistentOrders() throws Exception {
            BatchOperationRequest request = new BatchOperationRequest(Arrays.asList(1, 999, 1000));

            doNothing().when(productOrderService).batchCancel(anyList(), eq(testUserId));

            var result = performPut("/api/user/orders/batch-cancel", request);

            result.andExpect(status().isOk());

            verify(productOrderService).batchCancel(anyList(), eq(testUserId));
        }

        @Test
        @DisplayName("批量操作 - 包含其他用户订单")
        void testBatchOperation_WithOtherUserOrders() throws Exception {
            BatchOperationRequest request = new BatchOperationRequest(Arrays.asList(1, 2, 3));

            doNothing().when(productOrderService).batchCancel(anyList(), eq(testUserId));

            var result = performPut("/api/user/orders/batch-cancel", request);

            result.andExpect(status().isOk());

            verify(productOrderService).batchCancel(anyList(), eq(testUserId));
        }
    }
}
