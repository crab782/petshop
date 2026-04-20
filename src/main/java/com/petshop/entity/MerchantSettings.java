package com.petshop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "merchant_settings")
public class MerchantSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @OneToOne
    @JoinColumn(name = "merchant_id", nullable = false, unique = true)
    private Merchant merchant;
    
    @Column(name = "is_open", nullable = false)
    private Boolean isOpen = true;
    
    @Column(name = "appointment_notification", nullable = false)
    private Boolean appointmentNotification = true;
    
    @Column(name = "order_notification", nullable = false)
    private Boolean orderNotification = true;
    
    @Column(name = "review_notification", nullable = false)
    private Boolean reviewNotification = true;
    
    @Column(name = "notification_type", length = 20)
    private String notificationType = "email";
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public Boolean getAppointmentNotification() {
        return appointmentNotification;
    }

    public void setAppointmentNotification(Boolean appointmentNotification) {
        this.appointmentNotification = appointmentNotification;
    }

    public Boolean getOrderNotification() {
        return orderNotification;
    }

    public void setOrderNotification(Boolean orderNotification) {
        this.orderNotification = orderNotification;
    }

    public Boolean getReviewNotification() {
        return reviewNotification;
    }

    public void setReviewNotification(Boolean reviewNotification) {
        this.reviewNotification = reviewNotification;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
