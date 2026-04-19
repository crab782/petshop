package com.petshop.repository;

import com.petshop.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Integer>, JpaSpecificationExecutor<Activity> {
    
    Page<Activity> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    Page<Activity> findByStatus(String status, Pageable pageable);
    
    Page<Activity> findByType(String type, Pageable pageable);
    
    @Query("SELECT a FROM Activity a WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR a.name LIKE %:keyword%) AND " +
           "(:type IS NULL OR :type = '' OR a.type = :type) AND " +
           "(:status IS NULL OR :status = '' OR a.status = :status) AND " +
           "(:startDate IS NULL OR a.startTime >= :startDate) AND " +
           "(:endDate IS NULL OR a.endTime <= :endDate)")
    Page<Activity> searchActivities(
            @Param("keyword") String keyword,
            @Param("type") String type,
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    @Modifying
    @Query("UPDATE Activity a SET a.status = :status WHERE a.id IN :ids")
    int batchUpdateStatus(@Param("ids") List<Integer> ids, @Param("status") String status);
    
    @Modifying
    @Query("DELETE FROM Activity a WHERE a.id IN :ids")
    int batchDelete(@Param("ids") List<Integer> ids);
}
