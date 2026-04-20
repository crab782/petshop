package com.petshop.service;

import com.petshop.entity.Cart;
import com.petshop.entity.User;
import com.petshop.entity.Product;

import java.util.List;

public interface CartService {
    List<Cart> getCartByUser(User user);
    Cart addToCart(User user, Product product, Integer quantity);
    Cart updateCartItem(User user, Integer cartId, Integer quantity);
    void removeFromCart(User user, Integer cartId);
    void batchRemoveFromCart(User user, List<Integer> cartIds);
    void clearCart(User user);
}