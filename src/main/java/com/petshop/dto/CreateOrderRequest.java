package com.petshop.dto;

import java.util.List;

public class CreateOrderRequest {
    private List<OrderItemRequest> items;
    private Integer addressId;
    private String paymentMethod;
    private String remark;
    
    public CreateOrderRequest() {
    }
    
    public CreateOrderRequest(List<OrderItemRequest> items, Integer addressId, String paymentMethod, String remark) {
        this.items = items;
        this.addressId = addressId;
        this.paymentMethod = paymentMethod;
        this.remark = remark;
    }
    
    public List<OrderItemRequest> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
    
    public Integer getAddressId() {
        return addressId;
    }
    
    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public static class OrderItemRequest {
        private Integer productId;
        private Integer quantity;
        
        public OrderItemRequest() {
        }
        
        public OrderItemRequest(Integer productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
        
        public Integer getProductId() {
            return productId;
        }
        
        public void setProductId(Integer productId) {
            this.productId = productId;
        }
        
        public Integer getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
