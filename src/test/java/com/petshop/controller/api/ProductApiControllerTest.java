package com.petshop.controller.api;

import com.petshop.entity.Merchant;
import com.petshop.entity.Product;
import com.petshop.entity.Review;
import com.petshop.exception.GlobalExceptionHandler;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("商品公共API测试")
public class ProductApiControllerTest extends UserApiControllerTestBase {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        ProductApiController controller = new ProductApiController();
        injectDependencies(controller);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private void injectDependencies(ProductApiController controller) {
        try {
            var productServiceField = ProductApiController.class.getDeclaredField("productService");
            productServiceField.setAccessible(true);
            productServiceField.set(controller, productService);

            var reviewServiceField = ProductApiController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(controller, reviewService);
        } catch (Exception e) {
            throw new RuntimeException("注入依赖失败", e);
        }
    }

    @Nested
    @DisplayName("获取商品列表测试")
    class GetProductsTests {

        @Test
        @DisplayName("成功获取商品列表 - 无筛选条件")
        void testGetProducts_Success_NoFilters() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product1 = TestDataFactory.createProduct(1, merchant);
            Product product2 = TestDataFactory.createProduct(2, merchant);
            List<Product> products = Arrays.asList(product1, product2);

            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 2);

            when(productService.searchAllProducts(isNull(), eq("enabled"), isNull(), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = performGetUnauthenticated("/api/products");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[1].id").value(2));
            verify(productService).searchAllProducts(isNull(), eq("enabled"), isNull(), any(Pageable.class));
        }

        @Test
        @DisplayName("成功获取商品列表 - 带分页参数")
        void testGetProducts_Success_WithPagination() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product1 = TestDataFactory.createProduct(1, merchant);
            List<Product> products = Collections.singletonList(product1);

            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(1, 5), 15);

            when(productService.searchAllProducts(isNull(), eq("enabled"), isNull(), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = mockMvc.perform(get("/api/products")
                    .param("page", "1")
                    .param("pageSize", "5")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1));
            verify(productService).searchAllProducts(isNull(), eq("enabled"), isNull(), any(Pageable.class));
        }

        @Test
        @DisplayName("成功获取商品列表 - 按分类筛选")
        void testGetProducts_Success_WithCategory() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setCategory("宠物食品");
            List<Product> products = Collections.singletonList(product);

            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);

            when(productService.searchAllProducts(isNull(), eq("enabled"), eq("宠物食品"), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = mockMvc.perform(get("/api/products")
                    .param("category", "宠物食品")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].category").value("宠物食品"));
            verify(productService).searchAllProducts(isNull(), eq("enabled"), eq("宠物食品"), any(Pageable.class));
        }

        @Test
        @DisplayName("成功获取商品列表 - 按关键字筛选")
        void testGetProducts_Success_WithKeyword() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setName("狗粮");
            List<Product> products = Collections.singletonList(product);

            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);

            when(productService.searchAllProducts(eq("狗粮"), eq("enabled"), isNull(), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = mockMvc.perform(get("/api/products")
                    .param("keyword", "狗粮")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].name").value("狗粮"));
            verify(productService).searchAllProducts(eq("狗粮"), eq("enabled"), isNull(), any(Pageable.class));
        }

        @Test
        @DisplayName("成功获取商品列表 - 带所有筛选条件")
        void testGetProducts_Success_WithAllFilters() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setName("猫粮");
            product.setCategory("宠物食品");
            List<Product> products = Collections.singletonList(product);

            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);

            when(productService.searchAllProducts(eq("猫粮"), eq("enabled"), eq("宠物食品"), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = mockMvc.perform(get("/api/products")
                    .param("keyword", "猫粮")
                    .param("category", "宠物食品")
                    .param("page", "0")
                    .param("pageSize", "10")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1));
            verify(productService).searchAllProducts(eq("猫粮"), eq("enabled"), eq("宠物食品"), any(Pageable.class));
        }

        @Test
        @DisplayName("获取空商品列表")
        void testGetProducts_EmptyList() throws Exception {
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

            when(productService.searchAllProducts(isNull(), eq("enabled"), isNull(), any(Pageable.class)))
                    .thenReturn(emptyPage);

            var result = performGetUnauthenticated("/api/products");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
            verify(productService).searchAllProducts(isNull(), eq("enabled"), isNull(), any(Pageable.class));
        }

        @Test
        @DisplayName("只返回启用状态的商品")
        void testGetProducts_OnlyEnabledProducts() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product enabledProduct = TestDataFactory.createProduct(1, merchant);
            enabledProduct.setStatus("enabled");

            Page<Product> productPage = new PageImpl<>(Collections.singletonList(enabledProduct), PageRequest.of(0, 10), 1);

            when(productService.searchAllProducts(isNull(), eq("enabled"), isNull(), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = performGetUnauthenticated("/api/products");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].status").value("enabled"));
            verify(productService).searchAllProducts(isNull(), eq("enabled"), isNull(), any(Pageable.class));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetProducts_ServiceException() throws Exception {
            when(productService.searchAllProducts(isNull(), eq("enabled"), isNull(), any(Pageable.class)))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            var result = performGetUnauthenticated("/api/products");

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("获取商品详情测试")
    class GetProductByIdTests {

        @Test
        @DisplayName("成功获取商品详情")
        void testGetProductById_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setStatus("enabled");

            when(productService.findById(1)).thenReturn(product);

            var result = performGetUnauthenticated("/api/products/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value(product.getName()))
                    .andExpect(jsonPath("$.price").value(product.getPrice().doubleValue()))
                    .andExpect(jsonPath("$.stock").value(product.getStock()))
                    .andExpect(jsonPath("$.merchantId").value(1));
            verify(productService).findById(1);
        }

        @Test
        @DisplayName("商品不存在返回404")
        void testGetProductById_NotFound() throws Exception {
            when(productService.findById(999)).thenReturn(null);

            var result = performGetUnauthenticated("/api/products/{id}", 999);

            result.andExpect(status().isNotFound());
            verify(productService).findById(999);
        }

        @Test
        @DisplayName("商品已禁用返回404")
        void testGetProductById_DisabledProduct() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setStatus("disabled");

            when(productService.findById(1)).thenReturn(product);

            var result = performGetUnauthenticated("/api/products/{id}", 1);

            result.andExpect(status().isNotFound());
            verify(productService).findById(1);
        }

        @Test
        @DisplayName("验证返回的商品信息完整性")
        void testGetProductById_CompleteProductInfo() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setName("高级狗粮");
            product.setDescription("营养均衡的高级狗粮");
            product.setPrice(new BigDecimal("299.99"));
            product.setStock(50);
            product.setCategory("宠物食品");
            product.setImage("http://example.com/dog-food.jpg");
            product.setStatus("enabled");

            when(productService.findById(1)).thenReturn(product);

            var result = performGetUnauthenticated("/api/products/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("高级狗粮"))
                    .andExpect(jsonPath("$.description").value("营养均衡的高级狗粮"))
                    .andExpect(jsonPath("$.price").value(299.99))
                    .andExpect(jsonPath("$.stock").value(50))
                    .andExpect(jsonPath("$.category").value("宠物食品"))
                    .andExpect(jsonPath("$.image").value("http://example.com/dog-food.jpg"))
                    .andExpect(jsonPath("$.status").value("enabled"))
                    .andExpect(jsonPath("$.merchantId").value(1));
            verify(productService).findById(1);
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetProductById_ServiceException() throws Exception {
            when(productService.findById(1)).thenThrow(new RuntimeException("数据库错误"));

            var result = performGetUnauthenticated("/api/products/{id}", 1);

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("搜索商品测试")
    class SearchProductsTests {

        @Test
        @DisplayName("成功搜索商品")
        void testSearchProducts_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product1 = TestDataFactory.createProduct(1, merchant);
            product1.setName("狗粮");
            Product product2 = TestDataFactory.createProduct(2, merchant);
            product2.setName("猫粮");
            List<Product> products = Arrays.asList(product1, product2);

            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 20), 2);

            when(productService.searchAllProducts(eq("粮"), eq("enabled"), isNull(), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = mockMvc.perform(get("/api/products/search")
                    .param("keyword", "粮")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2));
            verify(productService).searchAllProducts(eq("粮"), eq("enabled"), isNull(), any(Pageable.class));
        }

        @Test
        @DisplayName("搜索结果为空")
        void testSearchProducts_EmptyResult() throws Exception {
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 20), 0);

            when(productService.searchAllProducts(eq("不存在的商品"), eq("enabled"), isNull(), any(Pageable.class)))
                    .thenReturn(emptyPage);

            var result = mockMvc.perform(get("/api/products/search")
                    .param("keyword", "不存在的商品")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
            verify(productService).searchAllProducts(eq("不存在的商品"), eq("enabled"), isNull(), any(Pageable.class));
        }

        @Test
        @DisplayName("搜索只返回启用的商品")
        void testSearchProducts_OnlyEnabledProducts() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setName("测试商品");
            product.setStatus("enabled");

            Page<Product> productPage = new PageImpl<>(Collections.singletonList(product), PageRequest.of(0, 20), 1);

            when(productService.searchAllProducts(eq("测试"), eq("enabled"), isNull(), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = mockMvc.perform(get("/api/products/search")
                    .param("keyword", "测试")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].status").value("enabled"));
            verify(productService).searchAllProducts(eq("测试"), eq("enabled"), isNull(), any(Pageable.class));
        }

        @Test
        @DisplayName("搜索默认返回前20条")
        void testSearchProducts_DefaultPageSize() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            List<Product> products = new ArrayList<>();
            for (int i = 1; i <= 20; i++) {
                Product p = TestDataFactory.createProduct(i, merchant);
                p.setName("商品" + i);
                products.add(p);
            }

            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 20), 25);

            when(productService.searchAllProducts(eq("商品"), eq("enabled"), isNull(), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = mockMvc.perform(get("/api/products/search")
                    .param("keyword", "商品")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(20));
            verify(productService).searchAllProducts(eq("商品"), eq("enabled"), isNull(), any(Pageable.class));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testSearchProducts_ServiceException() throws Exception {
            when(productService.searchAllProducts(anyString(), eq("enabled"), isNull(), any(Pageable.class)))
                    .thenThrow(new RuntimeException("搜索服务异常"));

            var result = mockMvc.perform(get("/api/products/search")
                    .param("keyword", "测试")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("获取商品评价测试")
    class GetProductReviewsTests {

        @Test
        @DisplayName("成功获取商品评价列表")
        void testGetProductReviews_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setStatus("enabled");

            Review review1 = TestDataFactory.createReview(1, merchant);
            Review review2 = TestDataFactory.createReview(2, merchant);
            List<Review> reviews = Arrays.asList(review1, review2);

            when(productService.findById(1)).thenReturn(product);
            when(reviewService.findByServiceId(1)).thenReturn(reviews);

            var result = performGetUnauthenticated("/api/products/{id}/reviews", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2));
            verify(productService).findById(1);
            verify(reviewService).findByServiceId(1);
        }

        @Test
        @DisplayName("获取商品评价 - 空列表")
        void testGetProductReviews_EmptyList() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setStatus("enabled");

            when(productService.findById(1)).thenReturn(product);
            when(reviewService.findByServiceId(1)).thenReturn(Collections.emptyList());

            var result = performGetUnauthenticated("/api/products/{id}/reviews", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
            verify(productService).findById(1);
            verify(reviewService).findByServiceId(1);
        }

        @Test
        @DisplayName("商品不存在返回404")
        void testGetProductReviews_ProductNotFound() throws Exception {
            when(productService.findById(999)).thenReturn(null);

            var result = performGetUnauthenticated("/api/products/{id}/reviews", 999);

            result.andExpect(status().isNotFound());
            verify(productService).findById(999);
            verify(reviewService, never()).findByServiceId(any());
        }

        @Test
        @DisplayName("验证评价信息完整性")
        void testGetProductReviews_CompleteReviewInfo() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setStatus("enabled");

            Review review = TestDataFactory.createReview(1, merchant);
            review.setRating(5);
            review.setComment("非常满意的商品");

            when(productService.findById(1)).thenReturn(product);
            when(reviewService.findByServiceId(1)).thenReturn(Collections.singletonList(review));

            var result = performGetUnauthenticated("/api/products/{id}/reviews", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].rating").value(5))
                    .andExpect(jsonPath("$[0].comment").value("非常满意的商品"));
            verify(productService).findById(1);
            verify(reviewService).findByServiceId(1);
        }

        @Test
        @DisplayName("评价包含用户信息")
        void testGetProductReviews_WithUserInfo() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setStatus("enabled");

            Review review = TestDataFactory.createReview(1, merchant);
            review.getUser().setUsername("测试用户");

            when(productService.findById(1)).thenReturn(product);
            when(reviewService.findByServiceId(1)).thenReturn(Collections.singletonList(review));

            var result = performGetUnauthenticated("/api/products/{id}/reviews", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].userName").value("测试用户"));
            verify(productService).findById(1);
            verify(reviewService).findByServiceId(1);
        }

        @Test
        @DisplayName("评价包含商家和服务信息")
        void testGetProductReviews_WithMerchantAndServiceInfo() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            merchant.setName("测试商家");
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setStatus("enabled");

            Review review = TestDataFactory.createReview(1, merchant);
            review.getService().setName("测试服务");

            when(productService.findById(1)).thenReturn(product);
            when(reviewService.findByServiceId(1)).thenReturn(Collections.singletonList(review));

            var result = performGetUnauthenticated("/api/products/{id}/reviews", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].merchantName").value("测试商家"))
                    .andExpect(jsonPath("$[0].serviceName").value("测试服务"));
            verify(productService).findById(1);
            verify(reviewService).findByServiceId(1);
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetProductReviews_ServiceException() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant);

            when(productService.findById(1)).thenReturn(product);
            when(reviewService.findByServiceId(1)).thenThrow(new RuntimeException("数据库错误"));

            var result = performGetUnauthenticated("/api/products/{id}/reviews", 1);

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("分页边界测试")
    class PaginationBoundaryTests {

        @Test
        @DisplayName("第一页商品列表")
        void testGetProducts_FirstPage() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            List<Product> products = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                products.add(TestDataFactory.createProduct(i, merchant));
            }

            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 25);

            when(productService.searchAllProducts(isNull(), eq("enabled"), isNull(), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = mockMvc.perform(get("/api/products")
                    .param("page", "0")
                    .param("pageSize", "10")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(10));
        }

        @Test
        @DisplayName("最后一页商品列表")
        void testGetProducts_LastPage() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            List<Product> products = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                products.add(TestDataFactory.createProduct(i, merchant));
            }

            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(2, 10), 25);

            when(productService.searchAllProducts(isNull(), eq("enabled"), isNull(), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = mockMvc.perform(get("/api/products")
                    .param("page", "2")
                    .param("pageSize", "10")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(5));
        }

        @Test
        @DisplayName("大页面尺寸")
        void testGetProducts_LargePageSize() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            List<Product> products = new ArrayList<>();
            for (int i = 1; i <= 50; i++) {
                products.add(TestDataFactory.createProduct(i, merchant));
            }

            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 50), 50);

            when(productService.searchAllProducts(isNull(), eq("enabled"), isNull(), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = mockMvc.perform(get("/api/products")
                    .param("page", "0")
                    .param("pageSize", "50")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(50));
        }

        @Test
        @DisplayName("默认分页参数")
        void testGetProducts_DefaultPagination() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            List<Product> products = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                products.add(TestDataFactory.createProduct(i, merchant));
            }

            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 10);

            when(productService.searchAllProducts(isNull(), eq("enabled"), isNull(), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = performGetUnauthenticated("/api/products");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(10));
        }
    }

    @Nested
    @DisplayName("分类筛选测试")
    class CategoryFilterTests {

        @Test
        @DisplayName("按宠物食品分类筛选")
        void testGetProducts_FoodCategory() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setCategory("宠物食品");
            List<Product> products = Collections.singletonList(product);

            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);

            when(productService.searchAllProducts(isNull(), eq("enabled"), eq("宠物食品"), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = mockMvc.perform(get("/api/products")
                    .param("category", "宠物食品")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].category").value("宠物食品"));
        }

        @Test
        @DisplayName("按宠物用品分类筛选")
        void testGetProducts_SuppliesCategory() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setCategory("宠物用品");
            List<Product> products = Collections.singletonList(product);

            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);

            when(productService.searchAllProducts(isNull(), eq("enabled"), eq("宠物用品"), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = mockMvc.perform(get("/api/products")
                    .param("category", "宠物用品")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].category").value("宠物用品"));
        }

        @Test
        @DisplayName("按宠物玩具分类筛选")
        void testGetProducts_ToysCategory() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setCategory("宠物玩具");
            List<Product> products = Collections.singletonList(product);

            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);

            when(productService.searchAllProducts(isNull(), eq("enabled"), eq("宠物玩具"), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = mockMvc.perform(get("/api/products")
                    .param("category", "宠物玩具")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].category").value("宠物玩具"));
        }

        @Test
        @DisplayName("不存在的分类返回空列表")
        void testGetProducts_NonExistentCategory() throws Exception {
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

            when(productService.searchAllProducts(isNull(), eq("enabled"), eq("不存在的分类"), any(Pageable.class)))
                    .thenReturn(emptyPage);

            var result = mockMvc.perform(get("/api/products")
                    .param("category", "不存在的分类")
                    .contentType(MediaType.APPLICATION_JSON));

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("商品数据完整性测试")
    class ProductDataIntegrityTests {

        @Test
        @DisplayName("商品包含商家ID")
        void testProduct_ContainsMerchantId() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(5);
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setStatus("enabled");

            when(productService.findById(1)).thenReturn(product);

            var result = performGetUnauthenticated("/api/products/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.merchantId").value(5));
        }

        @Test
        @DisplayName("商品价格精度正确")
        void testProduct_PricePrecision() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setPrice(new BigDecimal("99.99"));
            product.setStatus("enabled");

            when(productService.findById(1)).thenReturn(product);

            var result = performGetUnauthenticated("/api/products/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.price").value(99.99));
        }

        @Test
        @DisplayName("商品库存数量正确")
        void testProduct_StockQuantity() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setStock(150);
            product.setStatus("enabled");

            when(productService.findById(1)).thenReturn(product);

            var result = performGetUnauthenticated("/api/products/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.stock").value(150));
        }

        @Test
        @DisplayName("商品图片URL正确")
        void testProduct_ImageUrl() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant);
            product.setImage("http://example.com/product-image.jpg");
            product.setStatus("enabled");

            when(productService.findById(1)).thenReturn(product);

            var result = performGetUnauthenticated("/api/products/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.image").value("http://example.com/product-image.jpg"));
        }
    }
}
