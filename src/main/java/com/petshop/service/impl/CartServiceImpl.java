package com.petshop.service.impl;

import com.petshop.entity.Cart;
import com.petshop.entity.User;
import com.petshop.entity.Product;
import com.petshop.repository.CartRepository;
import com.petshop.repository.ProductRepository;
import com.petshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Cart> getCartByUser(User user) {
        return cartRepository.findByUser(user);
    }

    @Override
    @Transactional
    public Cart addToCart(User user, Product product, Integer quantity) {
        Optional<Cart> existingCart = cartRepository.findByUserAndProduct(user, product);
        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + quantity);
            return cartRepository.save(cart);
        } else {
            Cart cart = Cart.builder()
                    .user(user)
                    .product(product)
                    .quantity(quantity)
                    .build();
            return cartRepository.save(cart);
        }
    }

    @Override
    @Transactional
    public Cart updateCartItem(User user, Integer cartId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        if (!cart.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to cart item");
        }
        if (quantity <= 0) {
            cartRepository.delete(cart);
            return null;
        }
        cart.setQuantity(quantity);
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void removeFromCart(User user, Integer cartId) {
        cartRepository.deleteByUserAndId(user, cartId);
    }

    @Override
    @Transactional
    public void batchRemoveFromCart(User user, List<Integer> cartIds) {
        cartRepository.deleteByUserAndIdIn(user, cartIds);
    }

    @Override
    @Transactional
    public void clearCart(User user) {
        List<Cart> cartItems = cartRepository.findByUser(user);
        cartRepository.deleteAll(cartItems);
    }
}