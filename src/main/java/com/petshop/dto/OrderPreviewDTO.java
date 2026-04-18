package com.petshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPreviewDTO {
    private java.util.List<OrderPreviewItemDTO> items;
    private BigDecimal productTotal;
    private BigDecimal shippingFee;
    private BigDecimal discount;
    private BigDecimal totalAmount;
}
