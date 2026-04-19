package com.petshop.service;

import com.petshop.dto.ReviewDTO;
import com.petshop.entity.Review;
import com.petshop.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    public Review create(Review review) {
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }
    
    public Review findById(Integer id) {
        return reviewRepository.findById(id).orElse(null);
    }
    
    public List<Review> findByMerchantId(Integer merchantId) {
        return reviewRepository.findByMerchantId(merchantId);
    }
    
    public List<Review> findByUserId(Integer userId) {
        return reviewRepository.findByUserId(userId);
    }
    
    public Page<Review> findByUserId(Integer userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable);
    }
    
    public Review findByAppointmentId(Integer appointmentId) {
        return reviewRepository.findByAppointmentId(appointmentId);
    }
    
    public Page<ReviewDTO> findByUserIdWithPaging(Integer userId, Integer rating, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviewPage;
        if (rating != null) {
            reviewPage = reviewRepository.findByUserId(userId, pageable);
        } else {
            reviewPage = reviewRepository.findByUserId(userId, pageable);
        }
        List<ReviewDTO> dtos = reviewPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, reviewPage.getTotalElements());
    }
    
    public Page<ReviewDTO> findByUserIdWithPagingAndRating(Integer userId, Integer rating, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Review> reviewPage = reviewRepository.findByUserId(userId, pageable);
        List<ReviewDTO> dtos = reviewPage.getContent().stream()
                .filter(r -> rating == null || r.getRating().equals(rating))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, reviewPage.getTotalElements());
    }
    
    private ReviewDTO convertToDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getUsername())
                .merchantId(review.getMerchant().getId())
                .merchantName(review.getMerchant().getName())
                .serviceId(review.getService().getId())
                .serviceName(review.getService().getName())
                .appointmentId(review.getAppointment().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createTime(review.getCreatedAt())
                .build();
    }
    
    public List<Review> findByServiceId(Integer serviceId) {
        return reviewRepository.findByServiceId(serviceId);
    }
    
    public Page<Review> findByMerchantId(Integer merchantId, Pageable pageable) {
        return reviewRepository.findByMerchantId(merchantId, pageable);
    }
    
    public Page<Review> searchReviews(Integer merchantId, Integer rating, String keyword, Pageable pageable) {
        return reviewRepository.searchReviews(merchantId, rating, keyword, pageable);
    }
    
    public List<Review> getRecentReviews(Integer merchantId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return reviewRepository.findRecentByMerchantId(merchantId, pageable);
    }
    
    @Transactional
    public Review replyToReview(Integer reviewId, String replyContent) {
        Review review = reviewRepository.findById(reviewId).orElse(null);
        if (review != null) {
            review.setReplyContent(replyContent);
            review.setReplyTime(LocalDateTime.now());
            return reviewRepository.save(review);
        }
        return null;
    }
    
    @Transactional
    public Review update(Review review) {
        return reviewRepository.save(review);
    }
    
    @Transactional
    public void delete(Integer id) {
        reviewRepository.deleteById(id);
    }
    
    public Double getAverageRating(Integer merchantId) {
        Double avgRating = reviewRepository.getAverageRatingByMerchantId(merchantId);
        return avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0;
    }
    
    public Map<Integer, Long> getRatingDistribution(Integer merchantId) {
        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            Long count = reviewRepository.countByMerchantIdAndRating(merchantId, i);
            distribution.put(i, count != null ? count : 0L);
        }
        return distribution;
    }
    
    public Map<String, Object> getReviewStatistics(Integer merchantId) {
        Map<String, Object> statistics = new HashMap<>();
        
        Double avgRating = getAverageRating(merchantId);
        statistics.put("averageRating", avgRating);
        
        Map<Integer, Long> distribution = getRatingDistribution(merchantId);
        statistics.put("ratingDistribution", distribution);
        
        long totalReviews = reviewRepository.findByMerchantId(merchantId).size();
        statistics.put("totalReviews", totalReviews);
        
        return statistics;
    }
    
    public Page<Review> getReviewsWithPaging(Integer merchantId, Integer rating, String keyword, 
                                             int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") 
            ? Sort.by(sortBy).ascending() 
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        if (rating != null || (keyword != null && !keyword.trim().isEmpty())) {
            return searchReviews(merchantId, rating, keyword, pageable);
        }
        
        return findByMerchantId(merchantId, pageable);
    }
    
    public Page<Review> getAllReviews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
    
    public Page<Review> getReviewsByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByStatus(status, pageable);
    }
    
    public Page<Review> searchAdminReviews(Integer rating, String keyword, Integer merchantId, 
                                           String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.searchAdminReviews(rating, keyword, merchantId, status, pageable);
    }
    
    public Page<Review> getPendingReviews(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findPendingReviews(keyword, pageable);
    }
    
    @Transactional
    public Review auditReview(Integer id, String status, String reason) {
        Review review = reviewRepository.findById(id).orElse(null);
        if (review != null) {
            review.setStatus(status);
            return reviewRepository.save(review);
        }
        return null;
    }
    
    @Transactional
    public int batchUpdateStatus(List<Integer> ids, String status) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Review IDs cannot be empty");
        }
        if (status == null || (!status.equals("approved") && !status.equals("rejected"))) {
            throw new IllegalArgumentException("Invalid status. Must be 'approved' or 'rejected'");
        }
        return reviewRepository.batchUpdateStatus(ids, status);
    }
    
    @Transactional
    public int batchDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Review IDs cannot be empty");
        }
        return reviewRepository.batchDelete(ids);
    }
}
