package com.petshop.controller.api;

import com.petshop.dto.ServiceDTO;
import com.petshop.entity.Merchant;
import com.petshop.entity.Service;
import com.petshop.factory.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("服务API测试")
public class ServiceApiControllerTest extends UserApiControllerTestBase {

    private ServiceApiController controller;

    @BeforeEach
    public void setupController() {
        controller = new ServiceApiController();
        org.springframework.test.util.ReflectionTestUtils.setField(controller, "serviceService", serviceService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("GET /api/services - 获取服务列表")
    class GetServicesTests {

        @Test
        @DisplayName("成功获取所有服务列表（无类型筛选）")
        void testGetServices_Success_WithoutType() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service1 = TestDataFactory.createService(1, merchant, "宠物洗澡", new BigDecimal("99.00"));
            Service service2 = TestDataFactory.createService(2, merchant, "宠物美容", new BigDecimal("199.00"));
            List<Service> services = Arrays.asList(service1, service2);

            ServiceDTO dto1 = ServiceDTO.builder().id(1).name("宠物洗澡").price(new BigDecimal("99.00")).merchantId(1).merchantName("测试商家").build();
            ServiceDTO dto2 = ServiceDTO.builder().id(2).name("宠物美容").price(new BigDecimal("199.00")).merchantId(1).merchantName("测试商家").build();
            List<ServiceDTO> dtoList = Arrays.asList(dto1, dto2);

            when(serviceService.findByType(null)).thenReturn(services);
            when(serviceService.convertToDTOList(services)).thenReturn(dtoList);

            var result = performGetUnauthenticated("/api/services");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].name").value("宠物洗澡"))
                    .andExpect(jsonPath("$[1].name").value("宠物美容"));

            verify(serviceService).findByType(null);
            verify(serviceService).convertToDTOList(services);
        }

        @Test
        @DisplayName("成功获取服务列表（按类型筛选）")
        void testGetServices_Success_WithType() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service1 = TestDataFactory.createService(1, merchant, "宠物洗澡", new BigDecimal("99.00"));
            service1.setCategory("grooming");
            Service service2 = TestDataFactory.createService(2, merchant, "宠物美容", new BigDecimal("199.00"));
            service2.setCategory("grooming");
            List<Service> services = Arrays.asList(service1, service2);

            ServiceDTO dto1 = ServiceDTO.builder().id(1).name("宠物洗澡").price(new BigDecimal("99.00")).category("grooming").build();
            ServiceDTO dto2 = ServiceDTO.builder().id(2).name("宠物美容").price(new BigDecimal("199.00")).category("grooming").build();
            List<ServiceDTO> dtoList = Arrays.asList(dto1, dto2);

            when(serviceService.findByType("grooming")).thenReturn(services);
            when(serviceService.convertToDTOList(services)).thenReturn(dtoList);

            var result = performGetUnauthenticated("/api/services?type=grooming");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].category").value("grooming"))
                    .andExpect(jsonPath("$[1].category").value("grooming"));

            verify(serviceService).findByType("grooming");
            verify(serviceService).convertToDTOList(services);
        }

        @Test
        @DisplayName("成功获取空服务列表")
        void testGetServices_EmptyList() throws Exception {
            when(serviceService.findByType(null)).thenReturn(Collections.emptyList());
            when(serviceService.convertToDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

            var result = performGetUnauthenticated("/api/services");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(serviceService).findByType(null);
            verify(serviceService).convertToDTOList(Collections.emptyList());
        }

        @Test
        @DisplayName("空字符串类型参数返回所有服务")
        void testGetServices_EmptyTypeParameter() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant, "宠物洗澡", new BigDecimal("99.00"));
            List<Service> services = Collections.singletonList(service);

            ServiceDTO dto = ServiceDTO.builder().id(1).name("宠物洗澡").price(new BigDecimal("99.00")).build();
            List<ServiceDTO> dtoList = Collections.singletonList(dto);

            when(serviceService.findByType("")).thenReturn(services);
            when(serviceService.convertToDTOList(services)).thenReturn(dtoList);

            var result = performGetUnauthenticated("/api/services?type=");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1));

            verify(serviceService).findByType("");
        }
    }

    @Nested
    @DisplayName("GET /api/services/{id} - 获取服务详情")
    class GetServiceByIdTests {

        @Test
        @DisplayName("成功获取服务详情")
        void testGetServiceById_Success() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "测试商家", "test@merchant.com");
            Service service = TestDataFactory.createService(1, merchant, "宠物洗澡", "专业宠物洗澡服务", new BigDecimal("99.00"), 60);

            ServiceDTO dto = ServiceDTO.builder()
                    .id(1)
                    .name("宠物洗澡")
                    .description("专业宠物洗澡服务")
                    .price(new BigDecimal("99.00"))
                    .duration(60)
                    .merchantId(1)
                    .merchantName("测试商家")
                    .status("enabled")
                    .build();

            when(serviceService.findById(1)).thenReturn(service);
            when(serviceService.convertToDTO(service)).thenReturn(dto);

            var result = performGetUnauthenticated("/api/services/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("宠物洗澡"))
                    .andExpect(jsonPath("$.description").value("专业宠物洗澡服务"))
                    .andExpect(jsonPath("$.price").value(99.00))
                    .andExpect(jsonPath("$.duration").value(60))
                    .andExpect(jsonPath("$.merchantId").value(1))
                    .andExpect(jsonPath("$.merchantName").value("测试商家"));

            verify(serviceService).findById(1);
            verify(serviceService).convertToDTO(service);
        }

        @Test
        @DisplayName("服务不存在返回404")
        void testGetServiceById_NotFound() throws Exception {
            when(serviceService.findById(999)).thenReturn(null);

            var result = performGetUnauthenticated("/api/services/{id}", 999);

            result.andExpect(status().isNotFound());

            verify(serviceService).findById(999);
            verify(serviceService, never()).convertToDTO(any());
        }

        @Test
        @DisplayName("获取不同ID的服务详情")
        void testGetServiceById_DifferentIds() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(5, merchant, "宠物寄养", new BigDecimal("299.00"));

            ServiceDTO dto = ServiceDTO.builder()
                    .id(5)
                    .name("宠物寄养")
                    .price(new BigDecimal("299.00"))
                    .merchantId(1)
                    .build();

            when(serviceService.findById(5)).thenReturn(service);
            when(serviceService.convertToDTO(service)).thenReturn(dto);

            var result = performGetUnauthenticated("/api/services/{id}", 5);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(5))
                    .andExpect(jsonPath("$.name").value("宠物寄养"));

            verify(serviceService).findById(5);
        }

        @Test
        @DisplayName("服务详情包含完整信息")
        void testGetServiceById_CompleteInfo() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "pet@paradise.com");
            Service service = TestDataFactory.createService(1, merchant);
            service.setImage("http://example.com/service.jpg");
            service.setCategory("boarding");

            ServiceDTO dto = ServiceDTO.builder()
                    .id(1)
                    .name(service.getName())
                    .description(service.getDescription())
                    .price(service.getPrice())
                    .duration(service.getDuration())
                    .merchantId(1)
                    .merchantName("宠物乐园")
                    .category("boarding")
                    .image("http://example.com/service.jpg")
                    .status("enabled")
                    .build();

            when(serviceService.findById(1)).thenReturn(service);
            when(serviceService.convertToDTO(service)).thenReturn(dto);

            var result = performGetUnauthenticated("/api/services/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.merchantName").value("宠物乐园"))
                    .andExpect(jsonPath("$.category").value("boarding"))
                    .andExpect(jsonPath("$.image").value("http://example.com/service.jpg"))
                    .andExpect(jsonPath("$.status").value("enabled"));

            verify(serviceService).findById(1);
        }
    }

    @Nested
    @DisplayName("GET /api/services/search - 搜索服务")
    class SearchServicesTests {

        @Test
        @DisplayName("成功搜索服务（有关键字匹配）")
        void testSearchServices_Success_WithResults() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service1 = TestDataFactory.createService(1, merchant, "宠物洗澡", new BigDecimal("99.00"));
            Service service2 = TestDataFactory.createService(2, merchant, "宠物洗澡套餐", new BigDecimal("149.00"));
            List<Service> services = Arrays.asList(service1, service2);

            ServiceDTO dto1 = ServiceDTO.builder().id(1).name("宠物洗澡").price(new BigDecimal("99.00")).build();
            ServiceDTO dto2 = ServiceDTO.builder().id(2).name("宠物洗澡套餐").price(new BigDecimal("149.00")).build();
            List<ServiceDTO> dtoList = Arrays.asList(dto1, dto2);

            when(serviceService.searchByKeyword("洗澡")).thenReturn(services);
            when(serviceService.convertToDTOList(services)).thenReturn(dtoList);

            var result = performGetUnauthenticated("/api/services/search?keyword=洗澡");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].name").value("宠物洗澡"))
                    .andExpect(jsonPath("$[1].name").value("宠物洗澡套餐"));

            verify(serviceService).searchByKeyword("洗澡");
            verify(serviceService).convertToDTOList(services);
        }

        @Test
        @DisplayName("搜索无结果（空结果列表）")
        void testSearchServices_EmptyResults() throws Exception {
            when(serviceService.searchByKeyword("不存在的服务")).thenReturn(Collections.emptyList());
            when(serviceService.convertToDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

            var result = performGetUnauthenticated("/api/services/search?keyword=不存在的服务");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(serviceService).searchByKeyword("不存在的服务");
            verify(serviceService).convertToDTOList(Collections.emptyList());
        }

        @Test
        @DisplayName("搜索特殊字符关键字")
        void testSearchServices_SpecialCharacters() throws Exception {
            when(serviceService.searchByKeyword("%洗澡%")).thenReturn(Collections.emptyList());
            when(serviceService.convertToDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

            var result = performGetUnauthenticated("/api/services/search?keyword=%洗澡%");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(serviceService).searchByKeyword("%洗澡%");
        }

        @Test
        @DisplayName("搜索中文字符")
        void testSearchServices_ChineseCharacters() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant, "宠物美容护理", new BigDecimal("199.00"));
            List<Service> services = Collections.singletonList(service);

            ServiceDTO dto = ServiceDTO.builder().id(1).name("宠物美容护理").price(new BigDecimal("199.00")).build();
            List<ServiceDTO> dtoList = Collections.singletonList(dto);

            when(serviceService.searchByKeyword("美容")).thenReturn(services);
            when(serviceService.convertToDTOList(services)).thenReturn(dtoList);

            var result = performGetUnauthenticated("/api/services/search?keyword=美容");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].name").value("宠物美容护理"));

            verify(serviceService).searchByKeyword("美容");
        }

        @Test
        @DisplayName("搜索英文关键字")
        void testSearchServices_EnglishKeyword() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant, "Pet Grooming", new BigDecimal("199.00"));
            List<Service> services = Collections.singletonList(service);

            ServiceDTO dto = ServiceDTO.builder().id(1).name("Pet Grooming").price(new BigDecimal("199.00")).build();
            List<ServiceDTO> dtoList = Collections.singletonList(dto);

            when(serviceService.searchByKeyword("Grooming")).thenReturn(services);
            when(serviceService.convertToDTOList(services)).thenReturn(dtoList);

            var result = performGetUnauthenticated("/api/services/search?keyword=Grooming");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1));

            verify(serviceService).searchByKeyword("Grooming");
        }

        @Test
        @DisplayName("搜索结果包含商家信息")
        void testSearchServices_WithMerchantInfo() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "宠物乐园", "pet@paradise.com");
            Service service = TestDataFactory.createService(1, merchant, "宠物洗澡", new BigDecimal("99.00"));
            List<Service> services = Collections.singletonList(service);

            ServiceDTO dto = ServiceDTO.builder()
                    .id(1)
                    .name("宠物洗澡")
                    .price(new BigDecimal("99.00"))
                    .merchantId(1)
                    .merchantName("宠物乐园")
                    .build();
            List<ServiceDTO> dtoList = Collections.singletonList(dto);

            when(serviceService.searchByKeyword("洗澡")).thenReturn(services);
            when(serviceService.convertToDTOList(services)).thenReturn(dtoList);

            var result = performGetUnauthenticated("/api/services/search?keyword=洗澡");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].merchantId").value(1))
                    .andExpect(jsonPath("$[0].merchantName").value("宠物乐园"));

            verify(serviceService).searchByKeyword("洗澡");
        }
    }

    @Nested
    @DisplayName("GET /api/services/recommended - 获取推荐服务")
    class GetRecommendedServicesTests {

        @Test
        @DisplayName("成功获取推荐服务（默认限制4条）")
        void testGetRecommendedServices_Success_DefaultLimit() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service1 = TestDataFactory.createService(1, merchant, "热门服务1", new BigDecimal("99.00"));
            Service service2 = TestDataFactory.createService(2, merchant, "热门服务2", new BigDecimal("149.00"));
            Service service3 = TestDataFactory.createService(3, merchant, "热门服务3", new BigDecimal("199.00"));
            Service service4 = TestDataFactory.createService(4, merchant, "热门服务4", new BigDecimal("249.00"));
            List<Service> services = Arrays.asList(service1, service2, service3, service4);

            ServiceDTO dto1 = ServiceDTO.builder().id(1).name("热门服务1").price(new BigDecimal("99.00")).build();
            ServiceDTO dto2 = ServiceDTO.builder().id(2).name("热门服务2").price(new BigDecimal("149.00")).build();
            ServiceDTO dto3 = ServiceDTO.builder().id(3).name("热门服务3").price(new BigDecimal("199.00")).build();
            ServiceDTO dto4 = ServiceDTO.builder().id(4).name("热门服务4").price(new BigDecimal("249.00")).build();
            List<ServiceDTO> dtoList = Arrays.asList(dto1, dto2, dto3, dto4);

            when(serviceService.findRecommended(4)).thenReturn(services);
            when(serviceService.convertToDTOList(services)).thenReturn(dtoList);

            var result = performGetUnauthenticated("/api/services/recommended");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(4))
                    .andExpect(jsonPath("$[0].name").value("热门服务1"))
                    .andExpect(jsonPath("$[3].name").value("热门服务4"));

            verify(serviceService).findRecommended(4);
            verify(serviceService).convertToDTOList(services);
        }

        @Test
        @DisplayName("成功获取推荐服务（自定义限制数量）")
        void testGetRecommendedServices_Success_CustomLimit() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service1 = TestDataFactory.createService(1, merchant, "热门服务1", new BigDecimal("99.00"));
            Service service2 = TestDataFactory.createService(2, merchant, "热门服务2", new BigDecimal("149.00"));
            List<Service> services = Arrays.asList(service1, service2);

            ServiceDTO dto1 = ServiceDTO.builder().id(1).name("热门服务1").price(new BigDecimal("99.00")).build();
            ServiceDTO dto2 = ServiceDTO.builder().id(2).name("热门服务2").price(new BigDecimal("149.00")).build();
            List<ServiceDTO> dtoList = Arrays.asList(dto1, dto2);

            when(serviceService.findRecommended(2)).thenReturn(services);
            when(serviceService.convertToDTOList(services)).thenReturn(dtoList);

            var result = performGetUnauthenticated("/api/services/recommended?limit=2");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2));

            verify(serviceService).findRecommended(2);
            verify(serviceService).convertToDTOList(services);
        }

        @Test
        @DisplayName("获取推荐服务（限制数量为1）")
        void testGetRecommendedServices_LimitOne() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service = TestDataFactory.createService(1, merchant, "最热门服务", new BigDecimal("99.00"));
            List<Service> services = Collections.singletonList(service);

            ServiceDTO dto = ServiceDTO.builder().id(1).name("最热门服务").price(new BigDecimal("99.00")).build();
            List<ServiceDTO> dtoList = Collections.singletonList(dto);

            when(serviceService.findRecommended(1)).thenReturn(services);
            when(serviceService.convertToDTOList(services)).thenReturn(dtoList);

            var result = performGetUnauthenticated("/api/services/recommended?limit=1");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].name").value("最热门服务"));

            verify(serviceService).findRecommended(1);
        }

        @Test
        @DisplayName("获取推荐服务（限制数量为10）")
        void testGetRecommendedServices_LimitTen() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1);
            Service service1 = TestDataFactory.createService(1, merchant, "服务1", new BigDecimal("99.00"));
            Service service2 = TestDataFactory.createService(2, merchant, "服务2", new BigDecimal("99.00"));
            Service service3 = TestDataFactory.createService(3, merchant, "服务3", new BigDecimal("99.00"));
            List<Service> services = Arrays.asList(service1, service2, service3);

            ServiceDTO dto1 = ServiceDTO.builder().id(1).name("服务1").build();
            ServiceDTO dto2 = ServiceDTO.builder().id(2).name("服务2").build();
            ServiceDTO dto3 = ServiceDTO.builder().id(3).name("服务3").build();
            List<ServiceDTO> dtoList = Arrays.asList(dto1, dto2, dto3);

            when(serviceService.findRecommended(10)).thenReturn(services);
            when(serviceService.convertToDTOList(services)).thenReturn(dtoList);

            var result = performGetUnauthenticated("/api/services/recommended?limit=10");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(3));

            verify(serviceService).findRecommended(10);
        }

        @Test
        @DisplayName("获取推荐服务为空列表")
        void testGetRecommendedServices_EmptyList() throws Exception {
            when(serviceService.findRecommended(anyInt())).thenReturn(Collections.emptyList());
            when(serviceService.convertToDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

            var result = performGetUnauthenticated("/api/services/recommended?limit=5");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(serviceService).findRecommended(5);
            verify(serviceService).convertToDTOList(Collections.emptyList());
        }

        @Test
        @DisplayName("推荐服务包含完整信息")
        void testGetRecommendedServices_CompleteInfo() throws Exception {
            Merchant merchant = TestDataFactory.createMerchant(1, "推荐商家", "recommend@test.com");
            Service service = TestDataFactory.createService(1, merchant, "推荐服务", "高品质服务", new BigDecimal("299.00"), 120);
            service.setCategory("premium");
            service.setImage("http://example.com/recommended.jpg");
            List<Service> services = Collections.singletonList(service);

            ServiceDTO dto = ServiceDTO.builder()
                    .id(1)
                    .name("推荐服务")
                    .description("高品质服务")
                    .price(new BigDecimal("299.00"))
                    .duration(120)
                    .merchantId(1)
                    .merchantName("推荐商家")
                    .category("premium")
                    .image("http://example.com/recommended.jpg")
                    .status("enabled")
                    .build();
            List<ServiceDTO> dtoList = Collections.singletonList(dto);

            when(serviceService.findRecommended(4)).thenReturn(services);
            when(serviceService.convertToDTOList(services)).thenReturn(dtoList);

            var result = performGetUnauthenticated("/api/services/recommended");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("推荐服务"))
                    .andExpect(jsonPath("$[0].description").value("高品质服务"))
                    .andExpect(jsonPath("$[0].price").value(299.00))
                    .andExpect(jsonPath("$[0].duration").value(120))
                    .andExpect(jsonPath("$[0].merchantId").value(1))
                    .andExpect(jsonPath("$[0].merchantName").value("推荐商家"))
                    .andExpect(jsonPath("$[0].category").value("premium"))
                    .andExpect(jsonPath("$[0].image").value("http://example.com/recommended.jpg"));

            verify(serviceService).findRecommended(4);
        }

        @Test
        @DisplayName("推荐服务来自不同商家")
        void testGetRecommendedServices_DifferentMerchants() throws Exception {
            Merchant merchant1 = TestDataFactory.createMerchant(1, "商家A", "a@test.com");
            Merchant merchant2 = TestDataFactory.createMerchant(2, "商家B", "b@test.com");
            Service service1 = TestDataFactory.createService(1, merchant1, "服务A", new BigDecimal("99.00"));
            Service service2 = TestDataFactory.createService(2, merchant2, "服务B", new BigDecimal("149.00"));
            List<Service> services = Arrays.asList(service1, service2);

            ServiceDTO dto1 = ServiceDTO.builder().id(1).name("服务A").merchantId(1).merchantName("商家A").build();
            ServiceDTO dto2 = ServiceDTO.builder().id(2).name("服务B").merchantId(2).merchantName("商家B").build();
            List<ServiceDTO> dtoList = Arrays.asList(dto1, dto2);

            when(serviceService.findRecommended(4)).thenReturn(services);
            when(serviceService.convertToDTOList(services)).thenReturn(dtoList);

            var result = performGetUnauthenticated("/api/services/recommended");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].merchantName").value("商家A"))
                    .andExpect(jsonPath("$[1].merchantName").value("商家B"));

            verify(serviceService).findRecommended(4);
        }
    }
}
