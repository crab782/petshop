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
public class ProductDTO {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Integer merchantId;
    private String image;
    private String status;
    private String category;
}
