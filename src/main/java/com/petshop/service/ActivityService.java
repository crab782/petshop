package com.petshop.service;

import com.petshop.entity.Activity;
import com.petshop.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    public Activity findById(Integer id) {
        return activityRepository.findById(id).orElse(null);
    }

    public Page<Activity> findAll(Pageable pageable) {
        return activityRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Page<Activity> searchActivities(String keyword, String type, String status, 
                                           LocalDateTime startDate, LocalDateTime endDate, 
                                           Pageable pageable) {
        return activityRepository.searchActivities(keyword, type, status, startDate, endDate, pageable);
    }

    @Transactional
    public Activity create(Activity activity) {
        if (activity.getStatus() == null || activity.getStatus().isEmpty()) {
            activity.setStatus("enabled");
        }
        return activityRepository.save(activity);
    }

    @Transactional
    public Activity update(Integer id, Activity activity) {
        Activity existingActivity = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found with id: " + id));
        
        existingActivity.setName(activity.getName());
        existingActivity.setType(activity.getType());
        existingActivity.setDescription(activity.getDescription());
        existingActivity.setStartTime(activity.getStartTime());
        existingActivity.setEndTime(activity.getEndTime());
        
        return activityRepository.save(existingActivity);
    }

    @Transactional
    public Activity updateStatus(Integer id, String status) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found with id: " + id));
        
        if (!"enabled".equals(status) && !"disabled".equals(status)) {
            throw new IllegalArgumentException("Invalid status. Must be 'enabled' or 'disabled'");
        }
        
        activity.setStatus(status);
        return activityRepository.save(activity);
    }

    @Transactional
    public void delete(Integer id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found with id: " + id));
        activityRepository.delete(activity);
    }

    @Transactional
    public int batchUpdateStatus(List<Integer> ids, String status) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Activity IDs cannot be empty");
        }
        if (!"enabled".equals(status) && !"disabled".equals(status)) {
            throw new IllegalArgumentException("Invalid status. Must be 'enabled' or 'disabled'");
        }
        return activityRepository.batchUpdateStatus(ids, status);
    }

    @Transactional
    public int batchDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Activity IDs cannot be empty");
        }
        return activityRepository.batchDelete(ids);
    }
}
