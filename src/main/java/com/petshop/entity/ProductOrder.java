package com.petshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product_order")
public class ProductOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "order_no", length = 50, unique = true)
    private String orderNo;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;
    
    @Column(name = "total_price", columnDefinition = "decimal(10,2)", nullable = false)
    private BigDecimal totalPrice;
    
    @Column(name = "freight", columnDefinition = "decimal(10,2)")
    private BigDecimal freight = BigDecimal.ZERO;
    
    @Column(name = "status", columnDefinition = "enum('pending','paid','shipped','completed','cancelled') default 'pending'")
    private String status = "pending";
    
    @Column(name = "pay_method", length = 20)
    private String payMethod;
    
    @Column(name = "remark", length = 500)
    private String remark;
    
    @Column(name = "shipping_address", length = 255, nullable = false)
    private String shippingAddress;
    
    @Column(name = "logistics_company", length = 50)
    private String logisticsCompany;
    
    @Column(name = "logistics_number", length = 100)
    private String logisticsNumber;
    
    @Column(name = "transaction_id", length = 100)
    private String transactionId;
    
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    
    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;
    
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (orderNo == null) {
            orderNo = "PO" + System.currentTimeMillis();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}