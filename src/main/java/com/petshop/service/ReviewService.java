package com.petshop.service;

import com.petshop.entity.Review;
import com.petshop.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
