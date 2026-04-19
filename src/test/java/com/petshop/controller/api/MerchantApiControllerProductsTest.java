package com.petshop.controller.api;

import com.petshop.entity.Merchant;
import com.petshop.entity.Product;
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

@DisplayName("商家商品API测试")
public class MerchantApiControllerProductsTest extends MerchantApiControllerTestBase {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        MerchantApiController controller = new MerchantApiController();
        injectDependencies(controller);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private void injectDependencies(MerchantApiController controller) {
        try {
            var productServiceField = MerchantApiController.class.getDeclaredField("productService");
            productServiceField.setAccessible(true);
            productServiceField.set(controller, productService);

            var merchantServiceField = MerchantApiController.class.getDeclaredField("merchantService");
            merchantServiceField.setAccessible(true);
            merchantServiceField.set(controller, merchantService);
        } catch (Exception e) {
            throw new RuntimeException("注入依赖失败", e);
        }
    }

    @Nested
    @DisplayName("获取商品列表测试")
    class GetProductsTests {

        @Test
        @DisplayName("成功获取商品列表")
        void testGetProducts_Success() throws Exception {
            Product product1 = TestDataFactory.createProduct(1, testMerchant);
            Product product2 = TestDataFactory.createProduct(2, testMerchant);
            List<Product> products = Arrays.asList(product1, product2);

            when(productService.findByMerchantId(testMerchantId)).thenReturn(products);

            var result = performGet("/api/merchant/products");

            assertSuccess(result);
            assertListResponse(result, 2);
            verify(productService).findByMerchantId(testMerchantId);
        }

        @Test
        @DisplayName("获取空商品列表")
        void testGetProducts_EmptyList() throws Exception {
            when(productService.findByMerchantId(testMerchantId)).thenReturn(Collections.emptyList());

            var result = performGet("/api/merchant/products");

            assertSuccess(result);
            assertListResponse(result, 0);
            verify(productService).findByMerchantId(testMerchantId);
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetProducts_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/merchant/products")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问"));

            verify(productService, never()).findByMerchantId(any());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetProducts_ServiceException() throws Exception {
            when(productService.findByMerchantId(testMerchantId))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            var result = performGet("/api/merchant/products");

            result.andExpect(status().isInternalServerError());
            verify(productService).findByMerchantId(testMerchantId);
        }
    }

    @Nested
    @DisplayName("添加商品测试")
    class AddProductTests {

        @Test
        @DisplayName("成功添加商品")
        void testAddProduct_Success() throws Exception {
            Product newProduct = new Product();
            newProduct.setName("新商品");
            newProduct.setDescription("新商品描述");
            newProduct.setPrice(new BigDecimal("299.99"));
            newProduct.setStock(50);

            Product savedProduct = TestDataFactory.createProduct(1, testMerchant);
            savedProduct.setName("新商品");
            savedProduct.setPrice(new BigDecimal("299.99"));
            savedProduct.setStock(50);

            when(productService.create(any(Product.class))).thenReturn(savedProduct);

            var result = performPost("/api/merchant/products", newProduct);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.name").value("新商品"))
                    .andExpect(jsonPath("$.data.price").value(299.99))
                    .andExpect(jsonPath("$.data.stock").value(50));
            verify(productService).create(any(Product.class));
        }

        @Test
        @DisplayName("添加商品时自动设置默认状态")
        void testAddProduct_DefaultStatus() throws Exception {
            Product newProduct = new Product();
            newProduct.setName("测试商品");
            newProduct.setPrice(new BigDecimal("100.00"));
            newProduct.setStock(10);

            when(productService.create(any(Product.class))).thenAnswer(invocation -> {
                Product p = invocation.getArgument(0);
                if (p.getStatus() == null) {
                    p.setStatus("enabled");
                }
                p.setId(1);
                return p;
            });

            var result = performPost("/api/merchant/products", newProduct);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.status").value("enabled"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testAddProduct_Unauthorized() throws Exception {
            Product newProduct = new Product();
            newProduct.setName("新商品");
            newProduct.setPrice(new BigDecimal("100.00"));

            mockMvc.perform(post("/api/merchant/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(newProduct)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(productService, never()).create(any());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testAddProduct_ServiceException() throws Exception {
            Product newProduct = new Product();
            newProduct.setName("新商品");
            newProduct.setPrice(new BigDecimal("100.00"));
            newProduct.setStock(10);

            when(productService.create(any(Product.class)))
                    .thenThrow(new RuntimeException("保存失败"));

            var result = performPost("/api/merchant/products", newProduct);

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("获取商品详情测试")
    class GetProductByIdTests {

        @Test
        @DisplayName("成功获取商品详情")
        void testGetProductById_Success() throws Exception {
            Product product = TestDataFactory.createProduct(1, testMerchant);

            when(productService.findById(1)).thenReturn(product);

            var result = performGet("/api/merchant/products/{id}", 1);

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.name").value(product.getName()));
            verify(productService).findById(1);
        }

        @Test
        @DisplayName("商品不存在返回404")
        void testGetProductById_NotFound() throws Exception {
            when(productService.findById(999)).thenReturn(null);

            var result = performGet("/api/merchant/products/{id}", 999);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("商品不存在"));
            verify(productService).findById(999);
        }

        @Test
        @DisplayName("无权限访问其他商家商品返回404")
        void testGetProductById_Forbidden() throws Exception {
            Merchant otherMerchant = TestDataFactory.createMerchant(2);
            Product otherProduct = TestDataFactory.createProduct(1, otherMerchant);

            when(productService.findById(1)).thenReturn(otherProduct);

            var result = performGet("/api/merchant/products/{id}", 1);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("商品不存在"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetProductById_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/merchant/products/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(productService, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("更新商品测试")
    class UpdateProductTests {

        @Test
        @DisplayName("成功更新商品")
        void testUpdateProduct_Success() throws Exception {
            Product existingProduct = TestDataFactory.createProduct(1, testMerchant);

            Product updateData = new Product();
            updateData.setName("更新后的商品名称");
            updateData.setDescription("更新后的描述");
            updateData.setPrice(new BigDecimal("399.99"));
            updateData.setStock(80);

            Product updatedProduct = TestDataFactory.createProduct(1, testMerchant);
            updatedProduct.setName("更新后的商品名称");
            updatedProduct.setPrice(new BigDecimal("399.99"));
            updatedProduct.setStock(80);

            when(productService.findById(1)).thenReturn(existingProduct);
            when(productService.update(any(Product.class))).thenReturn(updatedProduct);

            var result = performPut("/api/merchant/products/{id}", updateData, 1);

            assertSuccess(result, "商品更新成功");
            result.andExpect(jsonPath("$.data.name").value("更新后的商品名称"));
            verify(productService).update(any(Product.class));
        }

        @Test
        @DisplayName("更新不存在的商品返回404")
        void testUpdateProduct_NotFound() throws Exception {
            Product updateData = new Product();
            updateData.setName("更新后的商品名称");
            updateData.setPrice(new BigDecimal("100.00"));

            when(productService.findById(999)).thenReturn(null);

            var result = performPut("/api/merchant/products/{id}", updateData, 999);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404));
            verify(productService, never()).update(any());
        }

        @Test
        @DisplayName("无权限更新其他商家商品返回404")
        void testUpdateProduct_Forbidden() throws Exception {
            Merchant otherMerchant = TestDataFactory.createMerchant(2);
            Product otherProduct = TestDataFactory.createProduct(1, otherMerchant);

            Product updateData = new Product();
            updateData.setName("更新后的商品名称");
            updateData.setPrice(new BigDecimal("100.00"));

            when(productService.findById(1)).thenReturn(otherProduct);

            var result = performPut("/api/merchant/products/{id}", updateData, 1);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404));
            verify(productService, never()).update(any());
        }

        @Test
        @DisplayName("未登录返回401")
        void testUpdateProduct_Unauthorized() throws Exception {
            Product updateData = new Product();
            updateData.setName("更新后的商品名称");

            mockMvc.perform(put("/api/merchant/products/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(updateData)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(productService, never()).update(any());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testUpdateProduct_ServiceException() throws Exception {
            Product existingProduct = TestDataFactory.createProduct(1, testMerchant);

            Product updateData = new Product();
            updateData.setName("更新后的商品名称");
            updateData.setPrice(new BigDecimal("100.00"));

            when(productService.findById(1)).thenReturn(existingProduct);
            when(productService.update(any(Product.class)))
                    .thenThrow(new RuntimeException("更新失败"));

            var result = performPut("/api/merchant/products/{id}", updateData, 1);

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("删除商品测试")
    class DeleteProductTests {

        @Test
        @DisplayName("成功删除商品")
        void testDeleteProduct_Success() throws Exception {
            Product product = TestDataFactory.createProduct(1, testMerchant);

            when(productService.findById(1)).thenReturn(product);
            doNothing().when(productService).delete(1);

            var result = performDelete("/api/merchant/products/{id}", 1);

            assertSuccess(result, "商品删除成功");
            verify(productService).delete(1);
        }

        @Test
        @DisplayName("删除不存在的商品返回404")
        void testDeleteProduct_NotFound() throws Exception {
            when(productService.findById(999)).thenReturn(null);

            var result = performDelete("/api/merchant/products/{id}", 999);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404));
            verify(productService, never()).delete(any());
        }

        @Test
        @DisplayName("无权限删除其他商家商品返回404")
        void testDeleteProduct_Forbidden() throws Exception {
            Merchant otherMerchant = TestDataFactory.createMerchant(2);
            Product otherProduct = TestDataFactory.createProduct(1, otherMerchant);

            when(productService.findById(1)).thenReturn(otherProduct);

            var result = performDelete("/api/merchant/products/{id}", 1);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404));
            verify(productService, never()).delete(any());
        }

        @Test
        @DisplayName("未登录返回401")
        void testDeleteProduct_Unauthorized() throws Exception {
            mockMvc.perform(delete("/api/merchant/products/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(productService, never()).delete(any());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testDeleteProduct_ServiceException() throws Exception {
            Product product = TestDataFactory.createProduct(1, testMerchant);

            when(productService.findById(1)).thenReturn(product);
            doThrow(new RuntimeException("删除失败")).when(productService).delete(1);

            var result = performDelete("/api/merchant/products/{id}", 1);

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("分页获取商品测试")
    class GetProductsPagedTests {

        @Test
        @DisplayName("成功分页获取商品")
        void testGetProductsPaged_Success() throws Exception {
            Product product1 = TestDataFactory.createProduct(1, testMerchant);
            Product product2 = TestDataFactory.createProduct(2, testMerchant);
            List<Product> products = Arrays.asList(product1, product2);

            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 2);

            when(productService.searchProducts(eq(testMerchantId), isNull(), isNull(), isNull(), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = performGet("/api/merchant/products/paged");

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.content.length()").value(2))
                    .andExpect(jsonPath("$.data.totalElements").value(2))
                    .andExpect(jsonPath("$.data.totalPages").value(1));
            verify(productService).searchProducts(eq(testMerchantId), isNull(), isNull(), isNull(), any(Pageable.class));
        }

        @Test
        @DisplayName("带筛选条件分页获取商品")
        void testGetProductsPaged_WithFilters() throws Exception {
            Product product = TestDataFactory.createProduct(1, testMerchant);
            product.setStatus("enabled");
            List<Product> products = Collections.singletonList(product);

            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);

            when(productService.searchProducts(eq(testMerchantId), eq("测试"), eq("enabled"), eq("食品"), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = mockMvc.perform(get("/api/merchant/products/paged")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .param("page", "0")
                    .param("size", "10")
                    .param("sortBy", "createdAt")
                    .param("sortDir", "desc")
                    .param("status", "enabled")
                    .param("name", "测试")
                    .param("category", "食品")
                    .contentType(MediaType.APPLICATION_JSON));

            assertPaginatedResponse(result);
            verify(productService).searchProducts(eq(testMerchantId), eq("测试"), eq("enabled"), eq("食品"), any(Pageable.class));
        }

        @Test
        @DisplayName("分页参数边界测试 - 第一页")
        void testGetProductsPaged_FirstPage() throws Exception {
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

            when(productService.searchProducts(eq(testMerchantId), isNull(), isNull(), isNull(), any(Pageable.class)))
                    .thenReturn(emptyPage);

            var result = mockMvc.perform(get("/api/merchant/products/paged")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON));

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.hasPrevious").value(false));
        }

        @Test
        @DisplayName("分页参数边界测试 - 大页面")
        void testGetProductsPaged_LargePageSize() throws Exception {
            Page<Product> productPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 100), 0);

            when(productService.searchProducts(eq(testMerchantId), isNull(), isNull(), isNull(), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = mockMvc.perform(get("/api/merchant/products/paged")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .param("page", "0")
                    .param("size", "100")
                    .contentType(MediaType.APPLICATION_JSON));

            assertPaginatedResponse(result);
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetProductsPaged_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/merchant/products/paged")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(productService, never()).searchProducts(any(), any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("更新商品状态测试")
    class UpdateProductStatusTests {

        @Test
        @DisplayName("成功启用商品")
        void testUpdateProductStatus_Enable() throws Exception {
            Product product = TestDataFactory.createProduct(1, testMerchant);
            product.setStatus("disabled");

            Product enabledProduct = TestDataFactory.createProduct(1, testMerchant);
            enabledProduct.setStatus("enabled");

            when(productService.findById(1)).thenReturn(product);
            when(productService.enableProduct(1)).thenReturn(enabledProduct);

            var result = mockMvc.perform(put("/api/merchant/products/{id}/status", 1)
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .param("status", "enabled")
                    .contentType(MediaType.APPLICATION_JSON));

            assertSuccess(result, "商品状态更新成功");
            result.andExpect(jsonPath("$.data.status").value("enabled"));
            verify(productService).enableProduct(1);
        }

        @Test
        @DisplayName("成功禁用商品")
        void testUpdateProductStatus_Disable() throws Exception {
            Product product = TestDataFactory.createProduct(1, testMerchant);
            product.setStatus("enabled");

            Product disabledProduct = TestDataFactory.createProduct(1, testMerchant);
            disabledProduct.setStatus("disabled");

            when(productService.findById(1)).thenReturn(product);
            when(productService.disableProduct(1)).thenReturn(disabledProduct);

            var result = mockMvc.perform(put("/api/merchant/products/{id}/status", 1)
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .param("status", "disabled")
                    .contentType(MediaType.APPLICATION_JSON));

            assertSuccess(result, "商品状态更新成功");
            result.andExpect(jsonPath("$.data.status").value("disabled"));
            verify(productService).disableProduct(1);
        }

        @Test
        @DisplayName("无效状态值返回400")
        void testUpdateProductStatus_InvalidStatus() throws Exception {
            Product product = TestDataFactory.createProduct(1, testMerchant);

            when(productService.findById(1)).thenReturn(product);

            var result = mockMvc.perform(put("/api/merchant/products/{id}/status", 1)
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .param("status", "invalid")
                    .contentType(MediaType.APPLICATION_JSON));

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("无效的状态值"));
            verify(productService, never()).enableProduct(any());
            verify(productService, never()).disableProduct(any());
        }

        @Test
        @DisplayName("商品不存在返回404")
        void testUpdateProductStatus_NotFound() throws Exception {
            when(productService.findById(999)).thenReturn(null);

            var result = mockMvc.perform(put("/api/merchant/products/{id}/status", 999)
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .param("status", "enabled")
                    .contentType(MediaType.APPLICATION_JSON));

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("无权限操作返回404")
        void testUpdateProductStatus_Forbidden() throws Exception {
            Merchant otherMerchant = TestDataFactory.createMerchant(2);
            Product otherProduct = TestDataFactory.createProduct(1, otherMerchant);

            when(productService.findById(1)).thenReturn(otherProduct);

            var result = mockMvc.perform(put("/api/merchant/products/{id}/status", 1)
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .param("status", "enabled")
                    .contentType(MediaType.APPLICATION_JSON));

            assertNotFound(result);
        }

        @Test
        @DisplayName("未登录返回401")
        void testUpdateProductStatus_Unauthorized() throws Exception {
            mockMvc.perform(put("/api/merchant/products/1/status")
                    .param("status", "enabled")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(productService, never()).enableProduct(any());
            verify(productService, never()).disableProduct(any());
        }
    }

    @Nested
    @DisplayName("批量更新商品状态测试")
    class BatchUpdateProductStatusTests {

        @Test
        @DisplayName("成功批量启用商品")
        void testBatchUpdateProductStatus_Enable() throws Exception {
            Product product1 = TestDataFactory.createProduct(1, testMerchant);
            Product product2 = TestDataFactory.createProduct(2, testMerchant);

            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2));
            request.put("status", "enabled");

            when(productService.findById(1)).thenReturn(product1);
            when(productService.findById(2)).thenReturn(product2);
            when(productService.batchUpdateStatus(Arrays.asList(1, 2), "enabled")).thenReturn(2);

            var result = performPut("/api/merchant/products/batch/status", request);

            assertSuccess(result, "批量更新状态成功");
            result.andExpect(jsonPath("$.data.updatedCount").value(2));
            verify(productService).batchUpdateStatus(Arrays.asList(1, 2), "enabled");
        }

        @Test
        @DisplayName("成功批量禁用商品")
        void testBatchUpdateProductStatus_Disable() throws Exception {
            Product product1 = TestDataFactory.createProduct(1, testMerchant);
            Product product2 = TestDataFactory.createProduct(2, testMerchant);

            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2));
            request.put("status", "disabled");

            when(productService.findById(1)).thenReturn(product1);
            when(productService.findById(2)).thenReturn(product2);
            when(productService.batchUpdateStatus(Arrays.asList(1, 2), "disabled")).thenReturn(2);

            var result = performPut("/api/merchant/products/batch/status", request);

            assertSuccess(result, "批量更新状态成功");
            result.andExpect(jsonPath("$.data.updatedCount").value(2));
        }

        @Test
        @DisplayName("空ID列表返回400")
        void testBatchUpdateProductStatus_EmptyIds() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Collections.emptyList());
            request.put("status", "enabled");

            var result = performPut("/api/merchant/products/batch/status", request);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("商品ID列表不能为空"));
            verify(productService, never()).batchUpdateStatus(any(), any());
        }

        @Test
        @DisplayName("无效状态值返回400")
        void testBatchUpdateProductStatus_InvalidStatus() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2));
            request.put("status", "invalid");

            var result = performPut("/api/merchant/products/batch/status", request);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("无效的状态值"));
        }

        @Test
        @DisplayName("商品不存在返回404")
        void testBatchUpdateProductStatus_ProductNotFound() throws Exception {
            Product product1 = TestDataFactory.createProduct(1, testMerchant);

            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 999));
            request.put("status", "enabled");

            when(productService.findById(1)).thenReturn(product1);
            when(productService.findById(999)).thenReturn(null);

            var result = performPut("/api/merchant/products/batch/status", request);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("商品ID 999 不存在或无权限"));
        }

        @Test
        @DisplayName("无权限操作返回404")
        void testBatchUpdateProductStatus_Forbidden() throws Exception {
            Product product1 = TestDataFactory.createProduct(1, testMerchant);
            Merchant otherMerchant = TestDataFactory.createMerchant(2);
            Product otherProduct = TestDataFactory.createProduct(2, otherMerchant);

            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2));
            request.put("status", "enabled");

            when(productService.findById(1)).thenReturn(product1);
            when(productService.findById(2)).thenReturn(otherProduct);

            var result = performPut("/api/merchant/products/batch/status", request);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("商品ID 2 不存在或无权限"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testBatchUpdateProductStatus_Unauthorized() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2));
            request.put("status", "enabled");

            mockMvc.perform(put("/api/merchant/products/batch/status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(productService, never()).batchUpdateStatus(any(), any());
        }
    }

    @Nested
    @DisplayName("批量删除商品测试")
    class BatchDeleteProductsTests {

        @Test
        @DisplayName("成功批量删除商品")
        void testBatchDeleteProducts_Success() throws Exception {
            Product product1 = TestDataFactory.createProduct(1, testMerchant);
            Product product2 = TestDataFactory.createProduct(2, testMerchant);

            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2));

            when(productService.findById(1)).thenReturn(product1);
            when(productService.findById(2)).thenReturn(product2);
            doNothing().when(productService).batchDelete(Arrays.asList(1, 2));

            var result = mockMvc.perform(delete("/api/merchant/products/batch")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)));

            assertSuccess(result, "批量删除成功");
            result.andExpect(jsonPath("$.data.deletedCount").value(2));
            verify(productService).batchDelete(Arrays.asList(1, 2));
        }

        @Test
        @DisplayName("空ID列表返回400")
        void testBatchDeleteProducts_EmptyIds() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Collections.emptyList());

            var result = mockMvc.perform(delete("/api/merchant/products/batch")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)));

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("商品ID列表不能为空"));
            verify(productService, never()).batchDelete(any());
        }

        @Test
        @DisplayName("商品不存在返回404")
        void testBatchDeleteProducts_ProductNotFound() throws Exception {
            Product product1 = TestDataFactory.createProduct(1, testMerchant);

            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 999));

            when(productService.findById(1)).thenReturn(product1);
            when(productService.findById(999)).thenReturn(null);

            var result = mockMvc.perform(delete("/api/merchant/products/batch")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)));

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("商品ID 999 不存在或无权限"));
        }

        @Test
        @DisplayName("无权限操作返回404")
        void testBatchDeleteProducts_Forbidden() throws Exception {
            Product product1 = TestDataFactory.createProduct(1, testMerchant);
            Merchant otherMerchant = TestDataFactory.createMerchant(2);
            Product otherProduct = TestDataFactory.createProduct(2, otherMerchant);

            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2));

            when(productService.findById(1)).thenReturn(product1);
            when(productService.findById(2)).thenReturn(otherProduct);

            var result = mockMvc.perform(delete("/api/merchant/products/batch")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)));

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("商品ID 2 不存在或无权限"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testBatchDeleteProducts_Unauthorized() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2));

            mockMvc.perform(delete("/api/merchant/products/batch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(productService, never()).batchDelete(any());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testBatchDeleteProducts_ServiceException() throws Exception {
            Product product1 = TestDataFactory.createProduct(1, testMerchant);

            Map<String, Object> request = new HashMap<>();
            request.put("ids", Collections.singletonList(1));

            when(productService.findById(1)).thenReturn(product1);
            doThrow(new RuntimeException("删除失败")).when(productService).batchDelete(any());

            var result = mockMvc.perform(delete("/api/merchant/products/batch")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)));

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("负数价格商品创建")
        void testAddProduct_NegativePrice() throws Exception {
            Product newProduct = new Product();
            newProduct.setName("测试商品");
            newProduct.setPrice(new BigDecimal("-10.00"));
            newProduct.setStock(10);

            when(productService.create(any(Product.class))).thenReturn(newProduct);

            var result = performPost("/api/merchant/products", newProduct);

            assertCreatedResponse(result);
        }

        @Test
        @DisplayName("零价格商品创建")
        void testAddProduct_ZeroPrice() throws Exception {
            Product newProduct = new Product();
            newProduct.setName("免费商品");
            newProduct.setPrice(BigDecimal.ZERO);
            newProduct.setStock(10);

            when(productService.create(any(Product.class))).thenReturn(newProduct);

            var result = performPost("/api/merchant/products", newProduct);

            assertCreatedResponse(result);
        }

        @Test
        @DisplayName("负数库存商品创建")
        void testAddProduct_NegativeStock() throws Exception {
            Product newProduct = new Product();
            newProduct.setName("测试商品");
            newProduct.setPrice(new BigDecimal("100.00"));
            newProduct.setStock(-5);

            when(productService.create(any(Product.class))).thenReturn(newProduct);

            var result = performPost("/api/merchant/products", newProduct);

            assertCreatedResponse(result);
        }

        @Test
        @DisplayName("零库存商品创建")
        void testAddProduct_ZeroStock() throws Exception {
            Product newProduct = new Product();
            newProduct.setName("缺货商品");
            newProduct.setPrice(new BigDecimal("100.00"));
            newProduct.setStock(0);

            when(productService.create(any(Product.class))).thenReturn(newProduct);

            var result = performPost("/api/merchant/products", newProduct);

            assertCreatedResponse(result);
        }

        @Test
        @DisplayName("空商品名称创建")
        void testAddProduct_EmptyName() throws Exception {
            Product newProduct = new Product();
            newProduct.setName("");
            newProduct.setPrice(new BigDecimal("100.00"));
            newProduct.setStock(10);

            when(productService.create(any(Product.class))).thenReturn(newProduct);

            var result = performPost("/api/merchant/products", newProduct);

            assertCreatedResponse(result);
        }

        @Test
        @DisplayName("null商品名称创建")
        void testAddProduct_NullName() throws Exception {
            Product newProduct = new Product();
            newProduct.setName(null);
            newProduct.setPrice(new BigDecimal("100.00"));
            newProduct.setStock(10);

            when(productService.create(any(Product.class))).thenReturn(newProduct);

            var result = performPost("/api/merchant/products", newProduct);

            assertCreatedResponse(result);
        }

        @Test
        @DisplayName("超大库存值")
        void testAddProduct_LargeStock() throws Exception {
            Product newProduct = new Product();
            newProduct.setName("测试商品");
            newProduct.setPrice(new BigDecimal("100.00"));
            newProduct.setStock(Integer.MAX_VALUE);

            when(productService.create(any(Product.class))).thenReturn(newProduct);

            var result = performPost("/api/merchant/products", newProduct);

            assertCreatedResponse(result);
        }

        @Test
        @DisplayName("超高精度价格")
        void testAddProduct_HighPrecisionPrice() throws Exception {
            Product newProduct = new Product();
            newProduct.setName("测试商品");
            newProduct.setPrice(new BigDecimal("99.999999"));
            newProduct.setStock(10);

            when(productService.create(any(Product.class))).thenReturn(newProduct);

            var result = performPost("/api/merchant/products", newProduct);

            assertCreatedResponse(result);
        }
    }
}
