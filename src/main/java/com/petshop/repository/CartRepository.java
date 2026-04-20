package com.petshop.repository;

import com.petshop.entity.Cart;
import com.petshop.entity.User;
import com.petshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByUser(User user);
    Optional<Cart> findByUserAndProduct(User user, Product product);
    void deleteByUserAndId(User user, Integer id);
    void deleteByUserAndIdIn(User user, List<Integer> ids);
}