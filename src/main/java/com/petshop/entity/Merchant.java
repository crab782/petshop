package com.petshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "merchant")
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String contactPerson;
    private String phone;
    private String email;
    private String password;
    private String address;
    private String logo;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
