package com.petshop.service;

import com.petshop.entity.OperationLog;
import com.petshop.repository.OperationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class OperationLogService {
    
    @Autowired
    private OperationLogRepository operationLogRepository;
    
    public Page<OperationLog> getLogs(Integer adminId, String action, LocalDate startDate, LocalDate endDate, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        
        if (startDate != null) {
            startDateTime = startDate.atStartOfDay();
        }
        if (endDate != null) {
            endDateTime = endDate.atTime(LocalTime.MAX);
        }
        
        return operationLogRepository.searchLogs(adminId, action, startDateTime, endDateTime, pageable);
    }
    
    public Page<OperationLog> getAllLogs(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return operationLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
    
    public OperationLog findById(Integer id) {
        return operationLogRepository.findById(id).orElse(null);
    }
    
    @Transactional
    public void deleteLog(Integer id) {
        operationLogRepository.deleteById(id);
    }
    
    @Transactional
    public long deleteAllLogs() {
        long count = operationLogRepository.count();
        operationLogRepository.deleteAll();
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
        return operationLogRepository.save(log);
    }
    
    @Transactional
    public OperationLog logOperation(OperationLog log) {
        if (log.getCreatedAt() == null) {
            log.setCreatedAt(LocalDateTime.now());
        }
        return operationLogRepository.save(log);
    }
    
    public long count() {
        return operationLogRepository.count();
    }
}
