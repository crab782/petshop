package com.petshop.controller.api;

import com.petshop.entity.*;
import com.petshop.test.BaseTest;
import com.petshop.test.JwtUtil;
import com.petshop.test.TestDataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserApiControllerTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    private TestDataGenerator testDataGenerator;
    private User testUser;
    private Merchant testMerchant;
    private Pet testPet;
    private Service testService;
    private Product testProduct;
    private Address testAddress;
    private Appointment testAppointment;
    private ProductOrder testOrder;
    private Cart testCart;

    @BeforeEach
    public void setUp() {
        super.setUp();
        testDataGenerator = new TestDataGenerator();
        testUser = testDataGenerator.generateUser(1, "test_user", "test_user@example.com");
        testMerchant = testDataGenerator.generateMerchant(1, "Test Merchant", "merchant@example.com");
        testPet = testDataGenerator.generatePet(1, testUser);
        testService = testDataGenerator.generateService(1, testMerchant, "Test Service", new BigDecimal(100));
        testProduct = testDataGenerator.generateProduct(1, testMerchant, "Test Product", new BigDecimal(50), 100);
        testAddress = new Address();
        testAddress.setId(1);
        testAddress.setUser(testUser);
        testAddress.setContactName("Test User");
        testAddress.setPhone("13800138000");
        testAddress.setProvince("Guangdong");
        testAddress.setCity("Shenzhen");
        testAddress.setDistrict("Nanshan");
        testAddress.setDetailAddress("Science Park");
        testAddress.setIsDefault(true);
        testAppointment = testDataGenerator.generateAppointment(1, testMerchant);
        testAppointment.setUser(testUser);
        testOrder = testDataGenerator.generateProductOrder(1, testMerchant);
        testOrder.setUserId(testUser.getId());
        testCart = new Cart();
        testCart.setId(1);
        testCart.setUserId(testUser.getId());
        testCart.setProductId(testProduct.getId());
        testCart.setQuantity(1);
    }

    @Override
    public void tearDown() {
        super.tearDown();
        testDataGenerator.cleanup();
    }

    // Pet management API tests
    @Test
    public void testPetManagement() throws Exception {
        // 1. Get pet list
        String token = JwtUtil.generateUserToken();
        ResultActions result = mockMvc.perform(get("/api/user/pets")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());

        // 2. Add pet
        Pet newPet = new Pet();
        newPet.setName("Test Pet");
        newPet.setType("Dog");
        newPet.setBreed("Golden Retriever");
        newPet.setAge(2);
        newPet.setGender("Male");
        newPet.setUser(testUser);

        result = mockMvc.perform(post("/api/user/pets")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test Pet\",\"type\":\"Dog\",\"breed\":\"Golden Retriever\",\"age\":2,\"gender\":\"Male\"}"))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());

        // 3. Update pet
        result = mockMvc.perform(put("/api/user/pets/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated Test Pet\",\"type\":\"Dog\",\"breed\":\"Golden Retriever\",\"age\":3,\"gender\":\"Male\"}"))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());

        // 4. Delete pet
        result = mockMvc.perform(delete("/api/user/pets/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // Appointment management API tests
    @Test
    public void testAppointmentManagement() throws Exception {
        String token = JwtUtil.generateUserToken();

        // 1. Get appointment list
        ResultActions result = mockMvc.perform(get("/api/user/appointments")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());

        // 2. Create appointment
        result = mockMvc.perform(post("/api/user/appointments")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"merchantId\":1,\"serviceId\":1,\"petId\":1,\"appointmentTime\":\"2026-05-01T10:00:00\",\"notes\":\"Test appointment\"}"))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());

        // 3. Cancel appointment
        result = mockMvc.perform(put("/api/user/appointments/1/cancel")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 4. Get appointment detail
        result = mockMvc.perform(get("/api/user/appointments/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    // Order management API tests
    @Test
    public void testOrderManagement() throws Exception {
        String token = JwtUtil.generateUserToken();

        // 1. Get order list
        ResultActions result = mockMvc.perform(get("/api/user/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());

        // 2. Create order
        result = mockMvc.perform(post("/api/user/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"merchantId\":1,\"addressId\":1,\"items\":[{\"productId\":1,\"quantity\":1}],\"totalPrice\":50,\"notes\":\"Test order\"}"))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());

        // 3. Cancel order
        result = mockMvc.perform(put("/api/user/orders/1/cancel")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 4. Confirm receipt
        result = mockMvc.perform(put("/api/user/orders/1/confirm")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // Cart management API tests
    @Test
    public void testCartManagement() throws Exception {
        String token = JwtUtil.generateUserToken();

        // 1. Add product to cart
        ResultActions result = mockMvc.perform(post("/api/user/cart")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":1,\"quantity\":2}"))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 2. Get cart list
        result = mockMvc.perform(get("/api/user/cart")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());

        // 3. Update cart item quantity
        result = mockMvc.perform(put("/api/user/cart/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantity\":3}"))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 4. Delete cart item
        result = mockMvc.perform(delete("/api/user/cart/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // Address management API tests
    @Test
    public void testAddressManagement() throws Exception {
        String token = JwtUtil.generateUserToken();

        // 1. Get address list
        ResultActions result = mockMvc.perform(get("/api/user/addresses")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());

        // 2. Add address
        result = mockMvc.perform(post("/api/user/addresses")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullName\":\"Test User\",\"phone\":\"13800138000\",\"province\":\"Guangdong\",\"city\":\"Shenzhen\",\"district\":\"Nanshan\",\"detailAddress\":\"Science Park\",\"isDefault\":true}"))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());

        // 3. Update address
        result = mockMvc.perform(put("/api/user/addresses/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullName\":\"Updated Test User\",\"phone\":\"13800138000\",\"province\":\"Guangdong\",\"city\":\"Shenzhen\",\"district\":\"Futian\",\"detailAddress\":\"Futian Center\",\"isDefault\":true}"))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());

        // 4. Delete address
        result = mockMvc.perform(delete("/api/user/addresses/1")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // Error scenario tests
    @Test
    public void testUnauthorizedAccess() throws Exception {
        // Request without token
        ResultActions result = mockMvc.perform(get("/api/user/pets")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        result.andExpect(status().isUnauthorized());

        // Request with invalid token
        result = mockMvc.perform(get("/api/user/pets")
                .header("Authorization", "Bearer invalid_token")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void testInvalidInput() throws Exception {
        String token = JwtUtil.generateUserToken();

        // Add pet with invalid data
        ResultActions result = mockMvc.perform(post("/api/user/pets")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"type\":\"Dog\",\"breed\":\"Golden Retriever\",\"age\":-1,\"gender\":\"Male\"}"))
                .andDo(print());
        result.andExpect(status().isBadRequest());

        // Create appointment with invalid time
        result = mockMvc.perform(post("/api/user/appointments")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"merchantId\":1,\"serviceId\":1,\"petId\":1,\"appointmentTime\":\"2025-01-01T10:00:00\",\"notes\":\"Test appointment\"}"))
                .andDo(print());
        result.andExpect(status().isBadRequest());
    }
}