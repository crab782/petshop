package com.petshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayStatusResponse {
    private Integer orderId;
    private String payStatus;
    private LocalDateTime payTime;
    private String transactionId;
}
