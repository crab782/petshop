package com.petshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
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
}
