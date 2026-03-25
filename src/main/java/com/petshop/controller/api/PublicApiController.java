package com.petshop.controller.api;

import com.petshop.entity.Service;
import com.petshop.entity.Merchant;
import com.petshop.service.ServiceService;
import com.petshop.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@Tag(name = "公共API", description = "无需认证的公共接口")
public class PublicApiController {
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private MerchantService merchantService;

    @Operation(summary = "获取所有服务", description = "获取所有可用的宠物服务列表")
    @GetMapping("/services")
    public ResponseEntity<List<Service>> getServices() {
        List<Service> services = serviceService.findAll();
        return ResponseEntity.ok(services);
    }

    @Operation(summary = "获取服务详情", description = "根据ID获取指定服务的详细信息")
    @GetMapping("/services/{id}")
    public ResponseEntity<Service> getService(@PathVariable Integer id) {
        Service service = serviceService.findById(id);
        if (service == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service);
    }

    @Operation(summary = "获取所有商家", description = "获取所有已审核通过的商家列表")
    @GetMapping("/merchants")
    public ResponseEntity<List<Merchant>> getMerchants() {
        List<Merchant> merchants = merchantService.findAll();
        // 只返回已审核通过的商家
        merchants = merchants.stream()
                .filter(merchant -> "approved".equals(merchant.getStatus()))
                .toList();
        return ResponseEntity.ok(merchants);
    }

    @Operation(summary = "获取商家详情", description = "根据ID获取指定商家的详细信息")
    @GetMapping("/merchants/{id}")
    public ResponseEntity<Merchant> getMerchant(@PathVariable Integer id) {
        Merchant merchant = merchantService.findById(id);
        if (merchant == null || !"approved".equals(merchant.getStatus())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(merchant);
    }

    @Operation(summary = "获取商家服务", description = "根据商家ID获取该商家提供的所有服务")
    @GetMapping("/merchants/{id}/services")
    public ResponseEntity<List<Service>> getMerchantServices(@PathVariable Integer id) {
        Merchant merchant = merchantService.findById(id);
        if (merchant == null || !"approved".equals(merchant.getStatus())) {
            return ResponseEntity.notFound().build();
        }
        List<Service> services = serviceService.findByMerchantId(id);
        return ResponseEntity.ok(services);
    }
}
