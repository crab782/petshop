package com.petshop.repository;

import com.petshop.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Integer> {
    Merchant findByEmail(String email);
}
