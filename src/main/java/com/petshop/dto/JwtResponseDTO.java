package com.petshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponseDTO {
    private String token;
    private String type = "Bearer";
    private Integer id;
    private String username;
    private String email;
    private String role;
}
