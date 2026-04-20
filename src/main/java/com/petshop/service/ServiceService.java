package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.dto.ServiceDTO;
import com.petshop.mapper.ServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceService {
    @Autowired
    private ServiceMapper serviceMapper;

    public com.petshop.entity.Service create(com.petshop.entity.Service service) {
        serviceMapper.insert(service);
        return service;
    }

    public com.petshop.entity.Service findById(Integer id) {
        return serviceMapper.selectById(id);
    }

    /**
     * 根据ID查询服务，同时加载商家信息
     *
     * @param id 服务ID
     * @return 包含商家信息的服务对象
     */
    public com.petshop.entity.Service findByIdWithMerchant(Integer id) {
        return serviceMapper.selectByIdWithMerchant(id);
    }

    public List<com.petshop.entity.Service> findByMerchantId(Integer merchantId) {
        return serviceMapper.selectList(new LambdaQueryWrapper<com.petshop.entity.Service>()
                .eq(com.petshop.entity.Service::getMerchantId, merchantId));
    }

    public List<com.petshop.entity.Service> findAll() {
        return serviceMapper.selectList(null);
    }
    
    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.petshop.entity.Service> findAll(org.springframework.data.domain.Pageable pageable) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.petshop.entity.Service> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        return serviceMapper.selectPage(page, new LambdaQueryWrapper<com.petshop.entity.Service>().orderByDesc(com.petshop.entity.Service::getCreatedAt));
    }

    public com.petshop.entity.Service update(com.petshop.entity.Service service) {
        serviceMapper.updateById(service);
        return service;
    }

    public void delete(Integer id) {
        serviceMapper.deleteById(id);
    }
    
    @Transactional
    public int batchUpdateStatus(List<Integer> ids, String status, Integer merchantId) {
        int count = 0;
        for (Integer id : ids) {
            com.petshop.entity.Service service = serviceMapper.selectById(id);
            if (service != null && service.getMerchantId().equals(merchantId)) {
                service.setStatus(status);
                serviceMapper.updateById(service);
                count++;
            }
        }
        return count;
    }
    
    public List<com.petshop.entity.Service> findByType(String type) {
        if (type == null || type.isEmpty()) {
            return serviceMapper.selectList(new LambdaQueryWrapper<com.petshop.entity.Service>()
                    .eq(com.petshop.entity.Service::getStatus, "enabled"));
        }
        return serviceMapper.selectList(new LambdaQueryWrapper<com.petshop.entity.Service>()
                .eq(com.petshop.entity.Service::getCategory, type));
    }
    
    public List<com.petshop.entity.Service> searchByKeyword(String keyword) {
        return serviceMapper.selectList(new LambdaQueryWrapper<com.petshop.entity.Service>()
                .like(com.petshop.entity.Service::getName, keyword)
                .or().like(com.petshop.entity.Service::getDescription, keyword));
    }
    
    public List<com.petshop.entity.Service> findRecommended(int limit) {
        return serviceMapper.selectList(new LambdaQueryWrapper<com.petshop.entity.Service>()
                .eq(com.petshop.entity.Service::getStatus, "enabled")
                .last("LIMIT " + limit));
    }

    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.petshop.entity.Service> searchServices(
            String name, Double minPrice, Double maxPrice, 
            Integer minDuration, Integer maxDuration, 
            String status, int page, int pageSize) {
        LambdaQueryWrapper<com.petshop.entity.Service> wrapper = new LambdaQueryWrapper<com.petshop.entity.Service>();
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(com.petshop.entity.Service::getName, name);
        }
        if (minPrice != null) {
            wrapper.ge(com.petshop.entity.Service::getPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(com.petshop.entity.Service::getPrice, maxPrice);
        }
        if (minDuration != null) {
            wrapper.ge(com.petshop.entity.Service::getDuration, minDuration);
        }
        if (maxDuration != null) {
            wrapper.le(com.petshop.entity.Service::getDuration, maxDuration);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(com.petshop.entity.Service::getStatus, status);
        }
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.petshop.entity.Service> pageResult = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize);
        return serviceMapper.selectPage(pageResult, wrapper);
    }

    public com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.petshop.entity.Service> searchServices(
            String keyword, Integer merchantId, 
            String sortBy, String sortOrder, 
            int page, int pageSize) {
        LambdaQueryWrapper<com.petshop.entity.Service> wrapper = new LambdaQueryWrapper<com.petshop.entity.Service>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(com.petshop.entity.Service::getName, keyword)
                    .or().like(com.petshop.entity.Service::getDescription, keyword));
        }
        if (merchantId != null) {
            wrapper.eq(com.petshop.entity.Service::getMerchantId, merchantId);
        }
        if ("price".equals(sortBy)) {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                wrapper.orderByAsc(com.petshop.entity.Service::getPrice);
            } else {
                wrapper.orderByDesc(com.petshop.entity.Service::getPrice);
            }
        } else if ("duration".equals(sortBy)) {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                wrapper.orderByAsc(com.petshop.entity.Service::getDuration);
            } else {
                wrapper.orderByDesc(com.petshop.entity.Service::getDuration);
            }
        } else {
            wrapper.orderByDesc(com.petshop.entity.Service::getCreatedAt);
        }
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.petshop.entity.Service> pageResult = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, pageSize);
        return serviceMapper.selectPage(pageResult, wrapper);
    }

    public List<com.petshop.entity.Service> getRecommendedServices(int limit) {
        return serviceMapper.selectList(new LambdaQueryWrapper<com.petshop.entity.Service>()
                .eq(com.petshop.entity.Service::getStatus, "enabled")
                .orderByDesc(com.petshop.entity.Service::getCreatedAt)
                .last("LIMIT " + limit));
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
                .merchantId(service.getMerchantId())
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
