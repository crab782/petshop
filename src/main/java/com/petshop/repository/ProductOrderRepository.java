package com.petshop.repository;

import com.petshop.entity.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {
    List<ProductOrder> findByMerchantId(Integer merchantId);
    
    List<ProductOrder> findByUserId(Integer userId);
    
    List<ProductOrder> findByMerchantIdAndStatus(Integer merchantId, String status);
    
    List<ProductOrder> findByUserIdAndStatus(Integer userId, String status);
    
    List<ProductOrder> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    
    Page<ProductOrder> findByUserId(Integer userId, Pageable pageable);
    
    @Query("SELECT o FROM ProductOrder o WHERE o.user.id = :userId " +
           "AND (:status IS NULL OR o.status = :status) " +
           "AND (:keyword IS NULL OR o.orderNo LIKE %:keyword%) " +
           "AND (:startDate IS NULL OR o.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR o.createdAt <= :endDate)")
    Page<ProductOrder> searchUserOrders(
            @Param("userId") Integer userId,
            @Param("status") String status,
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
    
    @Modifying
    @Query("UPDATE ProductOrder o SET o.status = :status WHERE o.id IN :ids")
    int batchUpdateStatus(@Param("ids") List<Integer> ids, @Param("status") String status);
    
    @Modifying
    @Query("DELETE FROM ProductOrder o WHERE o.id IN :ids AND o.user.id = :userId")
    int batchDeleteByUser(@Param("ids") List<Integer> ids, @Param("userId") Integer userId);
    
    long countByUserIdAndStatus(Integer userId, String status);
    
    long countByMerchantIdAndStatus(Integer merchantId, String status);
}
