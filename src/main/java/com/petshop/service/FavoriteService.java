package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.dto.FavoriteDTO;
import com.petshop.dto.FavoriteProductDTO;
import com.petshop.dto.FavoriteServiceDTO;
import com.petshop.entity.Favorite;
import com.petshop.entity.Merchant;
import com.petshop.entity.Product;
import com.petshop.entity.User;
import com.petshop.exception.BadRequestException;
import com.petshop.exception.ResourceNotFoundException;
import com.petshop.mapper.FavoriteMapper;
import com.petshop.mapper.MerchantMapper;
import com.petshop.mapper.ProductMapper;
import com.petshop.mapper.ServiceMapper;
import com.petshop.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    
    @Autowired
    private FavoriteMapper favoriteMapper;
    
    @Autowired
    private UserMapper userRepository;
    
    @Autowired
    private MerchantMapper merchantRepository;
    
    @Autowired
    private ServiceMapper serviceRepository;
    
    @Autowired
    private ProductMapper productRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public List<FavoriteDTO> getFavoriteMerchants(Integer userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .isNotNull(Favorite::getMerchantId);
        List<Favorite> favorites = favoriteMapper.selectList(wrapper);
        return favorites.stream()
                .map(this::convertToMerchantDTO)
                .collect(Collectors.toList());
    }
    
    public List<FavoriteServiceDTO> getFavoriteServices(Integer userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .isNotNull(Favorite::getServiceId);
        List<Favorite> favorites = favoriteMapper.selectList(wrapper);
        return favorites.stream()
                .map(this::convertToServiceDTO)
                .collect(Collectors.toList());
    }
    
    public List<FavoriteProductDTO> getFavoriteProducts(Integer userId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .isNotNull(Favorite::getProductId);
        List<Favorite> favorites = favoriteMapper.selectList(wrapper);
        return favorites.stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public FavoriteDTO addMerchantFavorite(Integer userId, Integer merchantId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getMerchantId, merchantId);
        if (favoriteMapper.selectCount(wrapper) > 0) {
            throw new BadRequestException("Already favorited this merchant");
        }
        
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        
        Merchant merchant = merchantRepository.selectById(merchantId);
        if (merchant == null) {
            throw new ResourceNotFoundException("Merchant not found");
        }
        
        Favorite favorite = new Favorite();
        favorite.setUserId(user.getId());
        favorite.setMerchantId(merchant.getId());
        
        favoriteMapper.insert(favorite);
        return convertToMerchantDTO(favorite);
    }
    
    @Transactional
    public FavoriteServiceDTO addServiceFavorite(Integer userId, Integer serviceId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getServiceId, serviceId);
        if (favoriteMapper.selectCount(wrapper) > 0) {
            throw new BadRequestException("Already favorited this service");
        }
        
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        
        com.petshop.entity.Service service = serviceRepository.selectById(serviceId);
        if (service == null) {
            throw new ResourceNotFoundException("Service not found");
        }
        
        Favorite favorite = new Favorite();
        favorite.setUserId(user.getId());
        favorite.setServiceId(service.getId());
        
        favoriteMapper.insert(favorite);
        return convertToServiceDTO(favorite);
    }
    
    @Transactional
    public FavoriteProductDTO addProductFavorite(Integer userId, Integer productId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getProductId, productId);
        if (favoriteMapper.selectCount(wrapper) > 0) {
            throw new BadRequestException("Already favorited this product");
        }
        
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        
        Product product = productRepository.selectById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        
        Favorite favorite = new Favorite();
        favorite.setUserId(user.getId());
        favorite.setProductId(product.getId());
        
        favoriteMapper.insert(favorite);
        return convertToProductDTO(favorite);
    }
    
    @Transactional
    public void removeMerchantFavorite(Integer userId, Integer merchantId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getMerchantId, merchantId);
        Favorite favorite = favoriteMapper.selectOne(wrapper);
        if (favorite == null) {
            throw new ResourceNotFoundException("Favorite not found");
        }
        favoriteMapper.delete(wrapper);
    }
    
    @Transactional
    public void removeServiceFavorite(Integer userId, Integer serviceId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getServiceId, serviceId);
        Favorite favorite = favoriteMapper.selectOne(wrapper);
        if (favorite == null) {
            throw new ResourceNotFoundException("Favorite not found");
        }
        favoriteMapper.delete(wrapper);
    }
    
    @Transactional
    public void removeProductFavorite(Integer userId, Integer productId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getProductId, productId);
        Favorite favorite = favoriteMapper.selectOne(wrapper);
        if (favorite == null) {
            throw new ResourceNotFoundException("Favorite not found");
        }
        favoriteMapper.delete(wrapper);
    }
    
    public boolean isProductFavorited(Integer userId, Integer productId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getProductId, productId);
        return favoriteMapper.selectCount(wrapper) > 0;
    }
    
    private FavoriteDTO convertToMerchantDTO(Favorite favorite) {
        Merchant merchant = merchantRepository.selectById(favorite.getMerchantId());
        return FavoriteDTO.builder()
                .id(favorite.getId())
                .userId(favorite.getUserId())
                .merchantId(merchant.getId())
                .merchantName(merchant.getName())
                .merchantLogo(merchant.getLogo())
                .merchantAddress(merchant.getAddress())
                .merchantPhone(merchant.getPhone())
                .createTime(favorite.getCreatedAt())
                .build();
    }
    
    private FavoriteServiceDTO convertToServiceDTO(Favorite favorite) {
        com.petshop.entity.Service service = serviceRepository.selectById(favorite.getServiceId());
        Merchant merchant = merchantRepository.selectById(service.getMerchantId());
        return FavoriteServiceDTO.builder()
                .id(favorite.getId())
                .serviceId(service.getId())
                .serviceName(service.getName())
                .serviceImage(service.getImage())
                .servicePrice(service.getPrice())
                .serviceDuration(service.getDuration())
                .merchantId(merchant.getId())
                .merchantName(merchant.getName())
                .createdAt(favorite.getCreatedAt() != null ? favorite.getCreatedAt().format(DATE_FORMATTER) : null)
                .build();
    }
    
    private FavoriteProductDTO convertToProductDTO(Favorite favorite) {
        Product product = productRepository.selectById(favorite.getProductId());
        Merchant merchant = merchantRepository.selectById(product.getMerchantId());
        return FavoriteProductDTO.builder()
                .id(favorite.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImage(product.getImage())
                .productPrice(product.getPrice())
                .merchantId(merchant.getId())
                .merchantName(merchant.getName())
                .createdAt(favorite.getCreatedAt() != null ? favorite.getCreatedAt().format(DATE_FORMATTER) : null)
                .build();
    }
}
