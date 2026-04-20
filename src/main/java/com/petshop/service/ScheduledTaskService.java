package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.entity.ScheduledTask;
import com.petshop.exception.BadRequestException;
import com.petshop.mapper.ScheduledTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduledTaskService {
    
    @Autowired
    private ScheduledTaskMapper scheduledTaskRepository;
    
    public ScheduledTask findById(Integer id) {
        return scheduledTaskRepository.selectById(id);
    }
    
    @Transactional
    public ScheduledTask create(ScheduledTask task) {
        if (task.getStatus() == null || task.getStatus().isEmpty()) {
            task.setStatus("enabled");
        }
        scheduledTaskRepository.insert(task);
        return task;
    }
    
    @Transactional
    public ScheduledTask update(Integer id, ScheduledTask task) {
        ScheduledTask existingTask = scheduledTaskRepository.selectById(id);
        if (existingTask == null) {
            throw new BadRequestException("Task not found with id: " + id);
        }
        
        existingTask.setName(task.getName());
        existingTask.setType(task.getType());
        existingTask.setCronExpression(task.getCronExpression());
        existingTask.setDescription(task.getDescription());
        
        scheduledTaskRepository.updateById(existingTask);
        return existingTask;
    }
    
    @Transactional
    public void delete(Integer id) {
        ScheduledTask task = scheduledTaskRepository.selectById(id);
        if (task == null) {
            throw new BadRequestException("Task not found with id: " + id);
        }
        scheduledTaskRepository.deleteById(id);
    }
    
    @Transactional
    public Map<String, Object> executeTask(Integer id) {
        ScheduledTask task = scheduledTaskRepository.selectById(id);
        if (task == null) {
            throw new BadRequestException("Task not found with id: " + id);
        }
        
        if (!"enabled".equals(task.getStatus())) {
            throw new IllegalStateException("Task is not enabled");
        }
        
        LocalDateTime executeTime = LocalDateTime.now();
        task.setLastExecuteTime(executeTime);
        scheduledTaskRepository.updateById(task);
        
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", task.getId());
        result.put("taskName", task.getName());
        result.put("taskType", task.getType());
        result.put("executeTime", executeTime);
        result.put("status", "success");
        result.put("message", "Task executed successfully");
        
        return result;
    }
    
    @Transactional
    public ScheduledTask updateStatus(Integer id, String status) {
        ScheduledTask task = scheduledTaskRepository.selectById(id);
        if (task == null) {
            throw new BadRequestException("Task not found with id: " + id);
        }
        
        if (!"enabled".equals(status) && !"disabled".equals(status)) {
            throw new BadRequestException("Invalid status. Must be 'enabled' or 'disabled'");
        }
        
        task.setStatus(status);
        scheduledTaskRepository.updateById(task);
        return task;
    }
    
    @Transactional
    public int batchUpdateStatus(List<Integer> ids, String status) {
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("Task IDs cannot be empty");
        }
        if (status == null || (!status.equals("enabled") && !status.equals("disabled"))) {
            throw new BadRequestException("Invalid status. Must be 'enabled' or 'disabled'");
        }
        
        int count = 0;
        for (Integer id : ids) {
            ScheduledTask task = scheduledTaskRepository.selectById(id);
            if (task != null) {
                task.setStatus(status);
                scheduledTaskRepository.updateById(task);
                count++;
            }
        }
        return count;
    }
    
    @Transactional
    public int batchDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("Task IDs cannot be empty");
        }
        
        int count = 0;
        for (Integer id : ids) {
            scheduledTaskRepository.deleteById(id);
            count++;
        }
        return count;
    }
    
    public org.springframework.data.domain.Page<ScheduledTask> getTasks(String keyword, String type, 
                                                                         String status, int page, int pageSize) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, pageSize);
        Page<ScheduledTask> mpPage = new Page<>(page + 1, pageSize);
        
        LambdaQueryWrapper<ScheduledTask> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(ScheduledTask::getName, keyword)
                    .or().like(ScheduledTask::getDescription, keyword));
        }
        
        if (type != null && !type.trim().isEmpty()) {
            wrapper.eq(ScheduledTask::getType, type);
        }
        
        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq(ScheduledTask::getStatus, status);
        }
        
        wrapper.orderByDesc(ScheduledTask::getCreatedAt);
        
        Page<ScheduledTask> result = scheduledTaskRepository.selectPage(mpPage, wrapper);
        
        return new org.springframework.data.domain.Page<ScheduledTask>() {
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
            public List<ScheduledTask> getContent() {
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
            public java.util.Iterator<ScheduledTask> iterator() {
                return result.getRecords().iterator();
            }

            @Override
            public <U> org.springframework.data.domain.Page<U> map(java.util.function.Function<? super ScheduledTask, ? extends U> converter) {
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
