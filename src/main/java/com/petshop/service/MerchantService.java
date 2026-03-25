package com.petshop.service;

import com.petshop.entity.Merchant;
import com.petshop.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantService {
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Merchant register(Merchant merchant) {
        merchant.setPassword(passwordEncoder.encode(merchant.getPassword()));
        merchant.setStatus("pending");
        merchant.setCreatedAt(java.time.LocalDateTime.now());
        merchant.setUpdatedAt(java.time.LocalDateTime.now());
        return merchantRepository.save(merchant);
    }

    public Merchant login(String email, String password) {
        Merchant merchant = merchantRepository.findByEmail(email);
        if (merchant != null && passwordEncoder.matches(password, merchant.getPassword()) && "approved".equals(merchant.getStatus())) {
            return merchant;
        }
        return null;
    }

    public Merchant findByEmail(String email) {
        return merchantRepository.findByEmail(email);
    }

    public Merchant findById(Integer id) {
        return merchantRepository.findById(id).orElse(null);
    }

    public List<Merchant> findAll() {
        return merchantRepository.findAll();
    }

    public Merchant update(Merchant merchant) {
        return merchantRepository.save(merchant);
    }

    public void delete(Integer id) {
        merchantRepository.deleteById(id);
    }
}
