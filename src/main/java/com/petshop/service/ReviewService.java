package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.dto.ReviewDTO;
import com.petshop.entity.Review;
import com.petshop.exception.BadRequestException;
import com.petshop.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
    private ReviewMapper reviewMapper;
    
    public Review create(Review review) {
        review.setCreatedAt(LocalDateTime.now());
        reviewMapper.insert(review);
        return review;
    }
    
    public Review findById(Integer id) {
        return reviewMapper.selectById(id);
    }
    
    public List<Review> findByMerchantId(Integer merchantId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getMerchantId, merchantId);
        return reviewMapper.selectList(wrapper);
    }
    
    public List<Review> findByUserId(Integer userId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getUserId, userId);
        return reviewMapper.selectList(wrapper);
    }
    
    public Page<Review> findByUserId(Integer userId, Page<Review> page) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getUserId, userId);
        wrapper.orderByDesc(Review::getCreatedAt);
        return reviewMapper.selectPage(page, wrapper);
    }
    
    public Review findByAppointmentId(Integer appointmentId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getAppointmentId, appointmentId);
        return reviewMapper.selectOne(wrapper);
    }
    
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<ReviewDTO> findByUserIdWithPaging(Integer userId, Integer rating, int page, int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Review> reviewPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, size);
        
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getUserId, userId);
        
        if (rating != null) {
            wrapper.eq(Review::getRating, rating);
        }
        
        wrapper.orderByDesc(Review::getCreatedAt);
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Review> result = reviewMapper.selectPage(reviewPage, wrapper);
        
        // 创建新的 Page 对象，用于存放转换后的 ReviewDTO
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ReviewDTO> dtoPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, size);
        dtoPage.setTotal(result.getTotal());
        dtoPage.setRecords(result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        
        return dtoPage;
    }
    
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<ReviewDTO> findByUserIdWithPagingAndRating(Integer userId, Integer rating, int page, int size) {
        return findByUserIdWithPaging(userId, rating, page, size);
    }
    
    private ReviewDTO convertToDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .userName(review.getUser() != null ? review.getUser().getUsername() : null)
                .merchantId(review.getMerchantId())
                .merchantName(review.getMerchant() != null ? review.getMerchant().getName() : null)
                .serviceId(review.getServiceId())
                .serviceName(review.getService() != null ? review.getService().getName() : null)
                .appointmentId(review.getAppointmentId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createTime(review.getCreatedAt())
                .build();
    }
    
    public List<Review> findByServiceId(Integer serviceId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getServiceId, serviceId);
        return reviewMapper.selectList(wrapper);
    }
    
    public Page<Review> findByMerchantId(Integer merchantId, Page<Review> page) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getMerchantId, merchantId);
        wrapper.orderByDesc(Review::getCreatedAt);
        return reviewMapper.selectPage(page, wrapper);
    }
    
    public Page<Review> searchReviews(Integer merchantId, Integer rating, String keyword, Page<Review> page) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        
        if (merchantId != null) {
            wrapper.eq(Review::getMerchantId, merchantId);
        }
        
        if (rating != null) {
            wrapper.eq(Review::getRating, rating);
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Review::getComment, keyword)
                    .or().like(Review::getReplyContent, keyword));
        }
        
        wrapper.orderByDesc(Review::getCreatedAt);
        
        return reviewMapper.selectPage(page, wrapper);
    }
    
    public List<Review> getRecentReviews(Integer merchantId, int limit) {
        Page<Review> page = new Page<>(1, limit);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getMerchantId, merchantId);
        wrapper.orderByDesc(Review::getCreatedAt);
        IPage<Review> result = reviewMapper.selectPage(page, wrapper);
        return result.getRecords();
    }
    
    @Transactional
    public Review replyToReview(Integer reviewId, String replyContent) {
        Review review = reviewMapper.selectById(reviewId);
        if (review != null) {
            review.setReplyContent(replyContent);
            review.setReplyTime(LocalDateTime.now());
            reviewMapper.updateById(review);
            return review;
        }
        return null;
    }
    
    @Transactional
    public Review update(Review review) {
        reviewMapper.updateById(review);
        return review;
    }
    
    @Transactional
    public void delete(Integer id) {
        reviewMapper.deleteById(id);
    }
    
    public Double getAverageRating(Integer merchantId) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getMerchantId, merchantId);
        List<Review> reviews = reviewMapper.selectList(wrapper);
        
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        double sum = reviews.stream()
                .mapToInt(Review::getRating)
                .sum();
        double avg = sum / reviews.size();
        return Math.round(avg * 10.0) / 10.0;
    }
    
    public Map<Integer, Long> getRatingDistribution(Integer merchantId) {
        Map<Integer, Long> distribution = new HashMap<>();
        
        for (int i = 1; i <= 5; i++) {
            LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Review::getMerchantId, merchantId)
                   .eq(Review::getRating, i);
            Long count = (long) reviewMapper.selectCount(wrapper);
            distribution.put(i, count);
        }
        
        return distribution;
    }
    
    public Map<String, Object> getReviewStatistics(Integer merchantId) {
        Map<String, Object> statistics = new HashMap<>();
        
        Double avgRating = getAverageRating(merchantId);
        statistics.put("averageRating", avgRating);
        
        Map<Integer, Long> distribution = getRatingDistribution(merchantId);
        statistics.put("ratingDistribution", distribution);
        
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getMerchantId, merchantId);
        long totalReviews = reviewMapper.selectCount(wrapper);
        statistics.put("totalReviews", totalReviews);
        
        return statistics;
    }
    
    public Page<Review> getReviewsWithPaging(Integer merchantId, Integer rating, String keyword, 
                                             int page, int size, String sortBy, String sortDir) {
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        
        if (merchantId != null) {
            wrapper.eq(Review::getMerchantId, merchantId);
        }
        
        if (rating != null) {
            wrapper.eq(Review::getRating, rating);
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Review::getComment, keyword)
                    .or().like(Review::getReplyContent, keyword));
        }
        
        if ("asc".equalsIgnoreCase(sortDir)) {
            wrapper.orderByAsc(Review::getCreatedAt);
        } else {
            wrapper.orderByDesc(Review::getCreatedAt);
        }
        
        Page<Review> reviewPage = new Page<>(page, size);
        return reviewMapper.selectPage(reviewPage, wrapper);
    }
    

    
    @Transactional
    public Review auditReview(Integer id, String status, String reason) {
        Review review = reviewMapper.selectById(id);
        if (review != null) {
            review.setStatus(status);
            reviewMapper.updateById(review);
            return review;
        }
        return null;
    }
    
    @Transactional
    public int batchUpdateStatus(List<Integer> ids, String status) {
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("Review IDs cannot be empty");
        }
        if (status == null || (!status.equals("approved") && !status.equals("rejected"))) {
            throw new BadRequestException("Invalid status. Must be 'approved' or 'rejected'");
        }
        
        int count = 0;
        for (Integer id : ids) {
            Review review = reviewMapper.selectById(id);
            if (review != null) {
                review.setStatus(status);
                reviewMapper.updateById(review);
                count++;
            }
        }
        return count;
    }
    
    @Transactional
    public int batchDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("Review IDs cannot be empty");
        }
        
        int count = 0;
        for (Integer id : ids) {
            reviewMapper.deleteById(id);
            count++;
        }
        return count;
    }
    
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<Review> getAllReviews(int page, int pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Review> mpPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, pageSize);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Review::getCreatedAt);
        return reviewMapper.selectPage(mpPage, wrapper);
    }
    
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<Review> searchAdminReviews(Integer rating, String keyword, 
                                                                            Integer merchantId, String status, 
                                                                            int page, int pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Review> mpPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, pageSize);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        
        if (rating != null) {
            wrapper.eq(Review::getRating, rating);
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Review::getComment, keyword)
                    .or().like(Review::getReplyContent, keyword));
        }
        
        if (merchantId != null) {
            wrapper.eq(Review::getMerchantId, merchantId);
        }
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Review::getStatus, status);
        }
        
        wrapper.orderByDesc(Review::getCreatedAt);
        
        return reviewMapper.selectPage(mpPage, wrapper);
    }
    
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<Review> getPendingReviews(String keyword, int page, int pageSize) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Review> mpPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, pageSize);
        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getStatus, "pending");
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Review::getComment, keyword);
        }
        
        wrapper.orderByDesc(Review::getCreatedAt);
        
        return reviewMapper.selectPage(mpPage, wrapper);
    }
}
