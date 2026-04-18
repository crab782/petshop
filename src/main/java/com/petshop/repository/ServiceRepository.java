package com.petshop.repository;

import com.petshop.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Integer> {
    List<Service> findByMerchantId(Integer merchantId);
    
    @Modifying
    @Query("UPDATE Service s SET s.status = :status WHERE s.id IN :ids AND s.merchant.id = :merchantId")
    int batchUpdateStatus(List<Integer> ids, String status, Integer merchantId);
}
