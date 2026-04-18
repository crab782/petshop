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
public class NotificationDTO {
    private Integer id;
    private String type;
    private String title;
    private String summary;
    private String content;
    private Boolean isRead;
    private LocalDateTime createTime;
}
