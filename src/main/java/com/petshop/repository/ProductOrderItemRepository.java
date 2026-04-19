package com.petshop.repository;

import com.petshop.entity.ProductOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOrderItemRepository extends JpaRepository<ProductOrderItem, Integer> {
    List<ProductOrderItem> findByOrderId(Integer orderId);
    void deleteByOrderId(Integer orderId);
}
