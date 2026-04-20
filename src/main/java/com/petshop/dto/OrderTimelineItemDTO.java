package com.petshop.dto;

public class OrderTimelineItemDTO {
    private String status;
    private String time;
    private String description;

    public OrderTimelineItemDTO() {
    }

    public OrderTimelineItemDTO(String status, String time, String description) {
        this.status = status;
        this.time = time;
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String status;
        private String time;
        private String description;

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder time(String time) {
            this.time = time;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public OrderTimelineItemDTO build() {
            return new OrderTimelineItemDTO(status, time, description);
        }
    }
}
