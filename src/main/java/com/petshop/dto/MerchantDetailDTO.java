package com.petshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantDetailDTO {
    private Integer id;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String logo;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private List<ServiceDTO> services;
    private List<ProductDTO> products;
    private List<AppointmentDTO> appointments;
    private List<ReviewDTO> reviews;
    
    private Long serviceCount;
    private Long productCount;
    private Long appointmentCount;
    private Long reviewCount;
    private Double averageRating;
}
