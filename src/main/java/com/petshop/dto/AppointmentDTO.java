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
public class AppointmentDTO {
    private Integer id;
    private Integer userId;
    private Integer serviceId;
    private Integer merchantId;
    private String serviceName;
    private String merchantName;
    private LocalDateTime appointmentTime;
    private String status;
    private String remark;
    private BigDecimal totalPrice;
    private Integer petId;
    private String petName;
    private String petType;
    private String merchantPhone;
    private String merchantAddress;
    private BigDecimal servicePrice;
    private Integer serviceDuration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
