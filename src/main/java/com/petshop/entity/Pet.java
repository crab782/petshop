package com.petshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pet")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "name", length = 50, nullable = false)
    private String name;
    
    @Column(name = "type", length = 50, nullable = false)
    private String type;
    
    @Column(name = "breed", length = 50)
    private String breed;
    
    @Column(name = "age")
    private Integer age;
    
    @Column(name = "gender", columnDefinition = "ENUM('male', 'female')")
    private String gender;
    
    @Column(name = "avatar", length = 255)
    private String avatar;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}
