package com.petshop.repository;

import com.petshop.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    
    List<Review> findByMerchantId(Integer merchantId);
    
    List<Review> findByUserId(Integer userId);
    
    List<Review> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    
    Page<Review> findByUserId(Integer userId, Pageable pageable);
    
    long countByUserId(Integer userId);
    
    Review findByAppointmentId(Integer appointmentId);
    
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
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.merchant.id = :merchantId")
    Long countByMerchantId(@Param("merchantId") Integer merchantId);
    
    @Query("SELECT r FROM Review r WHERE r.merchant.id = :merchantId ORDER BY r.createdAt DESC LIMIT :limit")
    List<Review> findRecentReviewsByMerchantId(@Param("merchantId") Integer merchantId, @Param("limit") int limit);
    
    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    Page<Review> findByStatus(String status, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.merchant.id = :merchantId ORDER BY r.createdAt DESC")
    Page<Review> findByMerchantIdOrderByCreatedAtDescPage(@Param("merchantId") Integer merchantId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE " +
           "(:rating IS NULL OR r.rating = :rating) " +
           "AND (:keyword IS NULL OR r.comment LIKE %:keyword% OR r.user.username LIKE %:keyword%) " +
           "AND (:merchantId IS NULL OR r.merchant.id = :merchantId) " +
           "AND (:status IS NULL OR r.status = :status) " +
           "ORDER BY r.createdAt DESC")
    Page<Review> searchAdminReviews(@Param("rating") Integer rating,
                                    @Param("keyword") String keyword,
                                    @Param("merchantId") Integer merchantId,
                                    @Param("status") String status,
                                    Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.status = 'pending' " +
           "AND (:keyword IS NULL OR r.comment LIKE %:keyword% OR r.user.username LIKE %:keyword%) " +
           "ORDER BY r.createdAt DESC")
    Page<Review> findPendingReviews(@Param("keyword") String keyword, Pageable pageable);
    
    @Modifying
    @Query("UPDATE Review r SET r.status = :status WHERE r.id IN :ids")
    int batchUpdateStatus(@Param("ids") List<Integer> ids, @Param("status") String status);
    
    @Modifying
    @Query("DELETE FROM Review r WHERE r.id IN :ids")
    int batchDelete(@Param("ids") List<Integer> ids);
}
