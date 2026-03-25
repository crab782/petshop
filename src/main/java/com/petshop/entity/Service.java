package com.petshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "service")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;
    private String name;
    private String description;
    private Double price;
    private Integer duration;
    private String image;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
