package com.petshop.dto;

import java.util.List;

public class PreviewOrderRequest {
    private List<OrderItemRequest> items;
    
    public PreviewOrderRequest() {
    }

    public PreviewOrderRequest(List<OrderItemRequest> items) {
        this.items = items;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
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
