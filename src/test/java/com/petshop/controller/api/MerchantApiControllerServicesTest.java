package com.petshop.controller.api;

import com.petshop.entity.Merchant;
import com.petshop.entity.Service;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("商家服务API测试")
public class MerchantApiControllerServicesTest extends MerchantApiControllerTestBase {

    private MerchantApiController controller;

    @BeforeEach
    public void setupController() {
        controller = new MerchantApiController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "serviceService", serviceService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("GET /api/merchant/services - 获取服务列表")
    class GetServicesTests {

        @Test
        @DisplayName("成功获取服务列表")
        void testGetServices_Success() throws Exception {
            Service service1 = TestDataFactory.createService(1, testMerchant, "宠物洗澡", new BigDecimal("99.00"));
            Service service2 = TestDataFactory.createService(2, testMerchant, "宠物美容", new BigDecimal("199.00"));
            List<Service> services = Arrays.asList(service1, service2);

            when(serviceService.findByMerchantId(testMerchantId)).thenReturn(services);

            var result = performGet("/api/merchant/services");

            assertSuccess(result);
            assertListResponse(result, 2);
            verify(serviceService).findByMerchantId(testMerchantId);
        }

        @Test
        @DisplayName("成功获取空服务列表")
        void testGetServices_EmptyList() throws Exception {
            when(serviceService.findByMerchantId(testMerchantId)).thenReturn(Collections.emptyList());

            var result = performGet("/api/merchant/services");

            assertSuccess(result);
            assertListResponse(result, 0);
            verify(serviceService).findByMerchantId(testMerchantId);
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetServices_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/merchant/services")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));

            verify(serviceService, never()).findByMerchantId(anyInt());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetServices_ServiceError() throws Exception {
            when(serviceService.findByMerchantId(testMerchantId))
                    .thenThrow(new RuntimeException("数据库连接失败"));

            var result = performGet("/api/merchant/services");

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("获取服务列表失败：数据库连接失败"));
        }
    }

    @Nested
    @DisplayName("POST /api/merchant/services - 添加服务")
    class AddServiceTests {

        @Test
        @DisplayName("成功添加服务")
        void testAddService_Success() throws Exception {
            Service newService = new Service();
            newService.setName("宠物寄养");
            newService.setDescription("专业宠物寄养服务");
            newService.setPrice(new BigDecimal("299.00"));
            newService.setDuration(120);

            Service savedService = TestDataFactory.createService(1, testMerchant, "宠物寄养", new BigDecimal("299.00"));
            savedService.setDescription("专业宠物寄养服务");
            savedService.setDuration(120);

            when(serviceService.create(any(Service.class))).thenReturn(savedService);

            var result = performPost("/api/merchant/services", newService);

            result.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("服务添加成功"))
                    .andExpect(jsonPath("$.data.name").value("宠物寄养"))
                    .andExpect(jsonPath("$.data.price").value(299.00));

            verify(serviceService).create(any(Service.class));
        }

        @Test
        @DisplayName("未登录返回401")
        void testAddService_Unauthorized() throws Exception {
            Service newService = new Service();
            newService.setName("宠物寄养");
            newService.setPrice(new BigDecimal("299.00"));
            newService.setDuration(60);

            mockMvc.perform(post("/api/merchant/services")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(newService)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(serviceService, never()).create(any(Service.class));
        }

        @Test
        @DisplayName("空服务名称返回400")
        void testAddService_EmptyName() throws Exception {
            Service newService = new Service();
            newService.setName("");
            newService.setPrice(new BigDecimal("299.00"));
            newService.setDuration(60);

            var result = performPost("/api/merchant/services", newService);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("服务名称不能为空"));

            verify(serviceService, never()).create(any(Service.class));
        }

        @Test
        @DisplayName("null服务名称返回400")
        void testAddService_NullName() throws Exception {
            Service newService = new Service();
            newService.setName(null);
            newService.setPrice(new BigDecimal("299.00"));
            newService.setDuration(60);

            var result = performPost("/api/merchant/services", newService);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("服务名称不能为空"));

            verify(serviceService, never()).create(any(Service.class));
        }

        @Test
        @DisplayName("纯空格服务名称返回400")
        void testAddService_BlankName() throws Exception {
            Service newService = new Service();
            newService.setName("   ");
            newService.setPrice(new BigDecimal("299.00"));
            newService.setDuration(60);

            var result = performPost("/api/merchant/services", newService);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("服务名称不能为空"));

            verify(serviceService, never()).create(any(Service.class));
        }

        @Test
        @DisplayName("负数价格返回400")
        void testAddService_NegativePrice() throws Exception {
            Service newService = new Service();
            newService.setName("宠物寄养");
            newService.setPrice(new BigDecimal("-100.00"));
            newService.setDuration(60);

            var result = performPost("/api/merchant/services", newService);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("服务价格无效"));

            verify(serviceService, never()).create(any(Service.class));
        }

        @Test
        @DisplayName("null价格返回400")
        void testAddService_NullPrice() throws Exception {
            Service newService = new Service();
            newService.setName("宠物寄养");
            newService.setPrice(null);
            newService.setDuration(60);

            var result = performPost("/api/merchant/services", newService);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("服务价格无效"));

            verify(serviceService, never()).create(any(Service.class));
        }

        @Test
        @DisplayName("零时长返回400")
        void testAddService_ZeroDuration() throws Exception {
            Service newService = new Service();
            newService.setName("宠物寄养");
            newService.setPrice(new BigDecimal("299.00"));
            newService.setDuration(0);

            var result = performPost("/api/merchant/services", newService);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("服务时长无效"));

            verify(serviceService, never()).create(any(Service.class));
        }

        @Test
        @DisplayName("负数时长返回400")
        void testAddService_NegativeDuration() throws Exception {
            Service newService = new Service();
            newService.setName("宠物寄养");
            newService.setPrice(new BigDecimal("299.00"));
            newService.setDuration(-30);

            var result = performPost("/api/merchant/services", newService);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("服务时长无效"));

            verify(serviceService, never()).create(any(Service.class));
        }

        @Test
        @DisplayName("null时长返回400")
        void testAddService_NullDuration() throws Exception {
            Service newService = new Service();
            newService.setName("宠物寄养");
            newService.setPrice(new BigDecimal("299.00"));
            newService.setDuration(null);

            var result = performPost("/api/merchant/services", newService);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("服务时长无效"));

            verify(serviceService, never()).create(any(Service.class));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testAddService_ServiceError() throws Exception {
            Service newService = new Service();
            newService.setName("宠物寄养");
            newService.setPrice(new BigDecimal("299.00"));
            newService.setDuration(60);

            when(serviceService.create(any(Service.class)))
                    .thenThrow(new RuntimeException("数据库插入失败"));

            var result = performPost("/api/merchant/services", newService);

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("添加服务失败：数据库插入失败"));
        }
    }

    @Nested
    @DisplayName("PUT /api/merchant/services/{id} - 更新服务")
    class UpdateServiceTests {

        @Test
        @DisplayName("成功更新服务")
        void testUpdateService_Success() throws Exception {
            Service existingService = TestDataFactory.createService(1, testMerchant, "宠物洗澡", new BigDecimal("99.00"));
            existingService.setDuration(60);

            Service updateData = new Service();
            updateData.setName("宠物洗澡升级版");
            updateData.setDescription("包含洗澡、吹干、梳毛");
            updateData.setPrice(new BigDecimal("129.00"));
            updateData.setDuration(90);

            Service updatedService = TestDataFactory.createService(1, testMerchant, "宠物洗澡升级版", new BigDecimal("129.00"));
            updatedService.setDescription("包含洗澡、吹干、梳毛");
            updatedService.setDuration(90);

            when(serviceService.findById(1)).thenReturn(existingService);
            when(serviceService.update(any(Service.class))).thenReturn(updatedService);

            var result = performPut("/api/merchant/services/{id}", updateData, 1);

            assertSuccess(result, "服务更新成功");
            result.andExpect(jsonPath("$.data.name").value("宠物洗澡升级版"))
                    .andExpect(jsonPath("$.data.price").value(129.00));

            verify(serviceService).findById(1);
            verify(serviceService).update(any(Service.class));
        }

        @Test
        @DisplayName("未登录返回401")
        void testUpdateService_Unauthorized() throws Exception {
            Service updateData = new Service();
            updateData.setName("宠物洗澡");
            updateData.setPrice(new BigDecimal("99.00"));
            updateData.setDuration(60);

            mockMvc.perform(put("/api/merchant/services/1")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(updateData)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(serviceService, never()).findById(anyInt());
            verify(serviceService, never()).update(any(Service.class));
        }

        @Test
        @DisplayName("服务不存在返回404")
        void testUpdateService_NotFound() throws Exception {
            when(serviceService.findById(999)).thenReturn(null);

            Service updateData = new Service();
            updateData.setName("宠物洗澡");
            updateData.setPrice(new BigDecimal("99.00"));
            updateData.setDuration(60);

            var result = performPut("/api/merchant/services/{id}", updateData, 999);

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("服务不存在"));

            verify(serviceService).findById(999);
            verify(serviceService, never()).update(any(Service.class));
        }

        @Test
        @DisplayName("无权限操作返回403")
        void testUpdateService_Forbidden() throws Exception {
            Merchant otherMerchant = TestDataFactory.createMerchant(2, "其他商家", "other@test.com");
            Service otherService = TestDataFactory.createService(1, otherMerchant, "宠物洗澡", new BigDecimal("99.00"));

            when(serviceService.findById(1)).thenReturn(otherService);

            Service updateData = new Service();
            updateData.setName("宠物洗澡");
            updateData.setPrice(new BigDecimal("99.00"));
            updateData.setDuration(60);

            var result = performPut("/api/merchant/services/{id}", updateData, 1);

            result.andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(403))
                    .andExpect(jsonPath("$.message").value("无权操作此服务"));

            verify(serviceService).findById(1);
            verify(serviceService, never()).update(any(Service.class));
        }

        @Test
        @DisplayName("空服务名称返回400")
        void testUpdateService_EmptyName() throws Exception {
            Service existingService = TestDataFactory.createService(1, testMerchant, "宠物洗澡", new BigDecimal("99.00"));

            when(serviceService.findById(1)).thenReturn(existingService);

            Service updateData = new Service();
            updateData.setName("");
            updateData.setPrice(new BigDecimal("99.00"));
            updateData.setDuration(60);

            var result = performPut("/api/merchant/services/{id}", updateData, 1);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("服务名称不能为空"));

            verify(serviceService, never()).update(any(Service.class));
        }

        @Test
        @DisplayName("负数价格返回400")
        void testUpdateService_NegativePrice() throws Exception {
            Service existingService = TestDataFactory.createService(1, testMerchant, "宠物洗澡", new BigDecimal("99.00"));

            when(serviceService.findById(1)).thenReturn(existingService);

            Service updateData = new Service();
            updateData.setName("宠物洗澡");
            updateData.setPrice(new BigDecimal("-50.00"));
            updateData.setDuration(60);

            var result = performPut("/api/merchant/services/{id}", updateData, 1);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("服务价格无效"));

            verify(serviceService, never()).update(any(Service.class));
        }

        @Test
        @DisplayName("零时长返回400")
        void testUpdateService_ZeroDuration() throws Exception {
            Service existingService = TestDataFactory.createService(1, testMerchant, "宠物洗澡", new BigDecimal("99.00"));

            when(serviceService.findById(1)).thenReturn(existingService);

            Service updateData = new Service();
            updateData.setName("宠物洗澡");
            updateData.setPrice(new BigDecimal("99.00"));
            updateData.setDuration(0);

            var result = performPut("/api/merchant/services/{id}", updateData, 1);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("服务时长无效"));

            verify(serviceService, never()).update(any(Service.class));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testUpdateService_ServiceError() throws Exception {
            Service existingService = TestDataFactory.createService(1, testMerchant, "宠物洗澡", new BigDecimal("99.00"));

            when(serviceService.findById(1)).thenReturn(existingService);
            when(serviceService.update(any(Service.class)))
                    .thenThrow(new RuntimeException("数据库更新失败"));

            Service updateData = new Service();
            updateData.setName("宠物洗澡");
            updateData.setPrice(new BigDecimal("99.00"));
            updateData.setDuration(60);

            var result = performPut("/api/merchant/services/{id}", updateData, 1);

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("更新服务失败：数据库更新失败"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/merchant/services/{id} - 删除服务")
    class DeleteServiceTests {

        @Test
        @DisplayName("成功删除服务")
        void testDeleteService_Success() throws Exception {
            Service existingService = TestDataFactory.createService(1, testMerchant, "宠物洗澡", new BigDecimal("99.00"));

            when(serviceService.findById(1)).thenReturn(existingService);
            doNothing().when(serviceService).delete(1);

            var result = performDelete("/api/merchant/services/{id}", 1);

            assertSuccess(result, "服务删除成功");

            verify(serviceService).findById(1);
            verify(serviceService).delete(1);
        }

        @Test
        @DisplayName("未登录返回401")
        void testDeleteService_Unauthorized() throws Exception {
            mockMvc.perform(delete("/api/merchant/services/1")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(serviceService, never()).findById(anyInt());
            verify(serviceService, never()).delete(anyInt());
        }

        @Test
        @DisplayName("服务不存在返回404")
        void testDeleteService_NotFound() throws Exception {
            when(serviceService.findById(999)).thenReturn(null);

            var result = performDelete("/api/merchant/services/{id}", 999);

            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("服务不存在"));

            verify(serviceService).findById(999);
            verify(serviceService, never()).delete(anyInt());
        }

        @Test
        @DisplayName("无权限操作返回403")
        void testDeleteService_Forbidden() throws Exception {
            Merchant otherMerchant = TestDataFactory.createMerchant(2, "其他商家", "other@test.com");
            Service otherService = TestDataFactory.createService(1, otherMerchant, "宠物洗澡", new BigDecimal("99.00"));

            when(serviceService.findById(1)).thenReturn(otherService);

            var result = performDelete("/api/merchant/services/{id}", 1);

            result.andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(403))
                    .andExpect(jsonPath("$.message").value("无权操作此服务"));

            verify(serviceService).findById(1);
            verify(serviceService, never()).delete(anyInt());
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testDeleteService_ServiceError() throws Exception {
            Service existingService = TestDataFactory.createService(1, testMerchant, "宠物洗澡", new BigDecimal("99.00"));

            when(serviceService.findById(1)).thenReturn(existingService);
            doThrow(new RuntimeException("数据库删除失败")).when(serviceService).delete(1);

            var result = performDelete("/api/merchant/services/{id}", 1);

            result.andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("删除服务失败：数据库删除失败"));
        }
    }

    @Nested
    @DisplayName("PUT /api/merchant/services/batch/status - 批量更新服务状态")
    class BatchUpdateServicesStatusTests {

        @Test
        @DisplayName("成功批量更新服务状态")
        void testBatchUpdateServicesStatus_Success() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));
            request.put("status", "disabled");

            when(serviceService.batchUpdateStatus(anyList(), eq("disabled"), eq(testMerchantId)))
                    .thenReturn(3);

            var result = performPut("/api/merchant/services/batch/status", request);

            assertSuccess(result, "批量更新服务状态成功");
            result.andExpect(jsonPath("$.data.updatedCount").value(3))
                    .andExpect(jsonPath("$.data.requestedCount").value(3));

            verify(serviceService).batchUpdateStatus(anyList(), eq("disabled"), eq(testMerchantId));
        }

        @Test
        @DisplayName("部分更新成功")
        void testBatchUpdateServicesStatus_PartialSuccess() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));
            request.put("status", "enabled");

            when(serviceService.batchUpdateStatus(anyList(), eq("enabled"), eq(testMerchantId)))
                    .thenReturn(2);

            var result = performPut("/api/merchant/services/batch/status", request);

            assertSuccess(result, "批量更新服务状态成功");
            result.andExpect(jsonPath("$.data.updatedCount").value(2))
                    .andExpect(jsonPath("$.data.requestedCount").value(3));
        }

        @Test
        @DisplayName("未找到可更新的服务")
        void testBatchUpdateServicesStatus_NoServicesFound() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));
            request.put("status", "enabled");

            when(serviceService.batchUpdateStatus(anyList(), eq("enabled"), eq(testMerchantId)))
                    .thenReturn(0);

            var result = performPut("/api/merchant/services/batch/status", request);

            assertSuccess(result, "未找到可更新的服务");
            result.andExpect(jsonPath("$.data.updatedCount").value(0));
        }

        @Test
        @DisplayName("未登录返回401")
        void testBatchUpdateServicesStatus_Unauthorized() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));
            request.put("status", "enabled");

            mockMvc.perform(put("/api/merchant/services/batch/status")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));

            verify(serviceService, never()).batchUpdateStatus(anyList(), anyString(), anyInt());
        }

        @Test
        @DisplayName("空ID列表返回400")
        void testBatchUpdateServicesStatus_EmptyIds() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Collections.emptyList());
            request.put("status", "enabled");

            var result = performPut("/api/merchant/services/batch/status", request);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("服务ID列表不能为空"));

            verify(serviceService, never()).batchUpdateStatus(anyList(), anyString(), anyInt());
        }

        @Test
        @DisplayName("null ID列表返回400")
        void testBatchUpdateServicesStatus_NullIds() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", null);
            request.put("status", "enabled");

            var result = performPut("/api/merchant/services/batch/status", request);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("服务ID列表不能为空"));

            verify(serviceService, never()).batchUpdateStatus(anyList(), anyString(), anyInt());
        }

        @Test
        @DisplayName("空状态值返回400")
        void testBatchUpdateServicesStatus_EmptyStatus() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));
            request.put("status", "");

            var result = performPut("/api/merchant/services/batch/status", request);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("状态值不能为空"));

            verify(serviceService, never()).batchUpdateStatus(anyList(), anyString(), anyInt());
        }

        @Test
        @DisplayName("null状态值返回400")
        void testBatchUpdateServicesStatus_NullStatus() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));
            request.put("status", null);

            var result = performPut("/api/merchant/services/batch/status", request);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("状态值不能为空"));

            verify(serviceService, never()).batchUpdateStatus(anyList(), anyString(), anyInt());
        }

        @Test
        @DisplayName("无效状态值返回400")
        void testBatchUpdateServicesStatus_InvalidStatus() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));
            request.put("status", "invalid_status");

            var result = performPut("/api/merchant/services/batch/status", request);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("状态值无效，只能是 enabled 或 disabled"));

            verify(serviceService, never()).batchUpdateStatus(anyList(), anyString(), anyInt());
        }

        @Test
        @DisplayName("纯空格状态值返回400")
        void testBatchUpdateServicesStatus_BlankStatus() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2, 3));
            request.put("status", "   ");

            var result = performPut("/api/merchant/services/batch/status", request);

            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("状态值不能为空"));

            verify(serviceService, never()).batchUpdateStatus(anyList(), anyString(), anyInt());
        }
    }
}
