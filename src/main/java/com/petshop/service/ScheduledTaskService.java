package com.petshop.service;

import com.petshop.entity.ScheduledTask;
import com.petshop.repository.ScheduledTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private ScheduledTaskRepository scheduledTaskRepository;
    
    public Page<ScheduledTask> getTasks(String keyword, String type, String status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        
        if ((keyword == null || keyword.trim().isEmpty()) && 
            (type == null || type.trim().isEmpty()) && 
            (status == null || status.trim().isEmpty())) {
            return scheduledTaskRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        
        return scheduledTaskRepository.searchTasks(keyword, type, status, pageable);
    }
    
    public ScheduledTask findById(Integer id) {
        return scheduledTaskRepository.findById(id).orElse(null);
    }
    
    @Transactional
    public ScheduledTask create(ScheduledTask task) {
        if (task.getStatus() == null || task.getStatus().isEmpty()) {
            task.setStatus("enabled");
        }
        return scheduledTaskRepository.save(task);
    }
    
    @Transactional
    public ScheduledTask update(Integer id, ScheduledTask task) {
        ScheduledTask existingTask = scheduledTaskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
        
        existingTask.setName(task.getName());
        existingTask.setType(task.getType());
        existingTask.setCronExpression(task.getCronExpression());
        existingTask.setDescription(task.getDescription());
        
        return scheduledTaskRepository.save(existingTask);
    }
    
    @Transactional
    public void delete(Integer id) {
        ScheduledTask task = scheduledTaskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
        scheduledTaskRepository.delete(task);
    }
    
    @Transactional
    public Map<String, Object> executeTask(Integer id) {
        ScheduledTask task = scheduledTaskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
        
        if (!"enabled".equals(task.getStatus())) {
            throw new IllegalStateException("Task is not enabled");
        }
        
        LocalDateTime executeTime = LocalDateTime.now();
        task.setLastExecuteTime(executeTime);
        scheduledTaskRepository.save(task);
        
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
        ScheduledTask task = scheduledTaskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
        
        if (!"enabled".equals(status) && !"disabled".equals(status)) {
            throw new IllegalArgumentException("Invalid status. Must be 'enabled' or 'disabled'");
        }
        
        task.setStatus(status);
        return scheduledTaskRepository.save(task);
    }
    
    @Transactional
    public int batchUpdateStatus(List<Integer> ids, String status) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Task IDs cannot be empty");
        }
        return scheduledTaskRepository.batchUpdateStatus(ids, status);
    }
    
    @Transactional
    public int batchDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Task IDs cannot be empty");
        }
        return scheduledTaskRepository.batchDelete(ids);
    }
}
