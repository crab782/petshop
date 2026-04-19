package com.petshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "system_settings")
public class SystemSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "email_smtp", length = 100)
    private String emailSmtp;
    
    @Column(name = "email_port")
    private Integer emailPort;
    
    @Column(name = "email_username", length = 100)
    private String emailUsername;
    
    @Column(name = "email_password", length = 255)
    private String emailPassword;
    
    @Column(name = "email_from", length = 100)
    private String emailFrom;
    
    @Column(name = "sms_provider", length = 50)
    private String smsProvider;
    
    @Column(name = "sms_api_key", length = 255)
    private String smsApiKey;
    
    @Column(name = "sms_api_secret", length = 255)
    private String smsApiSecret;
    
    @Column(name = "sms_sign_name", length = 50)
    private String smsSignName;
    
    @Column(name = "wechat_app_id", length = 100)
    private String wechatAppId;
    
    @Column(name = "wechat_app_secret", length = 255)
    private String wechatAppSecret;
    
    @Column(name = "wechat_mch_id", length = 50)
    private String wechatMchId;
    
    @Column(name = "wechat_pay_key", length = 255)
    private String wechatPayKey;
    
    @Column(name = "wechat_pay_cert", columnDefinition = "TEXT")
    private String wechatPayCert;
    
    @Column(name = "alipay_app_id", length = 100)
    private String alipayAppId;
    
    @Column(name = "alipay_private_key", columnDefinition = "TEXT")
    private String alipayPrivateKey;
    
    @Column(name = "alipay_public_key", columnDefinition = "TEXT")
    private String alipayPublicKey;
    
    @Column(name = "alipay_notify_url", length = 255)
    private String alipayNotifyUrl;
    
    @Column(name = "upload_path", length = 255)
    private String uploadPath;
    
    @Column(name = "max_file_size")
    private Long maxFileSize;
    
    @Column(name = "allowed_file_types", length = 255)
    private String allowedFileTypes;
    
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
