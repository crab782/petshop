package com.petshop.controller.api;

import com.petshop.dto.ApiResponse;
import com.petshop.entity.Cart;
import com.petshop.entity.User;
import com.petshop.entity.Product;
import com.petshop.service.CartService;
import com.petshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Cart>>> getCart(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized access"));
        }
        List<Cart> cartItems = cartService.getCartByUser(user);
        return ResponseEntity.ok(ApiResponse.success(cartItems));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Cart>> addToCart(@RequestBody Map<String, Object> request, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized access"));
        }
        Integer productId = (Integer) request.get("productId");
        Integer quantity = (Integer) request.get("quantity");
        if (productId == null || quantity == null || quantity <= 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid product id or quantity"));
        }
        Product product = productService.findById(productId);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Product not found"));
        }
        Cart cart = cartService.addToCart(user, product, quantity);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Added to cart successfully", cart));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Cart>> updateCart(@RequestBody Map<String, Object> request, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized access"));
        }
        Integer cartId = (Integer) request.get("cartId");
        Integer quantity = (Integer) request.get("quantity");
        if (cartId == null || quantity == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid cart id or quantity"));
        }
        Cart cart = cartService.updateCartItem(user, cartId, quantity);
        if (cart == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(ApiResponse.success("Cart updated successfully", cart));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(@PathVariable Integer id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized access"));
        }
        cartService.removeFromCart(user, id);
        return ResponseEntity.ok(ApiResponse.success("Removed from cart successfully", null));
    }

    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> batchRemoveFromCart(@RequestBody Map<String, List<Integer>> request, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized access"));
        }
        List<Integer> cartIds = request.get("ids");
        if (cartIds == null || cartIds.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid cart ids"));
        }
        cartService.batchRemoveFromCart(user, cartIds);
        return ResponseEntity.ok(ApiResponse.success("Batch removed from cart successfully", null));
    }
}