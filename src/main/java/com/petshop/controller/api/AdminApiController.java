package com.petshop.controller.api;

import com.petshop.dto.ApiResponse;
import com.petshop.dto.DashboardStatsDTO;
import com.petshop.dto.MerchantDetailDTO;
import com.petshop.dto.PageResponse;
import com.petshop.dto.RoleRequest;
import com.petshop.entity.User;
import com.petshop.entity.Merchant;
import com.petshop.entity.Announcement;
import com.petshop.entity.Product;
import com.petshop.entity.Review;
import com.petshop.entity.Role;
import com.petshop.entity.Permission;
import com.petshop.entity.ScheduledTask;
import com.petshop.entity.SystemConfig;
import com.petshop.entity.SystemSettings;
import com.petshop.entity.Activity;
import com.petshop.service.UserService;
import com.petshop.service.MerchantService;
import com.petshop.service.ProductService;
import com.petshop.service.ReviewService;
import com.petshop.service.OperationLogService;
import com.petshop.service.AnnouncementService;
import com.petshop.service.RoleService;
import com.petshop.service.ScheduledTaskService;
import com.petshop.service.ActivityService;
import com.petshop.repository.UserRepository;
import com.petshop.repository.MerchantRepository;
import com.petshop.repository.AppointmentRepository;
import com.petshop.repository.ServiceRepository;
import com.petshop.repository.AnnouncementRepository;
import com.petshop.repository.ProductRepository;
import com.petshop.repository.SystemConfigRepository;
import com.petshop.repository.SystemSettingsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "平台端API", description = "平台管理员使用的API接口，包括用户管理、商家管理、商品管理、评价管理、系统设置等功能")
@SecurityRequirement(name = "sessionAuth")
public class AdminApiController {
    @Autowired
    private UserService userService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private AnnouncementRepository announcementRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ScheduledTaskService scheduledTaskService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private SystemConfigRepository systemConfigRepository;
    @Autowired
    private SystemSettingsRepository systemSettingsRepository;

    @Operation(summary = "获取用户列表", description = "获取系统中所有用户的列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取用户列表"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "删除用户", description = "根据用户ID删除指定用户")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "用户删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Integer id, 
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "获取商家列表", description = "获取系统中所有商家的列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取商家列表"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/merchants")
    public ResponseEntity<List<Merchant>> getMerchants(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Merchant> merchants = merchantService.findAll();
        return ResponseEntity.ok(merchants);
    }

    @Operation(summary = "更新商家状态", description = "更新指定商家的状态（approved/rejected/pending等）")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "商家状态更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "商家不存在")
    })
    @PutMapping("/merchants/{id}/status")
    public ResponseEntity<Merchant> updateMerchantStatus(
            @Parameter(description = "商家ID", required = true) @PathVariable Integer id, 
            @Parameter(description = "商家状态（approved/rejected/pending）", required = true) @RequestParam String status, 
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Merchant merchant = merchantService.findById(id);
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        merchant.setStatus(status);
        Merchant updatedMerchant = merchantService.update(merchant);
        return ResponseEntity.ok(updatedMerchant);
    }

    @Operation(summary = "删除商家", description = "根据商家ID删除指定商家")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "商家删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @DeleteMapping("/merchants/{id}")
    public ResponseEntity<Void> deleteMerchant(
            @Parameter(description = "商家ID", required = true) @PathVariable Integer id, 
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        merchantService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "获取商家详情", description = "根据商家ID获取商家详细信息，包括服务、商品、订单、评价等")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取商家详情"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "商家不存在")
    })
    @GetMapping("/merchants/{id}")
    public ResponseEntity<ApiResponse<MerchantDetailDTO>> getMerchantDetail(
            @Parameter(description = "商家ID", required = true) @PathVariable Integer id, 
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        MerchantDetailDTO merchantDetail = merchantService.getMerchantDetail(id);
        if (merchantDetail == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Merchant not found"));
        }
        
        return ResponseEntity.ok(ApiResponse.success(merchantDetail));
    }
    
    @Operation(summary = "批量更新商家状态", description = "批量更新多个商家的状态")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "批量更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @PutMapping("/merchants/batch/status")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> batchUpdateMerchantStatus(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "批量更新请求，包含商家ID列表和目标状态",
                    content = @Content(schema = @Schema(example = "{\"ids\": [1, 2, 3], \"status\": \"approved\"}"))
            )
            @RequestBody Map<String, Object> request, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");
        String status = (String) request.get("status");
        
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Merchant IDs cannot be empty"));
        }
        
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Status cannot be empty"));
        }
        
        try {
            int updatedCount = merchantService.batchUpdateStatus(ids, status);
            Map<String, Integer> result = new HashMap<>();
            result.put("updatedCount", updatedCount);
            return ResponseEntity.ok(ApiResponse.success("Successfully updated " + updatedCount + " merchants", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
    
    @Operation(summary = "批量删除商家", description = "批量删除多个商家")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "批量删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @DeleteMapping("/merchants/batch")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> batchDeleteMerchants(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "批量删除请求，包含商家ID列表",
                    content = @Content(schema = @Schema(example = "{\"ids\": [1, 2, 3]}"))
            )
            @RequestBody Map<String, Object> request, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");
        
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Merchant IDs cannot be empty"));
        }
        
        try {
            int deletedCount = merchantService.batchDelete(ids);
            Map<String, Integer> result = new HashMap<>();
            result.put("deletedCount", deletedCount);
            return ResponseEntity.ok(ApiResponse.success("Successfully deleted " + deletedCount + " merchants", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
    
    @Operation(summary = "获取待审核商家列表", description = "分页获取待审核状态的商家列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取待审核商家列表"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/merchants/pending")
    public ResponseEntity<ApiResponse<PageResponse<Merchant>>> getPendingMerchantsList(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "搜索关键字（商家名称或联系人）") @RequestParam(required = false) String keyword,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        Page<Merchant> merchantPage = merchantService.getPendingMerchants(keyword, page, pageSize);
        
        PageResponse<Merchant> response = PageResponse.<Merchant>builder()
                .data(merchantPage.getContent())
                .total(merchantPage.getTotalElements())
                .page(page)
                .pageSize(pageSize)
                .totalPages(merchantPage.getTotalPages())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @Operation(summary = "审核商家", description = "审核商家入驻申请，通过或拒绝")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "审核成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "商家不存在")
    })
    @PutMapping("/merchants/{id}/audit")
    public ResponseEntity<ApiResponse<Merchant>> auditMerchant(
            @Parameter(description = "商家ID", required = true) @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "审核请求，包含审核状态和原因",
                    content = @Content(schema = @Schema(example = "{\"status\": \"approved\", \"reason\": \"资质审核通过\"}"))
            )
            @RequestBody Map<String, String> request,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        String status = request.get("status");
        String reason = request.get("reason");
        
        if (status == null || (!status.equals("approved") && !status.equals("rejected"))) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid audit status. Must be 'approved' or 'rejected'"));
        }
        
        try {
            Merchant updatedMerchant = merchantService.auditMerchant(id, status, reason);
            return ResponseEntity.ok(ApiResponse.success("Merchant audit completed", updatedMerchant));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @Operation(summary = "获取仪表盘统计数据", description = "获取平台首页统计数据，包括用户数、商家数、订单数、服务数")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取统计数据"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getDashboardStats(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        long userCount = userRepository.count();
        long merchantCount = merchantRepository.count();
        long orderCount = appointmentRepository.count();
        long serviceCount = serviceRepository.count();
        
        DashboardStatsDTO stats = DashboardStatsDTO.builder()
                .userCount(userCount)
                .merchantCount(merchantCount)
                .orderCount(orderCount)
                .serviceCount(serviceCount)
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @Operation(summary = "获取最近注册用户", description = "分页获取最近注册的用户列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取用户列表"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/dashboard/recent-users")
    public ResponseEntity<ApiResponse<PageResponse<User>>> getRecentUsers(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<User> userPage = userRepository.findAllByOrderByCreatedAtDesc(pageable);
        
        PageResponse<User> response = PageResponse.<User>builder()
                .data(userPage.getContent())
                .total(userPage.getTotalElements())
                .page(page)
                .pageSize(pageSize)
                .totalPages(userPage.getTotalPages())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取待审核商家（仪表盘）", description = "仪表盘页面获取待审核商家列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取待审核商家"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/dashboard/pending-merchants")
    public ResponseEntity<ApiResponse<PageResponse<Merchant>>> getPendingMerchants(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Merchant> merchantPage = merchantRepository.findByStatus("pending", pageable);
        
        PageResponse<Merchant> response = PageResponse.<Merchant>builder()
                .data(merchantPage.getContent())
                .total(merchantPage.getTotalElements())
                .page(page)
                .pageSize(pageSize)
                .totalPages(merchantPage.getTotalPages())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取公告列表（仪表盘）", description = "仪表盘页面获取公告列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取公告列表"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/dashboard/announcements")
    public ResponseEntity<ApiResponse<PageResponse<Announcement>>> getAnnouncements(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Announcement> announcementPage = announcementRepository.findAllByOrderByCreatedAtDesc(pageable);
        
        PageResponse<Announcement> response = PageResponse.<Announcement>builder()
                .data(announcementPage.getContent())
                .total(announcementPage.getTotalElements())
                .page(page)
                .pageSize(pageSize)
                .totalPages(announcementPage.getTotalPages())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取商品列表", description = "分页获取商品列表，支持多条件筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取商品列表"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<PageResponse<Product>>> getProducts(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "商品状态（enabled/disabled）") @RequestParam(required = false) String status,
            @Parameter(description = "商家ID") @RequestParam(required = false) Integer merchantId,
            @Parameter(description = "商品分类") @RequestParam(required = false) String category,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Product> productPage;
        
        if (keyword != null || status != null || merchantId != null || category != null) {
            productPage = productService.adminSearchProducts(keyword, status, merchantId, category, pageable);
        } else {
            productPage = productService.findAllByOrderByCreatedAtDesc(pageable);
        }
        
        PageResponse<Product> response = PageResponse.<Product>builder()
                .data(productPage.getContent())
                .total(productPage.getTotalElements())
                .page(page)
                .pageSize(pageSize)
                .totalPages(productPage.getTotalPages())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取商品详情", description = "根据商品ID获取商品详细信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取商品详情"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "商品不存在")
    })
    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductDetail(
            @Parameter(description = "商品ID", required = true) @PathVariable Integer id, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        Product product = productService.findById(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Product not found"));
        }
        
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @Operation(summary = "更新商品信息", description = "更新指定商品的信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "商品更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "商品不存在")
    })
    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @Parameter(description = "商品ID", required = true) @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "商品信息", required = true)
            @RequestBody Product product,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        Product existingProduct = productService.findById(id);
        if (existingProduct == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Product not found"));
        }
        
        product.setId(id);
        product.setMerchant(existingProduct.getMerchant());
        product.setCreatedAt(existingProduct.getCreatedAt());
        
        Product updatedProduct = productService.update(product);
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", updatedProduct));
    }

    @Operation(summary = "更新商品状态", description = "更新指定商品的启用/禁用状态")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "商品状态更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "商品不存在")
    })
    @PutMapping("/products/{id}/status")
    public ResponseEntity<ApiResponse<Product>> updateProductStatus(
            @Parameter(description = "商品ID", required = true) @PathVariable Integer id,
            @Parameter(description = "商品状态（enabled/disabled）", required = true) @RequestParam String status,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        Product product = productService.findById(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Product not found"));
        }
        
        if (!"enabled".equals(status) && !"disabled".equals(status)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid status. Must be 'enabled' or 'disabled'"));
        }
        
        product.setStatus(status);
        Product updatedProduct = productService.update(product);
        return ResponseEntity.ok(ApiResponse.success("Product status updated", updatedProduct));
    }

    @Operation(summary = "删除商品", description = "根据商品ID删除指定商品")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "商品删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "商品不存在")
    })
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "商品ID", required = true) @PathVariable Integer id, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Product product = productService.findById(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "批量更新商品状态", description = "批量更新多个商品的启用/禁用状态")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "批量更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @PutMapping("/products/batch/status")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> batchUpdateProductStatus(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "批量更新请求，包含商品ID列表和目标状态",
                    content = @Content(schema = @Schema(example = "{\"ids\": [1, 2, 3], \"status\": \"enabled\"}"))
            )
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");
        String status = (String) request.get("status");
        
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Product IDs cannot be empty"));
        }
        
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Status cannot be empty"));
        }
        
        if (!"enabled".equals(status) && !"disabled".equals(status)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid status. Must be 'enabled' or 'disabled'"));
        }
        
        try {
            int updatedCount = productService.batchUpdateStatus(ids, status);
            Map<String, Integer> result = new HashMap<>();
            result.put("updatedCount", updatedCount);
            return ResponseEntity.ok(ApiResponse.success("Successfully updated " + updatedCount + " products", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "批量删除商品", description = "批量删除多个商品")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "批量删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @DeleteMapping("/products/batch")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> batchDeleteProducts(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "批量删除请求，包含商品ID列表",
                    content = @Content(schema = @Schema(example = "{\"ids\": [1, 2, 3]}"))
            )
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");
        
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Product IDs cannot be empty"));
        }
        
        try {
            productService.batchDelete(ids);
            Map<String, Integer> result = new HashMap<>();
            result.put("deletedCount", ids.size());
            return ResponseEntity.ok(ApiResponse.success("Successfully deleted " + ids.size() + " products", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "获取评价列表", description = "分页获取评价列表，支持多条件筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取评价列表"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<PageResponse<Review>>> getReviews(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "评分筛选（1-5）") @RequestParam(required = false) Integer rating,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "商家ID") @RequestParam(required = false) Integer merchantId,
            @Parameter(description = "评价状态（approved/rejected/pending）") @RequestParam(required = false) String status,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        Page<Review> reviewPage;
        
        if ((rating == null) && 
            (keyword == null || keyword.trim().isEmpty()) && 
            merchantId == null &&
            (status == null || status.trim().isEmpty())) {
            reviewPage = reviewService.getAllReviews(page, pageSize);
        } else {
            reviewPage = reviewService.searchAdminReviews(rating, keyword, merchantId, status, page, pageSize);
        }
        
        PageResponse<Review> response = PageResponse.<Review>builder()
                .data(reviewPage.getContent())
                .total(reviewPage.getTotalElements())
                .page(page)
                .pageSize(pageSize)
                .totalPages(reviewPage.getTotalPages())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "获取待审核评价列表", description = "分页获取待审核状态的评价列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取待审核评价列表"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/reviews/pending")
    public ResponseEntity<ApiResponse<PageResponse<Review>>> getPendingReviews(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        Page<Review> reviewPage = reviewService.getPendingReviews(keyword, page, pageSize);
        
        PageResponse<Review> response = PageResponse.<Review>builder()
                .data(reviewPage.getContent())
                .total(reviewPage.getTotalElements())
                .page(page)
                .pageSize(pageSize)
                .totalPages(reviewPage.getTotalPages())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "审核评价", description = "审核评价内容，通过或拒绝")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "审核成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "评价不存在")
    })
    @PutMapping("/reviews/{id}/audit")
    public ResponseEntity<ApiResponse<Review>> auditReview(
            @Parameter(description = "评价ID", required = true) @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "审核请求，包含审核状态和原因",
                    content = @Content(schema = @Schema(example = "{\"status\": \"approved\", \"reason\": \"内容合规\"}"))
            )
            @RequestBody Map<String, String> request,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        String status = request.get("status");
        String reason = request.get("reason");
        
        if (status == null || (!status.equals("approved") && !status.equals("rejected"))) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid audit status. Must be 'approved' or 'rejected'"));
        }
        
        Review updatedReview = reviewService.auditReview(id, status, reason);
        if (updatedReview == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Review not found"));
        }
        
        return ResponseEntity.ok(ApiResponse.success("Review audit completed", updatedReview));
    }

    @Operation(summary = "删除评价", description = "根据评价ID删除指定评价")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "评价删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "评价不存在")
    })
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "评价ID", required = true) @PathVariable Integer id, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Review review = reviewService.findById(id);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "批量更新评价状态", description = "批量更新多个评价的审核状态")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "批量更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @PutMapping("/reviews/batch/status")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> batchUpdateReviewStatus(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "批量更新请求，包含评价ID列表和目标状态",
                    content = @Content(schema = @Schema(example = "{\"ids\": [1, 2, 3], \"status\": \"approved\"}"))
            )
            @RequestBody Map<String, Object> request, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");
        String status = (String) request.get("status");
        
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Review IDs cannot be empty"));
        }
        
        if (status == null || (!status.equals("approved") && !status.equals("rejected"))) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid status. Must be 'approved' or 'rejected'"));
        }
        
        try {
            int updatedCount = reviewService.batchUpdateStatus(ids, status);
            Map<String, Integer> result = new HashMap<>();
            result.put("updatedCount", updatedCount);
            return ResponseEntity.ok(ApiResponse.success("Successfully updated " + updatedCount + " reviews", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "批量删除评价", description = "批量删除多个评价")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "批量删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @DeleteMapping("/reviews/batch")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> batchDeleteReviews(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "批量删除请求，包含评价ID列表",
                    content = @Content(schema = @Schema(example = "{\"ids\": [1, 2, 3]}"))
            )
            @RequestBody Map<String, Object> request, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");
        
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Review IDs cannot be empty"));
        }
        
        try {
            int deletedCount = reviewService.batchDelete(ids);
            Map<String, Integer> result = new HashMap<>();
            result.put("deletedCount", deletedCount);
            return ResponseEntity.ok(ApiResponse.success("Successfully deleted " + deletedCount + " reviews", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "获取系统配置", description = "获取网站基本配置信息，如站点名称、Logo、联系方式等")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取系统配置"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/system/config")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemConfig(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        SystemConfig config = systemConfigRepository.findById(1).orElse(null);
        if (config == null) {
            config = new SystemConfig();
            config.setSiteName("宠物服务平台");
            config = systemConfigRepository.save(config);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", config.getId());
        result.put("siteName", config.getSiteName());
        result.put("logo", config.getLogo());
        result.put("contactEmail", config.getContactEmail());
        result.put("contactPhone", config.getContactPhone());
        result.put("icpNumber", config.getIcpNumber());
        result.put("siteDescription", config.getSiteDescription());
        result.put("siteKeywords", config.getSiteKeywords());
        result.put("footerText", config.getFooterText());
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "更新系统配置", description = "更新网站基本配置信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "系统配置更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @PutMapping("/system/config")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateSystemConfig(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "系统配置信息",
                    content = @Content(schema = @Schema(example = "{\"siteName\": \"宠物家园\", \"contactEmail\": \"support@petshop.com\"}"))
            )
            @RequestBody Map<String, Object> request, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        SystemConfig config = systemConfigRepository.findById(1).orElse(null);
        if (config == null) {
            config = new SystemConfig();
        }
        
        if (request.containsKey("siteName")) {
            config.setSiteName((String) request.get("siteName"));
        }
        if (request.containsKey("logo")) {
            config.setLogo((String) request.get("logo"));
        }
        if (request.containsKey("contactEmail")) {
            config.setContactEmail((String) request.get("contactEmail"));
        }
        if (request.containsKey("contactPhone")) {
            config.setContactPhone((String) request.get("contactPhone"));
        }
        if (request.containsKey("icpNumber")) {
            config.setIcpNumber((String) request.get("icpNumber"));
        }
        if (request.containsKey("siteDescription")) {
            config.setSiteDescription((String) request.get("siteDescription"));
        }
        if (request.containsKey("siteKeywords")) {
            config.setSiteKeywords((String) request.get("siteKeywords"));
        }
        if (request.containsKey("footerText")) {
            config.setFooterText((String) request.get("footerText"));
        }
        
        SystemConfig savedConfig = systemConfigRepository.save(config);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", savedConfig.getId());
        result.put("siteName", savedConfig.getSiteName());
        result.put("logo", savedConfig.getLogo());
        result.put("contactEmail", savedConfig.getContactEmail());
        result.put("contactPhone", savedConfig.getContactPhone());
        result.put("icpNumber", savedConfig.getIcpNumber());
        result.put("siteDescription", savedConfig.getSiteDescription());
        result.put("siteKeywords", savedConfig.getSiteKeywords());
        result.put("footerText", savedConfig.getFooterText());
        
        return ResponseEntity.ok(ApiResponse.success("System config updated successfully", result));
    }

    @Operation(summary = "获取系统设置", description = "获取邮件、短信、支付、上传等系统设置")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取系统设置"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/system/settings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemSettings(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        SystemSettings settings = systemSettingsRepository.findById(1).orElse(null);
        if (settings == null) {
            settings = new SystemSettings();
            settings.setMaxFileSize(10485760L);
            settings.setAllowedFileTypes("jpg,jpeg,png,gif,pdf");
            settings = systemSettingsRepository.save(settings);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", settings.getId());
        
        Map<String, Object> emailSettings = new HashMap<>();
        emailSettings.put("smtp", settings.getEmailSmtp());
        emailSettings.put("port", settings.getEmailPort());
        emailSettings.put("username", settings.getEmailUsername());
        emailSettings.put("password", settings.getEmailPassword());
        emailSettings.put("from", settings.getEmailFrom());
        result.put("emailSettings", emailSettings);
        
        Map<String, Object> smsSettings = new HashMap<>();
        smsSettings.put("provider", settings.getSmsProvider());
        smsSettings.put("apiKey", settings.getSmsApiKey());
        smsSettings.put("apiSecret", settings.getSmsApiSecret());
        smsSettings.put("signName", settings.getSmsSignName());
        result.put("smsSettings", smsSettings);
        
        Map<String, Object> wechatPaySettings = new HashMap<>();
        wechatPaySettings.put("appId", settings.getWechatAppId());
        wechatPaySettings.put("appSecret", settings.getWechatAppSecret());
        wechatPaySettings.put("mchId", settings.getWechatMchId());
        wechatPaySettings.put("payKey", settings.getWechatPayKey());
        wechatPaySettings.put("payCert", settings.getWechatPayCert());
        result.put("wechatPaySettings", wechatPaySettings);
        
        Map<String, Object> alipaySettings = new HashMap<>();
        alipaySettings.put("appId", settings.getAlipayAppId());
        alipaySettings.put("privateKey", settings.getAlipayPrivateKey());
        alipaySettings.put("publicKey", settings.getAlipayPublicKey());
        alipaySettings.put("notifyUrl", settings.getAlipayNotifyUrl());
        result.put("alipaySettings", alipaySettings);
        
        Map<String, Object> uploadSettings = new HashMap<>();
        uploadSettings.put("path", settings.getUploadPath());
        uploadSettings.put("maxFileSize", settings.getMaxFileSize());
        uploadSettings.put("allowedFileTypes", settings.getAllowedFileTypes());
        result.put("uploadSettings", uploadSettings);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @Operation(summary = "更新系统设置", description = "更新邮件、短信、支付、上传等系统设置")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "系统设置更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @PutMapping("/system/settings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateSystemSettings(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "系统设置信息", required = true)
            @RequestBody Map<String, Object> request, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        SystemSettings settings = systemSettingsRepository.findById(1).orElse(null);
        if (settings == null) {
            settings = new SystemSettings();
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> emailSettings = (Map<String, Object>) request.get("emailSettings");
        if (emailSettings != null) {
            if (emailSettings.containsKey("smtp")) {
                settings.setEmailSmtp((String) emailSettings.get("smtp"));
            }
            if (emailSettings.containsKey("port")) {
                settings.setEmailPort(((Number) emailSettings.get("port")).intValue());
            }
            if (emailSettings.containsKey("username")) {
                settings.setEmailUsername((String) emailSettings.get("username"));
            }
            if (emailSettings.containsKey("password")) {
                settings.setEmailPassword((String) emailSettings.get("password"));
            }
            if (emailSettings.containsKey("from")) {
                settings.setEmailFrom((String) emailSettings.get("from"));
            }
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> smsSettings = (Map<String, Object>) request.get("smsSettings");
        if (smsSettings != null) {
            if (smsSettings.containsKey("provider")) {
                settings.setSmsProvider((String) smsSettings.get("provider"));
            }
            if (smsSettings.containsKey("apiKey")) {
                settings.setSmsApiKey((String) smsSettings.get("apiKey"));
            }
            if (smsSettings.containsKey("apiSecret")) {
                settings.setSmsApiSecret((String) smsSettings.get("apiSecret"));
            }
            if (smsSettings.containsKey("signName")) {
                settings.setSmsSignName((String) smsSettings.get("signName"));
            }
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> wechatPaySettings = (Map<String, Object>) request.get("wechatPaySettings");
        if (wechatPaySettings != null) {
            if (wechatPaySettings.containsKey("appId")) {
                settings.setWechatAppId((String) wechatPaySettings.get("appId"));
            }
            if (wechatPaySettings.containsKey("appSecret")) {
                settings.setWechatAppSecret((String) wechatPaySettings.get("appSecret"));
            }
            if (wechatPaySettings.containsKey("mchId")) {
                settings.setWechatMchId((String) wechatPaySettings.get("mchId"));
            }
            if (wechatPaySettings.containsKey("payKey")) {
                settings.setWechatPayKey((String) wechatPaySettings.get("payKey"));
            }
            if (wechatPaySettings.containsKey("payCert")) {
                settings.setWechatPayCert((String) wechatPaySettings.get("payCert"));
            }
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> alipaySettings = (Map<String, Object>) request.get("alipaySettings");
        if (alipaySettings != null) {
            if (alipaySettings.containsKey("appId")) {
                settings.setAlipayAppId((String) alipaySettings.get("appId"));
            }
            if (alipaySettings.containsKey("privateKey")) {
                settings.setAlipayPrivateKey((String) alipaySettings.get("privateKey"));
            }
            if (alipaySettings.containsKey("publicKey")) {
                settings.setAlipayPublicKey((String) alipaySettings.get("publicKey"));
            }
            if (alipaySettings.containsKey("notifyUrl")) {
                settings.setAlipayNotifyUrl((String) alipaySettings.get("notifyUrl"));
            }
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> uploadSettings = (Map<String, Object>) request.get("uploadSettings");
        if (uploadSettings != null) {
            if (uploadSettings.containsKey("path")) {
                settings.setUploadPath((String) uploadSettings.get("path"));
            }
            if (uploadSettings.containsKey("maxFileSize")) {
                settings.setMaxFileSize(((Number) uploadSettings.get("maxFileSize")).longValue());
            }
            if (uploadSettings.containsKey("allowedFileTypes")) {
                settings.setAllowedFileTypes((String) uploadSettings.get("allowedFileTypes"));
            }
        }
        
        SystemSettings savedSettings = systemSettingsRepository.save(settings);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", savedSettings.getId());
        
        Map<String, Object> resultEmailSettings = new HashMap<>();
        resultEmailSettings.put("smtp", savedSettings.getEmailSmtp());
        resultEmailSettings.put("port", savedSettings.getEmailPort());
        resultEmailSettings.put("username", savedSettings.getEmailUsername());
        resultEmailSettings.put("password", savedSettings.getEmailPassword());
        resultEmailSettings.put("from", savedSettings.getEmailFrom());
        result.put("emailSettings", resultEmailSettings);
        
        Map<String, Object> resultSmsSettings = new HashMap<>();
        resultSmsSettings.put("provider", savedSettings.getSmsProvider());
        resultSmsSettings.put("apiKey", savedSettings.getSmsApiKey());
        resultSmsSettings.put("apiSecret", savedSettings.getSmsApiSecret());
        resultSmsSettings.put("signName", savedSettings.getSmsSignName());
        result.put("smsSettings", resultSmsSettings);
        
        Map<String, Object> resultWechatPaySettings = new HashMap<>();
        resultWechatPaySettings.put("appId", savedSettings.getWechatAppId());
        resultWechatPaySettings.put("appSecret", savedSettings.getWechatAppSecret());
        resultWechatPaySettings.put("mchId", savedSettings.getWechatMchId());
        resultWechatPaySettings.put("payKey", savedSettings.getWechatPayKey());
        resultWechatPaySettings.put("payCert", savedSettings.getWechatPayCert());
        result.put("wechatPaySettings", resultWechatPaySettings);
        
        Map<String, Object> resultAlipaySettings = new HashMap<>();
        resultAlipaySettings.put("appId", savedSettings.getAlipayAppId());
        resultAlipaySettings.put("privateKey", savedSettings.getAlipayPrivateKey());
        resultAlipaySettings.put("publicKey", savedSettings.getAlipayPublicKey());
        resultAlipaySettings.put("notifyUrl", savedSettings.getAlipayNotifyUrl());
        result.put("alipaySettings", resultAlipaySettings);
        
        Map<String, Object> resultUploadSettings = new HashMap<>();
        resultUploadSettings.put("path", savedSettings.getUploadPath());
        resultUploadSettings.put("maxFileSize", savedSettings.getMaxFileSize());
        resultUploadSettings.put("allowedFileTypes", savedSettings.getAllowedFileTypes());
        result.put("uploadSettings", resultUploadSettings);
        
        return ResponseEntity.ok(ApiResponse.success("System settings updated successfully", result));
    }

    @Operation(summary = "获取活动列表", description = "分页获取活动列表，支持多条件筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取活动列表"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/activities")
    public ResponseEntity<ApiResponse<PageResponse<Activity>>> getActivities(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "活动类型") @RequestParam(required = false) String type,
            @Parameter(description = "活动状态（enabled/disabled）") @RequestParam(required = false) String status,
            @Parameter(description = "开始日期（格式：yyyy-MM-dd）") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期（格式：yyyy-MM-dd）") @RequestParam(required = false) String endDate,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Activity> activityPage;
        
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        
        if (startDate != null && !startDate.trim().isEmpty()) {
            try {
                startDateTime = LocalDateTime.parse(startDate + "T00:00:00");
            } catch (Exception e) {
            }
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                endDateTime = LocalDateTime.parse(endDate + "T23:59:59");
            } catch (Exception e) {
            }
        }
        
        if (keyword != null || type != null || status != null || startDateTime != null || endDateTime != null) {
            activityPage = activityService.searchActivities(keyword, type, status, startDateTime, endDateTime, pageable);
        } else {
            activityPage = activityService.findAll(pageable);
        }
        
        PageResponse<Activity> response = PageResponse.<Activity>builder()
                .data(activityPage.getContent())
                .total(activityPage.getTotalElements())
                .page(page)
                .pageSize(pageSize)
                .totalPages(activityPage.getTotalPages())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "创建活动", description = "创建新的活动")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "活动创建成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @PostMapping("/activities")
    public ResponseEntity<ApiResponse<Activity>> createActivity(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "活动信息", required = true)
            @RequestBody Activity activity,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        if (activity.getName() == null || activity.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Activity name cannot be empty"));
        }
        
        Activity createdActivity = activityService.create(activity);
        return ResponseEntity.ok(ApiResponse.success("Activity created successfully", createdActivity));
    }

    @Operation(summary = "更新活动", description = "更新指定活动的信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "活动更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "活动不存在")
    })
    @PutMapping("/activities/{id}")
    public ResponseEntity<ApiResponse<Activity>> updateActivity(
            @Parameter(description = "活动ID", required = true) @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "活动信息", required = true)
            @RequestBody Activity activity,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        Activity existingActivity = activityService.findById(id);
        if (existingActivity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Activity not found"));
        }
        
        try {
            Activity updatedActivity = activityService.update(id, activity);
            return ResponseEntity.ok(ApiResponse.success("Activity updated successfully", updatedActivity));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "删除活动", description = "根据活动ID删除指定活动")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "活动删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "活动不存在")
    })
    @DeleteMapping("/activities/{id}")
    public ResponseEntity<Void> deleteActivity(
            @Parameter(description = "活动ID", required = true) @PathVariable Integer id, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Activity activity = activityService.findById(id);
        if (activity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        activityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "更新活动状态", description = "更新指定活动的启用/禁用状态")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "活动状态更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "活动不存在")
    })
    @PutMapping("/activities/{id}/status")
    public ResponseEntity<ApiResponse<Activity>> updateActivityStatus(
            @Parameter(description = "活动ID", required = true) @PathVariable Integer id,
            @Parameter(description = "活动状态（enabled/disabled）", required = true) @RequestParam String status,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        Activity activity = activityService.findById(id);
        if (activity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Activity not found"));
        }
        
        if (!"enabled".equals(status) && !"disabled".equals(status)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid status. Must be 'enabled' or 'disabled'"));
        }
        
        try {
            Activity updatedActivity = activityService.updateStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Activity status updated", updatedActivity));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "获取待审核店铺列表", description = "分页获取待审核状态的店铺列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取待审核店铺列表"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/shops/pending")
    public ResponseEntity<ApiResponse<PageResponse<Merchant>>> getPendingShops(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        Page<Merchant> merchantPage = merchantService.getPendingMerchants(keyword, page, pageSize);
        
        PageResponse<Merchant> response = PageResponse.<Merchant>builder()
                .data(merchantPage.getContent())
                .total(merchantPage.getTotalElements())
                .page(page)
                .pageSize(pageSize)
                .totalPages(merchantPage.getTotalPages())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "审核店铺", description = "审核店铺申请，通过或拒绝")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "审核成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "店铺不存在")
    })
    @PutMapping("/shops/{id}/audit")
    public ResponseEntity<ApiResponse<Merchant>> auditShop(
            @Parameter(description = "店铺ID", required = true) @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "审核请求，包含审核状态和原因",
                    content = @Content(schema = @Schema(example = "{\"status\": \"approved\", \"reason\": \"资质审核通过\"}"))
            )
            @RequestBody Map<String, String> request,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        String status = request.get("status");
        String reason = request.get("reason");
        
        if (status == null || (!status.equals("approved") && !status.equals("rejected"))) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid audit status. Must be 'approved' or 'rejected'"));
        }
        
        try {
            Merchant updatedMerchant = merchantService.auditMerchant(id, status, reason);
            return ResponseEntity.ok(ApiResponse.success("Shop audit completed", updatedMerchant));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @Operation(summary = "获取任务列表", description = "分页获取定时任务列表，支持多条件筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取任务列表"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<PageResponse<ScheduledTask>>> getTasks(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "任务类型") @RequestParam(required = false) String type,
            @Parameter(description = "任务状态") @RequestParam(required = false) String status,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        Page<ScheduledTask> taskPage = scheduledTaskService.getTasks(keyword, type, status, page, pageSize);
        
        PageResponse<ScheduledTask> response = PageResponse.<ScheduledTask>builder()
                .data(taskPage.getContent())
                .total(taskPage.getTotalElements())
                .page(page)
                .pageSize(pageSize)
                .totalPages(taskPage.getTotalPages())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "创建定时任务", description = "创建新的定时任务")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "任务创建成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @PostMapping("/tasks")
    public ResponseEntity<ApiResponse<ScheduledTask>> createTask(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "任务信息", required = true)
            @RequestBody ScheduledTask task,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        if (task.getName() == null || task.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Task name cannot be empty"));
        }
        
        if (task.getType() == null || task.getType().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Task type cannot be empty"));
        }
        
        ScheduledTask createdTask = scheduledTaskService.create(task);
        return ResponseEntity.ok(ApiResponse.success("Task created successfully", createdTask));
    }

    @Operation(summary = "更新定时任务", description = "更新指定定时任务的信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "任务更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "任务不存在")
    })
    @PutMapping("/tasks/{id}")
    public ResponseEntity<ApiResponse<ScheduledTask>> updateTask(
            @Parameter(description = "任务ID", required = true) @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "任务信息", required = true)
            @RequestBody ScheduledTask task,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        ScheduledTask existingTask = scheduledTaskService.findById(id);
        if (existingTask == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Task not found"));
        }
        
        try {
            ScheduledTask updatedTask = scheduledTaskService.update(id, task);
            return ResponseEntity.ok(ApiResponse.success("Task updated successfully", updatedTask));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @Operation(summary = "删除定时任务", description = "根据任务ID删除指定定时任务")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "任务删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "任务不存在")
    })
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "任务ID", required = true) @PathVariable Integer id, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        ScheduledTask task = scheduledTaskService.findById(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        scheduledTaskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "执行定时任务", description = "手动执行指定的定时任务")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "任务执行成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "任务执行失败"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "任务不存在")
    })
    @PostMapping("/tasks/{id}/execute")
    public ResponseEntity<ApiResponse<Map<String, Object>>> executeTask(
            @Parameter(description = "任务ID", required = true) @PathVariable Integer id,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        ScheduledTask task = scheduledTaskService.findById(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Task not found"));
        }
        
        try {
            Map<String, Object> result = scheduledTaskService.executeTask(id);
            return ResponseEntity.ok(ApiResponse.success("Task executed successfully", result));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }
}
