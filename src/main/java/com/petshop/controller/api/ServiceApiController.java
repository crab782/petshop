package com.petshop.controller.api;

import com.petshop.dto.ServiceDTO;
import com.petshop.entity.Service;
import com.petshop.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@Tag(name = "服务API", description = "服务相关的公共接口")
public class ServiceApiController {

    @Autowired
    private ServiceService serviceService;

    @Operation(summary = "获取服务列表", description = "获取所有可用的服务列表，支持按类型筛选")
    @GetMapping
    public ResponseEntity<List<ServiceDTO>> getServices(@RequestParam(required = false) String type) {
        List<Service> services = serviceService.findByType(type);
        List<ServiceDTO> serviceDTOs = serviceService.convertToDTOList(services);
        return ResponseEntity.ok(serviceDTOs);
    }

    @Operation(summary = "获取服务详情", description = "根据ID获取指定服务的详细信息")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO> getServiceById(@PathVariable Integer id) {
        Service service = serviceService.findById(id);
        if (service == null) {
            return ResponseEntity.notFound().build();
        }
        ServiceDTO serviceDTO = serviceService.convertToDTO(service);
        return ResponseEntity.ok(serviceDTO);
    }

    @Operation(summary = "搜索服务", description = "根据关键字搜索服务")
    @GetMapping("/search")
    public ResponseEntity<List<ServiceDTO>> searchServices(@RequestParam String keyword) {
        List<Service> services = serviceService.searchByKeyword(keyword);
        List<ServiceDTO> serviceDTOs = serviceService.convertToDTOList(services);
        return ResponseEntity.ok(serviceDTOs);
    }

    @Operation(summary = "获取推荐服务", description = "获取推荐的服务列表，按评分和评价数量排序")
    @GetMapping("/recommended")
    public ResponseEntity<List<ServiceDTO>> getRecommendedServices(
            @RequestParam(defaultValue = "4") int limit) {
        List<Service> services = serviceService.findRecommended(limit);
        List<ServiceDTO> serviceDTOs = serviceService.convertToDTOList(services);
        return ResponseEntity.ok(serviceDTOs);
    }
}
