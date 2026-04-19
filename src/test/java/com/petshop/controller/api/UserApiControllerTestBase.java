package com.petshop.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.entity.User;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class UserApiControllerTestBase {

    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper;

    {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Mock
    protected UserService userService;

    @Mock
    protected PetService petService;

    @Mock
    protected AddressService addressService;

    @Mock
    protected MerchantService merchantService;

    @Mock
    protected ServiceService serviceService;

    @Mock
    protected ProductService productService;

    @Mock
    protected CartService cartService;

    @Mock
    protected ProductOrderService productOrderService;

    @Mock
    protected AppointmentService appointmentService;

    @Mock
    protected ReviewService reviewService;

    @Mock
    protected NotificationService notificationService;

    @Mock
    protected SearchService searchService;

    @Mock
    protected FavoriteService favoriteService;

    @Mock
    protected AuthService authService;

    @Mock
    protected UserHomeService userHomeService;

    @Mock
    protected HttpSession session;

    protected User testUser;
    protected Integer testUserId = 1;

    @BeforeEach
    protected void setUp() {
        testUser = TestDataFactory.createUser(testUserId);
    }

    protected void mockUserSession() {
        when(session.getAttribute("user")).thenReturn(testUser);
        when(session.getAttribute("userId")).thenReturn(testUserId);
    }

    protected void mockUserSession(Integer userId) {
        testUser = TestDataFactory.createUser(userId);
        when(session.getAttribute("user")).thenReturn(testUser);
        when(session.getAttribute("userId")).thenReturn(userId);
    }

    protected void mockUserSession(User user) {
        this.testUser = user;
        when(session.getAttribute("user")).thenReturn(user);
        when(session.getAttribute("userId")).thenReturn(user.getId());
    }

    protected void clearUserSession() {
        when(session.getAttribute("user")).thenReturn(null);
        when(session.getAttribute("userId")).thenReturn(null);
    }

    protected String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    protected <T> T fromJson(String json, Class<T> clazz) throws Exception {
        return objectMapper.readValue(json, clazz);
    }

    protected ResultActions performGet(String url) throws Exception {
        return mockMvc.perform(get(url)
                .sessionAttr("user", testUser)
                .sessionAttr("userId", testUserId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performGet(String url, Object... uriVars) throws Exception {
        return mockMvc.perform(get(url, uriVars)
                .sessionAttr("user", testUser)
                .sessionAttr("userId", testUserId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performGetUnauthenticated(String url) throws Exception {
        return mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performGetUnauthenticated(String url, Object... uriVars) throws Exception {
        return mockMvc.perform(get(url, uriVars)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performPost(String url, Object body) throws Exception {
        return mockMvc.perform(post(url)
                .sessionAttr("user", testUser)
                .sessionAttr("userId", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
                .andDo(print());
    }

    protected ResultActions performPost(String url) throws Exception {
        return mockMvc.perform(post(url)
                .sessionAttr("user", testUser)
                .sessionAttr("userId", testUserId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performPostUnauthenticated(String url, Object body) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
                .andDo(print());
    }

    protected ResultActions performPut(String url, Object body) throws Exception {
        return mockMvc.perform(put(url)
                .sessionAttr("user", testUser)
                .sessionAttr("userId", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
                .andDo(print());
    }

    protected ResultActions performPut(String url, Object body, Object... uriVars) throws Exception {
        return mockMvc.perform(put(url, uriVars)
                .sessionAttr("user", testUser)
                .sessionAttr("userId", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(body)))
                .andDo(print());
    }

    protected ResultActions performDelete(String url) throws Exception {
        return mockMvc.perform(delete(url)
                .sessionAttr("user", testUser)
                .sessionAttr("userId", testUserId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    protected ResultActions performDelete(String url, Object... uriVars) throws Exception {
        return mockMvc.perform(delete(url, uriVars)
                .sessionAttr("user", testUser)
                .sessionAttr("userId", testUserId)
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

    protected void assertForbidden(ResultActions result) throws Exception {
        result.andExpect(status().isForbidden());
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
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
