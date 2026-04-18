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
public class OrderItemDTO {
    private Integer id;
    private Integer productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
