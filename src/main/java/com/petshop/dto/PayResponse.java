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
public class PayResponse {
    private Integer payId;
    private String qrCodeUrl;
    private String redirectUrl;
    private LocalDateTime expireTime;
}
