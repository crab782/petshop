package com.petshop.service;

import com.petshop.entity.ProductOrder;
import com.petshop.repository.ProductOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductOrderService {
    @Autowired
    private ProductOrderRepository productOrderRepository;

    public ProductOrder findById(Integer id) {
        return productOrderRepository.findById(id).orElse(null);
    }

    public List<ProductOrder> findByMerchantId(Integer merchantId) {
        return productOrderRepository.findByMerchantId(merchantId);
    }

    public List<ProductOrder> findByUserId(Integer userId) {
        return productOrderRepository.findByUserId(userId);
    }

    public List<ProductOrder> findByMerchantIdAndStatus(Integer merchantId, String status) {
        return productOrderRepository.findByMerchantIdAndStatus(merchantId, status);
    }

    public ProductOrder update(ProductOrder order) {
        return productOrderRepository.save(order);
    }
}
