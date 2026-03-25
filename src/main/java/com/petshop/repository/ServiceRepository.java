package com.petshop.repository;

import com.petshop.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Integer> {
    List<Service> findByMerchantId(Integer merchantId);
}
