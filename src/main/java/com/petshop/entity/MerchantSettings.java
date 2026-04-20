package com.petshop.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("merchant_settings")
public class MerchantSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("merchant_id")
    private Integer merchantId;

    @TableField("is_open")
    private Boolean isOpen = true;

    @TableField("appointment_notification")
    private Boolean appointmentNotification = true;

    @TableField("order_notification")
    private Boolean orderNotification = true;

    @TableField("review_notification")
    private Boolean reviewNotification = true;

    @TableField("notification_type")
    private String notificationType = "email";

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getMerchantId() { return merchantId; }
    public void setMerchantId(Integer merchantId) { this.merchantId = merchantId; }
    public Boolean getIsOpen() { return isOpen; }
    public void setIsOpen(Boolean isOpen) { this.isOpen = isOpen; }
    public Boolean getAppointmentNotification() { return appointmentNotification; }
    public void setAppointmentNotification(Boolean appointmentNotification) { this.appointmentNotification = appointmentNotification; }
    public Boolean getOrderNotification() { return orderNotification; }
    public void setOrderNotification(Boolean orderNotification) { this.orderNotification = orderNotification; }
    public Boolean getReviewNotification() { return reviewNotification; }
    public void setReviewNotification(Boolean reviewNotification) { this.reviewNotification = reviewNotification; }
    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
