package com.petshop.service;

import com.petshop.dto.FavoriteDTO;
import com.petshop.dto.FavoriteProductDTO;
import com.petshop.dto.FavoriteServiceDTO;
import com.petshop.entity.Favorite;
import com.petshop.entity.Merchant;
import com.petshop.entity.Product;
import com.petshop.entity.User;
import com.petshop.repository.FavoriteRepository;
import com.petshop.repository.MerchantRepository;
import com.petshop.repository.ProductRepository;
import com.petshop.repository.ServiceRepository;
import com.petshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class FavoriteService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MerchantRepository merchantRepository;
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public List<FavoriteDTO> getFavoriteMerchants(Integer userId) {
        List<Favorite> favorites = favoriteRepository.findByUserIdAndMerchantIsNotNull(userId);
        return favorites.stream()
                .map(this::convertToMerchantDTO)
                .collect(Collectors.toList());
    }
    
    public List<FavoriteServiceDTO> getFavoriteServices(Integer userId) {
        List<Favorite> favorites = favoriteRepository.findByUserIdAndServiceIsNotNull(userId);
        return favorites.stream()
                .map(this::convertToServiceDTO)
                .collect(Collectors.toList());
    }
    
    public List<FavoriteProductDTO> getFavoriteProducts(Integer userId) {
        List<Favorite> favorites = favoriteRepository.findByUserIdAndProductIsNotNull(userId);
        return favorites.stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public FavoriteDTO addMerchantFavorite(Integer userId, Integer merchantId) {
        if (favoriteRepository.existsByUserIdAndMerchantId(userId, merchantId)) {
            throw new RuntimeException("Already favorited this merchant");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));
        
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setMerchant(merchant);
        
        Favorite saved = favoriteRepository.save(favorite);
        return convertToMerchantDTO(saved);
    }
    
    @Transactional
    public FavoriteServiceDTO addServiceFavorite(Integer userId, Integer serviceId) {
        if (favoriteRepository.existsByUserIdAndServiceId(userId, serviceId)) {
            throw new RuntimeException("Already favorited this service");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        com.petshop.entity.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setService(service);
        
        Favorite saved = favoriteRepository.save(favorite);
        return convertToServiceDTO(saved);
    }
    
    @Transactional
    public FavoriteProductDTO addProductFavorite(Integer userId, Integer productId) {
        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("Already favorited this product");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        
        Favorite saved = favoriteRepository.save(favorite);
        return convertToProductDTO(saved);
    }
    
    @Transactional
    public void removeMerchantFavorite(Integer userId, Integer merchantId) {
        Favorite favorite = favoriteRepository.findByUserIdAndMerchantId(userId, merchantId)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));
        favoriteRepository.delete(favorite);
    }
    
    @Transactional
    public void removeServiceFavorite(Integer userId, Integer serviceId) {
        Favorite favorite = favoriteRepository.findByUserIdAndServiceId(userId, serviceId)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));
        favoriteRepository.delete(favorite);
    }
    
    @Transactional
    public void removeProductFavorite(Integer userId, Integer productId) {
        Favorite favorite = favoriteRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Favorite not found"));
        favoriteRepository.delete(favorite);
    }
    
    public boolean isProductFavorited(Integer userId, Integer productId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }
    
    private FavoriteDTO convertToMerchantDTO(Favorite favorite) {
        Merchant merchant = favorite.getMerchant();
        return FavoriteDTO.builder()
                .id(favorite.getId())
                .userId(favorite.getUser().getId())
                .merchantId(merchant.getId())
                .merchantName(merchant.getName())
                .merchantLogo(merchant.getLogo())
                .merchantAddress(merchant.getAddress())
                .merchantPhone(merchant.getPhone())
                .createTime(favorite.getCreatedAt())
                .build();
    }
    
    private FavoriteServiceDTO convertToServiceDTO(Favorite favorite) {
        com.petshop.entity.Service service = favorite.getService();
        Merchant merchant = service.getMerchant();
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
        Product product = favorite.getProduct();
        Merchant merchant = product.getMerchant();
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
