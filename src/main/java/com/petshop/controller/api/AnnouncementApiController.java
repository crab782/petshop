package com.petshop.controller.api;

import com.petshop.dto.AnnouncementDTO;
import com.petshop.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@Tag(name = "公告API", description = "公告相关接口")
public class AnnouncementApiController {
    @Autowired
    private AnnouncementService announcementService;

    @Operation(summary = "获取公告列表", description = "获取所有已发布的公告列表")
    @GetMapping
    public ResponseEntity<List<AnnouncementDTO>> getAnnouncements() {
        List<AnnouncementDTO> announcements = announcementService.findAllPublished();
        return ResponseEntity.ok(announcements);
    }

    @Operation(summary = "获取公告详情", description = "根据ID获取指定公告的详细信息")
    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementDTO> getAnnouncement(@PathVariable Integer id) {
        AnnouncementDTO announcement = announcementService.findById(id);
        if (announcement == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(announcement);
    }
}
