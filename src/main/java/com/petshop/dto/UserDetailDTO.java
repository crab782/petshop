package com.petshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailDTO {
    private Integer id;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private List<RecentOrder> recentOrders;
    private List<RecentAppointment> recentAppointments;
    private List<RecentReview> recentReviews;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentOrder {
        private Integer id;
        private String orderNo;
        private BigDecimal totalPrice;
        private String status;
        private LocalDateTime createdAt;
        private String merchantName;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentAppointment {
        private Integer id;
        private String serviceName;
        private String merchantName;
        private String petName;
        private LocalDateTime appointmentTime;
        private BigDecimal totalPrice;
        private String status;
        private LocalDateTime createdAt;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentReview {
        private Integer id;
        private String serviceName;
        private String merchantName;
        private Integer rating;
        private String comment;
        private LocalDateTime createdAt;
    }
}
