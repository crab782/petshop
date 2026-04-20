package com.petshop.controller.api;

import com.petshop.dto.ApiResponse;
import com.petshop.entity.Merchant;
import com.petshop.entity.Service;
import com.petshop.entity.Product;
import com.petshop.entity.Review;
import com.petshop.service.MerchantService;
import com.petshop.service.ServiceService;
import com.petshop.service.ProductService;
import com.petshop.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MerchantPublicController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/merchants")
    public ResponseEntity<ApiResponse<List<Merchant>>> getMerchants(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<Merchant> merchants = merchantService.searchMerchants(
                name, status, minRating, page, pageSize);
        return ResponseEntity.ok(ApiResponse.success(merchants));
    }

    @GetMapping("/merchants/search")
    public ResponseEntity<ApiResponse<List<Merchant>>> searchMerchants(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<Merchant> merchants = merchantService.searchMerchants(
                keyword, sortBy, sortOrder, page, pageSize);
        return ResponseEntity.ok(ApiResponse.success(merchants));
    }

    @GetMapping("/merchant/{id}")
    public ResponseEntity<ApiResponse<Merchant>> getMerchantById(@PathVariable Integer id) {
        Merchant merchant = merchantService.findById(id);
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Merchant not found"));
        }
        return ResponseEntity.ok(ApiResponse.success(merchant));
    }

    @GetMapping("/merchant/{id}/services")
    public ResponseEntity<ApiResponse<List<Service>>> getMerchantServices(
            @PathVariable Integer id,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<Service> services = serviceService.findByMerchantId(id);
        return ResponseEntity.ok(ApiResponse.success(services));
    }

    @GetMapping("/merchant/{id}/products")
    public ResponseEntity<ApiResponse<List<Product>>> getMerchantProducts(
            @PathVariable Integer id,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<Product> products = productService.findByMerchantId(id);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/merchant/{id}/reviews")
    public ResponseEntity<ApiResponse<List<Review>>> getMerchantReviews(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<Review> reviews = reviewService.findByMerchantId(id);
        return ResponseEntity.ok(ApiResponse.success(reviews));
    }

    @GetMapping("/merchant/{id}/available-slots")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> getAvailableSlots(
            @PathVariable Integer id,
            @RequestParam String date) {
        // 这里需要实现获取可用预约时段的业务逻辑
        // 暂时返回空列表，实际实现需要根据商家的营业时间和已预约情况进行计算
        Map<String, List<String>> slots = Map.of(
                "morning", List.of("09:00", "10:00", "11:00"),
                "afternoon", List.of("14:00", "15:00", "16:00", "17:00")
        );
        return ResponseEntity.ok(ApiResponse.success(slots));
    }
}