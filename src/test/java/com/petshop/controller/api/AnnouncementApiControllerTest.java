package com.petshop.controller.api;

import com.petshop.dto.AnnouncementDTO;
import com.petshop.service.AnnouncementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("公告API测试")
public class AnnouncementApiControllerTest extends UserApiControllerTestBase {

    private AnnouncementApiController controller;

    @Mock
    private AnnouncementService announcementService;

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        controller = new AnnouncementApiController();
        injectMocks();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private void injectMocks() {
        try {
            var announcementServiceField = AnnouncementApiController.class.getDeclaredField("announcementService");
            announcementServiceField.setAccessible(true);
            announcementServiceField.set(controller, announcementService);
        } catch (Exception e) {
            throw new RuntimeException("注入mock失败", e);
        }
    }

    @Nested
    @DisplayName("获取公告列表测试")
    class GetAnnouncementsTests {

        @Test
        @DisplayName("成功获取公告列表")
        void testGetAnnouncements_Success() throws Exception {
            List<AnnouncementDTO> announcements = createTestAnnouncementDTOs(3);
            when(announcementService.findAllPublished()).thenReturn(announcements);

            var result = performGetUnauthenticated("/api/announcements");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].id").value(3))
                    .andExpect(jsonPath("$[0].title").value("公告标题3"))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[2].id").value(1));

            verify(announcementService).findAllPublished();
        }

        @Test
        @DisplayName("公告列表按创建时间降序排列")
        void testGetAnnouncements_OrderedByDateDesc() throws Exception {
            List<AnnouncementDTO> announcements = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            
            AnnouncementDTO dto1 = AnnouncementDTO.builder()
                    .id(1)
                    .title("旧公告")
                    .content("旧公告内容")
                    .status("published")
                    .publishTime(now.minusDays(2))
                    .build();
            
            AnnouncementDTO dto2 = AnnouncementDTO.builder()
                    .id(2)
                    .title("新公告")
                    .content("新公告内容")
                    .status("published")
                    .publishTime(now)
                    .build();
            
            AnnouncementDTO dto3 = AnnouncementDTO.builder()
                    .id(3)
                    .title("最新公告")
                    .content("最新公告内容")
                    .status("published")
                    .publishTime(now.plusDays(1))
                    .build();

            announcements.add(dto3);
            announcements.add(dto2);
            announcements.add(dto1);

            when(announcementService.findAllPublished()).thenReturn(announcements);

            var result = performGetUnauthenticated("/api/announcements");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(3))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[2].id").value(1));

            verify(announcementService).findAllPublished();
        }

        @Test
        @DisplayName("空公告列表")
        void testGetAnnouncements_EmptyList() throws Exception {
            when(announcementService.findAllPublished()).thenReturn(Collections.emptyList());

            var result = performGetUnauthenticated("/api/announcements");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(announcementService).findAllPublished();
        }

        @Test
        @DisplayName("公告列表包含所有必要字段")
        void testGetAnnouncements_ContainsAllFields() throws Exception {
            LocalDateTime publishTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
            AnnouncementDTO dto = AnnouncementDTO.builder()
                    .id(1)
                    .title("测试公告标题")
                    .content("测试公告内容，这是一段详细的公告内容。")
                    .status("published")
                    .publishTime(publishTime)
                    .build();

            when(announcementService.findAllPublished()).thenReturn(List.of(dto));

            var result = performGetUnauthenticated("/api/announcements");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].title").value("测试公告标题"))
                    .andExpect(jsonPath("$[0].content").value("测试公告内容，这是一段详细的公告内容。"))
                    .andExpect(jsonPath("$[0].status").value("published"))
                    .andExpect(jsonPath("$[0].publishTime").exists());

            verify(announcementService).findAllPublished();
        }

        @Test
        @DisplayName("服务层异常返回空列表")
        void testGetAnnouncements_ServiceException() throws Exception {
            when(announcementService.findAllPublished()).thenThrow(new RuntimeException("数据库连接失败"));

            var result = performGetUnauthenticated("/api/announcements");

            result.andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("公告列表无需认证即可访问")
        void testGetAnnouncements_PublicEndpoint() throws Exception {
            List<AnnouncementDTO> announcements = createTestAnnouncementDTOs(1);
            when(announcementService.findAllPublished()).thenReturn(announcements);

            var result = mockMvc.perform(get("/api/announcements")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }
    }

    @Nested
    @DisplayName("获取公告详情测试")
    class GetAnnouncementByIdTests {

        @Test
        @DisplayName("成功获取公告详情")
        void testGetAnnouncementById_Success() throws Exception {
            LocalDateTime publishTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
            AnnouncementDTO dto = AnnouncementDTO.builder()
                    .id(1)
                    .title("测试公告标题")
                    .content("测试公告内容，这是一段详细的公告内容。包含多行文字和详细说明。")
                    .status("published")
                    .publishTime(publishTime)
                    .build();

            when(announcementService.findById(1)).thenReturn(dto);

            var result = performGetUnauthenticated("/api/announcements/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("测试公告标题"))
                    .andExpect(jsonPath("$.content").value("测试公告内容，这是一段详细的公告内容。包含多行文字和详细说明。"))
                    .andExpect(jsonPath("$.status").value("published"))
                    .andExpect(jsonPath("$.publishTime").exists());

            verify(announcementService).findById(1);
        }

        @Test
        @DisplayName("公告不存在返回404")
        void testGetAnnouncementById_NotFound() throws Exception {
            when(announcementService.findById(999)).thenReturn(null);

            var result = performGetUnauthenticated("/api/announcements/{id}", 999);

            result.andExpect(status().isNotFound());

            verify(announcementService).findById(999);
        }

        @Test
        @DisplayName("公告详情无需认证即可访问")
        void testGetAnnouncementById_PublicEndpoint() throws Exception {
            AnnouncementDTO dto = createTestAnnouncementDTO(1);
            when(announcementService.findById(1)).thenReturn(dto);

            var result = mockMvc.perform(get("/api/announcements/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("公告ID为1时成功获取")
        void testGetAnnouncementById_FirstId() throws Exception {
            AnnouncementDTO dto = createTestAnnouncementDTO(1);
            when(announcementService.findById(1)).thenReturn(dto);

            var result = performGetUnauthenticated("/api/announcements/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));

            verify(announcementService).findById(1);
        }

        @Test
        @DisplayName("公告ID为负数时返回404")
        void testGetAnnouncementById_NegativeId() throws Exception {
            when(announcementService.findById(-1)).thenReturn(null);

            var result = performGetUnauthenticated("/api/announcements/{id}", -1);

            result.andExpect(status().isNotFound());

            verify(announcementService).findById(-1);
        }

        @Test
        @DisplayName("公告ID为0时返回404")
        void testGetAnnouncementById_ZeroId() throws Exception {
            when(announcementService.findById(0)).thenReturn(null);

            var result = performGetUnauthenticated("/api/announcements/{id}", 0);

            result.andExpect(status().isNotFound());

            verify(announcementService).findById(0);
        }

        @Test
        @DisplayName("公告详情包含长内容")
        void testGetAnnouncementById_LongContent() throws Exception {
            String longContent = "这是一段很长的公告内容。".repeat(100);
            AnnouncementDTO dto = AnnouncementDTO.builder()
                    .id(1)
                    .title("长内容公告")
                    .content(longContent)
                    .status("published")
                    .publishTime(LocalDateTime.now())
                    .build();

            when(announcementService.findById(1)).thenReturn(dto);

            var result = performGetUnauthenticated("/api/announcements/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").value(longContent));

            verify(announcementService).findById(1);
        }

        @Test
        @DisplayName("公告详情包含特殊字符标题")
        void testGetAnnouncementById_SpecialCharactersInTitle() throws Exception {
            AnnouncementDTO dto = AnnouncementDTO.builder()
                    .id(1)
                    .title("公告标题 - 包含特殊字符！@#$%^&*()")
                    .content("公告内容")
                    .status("published")
                    .publishTime(LocalDateTime.now())
                    .build();

            when(announcementService.findById(1)).thenReturn(dto);

            var result = performGetUnauthenticated("/api/announcements/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("公告标题 - 包含特殊字符！@#$%^&*()"));

            verify(announcementService).findById(1);
        }

        @Test
        @DisplayName("服务层异常返回500")
        void testGetAnnouncementById_ServiceException() throws Exception {
            when(announcementService.findById(1)).thenThrow(new RuntimeException("数据库连接失败"));

            var result = performGetUnauthenticated("/api/announcements/{id}", 1);

            result.andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryTests {

        @Test
        @DisplayName("公告列表包含大量数据")
        void testGetAnnouncements_LargeList() throws Exception {
            List<AnnouncementDTO> announcements = createTestAnnouncementDTOs(100);
            when(announcementService.findAllPublished()).thenReturn(announcements);

            var result = performGetUnauthenticated("/api/announcements");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(100));

            verify(announcementService).findAllPublished();
        }

        @Test
        @DisplayName("公告标题为空字符串")
        void testGetAnnouncementById_EmptyTitle() throws Exception {
            AnnouncementDTO dto = AnnouncementDTO.builder()
                    .id(1)
                    .title("")
                    .content("公告内容")
                    .status("published")
                    .publishTime(LocalDateTime.now())
                    .build();

            when(announcementService.findById(1)).thenReturn(dto);

            var result = performGetUnauthenticated("/api/announcements/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value(""));

            verify(announcementService).findById(1);
        }

        @Test
        @DisplayName("公告内容为空字符串")
        void testGetAnnouncementById_EmptyContent() throws Exception {
            AnnouncementDTO dto = AnnouncementDTO.builder()
                    .id(1)
                    .title("公告标题")
                    .content("")
                    .status("published")
                    .publishTime(LocalDateTime.now())
                    .build();

            when(announcementService.findById(1)).thenReturn(dto);

            var result = performGetUnauthenticated("/api/announcements/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").value(""));

            verify(announcementService).findById(1);
        }

        @Test
        @DisplayName("公告发布时间为未来时间")
        void testGetAnnouncementById_FuturePublishTime() throws Exception {
            LocalDateTime futureTime = LocalDateTime.now().plusDays(30);
            AnnouncementDTO dto = AnnouncementDTO.builder()
                    .id(1)
                    .title("未来公告")
                    .content("这是一个未来发布的公告")
                    .status("published")
                    .publishTime(futureTime)
                    .build();

            when(announcementService.findById(1)).thenReturn(dto);

            var result = performGetUnauthenticated("/api/announcements/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.publishTime").exists());

            verify(announcementService).findById(1);
        }

        @Test
        @DisplayName("公告发布时间为过去时间")
        void testGetAnnouncementById_PastPublishTime() throws Exception {
            LocalDateTime pastTime = LocalDateTime.now().minusYears(1);
            AnnouncementDTO dto = AnnouncementDTO.builder()
                    .id(1)
                    .title("历史公告")
                    .content("这是一个历史公告")
                    .status("published")
                    .publishTime(pastTime)
                    .build();

            when(announcementService.findById(1)).thenReturn(dto);

            var result = performGetUnauthenticated("/api/announcements/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.publishTime").exists());

            verify(announcementService).findById(1);
        }

        @Test
        @DisplayName("大ID值获取公告")
        void testGetAnnouncementById_LargeId() throws Exception {
            Integer largeId = Integer.MAX_VALUE;
            when(announcementService.findById(largeId)).thenReturn(null);

            var result = performGetUnauthenticated("/api/announcements/{id}", largeId);

            result.andExpect(status().isNotFound());

            verify(announcementService).findById(largeId);
        }

        @Test
        @DisplayName("公告内容包含HTML标签")
        void testGetAnnouncementById_HtmlContent() throws Exception {
            String htmlContent = "<h1>公告标题</h1><p>公告内容</p><ul><li>项目1</li><li>项目2</li></ul>";
            AnnouncementDTO dto = AnnouncementDTO.builder()
                    .id(1)
                    .title("HTML公告")
                    .content(htmlContent)
                    .status("published")
                    .publishTime(LocalDateTime.now())
                    .build();

            when(announcementService.findById(1)).thenReturn(dto);

            var result = performGetUnauthenticated("/api/announcements/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").value(htmlContent));

            verify(announcementService).findById(1);
        }

        @Test
        @DisplayName("公告内容包含换行符")
        void testGetAnnouncementById_MultilineContent() throws Exception {
            String multilineContent = "第一行内容\n第二行内容\n第三行内容";
            AnnouncementDTO dto = AnnouncementDTO.builder()
                    .id(1)
                    .title("多行公告")
                    .content(multilineContent)
                    .status("published")
                    .publishTime(LocalDateTime.now())
                    .build();

            when(announcementService.findById(1)).thenReturn(dto);

            var result = performGetUnauthenticated("/api/announcements/{id}", 1);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").value(multilineContent));

            verify(announcementService).findById(1);
        }
    }

    @Nested
    @DisplayName("数据完整性测试")
    class DataIntegrityTests {

        @Test
        @DisplayName("公告列表中每个公告都有完整字段")
        void testGetAnnouncements_AllFieldsPresent() throws Exception {
            LocalDateTime now = LocalDateTime.now();
            List<AnnouncementDTO> announcements = new ArrayList<>();
            
            for (int i = 1; i <= 3; i++) {
                announcements.add(AnnouncementDTO.builder()
                        .id(i)
                        .title("公告标题" + i)
                        .content("公告内容" + i)
                        .status("published")
                        .publishTime(now.minusDays(i))
                        .build());
            }

            when(announcementService.findAllPublished()).thenReturn(announcements);

            var result = performGetUnauthenticated("/api/announcements");

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").exists())
                    .andExpect(jsonPath("$[0].title").exists())
                    .andExpect(jsonPath("$[0].content").exists())
                    .andExpect(jsonPath("$[0].status").exists())
                    .andExpect(jsonPath("$[0].publishTime").exists())
                    .andExpect(jsonPath("$[1].id").exists())
                    .andExpect(jsonPath("$[1].title").exists())
                    .andExpect(jsonPath("$[1].content").exists())
                    .andExpect(jsonPath("$[1].status").exists())
                    .andExpect(jsonPath("$[1].publishTime").exists())
                    .andExpect(jsonPath("$[2].id").exists())
                    .andExpect(jsonPath("$[2].title").exists())
                    .andExpect(jsonPath("$[2].content").exists())
                    .andExpect(jsonPath("$[2].status").exists())
                    .andExpect(jsonPath("$[2].publishTime").exists());

            verify(announcementService).findAllPublished();
        }

        @Test
        @DisplayName("公告详情所有字段都有值")
        void testGetAnnouncementById_AllFieldsHaveValues() throws Exception {
            LocalDateTime publishTime = LocalDateTime.of(2024, 6, 15, 14, 30, 0);
            AnnouncementDTO dto = AnnouncementDTO.builder()
                    .id(100)
                    .title("完整公告标题")
                    .content("完整公告内容，包含所有必要信息。")
                    .status("published")
                    .publishTime(publishTime)
                    .build();

            when(announcementService.findById(100)).thenReturn(dto);

            var result = performGetUnauthenticated("/api/announcements/{id}", 100);

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(100))
                    .andExpect(jsonPath("$.title").value("完整公告标题"))
                    .andExpect(jsonPath("$.content").value("完整公告内容，包含所有必要信息。"))
                    .andExpect(jsonPath("$.status").value("published"))
                    .andExpect(jsonPath("$.publishTime").exists());

            verify(announcementService).findById(100);
        }

        @Test
        @DisplayName("多次请求同一公告返回相同结果")
        void testGetAnnouncementById_ConsistentResults() throws Exception {
            AnnouncementDTO dto = createTestAnnouncementDTO(1);
            when(announcementService.findById(1)).thenReturn(dto);

            for (int i = 0; i < 3; i++) {
                var result = performGetUnauthenticated("/api/announcements/{id}", 1);
                result.andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.title").value("公告标题1"));
            }

            verify(announcementService, times(3)).findById(1);
        }
    }

    private List<AnnouncementDTO> createTestAnnouncementDTOs(int count) {
        List<AnnouncementDTO> dtos = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now();
        
        for (int i = 1; i <= count; i++) {
            dtos.add(AnnouncementDTO.builder()
                    .id(i)
                    .title("公告标题" + i)
                    .content("公告内容" + i)
                    .status("published")
                    .publishTime(baseTime.minusDays(count - i))
                    .build());
        }
        
        Collections.reverse(dtos);
        return dtos;
    }

    private AnnouncementDTO createTestAnnouncementDTO(Integer id) {
        return AnnouncementDTO.builder()
                .id(id)
                .title("公告标题" + id)
                .content("公告内容" + id)
                .status("published")
                .publishTime(LocalDateTime.now())
                .build();
    }
}
