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
@TableName("system_settings")
public class SystemSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("email_smtp")
    private String emailSmtp;

    @TableField("email_port")
    private Integer emailPort;

    @TableField("email_username")
    private String emailUsername;

    @TableField("email_password")
    private String emailPassword;

    @TableField("email_from")
    private String emailFrom;

    @TableField("sms_provider")
    private String smsProvider;

    @TableField("sms_api_key")
    private String smsApiKey;

    @TableField("sms_api_secret")
    private String smsApiSecret;

    @TableField("sms_sign_name")
    private String smsSignName;

    @TableField("wechat_app_id")
    private String wechatAppId;

    @TableField("wechat_app_secret")
    private String wechatAppSecret;

    @TableField("wechat_mch_id")
    private String wechatMchId;

    @TableField("wechat_pay_key")
    private String wechatPayKey;

    @TableField("wechat_pay_cert")
    private String wechatPayCert;

    @TableField("alipay_app_id")
    private String alipayAppId;

    @TableField("alipay_private_key")
    private String alipayPrivateKey;

    @TableField("alipay_public_key")
    private String alipayPublicKey;

    @TableField("alipay_notify_url")
    private String alipayNotifyUrl;

    @TableField("upload_path")
    private String uploadPath;

    @TableField("max_file_size")
    private Long maxFileSize;

    @TableField("allowed_file_types")
    private String allowedFileTypes;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getEmailSmtp() { return emailSmtp; }
    public void setEmailSmtp(String emailSmtp) { this.emailSmtp = emailSmtp; }
    public Integer getEmailPort() { return emailPort; }
    public void setEmailPort(Integer emailPort) { this.emailPort = emailPort; }
    public String getEmailUsername() { return emailUsername; }
    public void setEmailUsername(String emailUsername) { this.emailUsername = emailUsername; }
    public String getEmailPassword() { return emailPassword; }
    public void setEmailPassword(String emailPassword) { this.emailPassword = emailPassword; }
    public String getEmailFrom() { return emailFrom; }
    public void setEmailFrom(String emailFrom) { this.emailFrom = emailFrom; }
    public String getSmsProvider() { return smsProvider; }
    public void setSmsProvider(String smsProvider) { this.smsProvider = smsProvider; }
    public String getSmsApiKey() { return smsApiKey; }
    public void setSmsApiKey(String smsApiKey) { this.smsApiKey = smsApiKey; }
    public String getSmsApiSecret() { return smsApiSecret; }
    public void setSmsApiSecret(String smsApiSecret) { this.smsApiSecret = smsApiSecret; }
    public String getSmsSignName() { return smsSignName; }
    public void setSmsSignName(String smsSignName) { this.smsSignName = smsSignName; }
    public String getWechatAppId() { return wechatAppId; }
    public void setWechatAppId(String wechatAppId) { this.wechatAppId = wechatAppId; }
    public String getWechatAppSecret() { return wechatAppSecret; }
    public void setWechatAppSecret(String wechatAppSecret) { this.wechatAppSecret = wechatAppSecret; }
    public String getWechatMchId() { return wechatMchId; }
    public void setWechatMchId(String wechatMchId) { this.wechatMchId = wechatMchId; }
    public String getWechatPayKey() { return wechatPayKey; }
    public void setWechatPayKey(String wechatPayKey) { this.wechatPayKey = wechatPayKey; }
    public String getWechatPayCert() { return wechatPayCert; }
    public void setWechatPayCert(String wechatPayCert) { this.wechatPayCert = wechatPayCert; }
    public String getAlipayAppId() { return alipayAppId; }
    public void setAlipayAppId(String alipayAppId) { this.alipayAppId = alipayAppId; }
    public String getAlipayPrivateKey() { return alipayPrivateKey; }
    public void setAlipayPrivateKey(String alipayPrivateKey) { this.alipayPrivateKey = alipayPrivateKey; }
    public String getAlipayPublicKey() { return alipayPublicKey; }
    public void setAlipayPublicKey(String alipayPublicKey) { this.alipayPublicKey = alipayPublicKey; }
    public String getAlipayNotifyUrl() { return alipayNotifyUrl; }
    public void setAlipayNotifyUrl(String alipayNotifyUrl) { this.alipayNotifyUrl = alipayNotifyUrl; }
    public String getUploadPath() { return uploadPath; }
    public void setUploadPath(String uploadPath) { this.uploadPath = uploadPath; }
    public Long getMaxFileSize() { return maxFileSize; }
    public void setMaxFileSize(Long maxFileSize) { this.maxFileSize = maxFileSize; }
    public String getAllowedFileTypes() { return allowedFileTypes; }
    public void setAllowedFileTypes(String allowedFileTypes) { this.allowedFileTypes = allowedFileTypes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
