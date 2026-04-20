package com.petshop.dto;

import java.math.BigDecimal;

public class ServiceDTO {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private Integer merchantId;
    private String merchantName;
    private String category;
    private String image;
    private String status;

    public ServiceDTO() {
    }

    public ServiceDTO(Integer id, String name, String description, BigDecimal price, Integer duration, Integer merchantId, String merchantName, String category, String image, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.merchantId = merchantId;
        this.merchantName = merchantName;
        this.category = category;
        this.image = image;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer duration;
        private Integer merchantId;
        private String merchantName;
        private String category;
        private String image;
        private String status;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder duration(Integer duration) {
            this.duration = duration;
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

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder image(String image) {
            this.image = image;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public ServiceDTO build() {
            return new ServiceDTO(id, name, description, price, duration, merchantId, merchantName, category, image, status);
        }
    }
}
