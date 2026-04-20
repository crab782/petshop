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

    public List<Merchant> searchMerchants(
            String name, String status, Double minRating, 
            int page, int pageSize) {
        // 这里需要实现商家搜索的业务逻辑
        // 暂时返回所有已批准的商家，实际实现需要根据参数进行筛选
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, pageSize);
        return merchantRepository.findByStatus("approved", pageable).getContent();
    }

    public List<Merchant> searchMerchants(
            String keyword, String sortBy, String sortOrder, 
            int page, int pageSize) {
        // 这里需要实现商家搜索的业务逻辑
        // 暂时返回所有已批准的商家，实际实现需要根据参数进行筛选和排序
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, pageSize);
        return merchantRepository.findByStatus("approved", pageable).getContent();
    }

    /**
     * 修改商家密码
     * @param id 商家ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @throws RuntimeException 密码验证失败时抛出
     */
    public void changePassword(Integer id, String oldPassword, String newPassword) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商家不存在"));
        
        if (!passwordEncoder.matches(oldPassword, merchant.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        if (newPassword.length() < 6) {
            throw new RuntimeException("新密码长度至少6位");
        }
        
        merchant.setPassword(passwordEncoder.encode(newPassword));
        merchant.setUpdatedAt(java.time.LocalDateTime.now());
        merchantRepository.save(merchant);
    }

    /**
     * 更新商家手机号
     * @param id 商家ID
     * @param phone 新手机号
     */
    public void updatePhone(Integer id, String phone) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商家不存在"));
        
        merchant.setPhone(phone);
        merchant.setUpdatedAt(java.time.LocalDateTime.now());
        merchantRepository.save(merchant);
    }

    /**
     * 更新商家邮箱
     * @param id 商家ID
     * @param email 新邮箱
     */
    public void updateEmail(Integer id, String email) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商家不存在"));
        
        // 检查邮箱是否已被其他商家使用
        Optional<Merchant> existingMerchant = merchantRepository.findByEmail(email);
        if (existingMerchant.isPresent() && !existingMerchant.get().getId().equals(id)) {
            throw new RuntimeException("邮箱已被使用");
        }
        
        merchant.setEmail(email);
        merchant.setUpdatedAt(java.time.LocalDateTime.now());
        merchantRepository.save(merchant);
    }
}
