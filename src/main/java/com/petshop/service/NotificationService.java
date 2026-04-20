package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.dto.NotificationDTO;
import com.petshop.entity.Notification;
import com.petshop.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;

    public List<NotificationDTO> findByUserId(Integer userId, String type, Boolean isRead) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreatedAt);
        
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Notification::getType, type);
        }
        if (isRead != null) {
            wrapper.eq(Notification::getIsRead, isRead);
        }
        
        List<Notification> notifications = notificationMapper.selectList(wrapper);
        return notifications.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Notification findById(Integer id) {
        return notificationMapper.selectById(id);
    }

    public boolean isOwner(Integer notificationId, Integer userId) {
        Notification notification = findById(notificationId);
        return notification != null && notification.getUserId().equals(userId);
    }

    @Transactional
    public void markAsRead(Integer id) {
        Notification notification = notificationMapper.selectById(id);
        if (notification != null) {
            notification.setIsRead(true);
            notificationMapper.updateById(notification);
        }
    }

    @Transactional
    public void markAllAsRead(Integer userId) {
        notificationMapper.markAllAsReadByUserId(userId);
    }

    @Transactional
    public void markBatchAsRead(List<Integer> ids, Integer userId) {
        if (ids != null && !ids.isEmpty()) {
            notificationMapper.markAsReadByIds(ids, userId);
        }
    }

    @Transactional
    public void deleteNotification(Integer id) {
        notificationMapper.deleteById(id);
    }

    @Transactional
    public void deleteBatch(List<Integer> ids, Integer userId) {
        if (ids != null && !ids.isEmpty()) {
            for (Integer id : ids) {
                Notification notification = notificationMapper.selectById(id);
                if (notification != null && notification.getUserId().equals(userId)) {
                    notificationMapper.deleteById(id);
                }
            }
        }
    }

    public long getUnreadCount(Integer userId) {
        return notificationMapper.selectCount(new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, false));
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
