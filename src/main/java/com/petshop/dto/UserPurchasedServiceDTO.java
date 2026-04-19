package com.petshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPurchasedServiceDTO {
    private Integer id;
    private String name;
    private String merchant;
    private Integer merchantId;
    private BigDecimal price;
    private LocalDateTime purchaseDate;
    private LocalDateTime expiryDate;
    private String status;
    private String category;
    private Integer serviceId;
}
