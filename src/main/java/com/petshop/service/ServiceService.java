package com.petshop.service;

import com.petshop.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceService {
    @Autowired
    private ServiceRepository serviceRepository;

    public com.petshop.entity.Service create(com.petshop.entity.Service service) {
        return serviceRepository.save(service);
    }

    public com.petshop.entity.Service findById(Integer id) {
        return serviceRepository.findById(id).orElse(null);
    }

    public List<com.petshop.entity.Service> findByMerchantId(Integer merchantId) {
        return serviceRepository.findByMerchantId(merchantId);
    }

    public List<com.petshop.entity.Service> findAll() {
        return serviceRepository.findAll();
    }

    public com.petshop.entity.Service update(com.petshop.entity.Service service) {
        return serviceRepository.save(service);
    }

    public void delete(Integer id) {
        serviceRepository.deleteById(id);
    }
    
    @Transactional
    public int batchUpdateStatus(List<Integer> ids, String status, Integer merchantId) {
        return serviceRepository.batchUpdateStatus(ids, status, merchantId);
    }
}
