package com.petshop.controller.api;

import com.petshop.entity.Merchant;
import com.petshop.entity.Product;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
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

@DisplayName("管理员商品管理API测试")
public class AdminApiControllerProductsTest extends AdminApiControllerTestBase {

    private AdminApiController controller;

    @BeforeEach
    public void setupController() {
        controller = new AdminApiController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "productService", productService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("GET /api/admin/products - 获取商品列表")
    class GetProductsTests {

        @Test
        @DisplayName("成功获取商品列表")
        void testGetProducts_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product1 = TestDataFactory.createProduct(1, merchant, "商品1");
            Product product2 = TestDataFactory.createProduct(2, merchant, "商品2");
            List<Product> products = Arrays.asList(product1, product2);
            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 2);

            when(productService.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(productPage);

            var result = performGet("/api/admin/products");

            assertPaginatedResponse(result);
            result.andExpect(jsonPath("$.data.data.length()").value(2));

            verify(productService).findAllByOrderByCreatedAtDesc(any(Pageable.class));
        }

        @Test
        @DisplayName("带搜索条件获取商品列表")
        void testGetProducts_WithSearch() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant, "搜索商品");
            List<Product> products = Collections.singletonList(product);
            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);

            when(productService.adminSearchProducts(eq("搜索"), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = mockMvc.perform(get("/api/admin/products")
                    .sessionAttr("admin", testAdmin)
                    .param("keyword", "搜索")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(res -> {});

            assertPaginatedResponse(result);
            verify(productService).adminSearchProducts(eq("搜索"), any(), any(), any(), any(Pageable.class));
        }

        @Test
        @DisplayName("按状态筛选")
        void testGetProducts_ByStatus() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant, "启用商品");
            product.setStatus("enabled");
            List<Product> products = Collections.singletonList(product);
            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);

            when(productService.adminSearchProducts(any(), eq("enabled"), any(), any(), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = mockMvc.perform(get("/api/admin/products")
                    .sessionAttr("admin", testAdmin)
                    .param("status", "enabled")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(res -> {});

            assertPaginatedResponse(result);
        }

        @Test
        @DisplayName("按商家筛选")
        void testGetProducts_ByMerchant() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant, "商品");
            List<Product> products = Collections.singletonList(product);
            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);

            when(productService.adminSearchProducts(any(), any(), eq(1), any(), any(Pageable.class)))
                    .thenReturn(productPage);

            var result = mockMvc.perform(get("/api/admin/products")
                    .sessionAttr("admin", testAdmin)
                    .param("merchantId", "1")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(res -> {});

            assertPaginatedResponse(result);
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetProducts_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/products")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(productService, never()).findAllByOrderByCreatedAtDesc(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("GET /api/admin/products/{id} - 获取商品详情")
    class GetProductDetailTests {

        @Test
        @DisplayName("成功获取商品详情")
        void testGetProductDetail_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant, "测试商品");

            when(productService.findById(1)).thenReturn(product);

            var result = performGet("/api/admin/products/{id}", 1);

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.name").value("测试商品"));

            verify(productService).findById(1);
        }

        @Test
        @DisplayName("商品不存在返回404")
        void testGetProductDetail_NotFound() throws Exception {
            when(productService.findById(999)).thenReturn(null);

            var result = performGet("/api/admin/products/{id}", 999);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("Product not found"));

            verify(productService).findById(999);
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetProductDetail_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/admin/products/1")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(productService, never()).findById(anyInt());
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/products/{id} - 更新商品")
    class UpdateProductTests {

        @Test
        @DisplayName("成功更新商品")
        void testUpdateProduct_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product existingProduct = TestDataFactory.createProduct(1, merchant, "原商品名");

            Product updateData = new Product();
            updateData.setName("新商品名");
            updateData.setPrice(new BigDecimal("299.00"));
            updateData.setStock(50);

            Product updatedProduct = TestDataFactory.createProduct(1, merchant, "新商品名");
            updatedProduct.setPrice(new BigDecimal("299.00"));
            updatedProduct.setStock(50);

            when(productService.findById(1)).thenReturn(existingProduct);
            when(productService.update(any(Product.class))).thenReturn(updatedProduct);

            var result = performPut("/api/admin/products/{id}", updateData, 1);

            assertSuccess(result, "Product updated successfully");
            result.andExpect(jsonPath("$.data.name").value("新商品名"));

            verify(productService).findById(1);
            verify(productService).update(any(Product.class));
        }

        @Test
        @DisplayName("商品不存在返回404")
        void testUpdateProduct_NotFound() throws Exception {
            when(productService.findById(999)).thenReturn(null);

            Product updateData = new Product();
            updateData.setName("新商品名");

            var result = performPut("/api/admin/products/{id}", updateData, 999);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("Product not found"));

            verify(productService).findById(999);
            verify(productService, never()).update(any(Product.class));
        }

        @Test
        @DisplayName("未登录返回401")
        void testUpdateProduct_Unauthorized() throws Exception {
            Product updateData = new Product();
            updateData.setName("新商品名");

            mockMvc.perform(put("/api/admin/products/1")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(updateData)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(productService, never()).findById(anyInt());
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/products/{id}/status - 更新商品状态")
    class UpdateProductStatusTests {

        @Test
        @DisplayName("成功启用商品")
        void testUpdateProductStatus_Enable() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant, "测试商品");
            product.setStatus("disabled");

            Product updatedProduct = TestDataFactory.createProduct(1, merchant, "测试商品");
            updatedProduct.setStatus("enabled");

            when(productService.findById(1)).thenReturn(product);
            when(productService.update(any(Product.class))).thenReturn(updatedProduct);

            var result = performPutWithParam("/api/admin/products/{id}/status", "status", "enabled", 1);

            assertSuccess(result, "Product status updated");
            result.andExpect(jsonPath("$.data.status").value("enabled"));

            verify(productService).findById(1);
            verify(productService).update(any(Product.class));
        }

        @Test
        @DisplayName("成功禁用商品")
        void testUpdateProductStatus_Disable() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant, "测试商品");
            product.setStatus("enabled");

            Product updatedProduct = TestDataFactory.createProduct(1, merchant, "测试商品");
            updatedProduct.setStatus("disabled");

            when(productService.findById(1)).thenReturn(product);
            when(productService.update(any(Product.class))).thenReturn(updatedProduct);

            var result = performPutWithParam("/api/admin/products/{id}/status", "status", "disabled", 1);

            assertSuccess(result, "Product status updated");
            result.andExpect(jsonPath("$.data.status").value("disabled"));
        }

        @Test
        @DisplayName("商品不存在返回404")
        void testUpdateProductStatus_NotFound() throws Exception {
            when(productService.findById(999)).thenReturn(null);

            var result = performPutWithParam("/api/admin/products/{id}/status", "status", "enabled", 999);

            assertNotFound(result);
            result.andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("Product not found"));
        }

        @Test
        @DisplayName("无效状态返回400")
        void testUpdateProductStatus_InvalidStatus() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant, "测试商品");

            when(productService.findById(1)).thenReturn(product);

            var result = performPutWithParam("/api/admin/products/{id}/status", "status", "invalid", 1);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Invalid status. Must be 'enabled' or 'disabled'"));

            verify(productService, never()).update(any(Product.class));
        }

        @Test
        @DisplayName("未登录返回401")
        void testUpdateProductStatus_Unauthorized() throws Exception {
            mockMvc.perform(put("/api/admin/products/1/status")
                    .param("status", "enabled")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(productService, never()).findById(anyInt());
        }
    }

    @Nested
    @DisplayName("DELETE /api/admin/products/{id} - 删除商品")
    class DeleteProductTests {

        @Test
        @DisplayName("成功删除商品")
        void testDeleteProduct_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Product product = TestDataFactory.createProduct(1, merchant, "测试商品");

            when(productService.findById(1)).thenReturn(product);
            doNothing().when(productService).delete(1);

            var result = performDelete("/api/admin/products/{id}", 1);

            assertNoContent(result);

            verify(productService).findById(1);
            verify(productService).delete(1);
        }

        @Test
        @DisplayName("商品不存在返回404")
        void testDeleteProduct_NotFound() throws Exception {
            when(productService.findById(999)).thenReturn(null);

            var result = performDelete("/api/admin/products/{id}", 999);

            assertNotFound(result);

            verify(productService).findById(999);
            verify(productService, never()).delete(anyInt());
        }

        @Test
        @DisplayName("未登录返回401")
        void testDeleteProduct_Unauthorized() throws Exception {
            mockMvc.perform(delete("/api/admin/products/1")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(productService, never()).findById(anyInt());
            verify(productService, never()).delete(anyInt());
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/products/batch/status - 批量更新商品状态")
    class BatchUpdateProductStatusTests {

        @Test
        @DisplayName("成功批量更新商品状态")
        void testBatchUpdateProductStatus_Success() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));
            request.put("status", "enabled");

            when(productService.batchUpdateStatus(anyList(), eq("enabled"))).thenReturn(3);

            var result = performPut("/api/admin/products/batch/status", request);

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.updatedCount").value(3));

            verify(productService).batchUpdateStatus(anyList(), eq("enabled"));
        }

        @Test
        @DisplayName("空ID列表返回400")
        void testBatchUpdateProductStatus_EmptyIds() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Collections.emptyList());
            request.put("status", "enabled");

            var result = performPut("/api/admin/products/batch/status", request);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Product IDs cannot be empty"));

            verify(productService, never()).batchUpdateStatus(anyList(), anyString());
        }

        @Test
        @DisplayName("空状态值返回400")
        void testBatchUpdateProductStatus_EmptyStatus() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));
            request.put("status", "");

            var result = performPut("/api/admin/products/batch/status", request);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Status cannot be empty"));
        }

        @Test
        @DisplayName("无效状态值返回400")
        void testBatchUpdateProductStatus_InvalidStatus() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));
            request.put("status", "invalid");

            var result = performPut("/api/admin/products/batch/status", request);

            assertBadRequest(result);
            result.andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Invalid status. Must be 'enabled' or 'disabled'"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testBatchUpdateProductStatus_Unauthorized() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));
            request.put("status", "enabled");

            mockMvc.perform(put("/api/admin/products/batch/status")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(productService, never()).batchUpdateStatus(anyList(), anyString());
        }
    }

    @Nested
    @DisplayName("DELETE /api/admin/products/batch - 批量删除商品")
    class BatchDeleteProductsTests {

        @Test
        @DisplayName("成功批量删除商品")
        void testBatchDeleteProducts_Success() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));

            doNothing().when(productService).batchDelete(anyList());

            var result = mockMvc.perform(delete("/api/admin/products/batch")
                    .sessionAttr("admin", testAdmin)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(res -> {});

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.deletedCount").value(3));

            verify(productService).batchDelete(anyList());
        }

        @Test
        @DisplayName("空ID列表返回400")
        void testBatchDeleteProducts_EmptyIds() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Collections.emptyList());

            var result = mockMvc.perform(delete("/api/admin/products/batch")
                    .sessionAttr("admin", testAdmin)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(res -> {});

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Product IDs cannot be empty"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testBatchDeleteProducts_Unauthorized() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));

            mockMvc.perform(delete("/api/admin/products/batch")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(res -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(productService, never()).batchDelete(anyList());
        }
    }
}
