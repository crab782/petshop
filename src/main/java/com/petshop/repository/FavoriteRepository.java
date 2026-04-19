package com.petshop.repository;

import com.petshop.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    
    List<Favorite> findByUserIdAndMerchantIsNotNull(Integer userId);
    
    List<Favorite> findByUserIdAndServiceIsNotNull(Integer userId);
    
    List<Favorite> findByUserIdAndProductIsNotNull(Integer userId);
    
    Optional<Favorite> findByUserIdAndMerchantId(Integer userId, Integer merchantId);
    
    Optional<Favorite> findByUserIdAndServiceId(Integer userId, Integer serviceId);
    
    Optional<Favorite> findByUserIdAndProductId(Integer userId, Integer productId);
    
    boolean existsByUserIdAndMerchantId(Integer userId, Integer merchantId);
    
    boolean existsByUserIdAndServiceId(Integer userId, Integer serviceId);
    
    boolean existsByUserIdAndProductId(Integer userId, Integer productId);
}
