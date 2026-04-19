package com.petshop.repository;

import com.petshop.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Integer>, JpaSpecificationExecutor<OperationLog> {
    
    Page<OperationLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    Page<OperationLog> findByAdminIdOrderByCreatedAtDesc(Integer adminId, Pageable pageable);
    
    Page<OperationLog> findByActionOrderByCreatedAtDesc(String action, Pageable pageable);
    
    @Query("SELECT o FROM OperationLog o WHERE " +
           "(:adminId IS NULL OR o.adminId = :adminId) AND " +
           "(:action IS NULL OR o.action = :action) AND " +
           "(:startDate IS NULL OR o.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR o.createdAt <= :endDate) " +
           "ORDER BY o.createdAt DESC")
    Page<OperationLog> searchLogs(
            @Param("adminId") Integer adminId,
            @Param("action") String action,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    List<OperationLog> findByAdminId(Integer adminId);
    
    List<OperationLog> findByAction(String action);
    
    long countByAdminId(Integer adminId);
    
    long countByAction(String action);
}
