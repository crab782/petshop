package com.petshop.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.entity.Merchant;
import com.petshop.entity.User;
import com.petshop.factory.TestDataFactory;
import com.petshop.repository.AnnouncementRepository;
import com.petshop.repository.AppointmentRepository;
import com.petshop.repository.MerchantRepository;
import com.petshop.repository.ProductRepository;
import com.petshop.repository.ServiceRepository;
import com.petshop.repository.SystemConfigRepository;
import com.petshop.repository.SystemSettingsRepository;
import com.petshop.repository.UserRepository;
import com.petshop.service.ActivityService;
import com.petshop.service.MerchantService;
import com.petshop.service.ProductService;
import com.petshop.service.ReviewService;
import com.petshop.service.RoleService;
import com.petshop.service.ScheduledTaskService;
import com.petshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class AdminApiControllerTestBase {

    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper;

    {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Mock
    protected UserService userService;

    @Mock
    protected MerchantService merchantService;

    @Mock
    protected ProductService productService;

    @Mock
    protected ReviewService reviewService;

    @Mock
    protected RoleService roleService;

    @Mock
    protected ActivityService activityService;

    @Mock
    protected ScheduledTaskService scheduledTaskService;

    @Mock
    protected UserRepository userRepository;

    @Mock
    protected MerchantRepository merchantRepository;

    @Mock
    protected AppointmentRepository appointmentRepository;

    @Mock
    protected ServiceRepository serviceRepository;

    @Mock
    protected AnnouncementRepository announcementRepository;

    @Mock
    protected ProductRepository productRepository;

    @Mock
    protected SystemConfigRepository systemConfigRepository;

    @Mock
    protected SystemSettingsRepository systemSettingsRepository;

    protected User testAdmin;
    protected Merchant testMerchant;
    protected Integer testAdminId = 1;
    protected Integer testMerchantId = 1;

    @BeforeEach
    protected void setUp() {
        testAdmin = TestDataFactory.createUser(testAdminId);
        testMerchant = TestDataFactory.createMerchant(testMerchantId);
    }

    protected String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    protected ResultActions performGet(String url) throws Exception {
        return mockMvc.perform(get(url)
                .sessionAttr("admin", testAdmin)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performGet(String url, Object... uriVars) throws Exception {
        return mockMvc.perform(get(url, uriVars)
                .sessionAttr("admin", testAdmin)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performGetWithParams(String url, String paramName, String paramValue) throws Exception {
        return mockMvc.perform(get(url)
                .sessionAttr("admin", testAdmin)
                .param(paramName, paramValue)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performPost(String url, Object body) throws Exception {
        return mockMvc.perform(post(url)
                .sessionAttr("admin", testAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
                .andDo(print());
    }

    protected ResultActions performPost(String url) throws Exception {
        return mockMvc.perform(post(url)
                .sessionAttr("admin", testAdmin)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performPost(String url, Object body, Object... uriVars) throws Exception {
        return mockMvc.perform(post(url, uriVars)
                .sessionAttr("admin", testAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body != null ? toJson(body) : ""))
                .andDo(print());
    }

    protected ResultActions performPut(String url, Object body) throws Exception {
        return mockMvc.perform(put(url)
                .sessionAttr("admin", testAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
                .andDo(print());
    }

    protected ResultActions performPut(String url, Object body, Object... uriVars) throws Exception {
        return mockMvc.perform(put(url, uriVars)
                .sessionAttr("admin", testAdmin)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
                .andDo(print());
    }

    protected ResultActions performPutWithParam(String url, String paramName, String paramValue, Object... uriVars) throws Exception {
        return mockMvc.perform(put(url, uriVars)
                .sessionAttr("admin", testAdmin)
                .param(paramName, paramValue)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performDelete(String url) throws Exception {
        return mockMvc.perform(delete(url)
                .sessionAttr("admin", testAdmin)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performDelete(String url, Object... uriVars) throws Exception {
        return mockMvc.perform(delete(url, uriVars)
                .sessionAttr("admin", testAdmin)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performGetUnauthorized(String url) throws Exception {
        return mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performPostUnauthorized(String url, Object body) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
                .andDo(print());
    }

    protected ResultActions performPutUnauthorized(String url, Object body) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
                .andDo(print());
    }

    protected ResultActions performDeleteUnauthorized(String url) throws Exception {
        return mockMvc.perform(delete(url)
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

    protected void assertUnauthorized(ResultActions result) throws Exception {
        result.andExpect(status().isUnauthorized());
    }

    protected void assertNotFound(ResultActions result) throws Exception {
        result.andExpect(status().isNotFound());
    }

    protected void assertBadRequest(ResultActions result) throws Exception {
        result.andExpect(status().isBadRequest());
    }

    protected void assertNoContent(ResultActions result) throws Exception {
        result.andExpect(status().isNoContent());
    }

    protected void assertDataNotNull(ResultActions result) throws Exception {
        result.andExpect(jsonPath("$.data").exists());
    }

    protected void assertPaginatedResponse(ResultActions result) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.data").isArray())
                .andExpect(jsonPath("$.data.total").isNumber())
                .andExpect(jsonPath("$.data.page").isNumber())
                .andExpect(jsonPath("$.data.pageSize").isNumber())
                .andExpect(jsonPath("$.data.totalPages").isNumber());
    }
}
