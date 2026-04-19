package com.petshop.controller.api;

import com.petshop.dto.ProductDTO;
import com.petshop.dto.ReviewDTO;
import com.petshop.entity.Product;
import com.petshop.entity.Review;
import com.petshop.service.ProductService;
import com.petshop.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@Tag(name = "商品API", description = "商品相关公共接口")
public class ProductApiController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private ReviewService reviewService;
    
    @Operation(summary = "获取商品列表", description = "获取商品列表，支持关键字搜索、分类筛选和分页")
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Product> productPage = productService.searchAllProducts(keyword, "enabled", category, pageable);
        
        List<ProductDTO> products = productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "获取商品详情", description = "根据ID获取指定商品的详细信息")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Integer id) {
        Product product = productService.findById(id);
        if (product == null || !"enabled".equals(product.getStatus())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(product));
    }
    
    @Operation(summary = "搜索商品", description = "根据关键字搜索商品")
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String keyword) {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Product> productPage = productService.searchAllProducts(keyword, "enabled", null, pageable);
        
        List<ProductDTO> products = productPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(products);
    }
    
    @Operation(summary = "获取商品评价", description = "获取指定商品的评价列表")
    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getProductReviews(@PathVariable Integer id) {
        Product product = productService.findById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<Review> reviews = reviewService.findByServiceId(id);
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(this::convertReviewToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(reviewDTOs);
    }
    
    private ProductDTO convertToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .merchantId(product.getMerchant().getId())
                .image(product.getImage())
                .status(product.getStatus())
                .category(product.getCategory())
                .build();
    }
    
    private ReviewDTO convertReviewToDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getUsername())
                .merchantId(review.getMerchant().getId())
                .serviceId(review.getService().getId())
                .appointmentId(review.getAppointment().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createTime(review.getCreatedAt())
                .serviceName(review.getService().getName())
                .merchantName(review.getMerchant().getName())
                .build();
    }
}
