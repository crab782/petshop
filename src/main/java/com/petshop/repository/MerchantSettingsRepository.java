package com.petshop.repository;

import com.petshop.entity.MerchantSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MerchantSettingsRepository extends JpaRepository<MerchantSettings, Integer> {
    Optional<MerchantSettings> findByMerchantId(Integer merchantId);
    boolean existsByMerchantId(Integer merchantId);
}
