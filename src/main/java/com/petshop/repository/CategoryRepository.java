package com.petshop.repository;

import com.petshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByMerchantId(Integer merchantId);
    
    List<Category> findByMerchantIdAndStatus(Integer merchantId, String status);
    
    List<Category> findByMerchantIdOrderBySortAsc(Integer merchantId);
    
    @Query("SELECT c FROM Category c WHERE c.merchantId = :merchantId ORDER BY c.sort ASC, c.createdAt DESC")
    List<Category> findByMerchantIdSorted(@Param("merchantId") Integer merchantId);
    
    @Modifying
    @Query("UPDATE Category c SET c.status = :status WHERE c.id = :id")
    int updateStatus(@Param("id") Integer id, @Param("status") String status);
    
    @Modifying
    @Query("UPDATE Category c SET c.productCount = c.productCount + :delta WHERE c.id = :id")
    int updateProductCount(@Param("id") Integer id, @Param("delta") Integer delta);
    
    @Modifying
    @Query("UPDATE Category c SET c.status = :status WHERE c.id IN :ids")
    int batchUpdateStatus(@Param("ids") List<Integer> ids, @Param("status") String status);
    
    boolean existsByMerchantIdAndName(Integer merchantId, String name);
    
    long countByMerchantIdAndStatus(Integer merchantId, String status);
}
