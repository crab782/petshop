package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.entity.OperationLog;
import com.petshop.mapper.OperationLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class OperationLogService {
    
    @Autowired
    private OperationLogMapper operationLogRepository;
    
    public Page<OperationLog> getLogs(Integer adminId, String action, LocalDate startDate, LocalDate endDate, int page, int pageSize) {
        Page<OperationLog> logPage = new Page<>(page, pageSize);
        
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        
        if (adminId != null) {
            wrapper.eq(OperationLog::getAdminId, adminId);
        }
        
        if (action != null && !action.trim().isEmpty()) {
            wrapper.like(OperationLog::getAction, action);
        }
        
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        
        if (startDate != null) {
            startDateTime = startDate.atStartOfDay();
            wrapper.ge(OperationLog::getCreatedAt, startDateTime);
        }
        if (endDate != null) {
            endDateTime = endDate.atTime(LocalTime.MAX);
            wrapper.le(OperationLog::getCreatedAt, endDateTime);
        }
        
        wrapper.orderByDesc(OperationLog::getCreatedAt);
        
        return operationLogRepository.selectPage(logPage, wrapper);
    }
    
    public Page<OperationLog> getAllLogs(int page, int pageSize) {
        Page<OperationLog> logPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(OperationLog::getCreatedAt);
        return operationLogRepository.selectPage(logPage, wrapper);
    }
    
    public OperationLog findById(Integer id) {
        return operationLogRepository.selectById(id);
    }
    
    @Transactional
    public void deleteLog(Integer id) {
        operationLogRepository.deleteById(id);
    }
    
    @Transactional
    public long deleteAllLogs() {
        long count = operationLogRepository.selectCount(null);
        operationLogRepository.delete(null);
        return count;
    }
    
    @Transactional
    public OperationLog logOperation(Integer adminId, String adminName, String action, String targetType, Integer targetId, String detail, String ipAddress) {
        OperationLog log = new OperationLog();
        log.setAdminId(adminId);
        log.setAdminName(adminName);
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);
        log.setIpAddress(ipAddress);
        log.setCreatedAt(LocalDateTime.now());
        operationLogRepository.insert(log);
        return log;
    }
    
    @Transactional
    public OperationLog logOperation(OperationLog log) {
        if (log.getCreatedAt() == null) {
            log.setCreatedAt(LocalDateTime.now());
        }
        operationLogRepository.insert(log);
        return log;
    }
    
    public long count() {
        return operationLogRepository.selectCount(null);
    }
}
