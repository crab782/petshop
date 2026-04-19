package com.petshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeStatsDTO {
    private long petCount;
    private long pendingAppointments;
    private long reviewCount;
}
