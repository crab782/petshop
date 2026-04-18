package com.petshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantDTO {
    private Integer id;
    private String name;
    private String logo;
    private String contact;
    private String phone;
    private String address;
    private String description;
    private Double rating;
    private Boolean isFavorited;
}
