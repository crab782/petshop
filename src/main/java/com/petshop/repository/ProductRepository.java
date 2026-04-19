package com.petshop.repository;

import com.petshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    List<Product> findByMerchantId(Integer merchantId);
    
    Page<Product> findByMerchantId(Integer merchantId, Pageable pageable);
    
    List<Product> findByMerchantIdAndStatus(Integer merchantId, String status);
    
    Page<Product> findByMerchantIdAndStatus(Integer merchantId, String status, Pageable pageable);
    
    List<Product> findByMerchantIdAndCategory(Integer merchantId, String category);
    
    Page<Product> findByMerchantIdAndCategory(Integer merchantId, String category, Pageable pageable);
    
    List<Product> findByStatus(String status);
    
    Page<Product> findByStatus(String status, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.merchant.id = :merchantId AND " +
           "(:name IS NULL OR p.name LIKE %:name%) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:category IS NULL OR p.category = :category)")
    Page<Product> searchProducts(@Param("merchantId") Integer merchantId,
                                  @Param("name") String name,
                                  @Param("status") String status,
                                  @Param("category") String category,
                                  Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR p.name LIKE %:name%) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:category IS NULL OR p.category = :category)")
    Page<Product> searchAllProducts(@Param("name") String name,
                                     @Param("status") String status,
                                     @Param("category") String category,
                                     Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.merchant.id = :merchantId AND p.stock <= p.lowStockThreshold")
    List<Product> findLowStockProducts(@Param("merchantId") Integer merchantId);
    
    @Query("SELECT p FROM Product p WHERE p.stock <= p.lowStockThreshold")
    List<Product> findAllLowStockProducts();
    
    @Modifying
    @Query("UPDATE Product p SET p.status = :status WHERE p.id IN :ids")
    int batchUpdateStatus(@Param("ids") List<Integer> ids, @Param("status") String status);
    
    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock + :quantity WHERE p.id = :id")
    int updateStock(@Param("id") Integer id, @Param("quantity") Integer quantity);
    
    @Modifying
    @Query("DELETE FROM Product p WHERE p.id IN :ids")
    int batchDelete(@Param("ids") List<Integer> ids);
    
    long countByMerchantId(Integer merchantId);
    
    long countByMerchantIdAndStatus(Integer merchantId, String status);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.merchant.id = :merchantId AND p.stock <= p.lowStockThreshold")
    long countLowStockProducts(@Param("merchantId") Integer merchantId);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    Page<Product> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR p.name LIKE %:keyword% OR p.description LIKE %:keyword%) AND " +
           "(:status IS NULL OR :status = '' OR p.status = :status) AND " +
           "(:merchantId IS NULL OR p.merchant.id = :merchantId) AND " +
           "(:category IS NULL OR :category = '' OR p.category = :category)")
    Page<Product> adminSearchProducts(@Param("keyword") String keyword,
                                       @Param("status") String status,
                                       @Param("merchantId") Integer merchantId,
                                       @Param("category") String category,
                                       Pageable pageable);
}
