package com.petshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {
    private Integer id;
    private Integer userId;
    private String contactName;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detailAddress;
    private Boolean isDefault;
}
