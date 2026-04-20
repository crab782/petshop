package com.petshop.service;

import com.petshop.dto.ServiceDTO;
import com.petshop.repository.ReviewRepository;
import com.petshop.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceService {
    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;

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
    
    public org.springframework.data.domain.Page<com.petshop.entity.Service> findAll(org.springframework.data.domain.Pageable pageable) {
        return serviceRepository.findAll(pageable);
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
    
    public List<com.petshop.entity.Service> findByType(String type) {
        if (type == null || type.isEmpty()) {
            return serviceRepository.findByStatus("enabled");
        }
        return serviceRepository.findByCategory(type);
    }
    
    public List<com.petshop.entity.Service> searchByKeyword(String keyword) {
        return serviceRepository.searchByKeyword(keyword);
    }
    
    public List<com.petshop.entity.Service> findRecommended(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return serviceRepository.findByStatus("enabled");
    }

    public List<com.petshop.entity.Service> searchServices(
            String name, Double minPrice, Double maxPrice, 
            Integer minDuration, Integer maxDuration, 
            String status, int page, int pageSize) {
        // 这里需要实现服务搜索的业务逻辑
        // 暂时返回所有启用的服务，实际实现需要根据参数进行筛选
        return serviceRepository.findByStatus("enabled");
    }

    public List<com.petshop.entity.Service> searchServices(
            String keyword, Integer merchantId, 
            String sortBy, String sortOrder, 
            int page, int pageSize) {
        // 这里需要实现服务搜索的业务逻辑
        // 暂时返回所有启用的服务，实际实现需要根据参数进行筛选和排序
        return serviceRepository.findByStatus("enabled");
    }

    public List<com.petshop.entity.Service> getRecommendedServices(int limit) {
        // 这里需要实现推荐服务的业务逻辑
        // 暂时返回所有启用的服务，实际实现需要根据推荐算法进行筛选
        return serviceRepository.findByStatus("enabled");
    }
    
    public ServiceDTO convertToDTO(com.petshop.entity.Service service) {
        if (service == null) {
            return null;
        }
        return ServiceDTO.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .price(service.getPrice())
                .duration(service.getDuration())
                .merchantId(service.getMerchant() != null ? service.getMerchant().getId() : null)
                .merchantName(service.getMerchant() != null ? service.getMerchant().getName() : null)
                .category(service.getCategory())
                .image(service.getImage())
                .status(service.getStatus())
                .build();
    }
    
    public List<ServiceDTO> convertToDTOList(List<com.petshop.entity.Service> services) {
        return services.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
