package com.petshop.controller.api;

import com.petshop.dto.ApiResponse;
import com.petshop.entity.Service;
import com.petshop.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Service>>> getServices(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minDuration,
            @RequestParam(required = false) Integer maxDuration,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<Service> services = serviceService.searchServices(
                name, minPrice, maxPrice, minDuration, maxDuration, status, page, pageSize);
        return ResponseEntity.ok(ApiResponse.success(services));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Service>> getServiceById(@PathVariable Integer id) {
        Service service = serviceService.findById(id);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Service not found"));
        }
        return ResponseEntity.ok(ApiResponse.success(service));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Service>>> searchServices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer merchantId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<Service> services = serviceService.searchServices(
                keyword, merchantId, sortBy, sortOrder, page, pageSize);
        return ResponseEntity.ok(ApiResponse.success(services));
    }

    @GetMapping("/recommended")
    public ResponseEntity<ApiResponse<List<Service>>> getRecommendedServices(
            @RequestParam(required = false) Integer limit) {
        if (limit == null) {
            limit = 10;
        }
        List<Service> services = serviceService.getRecommendedServices(limit);
        return ResponseEntity.ok(ApiResponse.success(services));
    }
}