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
public class ProductOrderDTO {
    private Integer id;
    private String productName;
    private String merchantName;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String status;
    private String orderTime;
}
