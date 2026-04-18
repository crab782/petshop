package com.petshop.service;

import com.petshop.entity.Merchant;
import com.petshop.entity.MerchantSettings;
import com.petshop.repository.MerchantRepository;
import com.petshop.repository.MerchantSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class MerchantSettingsService {
    @Autowired
    private MerchantSettingsRepository settingsRepository;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public MerchantSettings getSettings(Integer merchantId) {
        return settingsRepository.findByMerchantId(merchantId)
                .orElseGet(() -> createDefaultSettings(merchantId));
    }

    @Transactional
    public MerchantSettings createDefaultSettings(Integer merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId).orElse(null);
        if (merchant == null) {
            return null;
        }
        MerchantSettings settings = new MerchantSettings();
        settings.setMerchant(merchant);
        settings.setIsOpen(true);
        settings.setAppointmentNotification(true);
        settings.setOrderNotification(true);
        settings.setReviewNotification(true);
        settings.setNotificationType("email");
        return settingsRepository.save(settings);
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
        return settingsRepository.save(settings);
    }

    @Transactional
    public boolean changePassword(Integer merchantId, String oldPassword, String newPassword) {
        Merchant merchant = merchantRepository.findById(merchantId).orElse(null);
        if (merchant == null) {
            return false;
        }
        if (!passwordEncoder.matches(oldPassword, merchant.getPassword())) {
            return false;
        }
        merchant.setPassword(passwordEncoder.encode(newPassword));
        merchantRepository.save(merchant);
        return true;
    }

    @Transactional
    public boolean bindPhone(Integer merchantId, String phone, String verifyCode) {
        Merchant merchant = merchantRepository.findById(merchantId).orElse(null);
        if (merchant == null) {
            return false;
        }
        merchant.setPhone(phone);
        merchantRepository.save(merchant);
        return true;
    }

    @Transactional
    public boolean bindEmail(Integer merchantId, String email, String verifyCode) {
        Merchant merchant = merchantRepository.findById(merchantId).orElse(null);
        if (merchant == null) {
            return false;
        }
        Optional<Merchant> existingMerchantOpt = merchantRepository.findByEmail(email);
        if (existingMerchantOpt.isPresent() && !existingMerchantOpt.get().getId().equals(merchantId)) {
            return false;
        }
        merchant.setEmail(email);
        merchantRepository.save(merchant);
        return true;
    }

    public String generateVerifyCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public Map<String, Object> getSettingsOverview(Integer merchantId) {
        Map<String, Object> overview = new HashMap<>();
        Merchant merchant = merchantRepository.findById(merchantId).orElse(null);
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
        settingsRepository.save(settings);
        return settings.getIsOpen();
    }
}
