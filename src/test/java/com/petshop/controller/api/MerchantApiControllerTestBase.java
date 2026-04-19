package com.petshop.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.dto.ApiResponse;
import com.petshop.entity.Merchant;
import com.petshop.factory.TestDataFactory;
import com.petshop.service.*;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class MerchantApiControllerTestBase {

    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper;

    {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Mock
    protected MerchantService merchantService;

    @Mock
    protected ServiceService serviceService;

    @Mock
    protected ProductService productService;

    @Mock
    protected CategoryService categoryService;

    @Mock
    protected AppointmentService appointmentService;

    @Mock
    protected ProductOrderService productOrderService;

    @Mock
    protected ReviewService reviewService;

    @Mock
    protected MerchantSettingsService merchantSettingsService;

    @Mock
    protected MerchantStatsService merchantStatsService;

    @Mock
    protected UserService userService;

    @Mock
    protected PetService petService;

    @Mock
    protected HttpSession session;

    protected Merchant testMerchant;
    protected Integer testMerchantId = 1;

    @BeforeEach
    protected void setUp() {
        testMerchant = TestDataFactory.createMerchant(testMerchantId);
    }

    protected void mockMerchantSession() {
        when(session.getAttribute("merchant")).thenReturn(testMerchant);
        when(session.getAttribute("merchantId")).thenReturn(testMerchantId);
    }

    protected void mockMerchantSession(Integer merchantId) {
        testMerchant = TestDataFactory.createMerchant(merchantId);
        when(session.getAttribute("merchant")).thenReturn(testMerchant);
        when(session.getAttribute("merchantId")).thenReturn(merchantId);
    }

    protected void mockMerchantSession(Merchant merchant) {
        this.testMerchant = merchant;
        when(session.getAttribute("merchant")).thenReturn(merchant);
        when(session.getAttribute("merchantId")).thenReturn(merchant.getId());
    }

    protected String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    protected <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }

    protected ResultActions performGet(String url) throws Exception {
        return mockMvc.perform(get(url)
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performGet(String url, Object... uriVars) throws Exception {
        return mockMvc.perform(get(url, uriVars)
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performPost(String url, Object body) throws Exception {
        return mockMvc.perform(post(url)
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
                .andDo(print());
    }

    protected ResultActions performPost(String url) throws Exception {
        return mockMvc.perform(post(url)
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performPut(String url, Object body) throws Exception {
        return mockMvc.perform(put(url)
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
                .andDo(print());
    }

    protected ResultActions performPut(String url, Object body, Object... uriVars) throws Exception {
        return mockMvc.perform(put(url, uriVars)
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
                .andDo(print());
    }

    protected ResultActions performDelete(String url) throws Exception {
        return mockMvc.perform(delete(url)
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performDelete(String url, Object... uriVars) throws Exception {
        return mockMvc.perform(delete(url, uriVars)
                .sessionAttr("merchant", testMerchant)
                .sessionAttr("merchantId", testMerchantId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected void assertSuccess(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    protected void assertSuccess(ResultActions result, String message) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value(message));
    }

    protected void assertError(ResultActions result, int code) throws Exception {
        result.andExpect(jsonPath("$.code").value(code));
    }

    protected void assertError(ResultActions result, int code, String message) throws Exception {
        result.andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").value(message));
    }

    protected void assertBadRequest(ResultActions result) throws Exception {
        result.andExpect(status().isBadRequest());
    }

    protected void assertUnauthorized(ResultActions result) throws Exception {
        result.andExpect(status().isUnauthorized());
    }

    protected void assertNotFound(ResultActions result) throws Exception {
        result.andExpect(status().isNotFound());
    }

    protected void assertDataNotNull(ResultActions result) throws Exception {
        result.andExpect(jsonPath("$.data").exists());
    }

    protected void assertDataEquals(ResultActions result, String field, Object value) throws Exception {
        result.andExpect(jsonPath("$.data." + field).value(value));
    }

    protected void assertMessageEquals(ResultActions result, String message) throws Exception {
        result.andExpect(jsonPath("$.message").value(message));
    }

    protected void assertSuccessResponse(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    protected void assertSuccessResponseWithData(ResultActions result, String dataField) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data." + dataField).exists());
    }

    protected void assertErrorResponse(ResultActions result, int code, String message) throws Exception {
        result.andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    protected void assertPaginatedResponse(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").isNumber())
                .andExpect(jsonPath("$.data.totalPages").isNumber())
                .andExpect(jsonPath("$.data.pageSize").isNumber())
                .andExpect(jsonPath("$.data.currentPage").isNumber());
    }

    protected void assertListResponse(ResultActions result, int expectedSize) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(expectedSize));
    }

    protected void assertCreatedResponse(ResultActions result) throws Exception {
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    protected void assertNoContentResponse(ResultActions result) throws Exception {
        result.andExpect(status().isNoContent());
    }
}
