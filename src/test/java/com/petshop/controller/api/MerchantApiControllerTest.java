package com.petshop.controller.api;

import com.petshop.entity.Merchant;
import com.petshop.entity.Service;
import com.petshop.entity.Product;
import com.petshop.entity.Appointment;
import com.petshop.entity.ProductOrder;
import com.petshop.entity.Review;
import com.petshop.service.MerchantService;
import com.petshop.service.ServiceService;
import com.petshop.service.ProductService;
import com.petshop.service.AppointmentService;
import com.petshop.service.ProductOrderService;
import com.petshop.service.ReviewService;
import com.petshop.test.BaseTest;
import com.petshop.test.JwtUtil;
import com.petshop.test.TestDataGenerator;
import com.petshop.dto.CreateOrderRequest;
import com.petshop.dto.CreateOrderResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

@AutoConfigureMockMvc
public class MerchantApiControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private ProductService productService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private ProductOrderService productOrderService;
    @Autowired
    private ReviewService reviewService;

    private ObjectMapper objectMapper;
    private TestDataGenerator testDataGenerator;
    private String merchantToken;
    private Merchant testMerchant;
    private Service testService;
    private Product testProduct;
    private Appointment testAppointment;
    private ProductOrder testOrder;
    private Review testReview;

    @BeforeEach
    public void setUp() {
        // 初始化ObjectMapper
        objectMapper = new ObjectMapper();
        // 初始化TestDataGenerator
        testDataGenerator = new TestDataGenerator();
        // 生成测试商家数据
        testMerchant = testDataGenerator.generateMerchant();
        // 保存商家到数据库
        testMerchant = merchantService.register(testMerchant);
        // 生成商家token
        merchantToken = JwtUtil.generateToken(testMerchant.getPhone());
        // 生成测试服务数据
        testService = testDataGenerator.generateService(testMerchant);
        // 保存服务到数据库
        testService = serviceService.create(testService);
        // 生成测试商品数据
        testProduct = testDataGenerator.generateProduct(testMerchant);
        // 保存商品到数据库
        testProduct = productService.create(testProduct);
        // 生成测试预约数据
        testAppointment = testDataGenerator.generateAppointment(testMerchant);
        // 保存预约到数据库
        testAppointment = appointmentService.create(testAppointment);
        // 生成测试订单数据
        testOrder = testDataGenerator.generateProductOrder(testMerchant);
        // 创建订单请求
        CreateOrderRequest.OrderItemRequest item = new CreateOrderRequest.OrderItemRequest(testProduct.getId(), 1);
        CreateOrderRequest orderRequest = new CreateOrderRequest(Collections.singletonList(item), 1, "wechat", null);
        // 保存订单到数据库
        CreateOrderResponse response = productOrderService.createOrder(1, orderRequest);
        testOrder = productOrderService.findById(response.getOrderId());
        // 生成测试评价数据
        testReview = testDataGenerator.generateReview(testMerchant);
        // 保存评价到数据库
        testReview = reviewService.create(testReview);
    }

    @AfterEach
    public void tearDown() {
        // 清理测试数据
        if (testReview != null) {
            reviewService.delete(testReview.getId());
        }
        if (testOrder != null) {
            productOrderService.deleteOrder(testOrder.getId(), 1);
        }
        if (testAppointment != null) {
            appointmentService.delete(testAppointment.getId());
        }
        if (testProduct != null) {
            productService.delete(testProduct.getId());
        }
        if (testService != null) {
            serviceService.delete(testService.getId());
        }
        if (testMerchant != null) {
            merchantService.delete(testMerchant.getId());
        }
        testDataGenerator.cleanup();
    }

    // ==================== 服务管理接口测试 ====================

    @Test
    public void testGetServices() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/merchant/services")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray());
    }

    @Test
    public void testAddService() throws Exception {
        Map<String, Object> serviceData = new HashMap<>();
        serviceData.put("name", "测试服务");
        serviceData.put("description", "测试服务描述");
        serviceData.put("price", 100.00);
        serviceData.put("duration", 60);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/merchant/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(serviceData)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("测试服务"));
    }

    @Test
    public void testUpdateService() throws Exception {
        Map<String, Object> serviceData = new HashMap<>();
        serviceData.put("name", "更新后的测试服务");
        serviceData.put("description", "更新后的测试服务描述");
        serviceData.put("price", 150.00);
        serviceData.put("duration", 90);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/merchant/services/{id}", testService.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(serviceData)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("更新后的测试服务"));
    }

    @Test
    public void testDeleteService() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/merchant/services/{id}", testService.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("服务删除成功"));
    }

    // ==================== 商品管理接口测试 ====================

    @Test
    public void testGetProducts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/merchant/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray());
    }

    @Test
    public void testAddProduct() throws Exception {
        Map<String, Object> productData = new HashMap<>();
        productData.put("name", "测试商品");
        productData.put("description", "测试商品描述");
        productData.put("price", 50.00);
        productData.put("stock", 100);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/merchant/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productData)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("测试商品"));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        Map<String, Object> productData = new HashMap<>();
        productData.put("name", "更新后的测试商品");
        productData.put("description", "更新后的测试商品描述");
        productData.put("price", 75.00);
        productData.put("stock", 150);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/merchant/products/{id}", testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productData)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("更新后的测试商品"));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/merchant/products/{id}", testProduct.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("商品删除成功"));
    }

    // ==================== 订单处理接口测试 ====================

    @Test
    public void testGetAppointments() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/merchant/appointments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray());
    }

    @Test
    public void testUpdateAppointmentStatus() throws Exception {
        Map<String, String> statusData = new HashMap<>();
        statusData.put("status", "confirmed");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/merchant/appointments/{id}/status", testAppointment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusData)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("confirmed"));
    }

    @Test
    public void testGetOrders() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/merchant/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray());
    }

    @Test
    public void testUpdateOrderStatus() throws Exception {
        Map<String, String> statusData = new HashMap<>();
        statusData.put("status", "shipped");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/merchant/orders/{id}/status", testOrder.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusData)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.status").value("shipped"));
    }

    // ==================== 评价管理接口测试 ====================

    @Test
    public void testGetReviews() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/merchant/reviews")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists());
    }

    @Test
    public void testReplyToReview() throws Exception {
        Map<String, String> replyData = new HashMap<>();
        replyData.put("reply", "感谢您的评价，我们会继续努力！");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/merchant/reviews/{id}/reply", testReview.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(replyData)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.reply").value("感谢您的评价，我们会继续努力！"));
    }

    @Test
    public void testDeleteReview() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/merchant/reviews/{id}", testReview.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("删除评价成功"));
    }

    // ==================== 店铺管理接口测试 ====================

    @Test
    public void testGetProfile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/merchant/profile")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value(testMerchant.getName()));
    }

    @Test
    public void testUpdateProfile() throws Exception {
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("name", "更新后的测试商家");
        profileData.put("contactPerson", "更新后的联系人");
        profileData.put("phone", testMerchant.getPhone()); // 保持手机号不变
        profileData.put("email", "updated@example.com");
        profileData.put("address", "更新后的地址");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/merchant/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileData)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.name").value("更新后的测试商家"));
    }

    @Test
    public void testToggleShopStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/merchant/settings/toggle-status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.isOpen").isBoolean());
    }

    // ==================== 统计接口测试 ====================

    @Test
    public void testGetRevenueStats() throws Exception {
        String startDate = LocalDate.now().minusMonths(1).toString();
        String endDate = LocalDate.now().toString();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/merchant/revenue-stats")
                .param("startDate", startDate)
                .param("endDate", endDate)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists());
    }

    @Test
    public void testExportRevenueStats() throws Exception {
        String startDate = LocalDate.now().minusMonths(1).toString();
        String endDate = LocalDate.now().toString();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/merchant/revenue-stats/export")
                .param("startDate", startDate)
                .param("endDate", endDate)
                .param("format", "csv")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists("Content-Disposition"));
    }

    @Test
    public void testGetAppointmentStats() throws Exception {
        String startDate = LocalDate.now().minusMonths(1).toString();
        String endDate = LocalDate.now().toString();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/merchant/appointment-stats")
                .param("startDate", startDate)
                .param("endDate", endDate)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists());
    }

    @Test
    public void testExportAppointmentStats() throws Exception {
        String startDate = LocalDate.now().minusMonths(1).toString();
        String endDate = LocalDate.now().toString();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/merchant/appointment-stats/export")
                .param("startDate", startDate)
                .param("endDate", endDate)
                .param("format", "excel")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists("Content-Disposition"));
    }

    @Test
    public void testGetDashboardStats() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/merchant/dashboard")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").exists());
    }
}
