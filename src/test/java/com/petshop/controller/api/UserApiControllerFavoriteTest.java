package com.petshop.controller.api;

import com.petshop.dto.FavoriteDTO;
import com.petshop.dto.FavoriteProductDTO;
import com.petshop.dto.FavoriteServiceDTO;
import com.petshop.entity.Favorite;
import com.petshop.entity.Merchant;
import com.petshop.entity.Product;
import com.petshop.entity.Service;
import com.petshop.entity.User;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("用户收藏API测试")
public class UserApiControllerFavoriteTest extends UserApiControllerTestBase {

    private UserApiController controller;

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        controller = new UserApiController();
        injectMocks();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private void injectMocks() {
        try {
            var userServiceField = UserApiController.class.getDeclaredField("userService");
            userServiceField.setAccessible(true);
            userServiceField.set(controller, userService);

            var petServiceField = UserApiController.class.getDeclaredField("petService");
            petServiceField.setAccessible(true);
            petServiceField.set(controller, petService);

            var appointmentServiceField = UserApiController.class.getDeclaredField("appointmentService");
            appointmentServiceField.setAccessible(true);
            appointmentServiceField.set(controller, appointmentService);

            var serviceServiceField = UserApiController.class.getDeclaredField("serviceService");
            serviceServiceField.setAccessible(true);
            serviceServiceField.set(controller, serviceService);

            var productOrderServiceField = UserApiController.class.getDeclaredField("productOrderService");
            productOrderServiceField.setAccessible(true);
            productOrderServiceField.set(controller, productOrderService);

            var favoriteServiceField = UserApiController.class.getDeclaredField("favoriteService");
            favoriteServiceField.setAccessible(true);
            favoriteServiceField.set(controller, favoriteService);
        } catch (Exception e) {
            throw new RuntimeException("注入mock失败", e);
        }
    }

    @Nested
    @DisplayName("获取收藏商家列表测试")
    class GetFavoriteMerchantsTests {

        @Test
        @DisplayName("成功获取收藏商家列表")
        void testGetFavoriteMerchants_Success() throws Exception {
            mockUserSession();

            List<FavoriteDTO> favorites = createTestFavoriteMerchants(3);
            when(favoriteService.getFavoriteMerchants(testUserId)).thenReturn(favorites);

            var result = performGet("/api/user/favorites");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(3))
                    .andExpect(jsonPath("$.data[0].merchantId").value(1))
                    .andExpect(jsonPath("$.data[0].merchantName").value("测试商家1"))
                    .andExpect(jsonPath("$.data[1].merchantId").value(2))
                    .andExpect(jsonPath("$.data[2].merchantId").value(3));

            verify(favoriteService).getFavoriteMerchants(testUserId);
        }

        @Test
        @DisplayName("空收藏列表")
        void testGetFavoriteMerchants_EmptyList() throws Exception {
            mockUserSession();

            when(favoriteService.getFavoriteMerchants(testUserId)).thenReturn(Collections.emptyList());

            var result = performGet("/api/user/favorites");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetFavoriteMerchants_Unauthorized() throws Exception {
            var result = mockMvc.perform(get("/api/user/favorites")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetFavoriteMerchants_ServiceException() throws Exception {
            mockUserSession();

            when(favoriteService.getFavoriteMerchants(testUserId))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            var result = performGet("/api/user/favorites");

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("添加商家收藏测试")
    class AddMerchantFavoriteTests {

        @Test
        @DisplayName("成功添加商家收藏")
        void testAddMerchantFavorite_Success() throws Exception {
            mockUserSession();

            FavoriteDTO favorite = createTestFavoriteMerchant(1, 1);
            when(favoriteService.addMerchantFavorite(testUserId, 1)).thenReturn(favorite);

            Map<String, Integer> request = new HashMap<>();
            request.put("merchantId", 1);

            var result = performPost("/api/user/favorites", request);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.merchantId").value(1))
                    .andExpect(jsonPath("$.data.merchantName").value("测试商家1"));

            verify(favoriteService).addMerchantFavorite(testUserId, 1);
        }

        @Test
        @DisplayName("重复收藏返回409")
        void testAddMerchantFavorite_Duplicate() throws Exception {
            mockUserSession();

            when(favoriteService.addMerchantFavorite(testUserId, 1))
                    .thenThrow(new RuntimeException("Already favorited this merchant"));

            Map<String, Integer> request = new HashMap<>();
            request.put("merchantId", 1);

            var result = performPost("/api/user/favorites", request);

            result.andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(409));
        }

        @Test
        @DisplayName("商家不存在返回404")
        void testAddMerchantFavorite_MerchantNotFound() throws Exception {
            mockUserSession();

            when(favoriteService.addMerchantFavorite(testUserId, 999))
                    .thenThrow(new RuntimeException("Merchant not found"));

            Map<String, Integer> request = new HashMap<>();
            request.put("merchantId", 999);

            var result = performPost("/api/user/favorites", request);

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("未登录返回401")
        void testAddMerchantFavorite_Unauthorized() throws Exception {
            Map<String, Integer> request = new HashMap<>();
            request.put("merchantId", 1);

            var result = mockMvc.perform(post("/api/user/favorites")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("缺少merchantId返回400")
        void testAddMerchantFavorite_MissingMerchantId() throws Exception {
            mockUserSession();

            Map<String, Object> request = new HashMap<>();

            var result = performPost("/api/user/favorites", request);

            result.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("移除商家收藏测试")
    class RemoveMerchantFavoriteTests {

        @Test
        @DisplayName("成功移除商家收藏")
        void testRemoveMerchantFavorite_Success() throws Exception {
            mockUserSession();

            doNothing().when(favoriteService).removeMerchantFavorite(testUserId, 1);

            var result = performDelete("/api/user/favorites/{id}", 1);

            assertNoContentResponse(result);

            verify(favoriteService).removeMerchantFavorite(testUserId, 1);
        }

        @Test
        @DisplayName("收藏不存在返回404")
        void testRemoveMerchantFavorite_NotFound() throws Exception {
            mockUserSession();

            doThrow(new RuntimeException("Favorite not found"))
                    .when(favoriteService).removeMerchantFavorite(testUserId, 999);

            var result = performDelete("/api/user/favorites/{id}", 999);

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("未登录返回401")
        void testRemoveMerchantFavorite_Unauthorized() throws Exception {
            var result = mockMvc.perform(delete("/api/user/favorites/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("获取收藏服务列表测试")
    class GetFavoriteServicesTests {

        @Test
        @DisplayName("成功获取收藏服务列表")
        void testGetFavoriteServices_Success() throws Exception {
            mockUserSession();

            List<FavoriteServiceDTO> favorites = createTestFavoriteServices(3);
            when(favoriteService.getFavoriteServices(testUserId)).thenReturn(favorites);

            var result = performGet("/api/user/favorites/services");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(3))
                    .andExpect(jsonPath("$.data[0].serviceId").value(1))
                    .andExpect(jsonPath("$.data[0].serviceName").value("测试服务1"))
                    .andExpect(jsonPath("$.data[1].serviceId").value(2))
                    .andExpect(jsonPath("$.data[2].serviceId").value(3));

            verify(favoriteService).getFavoriteServices(testUserId);
        }

        @Test
        @DisplayName("空收藏列表")
        void testGetFavoriteServices_EmptyList() throws Exception {
            mockUserSession();

            when(favoriteService.getFavoriteServices(testUserId)).thenReturn(Collections.emptyList());

            var result = performGet("/api/user/favorites/services");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetFavoriteServices_Unauthorized() throws Exception {
            var result = mockMvc.perform(get("/api/user/favorites/services")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetFavoriteServices_ServiceException() throws Exception {
            mockUserSession();

            when(favoriteService.getFavoriteServices(testUserId))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            var result = performGet("/api/user/favorites/services");

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("添加服务收藏测试")
    class AddServiceFavoriteTests {

        @Test
        @DisplayName("成功添加服务收藏")
        void testAddServiceFavorite_Success() throws Exception {
            mockUserSession();

            FavoriteServiceDTO favorite = createTestFavoriteService(1, 1);
            when(favoriteService.addServiceFavorite(testUserId, 1)).thenReturn(favorite);

            Map<String, Integer> request = new HashMap<>();
            request.put("serviceId", 1);

            var result = performPost("/api/user/favorites/services", request);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.serviceId").value(1))
                    .andExpect(jsonPath("$.data.serviceName").value("测试服务1"));

            verify(favoriteService).addServiceFavorite(testUserId, 1);
        }

        @Test
        @DisplayName("重复收藏返回409")
        void testAddServiceFavorite_Duplicate() throws Exception {
            mockUserSession();

            when(favoriteService.addServiceFavorite(testUserId, 1))
                    .thenThrow(new RuntimeException("Already favorited this service"));

            Map<String, Integer> request = new HashMap<>();
            request.put("serviceId", 1);

            var result = performPost("/api/user/favorites/services", request);

            result.andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(409));
        }

        @Test
        @DisplayName("服务不存在返回404")
        void testAddServiceFavorite_ServiceNotFound() throws Exception {
            mockUserSession();

            when(favoriteService.addServiceFavorite(testUserId, 999))
                    .thenThrow(new RuntimeException("Service not found"));

            Map<String, Integer> request = new HashMap<>();
            request.put("serviceId", 999);

            var result = performPost("/api/user/favorites/services", request);

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("未登录返回401")
        void testAddServiceFavorite_Unauthorized() throws Exception {
            Map<String, Integer> request = new HashMap<>();
            request.put("serviceId", 1);

            var result = mockMvc.perform(post("/api/user/favorites/services")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("缺少serviceId返回400")
        void testAddServiceFavorite_MissingServiceId() throws Exception {
            mockUserSession();

            Map<String, Object> request = new HashMap<>();

            var result = performPost("/api/user/favorites/services", request);

            result.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("移除服务收藏测试")
    class RemoveServiceFavoriteTests {

        @Test
        @DisplayName("成功移除服务收藏")
        void testRemoveServiceFavorite_Success() throws Exception {
            mockUserSession();

            doNothing().when(favoriteService).removeServiceFavorite(testUserId, 1);

            var result = performDelete("/api/user/favorites/services/{id}", 1);

            assertNoContentResponse(result);

            verify(favoriteService).removeServiceFavorite(testUserId, 1);
        }

        @Test
        @DisplayName("收藏不存在返回404")
        void testRemoveServiceFavorite_NotFound() throws Exception {
            mockUserSession();

            doThrow(new RuntimeException("Favorite not found"))
                    .when(favoriteService).removeServiceFavorite(testUserId, 999);

            var result = performDelete("/api/user/favorites/services/{id}", 999);

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("未登录返回401")
        void testRemoveServiceFavorite_Unauthorized() throws Exception {
            var result = mockMvc.perform(delete("/api/user/favorites/services/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("添加商品收藏测试")
    class AddProductFavoriteTests {

        @Test
        @DisplayName("成功添加商品收藏")
        void testAddProductFavorite_Success() throws Exception {
            mockUserSession();

            FavoriteProductDTO favorite = createTestFavoriteProduct(1, 1);
            when(favoriteService.addProductFavorite(testUserId, 1)).thenReturn(favorite);

            Map<String, Integer> request = new HashMap<>();
            request.put("productId", 1);

            var result = performPost("/api/user/favorites/products", request);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.productId").value(1))
                    .andExpect(jsonPath("$.data.productName").value("测试商品1"));

            verify(favoriteService).addProductFavorite(testUserId, 1);
        }

        @Test
        @DisplayName("重复收藏返回409")
        void testAddProductFavorite_Duplicate() throws Exception {
            mockUserSession();

            when(favoriteService.addProductFavorite(testUserId, 1))
                    .thenThrow(new RuntimeException("Already favorited this product"));

            Map<String, Integer> request = new HashMap<>();
            request.put("productId", 1);

            var result = performPost("/api/user/favorites/products", request);

            result.andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(409));
        }

        @Test
        @DisplayName("商品不存在返回404")
        void testAddProductFavorite_ProductNotFound() throws Exception {
            mockUserSession();

            when(favoriteService.addProductFavorite(testUserId, 999))
                    .thenThrow(new RuntimeException("Product not found"));

            Map<String, Integer> request = new HashMap<>();
            request.put("productId", 999);

            var result = performPost("/api/user/favorites/products", request);

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("未登录返回401")
        void testAddProductFavorite_Unauthorized() throws Exception {
            Map<String, Integer> request = new HashMap<>();
            request.put("productId", 1);

            var result = mockMvc.perform(post("/api/user/favorites/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("缺少productId返回400")
        void testAddProductFavorite_MissingProductId() throws Exception {
            mockUserSession();

            Map<String, Object> request = new HashMap<>();

            var result = performPost("/api/user/favorites/products", request);

            result.andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("移除商品收藏测试")
    class RemoveProductFavoriteTests {

        @Test
        @DisplayName("成功移除商品收藏")
        void testRemoveProductFavorite_Success() throws Exception {
            mockUserSession();

            doNothing().when(favoriteService).removeProductFavorite(testUserId, 1);

            var result = performDelete("/api/user/favorites/products/{id}", 1);

            assertNoContentResponse(result);

            verify(favoriteService).removeProductFavorite(testUserId, 1);
        }

        @Test
        @DisplayName("收藏不存在返回404")
        void testRemoveProductFavorite_NotFound() throws Exception {
            mockUserSession();

            doThrow(new RuntimeException("Favorite not found"))
                    .when(favoriteService).removeProductFavorite(testUserId, 999);

            var result = performDelete("/api/user/favorites/products/{id}", 999);

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("未登录返回401")
        void testRemoveProductFavorite_Unauthorized() throws Exception {
            var result = mockMvc.perform(delete("/api/user/favorites/products/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("检查商品收藏状态测试")
    class CheckProductFavoriteStatusTests {

        @Test
        @DisplayName("已收藏返回true")
        void testCheckProductFavoriteStatus_Favorited() throws Exception {
            mockUserSession();

            when(favoriteService.isProductFavorited(testUserId, 1)).thenReturn(true);

            var result = performGet("/api/user/favorites/products/{id}/check", 1);

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.favorited").value(true));

            verify(favoriteService).isProductFavorited(testUserId, 1);
        }

        @Test
        @DisplayName("未收藏返回false")
        void testCheckProductFavoriteStatus_NotFavorited() throws Exception {
            mockUserSession();

            when(favoriteService.isProductFavorited(testUserId, 1)).thenReturn(false);

            var result = performGet("/api/user/favorites/products/{id}/check", 1);

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.favorited").value(false));

            verify(favoriteService).isProductFavorited(testUserId, 1);
        }

        @Test
        @DisplayName("未登录返回401")
        void testCheckProductFavoriteStatus_Unauthorized() throws Exception {
            var result = mockMvc.perform(get("/api/user/favorites/products/1/check")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testCheckProductFavoriteStatus_ServiceException() throws Exception {
            mockUserSession();

            when(favoriteService.isProductFavorited(testUserId, 1))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            var result = performGet("/api/user/favorites/products/{id}/check", 1);

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("大量收藏列表")
        void testGetFavoriteMerchants_LargeList() throws Exception {
            mockUserSession();

            List<FavoriteDTO> favorites = createTestFavoriteMerchants(100);
            when(favoriteService.getFavoriteMerchants(testUserId)).thenReturn(favorites);

            var result = performGet("/api/user/favorites");

            assertSuccess(result);
            result.andExpect(jsonPath("$.data.length()").value(100));
        }

        @Test
        @DisplayName("收藏商家ID边界值")
        void testAddMerchantFavorite_MaxId() throws Exception {
            mockUserSession();

            FavoriteDTO favorite = createTestFavoriteMerchant(Integer.MAX_VALUE, Integer.MAX_VALUE);
            when(favoriteService.addMerchantFavorite(testUserId, Integer.MAX_VALUE)).thenReturn(favorite);

            Map<String, Integer> request = new HashMap<>();
            request.put("merchantId", Integer.MAX_VALUE);

            var result = performPost("/api/user/favorites", request);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.merchantId").value(Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("收藏服务ID边界值")
        void testAddServiceFavorite_MaxId() throws Exception {
            mockUserSession();

            FavoriteServiceDTO favorite = createTestFavoriteService(Integer.MAX_VALUE, Integer.MAX_VALUE);
            when(favoriteService.addServiceFavorite(testUserId, Integer.MAX_VALUE)).thenReturn(favorite);

            Map<String, Integer> request = new HashMap<>();
            request.put("serviceId", Integer.MAX_VALUE);

            var result = performPost("/api/user/favorites/services", request);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.serviceId").value(Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("收藏商品ID边界值")
        void testAddProductFavorite_MaxId() throws Exception {
            mockUserSession();

            FavoriteProductDTO favorite = createTestFavoriteProduct(Integer.MAX_VALUE, Integer.MAX_VALUE);
            when(favoriteService.addProductFavorite(testUserId, Integer.MAX_VALUE)).thenReturn(favorite);

            Map<String, Integer> request = new HashMap<>();
            request.put("productId", Integer.MAX_VALUE);

            var result = performPost("/api/user/favorites/products", request);

            assertCreatedResponse(result);
            result.andExpect(jsonPath("$.data.productId").value(Integer.MAX_VALUE));
        }

        @Test
        @DisplayName("不同用户隔离测试")
        void testGetFavoriteMerchants_UserIsolation() throws Exception {
            mockUserSession(1);

            List<FavoriteDTO> favorites1 = createTestFavoriteMerchants(2);
            when(favoriteService.getFavoriteMerchants(1)).thenReturn(favorites1);

            var result1 = performGet("/api/user/favorites");
            assertSuccess(result1);
            result1.andExpect(jsonPath("$.data.length()").value(2));

            mockUserSession(2);
            List<FavoriteDTO> favorites2 = createTestFavoriteMerchants(3);
            when(favoriteService.getFavoriteMerchants(2)).thenReturn(favorites2);

            var result2 = performGet("/api/user/favorites");
            assertSuccess(result2);
            result2.andExpect(jsonPath("$.data.length()").value(3));

            verify(favoriteService).getFavoriteMerchants(1);
            verify(favoriteService).getFavoriteMerchants(2);
        }
    }

    @Nested
    @DisplayName("收藏状态流转测试")
    class FavoriteStateFlowTests {

        @Test
        @DisplayName("完整收藏流程-商家")
        void testCompleteMerchantFavoriteFlow() throws Exception {
            mockUserSession();

            FavoriteDTO favorite = createTestFavoriteMerchant(1, 1);
            when(favoriteService.addMerchantFavorite(testUserId, 1)).thenReturn(favorite);
            doNothing().when(favoriteService).removeMerchantFavorite(testUserId, 1);

            Map<String, Integer> addRequest = new HashMap<>();
            addRequest.put("merchantId", 1);

            var addResult = performPost("/api/user/favorites", addRequest);
            assertCreatedResponse(addResult);

            var removeResult = performDelete("/api/user/favorites/{id}", 1);
            assertNoContentResponse(removeResult);

            verify(favoriteService).addMerchantFavorite(testUserId, 1);
            verify(favoriteService).removeMerchantFavorite(testUserId, 1);
        }

        @Test
        @DisplayName("完整收藏流程-服务")
        void testCompleteServiceFavoriteFlow() throws Exception {
            mockUserSession();

            FavoriteServiceDTO favorite = createTestFavoriteService(1, 1);
            when(favoriteService.addServiceFavorite(testUserId, 1)).thenReturn(favorite);
            doNothing().when(favoriteService).removeServiceFavorite(testUserId, 1);

            Map<String, Integer> addRequest = new HashMap<>();
            addRequest.put("serviceId", 1);

            var addResult = performPost("/api/user/favorites/services", addRequest);
            assertCreatedResponse(addResult);

            var removeResult = performDelete("/api/user/favorites/services/{id}", 1);
            assertNoContentResponse(removeResult);

            verify(favoriteService).addServiceFavorite(testUserId, 1);
            verify(favoriteService).removeServiceFavorite(testUserId, 1);
        }

        @Test
        @DisplayName("完整收藏流程-商品")
        void testCompleteProductFavoriteFlow() throws Exception {
            mockUserSession();

            FavoriteProductDTO favorite = createTestFavoriteProduct(1, 1);
            when(favoriteService.addProductFavorite(testUserId, 1)).thenReturn(favorite);
            when(favoriteService.isProductFavorited(testUserId, 1)).thenReturn(true);
            doNothing().when(favoriteService).removeProductFavorite(testUserId, 1);

            Map<String, Integer> addRequest = new HashMap<>();
            addRequest.put("productId", 1);

            var addResult = performPost("/api/user/favorites/products", addRequest);
            assertCreatedResponse(addResult);

            var checkResult = performGet("/api/user/favorites/products/{id}/check", 1);
            assertSuccess(checkResult);
            checkResult.andExpect(jsonPath("$.data.favorited").value(true));

            var removeResult = performDelete("/api/user/favorites/products/{id}", 1);
            assertNoContentResponse(removeResult);

            verify(favoriteService).addProductFavorite(testUserId, 1);
            verify(favoriteService).isProductFavorited(testUserId, 1);
            verify(favoriteService).removeProductFavorite(testUserId, 1);
        }
    }

    @Nested
    @DisplayName("并发场景测试")
    class ConcurrencyTests {

        @Test
        @DisplayName("重复添加收藏处理")
        void testDuplicateAddFavorite() throws Exception {
            mockUserSession();

            when(favoriteService.addMerchantFavorite(testUserId, 1))
                    .thenThrow(new RuntimeException("Already favorited this merchant"));

            Map<String, Integer> request = new HashMap<>();
            request.put("merchantId", 1);

            var result1 = performPost("/api/user/favorites", request);
            result1.andExpect(status().isConflict());

            var result2 = performPost("/api/user/favorites", request);
            result2.andExpect(status().isConflict());

            verify(favoriteService, times(2)).addMerchantFavorite(testUserId, 1);
        }

        @Test
        @DisplayName("删除不存在的收藏")
        void testDeleteNonExistentFavorite() throws Exception {
            mockUserSession();

            doThrow(new RuntimeException("Favorite not found"))
                    .when(favoriteService).removeMerchantFavorite(testUserId, 999);

            var result = performDelete("/api/user/favorites/{id}", 999);
            result.andExpect(status().isNotFound());
        }
    }

    private List<FavoriteDTO> createTestFavoriteMerchants(int count) {
        List<FavoriteDTO> favorites = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            favorites.add(createTestFavoriteMerchant(i, i));
        }
        return favorites;
    }

    private FavoriteDTO createTestFavoriteMerchant(Integer id, Integer merchantId) {
        return FavoriteDTO.builder()
                .id(id)
                .userId(testUserId)
                .merchantId(merchantId)
                .merchantName("测试商家" + merchantId)
                .merchantLogo("http://example.com/logo" + merchantId + ".png")
                .merchantAddress("测试地址" + merchantId + "号")
                .merchantPhone("1380013800" + merchantId)
                .createTime(LocalDateTime.now())
                .build();
    }

    private List<FavoriteServiceDTO> createTestFavoriteServices(int count) {
        List<FavoriteServiceDTO> favorites = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            favorites.add(createTestFavoriteService(i, i));
        }
        return favorites;
    }

    private FavoriteServiceDTO createTestFavoriteService(Integer id, Integer serviceId) {
        return FavoriteServiceDTO.builder()
                .id(id)
                .serviceId(serviceId)
                .serviceName("测试服务" + serviceId)
                .serviceImage("http://example.com/service" + serviceId + ".png")
                .servicePrice(new BigDecimal("99.99"))
                .serviceDuration(60)
                .merchantId(1)
                .merchantName("测试商家")
                .createdAt("2024-01-01 12:00:00")
                .build();
    }

    private List<FavoriteProductDTO> createTestFavoriteProducts(int count) {
        List<FavoriteProductDTO> favorites = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            favorites.add(createTestFavoriteProduct(i, i));
        }
        return favorites;
    }

    private FavoriteProductDTO createTestFavoriteProduct(Integer id, Integer productId) {
        return FavoriteProductDTO.builder()
                .id(id)
                .productId(productId)
                .productName("测试商品" + productId)
                .productImage("http://example.com/product" + productId + ".png")
                .productPrice(new BigDecimal("199.99"))
                .merchantId(1)
                .merchantName("测试商家")
                .createdAt("2024-01-01 12:00:00")
                .build();
    }
}
