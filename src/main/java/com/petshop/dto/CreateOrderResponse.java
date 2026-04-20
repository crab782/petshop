package com.petshop.dto;

public class CreateOrderResponse {
    private Integer orderId;
    private String orderNo;

    public CreateOrderResponse() {
    }

    public CreateOrderResponse(Integer orderId, String orderNo) {
        this.orderId = orderId;
        this.orderNo = orderNo;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
