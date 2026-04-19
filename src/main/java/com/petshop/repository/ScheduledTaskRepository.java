package com.petshop.repository;

import com.petshop.entity.ScheduledTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduledTaskRepository extends JpaRepository<ScheduledTask, Integer>, JpaSpecificationExecutor<ScheduledTask> {
    
    Page<ScheduledTask> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    Page<ScheduledTask> findByStatus(String status, Pageable pageable);
    
    Page<ScheduledTask> findByType(String type, Pageable pageable);
    
    @Query("SELECT t FROM ScheduledTask t WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR t.name LIKE %:keyword% OR t.description LIKE %:keyword%) AND " +
           "(:type IS NULL OR :type = '' OR t.type = :type) AND " +
           "(:status IS NULL OR :status = '' OR t.status = :status)")
    Page<ScheduledTask> searchTasks(@Param("keyword") String keyword, 
                                    @Param("type") String type, 
                                    @Param("status") String status, 
                                    Pageable pageable);
    
    @Modifying
    @Query("UPDATE ScheduledTask t SET t.status = :status WHERE t.id IN :ids")
    int batchUpdateStatus(@Param("ids") List<Integer> ids, @Param("status") String status);
    
    @Modifying
    @Query("DELETE FROM ScheduledTask t WHERE t.id IN :ids")
    int batchDelete(@Param("ids") List<Integer> ids);
}
