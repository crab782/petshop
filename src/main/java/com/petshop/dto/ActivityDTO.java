package com.petshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityDTO {
    private Integer id;
    private String type;
    private String title;
    private String time;
    private String status;
    private String statusColor;
    private Integer relatedId;
}
