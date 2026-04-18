package com.petshop.service;

import com.petshop.entity.Merchant;
import com.petshop.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        Optional<Merchant> merchantOpt = merchantRepository.findByEmail(email);
        if (merchantOpt.isPresent() && passwordEncoder.matches(password, merchantOpt.get().getPassword()) && "approved".equals(merchantOpt.get().getStatus())) {
            return merchantOpt.get();
        }
        return null;
    }

    public Merchant findByEmail(String email) {
        return merchantRepository.findByEmail(email).orElse(null);
    }

    public Merchant findById(Integer id) {
        return merchantRepository.findById(id).orElse(null);
    }

    public List<Merchant> findAll() {
        return merchantRepository.findAll();
    }

    public Merchant update(Merchant merchant) {
        merchant.setUpdatedAt(java.time.LocalDateTime.now());
        return merchantRepository.save(merchant);
    }

    public void delete(Integer id) {
        merchantRepository.deleteById(id);
    }

    public Optional<Merchant> findByEmailOptional(String email) {
        return merchantRepository.findByEmail(email);
    }
}
