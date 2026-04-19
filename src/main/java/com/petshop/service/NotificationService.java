package com.petshop.service;

import com.petshop.dto.NotificationDTO;
import com.petshop.entity.Notification;
import com.petshop.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public List<NotificationDTO> findByUserId(Integer userId, String type, Boolean isRead) {
        List<Notification> notifications;

        if (type != null && isRead != null) {
            notifications = notificationRepository.findByUserIdAndTypeAndIsReadOrderByCreatedAtDesc(userId, type, isRead);
        } else if (type != null) {
            notifications = notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type);
        } else if (isRead != null) {
            notifications = notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId, isRead);
        } else {
            notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }

        return notifications.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Notification findById(Integer id) {
        return notificationRepository.findById(id).orElse(null);
    }

    public boolean isOwner(Integer notificationId, Integer userId) {
        Notification notification = findById(notificationId);
        return notification != null && notification.getUserId().equals(userId);
    }

    @Transactional
    public void markAsRead(Integer id) {
        Notification notification = notificationRepository.findById(id).orElse(null);
        if (notification != null) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void markAllAsRead(Integer userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Transactional
    public void markBatchAsRead(List<Integer> ids, Integer userId) {
        if (ids != null && !ids.isEmpty()) {
            notificationRepository.markAsReadByIds(ids, userId);
        }
    }

    @Transactional
    public void deleteNotification(Integer id) {
        notificationRepository.deleteById(id);
    }

    @Transactional
    public void deleteBatch(List<Integer> ids, Integer userId) {
        if (ids != null && !ids.isEmpty()) {
            notificationRepository.deleteByUserIdAndIdIn(userId, ids);
        }
    }

    public long getUnreadCount(Integer userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    public Map<String, Object> getUnreadCountResponse(Integer userId) {
        long count = getUnreadCount(userId);
        return Map.of("count", count);
    }

    private NotificationDTO toDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .summary(notification.getSummary())
                .content(notification.getContent())
                .isRead(notification.getIsRead())
                .createTime(notification.getCreatedAt())
                .build();
    }
}
