package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.dto.*;
import com.petshop.entity.*;
import com.petshop.exception.BadRequestException;
import com.petshop.exception.ResourceNotFoundException;
import com.petshop.mapper.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductOrderServiceTest {

    @Mock
    private ProductOrderMapper productOrderMapper;

    @Mock
    private ProductOrderItemMapper productOrderItemMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private UserMapper userRepository;

    @InjectMocks
    private ProductOrderService productOrderService;

    private ProductOrder testOrder;
    private User testUser;
    private Merchant testMerchant;
    private Product testProduct;
    private Address testAddress;
    private ProductOrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testMerchant = new Merchant();
        testMerchant.setId(1);
        testMerchant.setName("Test Merchant");

        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("Test Product");
        testProduct.setPrice(new BigDecimal("100.00"));
        testProduct.setStock(10);
        testProduct.setStatus("enabled");
        testProduct.setMerchantId(1);
        testProduct.setMerchant(testMerchant);

        testAddress = new Address();
        testAddress.setId(1);
        testAddress.setUserId(1);
        testAddress.setContactName("John Doe");
        testAddress.setPhone("13800138000");
        testAddress.setProvince("Beijing");
        testAddress.setCity("Beijing");
        testAddress.setDistrict("Chaoyang");
        testAddress.setDetailAddress("Test Street 123");

        testOrder = new ProductOrder();
        testOrder.setId(1);
        testOrder.setOrderNo("ORD202401010001");
        testOrder.setUserId(1);
        testOrder.setMerchantId(1);
        testOrder.setTotalPrice(new BigDecimal("100.00"));
        testOrder.setFreight(BigDecimal.ZERO);
        testOrder.setStatus("pending");
        testOrder.setPayMethod("wechat");
        testOrder.setShippingAddress("Beijing Beijing Chaoyang Test Street 123 (John Doe 13800138000)");
        testOrder.setCreatedAt(LocalDateTime.now());

        testOrderItem = new ProductOrderItem();
        testOrderItem.setId(1);
        testOrderItem.setOrderId(1);
        testOrderItem.setProductId(1);
        testOrderItem.setQuantity(1);
        testOrderItem.setPrice(new BigDecimal("100.00"));
        testOrderItem.setProduct(testProduct);
    }

    @Nested
    @DisplayName("订单创建功能测试")
    class CreateOrderTests {

        @Test
        @DisplayName("正常创建订单")
        void testCreateOrder_Success() {
            CreateOrderRequest.OrderItemRequest itemRequest = new CreateOrderRequest.OrderItemRequest(1, 2);
            CreateOrderRequest request = new CreateOrderRequest(
                    List.of(itemRequest),
                    1,
                    "wechat",
                    "Test remark"
            );

            when(userRepository.selectById(1)).thenReturn(testUser);
            when(addressMapper.selectById(1)).thenReturn(testAddress);
            when(productMapper.selectById(1)).thenReturn(testProduct);
            when(productOrderMapper.insert(any(ProductOrder.class))).thenAnswer(invocation -> {
                ProductOrder order = invocation.getArgument(0);
                order.setId(1);
                order.setOrderNo("ORD202401010001");
                return 1;
            });
            when(productOrderItemMapper.insert(any(ProductOrderItem.class))).thenReturn(1);
            when(productMapper.updateById(any(Product.class))).thenReturn(1);

            CreateOrderResponse response = productOrderService.createOrder(1, request);

            assertNotNull(response);
            assertEquals(1, response.getOrderId());
            assertNotNull(response.getOrderNo());

            verify(productOrderMapper, times(1)).insert(any(ProductOrder.class));
            verify(productOrderItemMapper, times(1)).insert(any(ProductOrderItem.class));
            verify(productMapper, times(1)).updateById(any(Product.class));
        }

        @Test
        @DisplayName("创建订单 - 用户不存在")
        void testCreateOrder_UserNotFound() {
            CreateOrderRequest request = new CreateOrderRequest(
                    List.of(new CreateOrderRequest.OrderItemRequest(1, 1)),
                    1,
                    "wechat",
                    null
            );

            when(userRepository.selectById(1)).thenReturn(null);

            assertThrows(ResourceNotFoundException.class, () -> {
                productOrderService.createOrder(1, request);
            });

            verify(productOrderMapper, never()).insert(any());
        }

        @Test
        @DisplayName("创建订单 - 地址不存在")
        void testCreateOrder_AddressNotFound() {
            CreateOrderRequest request = new CreateOrderRequest(
                    List.of(new CreateOrderRequest.OrderItemRequest(1, 1)),
                    1,
                    "wechat",
                    null
            );

            when(userRepository.selectById(1)).thenReturn(testUser);
            when(addressMapper.selectById(1)).thenReturn(null);

            assertThrows(ResourceNotFoundException.class, () -> {
                productOrderService.createOrder(1, request);
            });
        }

        @Test
        @DisplayName("创建订单 - 地址不属于用户")
        void testCreateOrder_AddressNotBelongToUser() {
            Address otherUserAddress = new Address();
            otherUserAddress.setId(1);
            otherUserAddress.setUserId(2);

            CreateOrderRequest request = new CreateOrderRequest(
                    List.of(new CreateOrderRequest.OrderItemRequest(1, 1)),
                    1,
                    "wechat",
                    null
            );

            when(userRepository.selectById(1)).thenReturn(testUser);
            when(addressMapper.selectById(1)).thenReturn(otherUserAddress);

            assertThrows(BadRequestException.class, () -> {
                productOrderService.createOrder(1, request);
            });
        }

        @Test
        @DisplayName("创建订单 - 订单项为空")
        void testCreateOrder_EmptyItems() {
            CreateOrderRequest request = new CreateOrderRequest(
                    Collections.emptyList(),
                    1,
                    "wechat",
                    null
            );

            when(userRepository.selectById(1)).thenReturn(testUser);
            when(addressMapper.selectById(1)).thenReturn(testAddress);

            assertThrows(BadRequestException.class, () -> {
                productOrderService.createOrder(1, request);
            });
        }

        @Test
        @DisplayName("创建订单 - 商品不存在")
        void testCreateOrder_ProductNotFound() {
            CreateOrderRequest request = new CreateOrderRequest(
                    List.of(new CreateOrderRequest.OrderItemRequest(999, 1)),
                    1,
                    "wechat",
                    null
            );

            when(userRepository.selectById(1)).thenReturn(testUser);
            when(addressMapper.selectById(1)).thenReturn(testAddress);
            when(productMapper.selectById(999)).thenReturn(null);

            assertThrows(ResourceNotFoundException.class, () -> {
                productOrderService.createOrder(1, request);
            });
        }

        @Test
        @DisplayName("创建订单 - 商品已禁用")
        void testCreateOrder_ProductDisabled() {
            Product disabledProduct = new Product();
            disabledProduct.setId(1);
            disabledProduct.setName("Disabled Product");
            disabledProduct.setStatus("disabled");

            CreateOrderRequest request = new CreateOrderRequest(
                    List.of(new CreateOrderRequest.OrderItemRequest(1, 1)),
                    1,
                    "wechat",
                    null
            );

            when(userRepository.selectById(1)).thenReturn(testUser);
            when(addressMapper.selectById(1)).thenReturn(testAddress);
            when(productMapper.selectById(1)).thenReturn(disabledProduct);

            assertThrows(BadRequestException.class, () -> {
                productOrderService.createOrder(1, request);
            });
        }

        @Test
        @DisplayName("创建订单 - 库存不足")
        void testCreateOrder_InsufficientStock() {
            CreateOrderRequest request = new CreateOrderRequest(
                    List.of(new CreateOrderRequest.OrderItemRequest(1, 100)),
                    1,
                    "wechat",
                    null
            );

            when(userRepository.selectById(1)).thenReturn(testUser);
            when(addressMapper.selectById(1)).thenReturn(testAddress);
            when(productMapper.selectById(1)).thenReturn(testProduct);

            assertThrows(BadRequestException.class, () -> {
                productOrderService.createOrder(1, request);
            });
        }

        @Test
        @DisplayName("创建订单 - 不同商家的商品")
        void testCreateOrder_DifferentMerchants() {
            Product product2 = new Product();
            product2.setId(2);
            product2.setName("Product 2");
            product2.setPrice(new BigDecimal("50.00"));
            product2.setStock(10);
            product2.setStatus("enabled");
            product2.setMerchantId(2);

            Merchant merchant2 = new Merchant();
            merchant2.setId(2);
            product2.setMerchant(merchant2);

            CreateOrderRequest request = new CreateOrderRequest(
                    List.of(
                            new CreateOrderRequest.OrderItemRequest(1, 1),
                            new CreateOrderRequest.OrderItemRequest(2, 1)
                    ),
                    1,
                    "wechat",
                    null
            );

            when(userRepository.selectById(1)).thenReturn(testUser);
            when(addressMapper.selectById(1)).thenReturn(testAddress);
            when(productMapper.selectById(1)).thenReturn(testProduct);
            when(productMapper.selectById(2)).thenReturn(product2);

            assertThrows(BadRequestException.class, () -> {
                productOrderService.createOrder(1, request);
            });
        }
    }

    @Nested
    @DisplayName("订单查询功能测试")
    class QueryOrderTests {

        @Test
        @DisplayName("根据ID查询订单 - 成功")
        void testFindById_Success() {
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);

            ProductOrder result = productOrderService.findById(1);

            assertNotNull(result);
            assertEquals(1, result.getId());
            assertEquals("pending", result.getStatus());
        }

        @Test
        @DisplayName("根据ID查询订单 - 订单不存在")
        void testFindById_NotFound() {
            when(productOrderMapper.selectById(999)).thenReturn(null);

            ProductOrder result = productOrderService.findById(999);

            assertNull(result);
        }

        @Test
        @DisplayName("根据ID查询订单（含商家信息）- 成功")
        void testFindByIdWithMerchant_Success() {
            testOrder.setMerchant(testMerchant);
            when(productOrderMapper.selectByIdWithMerchant(1)).thenReturn(testOrder);

            ProductOrder result = productOrderService.findByIdWithMerchant(1);

            assertNotNull(result);
            assertNotNull(result.getMerchant());
            assertEquals("Test Merchant", result.getMerchant().getName());
        }

        @Test
        @DisplayName("根据用户ID查询订单列表")
        void testFindByUserId() {
            List<ProductOrder> orders = List.of(testOrder);
            when(productOrderMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(orders);

            List<ProductOrder> result = productOrderService.findByUserId(1);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(1, result.get(0).getUserId());
        }

        @Test
        @DisplayName("根据商家ID查询订单列表")
        void testFindByMerchantId() {
            List<ProductOrder> orders = List.of(testOrder);
            when(productOrderMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(orders);

            List<ProductOrder> result = productOrderService.findByMerchantId(1);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(1, result.get(0).getMerchantId());
        }

        @Test
        @DisplayName("根据商家ID和状态查询订单")
        void testFindByMerchantIdAndStatus() {
            List<ProductOrder> orders = List.of(testOrder);
            when(productOrderMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(orders);

            List<ProductOrder> result = productOrderService.findByMerchantIdAndStatus(1, "pending");

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("获取用户订单列表 - 分页查询")
        void testGetUserOrders() {
            Page<ProductOrder> mockPage = new Page<>(1, 10);
            mockPage.setRecords(List.of(testOrder));
            mockPage.setTotal(1);

            when(productOrderMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);
            when(productOrderItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(testOrderItem));

            PageResponse<OrderDTO> result = productOrderService.getUserOrders(
                    1, null, null, null, null, 1, 10
            );

            assertNotNull(result);
            assertEquals(1, result.getTotal());
            assertEquals(1, result.getData().size());
        }

        @Test
        @DisplayName("获取用户订单列表 - 带状态筛选")
        void testGetUserOrders_WithStatus() {
            Page<ProductOrder> mockPage = new Page<>(1, 10);
            mockPage.setRecords(List.of(testOrder));
            mockPage.setTotal(1);

            when(productOrderMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);
            when(productOrderItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(testOrderItem));

            PageResponse<OrderDTO> result = productOrderService.getUserOrders(
                    1, "pending", null, null, null, 1, 10
            );

            assertNotNull(result);
            assertEquals(1, result.getData().size());
        }

        @Test
        @DisplayName("获取订单详情 - 成功")
        void testGetOrderDetail_Success() {
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);
            when(productOrderItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(testOrderItem));

            OrderDTO result = productOrderService.getOrderDetail(1, 1);

            assertNotNull(result);
            assertEquals(1, result.getId());
            assertEquals("pending", result.getStatus());
        }

        @Test
        @DisplayName("获取订单详情 - 订单不存在")
        void testGetOrderDetail_OrderNotFound() {
            when(productOrderMapper.selectById(999)).thenReturn(null);

            assertThrows(ResourceNotFoundException.class, () -> {
                productOrderService.getOrderDetail(999, 1);
            });
        }

        @Test
        @DisplayName("获取订单详情 - 无权限查看")
        void testGetOrderDetail_NoPermission() {
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);

            assertThrows(BadRequestException.class, () -> {
                productOrderService.getOrderDetail(1, 2);
            });
        }
    }

    @Nested
    @DisplayName("订单状态更新功能测试")
    class UpdateOrderStatusTests {

        @Test
        @DisplayName("支付订单 - 成功")
        void testPayOrder_Success() {
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);
            when(productOrderMapper.updateById(any(ProductOrder.class))).thenReturn(1);

            PayResponse result = productOrderService.payOrder(1, 1, "alipay");

            assertNotNull(result);
            assertEquals(1, result.getPayId());
            assertNotNull(result.getExpireTime());

            verify(productOrderMapper, times(1)).updateById(any(ProductOrder.class));
        }

        @Test
        @DisplayName("支付订单 - 订单不存在")
        void testPayOrder_OrderNotFound() {
            when(productOrderMapper.selectById(999)).thenReturn(null);

            assertThrows(ResourceNotFoundException.class, () -> {
                productOrderService.payOrder(999, 1, "alipay");
            });
        }

        @Test
        @DisplayName("支付订单 - 无权限")
        void testPayOrder_NoPermission() {
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);

            assertThrows(BadRequestException.class, () -> {
                productOrderService.payOrder(1, 2, "alipay");
            });
        }

        @Test
        @DisplayName("支付订单 - 状态不允许支付")
        void testPayOrder_InvalidStatus() {
            testOrder.setStatus("paid");
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);

            assertThrows(BadRequestException.class, () -> {
                productOrderService.payOrder(1, 1, "alipay");
            });
        }

        @Test
        @DisplayName("确认收货 - 成功")
        void testConfirmReceive_Success() {
            testOrder.setStatus("shipped");
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);
            when(productOrderMapper.updateById(any(ProductOrder.class))).thenReturn(1);

            assertDoesNotThrow(() -> {
                productOrderService.confirmReceive(1, 1);
            });

            verify(productOrderMapper, times(1)).updateById(any(ProductOrder.class));
        }

        @Test
        @DisplayName("确认收货 - 订单不存在")
        void testConfirmReceive_OrderNotFound() {
            when(productOrderMapper.selectById(999)).thenReturn(null);

            assertThrows(ResourceNotFoundException.class, () -> {
                productOrderService.confirmReceive(999, 1);
            });
        }

        @Test
        @DisplayName("确认收货 - 无权限")
        void testConfirmReceive_NoPermission() {
            testOrder.setStatus("shipped");
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);

            assertThrows(BadRequestException.class, () -> {
                productOrderService.confirmReceive(1, 2);
            });
        }

        @Test
        @DisplayName("确认收货 - 状态不允许确认")
        void testConfirmReceive_InvalidStatus() {
            testOrder.setStatus("pending");
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);

            assertThrows(BadRequestException.class, () -> {
                productOrderService.confirmReceive(1, 1);
            });
        }

        @Test
        @DisplayName("获取支付状态 - 待支付")
        void testGetPayStatus_Pending() {
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);

            PayStatusResponse result = productOrderService.getPayStatus(1, 1);

            assertNotNull(result);
            assertEquals("pending", result.getPayStatus());
        }

        @Test
        @DisplayName("获取支付状态 - 已支付")
        void testGetPayStatus_Success() {
            testOrder.setStatus("paid");
            testOrder.setPaidAt(LocalDateTime.now());
            testOrder.setTransactionId("TXN123456");
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);

            PayStatusResponse result = productOrderService.getPayStatus(1, 1);

            assertNotNull(result);
            assertEquals("success", result.getPayStatus());
            assertNotNull(result.getPayTime());
            assertEquals("TXN123456", result.getTransactionId());
        }

        @Test
        @DisplayName("获取支付状态 - 已取消")
        void testGetPayStatus_Cancelled() {
            testOrder.setStatus("cancelled");
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);

            PayStatusResponse result = productOrderService.getPayStatus(1, 1);

            assertNotNull(result);
            assertEquals("failed", result.getPayStatus());
        }
    }

    @Nested
    @DisplayName("订单取消功能测试")
    class CancelOrderTests {

        @Test
        @DisplayName("取消订单 - 待支付状态")
        void testCancelOrder_Pending() {
            testOrder.setStatus("pending");
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);
            when(productOrderMapper.updateById(any(ProductOrder.class))).thenReturn(1);

            assertDoesNotThrow(() -> {
                productOrderService.cancelOrder(1, 1);
            });

            verify(productOrderMapper, times(1)).updateById(any(ProductOrder.class));
            verify(productMapper, never()).updateById(any());
        }

        @Test
        @DisplayName("取消订单 - 已支付状态（恢复库存）")
        void testCancelOrder_Paid() {
            testOrder.setStatus("paid");
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);
            when(productOrderMapper.updateById(any(ProductOrder.class))).thenReturn(1);
            when(productOrderItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(testOrderItem));
            when(productMapper.selectById(1)).thenReturn(testProduct);
            when(productMapper.updateById(any(Product.class))).thenReturn(1);

            assertDoesNotThrow(() -> {
                productOrderService.cancelOrder(1, 1);
            });

            verify(productOrderMapper, times(1)).updateById(any(ProductOrder.class));
            verify(productMapper, times(1)).updateById(any(Product.class));
        }

        @Test
        @DisplayName("取消订单 - 订单不存在")
        void testCancelOrder_OrderNotFound() {
            when(productOrderMapper.selectById(999)).thenReturn(null);

            assertThrows(ResourceNotFoundException.class, () -> {
                productOrderService.cancelOrder(999, 1);
            });
        }

        @Test
        @DisplayName("取消订单 - 无权限")
        void testCancelOrder_NoPermission() {
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);

            assertThrows(BadRequestException.class, () -> {
                productOrderService.cancelOrder(1, 2);
            });
        }

        @Test
        @DisplayName("取消订单 - 状态不允许取消")
        void testCancelOrder_InvalidStatus() {
            testOrder.setStatus("shipped");
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);

            assertThrows(BadRequestException.class, () -> {
                productOrderService.cancelOrder(1, 1);
            });
        }

        @Test
        @DisplayName("退款订单 - 成功")
        void testRefundOrder_Success() {
            testOrder.setStatus("paid");
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);
            when(productOrderMapper.updateById(any(ProductOrder.class))).thenReturn(1);
            when(productOrderItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(testOrderItem));
            when(productMapper.selectById(1)).thenReturn(testProduct);
            when(productMapper.updateById(any(Product.class))).thenReturn(1);

            assertDoesNotThrow(() -> {
                productOrderService.refundOrder(1, 1, "不想买了");
            });

            verify(productMapper, times(1)).updateById(any(Product.class));
        }

        @Test
        @DisplayName("退款订单 - 已发货状态")
        void testRefundOrder_Shipped() {
            testOrder.setStatus("shipped");
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);
            when(productOrderMapper.updateById(any(ProductOrder.class))).thenReturn(1);
            when(productOrderItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(testOrderItem));
            when(productMapper.selectById(1)).thenReturn(testProduct);
            when(productMapper.updateById(any(Product.class))).thenReturn(1);

            assertDoesNotThrow(() -> {
                productOrderService.refundOrder(1, 1, "商品有问题");
            });
        }

        @Test
        @DisplayName("退款订单 - 状态不允许退款")
        void testRefundOrder_InvalidStatus() {
            testOrder.setStatus("pending");
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);

            assertThrows(BadRequestException.class, () -> {
                productOrderService.refundOrder(1, 1, "不想买了");
            });
        }
    }

    @Nested
    @DisplayName("订单删除功能测试")
    class DeleteOrderTests {

        @Test
        @DisplayName("删除订单 - 已完成状态")
        void testDeleteOrder_Completed() {
            testOrder.setStatus("completed");
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);
            when(productOrderItemMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);
            when(productOrderMapper.deleteById(1)).thenReturn(1);

            assertDoesNotThrow(() -> {
                productOrderService.deleteOrder(1, 1);
            });

            verify(productOrderItemMapper, times(1)).delete(any(LambdaQueryWrapper.class));
            verify(productOrderMapper, times(1)).deleteById(1);
        }

        @Test
        @DisplayName("删除订单 - 已取消状态")
        void testDeleteOrder_Cancelled() {
            testOrder.setStatus("cancelled");
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);
            when(productOrderItemMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);
            when(productOrderMapper.deleteById(1)).thenReturn(1);

            assertDoesNotThrow(() -> {
                productOrderService.deleteOrder(1, 1);
            });
        }

        @Test
        @DisplayName("删除订单 - 订单不存在")
        void testDeleteOrder_OrderNotFound() {
            when(productOrderMapper.selectById(999)).thenReturn(null);

            assertThrows(ResourceNotFoundException.class, () -> {
                productOrderService.deleteOrder(999, 1);
            });
        }

        @Test
        @DisplayName("删除订单 - 无权限")
        void testDeleteOrder_NoPermission() {
            testOrder.setStatus("completed");
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);

            assertThrows(BadRequestException.class, () -> {
                productOrderService.deleteOrder(1, 2);
            });
        }

        @Test
        @DisplayName("删除订单 - 状态不允许删除")
        void testDeleteOrder_InvalidStatus() {
            testOrder.setStatus("pending");
            when(productOrderMapper.selectById(1)).thenReturn(testOrder);

            assertThrows(BadRequestException.class, () -> {
                productOrderService.deleteOrder(1, 1);
            });
        }
    }

    @Nested
    @DisplayName("批量操作功能测试")
    class BatchOperationTests {

        @Test
        @DisplayName("批量取消订单")
        void testBatchCancel() {
            ProductOrder order1 = new ProductOrder();
            order1.setId(1);
            order1.setUserId(1);
            order1.setStatus("pending");

            ProductOrder order2 = new ProductOrder();
            order2.setId(2);
            order2.setUserId(1);
            order2.setStatus("pending");

            when(productOrderMapper.selectById(1)).thenReturn(order1);
            when(productOrderMapper.selectById(2)).thenReturn(order2);
            when(productOrderMapper.updateById(any(ProductOrder.class))).thenReturn(1);

            assertDoesNotThrow(() -> {
                productOrderService.batchCancel(List.of(1, 2), 1);
            });

            verify(productOrderMapper, times(2)).updateById(any(ProductOrder.class));
        }

        @Test
        @DisplayName("批量删除订单")
        void testBatchDelete() {
            ProductOrder order1 = new ProductOrder();
            order1.setId(1);
            order1.setUserId(1);
            order1.setStatus("completed");

            ProductOrder order2 = new ProductOrder();
            order2.setId(2);
            order2.setUserId(1);
            order2.setStatus("cancelled");

            when(productOrderMapper.selectById(1)).thenReturn(order1);
            when(productOrderMapper.selectById(2)).thenReturn(order2);
            when(productOrderItemMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);
            when(productOrderMapper.deleteById(anyInt())).thenReturn(1);

            assertDoesNotThrow(() -> {
                productOrderService.batchDelete(List.of(1, 2), 1);
            });

            verify(productOrderMapper, times(2)).deleteById(anyInt());
        }
    }

    @Nested
    @DisplayName("订单预览功能测试")
    class PreviewOrderTests {

        @Test
        @DisplayName("预览订单 - 成功")
        void testPreviewOrder_Success() {
            PreviewOrderRequest.OrderItemRequest itemRequest = new PreviewOrderRequest.OrderItemRequest(1, 2);
            PreviewOrderRequest request = new PreviewOrderRequest();
            request.setItems(List.of(itemRequest));

            when(productMapper.selectById(1)).thenReturn(testProduct);

            OrderPreviewDTO result = productOrderService.previewOrder(1, request);

            assertNotNull(result);
            assertEquals(1, result.getItems().size());
            assertEquals(new BigDecimal("200.00"), result.getProductTotal());
        }

        @Test
        @DisplayName("预览订单 - 订单项为空")
        void testPreviewOrder_EmptyItems() {
            PreviewOrderRequest request = new PreviewOrderRequest();
            request.setItems(Collections.emptyList());

            assertThrows(BadRequestException.class, () -> {
                productOrderService.previewOrder(1, request);
            });
        }

        @Test
        @DisplayName("预览订单 - 商品不存在")
        void testPreviewOrder_ProductNotFound() {
            PreviewOrderRequest.OrderItemRequest itemRequest = new PreviewOrderRequest.OrderItemRequest(999, 1);
            PreviewOrderRequest request = new PreviewOrderRequest();
            request.setItems(List.of(itemRequest));

            when(productMapper.selectById(999)).thenReturn(null);

            assertThrows(ResourceNotFoundException.class, () -> {
                productOrderService.previewOrder(1, request);
            });
        }

        @Test
        @DisplayName("预览订单 - 商品已禁用")
        void testPreviewOrder_ProductDisabled() {
            Product disabledProduct = new Product();
            disabledProduct.setId(1);
            disabledProduct.setName("Disabled Product");
            disabledProduct.setStatus("disabled");

            PreviewOrderRequest.OrderItemRequest itemRequest = new PreviewOrderRequest.OrderItemRequest(1, 1);
            PreviewOrderRequest request = new PreviewOrderRequest();
            request.setItems(List.of(itemRequest));

            when(productMapper.selectById(1)).thenReturn(disabledProduct);

            assertThrows(BadRequestException.class, () -> {
                productOrderService.previewOrder(1, request);
            });
        }
    }

    @Nested
    @DisplayName("订单更新功能测试")
    class UpdateOrderTests {

        @Test
        @DisplayName("更新订单 - 成功")
        void testUpdate_Success() {
            testOrder.setStatus("paid");
            when(productOrderMapper.updateById(testOrder)).thenReturn(1);

            ProductOrder result = productOrderService.update(testOrder);

            assertNotNull(result);
            assertEquals("paid", result.getStatus());
            verify(productOrderMapper, times(1)).updateById(testOrder);
        }
    }

    @Nested
    @DisplayName("私有方法测试 - 通过公共方法间接测试")
    class PrivateMethodTests {

        @Test
        @DisplayName("测试库存恢复功能")
        void testRestoreStock() {
            testOrder.setStatus("paid");
            testProduct.setStock(8);

            when(productOrderMapper.selectById(1)).thenReturn(testOrder);
            when(productOrderMapper.updateById(any(ProductOrder.class))).thenReturn(1);
            when(productOrderItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(testOrderItem));
            when(productMapper.selectById(1)).thenReturn(testProduct);
            when(productMapper.updateById(any(Product.class))).thenAnswer(invocation -> {
                Product updatedProduct = invocation.getArgument(0);
                assertEquals(9, updatedProduct.getStock());
                return 1;
            });

            productOrderService.cancelOrder(1, 1);

            verify(productMapper, times(1)).updateById(any(Product.class));
        }

        @Test
        @DisplayName("测试地址格式化功能")
        void testFormatAddress() {
            CreateOrderRequest request = new CreateOrderRequest(
                    List.of(new CreateOrderRequest.OrderItemRequest(1, 1)),
                    1,
                    "wechat",
                    null
            );

            when(userRepository.selectById(1)).thenReturn(testUser);
            when(addressMapper.selectById(1)).thenReturn(testAddress);
            when(productMapper.selectById(1)).thenReturn(testProduct);
            when(productOrderMapper.insert(any(ProductOrder.class))).thenAnswer(invocation -> {
                ProductOrder order = invocation.getArgument(0);
                String expectedAddress = "Beijing Beijing Chaoyang Test Street 123 (John Doe 13800138000)";
                assertEquals(expectedAddress, order.getShippingAddress());
                return 1;
            });
            when(productOrderItemMapper.insert(any(ProductOrderItem.class))).thenReturn(1);
            when(productMapper.updateById(any(Product.class))).thenReturn(1);

            productOrderService.createOrder(1, request);

            verify(productOrderMapper, times(1)).insert(any(ProductOrder.class));
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("创建订单 - 多个商品项")
        void testCreateOrder_MultipleItems() {
            Product product2 = new Product();
            product2.setId(2);
            product2.setName("Product 2");
            product2.setPrice(new BigDecimal("50.00"));
            product2.setStock(20);
            product2.setStatus("enabled");
            product2.setMerchantId(1);
            product2.setMerchant(testMerchant);

            CreateOrderRequest request = new CreateOrderRequest(
                    List.of(
                            new CreateOrderRequest.OrderItemRequest(1, 2),
                            new CreateOrderRequest.OrderItemRequest(2, 3)
                    ),
                    1,
                    "wechat",
                    null
            );

            when(userRepository.selectById(1)).thenReturn(testUser);
            when(addressMapper.selectById(1)).thenReturn(testAddress);
            when(productMapper.selectById(1)).thenReturn(testProduct);
            when(productMapper.selectById(2)).thenReturn(product2);
            when(productOrderMapper.insert(any(ProductOrder.class))).thenAnswer(invocation -> {
                ProductOrder order = invocation.getArgument(0);
                order.setId(1);
                assertEquals(new BigDecimal("350.00"), order.getTotalPrice());
                return 1;
            });
            when(productOrderItemMapper.insert(any(ProductOrderItem.class))).thenReturn(1);
            when(productMapper.updateById(any(Product.class))).thenReturn(1);

            CreateOrderResponse response = productOrderService.createOrder(1, request);

            assertNotNull(response);
            verify(productOrderItemMapper, times(2)).insert(any(ProductOrderItem.class));
            verify(productMapper, times(2)).updateById(any(Product.class));
        }

        @Test
        @DisplayName("查询订单 - 空列表")
        void testFindByUserId_EmptyList() {
            when(productOrderMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

            List<ProductOrder> result = productOrderService.findByUserId(1);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("获取用户订单列表 - 带日期范围筛选")
        void testGetUserOrders_WithDateRange() {
            Page<ProductOrder> mockPage = new Page<>(1, 10);
            mockPage.setRecords(List.of(testOrder));
            mockPage.setTotal(1);

            LocalDateTime startDate = LocalDateTime.now().minusDays(7);
            LocalDateTime endDate = LocalDateTime.now();

            when(productOrderMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);
            when(productOrderItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(testOrderItem));

            PageResponse<OrderDTO> result = productOrderService.getUserOrders(
                    1, null, null, startDate, endDate, 1, 10
            );

            assertNotNull(result);
            assertEquals(1, result.getData().size());
        }

        @Test
        @DisplayName("获取用户订单列表 - 带关键字搜索")
        void testGetUserOrders_WithKeyword() {
            Page<ProductOrder> mockPage = new Page<>(1, 10);
            mockPage.setRecords(List.of(testOrder));
            mockPage.setTotal(1);

            when(productOrderMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(mockPage);
            when(productOrderItemMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(testOrderItem));

            PageResponse<OrderDTO> result = productOrderService.getUserOrders(
                    1, null, "ORD2024", null, null, 1, 10
            );

            assertNotNull(result);
            assertEquals(1, result.getData().size());
        }
    }
}
