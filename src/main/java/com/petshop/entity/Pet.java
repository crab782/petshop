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
    @JoinColumn(name = "user_id")
    private User user;
    private String name;
    private String type;
    private String breed;
    private Integer age;
    private String gender;
    private String avatar;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
