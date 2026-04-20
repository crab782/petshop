package com.petshop.dto;

import java.math.BigDecimal;

public class FavoriteProductDTO {
    private Integer id;
    private Integer productId;
    private String productName;
    private String productImage;
    private BigDecimal productPrice;
    private Integer merchantId;
    private String merchantName;
    private String createdAt;

    public FavoriteProductDTO() {
    }

    public FavoriteProductDTO(Integer id, Integer productId, String productName, String productImage, BigDecimal productPrice, Integer merchantId, String merchantName, String createdAt) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.productPrice = productPrice;
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

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
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
        private Integer productId;
        private String productName;
        private String productImage;
        private BigDecimal productPrice;
        private Integer merchantId;
        private String merchantName;
        private String createdAt;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder productId(Integer productId) {
            this.productId = productId;
            return this;
        }

        public Builder productName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder productImage(String productImage) {
            this.productImage = productImage;
            return this;
        }

        public Builder productPrice(BigDecimal productPrice) {
            this.productPrice = productPrice;
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

        public FavoriteProductDTO build() {
            return new FavoriteProductDTO(id, productId, productName, productImage, productPrice, merchantId, merchantName, createdAt);
        }
    }
}
