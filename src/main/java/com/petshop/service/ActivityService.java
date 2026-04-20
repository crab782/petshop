package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.entity.Activity;
import com.petshop.exception.BadRequestException;
import com.petshop.mapper.ActivityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityService {
    @Autowired
    private ActivityMapper activityRepository;

    public Activity findById(Integer id) {
        return activityRepository.selectById(id);
    }

    @Transactional
    public Activity create(Activity activity) {
        if (activity.getStatus() == null || activity.getStatus().isEmpty()) {
            activity.setStatus("enabled");
        }
        activityRepository.insert(activity);
        return activity;
    }

    @Transactional
    public Activity update(Integer id, Activity activity) {
        Activity existingActivity = activityRepository.selectById(id);
        if (existingActivity == null) {
            throw new BadRequestException("Activity not found with id: " + id);
        }
        
        existingActivity.setName(activity.getName());
        existingActivity.setType(activity.getType());
        existingActivity.setDescription(activity.getDescription());
        existingActivity.setStartTime(activity.getStartTime());
        existingActivity.setEndTime(activity.getEndTime());
        
        activityRepository.updateById(existingActivity);
        return existingActivity;
    }

    @Transactional
    public Activity updateStatus(Integer id, String status) {
        Activity activity = activityRepository.selectById(id);
        if (activity == null) {
            throw new BadRequestException("Activity not found with id: " + id);
        }
        
        if (!"enabled".equals(status) && !"disabled".equals(status)) {
            throw new BadRequestException("Invalid status. Must be 'enabled' or 'disabled'");
        }
        
        activity.setStatus(status);
        activityRepository.updateById(activity);
        return activity;
    }

    @Transactional
    public void delete(Integer id) {
        Activity activity = activityRepository.selectById(id);
        if (activity == null) {
            throw new BadRequestException("Activity not found with id: " + id);
        }
        activityRepository.deleteById(id);
    }

    @Transactional
    public int batchUpdateStatus(List<Integer> ids, String status) {
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("Activity IDs cannot be empty");
        }
        if (!"enabled".equals(status) && !"disabled".equals(status)) {
            throw new BadRequestException("Invalid status. Must be 'enabled' or 'disabled'");
        }
        
        int count = 0;
        for (Integer id : ids) {
            Activity activity = activityRepository.selectById(id);
            if (activity != null) {
                activity.setStatus(status);
                activityRepository.updateById(activity);
                count++;
            }
        }
        return count;
    }

    @Transactional
    public int batchDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("Activity IDs cannot be empty");
        }
        
        int count = 0;
        for (Integer id : ids) {
            activityRepository.deleteById(id);
            count++;
        }
        return count;
    }
    
    public org.springframework.data.domain.Page<Activity> findAll(int page, int pageSize) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, pageSize);
        Page<Activity> mpPage = new Page<>(page + 1, pageSize);
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Activity::getCreatedAt);
        Page<Activity> result = activityRepository.selectPage(mpPage, wrapper);
        
        return new org.springframework.data.domain.Page<Activity>() {
            @Override
            public int getTotalPages() {
                return (int) Math.ceil((double) result.getTotal() / pageSize);
            }

            @Override
            public long getTotalElements() {
                return result.getTotal();
            }

            @Override
            public int getNumber() {
                return page;
            }

            @Override
            public int getSize() {
                return pageSize;
            }

            @Override
            public int getNumberOfElements() {
                return result.getRecords().size();
            }

            @Override
            public List<Activity> getContent() {
                return result.getRecords();
            }

            @Override
            public boolean hasContent() {
                return !result.getRecords().isEmpty();
            }

            @Override
            public boolean isFirst() {
                return page == 0;
            }

            @Override
            public boolean isLast() {
                return page >= getTotalPages() - 1;
            }

            @Override
            public boolean hasNext() {
                return page < getTotalPages() - 1;
            }

            @Override
            public boolean hasPrevious() {
                return page > 0;
            }

            @Override
            public org.springframework.data.domain.Pageable nextPageable() {
                if (hasNext()) {
                    return org.springframework.data.domain.PageRequest.of(page + 1, pageSize);
                }
                return org.springframework.data.domain.Pageable.unpaged();
            }

            @Override
            public org.springframework.data.domain.Pageable previousPageable() {
                if (hasPrevious()) {
                    return org.springframework.data.domain.PageRequest.of(page - 1, pageSize);
                }
                return org.springframework.data.domain.Pageable.unpaged();
            }

            @Override
            public org.springframework.data.domain.Sort getSort() {
                return org.springframework.data.domain.Sort.unsorted();
            }

            @Override
            public java.util.Iterator<Activity> iterator() {
                return result.getRecords().iterator();
            }

            @Override
            public <U> org.springframework.data.domain.Page<U> map(java.util.function.Function<? super Activity, ? extends U> converter) {
                List<U> converted = result.getRecords().stream()
                        .map(converter)
                        .collect(java.util.stream.Collectors.toList());
                return new org.springframework.data.domain.Page<U>() {
                    @Override public int getTotalPages() { return (int) Math.ceil((double) result.getTotal() / pageSize); }
                    @Override public long getTotalElements() { return result.getTotal(); }
                    @Override public int getNumber() { return page; }
                    @Override public int getSize() { return pageSize; }
                    @Override public int getNumberOfElements() { return converted.size(); }
                    @Override public List<U> getContent() { return converted; }
                    @Override public boolean hasContent() { return !converted.isEmpty(); }
                    @Override public boolean isFirst() { return page == 0; }
                    @Override public boolean isLast() { return page >= getTotalPages() - 1; }
                    @Override public boolean hasNext() { return page < getTotalPages() - 1; }
                    @Override public boolean hasPrevious() { return page > 0; }
                    @Override public org.springframework.data.domain.Pageable nextPageable() { return hasNext() ? org.springframework.data.domain.PageRequest.of(page + 1, pageSize) : org.springframework.data.domain.Pageable.unpaged(); }
                    @Override public org.springframework.data.domain.Pageable previousPageable() { return hasPrevious() ? org.springframework.data.domain.PageRequest.of(page - 1, pageSize) : org.springframework.data.domain.Pageable.unpaged(); }
                    @Override public org.springframework.data.domain.Sort getSort() { return org.springframework.data.domain.Sort.unsorted(); }
                    @Override public java.util.Iterator<U> iterator() { return converted.iterator(); }
                    @Override public <V> org.springframework.data.domain.Page<V> map(java.util.function.Function<? super U, ? extends V> converter2) { throw new UnsupportedOperationException(); }
                };
            }
        };
    }
    
    public org.springframework.data.domain.Page<Activity> searchActivities(String keyword, String type, 
                                                                            String status, LocalDateTime startDate, 
                                                                            LocalDateTime endDate, 
                                                                            int page, int pageSize) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, pageSize);
        Page<Activity> mpPage = new Page<>(page + 1, pageSize);
        
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Activity::getName, keyword)
                    .or().like(Activity::getDescription, keyword));
        }
        
        if (type != null && !type.trim().isEmpty()) {
            wrapper.eq(Activity::getType, type);
        }
        
        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq(Activity::getStatus, status);
        }
        
        if (startDate != null) {
            wrapper.ge(Activity::getStartTime, startDate);
        }
        
        if (endDate != null) {
            wrapper.le(Activity::getEndTime, endDate);
        }
        
        wrapper.orderByDesc(Activity::getCreatedAt);
        
        Page<Activity> result = activityRepository.selectPage(mpPage, wrapper);
        
        return new org.springframework.data.domain.Page<Activity>() {
            @Override
            public int getTotalPages() {
                return (int) Math.ceil((double) result.getTotal() / pageSize);
            }

            @Override
            public long getTotalElements() {
                return result.getTotal();
            }

            @Override
            public int getNumber() {
                return page;
            }

            @Override
            public int getSize() {
                return pageSize;
            }

            @Override
            public int getNumberOfElements() {
                return result.getRecords().size();
            }

            @Override
            public List<Activity> getContent() {
                return result.getRecords();
            }

            @Override
            public boolean hasContent() {
                return !result.getRecords().isEmpty();
            }

            @Override
            public boolean isFirst() {
                return page == 0;
            }

            @Override
            public boolean isLast() {
                return page >= getTotalPages() - 1;
            }

            @Override
            public boolean hasNext() {
                return page < getTotalPages() - 1;
            }

            @Override
            public boolean hasPrevious() {
                return page > 0;
            }

            @Override
            public org.springframework.data.domain.Pageable nextPageable() {
                if (hasNext()) {
                    return org.springframework.data.domain.PageRequest.of(page + 1, pageSize);
                }
                return org.springframework.data.domain.Pageable.unpaged();
            }

            @Override
            public org.springframework.data.domain.Pageable previousPageable() {
                if (hasPrevious()) {
                    return org.springframework.data.domain.PageRequest.of(page - 1, pageSize);
                }
                return org.springframework.data.domain.Pageable.unpaged();
            }

            @Override
            public org.springframework.data.domain.Sort getSort() {
                return org.springframework.data.domain.Sort.unsorted();
            }

            @Override
            public java.util.Iterator<Activity> iterator() {
                return result.getRecords().iterator();
            }

            @Override
            public <U> org.springframework.data.domain.Page<U> map(java.util.function.Function<? super Activity, ? extends U> converter) {
                List<U> converted = result.getRecords().stream()
                        .map(converter)
                        .collect(java.util.stream.Collectors.toList());
                return new org.springframework.data.domain.Page<U>() {
                    @Override public int getTotalPages() { return (int) Math.ceil((double) result.getTotal() / pageSize); }
                    @Override public long getTotalElements() { return result.getTotal(); }
                    @Override public int getNumber() { return page; }
                    @Override public int getSize() { return pageSize; }
                    @Override public int getNumberOfElements() { return converted.size(); }
                    @Override public List<U> getContent() { return converted; }
                    @Override public boolean hasContent() { return !converted.isEmpty(); }
                    @Override public boolean isFirst() { return page == 0; }
                    @Override public boolean isLast() { return page >= getTotalPages() - 1; }
                    @Override public boolean hasNext() { return page < getTotalPages() - 1; }
                    @Override public boolean hasPrevious() { return page > 0; }
                    @Override public org.springframework.data.domain.Pageable nextPageable() { return hasNext() ? org.springframework.data.domain.PageRequest.of(page + 1, pageSize) : org.springframework.data.domain.Pageable.unpaged(); }
                    @Override public org.springframework.data.domain.Pageable previousPageable() { return hasPrevious() ? org.springframework.data.domain.PageRequest.of(page - 1, pageSize) : org.springframework.data.domain.Pageable.unpaged(); }
                    @Override public org.springframework.data.domain.Sort getSort() { return org.springframework.data.domain.Sort.unsorted(); }
                    @Override public java.util.Iterator<U> iterator() { return converted.iterator(); }
                    @Override public <V> org.springframework.data.domain.Page<V> map(java.util.function.Function<? super U, ? extends V> converter2) { throw new UnsupportedOperationException(); }
                };
            }
        };
    }
}
