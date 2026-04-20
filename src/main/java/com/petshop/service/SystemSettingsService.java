package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.entity.SystemSettings;
import com.petshop.mapper.SystemSettingsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemSettingsService {
    @Autowired
    private SystemSettingsMapper systemSettingsRepository;
    
    public SystemSettings getSystemSettings() {
        SystemSettings settings = systemSettingsRepository.selectById(1);
        
        if (settings == null) {
            SystemSettings defaultSettings = new SystemSettings();
            defaultSettings.setId(1);
            defaultSettings.setEmailSmtp("smtp.gmail.com");
            defaultSettings.setEmailPort(587);
            defaultSettings.setUploadPath("uploads/");
            defaultSettings.setMaxFileSize(10485760L);
            defaultSettings.setAllowedFileTypes("jpg,jpeg,png,gif");
            systemSettingsRepository.insert(defaultSettings);
            return defaultSettings;
        }
        
        return settings;
    }
    
    @Transactional
    public SystemSettings updateSystemSettings(SystemSettings settings) {
        SystemSettings existingSettings = getSystemSettings();
        
        existingSettings.setEmailSmtp(settings.getEmailSmtp());
        existingSettings.setEmailPort(settings.getEmailPort());
        existingSettings.setEmailUsername(settings.getEmailUsername());
        existingSettings.setEmailPassword(settings.getEmailPassword());
        existingSettings.setEmailFrom(settings.getEmailFrom());
        
        existingSettings.setSmsProvider(settings.getSmsProvider());
        existingSettings.setSmsApiKey(settings.getSmsApiKey());
        existingSettings.setSmsApiSecret(settings.getSmsApiSecret());
        existingSettings.setSmsSignName(settings.getSmsSignName());
        
        existingSettings.setWechatAppId(settings.getWechatAppId());
        existingSettings.setWechatAppSecret(settings.getWechatAppSecret());
        existingSettings.setWechatMchId(settings.getWechatMchId());
        existingSettings.setWechatPayKey(settings.getWechatPayKey());
        existingSettings.setWechatPayCert(settings.getWechatPayCert());
        
        existingSettings.setAlipayAppId(settings.getAlipayAppId());
        existingSettings.setAlipayPrivateKey(settings.getAlipayPrivateKey());
        existingSettings.setAlipayPublicKey(settings.getAlipayPublicKey());
        existingSettings.setAlipayNotifyUrl(settings.getAlipayNotifyUrl());
        
        existingSettings.setUploadPath(settings.getUploadPath());
        existingSettings.setMaxFileSize(settings.getMaxFileSize());
        existingSettings.setAllowedFileTypes(settings.getAllowedFileTypes());
        
        systemSettingsRepository.updateById(existingSettings);
        return existingSettings;
    }
    
    public SystemSettings findById(Integer id) {
        return systemSettingsRepository.selectById(id);
    }
}
