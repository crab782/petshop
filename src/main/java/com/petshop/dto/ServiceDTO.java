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
public class ServiceDTO {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration;
    private Integer merchantId;
    private String merchantName;
    private String category;
    private String image;
    private String status;
}
