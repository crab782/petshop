package com.petshop.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.config.TestRedisConfig;
import com.petshop.dto.*;
import com.petshop.entity.Merchant;
import com.petshop.entity.Product;
import com.petshop.entity.User;
import com.petshop.entity.Address;
import com.petshop.mapper.MerchantMapper;
import com.petshop.mapper.ProductMapper;
import com.petshop.mapper.UserMapper;
import com.petshop.mapper.AddressMapper;
import com.petshop.service.AuthService;
import com.petshop.service.ProductService;
import com.petshop.service.ProductOrderService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class PerformanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductOrderService productOrderService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final int WARMUP_ITERATIONS = 5;
    private static final int MEASUREMENT_ITERATIONS = 10;
    private static final int CONCURRENT_USERS = 50;
    private static final int TEST_TIMEOUT_SECONDS = 30;

    private static List<Long> loginResponseTimes = new CopyOnWriteArrayList<>();
    private static List<Long> productQueryResponseTimes = new CopyOnWriteArrayList<>();
    private static List<Long> orderCreationResponseTimes = new CopyOnWriteArrayList<>();
    private static AtomicInteger successCount = new AtomicInteger(0);
    private static AtomicInteger failureCount = new AtomicInteger(0);

    private User testUser;
    private Merchant testMerchant;
    private Product testProduct;
    private Address testAddress;
    private String testPassword = "password123";
    private String encodedPassword;
    private String testPhone = "13800138000";

    @BeforeEach
    void setUp() {
        encodedPassword = passwordEncoder.encode(testPassword);

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPhone(testPhone);
        testUser.setEmail("testuser@example.com");
        testUser.setPassword(encodedPassword);
        testUser.setStatus("active");
        userMapper.insert(testUser);

        testMerchant = new Merchant();
        testMerchant.setName("Test Merchant");
        testMerchant.setContactPerson("Contact Person");
        testMerchant.setPhone("13800138001");
        testMerchant.setEmail("merchant@example.com");
        testMerchant.setPassword(encodedPassword);
        testMerchant.setAddress("Test Address");
        testMerchant.setStatus("approved");
        merchantMapper.insert(testMerchant);

        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStock(100);
        testProduct.setMerchantId(testMerchant.getId());
        testProduct.setImage("test.jpg");
        productMapper.insert(testProduct);

        testAddress = new Address();
        testAddress.setUserId(testUser.getId());
        testAddress.setContactName("Test Contact");
        testAddress.setPhone(testPhone);
        testAddress.setProvince("北京市");
        testAddress.setCity("北京市");
        testAddress.setDistrict("朝阳区");
        testAddress.setDetailAddress("测试地址123号");
        testAddress.setIsDefault(true);
        addressMapper.insert(testAddress);

        loginResponseTimes.clear();
        productQueryResponseTimes.clear();
        orderCreationResponseTimes.clear();
        successCount.set(0);
        failureCount.set(0);
    }

    @BeforeAll
    static void setup() {
        System.out.println("========================================");
        System.out.println("性能测试开始");
        System.out.println("========================================");
        System.out.println("预热迭代次数: " + WARMUP_ITERATIONS);
        System.out.println("测量迭代次数: " + MEASUREMENT_ITERATIONS);
        System.out.println("并发用户数: " + CONCURRENT_USERS);
        System.out.println("========================================\n");
    }

    @AfterAll
    static void teardown() {
        System.out.println("\n========================================");
        System.out.println("性能测试结束");
        System.out.println("========================================");
        printPerformanceReport();
    }

    @Test
    @Order(1)
    @DisplayName("用户登录接口性能测试")
    void testLoginPerformance() throws Exception {
        System.out.println("\n>>> 测试用户登录接口性能 <<<");
        
        loginResponseTimes.clear();
        
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            performLogin("warmup_user_" + i);
        }
        
        long totalStartTime = System.currentTimeMillis();
        
        for (int i = 0; i < MEASUREMENT_ITERATIONS; i++) {
            long startTime = System.nanoTime();
            MvcResult result = performLogin("test_user_" + i);
            long endTime = System.nanoTime();
            
            long responseTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
            loginResponseTimes.add(responseTime);
            
            System.out.printf("迭代 %2d: 响应时间 = %4d ms, 状态码 = %d%n",
                i + 1, responseTime, result.getResponse().getStatus());
        }
        
        long totalEndTime = System.currentTimeMillis();
        long totalTime = totalEndTime - totalStartTime;
        
        printStatistics("登录接口", loginResponseTimes, totalTime);
    }

    @Test
    @Order(2)
    @DisplayName("商品查询接口性能测试")
    void testProductQueryPerformance() throws Exception {
        System.out.println("\n>>> 测试商品查询接口性能 <<<");
        
        productQueryResponseTimes.clear();
        
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            performProductQuery(i);
        }
        
        long totalStartTime = System.currentTimeMillis();
        
        for (int i = 0; i < MEASUREMENT_ITERATIONS; i++) {
            long startTime = System.nanoTime();
            MvcResult result = performProductQuery(i);
            long endTime = System.nanoTime();
            
            long responseTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
            productQueryResponseTimes.add(responseTime);
            
            System.out.printf("迭代 %2d: 响应时间 = %4d ms, 状态码 = %d%n",
                i + 1, responseTime, result.getResponse().getStatus());
        }
        
        long totalEndTime = System.currentTimeMillis();
        long totalTime = totalEndTime - totalStartTime;
        
        printStatistics("商品查询接口", productQueryResponseTimes, totalTime);
    }

    @Test
    @Order(3)
    @DisplayName("订单创建接口性能测试")
    void testOrderCreationPerformance() throws Exception {
        System.out.println("\n>>> 测试订单创建接口性能 <<<");
        
        orderCreationResponseTimes.clear();
        
        String token = getAuthToken();
        
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            performOrderCreation(token, i);
        }
        
        long totalStartTime = System.currentTimeMillis();
        
        for (int i = 0; i < MEASUREMENT_ITERATIONS; i++) {
            long startTime = System.nanoTime();
            MvcResult result = performOrderCreation(token, i);
            long endTime = System.nanoTime();
            
            long responseTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
            orderCreationResponseTimes.add(responseTime);
            
            System.out.printf("迭代 %2d: 响应时间 = %4d ms, 状态码 = %d%n",
                i + 1, responseTime, result.getResponse().getStatus());
        }
        
        long totalEndTime = System.currentTimeMillis();
        long totalTime = totalEndTime - totalStartTime;
        
        printStatistics("订单创建接口", orderCreationResponseTimes, totalTime);
    }

    @Test
    @Order(4)
    @DisplayName("并发访问测试")
    void testConcurrentAccess() throws Exception {
        System.out.println("\n>>> 测试并发访问性能 <<<");
        System.out.println("并发用户数: " + CONCURRENT_USERS);
        
        successCount.set(0);
        failureCount.set(0);
        
        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_USERS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_USERS);
        List<Future<Long>> futures = new ArrayList<>();
        
        long testStartTime = System.currentTimeMillis();
        
        for (int i = 0; i < CONCURRENT_USERS; i++) {
            final int userId = i;
            Future<Long> future = executorService.submit(() -> {
                try {
                    long startTime = System.nanoTime();
                    
                    MvcResult productResult = performProductQuery(userId);
                    MvcResult servicesResult = performServicesQuery(userId);
                    
                    long endTime = System.nanoTime();
                    long responseTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
                    
                    if (productResult.getResponse().getStatus() == 200 &&
                        servicesResult.getResponse().getStatus() == 200) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                    }
                    
                    return responseTime;
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    return -1L;
                } finally {
                    latch.countDown();
                }
            });
            futures.add(future);
        }
        
        boolean completed = latch.await(TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        long testEndTime = System.currentTimeMillis();
        
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        
        List<Long> responseTimes = new ArrayList<>();
        for (Future<Long> future : futures) {
            try {
                Long time = future.get(1, TimeUnit.SECONDS);
                if (time > 0) {
                    responseTimes.add(time);
                }
            } catch (Exception e) {
                // Ignore
            }
        }
        
        System.out.println("\n并发测试结果:");
        System.out.println("总请求数: " + CONCURRENT_USERS);
        System.out.println("成功请求: " + successCount.get());
        System.out.println("失败请求: " + failureCount.get());
        System.out.println("成功率: " + String.format("%.2f%%", 
            (successCount.get() * 100.0 / CONCURRENT_USERS)));
        System.out.println("总耗时: " + (testEndTime - testStartTime) + " ms");
        
        if (!responseTimes.isEmpty()) {
            double avgResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
            double throughput = (successCount.get() * 1000.0) / (testEndTime - testStartTime);
            
            System.out.println("平均响应时间: " + String.format("%.2f", avgResponseTime) + " ms");
            System.out.println("吞吐量: " + String.format("%.2f", throughput) + " 请求/秒");
        }
        
        assertTrue(completed, "并发测试应在超时时间内完成");
        assertTrue(successCount.get() > 0, "应至少有一个成功的请求");
    }

    @Test
    @Order(5)
    @DisplayName("吞吐量测试")
    void testThroughput() throws Exception {
        System.out.println("\n>>> 测试系统吞吐量 <<<");
        
        int totalRequests = 100;
        int threadCount = 10;
        
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(totalRequests);
        AtomicInteger completedRequests = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < totalRequests; i++) {
            executorService.submit(() -> {
                try {
                    performProductQuery(new Random().nextInt(1000));
                    completedRequests.incrementAndGet();
                } catch (Exception e) {
                    // Ignore
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();
        
        executorService.shutdown();
        
        long duration = endTime - startTime;
        double throughput = (completedRequests.get() * 1000.0) / duration;
        
        System.out.println("总请求数: " + totalRequests);
        System.out.println("完成请求数: " + completedRequests.get());
        System.out.println("测试时长: " + duration + " ms");
        System.out.println("吞吐量: " + String.format("%.2f", throughput) + " 请求/秒");
        
        assertTrue(completedRequests.get() > totalRequests * 0.9, 
            "应完成至少90%的请求");
    }

    private MvcResult performLogin(String username) throws Exception {
        LoginRequest request = new LoginRequest();
        request.setPhone(testPhone);
        request.setPassword(testPassword);
        
        return mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn();
    }

    private MvcResult performProductQuery(int queryId) throws Exception {
        return mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("pageSize", "10"))
            .andReturn();
    }

    private MvcResult performServicesQuery(int queryId) throws Exception {
        return mockMvc.perform(get("/api/services")
                .param("page", "0")
                .param("pageSize", "10"))
            .andReturn();
    }

    private MvcResult performOrderCreation(String token, int orderId) throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setAddressId(testAddress.getId());
        
        List<CreateOrderRequest.OrderItemRequest> items = new ArrayList<>();
        CreateOrderRequest.OrderItemRequest item = new CreateOrderRequest.OrderItemRequest();
        item.setProductId(testProduct.getId());
        item.setQuantity(1);
        items.add(item);
        request.setItems(items);
        request.setRemark("性能测试订单 " + orderId);
        
        return mockMvc.perform(post("/api/user/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn();
    }

    private String getAuthToken() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setPhone(testPhone);
        request.setPassword(testPassword);
        
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn();
        
        String responseBody = result.getResponse().getContentAsString();
        ApiResponse response = objectMapper.readValue(responseBody, ApiResponse.class);
        
        if (response.getData() instanceof Map) {
            Map<String, Object> data = (Map<String, Object>) response.getData();
            return (String) data.get("token");
        }
        
        return "";
    }

    private void printStatistics(String testName, List<Long> responseTimes, long totalTime) {
        if (responseTimes.isEmpty()) {
            System.out.println("无有效数据");
            return;
        }
        
        Collections.sort(responseTimes);
        
        long min = responseTimes.get(0);
        long max = responseTimes.get(responseTimes.size() - 1);
        double avg = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long median = responseTimes.get(responseTimes.size() / 2);
        long p90 = responseTimes.get((int) (responseTimes.size() * 0.9));
        long p95 = responseTimes.get((int) (responseTimes.size() * 0.95));
        long p99 = responseTimes.get((int) (responseTimes.size() * 0.99));
        
        double throughput = (responseTimes.size() * 1000.0) / totalTime;
        
        System.out.println("\n" + testName + " 性能统计:");
        System.out.println("----------------------------------------");
        System.out.println("最小响应时间: " + min + " ms");
        System.out.println("最大响应时间: " + max + " ms");
        System.out.println("平均响应时间: " + String.format("%.2f", avg) + " ms");
        System.out.println("中位数响应时间: " + median + " ms");
        System.out.println("P90 响应时间: " + p90 + " ms");
        System.out.println("P95 响应时间: " + p95 + " ms");
        System.out.println("P99 响应时间: " + p99 + " ms");
        System.out.println("总耗时: " + totalTime + " ms");
        System.out.println("吞吐量: " + String.format("%.2f", throughput) + " 请求/秒");
        System.out.println("----------------------------------------");
    }

    private static void printPerformanceReport() {
        System.out.println("\n========================================");
        System.out.println("性能测试报告汇总");
        System.out.println("========================================");
        
        if (!loginResponseTimes.isEmpty()) {
            System.out.println("\n登录接口性能:");
            printSummaryStatistics(loginResponseTimes);
        }
        
        if (!productQueryResponseTimes.isEmpty()) {
            System.out.println("\n商品查询接口性能:");
            printSummaryStatistics(productQueryResponseTimes);
        }
        
        if (!orderCreationResponseTimes.isEmpty()) {
            System.out.println("\n订单创建接口性能:");
            printSummaryStatistics(orderCreationResponseTimes);
        }
        
        System.out.println("\n========================================");
    }

    private static void printSummaryStatistics(List<Long> responseTimes) {
        Collections.sort(responseTimes);
        
        long min = responseTimes.get(0);
        long max = responseTimes.get(responseTimes.size() - 1);
        double avg = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        long median = responseTimes.get(responseTimes.size() / 2);
        
        System.out.println("  最小: " + min + " ms");
        System.out.println("  最大: " + max + " ms");
        System.out.println("  平均: " + String.format("%.2f", avg) + " ms");
        System.out.println("  中位数: " + median + " ms");
    }
}
