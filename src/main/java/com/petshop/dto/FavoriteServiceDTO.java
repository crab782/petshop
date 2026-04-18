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
public class FavoriteServiceDTO {
    private Integer id;
    private Integer serviceId;
    private String serviceName;
    private String serviceImage;
    private BigDecimal servicePrice;
    private Integer serviceDuration;
    private Integer merchantId;
    private String merchantName;
    private String createdAt;
}
