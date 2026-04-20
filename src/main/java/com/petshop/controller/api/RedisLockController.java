package com.petshop.controller.api;

import com.petshop.service.ProductService;
import com.petshop.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/api/redis-lock")
public class RedisLockController {

    @Resource
    private ProductService productService;

    /**
     * 测试扣减库存，使用Redis分布式锁防止超卖
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @return 操作结果
     */
    @PostMapping("/reduce-stock")
    public ApiResponse<Boolean> reduceStock(@RequestParam Long productId, @RequestParam int quantity) {
        try {
            boolean result = productService.reduceStock(productId, quantity);
            return ApiResponse.success(result, "库存扣减成功");
        } catch (Exception e) {
            return ApiResponse.error("库存扣减失败: " + e.getMessage());
        }
    }
}
