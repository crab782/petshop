package com.petshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "contact_name", length = 50, nullable = false)
    private String contactName;
    
    @Column(name = "phone", length = 20, nullable = false)
    private String phone;
    
    @Column(name = "province", length = 50, nullable = false)
    private String province;
    
    @Column(name = "city", length = 50, nullable = false)
    private String city;
    
    @Column(name = "district", length = 50, nullable = false)
    private String district;
    
    @Column(name = "detail_address", length = 255, nullable = false)
    private String detailAddress;
    
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}
