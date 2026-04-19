package com.petshop.repository;

import com.petshop.entity.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MerchantRepository extends JpaRepository<Merchant, Integer> {
    Optional<Merchant> findByEmail(String email);
    
    @Query("SELECT m FROM Merchant m WHERE m.name LIKE %:keyword% OR m.address LIKE %:keyword%")
    Page<Merchant> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    Page<Merchant> findByStatus(String status, Pageable pageable);
    
    @Query("SELECT m FROM Merchant m WHERE m.status = 'pending' AND (m.name LIKE %:keyword% OR m.contactPerson LIKE %:keyword% OR m.email LIKE %:keyword%)")
    Page<Merchant> findPendingByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
