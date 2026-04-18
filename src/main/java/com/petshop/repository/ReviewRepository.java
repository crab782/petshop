package com.petshop.repository;

import com.petshop.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    
    List<Review> findByMerchantId(Integer merchantId);
    
    List<Review> findByUserId(Integer userId);
    
    List<Review> findByServiceId(Integer serviceId);
    
    Page<Review> findByMerchantId(Integer merchantId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.merchant.id = :merchantId ORDER BY r.createdAt DESC")
    List<Review> findByMerchantIdOrderByCreatedAtDesc(@Param("merchantId") Integer merchantId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.merchant.id = :merchantId")
    Double getAverageRatingByMerchantId(@Param("merchantId") Integer merchantId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.merchant.id = :merchantId AND r.rating = :rating")
    Long countByMerchantIdAndRating(@Param("merchantId") Integer merchantId, @Param("rating") Integer rating);
    
    @Query("SELECT r FROM Review r WHERE r.merchant.id = :merchantId " +
           "AND (:rating IS NULL OR r.rating = :rating) " +
           "AND (:keyword IS NULL OR r.comment LIKE %:keyword%)")
    Page<Review> searchReviews(@Param("merchantId") Integer merchantId,
                               @Param("rating") Integer rating,
                               @Param("keyword") String keyword,
                               Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.merchant.id = :merchantId ORDER BY r.createdAt DESC")
    List<Review> findRecentByMerchantId(@Param("merchantId") Integer merchantId, Pageable pageable);
}
