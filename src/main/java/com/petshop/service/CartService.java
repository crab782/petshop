package com.petshop.service;

import com.petshop.dto.CartDTO;
import com.petshop.entity.Cart;
import com.petshop.entity.Product;
import com.petshop.entity.User;
import com.petshop.exception.BadRequestException;
import com.petshop.exception.ResourceNotFoundException;
import com.petshop.repository.CartRepository;
import com.petshop.repository.ProductRepository;
import com.petshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<CartDTO> getCartByUserId(Integer userId) {
        List<Cart> cartItems = cartRepository.findByUserId(userId);
        return cartItems.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public CartDTO addToCart(Integer userId, Integer productId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        if (!"enabled".equals(product.getStatus())) {
            throw new BadRequestException("Product is not available");
        }
        
        if (product.getStock() < quantity) {
            throw new BadRequestException("Insufficient stock. Available: " + product.getStock());
        }
        
        Cart existingCart = cartRepository.findByUserIdAndProductId(userId, productId).orElse(null);
        
        if (existingCart != null) {
            int newQuantity = existingCart.getQuantity() + quantity;
            if (product.getStock() < newQuantity) {
                throw new BadRequestException("Insufficient stock. Available: " + product.getStock() + 
                        ", already in cart: " + existingCart.getQuantity());
            }
            existingCart.setQuantity(newQuantity);
            Cart updatedCart = cartRepository.save(existingCart);
            return convertToDTO(updatedCart);
        }
        
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setProduct(product);
        cart.setQuantity(quantity);
        Cart savedCart = cartRepository.save(cart);
        return convertToDTO(savedCart);
    }
    
    @Transactional
    public CartDTO updateCartItem(Integer userId, Integer productId, Integer quantity) {
        Cart cart = cartRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found for product: " + productId));
        
        Product product = cart.getProduct();
        
        if (quantity <= 0) {
            cartRepository.delete(cart);
            return null;
        }
        
        if (product.getStock() < quantity) {
            throw new BadRequestException("Insufficient stock. Available: " + product.getStock());
        }
        
        cart.setQuantity(quantity);
        Cart updatedCart = cartRepository.save(cart);
        return convertToDTO(updatedCart);
    }
    
    @Transactional
    public void removeFromCart(Integer userId, Integer productId) {
        cartRepository.deleteByUserIdAndProductId(userId, productId);
    }
    
    @Transactional
    public void batchRemoveFromCart(Integer userId, List<Integer> productIds) {
        if (productIds != null && !productIds.isEmpty()) {
            cartRepository.deleteByUserIdAndProductIds(userId, productIds);
        }
    }
    
    @Transactional
    public void clearCart(Integer userId) {
        cartRepository.deleteByUserId(userId);
    }
    
    public long getCartCount(Integer userId) {
        return cartRepository.countByUserId(userId);
    }
    
    private CartDTO convertToDTO(Cart cart) {
        Product product = cart.getProduct();
        return CartDTO.builder()
                .id(cart.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImage(product.getImage())
                .price(product.getPrice())
                .quantity(cart.getQuantity())
                .merchantId(product.getMerchant().getId())
                .merchantName(product.getMerchant().getName())
                .build();
    }
}
