package com.petshop.dto;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Integer id;
    private String type;
    private String title;
    private String summary;
    private String content;
    private Boolean isRead;
    private LocalDateTime createTime;

    public NotificationDTO() {
    }

    public NotificationDTO(Integer id, String type, String title, String summary, String content, Boolean isRead, LocalDateTime createTime) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.isRead = isRead;
        this.createTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer id;
        private String type;
        private String title;
        private String summary;
        private String content;
        private Boolean isRead;
        private LocalDateTime createTime;

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder isRead(Boolean isRead) {
            this.isRead = isRead;
            return this;
        }

        public Builder createTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public NotificationDTO build() {
            return new NotificationDTO(id, type, title, summary, content, isRead, createTime);
        }
    }
}
