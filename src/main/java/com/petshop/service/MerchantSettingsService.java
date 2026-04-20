package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.entity.Merchant;
import com.petshop.entity.MerchantSettings;
import com.petshop.exception.ResourceNotFoundException;
import com.petshop.mapper.MerchantMapper;
import com.petshop.mapper.MerchantSettingsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class MerchantSettingsService {
    @Autowired
    private MerchantSettingsMapper settingsRepository;
    @Autowired
    private MerchantMapper merchantRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public MerchantSettings getSettings(Integer merchantId) {
        LambdaQueryWrapper<MerchantSettings> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MerchantSettings::getMerchantId, merchantId);
        MerchantSettings settings = settingsRepository.selectOne(wrapper);
        
        if (settings == null) {
            return createDefaultSettings(merchantId);
        }
        return settings;
    }

    @Transactional
    public MerchantSettings createDefaultSettings(Integer merchantId) {
        Merchant merchant = merchantRepository.selectById(merchantId);
        if (merchant == null) {
            return null;
        }
        MerchantSettings settings = new MerchantSettings();
        settings.setMerchantId(merchant.getId());
        settings.setIsOpen(true);
        settings.setAppointmentNotification(true);
        settings.setOrderNotification(true);
        settings.setReviewNotification(true);
        settings.setNotificationType("email");
        settingsRepository.insert(settings);
        return settings;
    }

    @Transactional
    public MerchantSettings updateSettings(Integer merchantId, MerchantSettings newSettings) {
        MerchantSettings settings = getSettings(merchantId);
        if (newSettings.getIsOpen() != null) {
            settings.setIsOpen(newSettings.getIsOpen());
        }
        if (newSettings.getAppointmentNotification() != null) {
            settings.setAppointmentNotification(newSettings.getAppointmentNotification());
        }
        if (newSettings.getOrderNotification() != null) {
            settings.setOrderNotification(newSettings.getOrderNotification());
        }
        if (newSettings.getReviewNotification() != null) {
            settings.setReviewNotification(newSettings.getReviewNotification());
        }
        if (newSettings.getNotificationType() != null) {
            settings.setNotificationType(newSettings.getNotificationType());
        }
        settingsRepository.updateById(settings);
        return settings;
    }

    @Transactional
    public boolean changePassword(Integer merchantId, String oldPassword, String newPassword) {
        Merchant merchant = merchantRepository.selectById(merchantId);
        if (merchant == null) {
            return false;
        }
        if (!passwordEncoder.matches(oldPassword, merchant.getPassword())) {
            return false;
        }
        merchant.setPassword(passwordEncoder.encode(newPassword));
        merchantRepository.updateById(merchant);
        return true;
    }

    @Transactional
    public boolean bindPhone(Integer merchantId, String phone, String verifyCode) {
        Merchant merchant = merchantRepository.selectById(merchantId);
        if (merchant == null) {
            return false;
        }
        merchant.setPhone(phone);
        merchantRepository.updateById(merchant);
        return true;
    }

    @Transactional
    public boolean bindEmail(Integer merchantId, String email, String verifyCode) {
        Merchant merchant = merchantRepository.selectById(merchantId);
        if (merchant == null) {
            return false;
        }
        
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getEmail, email)
               .ne(Merchant::getId, merchantId);
        if (merchantRepository.selectCount(wrapper) > 0) {
            return false;
        }
        
        merchant.setEmail(email);
        merchantRepository.updateById(merchant);
        return true;
    }

    public String generateVerifyCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public Map<String, Object> getSettingsOverview(Integer merchantId) {
        Map<String, Object> overview = new HashMap<>();
        Merchant merchant = merchantRepository.selectById(merchantId);
        if (merchant == null) {
            return overview;
        }
        overview.put("merchantId", merchant.getId());
        overview.put("merchantName", merchant.getName());
        overview.put("contactPerson", merchant.getContactPerson());
        overview.put("phone", merchant.getPhone());
        overview.put("email", merchant.getEmail());
        overview.put("address", merchant.getAddress());
        overview.put("logo", merchant.getLogo());
        overview.put("status", merchant.getStatus());
        overview.put("createdAt", merchant.getCreatedAt());
        MerchantSettings settings = getSettings(merchantId);
        overview.put("isOpen", settings.getIsOpen());
        overview.put("appointmentNotification", settings.getAppointmentNotification());
        overview.put("orderNotification", settings.getOrderNotification());
        overview.put("reviewNotification", settings.getReviewNotification());
        overview.put("notificationType", settings.getNotificationType());
        return overview;
    }

    @Transactional
    public boolean toggleShopStatus(Integer merchantId) {
        MerchantSettings settings = getSettings(merchantId);
        settings.setIsOpen(!settings.getIsOpen());
        settingsRepository.updateById(settings);
        return settings.getIsOpen();
    }
}
