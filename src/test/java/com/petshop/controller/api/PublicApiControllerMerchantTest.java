package com.petshop.controller.api;

import com.petshop.entity.Merchant;
import com.petshop.entity.Product;
import com.petshop.entity.Review;
import com.petshop.entity.Service;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("公共商家API测试")
public class PublicApiControllerMerchantTest extends UserApiControllerTestBase {

    private PublicApiController controller;

    @BeforeEach
    public void setupController() {
        controller = new PublicApiController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "merchantService", merchantService);
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "serviceService", serviceService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("GET /api/merchants - 获取商家列表")
    class GetMerchantsTests {

        @Test
        @DisplayName("成功获取商家列表")
        void testGetMerchants_Success() throws Exception {
            Merchant merchant1 = TestDataFactory.createMerchant(1, "宠物乐园", "shop1@test.com");
            Merchant merchant2 = TestDataFactory.createMerchant(2, "爱宠之家", "shop2@test.com");
            List<Merchant> merchants = Arrays.asList(merchant1, merchant2);

            when(merchantService.findAll()).thenReturn(merchants);

            var result = mockMvc.perform(get("/api/public/merchants")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].name").value("宠物乐园"))
                    .andExpect(jsonPath("$[1].name").value("爱宠之家"));

            verify(merchantService).findAll();
        }

        @Test
        @DisplayName("成功获取空商家列表")
        void testGetMerchants_EmptyList() throws Exception {
            when(merchantService.findAll()).thenReturn(Collections.emptyList());

            var result = mockMvc.perform(get("/api/public/merchants")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(merchantService).findAll();
        }

        @Test
        @DisplayName("只返回已审核通过的商家")
        void testGetMerchants_OnlyApprovedMerchants() throws Exception {
            Merchant approvedMerchant = TestDataFactory.createMerchant(1, "已审核商家", "approved@test.com");
            approvedMerchant.setStatus("approved");

            Merchant pendingMerchant = TestDataFactory.createMerchant(2, "待审核商家", "pending@test.com");
            pendingMerchant.setStatus("pending");

            Merchant rejectedMerchant = TestDataFactory.createMerchant(3, "已拒绝商家", "rejected@test.com");
            rejectedMerchant.setStatus("rejected");

            List<Merchant> merchants = Arrays.asList(approvedMerchant, pendingMerchant, rejectedMerchant);
            when(merchantService.findAll()).thenReturn(merchants);

            var result = mockMvc.perform(get("/api/public/merchants")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].name").value("已审核商家"))
                    .andExpect(jsonPath("$[0].status").value("approved"));

            verify(merchantService).findAll();
        }

        @Test
        @DisplayName("服务层异常返回空列表")
        void testGetMerchants_ServiceError() throws Exception {
            when(merchantService.findAll()).thenThrow(new RuntimeException("数据库连接失败"));

            var result = mockMvc.perform(get("/api/public/merchants")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().is5xxServerError());
        }
    }

    @Nested
    @DisplayName("GET /api/merchants/{id} - 获取商家详情")
    class GetMerchantByIdTests {

        @Test
        @DisplayName("成功获取商家详情")
        void testGetMerchantById_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "shop@test.com");
            merchant.setAddress("北京市朝阳区xxx街道");
            merchant.setPhone("13800138000");

            when(merchantService.findById(1)).thenReturn(merchant);

            var result = mockMvc.perform(get("/api/public/merchants/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("宠物乐园"))
                    .andExpect(jsonPath("$.email").value("shop@test.com"))
                    .andExpect(jsonPath("$.address").value("北京市朝阳区xxx街道"))
                    .andExpect(jsonPath("$.phone").value("13800138000"));

            verify(merchantService).findById(1);
        }

        @Test
        @DisplayName("商家不存在返回404")
        void testGetMerchantById_NotFound() throws Exception {
            when(merchantService.findById(999)).thenReturn(null);

            var result = mockMvc.perform(get("/api/public/merchants/{id}", 999)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isNotFound());

            verify(merchantService).findById(999);
        }

        @Test
        @DisplayName("待审核商家返回404")
        void testGetMerchantById_PendingStatus() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "待审核商家", "pending@test.com");
            merchant.setStatus("pending");

            when(merchantService.findById(1)).thenReturn(merchant);

            var result = mockMvc.perform(get("/api/public/merchants/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isNotFound());

            verify(merchantService).findById(1);
        }

        @Test
        @DisplayName("已拒绝商家返回404")
        void testGetMerchantById_RejectedStatus() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "已拒绝商家", "rejected@test.com");
            merchant.setStatus("rejected");

            when(merchantService.findById(1)).thenReturn(merchant);

            var result = mockMvc.perform(get("/api/public/merchants/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isNotFound());

            verify(merchantService).findById(1);
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetMerchantById_ServiceError() throws Exception {
            when(merchantService.findById(1)).thenThrow(new RuntimeException("数据库连接失败"));

            var result = mockMvc.perform(get("/api/public/merchants/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().is5xxServerError());
        }
    }

    @Nested
    @DisplayName("GET /api/merchants/{id}/services - 获取商家服务列表")
    class GetMerchantServicesTests {

        @Test
        @DisplayName("成功获取商家服务列表")
        void testGetMerchantServices_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "shop@test.com");
            Service service1 = TestDataFactory.createService(1, merchant, "宠物洗澡", new BigDecimal("99.00"));
            Service service2 = TestDataFactory.createService(2, merchant, "宠物美容", new BigDecimal("199.00"));
            List<Service> services = Arrays.asList(service1, service2);

            when(merchantService.findById(1)).thenReturn(merchant);
            when(serviceService.findByMerchantId(1)).thenReturn(services);

            var result = mockMvc.perform(get("/api/public/merchants/{id}/services", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].name").value("宠物洗澡"))
                    .andExpect(jsonPath("$[1].name").value("宠物美容"));

            verify(merchantService).findById(1);
            verify(serviceService).findByMerchantId(1);
        }

        @Test
        @DisplayName("成功获取空服务列表")
        void testGetMerchantServices_EmptyList() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "shop@test.com");

            when(merchantService.findById(1)).thenReturn(merchant);
            when(serviceService.findByMerchantId(1)).thenReturn(Collections.emptyList());

            var result = mockMvc.perform(get("/api/public/merchants/{id}/services", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(merchantService).findById(1);
            verify(serviceService).findByMerchantId(1);
        }

        @Test
        @DisplayName("商家不存在返回404")
        void testGetMerchantServices_MerchantNotFound() throws Exception {
            when(merchantService.findById(999)).thenReturn(null);

            var result = mockMvc.perform(get("/api/public/merchants/{id}/services", 999)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isNotFound());

            verify(merchantService).findById(999);
            verify(serviceService, never()).findByMerchantId(anyInt());
        }

        @Test
        @DisplayName("待审核商家返回404")
        void testGetMerchantServices_PendingMerchant() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "待审核商家", "pending@test.com");
            merchant.setStatus("pending");

            when(merchantService.findById(1)).thenReturn(merchant);

            var result = mockMvc.perform(get("/api/public/merchants/{id}/services", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isNotFound());

            verify(merchantService).findById(1);
            verify(serviceService, never()).findByMerchantId(anyInt());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetMerchantServices_ServiceError() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "shop@test.com");

            when(merchantService.findById(1)).thenReturn(merchant);
            when(serviceService.findByMerchantId(1)).thenThrow(new RuntimeException("数据库连接失败"));

            var result = mockMvc.perform(get("/api/public/merchants/{id}/services", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().is5xxServerError());
        }
    }

    @Nested
    @DisplayName("GET /api/merchants/{id}/products - 获取商家商品列表")
    class GetMerchantProductsTests {

        @Test
        @DisplayName("成功获取商家商品列表")
        void testGetMerchantProducts_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "shop@test.com");
            Product product1 = TestDataFactory.createProduct(1, merchant, "狗粮", new BigDecimal("99.00"), 100);
            Product product2 = TestDataFactory.createProduct(2, merchant, "猫粮", new BigDecimal("89.00"), 50);
            List<Product> products = Arrays.asList(product1, product2);

            when(merchantService.findById(1)).thenReturn(merchant);
            when(productService.findByMerchantId(1)).thenReturn(products);

            var result = mockMvc.perform(get("/api/public/merchants/{id}/products", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].name").value("狗粮"))
                    .andExpect(jsonPath("$[1].name").value("猫粮"));

            verify(merchantService).findById(1);
            verify(productService).findByMerchantId(1);
        }

        @Test
        @DisplayName("成功获取空商品列表")
        void testGetMerchantProducts_EmptyList() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "shop@test.com");

            when(merchantService.findById(1)).thenReturn(merchant);
            when(productService.findByMerchantId(1)).thenReturn(Collections.emptyList());

            var result = mockMvc.perform(get("/api/public/merchants/{id}/products", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(merchantService).findById(1);
            verify(productService).findByMerchantId(1);
        }

        @Test
        @DisplayName("商家不存在返回404")
        void testGetMerchantProducts_MerchantNotFound() throws Exception {
            when(merchantService.findById(999)).thenReturn(null);

            var result = mockMvc.perform(get("/api/public/merchants/{id}/products", 999)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isNotFound());

            verify(merchantService).findById(999);
            verify(productService, never()).findByMerchantId(anyInt());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetMerchantProducts_ServiceError() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "shop@test.com");

            when(merchantService.findById(1)).thenReturn(merchant);
            when(productService.findByMerchantId(1)).thenThrow(new RuntimeException("数据库连接失败"));

            var result = mockMvc.perform(get("/api/public/merchants/{id}/products", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().is5xxServerError());
        }
    }

    @Nested
    @DisplayName("GET /api/merchants/{id}/reviews - 获取商家评价列表")
    class GetMerchantReviewsTests {

        @Test
        @DisplayName("成功获取商家评价列表")
        void testGetMerchantReviews_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "shop@test.com");
            Review review1 = TestDataFactory.createReview(1, merchant, 5, "服务非常好！");
            Review review2 = TestDataFactory.createReview(2, merchant, 4, "服务不错，下次还会来");
            List<Review> reviews = Arrays.asList(review1, review2);

            when(merchantService.findById(1)).thenReturn(merchant);
            when(reviewService.findByMerchantId(1)).thenReturn(reviews);

            var result = mockMvc.perform(get("/api/public/merchants/{id}/reviews", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].rating").value(5))
                    .andExpect(jsonPath("$[0].comment").value("服务非常好！"))
                    .andExpect(jsonPath("$[1].rating").value(4));

            verify(merchantService).findById(1);
            verify(reviewService).findByMerchantId(1);
        }

        @Test
        @DisplayName("成功获取空评价列表")
        void testGetMerchantReviews_EmptyList() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "shop@test.com");

            when(merchantService.findById(1)).thenReturn(merchant);
            when(reviewService.findByMerchantId(1)).thenReturn(Collections.emptyList());

            var result = mockMvc.perform(get("/api/public/merchants/{id}/reviews", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(merchantService).findById(1);
            verify(reviewService).findByMerchantId(1);
        }

        @Test
        @DisplayName("商家不存在返回404")
        void testGetMerchantReviews_MerchantNotFound() throws Exception {
            when(merchantService.findById(999)).thenReturn(null);

            var result = mockMvc.perform(get("/api/public/merchants/{id}/reviews", 999)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isNotFound());

            verify(merchantService).findById(999);
            verify(reviewService, never()).findByMerchantId(anyInt());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetMerchantReviews_ServiceError() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "shop@test.com");

            when(merchantService.findById(1)).thenReturn(merchant);
            when(reviewService.findByMerchantId(1)).thenThrow(new RuntimeException("数据库连接失败"));

            var result = mockMvc.perform(get("/api/public/merchants/{id}/reviews", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().is5xxServerError());
        }
    }

    @Nested
    @DisplayName("GET /api/merchants/{id}/available-slots - 获取可用预约时段")
    class GetAvailableSlotsTests {

        @Test
        @DisplayName("成功获取可用时段")
        void testGetAvailableSlots_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "shop@test.com");
            Service service = TestDataFactory.createService(1, merchant, "宠物洗澡", new BigDecimal("99.00"));
            service.setDuration(60);

            List<String> availableSlots = Arrays.asList("09:00", "10:00", "11:00", "14:00", "15:00", "16:00");

            when(merchantService.findById(1)).thenReturn(merchant);
            when(serviceService.findByMerchantId(1)).thenReturn(Arrays.asList(service));

            var result = mockMvc.perform(get("/api/public/merchants/{id}/available-slots", 1)
                    .param("date", "2024-01-15")
                    .param("serviceId", "1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk());

            verify(merchantService).findById(1);
        }

        @Test
        @DisplayName("商家不存在返回404")
        void testGetAvailableSlots_MerchantNotFound() throws Exception {
            when(merchantService.findById(999)).thenReturn(null);

            var result = mockMvc.perform(get("/api/public/merchants/{id}/available-slots", 999)
                    .param("date", "2024-01-15")
                    .param("serviceId", "1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isNotFound());

            verify(merchantService).findById(999);
        }

        @Test
        @DisplayName("缺少日期参数")
        void testGetAvailableSlots_MissingDate() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "shop@test.com");

            when(merchantService.findById(1)).thenReturn(merchant);

            var result = mockMvc.perform(get("/api/public/merchants/{id}/available-slots", 1)
                    .param("serviceId", "1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("缺少服务ID参数")
        void testGetAvailableSlots_MissingServiceId() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "shop@test.com");

            when(merchantService.findById(1)).thenReturn(merchant);

            var result = mockMvc.perform(get("/api/public/merchants/{id}/available-slots", 1)
                    .param("date", "2024-01-15")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("待审核商家返回404")
        void testGetAvailableSlots_PendingMerchant() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "待审核商家", "pending@test.com");
            merchant.setStatus("pending");

            when(merchantService.findById(1)).thenReturn(merchant);

            var result = mockMvc.perform(get("/api/public/merchants/{id}/available-slots", 1)
                    .param("date", "2024-01-15")
                    .param("serviceId", "1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isNotFound());

            verify(merchantService).findById(1);
        }
    }

    @Nested
    @DisplayName("商家状态过滤测试")
    class MerchantStatusFilterTests {

        @Test
        @DisplayName("过滤pending状态商家")
        void testFilterPendingMerchant() throws Exception {
            Merchant approved = TestDataFactory.createMerchant(1, "已审核", "approved@test.com");
            approved.setStatus("approved");

            Merchant pending = TestDataFactory.createMerchant(2, "待审核", "pending@test.com");
            pending.setStatus("pending");

            when(merchantService.findAll()).thenReturn(Arrays.asList(approved, pending));

            var result = mockMvc.perform(get("/api/public/merchants")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].status").value("approved"));
        }

        @Test
        @DisplayName("过滤rejected状态商家")
        void testFilterRejectedMerchant() throws Exception {
            Merchant approved = TestDataFactory.createMerchant(1, "已审核", "approved@test.com");
            approved.setStatus("approved");

            Merchant rejected = TestDataFactory.createMerchant(2, "已拒绝", "rejected@test.com");
            rejected.setStatus("rejected");

            when(merchantService.findAll()).thenReturn(Arrays.asList(approved, rejected));

            var result = mockMvc.perform(get("/api/public/merchants")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].status").value("approved"));
        }

        @Test
        @DisplayName("混合状态只返回approved")
        void testMixedStatusOnlyApproved() throws Exception {
            Merchant approved1 = TestDataFactory.createMerchant(1, "已审核1", "approved1@test.com");
            approved1.setStatus("approved");

            Merchant pending = TestDataFactory.createMerchant(2, "待审核", "pending@test.com");
            pending.setStatus("pending");

            Merchant approved2 = TestDataFactory.createMerchant(3, "已审核2", "approved2@test.com");
            approved2.setStatus("approved");

            Merchant rejected = TestDataFactory.createMerchant(4, "已拒绝", "rejected@test.com");
            rejected.setStatus("rejected");

            when(merchantService.findAll()).thenReturn(Arrays.asList(approved1, pending, approved2, rejected));

            var result = mockMvc.perform(get("/api/public/merchants")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }
    }

    @Nested
    @DisplayName("商家信息完整性测试")
    class MerchantDataIntegrityTests {

        @Test
        @DisplayName("商家包含完整信息")
        void testMerchantCompleteInfo() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "shop@test.com");
            merchant.setAddress("北京市朝阳区xxx街道123号");
            merchant.setPhone("13800138000");
            merchant.setContactPerson("张经理");
            merchant.setLogo("http://example.com/logo.png");

            when(merchantService.findById(1)).thenReturn(merchant);

            var result = mockMvc.perform(get("/api/public/merchants/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("宠物乐园"))
                    .andExpect(jsonPath("$.email").value("shop@test.com"))
                    .andExpect(jsonPath("$.address").value("北京市朝阳区xxx街道123号"))
                    .andExpect(jsonPath("$.phone").value("13800138000"))
                    .andExpect(jsonPath("$.contactPerson").value("张经理"))
                    .andExpect(jsonPath("$.logo").value("http://example.com/logo.png"));
        }

        @Test
        @DisplayName("服务包含完整信息")
        void testServiceCompleteInfo() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "shop@test.com");
            Service service = TestDataFactory.createService(1, merchant, "宠物洗澡", new BigDecimal("99.00"));
            service.setDescription("专业宠物洗澡服务，包含洗澡、吹干、梳毛");
            service.setDuration(60);
            service.setImage("http://example.com/service.png");

            when(merchantService.findById(1)).thenReturn(merchant);
            when(serviceService.findByMerchantId(1)).thenReturn(Arrays.asList(service));

            var result = mockMvc.perform(get("/api/public/merchants/{id}/services", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("宠物洗澡"))
                    .andExpect(jsonPath("$[0].price").value(99.00))
                    .andExpect(jsonPath("$[0].duration").value(60))
                    .andExpect(jsonPath("$[0].description").value("专业宠物洗澡服务，包含洗澡、吹干、梳毛"));
        }

        @Test
        @DisplayName("商品包含完整信息")
        void testProductCompleteInfo() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "shop@test.com");
            Product product = TestDataFactory.createProduct(1, merchant, "优质狗粮", new BigDecimal("199.00"), 100);
            product.setDescription("进口优质狗粮，营养均衡");
            product.setImage("http://example.com/product.png");

            when(merchantService.findById(1)).thenReturn(merchant);
            when(productService.findByMerchantId(1)).thenReturn(Arrays.asList(product));

            var result = mockMvc.perform(get("/api/public/merchants/{id}/products", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("优质狗粮"))
                    .andExpect(jsonPath("$[0].price").value(199.00))
                    .andExpect(jsonPath("$[0].stock").value(100))
                    .andExpect(jsonPath("$[0].description").value("进口优质狗粮，营养均衡"));
        }

        @Test
        @DisplayName("评价包含完整信息")
        void testReviewCompleteInfo() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "shop@test.com");
            Review review = TestDataFactory.createReview(1, merchant, 5, "服务非常好，宠物很喜欢！");
            review.setCreatedAt(LocalDateTime.now());

            when(merchantService.findById(1)).thenReturn(merchant);
            when(reviewService.findByMerchantId(1)).thenReturn(Arrays.asList(review));

            var result = mockMvc.perform(get("/api/public/merchants/{id}/reviews", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].rating").value(5))
                    .andExpect(jsonPath("$[0].comment").value("服务非常好，宠物很喜欢！"));
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("无效商家ID格式")
        void testInvalidMerchantIdFormat() throws Exception {
            var result = mockMvc.perform(get("/api/public/merchants/{id}/services", "abc")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("负数商家ID")
        void testNegativeMerchantId() throws Exception {
            when(merchantService.findById(-1)).thenReturn(null);

            var result = mockMvc.perform(get("/api/public/merchants/{id}", -1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("零商家ID")
        void testZeroMerchantId() throws Exception {
            when(merchantService.findById(0)).thenReturn(null);

            var result = mockMvc.perform(get("/api/public/merchants/{id}", 0)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("超大商家ID")
        void testLargeMerchantId() throws Exception {
            when(merchantService.findById(Integer.MAX_VALUE)).thenReturn(null);

            var result = mockMvc.perform(get("/api/public/merchants/{id}", Integer.MAX_VALUE)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("并发和性能测试")
    class ConcurrencyTests {

        @Test
        @DisplayName("多次请求同一商家")
        void testMultipleRequestsSameMerchant() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "shop@test.com");

            when(merchantService.findById(1)).thenReturn(merchant);

            for (int i = 0; i < 5; i++) {
                var result = mockMvc.perform(get("/api/public/merchants/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print());

                result.andExpect(status().isOk())
                        .andExpect(jsonPath("$.name").value("宠物乐园"));
            }

            verify(merchantService, org.mockito.Mockito.times(5)).findById(1);
        }

        @Test
        @DisplayName("同时请求多个不同商家")
        void testConcurrentDifferentMerchants() throws Exception {
            Merchant merchant1 = TestDataFactory.createMerchant(1, "商家1", "shop1@test.com");
            Merchant merchant2 = TestDataFactory.createMerchant(2, "商家2", "shop2@test.com");
            Merchant merchant3 = TestDataFactory.createMerchant(3, "商家3", "shop3@test.com");

            when(merchantService.findById(1)).thenReturn(merchant1);
            when(merchantService.findById(2)).thenReturn(merchant2);
            when(merchantService.findById(3)).thenReturn(merchant3);

            var result1 = mockMvc.perform(get("/api/public/merchants/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON));
            var result2 = mockMvc.perform(get("/api/public/merchants/{id}", 2)
                    .contentType(MediaType.APPLICATION_JSON));
            var result3 = mockMvc.perform(get("/api/public/merchants/{id}", 3)
                    .contentType(MediaType.APPLICATION_JSON));

            result1.andExpect(status().isOk()).andExpect(jsonPath("$.name").value("商家1"));
            result2.andExpect(status().isOk()).andExpect(jsonPath("$.name").value("商家2"));
            result3.andExpect(status().isOk()).andExpect(jsonPath("$.name").value("商家3"));
        }
    }
}
