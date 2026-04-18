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
public class ReviewDTO {
    private Integer id;
    private Integer userId;
    private String userName;
    private Integer merchantId;
    private Integer serviceId;
    private Integer appointmentId;
    private Integer rating;
    private String comment;
    private LocalDateTime createTime;
    private String serviceName;
    private String merchantName;
}
