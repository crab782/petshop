package com.petshop.repository;

import com.petshop.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {
    List<ProductOrder> findByMerchantId(Integer merchantId);
    List<ProductOrder> findByUserId(Integer userId);
    List<ProductOrder> findByMerchantIdAndStatus(Integer merchantId, String status);
}
