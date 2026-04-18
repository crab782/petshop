package com.petshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetDTO {
    private Integer id;
    private String name;
    private String type;
    private String breed;
    private Integer age;
    private String gender;
    private String avatar;
    private String description;
    private Double weight;
    private String bodyType;
    private String furColor;
    private String personality;
    private Integer userId;
}
