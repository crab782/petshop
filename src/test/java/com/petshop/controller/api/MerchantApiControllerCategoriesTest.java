package com.petshop.controller.api;

import com.petshop.entity.Category;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("商家分类API测试")
public class MerchantApiControllerCategoriesTest extends MerchantApiControllerTestBase {

    private MerchantApiController controller;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        controller = new MerchantApiController();
        injectDependencies();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private void injectDependencies() {
        try {
            java.lang.reflect.Field categoryServiceField = MerchantApiController.class.getDeclaredField("categoryService");
            categoryServiceField.setAccessible(true);
            categoryServiceField.set(controller, categoryService);
        } catch (Exception e) {
            throw new RuntimeException("注入依赖失败", e);
        }
    }

    @Nested
    @DisplayName("获取分类列表测试")
    class GetCategoriesTests {

        @Test
        @DisplayName("成功获取分类列表（排序）")
        void testGetCategories_Success() throws Exception {
            mockMerchantSession();
            
            Category category1 = TestDataFactory.createCategory(1, testMerchantId, "分类A");
            category1.setSort(1);
            Category category2 = TestDataFactory.createCategory(2, testMerchantId, "分类B");
            category2.setSort(2);
            List<Category> categories = Arrays.asList(category1, category2);
            
            when(categoryService.findByMerchantIdSorted(testMerchantId)).thenReturn(categories);
            
            mockMvc.perform(get("/api/merchant/categories")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].name").value("分类A"))
                    .andExpect(jsonPath("$.data[1].name").value("分类B"));
            
            verify(categoryService).findByMerchantIdSorted(testMerchantId);
        }

        @Test
        @DisplayName("获取空分类列表")
        void testGetCategories_EmptyList() throws Exception {
            mockMerchantSession();
            
            when(categoryService.findByMerchantIdSorted(testMerchantId)).thenReturn(Collections.emptyList());
            
            mockMvc.perform(get("/api/merchant/categories")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0));
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetCategories_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/merchant/categories")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401))
                    .andExpect(jsonPath("$.message").value("未授权访问，请先登录"));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetCategories_ServiceException() throws Exception {
            mockMerchantSession();
            
            when(categoryService.findByMerchantIdSorted(testMerchantId))
                    .thenThrow(new RuntimeException("数据库连接失败"));
            
            mockMvc.perform(get("/api/merchant/categories")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("获取分类列表失败：数据库连接失败"));
        }
    }

    @Nested
    @DisplayName("添加分类测试")
    class AddCategoryTests {

        @Test
        @DisplayName("成功添加分类")
        void testAddCategory_Success() throws Exception {
            mockMerchantSession();
            
            Category newCategory = new Category();
            newCategory.setName("新分类");
            newCategory.setIcon("http://example.com/icon.png");
            newCategory.setDescription("新分类描述");
            newCategory.setSort(1);
            
            Category savedCategory = TestDataFactory.createCategory(1, testMerchantId, "新分类");
            savedCategory.setIcon("http://example.com/icon.png");
            savedCategory.setDescription("新分类描述");
            savedCategory.setSort(1);
            
            when(categoryService.create(any(Category.class))).thenReturn(savedCategory);
            
            mockMvc.perform(post("/api/merchant/categories")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(newCategory)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("分类添加成功"))
                    .andExpect(jsonPath("$.data.name").value("新分类"))
                    .andExpect(jsonPath("$.data.merchantId").value(testMerchantId));
            
            verify(categoryService).create(any(Category.class));
        }

        @Test
        @DisplayName("未登录返回401")
        void testAddCategory_Unauthorized() throws Exception {
            Category newCategory = new Category();
            newCategory.setName("新分类");
            
            mockMvc.perform(post("/api/merchant/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(newCategory)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("空分类名称返回400")
        void testAddCategory_EmptyName() throws Exception {
            mockMerchantSession();
            
            Category newCategory = new Category();
            newCategory.setName("");
            
            when(categoryService.create(any(Category.class)))
                    .thenThrow(new RuntimeException("分类名称不能为空"));
            
            mockMvc.perform(post("/api/merchant/categories")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(newCategory)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        }

        @Test
        @DisplayName("分类名称重复返回400")
        void testAddCategory_DuplicateName() throws Exception {
            mockMerchantSession();
            
            Category newCategory = new Category();
            newCategory.setName("已存在的分类");
            
            when(categoryService.create(any(Category.class)))
                    .thenThrow(new RuntimeException("分类名称已存在"));
            
            mockMvc.perform(post("/api/merchant/categories")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(newCategory)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("分类名称已存在"));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testAddCategory_ServiceException() throws Exception {
            mockMerchantSession();
            
            Category newCategory = new Category();
            newCategory.setName("新分类");
            
            when(categoryService.create(any(Category.class)))
                    .thenThrow(new RuntimeException("数据库错误"));
            
            mockMvc.perform(post("/api/merchant/categories")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(newCategory)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }

    @Nested
    @DisplayName("更新分类测试")
    class UpdateCategoryTests {

        @Test
        @DisplayName("成功更新分类")
        void testUpdateCategory_Success() throws Exception {
            mockMerchantSession();
            
            Category existingCategory = TestDataFactory.createCategory(1, testMerchantId, "原分类");
            Category updateData = new Category();
            updateData.setName("更新后的分类");
            updateData.setIcon("http://example.com/new-icon.png");
            updateData.setDescription("更新后的描述");
            updateData.setSort(2);
            
            Category updatedCategory = TestDataFactory.createCategory(1, testMerchantId, "更新后的分类");
            updatedCategory.setIcon("http://example.com/new-icon.png");
            updatedCategory.setDescription("更新后的描述");
            updatedCategory.setSort(2);
            
            when(categoryService.findById(1)).thenReturn(existingCategory);
            when(categoryService.update(any(Category.class))).thenReturn(updatedCategory);
            
            mockMvc.perform(put("/api/merchant/categories/{id}", 1)
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(updateData)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("分类更新成功"))
                    .andExpect(jsonPath("$.data.name").value("更新后的分类"));
            
            verify(categoryService).update(any(Category.class));
        }

        @Test
        @DisplayName("未登录返回401")
        void testUpdateCategory_Unauthorized() throws Exception {
            Category updateData = new Category();
            updateData.setName("更新后的分类");
            
            mockMvc.perform(put("/api/merchant/categories/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(updateData)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("分类不存在返回404")
        void testUpdateCategory_NotFound() throws Exception {
            mockMerchantSession();
            
            Category updateData = new Category();
            updateData.setName("更新后的分类");
            
            when(categoryService.findById(999)).thenReturn(null);
            
            mockMvc.perform(put("/api/merchant/categories/{id}", 999)
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(updateData)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("分类不存在"));
        }

        @Test
        @DisplayName("无权限操作返回403")
        void testUpdateCategory_Forbidden() throws Exception {
            mockMerchantSession();
            
            Category otherMerchantCategory = TestDataFactory.createCategory(1, 999, "其他商家的分类");
            Category updateData = new Category();
            updateData.setName("更新后的分类");
            
            when(categoryService.findById(1)).thenReturn(otherMerchantCategory);
            
            mockMvc.perform(put("/api/merchant/categories/{id}", 1)
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(updateData)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(403))
                    .andExpect(jsonPath("$.message").value("无权操作此分类"));
        }

        @Test
        @DisplayName("分类名称重复返回400")
        void testUpdateCategory_DuplicateName() throws Exception {
            mockMerchantSession();
            
            Category existingCategory = TestDataFactory.createCategory(1, testMerchantId, "原分类");
            Category updateData = new Category();
            updateData.setName("已存在的分类");
            
            when(categoryService.findById(1)).thenReturn(existingCategory);
            when(categoryService.update(any(Category.class)))
                    .thenThrow(new RuntimeException("分类名称已存在"));
            
            mockMvc.perform(put("/api/merchant/categories/{id}", 1)
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(updateData)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        }
    }

    @Nested
    @DisplayName("删除分类测试")
    class DeleteCategoryTests {

        @Test
        @DisplayName("成功删除分类")
        void testDeleteCategory_Success() throws Exception {
            mockMerchantSession();
            
            Category category = TestDataFactory.createCategory(1, testMerchantId, "要删除的分类");
            category.setProductCount(0);
            
            when(categoryService.findById(1)).thenReturn(category);
            doNothing().when(categoryService).delete(1);
            
            mockMvc.perform(delete("/api/merchant/categories/{id}", 1)
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("分类删除成功"));
            
            verify(categoryService).delete(1);
        }

        @Test
        @DisplayName("未登录返回401")
        void testDeleteCategory_Unauthorized() throws Exception {
            mockMvc.perform(delete("/api/merchant/categories/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("分类不存在返回404")
        void testDeleteCategory_NotFound() throws Exception {
            mockMerchantSession();
            
            when(categoryService.findById(999)).thenReturn(null);
            
            mockMvc.perform(delete("/api/merchant/categories/{id}", 999)
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("分类不存在"));
        }

        @Test
        @DisplayName("无权限操作返回403")
        void testDeleteCategory_Forbidden() throws Exception {
            mockMerchantSession();
            
            Category otherMerchantCategory = TestDataFactory.createCategory(1, 999, "其他商家的分类");
            
            when(categoryService.findById(1)).thenReturn(otherMerchantCategory);
            
            mockMvc.perform(delete("/api/merchant/categories/{id}", 1)
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(403));
        }

        @Test
        @DisplayName("分类下存在商品时无法删除")
        void testDeleteCategory_HasProducts() throws Exception {
            mockMerchantSession();
            
            Category category = TestDataFactory.createCategory(1, testMerchantId, "有商品的分类");
            category.setProductCount(5);
            
            when(categoryService.findById(1)).thenReturn(category);
            doThrow(new RuntimeException("该分类下存在商品，无法删除")).when(categoryService).delete(1);
            
            mockMvc.perform(delete("/api/merchant/categories/{id}", 1)
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        }
    }

    @Nested
    @DisplayName("更新分类状态测试")
    class UpdateCategoryStatusTests {

        @Test
        @DisplayName("成功更新状态为启用")
        void testUpdateCategoryStatus_EnableSuccess() throws Exception {
            mockMerchantSession();
            
            Category category = TestDataFactory.createCategory(1, testMerchantId, "测试分类");
            category.setStatus("disabled");
            
            Category enabledCategory = TestDataFactory.createCategory(1, testMerchantId, "测试分类");
            enabledCategory.setStatus("enabled");
            
            when(categoryService.findById(1)).thenReturn(category);
            when(categoryService.toggleStatus(1)).thenReturn(enabledCategory);
            
            mockMvc.perform(put("/api/merchant/categories/{id}/status", 1)
                    .param("status", "enabled")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("分类状态更新成功"))
                    .andExpect(jsonPath("$.data.status").value("enabled"));
        }

        @Test
        @DisplayName("成功更新状态为禁用")
        void testUpdateCategoryStatus_DisableSuccess() throws Exception {
            mockMerchantSession();
            
            Category category = TestDataFactory.createCategory(1, testMerchantId, "测试分类");
            category.setStatus("enabled");
            
            Category disabledCategory = TestDataFactory.createCategory(1, testMerchantId, "测试分类");
            disabledCategory.setStatus("disabled");
            
            when(categoryService.findById(1)).thenReturn(category);
            when(categoryService.toggleStatus(1)).thenReturn(disabledCategory);
            
            mockMvc.perform(put("/api/merchant/categories/{id}/status", 1)
                    .param("status", "disabled")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.status").value("disabled"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testUpdateCategoryStatus_Unauthorized() throws Exception {
            mockMvc.perform(put("/api/merchant/categories/{id}/status", 1)
                    .param("status", "enabled")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("分类不存在返回404")
        void testUpdateCategoryStatus_NotFound() throws Exception {
            mockMerchantSession();
            
            when(categoryService.findById(999)).thenReturn(null);
            
            mockMvc.perform(put("/api/merchant/categories/{id}/status", 999)
                    .param("status", "enabled")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }

        @Test
        @DisplayName("无权限操作返回403")
        void testUpdateCategoryStatus_Forbidden() throws Exception {
            mockMerchantSession();
            
            Category otherMerchantCategory = TestDataFactory.createCategory(1, 999, "其他商家的分类");
            
            when(categoryService.findById(1)).thenReturn(otherMerchantCategory);
            
            mockMvc.perform(put("/api/merchant/categories/{id}/status", 1)
                    .param("status", "enabled")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(403));
        }

        @Test
        @DisplayName("无效状态值返回400")
        void testUpdateCategoryStatus_InvalidStatus() throws Exception {
            mockMerchantSession();
            
            Category category = TestDataFactory.createCategory(1, testMerchantId, "测试分类");
            
            when(categoryService.findById(1)).thenReturn(category);
            
            mockMvc.perform(put("/api/merchant/categories/{id}/status", 1)
                    .param("status", "invalid_status")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("无效的状态值，只能是 enabled 或 disabled"));
        }
    }

    @Nested
    @DisplayName("批量更新分类状态测试")
    class BatchUpdateCategoryStatusTests {

        @Test
        @DisplayName("成功批量启用分类")
        void testBatchUpdateCategoryStatus_EnableSuccess() throws Exception {
            mockMerchantSession();
            
            Category category1 = TestDataFactory.createCategory(1, testMerchantId, "分类1");
            Category category2 = TestDataFactory.createCategory(2, testMerchantId, "分类2");
            
            when(categoryService.findById(1)).thenReturn(category1);
            when(categoryService.findById(2)).thenReturn(category2);
            when(categoryService.batchEnable(Arrays.asList(1, 2))).thenReturn(2);
            
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2));
            request.put("status", "enabled");
            
            mockMvc.perform(put("/api/merchant/categories/batch/status")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("批量更新分类状态成功"))
                    .andExpect(jsonPath("$.data.updatedCount").value(2))
                    .andExpect(jsonPath("$.data.ids").isArray());
        }

        @Test
        @DisplayName("成功批量禁用分类")
        void testBatchUpdateCategoryStatus_DisableSuccess() throws Exception {
            mockMerchantSession();
            
            Category category1 = TestDataFactory.createCategory(1, testMerchantId, "分类1");
            Category category2 = TestDataFactory.createCategory(2, testMerchantId, "分类2");
            
            when(categoryService.findById(1)).thenReturn(category1);
            when(categoryService.findById(2)).thenReturn(category2);
            when(categoryService.batchDisable(Arrays.asList(1, 2))).thenReturn(2);
            
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2));
            request.put("status", "disabled");
            
            mockMvc.perform(put("/api/merchant/categories/batch/status")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.status").value("disabled"));
        }

        @Test
        @DisplayName("未登录返回401")
        void testBatchUpdateCategoryStatus_Unauthorized() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2));
            request.put("status", "enabled");
            
            mockMvc.perform(put("/api/merchant/categories/batch/status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("空ID列表返回400")
        void testBatchUpdateCategoryStatus_EmptyIds() throws Exception {
            mockMerchantSession();
            
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Collections.emptyList());
            request.put("status", "enabled");
            
            mockMvc.perform(put("/api/merchant/categories/batch/status")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("分类ID列表不能为空"));
        }

        @Test
        @DisplayName("无效状态值返回400")
        void testBatchUpdateCategoryStatus_InvalidStatus() throws Exception {
            mockMerchantSession();
            
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2));
            request.put("status", "invalid_status");
            
            mockMvc.perform(put("/api/merchant/categories/batch/status")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("无效的状态值，只能是 enabled 或 disabled"));
        }

        @Test
        @DisplayName("分类不存在返回404")
        void testBatchUpdateCategoryStatus_CategoryNotFound() throws Exception {
            mockMerchantSession();
            
            when(categoryService.findById(999)).thenReturn(null);
            
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(999));
            request.put("status", "enabled");
            
            mockMvc.perform(put("/api/merchant/categories/batch/status")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("分类ID 999 不存在"));
        }

        @Test
        @DisplayName("无权限操作返回403")
        void testBatchUpdateCategoryStatus_Forbidden() throws Exception {
            mockMerchantSession();
            
            Category otherMerchantCategory = TestDataFactory.createCategory(1, 999, "其他商家的分类");
            
            when(categoryService.findById(1)).thenReturn(otherMerchantCategory);
            
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1));
            request.put("status", "enabled");
            
            mockMvc.perform(put("/api/merchant/categories/batch/status")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(403));
        }
    }

    @Nested
    @DisplayName("批量删除分类测试")
    class BatchDeleteCategoriesTests {

        @Test
        @DisplayName("成功批量删除分类")
        void testBatchDeleteCategories_Success() throws Exception {
            mockMerchantSession();
            
            Category category1 = TestDataFactory.createCategory(1, testMerchantId, "分类1");
            category1.setProductCount(0);
            Category category2 = TestDataFactory.createCategory(2, testMerchantId, "分类2");
            category2.setProductCount(0);
            
            when(categoryService.findById(1)).thenReturn(category1);
            when(categoryService.findById(2)).thenReturn(category2);
            doNothing().when(categoryService).batchDelete(Arrays.asList(1, 2));
            
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2));
            
            mockMvc.perform(delete("/api/merchant/categories/batch")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("批量删除分类成功"))
                    .andExpect(jsonPath("$.data.deletedCount").value(2))
                    .andExpect(jsonPath("$.data.ids").isArray());
            
            verify(categoryService).batchDelete(Arrays.asList(1, 2));
        }

        @Test
        @DisplayName("未登录返回401")
        void testBatchDeleteCategories_Unauthorized() throws Exception {
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1, 2));
            
            mockMvc.perform(delete("/api/merchant/categories/batch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("空ID列表返回400")
        void testBatchDeleteCategories_EmptyIds() throws Exception {
            mockMerchantSession();
            
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Collections.emptyList());
            
            mockMvc.perform(delete("/api/merchant/categories/batch")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("分类ID列表不能为空"));
        }

        @Test
        @DisplayName("分类不存在返回404")
        void testBatchDeleteCategories_CategoryNotFound() throws Exception {
            mockMerchantSession();
            
            when(categoryService.findById(999)).thenReturn(null);
            
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(999));
            
            mockMvc.perform(delete("/api/merchant/categories/batch")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.message").value("分类ID 999 不存在"));
        }

        @Test
        @DisplayName("无权限操作返回403")
        void testBatchDeleteCategories_Forbidden() throws Exception {
            mockMerchantSession();
            
            Category otherMerchantCategory = TestDataFactory.createCategory(1, 999, "其他商家的分类");
            otherMerchantCategory.setProductCount(0);
            
            when(categoryService.findById(1)).thenReturn(otherMerchantCategory);
            
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1));
            
            mockMvc.perform(delete("/api/merchant/categories/batch")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.code").value(403));
        }

        @Test
        @DisplayName("分类下存在商品时无法删除")
        void testBatchDeleteCategories_HasProducts() throws Exception {
            mockMerchantSession();
            
            Category category = TestDataFactory.createCategory(1, testMerchantId, "有商品的分类");
            category.setProductCount(5);
            
            when(categoryService.findById(1)).thenReturn(category);
            
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1));
            
            mockMvc.perform(delete("/api/merchant/categories/batch")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("分类 有商品的分类 下存在商品，无法删除"));
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testBatchDeleteCategories_ServiceException() throws Exception {
            mockMerchantSession();
            
            Category category = TestDataFactory.createCategory(1, testMerchantId, "测试分类");
            category.setProductCount(0);
            
            when(categoryService.findById(1)).thenReturn(category);
            doThrow(new RuntimeException("数据库错误")).when(categoryService).batchDelete(anyList());
            
            Map<String, Object> request = new HashMap<>();
            request.put("ids", Arrays.asList(1));
            
            mockMvc.perform(delete("/api/merchant/categories/batch")
                    .sessionAttr("merchant", testMerchant)
                    .sessionAttr("merchantId", testMerchantId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value(500));
        }
    }
}
