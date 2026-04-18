package com.petshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "merchant_id", nullable = false)
    private Integer merchantId;
    
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    
    @Column(name = "icon", length = 255)
    private String icon;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "sort", nullable = false)
    private Integer sort = 0;
    
    @Column(name = "status", columnDefinition = "ENUM('enabled', 'disabled') DEFAULT 'enabled'")
    private String status = "enabled";
    
    @Column(name = "product_count", nullable = false)
    private Integer productCount = 0;
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}
