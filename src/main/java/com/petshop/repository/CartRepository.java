package com.petshop.repository;

import com.petshop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    
    List<Cart> findByUserId(Integer userId);
    
    Optional<Cart> findByUserIdAndProductId(Integer userId, Integer productId);
    
    void deleteByUserIdAndProductId(Integer userId, Integer productId);
    
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.user.id = :userId AND c.product.id IN :productIds")
    int deleteByUserIdAndProductIds(@Param("userId") Integer userId, @Param("productIds") List<Integer> productIds);
    
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.user.id = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
    
    long countByUserId(Integer userId);
}
