package com.petshop.controller.api;

import com.petshop.dto.ApiResponse;
import com.petshop.entity.Service;
import com.petshop.entity.Merchant;
import com.petshop.service.ServiceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/services")
public class MerchantServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Service>>> getMerchantServices(HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        List<Service> services = serviceService.findByMerchantId(merchant.getId());
        return ResponseEntity.ok(ApiResponse.success(services));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Service>> addService(@RequestBody Service service, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        service.setMerchant(merchant);
        service.setStatus("enabled");
        Service createdService = serviceService.create(service);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdService));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Service>> updateService(@PathVariable Integer id, @RequestBody Service service, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        Service existingService = serviceService.findById(id);
        if (existingService == null || existingService.getMerchant().getId() != merchant.getId()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Service not found"));
        }
        service.setId(id);
        service.setMerchant(merchant);
        Service updatedService = serviceService.update(service);
        return ResponseEntity.ok(ApiResponse.success(updatedService));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Integer id, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        Service existingService = serviceService.findById(id);
        if (existingService == null || existingService.getMerchant().getId() != merchant.getId()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Service not found"));
        }
        serviceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Service>> getServiceById(@PathVariable Integer id, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        Service service = serviceService.findById(id);
        if (service == null || service.getMerchant().getId() != merchant.getId()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Service not found"));
        }
        return ResponseEntity.ok(ApiResponse.success(service));
    }

    @PutMapping("/batch/status")
    public ResponseEntity<ApiResponse<List<Service>>> batchUpdateServiceStatus(@RequestBody Map<String, Object> request, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        List<Integer> ids = (List<Integer>) request.get("ids");
        String status = (String) request.get("status");
        serviceService.batchUpdateStatus(ids, status, merchant.getId());
        List<Service> services = serviceService.findByMerchantId(merchant.getId());
        return ResponseEntity.ok(ApiResponse.success(services));
    }

    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> batchDeleteServices(@RequestBody Map<String, List<Integer>> request, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        List<Integer> ids = request.get("ids");
        for (Integer id : ids) {
            Service service = serviceService.findById(id);
            if (service != null && service.getMerchant().getId() == merchant.getId()) {
                serviceService.delete(id);
            }
        }
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}