package com.petshop.repository;

import com.petshop.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    List<Announcement> findAllByOrderByCreatedAtDesc();
    
    Page<Announcement> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    Page<Announcement> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    
    List<Announcement> findByStatus(String status);
    
    @Modifying
    @Query("UPDATE Announcement a SET a.status = :status WHERE a.id IN :ids")
    int batchUpdateStatus(@Param("ids") List<Integer> ids, @Param("status") String status);
    
    @Modifying
    @Query("DELETE FROM Announcement a WHERE a.id IN :ids")
    int batchDelete(@Param("ids") List<Integer> ids);
}
