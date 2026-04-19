package com.petshop.service;

import com.petshop.dto.MerchantDetailDTO;
import com.petshop.entity.Merchant;
import com.petshop.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<Merchant> getPendingMerchants(String keyword, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return merchantRepository.findPendingByKeyword(keyword.trim(), pageable);
        }
        return merchantRepository.findByStatus("pending", pageable);
    }

    public Merchant auditMerchant(Integer id, String status, String reason) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found with id: " + id));
        
        if (!"approved".equals(status) && !"rejected".equals(status)) {
            throw new IllegalArgumentException("Invalid status. Must be 'approved' or 'rejected'");
        }
        
        merchant.setStatus(status);
        merchant.setUpdatedAt(java.time.LocalDateTime.now());
        return merchantRepository.save(merchant);
    }

    public int batchUpdateStatus(List<Integer> ids, String status) {
        int count = 0;
        for (Integer id : ids) {
            Merchant merchant = merchantRepository.findById(id).orElse(null);
            if (merchant != null) {
                merchant.setStatus(status);
                merchant.setUpdatedAt(java.time.LocalDateTime.now());
                merchantRepository.save(merchant);
                count++;
            }
        }
        return count;
    }

    public int batchDelete(List<Integer> ids) {
        int count = 0;
        for (Integer id : ids) {
            if (merchantRepository.existsById(id)) {
                merchantRepository.deleteById(id);
                count++;
            }
        }
        return count;
    }

    public MerchantDetailDTO getMerchantDetail(Integer id) {
        Merchant merchant = merchantRepository.findById(id).orElse(null);
        if (merchant == null) {
            return null;
        }
        
        MerchantDetailDTO dto = new MerchantDetailDTO();
        dto.setId(merchant.getId());
        dto.setName(merchant.getName());
        dto.setContactPerson(merchant.getContactPerson());
        dto.setPhone(merchant.getPhone());
        dto.setEmail(merchant.getEmail());
        dto.setAddress(merchant.getAddress());
        dto.setLogo(merchant.getLogo());
        dto.setStatus(merchant.getStatus());
        dto.setCreatedAt(merchant.getCreatedAt());
        dto.setUpdatedAt(merchant.getUpdatedAt());
        
        return dto;
    }
}
