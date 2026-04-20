package com.petshop.dto;

import java.math.BigDecimal;

public class FavoriteServiceDTO {
    private Integer id;
    private Integer serviceId;
    private String serviceName;
    private String serviceImage;
    private BigDecimal servicePrice;
    private Integer serviceDuration;
    private Integer merchantId;
    private String merchantName;
    private String createdAt;

    public FavoriteServiceDTO() {
    }

    public FavoriteServiceDTO(Integer id, Integer serviceId, String serviceName, String serviceImage, BigDecimal servicePrice, Integer serviceDuration, Integer merchantId, String merchantName, String createdAt) {
        this.id = id;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceImage = serviceImage;
        this.servicePrice = servicePrice;
        this.serviceDuration = serviceDuration;
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(String serviceImage) {
        this.serviceImage = serviceImage;
    }

    public BigDecimal getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(BigDecimal servicePrice) {
        this.servicePrice = servicePrice;
    }

    public Integer getServiceDuration() {
        return serviceDuration;
    }

    public void setServiceDuration(Integer serviceDuration) {
        this.serviceDuration = serviceDuration;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private Integer serviceId;
        private String serviceName;
        private String serviceImage;
        private BigDecimal servicePrice;
        private Integer serviceDuration;
        private Integer merchantId;
        private String merchantName;
        private String createdAt;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder serviceId(Integer serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder serviceImage(String serviceImage) {
            this.serviceImage = serviceImage;
            return this;
        }

        public Builder servicePrice(BigDecimal servicePrice) {
            this.servicePrice = servicePrice;
            return this;
        }

        public Builder serviceDuration(Integer serviceDuration) {
            this.serviceDuration = serviceDuration;
            return this;
        }

        public Builder merchantId(Integer merchantId) {
            this.merchantId = merchantId;
            return this;
        }

        public Builder merchantName(String merchantName) {
            this.merchantName = merchantName;
            return this;
        }

        public Builder createdAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public FavoriteServiceDTO build() {
            return new FavoriteServiceDTO(id, serviceId, serviceName, serviceImage, servicePrice, serviceDuration, merchantId, merchantName, createdAt);
        }
    }
}
