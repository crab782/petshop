package com.petshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.entity.Cart;
import com.petshop.entity.User;
import com.petshop.entity.Product;
import com.petshop.exception.ResourceNotFoundException;
import com.petshop.mapper.CartMapper;
import com.petshop.mapper.ProductMapper;
import com.petshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productRepository;

    @Override
    public List<Cart> getCartByUser(User user) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, user.getId());
        return cartMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public Cart addToCart(User user, Product product, Integer quantity) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, user.getId())
               .eq(Cart::getProductId, product.getId());
        Cart existingCart = cartMapper.selectOne(wrapper);
        
        if (existingCart != null) {
            existingCart.setQuantity(existingCart.getQuantity() + quantity);
            cartMapper.updateById(existingCart);
            return existingCart;
        } else {
            Cart cart = Cart.builder()
                    .userId(user.getId())
                    .productId(product.getId())
                    .quantity(quantity)
                    .build();
            cartMapper.insert(cart);
            return cart;
        }
    }

    @Override
    @Transactional
    public Cart updateCartItem(User user, Integer cartId, Integer quantity) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart item not found");
        }
        if (!cart.getUserId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to cart item");
        }
        if (quantity <= 0) {
            cartMapper.deleteById(cartId);
            return null;
        }
        cart.setQuantity(quantity);
        cartMapper.updateById(cart);
        return cart;
    }

    @Override
    @Transactional
    public void removeFromCart(User user, Integer cartId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, user.getId())
               .eq(Cart::getId, cartId);
        cartMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void batchRemoveFromCart(User user, List<Integer> cartIds) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, user.getId())
               .in(Cart::getId, cartIds);
        cartMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public void clearCart(User user) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, user.getId());
        cartMapper.delete(wrapper);
    }
}
