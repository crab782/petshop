package com.petshop.dto;

public class HomeStatsDTO {
    private long petCount;
    private long pendingAppointments;
    private long reviewCount;

    public HomeStatsDTO() {
    }

    public HomeStatsDTO(long petCount, long pendingAppointments, long reviewCount) {
        this.petCount = petCount;
        this.pendingAppointments = pendingAppointments;
        this.reviewCount = reviewCount;
    }

    public long getPetCount() {
        return petCount;
    }

    public void setPetCount(long petCount) {
        this.petCount = petCount;
    }

    public long getPendingAppointments() {
        return pendingAppointments;
    }

    public void setPendingAppointments(long pendingAppointments) {
        this.pendingAppointments = pendingAppointments;
    }

    public long getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(long reviewCount) {
        this.reviewCount = reviewCount;
    }
}
