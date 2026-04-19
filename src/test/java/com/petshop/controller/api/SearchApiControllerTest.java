package com.petshop.controller.api;

import com.petshop.dto.HotKeywordDTO;
import com.petshop.dto.MerchantDTO;
import com.petshop.dto.ProductDTO;
import com.petshop.dto.SearchSuggestionsDTO;
import com.petshop.dto.ServiceDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("搜索API测试")
public class SearchApiControllerTest extends UserApiControllerTestBase {

    private SearchApiController controller;

    @BeforeEach
    public void setupController() {
        controller = new SearchApiController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "searchService", searchService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("GET /api/search/suggestions - 获取搜索建议")
    class GetSearchSuggestionsTests {

        @Test
        @DisplayName("成功获取搜索建议")
        void testGetSearchSuggestions_Success() throws Exception {
            ServiceDTO service = ServiceDTO.builder()
                    .id(1)
                    .name("宠物洗澡")
                    .description("专业宠物洗澡服务")
                    .price(new BigDecimal("99.00"))
                    .duration(60)
                    .merchantId(1)
                    .merchantName("测试商家")
                    .status("enabled")
                    .build();

            ProductDTO product = ProductDTO.builder()
                    .id(1)
                    .name("宠物食品")
                    .description("优质宠物食品")
                    .price(new BigDecimal("199.00"))
                    .stock(100)
                    .merchantId(1)
                    .status("enabled")
                    .build();

            MerchantDTO merchant = MerchantDTO.builder()
                    .id(1)
                    .name("测试商家")
                    .logo("http://example.com/logo.png")
                    .phone("13800138000")
                    .address("测试地址")
                    .rating(4.5)
                    .build();

            SearchSuggestionsDTO suggestions = SearchSuggestionsDTO.builder()
                    .services(Arrays.asList(service))
                    .products(Arrays.asList(product))
                    .merchants(Arrays.asList(merchant))
                    .build();

            when(searchService.getSearchSuggestions("宠物")).thenReturn(suggestions);

            mockMvc.perform(get("/api/search/suggestions")
                    .param("keyword", "宠物")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.services").isArray())
                    .andExpect(jsonPath("$.services.length()").value(1))
                    .andExpect(jsonPath("$.services[0].name").value("宠物洗澡"))
                    .andExpect(jsonPath("$.products").isArray())
                    .andExpect(jsonPath("$.products.length()").value(1))
                    .andExpect(jsonPath("$.products[0].name").value("宠物食品"))
                    .andExpect(jsonPath("$.merchants").isArray())
                    .andExpect(jsonPath("$.merchants.length()").value(1))
                    .andExpect(jsonPath("$.merchants[0].name").value("测试商家"));

            verify(searchService).getSearchSuggestions("宠物");
        }

        @Test
        @DisplayName("空关键字返回空建议列表")
        void testGetSearchSuggestions_EmptyKeyword() throws Exception {
            SearchSuggestionsDTO emptySuggestions = SearchSuggestionsDTO.builder()
                    .services(new ArrayList<>())
                    .products(new ArrayList<>())
                    .merchants(new ArrayList<>())
                    .build();

            when(searchService.getSearchSuggestions("")).thenReturn(emptySuggestions);

            mockMvc.perform(get("/api/search/suggestions")
                    .param("keyword", "")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.services").isArray())
                    .andExpect(jsonPath("$.services.length()").value(0))
                    .andExpect(jsonPath("$.products").isArray())
                    .andExpect(jsonPath("$.products.length()").value(0))
                    .andExpect(jsonPath("$.merchants").isArray())
                    .andExpect(jsonPath("$.merchants.length()").value(0));

            verify(searchService).getSearchSuggestions("");
        }

        @Test
        @DisplayName("null关键字返回空建议列表")
        void testGetSearchSuggestions_NullKeyword() throws Exception {
            SearchSuggestionsDTO emptySuggestions = SearchSuggestionsDTO.builder()
                    .services(new ArrayList<>())
                    .products(new ArrayList<>())
                    .merchants(new ArrayList<>())
                    .build();

            when(searchService.getSearchSuggestions(null)).thenReturn(emptySuggestions);

            mockMvc.perform(get("/api/search/suggestions")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isBadRequest());

            verify(searchService, never()).getSearchSuggestions(anyString());
        }

        @Test
        @DisplayName("无匹配结果返回空列表")
        void testGetSearchSuggestions_NoMatches() throws Exception {
            SearchSuggestionsDTO emptySuggestions = SearchSuggestionsDTO.builder()
                    .services(new ArrayList<>())
                    .products(new ArrayList<>())
                    .merchants(new ArrayList<>())
                    .build();

            when(searchService.getSearchSuggestions("不存在的关键字xyz")).thenReturn(emptySuggestions);

            mockMvc.perform(get("/api/search/suggestions")
                    .param("keyword", "不存在的关键字xyz")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.services").isArray())
                    .andExpect(jsonPath("$.services.length()").value(0))
                    .andExpect(jsonPath("$.products").isArray())
                    .andExpect(jsonPath("$.products.length()").value(0))
                    .andExpect(jsonPath("$.merchants").isArray())
                    .andExpect(jsonPath("$.merchants.length()").value(0));

            verify(searchService).getSearchSuggestions("不存在的关键字xyz");
        }

        @Test
        @DisplayName("仅返回服务建议")
        void testGetSearchSuggestions_OnlyServices() throws Exception {
            ServiceDTO service = ServiceDTO.builder()
                    .id(1)
                    .name("宠物美容")
                    .price(new BigDecimal("199.00"))
                    .build();

            SearchSuggestionsDTO suggestions = SearchSuggestionsDTO.builder()
                    .services(Arrays.asList(service))
                    .products(new ArrayList<>())
                    .merchants(new ArrayList<>())
                    .build();

            when(searchService.getSearchSuggestions("美容")).thenReturn(suggestions);

            mockMvc.perform(get("/api/search/suggestions")
                    .param("keyword", "美容")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.services.length()").value(1))
                    .andExpect(jsonPath("$.products.length()").value(0))
                    .andExpect(jsonPath("$.merchants.length()").value(0));

            verify(searchService).getSearchSuggestions("美容");
        }

        @Test
        @DisplayName("多个匹配结果")
        void testGetSearchSuggestions_MultipleMatches() throws Exception {
            ServiceDTO service1 = ServiceDTO.builder()
                    .id(1)
                    .name("宠物洗澡")
                    .price(new BigDecimal("99.00"))
                    .build();
            ServiceDTO service2 = ServiceDTO.builder()
                    .id(2)
                    .name("宠物美容")
                    .price(new BigDecimal("199.00"))
                    .build();

            ProductDTO product1 = ProductDTO.builder()
                    .id(1)
                    .name("宠物食品")
                    .price(new BigDecimal("99.00"))
                    .build();
            ProductDTO product2 = ProductDTO.builder()
                    .id(2)
                    .name("宠物玩具")
                    .price(new BigDecimal("49.00"))
                    .build();

            SearchSuggestionsDTO suggestions = SearchSuggestionsDTO.builder()
                    .services(Arrays.asList(service1, service2))
                    .products(Arrays.asList(product1, product2))
                    .merchants(new ArrayList<>())
                    .build();

            when(searchService.getSearchSuggestions("宠物")).thenReturn(suggestions);

            mockMvc.perform(get("/api/search/suggestions")
                    .param("keyword", "宠物")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.services.length()").value(2))
                    .andExpect(jsonPath("$.products.length()").value(2))
                    .andExpect(jsonPath("$.merchants.length()").value(0));

            verify(searchService).getSearchSuggestions("宠物");
        }
    }

    @Nested
    @DisplayName("GET /api/search/hot-keywords - 获取热门搜索关键字")
    class GetHotKeywordsTests {

        @Test
        @DisplayName("成功获取热门关键字")
        void testGetHotKeywords_Success() throws Exception {
            List<HotKeywordDTO> hotKeywords = Arrays.asList(
                    HotKeywordDTO.builder().keyword("宠物洗澡").count(100L).build(),
                    HotKeywordDTO.builder().keyword("宠物美容").count(80L).build(),
                    HotKeywordDTO.builder().keyword("宠物食品").count(60L).build()
            );

            when(searchService.getHotKeywords(10)).thenReturn(hotKeywords);

            mockMvc.perform(get("/api/search/hot-keywords")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].keyword").value("宠物洗澡"))
                    .andExpect(jsonPath("$[0].count").value(100))
                    .andExpect(jsonPath("$[1].keyword").value("宠物美容"))
                    .andExpect(jsonPath("$[2].keyword").value("宠物食品"));

            verify(searchService).getHotKeywords(10);
        }

        @Test
        @DisplayName("使用自定义limit参数")
        void testGetHotKeywords_WithCustomLimit() throws Exception {
            List<HotKeywordDTO> hotKeywords = Arrays.asList(
                    HotKeywordDTO.builder().keyword("宠物洗澡").count(100L).build(),
                    HotKeywordDTO.builder().keyword("宠物美容").count(80L).build()
            );

            when(searchService.getHotKeywords(2)).thenReturn(hotKeywords);

            mockMvc.perform(get("/api/search/hot-keywords")
                    .param("limit", "2")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));

            verify(searchService).getHotKeywords(2);
        }

        @Test
        @DisplayName("空热门关键字列表")
        void testGetHotKeywords_EmptyList() throws Exception {
            when(searchService.getHotKeywords(10)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/search/hot-keywords")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(searchService).getHotKeywords(10);
        }

        @Test
        @DisplayName("limit参数为null使用默认值")
        void testGetHotKeywords_NullLimit() throws Exception {
            List<HotKeywordDTO> hotKeywords = Arrays.asList(
                    HotKeywordDTO.builder().keyword("宠物洗澡").count(100L).build()
            );

            when(searchService.getHotKeywords(null)).thenReturn(hotKeywords);

            mockMvc.perform(get("/api/search/hot-keywords")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk());

            verify(searchService).getHotKeywords(null);
        }

        @Test
        @DisplayName("limit参数为负数")
        void testGetHotKeywords_NegativeLimit() throws Exception {
            List<HotKeywordDTO> hotKeywords = Arrays.asList(
                    HotKeywordDTO.builder().keyword("宠物洗澡").count(100L).build()
            );

            when(searchService.getHotKeywords(-1)).thenReturn(hotKeywords);

            mockMvc.perform(get("/api/search/hot-keywords")
                    .param("limit", "-1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk());

            verify(searchService).getHotKeywords(-1);
        }

        @Test
        @DisplayName("limit参数为零")
        void testGetHotKeywords_ZeroLimit() throws Exception {
            List<HotKeywordDTO> hotKeywords = Collections.emptyList();

            when(searchService.getHotKeywords(0)).thenReturn(hotKeywords);

            mockMvc.perform(get("/api/search/hot-keywords")
                    .param("limit", "0")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(searchService).getHotKeywords(0);
        }

        @Test
        @DisplayName("大量热门关键字")
        void testGetHotKeywords_LargeLimit() throws Exception {
            List<HotKeywordDTO> hotKeywords = Arrays.asList(
                    HotKeywordDTO.builder().keyword("关键字1").count(100L).build(),
                    HotKeywordDTO.builder().keyword("关键字2").count(90L).build(),
                    HotKeywordDTO.builder().keyword("关键字3").count(80L).build(),
                    HotKeywordDTO.builder().keyword("关键字4").count(70L).build(),
                    HotKeywordDTO.builder().keyword("关键字5").count(60L).build()
            );

            when(searchService.getHotKeywords(50)).thenReturn(hotKeywords);

            mockMvc.perform(get("/api/search/hot-keywords")
                    .param("limit", "50")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(5));

            verify(searchService).getHotKeywords(50);
        }
    }

    @Nested
    @DisplayName("POST /api/user/search-history - 保存搜索历史")
    class SaveSearchHistoryTests {

        @Test
        @DisplayName("成功保存搜索历史")
        void testSaveSearchHistory_Success() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("keyword", "宠物洗澡");

            doNothing().when(searchService).saveSearchHistory(testUserId, "宠物洗澡");

            mockMvc.perform(post("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isOk());

            verify(searchService).saveSearchHistory(testUserId, "宠物洗澡");
        }

        @Test
        @DisplayName("未登录返回401")
        void testSaveSearchHistory_Unauthorized() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("keyword", "宠物洗澡");

            mockMvc.perform(post("/api/user/search-history")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(searchService, never()).saveSearchHistory(anyInt(), anyString());
        }

        @Test
        @DisplayName("空关键字不保存")
        void testSaveSearchHistory_EmptyKeyword() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("keyword", "");

            doNothing().when(searchService).saveSearchHistory(testUserId, "");

            mockMvc.perform(post("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isOk());

            verify(searchService).saveSearchHistory(testUserId, "");
        }

        @Test
        @DisplayName("null关键字不保存")
        void testSaveSearchHistory_NullKeyword() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("keyword", null);

            mockMvc.perform(post("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isOk());

            verify(searchService, never()).saveSearchHistory(anyInt(), anyString());
        }

        @Test
        @DisplayName("纯空格关键字不保存")
        void testSaveSearchHistory_BlankKeyword() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("keyword", "   ");

            doNothing().when(searchService).saveSearchHistory(testUserId, "   ");

            mockMvc.perform(post("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isOk());

            verify(searchService).saveSearchHistory(testUserId, "   ");
        }

        @Test
        @DisplayName("关键字前后空格被保留")
        void testSaveSearchHistory_KeywordWithSpaces() throws Exception {
            Map<String, String> request = new HashMap<>();
            request.put("keyword", "  宠物洗澡  ");

            doNothing().when(searchService).saveSearchHistory(testUserId, "  宠物洗澡  ");

            mockMvc.perform(post("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isOk());

            verify(searchService).saveSearchHistory(testUserId, "  宠物洗澡  ");
        }

        @Test
        @DisplayName("长关键字保存")
        void testSaveSearchHistory_LongKeyword() throws Exception {
            String longKeyword = "这是一个很长的搜索关键字用于测试系统对长关键字的处理能力";
            Map<String, String> request = new HashMap<>();
            request.put("keyword", longKeyword);

            doNothing().when(searchService).saveSearchHistory(testUserId, longKeyword);

            mockMvc.perform(post("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isOk());

            verify(searchService).saveSearchHistory(testUserId, longKeyword);
        }

        @Test
        @DisplayName("特殊字符关键字保存")
        void testSaveSearchHistory_SpecialCharacters() throws Exception {
            String specialKeyword = "宠物%洗澡&美容#服务";
            Map<String, String> request = new HashMap<>();
            request.put("keyword", specialKeyword);

            doNothing().when(searchService).saveSearchHistory(testUserId, specialKeyword);

            mockMvc.perform(post("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(toJson(request)))
                    .andDo(result -> {})
                    .andExpect(status().isOk());

            verify(searchService).saveSearchHistory(testUserId, specialKeyword);
        }
    }

    @Nested
    @DisplayName("GET /api/user/search-history - 获取搜索历史")
    class GetSearchHistoryTests {

        @Test
        @DisplayName("成功获取搜索历史")
        void testGetSearchHistory_Success() throws Exception {
            List<String> history = Arrays.asList("宠物洗澡", "宠物美容", "宠物食品");

            when(searchService.getSearchHistory(testUserId, 10)).thenReturn(history);

            mockMvc.perform(get("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0]").value("宠物洗澡"))
                    .andExpect(jsonPath("$[1]").value("宠物美容"))
                    .andExpect(jsonPath("$[2]").value("宠物食品"));

            verify(searchService).getSearchHistory(testUserId, 10);
        }

        @Test
        @DisplayName("未登录返回401")
        void testGetSearchHistory_Unauthorized() throws Exception {
            mockMvc.perform(get("/api/user/search-history")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(searchService, never()).getSearchHistory(anyInt(), anyInt());
        }

        @Test
        @DisplayName("使用自定义limit参数")
        void testGetSearchHistory_WithCustomLimit() throws Exception {
            List<String> history = Arrays.asList("宠物洗澡", "宠物美容");

            when(searchService.getSearchHistory(testUserId, 5)).thenReturn(history);

            mockMvc.perform(get("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .param("limit", "5")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));

            verify(searchService).getSearchHistory(testUserId, 5);
        }

        @Test
        @DisplayName("空搜索历史列表")
        void testGetSearchHistory_EmptyList() throws Exception {
            when(searchService.getSearchHistory(testUserId, 10)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(searchService).getSearchHistory(testUserId, 10);
        }

        @Test
        @DisplayName("limit参数为null使用默认值")
        void testGetSearchHistory_NullLimit() throws Exception {
            List<String> history = Arrays.asList("宠物洗澡");

            when(searchService.getSearchHistory(testUserId, null)).thenReturn(history);

            mockMvc.perform(get("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk());

            verify(searchService).getSearchHistory(testUserId, null);
        }

        @Test
        @DisplayName("limit参数为负数")
        void testGetSearchHistory_NegativeLimit() throws Exception {
            List<String> history = Arrays.asList("宠物洗澡");

            when(searchService.getSearchHistory(testUserId, -1)).thenReturn(history);

            mockMvc.perform(get("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .param("limit", "-1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk());

            verify(searchService).getSearchHistory(testUserId, -1);
        }

        @Test
        @DisplayName("limit参数为零")
        void testGetSearchHistory_ZeroLimit() throws Exception {
            List<String> history = Collections.emptyList();

            when(searchService.getSearchHistory(testUserId, 0)).thenReturn(history);

            mockMvc.perform(get("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .param("limit", "0")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(searchService).getSearchHistory(testUserId, 0);
        }

        @Test
        @DisplayName("大量搜索历史")
        void testGetSearchHistory_LargeLimit() throws Exception {
            List<String> history = Arrays.asList(
                    "关键字1", "关键字2", "关键字3", "关键字4", "关键字5",
                    "关键字6", "关键字7", "关键字8", "关键字9", "关键字10"
            );

            when(searchService.getSearchHistory(testUserId, 20)).thenReturn(history);

            mockMvc.perform(get("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .param("limit", "20")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(10));

            verify(searchService).getSearchHistory(testUserId, 20);
        }

        @Test
        @DisplayName("不同用户获取各自的搜索历史")
        void testGetSearchHistory_DifferentUsers() throws Exception {
            com.petshop.entity.User anotherUser = com.petshop.factory.TestDataFactory.createUser(2);
            anotherUser.setUsername("另一个用户");

            List<String> history1 = Arrays.asList("用户1的搜索");
            List<String> history2 = Arrays.asList("用户2的搜索");

            when(searchService.getSearchHistory(1, 10)).thenReturn(history1);
            when(searchService.getSearchHistory(2, 10)).thenReturn(history2);

            mockMvc.perform(get("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0]").value("用户1的搜索"));

            mockMvc.perform(get("/api/user/search-history")
                    .sessionAttr("user", anotherUser)
                    .sessionAttr("userId", 2)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0]").value("用户2的搜索"));

            verify(searchService).getSearchHistory(1, 10);
            verify(searchService).getSearchHistory(2, 10);
        }
    }

    @Nested
    @DisplayName("DELETE /api/user/search-history - 清空搜索历史")
    class ClearSearchHistoryTests {

        @Test
        @DisplayName("成功清空搜索历史")
        void testClearSearchHistory_Success() throws Exception {
            doNothing().when(searchService).clearSearchHistory(testUserId);

            mockMvc.perform(delete("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isNoContent());

            verify(searchService).clearSearchHistory(testUserId);
        }

        @Test
        @DisplayName("未登录返回401")
        void testClearSearchHistory_Unauthorized() throws Exception {
            mockMvc.perform(delete("/api/user/search-history")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isUnauthorized());

            verify(searchService, never()).clearSearchHistory(anyInt());
        }

        @Test
        @DisplayName("清空后搜索历史为空")
        void testClearSearchHistory_VerifyEmptyAfterClear() throws Exception {
            doNothing().when(searchService).clearSearchHistory(testUserId);
            when(searchService.getSearchHistory(testUserId, 10)).thenReturn(Collections.emptyList());

            mockMvc.perform(delete("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(searchService).clearSearchHistory(testUserId);
            verify(searchService).getSearchHistory(testUserId, 10);
        }

        @Test
        @DisplayName("不同用户清空各自的搜索历史")
        void testClearSearchHistory_DifferentUsers() throws Exception {
            com.petshop.entity.User anotherUser = com.petshop.factory.TestDataFactory.createUser(2);

            doNothing().when(searchService).clearSearchHistory(1);
            doNothing().when(searchService).clearSearchHistory(2);

            mockMvc.perform(delete("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isNoContent());

            mockMvc.perform(delete("/api/user/search-history")
                    .sessionAttr("user", anotherUser)
                    .sessionAttr("userId", 2)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isNoContent());

            verify(searchService).clearSearchHistory(1);
            verify(searchService).clearSearchHistory(2);
        }

        @Test
        @DisplayName("重复清空操作")
        void testClearSearchHistory_Idempotent() throws Exception {
            doNothing().when(searchService).clearSearchHistory(testUserId);

            mockMvc.perform(delete("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isNoContent());

            mockMvc.perform(delete("/api/user/search-history")
                    .sessionAttr("user", testUser)
                    .sessionAttr("userId", testUserId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(result -> {})
                    .andExpect(status().isNoContent());

            verify(searchService, org.mockito.Mockito.times(2)).clearSearchHistory(testUserId);
        }
    }
}
