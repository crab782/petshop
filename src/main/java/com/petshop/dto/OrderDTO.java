package com.petshop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {
    private Integer id;
    private String orderNo;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime completeTime;
    private LocalDateTime cancelTime;
    private BigDecimal totalPrice;
    private BigDecimal freight;
    private String payMethod;
    private String remark;
    private List<OrderItemDTO> items;
    private OrderAddressDTO address;
    private List<OrderTimelineItemDTO> timeline;

    public OrderDTO() {
    }

    public OrderDTO(Integer id, String orderNo, String status, LocalDateTime createTime, LocalDateTime payTime, LocalDateTime shipTime, LocalDateTime completeTime, LocalDateTime cancelTime, BigDecimal totalPrice, BigDecimal freight, String payMethod, String remark, List<OrderItemDTO> items, OrderAddressDTO address, List<OrderTimelineItemDTO> timeline) {
        this.id = id;
        this.orderNo = orderNo;
        this.status = status;
        this.createTime = createTime;
        this.payTime = payTime;
        this.shipTime = shipTime;
        this.completeTime = completeTime;
        this.cancelTime = cancelTime;
        this.totalPrice = totalPrice;
        this.freight = freight;
        this.payMethod = payMethod;
        this.remark = remark;
        this.items = items;
        this.address = address;
        this.timeline = timeline;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public LocalDateTime getShipTime() {
        return shipTime;
    }

    public void setShipTime(LocalDateTime shipTime) {
        this.shipTime = shipTime;
    }

    public LocalDateTime getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(LocalDateTime completeTime) {
        this.completeTime = completeTime;
    }

    public LocalDateTime getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(LocalDateTime cancelTime) {
        this.cancelTime = cancelTime;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public OrderAddressDTO getAddress() {
        return address;
    }

    public void setAddress(OrderAddressDTO address) {
        this.address = address;
    }

    public List<OrderTimelineItemDTO> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<OrderTimelineItemDTO> timeline) {
        this.timeline = timeline;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String orderNo;
        private String status;
        private LocalDateTime createTime;
        private LocalDateTime payTime;
        private LocalDateTime shipTime;
        private LocalDateTime completeTime;
        private LocalDateTime cancelTime;
        private BigDecimal totalPrice;
        private BigDecimal freight;
        private String payMethod;
        private String remark;
        private List<OrderItemDTO> items;
        private OrderAddressDTO address;
        private List<OrderTimelineItemDTO> timeline;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder orderNo(String orderNo) {
            this.orderNo = orderNo;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder createTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder payTime(LocalDateTime payTime) {
            this.payTime = payTime;
            return this;
        }

        public Builder shipTime(LocalDateTime shipTime) {
            this.shipTime = shipTime;
            return this;
        }

        public Builder completeTime(LocalDateTime completeTime) {
            this.completeTime = completeTime;
            return this;
        }

        public Builder cancelTime(LocalDateTime cancelTime) {
            this.cancelTime = cancelTime;
            return this;
        }

        public Builder totalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public Builder freight(BigDecimal freight) {
            this.freight = freight;
            return this;
        }

        public Builder payMethod(String payMethod) {
            this.payMethod = payMethod;
            return this;
        }

        public Builder remark(String remark) {
            this.remark = remark;
            return this;
        }

        public Builder items(List<OrderItemDTO> items) {
            this.items = items;
            return this;
        }

        public Builder address(OrderAddressDTO address) {
            this.address = address;
            return this;
        }

        public Builder timeline(List<OrderTimelineItemDTO> timeline) {
            this.timeline = timeline;
            return this;
        }

        public OrderDTO build() {
            return new OrderDTO(id, orderNo, status, createTime, payTime, shipTime, completeTime, cancelTime, totalPrice, freight, payMethod, remark, items, address, timeline);
        }
    }
}
