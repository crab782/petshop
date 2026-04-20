package com.petshop.service;

import com.petshop.entity.SystemSettings;
import com.petshop.repository.SystemSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SystemSettingsService {
    @Autowired
    private SystemSettingsRepository systemSettingsRepository;
    
    public SystemSettings getSystemSettings() {
        return systemSettingsRepository.findById(1)
                .orElseGet(() -> {
                    SystemSettings defaultSettings = new SystemSettings();
                    defaultSettings.setId(1);
                    defaultSettings.setEmailSmtp("smtp.gmail.com");
                    defaultSettings.setEmailPort(587);
                    defaultSettings.setUploadPath("uploads/");
                    defaultSettings.setMaxFileSize(10485760L); // 10MB
                    defaultSettings.setAllowedFileTypes("jpg,jpeg,png,gif");
                    return systemSettingsRepository.save(defaultSettings);
                });
    }
    
    @Transactional
    public SystemSettings updateSystemSettings(SystemSettings settings) {
        SystemSettings existingSettings = getSystemSettings();
        
        // Update email settings
        existingSettings.setEmailSmtp(settings.getEmailSmtp());
        existingSettings.setEmailPort(settings.getEmailPort());
        existingSettings.setEmailUsername(settings.getEmailUsername());
        existingSettings.setEmailPassword(settings.getEmailPassword());
        existingSettings.setEmailFrom(settings.getEmailFrom());
        
        // Update SMS settings
        existingSettings.setSmsProvider(settings.getSmsProvider());
        existingSettings.setSmsApiKey(settings.getSmsApiKey());
        existingSettings.setSmsApiSecret(settings.getSmsApiSecret());
        existingSettings.setSmsSignName(settings.getSmsSignName());
        
        // Update WeChat settings
        existingSettings.setWechatAppId(settings.getWechatAppId());
        existingSettings.setWechatAppSecret(settings.getWechatAppSecret());
        existingSettings.setWechatMchId(settings.getWechatMchId());
        existingSettings.setWechatPayKey(settings.getWechatPayKey());
        existingSettings.setWechatPayCert(settings.getWechatPayCert());
        
        // Update Alipay settings
        existingSettings.setAlipayAppId(settings.getAlipayAppId());
        existingSettings.setAlipayPrivateKey(settings.getAlipayPrivateKey());
        existingSettings.setAlipayPublicKey(settings.getAlipayPublicKey());
        existingSettings.setAlipayNotifyUrl(settings.getAlipayNotifyUrl());
        
        // Update file upload settings
        existingSettings.setUploadPath(settings.getUploadPath());
        existingSettings.setMaxFileSize(settings.getMaxFileSize());
        existingSettings.setAllowedFileTypes(settings.getAllowedFileTypes());
        
        return systemSettingsRepository.save(existingSettings);
    }
    
    public SystemSettings findById(Integer id) {
        return systemSettingsRepository.findById(id).orElse(null);
    }
}