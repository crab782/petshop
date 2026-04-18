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
}
