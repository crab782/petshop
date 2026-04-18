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
public class FavoriteDTO {
    private Integer id;
    private Integer userId;
    private Integer merchantId;
    private String merchantName;
    private String merchantLogo;
    private String merchantAddress;
    private String merchantPhone;
    private LocalDateTime createTime;
}
