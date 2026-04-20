package com.petshop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmailSmtp() {
        return emailSmtp;
    }

    public void setEmailSmtp(String emailSmtp) {
        this.emailSmtp = emailSmtp;
    }

    public Integer getEmailPort() {
        return emailPort;
    }

    public void setEmailPort(Integer emailPort) {
        this.emailPort = emailPort;
    }

    public String getEmailUsername() {
        return emailUsername;
    }

    public void setEmailUsername(String emailUsername) {
        this.emailUsername = emailUsername;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    public String getSmsProvider() {
        return smsProvider;
    }

    public void setSmsProvider(String smsProvider) {
        this.smsProvider = smsProvider;
    }

    public String getSmsApiKey() {
        return smsApiKey;
    }

    public void setSmsApiKey(String smsApiKey) {
        this.smsApiKey = smsApiKey;
    }

    public String getSmsApiSecret() {
        return smsApiSecret;
    }

    public void setSmsApiSecret(String smsApiSecret) {
        this.smsApiSecret = smsApiSecret;
    }

    public String getSmsSignName() {
        return smsSignName;
    }

    public void setSmsSignName(String smsSignName) {
        this.smsSignName = smsSignName;
    }

    public String getWechatAppId() {
        return wechatAppId;
    }

    public void setWechatAppId(String wechatAppId) {
        this.wechatAppId = wechatAppId;
    }

    public String getWechatAppSecret() {
        return wechatAppSecret;
    }

    public void setWechatAppSecret(String wechatAppSecret) {
        this.wechatAppSecret = wechatAppSecret;
    }

    public String getWechatMchId() {
        return wechatMchId;
    }

    public void setWechatMchId(String wechatMchId) {
        this.wechatMchId = wechatMchId;
    }

    public String getWechatPayKey() {
        return wechatPayKey;
    }

    public void setWechatPayKey(String wechatPayKey) {
        this.wechatPayKey = wechatPayKey;
    }

    public String getWechatPayCert() {
        return wechatPayCert;
    }

    public void setWechatPayCert(String wechatPayCert) {
        this.wechatPayCert = wechatPayCert;
    }

    public String getAlipayAppId() {
        return alipayAppId;
    }

    public void setAlipayAppId(String alipayAppId) {
        this.alipayAppId = alipayAppId;
    }

    public String getAlipayPrivateKey() {
        return alipayPrivateKey;
    }

    public void setAlipayPrivateKey(String alipayPrivateKey) {
        this.alipayPrivateKey = alipayPrivateKey;
    }

    public String getAlipayPublicKey() {
        return alipayPublicKey;
    }

    public void setAlipayPublicKey(String alipayPublicKey) {
        this.alipayPublicKey = alipayPublicKey;
    }

    public String getAlipayNotifyUrl() {
        return alipayNotifyUrl;
    }

    public void setAlipayNotifyUrl(String alipayNotifyUrl) {
        this.alipayNotifyUrl = alipayNotifyUrl;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public Long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String getAllowedFileTypes() {
        return allowedFileTypes;
    }

    public void setAllowedFileTypes(String allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
