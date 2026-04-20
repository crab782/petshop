package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.dto.AnnouncementDTO;
import com.petshop.entity.Announcement;
import com.petshop.mapper.AnnouncementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementService {
    @Autowired
    private AnnouncementMapper announcementMapper;

    public List<AnnouncementDTO> findAllPublished() {
        List<Announcement> announcements = announcementMapper.selectList(
                new LambdaQueryWrapper<Announcement>().eq(Announcement::getStatus, "published"));
        return announcements.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AnnouncementDTO findById(Integer id) {
        Announcement announcement = announcementMapper.selectById(id);
        return announcement != null ? toDTO(announcement) : null;
    }
    
    public Announcement findEntityById(Integer id) {
        return announcementMapper.selectById(id);
    }
    
    public org.springframework.data.domain.Page<Announcement> findAll(org.springframework.data.domain.Pageable pageable) {
        Page<Announcement> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        Page<Announcement> result = announcementMapper.selectPage(page, new LambdaQueryWrapper<Announcement>()
                .orderByDesc(Announcement::getCreatedAt));
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }
    
    public org.springframework.data.domain.Page<Announcement> findByStatus(String status, org.springframework.data.domain.Pageable pageable) {
        Page<Announcement> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        Page<Announcement> result = announcementMapper.selectPage(page, new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getStatus, status)
                .orderByDesc(Announcement::getCreatedAt));
        return new PageImpl<>(result.getRecords(), pageable, result.getTotal());
    }
    
    @Transactional
    public Announcement create(Announcement announcement) {
        if (announcement.getStatus() == null || announcement.getStatus().isEmpty()) {
            announcement.setStatus("draft");
        }
        announcementMapper.insert(announcement);
        return announcement;
    }
    
    @Transactional
    public Announcement update(Integer id, Announcement announcement) {
        Announcement existingAnnouncement = announcementMapper.selectById(id);
        if (existingAnnouncement == null) {
            throw new IllegalArgumentException("Announcement not found with id: " + id);
        }
        
        existingAnnouncement.setTitle(announcement.getTitle());
        existingAnnouncement.setContent(announcement.getContent());
        
        announcementMapper.updateById(existingAnnouncement);
        return existingAnnouncement;
    }
    
    @Transactional
    public Announcement publish(Integer id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new IllegalArgumentException("Announcement not found with id: " + id);
        }
        
        announcement.setStatus("published");
        announcementMapper.updateById(announcement);
        return announcement;
    }
    
    @Transactional
    public Announcement unpublish(Integer id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new IllegalArgumentException("Announcement not found with id: " + id);
        }
        
        announcement.setStatus("draft");
        announcementMapper.updateById(announcement);
        return announcement;
    }
    
    @Transactional
    public void delete(Integer id) {
        Announcement announcement = announcementMapper.selectById(id);
        if (announcement == null) {
            throw new IllegalArgumentException("Announcement not found with id: " + id);
        }
        announcementMapper.deleteById(id);
    }
    
    @Transactional
    public int batchPublish(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Announcement IDs cannot be empty");
        }
        int count = 0;
        for (Integer id : ids) {
            Announcement announcement = announcementMapper.selectById(id);
            if (announcement != null) {
                announcement.setStatus("published");
                announcementMapper.updateById(announcement);
                count++;
            }
        }
        return count;
    }
    
    @Transactional
    public int batchUnpublish(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Announcement IDs cannot be empty");
        }
        int count = 0;
        for (Integer id : ids) {
            Announcement announcement = announcementMapper.selectById(id);
            if (announcement != null) {
                announcement.setStatus("draft");
                announcementMapper.updateById(announcement);
                count++;
            }
        }
        return count;
    }
    
    @Transactional
    public int batchDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Announcement IDs cannot be empty");
        }
        int count = 0;
        for (Integer id : ids) {
            if (announcementMapper.selectById(id) != null) {
                announcementMapper.deleteById(id);
                count++;
            }
        }
        return count;
    }

    private AnnouncementDTO toDTO(Announcement announcement) {
        return AnnouncementDTO.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .status(announcement.getStatus())
                .publishTime(announcement.getCreatedAt())
                .build();
    }
}
