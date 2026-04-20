package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.dto.MerchantDetailDTO;
import com.petshop.entity.Merchant;
import com.petshop.mapper.MerchantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MerchantService {
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Merchant register(Merchant merchant) {
        merchant.setPassword(passwordEncoder.encode(merchant.getPassword()));
        merchant.setStatus("pending");
        merchant.setCreatedAt(java.time.LocalDateTime.now());
        merchant.setUpdatedAt(java.time.LocalDateTime.now());
        merchantMapper.insert(merchant);
        return merchant;
    }

    public Merchant login(String email, String password) {
        Merchant merchant = merchantMapper.selectOne(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getEmail, email));
        if (merchant != null && passwordEncoder.matches(password, merchant.getPassword()) 
                && "approved".equals(merchant.getStatus())) {
            return merchant;
        }
        return null;
    }

    public Merchant findByEmail(String email) {
        return merchantMapper.selectOne(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getEmail, email));
    }

    public Merchant findById(Integer id) {
        return merchantMapper.selectById(id);
    }

    public List<Merchant> findAll() {
        return merchantMapper.selectList(null);
    }

    public Merchant update(Merchant merchant) {
        merchant.setUpdatedAt(java.time.LocalDateTime.now());
        merchantMapper.updateById(merchant);
        return merchant;
    }

    public void delete(Integer id) {
        merchantMapper.deleteById(id);
    }

    public Merchant auditMerchant(Integer id, String status, String reason) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            throw new IllegalArgumentException("Merchant not found with id: " + id);
        }
        
        if (!"approved".equals(status) && !"rejected".equals(status)) {
            throw new IllegalArgumentException("Invalid status. Must be 'approved' or 'rejected'");
        }
        
        merchant.setStatus(status);
        merchant.setUpdatedAt(java.time.LocalDateTime.now());
        merchantMapper.updateById(merchant);
        return merchant;
    }

    public int batchUpdateStatus(List<Integer> ids, String status) {
        int count = 0;
        for (Integer id : ids) {
            Merchant merchant = merchantMapper.selectById(id);
            if (merchant != null) {
                merchant.setStatus(status);
                merchant.setUpdatedAt(java.time.LocalDateTime.now());
                merchantMapper.updateById(merchant);
                count++;
            }
        }
        return count;
    }

    public int batchDelete(List<Integer> ids) {
        int count = 0;
        for (Integer id : ids) {
            if (merchantMapper.selectById(id) != null) {
                merchantMapper.deleteById(id);
                count++;
            }
        }
        return count;
    }

    public MerchantDetailDTO getMerchantDetail(Integer id) {
        Merchant merchant = merchantMapper.selectById(id);
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
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getStatus, "approved");
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(Merchant::getName, name);
        }
        return merchantMapper.selectList(wrapper);
    }

    public List<Merchant> searchMerchants(
            String keyword, String sortBy, String sortOrder, 
            int page, int pageSize) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getStatus, "approved");
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Merchant::getName, keyword)
                    .or().like(Merchant::getContactPerson, keyword));
        }
        return merchantMapper.selectList(wrapper);
    }

    public void changePassword(Integer id, String oldPassword, String newPassword) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        
        if (!passwordEncoder.matches(oldPassword, merchant.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        if (newPassword.length() < 6) {
            throw new RuntimeException("新密码长度至少 6 位");
        }
        
        merchant.setPassword(passwordEncoder.encode(newPassword));
        merchant.setUpdatedAt(java.time.LocalDateTime.now());
        merchantMapper.updateById(merchant);
    }

    public void updatePhone(Integer id, String phone) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        
        merchant.setPhone(phone);
        merchant.setUpdatedAt(java.time.LocalDateTime.now());
        merchantMapper.updateById(merchant);
    }

    public void updateEmail(Integer id, String email) {
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }
        
        Merchant existingMerchant = merchantMapper.selectOne(new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getEmail, email));
        if (existingMerchant != null && !existingMerchant.getId().equals(id)) {
            throw new RuntimeException("邮箱已被使用");
        }
        
        merchant.setEmail(email);
        merchant.setUpdatedAt(java.time.LocalDateTime.now());
        merchantMapper.updateById(merchant);
    }
    
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<Merchant> findByStatus(String status, Pageable pageable) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Merchant> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<Merchant>()
                .eq(Merchant::getStatus, status)
                .orderByDesc(Merchant::getCreatedAt);
        return merchantMapper.selectPage(page, wrapper);
    }
    
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<Merchant> getPendingMerchants(String keyword, int page, int pageSize) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, pageSize);
        return findByStatus("pending", pageable);
    }
}
