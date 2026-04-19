package com.petshop.service;

import com.petshop.dto.AnnouncementDTO;
import com.petshop.entity.Announcement;
import com.petshop.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementService {
    @Autowired
    private AnnouncementRepository announcementRepository;

    public List<AnnouncementDTO> findAllPublished() {
        List<Announcement> announcements = announcementRepository.findByStatus("published");
        return announcements.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AnnouncementDTO findById(Integer id) {
        return announcementRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }
    
    public Announcement findEntityById(Integer id) {
        return announcementRepository.findById(id).orElse(null);
    }
    
    public Page<Announcement> findAll(Pageable pageable) {
        return announcementRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
    
    public Page<Announcement> findByStatus(String status, Pageable pageable) {
        return announcementRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }
    
    @Transactional
    public Announcement create(Announcement announcement) {
        if (announcement.getStatus() == null || announcement.getStatus().isEmpty()) {
            announcement.setStatus("draft");
        }
        return announcementRepository.save(announcement);
    }
    
    @Transactional
    public Announcement update(Integer id, Announcement announcement) {
        Announcement existingAnnouncement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found with id: " + id));
        
        existingAnnouncement.setTitle(announcement.getTitle());
        existingAnnouncement.setContent(announcement.getContent());
        
        return announcementRepository.save(existingAnnouncement);
    }
    
    @Transactional
    public Announcement publish(Integer id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found with id: " + id));
        
        announcement.setStatus("published");
        return announcementRepository.save(announcement);
    }
    
    @Transactional
    public Announcement unpublish(Integer id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found with id: " + id));
        
        announcement.setStatus("draft");
        return announcementRepository.save(announcement);
    }
    
    @Transactional
    public void delete(Integer id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found with id: " + id));
        announcementRepository.delete(announcement);
    }
    
    @Transactional
    public int batchPublish(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Announcement IDs cannot be empty");
        }
        return announcementRepository.batchUpdateStatus(ids, "published");
    }
    
    @Transactional
    public int batchUnpublish(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Announcement IDs cannot be empty");
        }
        return announcementRepository.batchUpdateStatus(ids, "draft");
    }
    
    @Transactional
    public int batchDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Announcement IDs cannot be empty");
        }
        return announcementRepository.batchDelete(ids);
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
