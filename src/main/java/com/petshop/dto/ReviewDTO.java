package com.petshop.dto;

import java.time.LocalDateTime;

public class ReviewDTO {
    private Integer id;
    private Integer userId;
    private String userName;
    private Integer merchantId;
    private Integer serviceId;
    private Integer appointmentId;
    private Integer rating;
    private String comment;
    private LocalDateTime createTime;
    private String serviceName;
    private String merchantName;

    public ReviewDTO() {
    }

    public ReviewDTO(Integer id, Integer userId, String userName, Integer merchantId, Integer serviceId, Integer appointmentId, Integer rating, String comment, LocalDateTime createTime, String serviceName, String merchantName) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.merchantId = merchantId;
        this.serviceId = serviceId;
        this.appointmentId = appointmentId;
        this.rating = rating;
        this.comment = comment;
        this.createTime = createTime;
        this.serviceName = serviceName;
        this.merchantName = merchantName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer userId;
        private String userName;
        private Integer merchantId;
        private Integer serviceId;
        private Integer appointmentId;
        private Integer rating;
        private String comment;
        private LocalDateTime createTime;
        private String serviceName;
        private String merchantName;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder userId(Integer userId) {
            this.userId = userId;
            return this;
        }

        public Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder merchantId(Integer merchantId) {
            this.merchantId = merchantId;
            return this;
        }

        public Builder serviceId(Integer serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public Builder appointmentId(Integer appointmentId) {
            this.appointmentId = appointmentId;
            return this;
        }

        public Builder rating(Integer rating) {
            this.rating = rating;
            return this;
        }

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder createTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder merchantName(String merchantName) {
            this.merchantName = merchantName;
            return this;
        }

        public ReviewDTO build() {
            return new ReviewDTO(id, userId, userName, merchantId, serviceId, appointmentId, rating, comment, createTime, serviceName, merchantName);
        }
    }
}
