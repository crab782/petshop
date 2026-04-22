package com.petshop.controller.api;

import com.petshop.dto.ApiResponse;
import com.petshop.dto.DashboardStatsDTO;
import com.petshop.dto.MerchantDetailDTO;
import com.petshop.dto.PageResponse;
import com.petshop.dto.RoleRequest;
import com.petshop.dto.UserDetailDTO;
import com.petshop.entity.User;
import com.petshop.entity.Merchant;
import com.petshop.entity.Announcement;
import com.petshop.entity.Appointment;
import com.petshop.entity.Product;
import com.petshop.entity.Review;
import com.petshop.entity.Role;
import com.petshop.entity.Permission;
import com.petshop.entity.OperationLog;
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
import com.petshop.service.ServiceService;
import com.petshop.service.SystemSettingsService;
import com.petshop.mapper.UserMapper;
import com.petshop.mapper.MerchantMapper;
import com.petshop.mapper.AppointmentMapper;
import com.petshop.mapper.ServiceMapper;
import com.petshop.mapper.AnnouncementMapper;
import com.petshop.mapper.ProductMapper;
import com.petshop.mapper.SystemConfigMapper;
import com.petshop.mapper.SystemSettingsMapper;
import com.petshop.mapper.PetMapper;
import com.petshop.mapper.ReviewMapper;
import com.petshop.mapper.ProductOrderMapper;
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
@Tag(name = "平台端 API", description = "平台管理员使用的 API 接口，包括用户管理、商家管理、商品管理、评价管理、系统设置等功能")
@SecurityRequirement(name = "sessionAuth")
public class AdminApiController {
    @Autowired
    private UserService userService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private AppointmentMapper appointmentMapper;
    @Autowired
    private ServiceMapper serviceMapper;
    @Autowired
    private AnnouncementMapper announcementMapper;
    @Autowired
    private AnnouncementService announcementService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ScheduledTaskService scheduledTaskService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private SystemConfigMapper systemConfigMapper;
    @Autowired
    private SystemSettingsMapper systemSettingsMapper;
    @Autowired
    private ServiceService serviceService;
    
    @Autowired
    private SystemSettingsService systemSettingsService;
    @Autowired
    private PetMapper petMapper;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private ProductOrderMapper productOrderMapper;
    @Autowired
    private OperationLogService operationLogService;

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

    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息，包括宠物数量、订单数量、评价数量等")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取用户详情"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserDetailDTO>> getUserDetail(
            @Parameter(description = "用户ID", required = true) @PathVariable Integer id,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }

        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "User not found"));
        }

        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.petshop.entity.Pet> petWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        petWrapper.eq(com.petshop.entity.Pet::getUserId, id);
        long petCount = petMapper.selectCount(petWrapper);

        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Appointment> apptWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        apptWrapper.eq(Appointment::getUserId, id);
        long appointmentCount = appointmentMapper.selectCount(apptWrapper);

        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Review> reviewWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        reviewWrapper.eq(Review::getUserId, id);
        long reviewCount = reviewMapper.selectCount(reviewWrapper);

        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.petshop.entity.ProductOrder> orderWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        orderWrapper.eq(com.petshop.entity.ProductOrder::getUserId, id);
        long orderCount = productOrderMapper.selectCount(orderWrapper);

        UserDetailDTO detail = UserDetailDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        Map<String, Object> extraInfo = new HashMap<>();
        extraInfo.put("petCount", petCount);
        extraInfo.put("appointmentCount", appointmentCount);
        extraInfo.put("reviewCount", reviewCount);
        extraInfo.put("orderCount", orderCount);

        return ResponseEntity.ok(ApiResponse.success(Map.of("user", detail, "stats", extraInfo)));
    }

    @Operation(summary = "更新用户状态", description = "更新指定用户的启用/禁用状态")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "用户状态更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PutMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<User>> updateUserStatus(
            @Parameter(description = "用户ID", required = true) @PathVariable Integer id, 
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "状态更新请求，包含目标状态",
                    content = @Content(schema = @Schema(example = "{\"status\": \"active\"}"))
            )
            @RequestBody Map<String, String> request,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        String status = request.get("status");
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Status cannot be empty"));
        }
        
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "User not found"));
        }
        
        if (!"active".equals(status) && !"disabled".equals(status)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid status. Must be 'active' or 'disabled'"));
        }
        
        user.setStatus(status);
        User updatedUser = userService.update(user);
        return ResponseEntity.ok(ApiResponse.success("User status updated", updatedUser));
    }
    
    @Operation(summary = "批量更新用户状态", description = "批量更新多个用户的启用/禁用状态")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "批量更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @PutMapping("/users/batch/status")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> batchUpdateUserStatus(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "批量更新请求，包含用户ID列表和目标状态",
                    content = @Content(schema = @Schema(example = "{\"ids\": [1, 2, 3], \"status\": \"active\"}"))
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
                    .body(ApiResponse.error(400, "User IDs cannot be empty"));
        }
        
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Status cannot be empty"));
        }
        
        if (!"active".equals(status) && !"disabled".equals(status)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid status. Must be 'active' or 'disabled'"));
        }
        
        try {
            int updatedCount = 0;
            for (Integer id : ids) {
                User user = userService.findById(id);
                if (user != null) {
                    user.setStatus(status);
                    userService.update(user);
                    updatedCount++;
                }
            }
            Map<String, Integer> result = new HashMap<>();
            result.put("updatedCount", updatedCount);
            return ResponseEntity.ok(ApiResponse.success("Successfully updated " + updatedCount + " users", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
    
    @Operation(summary = "批量删除用户", description = "批量删除多个用户")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "批量删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @DeleteMapping("/users/batch")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> batchDeleteUsers(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "批量删除请求，包含用户ID列表",
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
                    .body(ApiResponse.error(400, "User IDs cannot be empty"));
        }
        
        try {
            int deletedCount = 0;
            for (Integer id : ids) {
                if (userService.findById(id) != null) {
                    userService.delete(id);
                    deletedCount++;
                }
            }
            Map<String, Integer> result = new HashMap<>();
            result.put("deletedCount", deletedCount);
            return ResponseEntity.ok(ApiResponse.success("Successfully deleted " + deletedCount + " users", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "商家不存在")
    })
    @PutMapping("/merchants/{id}/status")
    public ResponseEntity<ApiResponse<Merchant>> updateMerchantStatus(
            @Parameter(description = "商家ID", required = true) @PathVariable Integer id, 
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "状态更新请求，包含目标状态",
                    content = @Content(schema = @Schema(example = "{\"status\": \"approved\"}"))
            )
            @RequestBody Map<String, String> request,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        String status = request.get("status");
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Status cannot be empty"));
        }
        
        Merchant merchant = merchantService.findById(id);
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Merchant not found"));
        }
        merchant.setStatus(status);
        Merchant updatedMerchant = merchantService.update(merchant);
        return ResponseEntity.ok(ApiResponse.success("Merchant status updated", updatedMerchant));
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
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Merchant> merchantPage = merchantService.getPendingMerchants(keyword, page, pageSize);
        
        PageResponse<Merchant> response = PageResponse.<Merchant>builder()
                .data(merchantPage.getRecords())
                .total(merchantPage.getTotal())
                .page(page)
                .pageSize(pageSize)
                .totalPages((int) merchantPage.getPages())
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
        
        long userCount = userMapper.selectCount(null);
        long merchantCount = merchantMapper.selectCount(null);
        long orderCount = appointmentMapper.selectCount(null);
        long serviceCount = serviceMapper.selectCount(null);
        
        DashboardStatsDTO stats = DashboardStatsDTO.builder()
                .userCount(userCount)
                .merchantCount(merchantCount)
                .orderCount(orderCount)
                .serviceCount(serviceCount)
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    @Operation(summary = "获取最近注册用户", description = "获取最近注册的用户列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取用户列表"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/users/recent")
    public ResponseEntity<ApiResponse<PageResponse<User>>> getRecentUsers(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        Pageable pageable = PageRequest.of(page, pageSize);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<User> userPage = userService.findAll(pageable);
        
        PageResponse<User> response = PageResponse.<User>builder()
                .data(userPage.getRecords())
                .total(userPage.getTotal())
                .page(page)
                .pageSize(pageSize)
                .totalPages((int) userPage.getPages())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @Operation(summary = "获取公告列表", description = "获取系统公告列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取公告列表"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/announcements")
    public ResponseEntity<ApiResponse<PageResponse<Announcement>>> getAnnouncements(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "状态筛选") @RequestParam(required = false) String status,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Announcement> announcementPage;
        
        if (status != null && !status.trim().isEmpty()) {
            announcementPage = announcementService.findByStatus(status, pageable);
        } else {
            announcementPage = announcementService.findAll(pageable);
        }
        
        PageResponse<Announcement> response = PageResponse.<Announcement>builder()
                .data(announcementPage.getContent())
                .total(announcementPage.getTotalElements())
                .page(page)
                .pageSize(pageSize)
                .totalPages(announcementPage.getTotalPages())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @Operation(summary = "创建公告", description = "创建新的系统公告")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "公告创建成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @PostMapping("/announcements")
    public ResponseEntity<ApiResponse<Announcement>> createAnnouncement(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "公告信息", required = true)
            @RequestBody Announcement announcement,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        Announcement createdAnnouncement = announcementService.create(announcement);
        return ResponseEntity.ok(ApiResponse.success("Announcement created successfully", createdAnnouncement));
    }
    
    @Operation(summary = "更新公告", description = "更新指定公告的信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "公告更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "公告不存在")
    })
    @PutMapping("/announcements/{id}")
    public ResponseEntity<ApiResponse<Announcement>> updateAnnouncement(
            @Parameter(description = "公告ID", required = true) @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "公告信息", required = true)
            @RequestBody Announcement announcement,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        try {
            Announcement updatedAnnouncement = announcementService.update(id, announcement);
            return ResponseEntity.ok(ApiResponse.success("Announcement updated successfully", updatedAnnouncement));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }
    
    @Operation(summary = "删除公告", description = "根据公告ID删除指定公告")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "公告删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "公告不存在")
    })
    @DeleteMapping("/announcements/{id}")
    public ResponseEntity<Void> deleteAnnouncement(
            @Parameter(description = "公告ID", required = true) @PathVariable Integer id,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        try {
            announcementService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @Operation(summary = "发布公告", description = "发布指定公告")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "公告发布成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "公告不存在")
    })
    @PutMapping("/announcements/{id}/publish")
    public ResponseEntity<ApiResponse<Announcement>> publishAnnouncement(
            @Parameter(description = "公告ID", required = true) @PathVariable Integer id,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        try {
            Announcement publishedAnnouncement = announcementService.publish(id);
            return ResponseEntity.ok(ApiResponse.success("Announcement published successfully", publishedAnnouncement));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }
    
    @Operation(summary = "下架公告", description = "下架指定公告")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "公告下架成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "公告不存在")
    })
    @PutMapping("/announcements/{id}/unpublish")
    public ResponseEntity<ApiResponse<Announcement>> unpublishAnnouncement(
            @Parameter(description = "公告ID", required = true) @PathVariable Integer id,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        try {
            Announcement unpublishedAnnouncement = announcementService.unpublish(id);
            return ResponseEntity.ok(ApiResponse.success("Announcement unpublished successfully", unpublishedAnnouncement));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }
    
    @Operation(summary = "批量发布公告", description = "批量发布多个公告")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "批量发布成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @PutMapping("/announcements/batch/publish")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> batchPublishAnnouncements(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "批量发布请求，包含公告ID列表",
                    content = @Content(schema = @Schema(example = "{\"ids\": [1, 2, 3]}"))
            )
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");
        
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Announcement IDs cannot be empty"));
        }
        
        try {
            int publishedCount = announcementService.batchPublish(ids);
            Map<String, Integer> result = new HashMap<>();
            result.put("publishedCount", publishedCount);
            return ResponseEntity.ok(ApiResponse.success("Successfully published " + publishedCount + " announcements", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
    
    @Operation(summary = "批量下架公告", description = "批量下架多个公告")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "批量下架成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @PutMapping("/announcements/batch/unpublish")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> batchUnpublishAnnouncements(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "批量下架请求，包含公告ID列表",
                    content = @Content(schema = @Schema(example = "{\"ids\": [1, 2, 3]}"))
            )
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");
        
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Announcement IDs cannot be empty"));
        }
        
        try {
            int unpublishedCount = announcementService.batchUnpublish(ids);
            Map<String, Integer> result = new HashMap<>();
            result.put("unpublishedCount", unpublishedCount);
            return ResponseEntity.ok(ApiResponse.success("Successfully unpublished " + unpublishedCount + " announcements", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
    
    @Operation(summary = "批量删除公告", description = "批量删除多个公告")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "批量删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @DeleteMapping("/announcements/batch")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> batchDeleteAnnouncements(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "批量删除请求，包含公告ID列表",
                    content = @Content(schema = @Schema(example = "{\"ids\": [1, 2, 3]}"))
            )
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");
        
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Announcement IDs cannot be empty"));
        }
        
        try {
            int deletedCount = announcementService.batchDelete(ids);
            Map<String, Integer> result = new HashMap<>();
            result.put("deletedCount", deletedCount);
            return ResponseEntity.ok(ApiResponse.success("Successfully deleted " + deletedCount + " announcements", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
    
    @Operation(summary = "获取系统设置", description = "获取系统设置信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取系统设置"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/system/settings")
    public ResponseEntity<ApiResponse<SystemSettings>> getSystemSettings(
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        SystemSettings settings = systemSettingsService.getSystemSettings();
        return ResponseEntity.ok(ApiResponse.success(settings));
    }
    
    @Operation(summary = "更新系统设置", description = "更新系统设置信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新系统设置"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @PutMapping("/system/settings")
    public ResponseEntity<ApiResponse<SystemSettings>> updateSystemSettings(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "系统设置信息", required = true)
            @RequestBody SystemSettings settings,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        SystemSettings updatedSettings = systemSettingsService.updateSystemSettings(settings);
        return ResponseEntity.ok(ApiResponse.success("System settings updated successfully", updatedSettings));
    }
    
    @Operation(summary = "获取邮件设置", description = "获取邮件相关设置")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取邮件设置"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/system/settings/email")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEmailSettings(
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        SystemSettings settings = systemSettingsService.getSystemSettings();
        Map<String, Object> emailSettings = new HashMap<>();
        emailSettings.put("smtp", settings.getEmailSmtp());
        emailSettings.put("port", settings.getEmailPort());
        emailSettings.put("username", settings.getEmailUsername());
        emailSettings.put("from", settings.getEmailFrom());
        
        return ResponseEntity.ok(ApiResponse.success(emailSettings));
    }
    
    @Operation(summary = "获取短信设置", description = "获取短信相关设置")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取短信设置"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/system/settings/sms")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSmsSettings(
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        SystemSettings settings = systemSettingsService.getSystemSettings();
        Map<String, Object> smsSettings = new HashMap<>();
        smsSettings.put("provider", settings.getSmsProvider());
        smsSettings.put("apiKey", settings.getSmsApiKey());
        smsSettings.put("signName", settings.getSmsSignName());
        
        return ResponseEntity.ok(ApiResponse.success(smsSettings));
    }
    
    @Operation(summary = "获取支付设置", description = "获取支付相关设置")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取支付设置"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/system/settings/payment")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPaymentSettings(
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        SystemSettings settings = systemSettingsService.getSystemSettings();
        Map<String, Object> paymentSettings = new HashMap<>();
        
        Map<String, Object> wechatSettings = new HashMap<>();
        wechatSettings.put("appId", settings.getWechatAppId());
        wechatSettings.put("mchId", settings.getWechatMchId());
        
        Map<String, Object> alipaySettings = new HashMap<>();
        alipaySettings.put("appId", settings.getAlipayAppId());
        alipaySettings.put("notifyUrl", settings.getAlipayNotifyUrl());
        
        paymentSettings.put("wechat", wechatSettings);
        paymentSettings.put("alipay", alipaySettings);
        
        return ResponseEntity.ok(ApiResponse.success(paymentSettings));
    }
    
    @Operation(summary = "获取文件上传设置", description = "获取文件上传相关设置")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取文件上传设置"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/system/settings/upload")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUploadSettings(
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        
        SystemSettings settings = systemSettingsService.getSystemSettings();
        Map<String, Object> uploadSettings = new HashMap<>();
        uploadSettings.put("path", settings.getUploadPath());
        uploadSettings.put("maxFileSize", settings.getMaxFileSize());
        uploadSettings.put("allowedFileTypes", settings.getAllowedFileTypes());
        
        return ResponseEntity.ok(ApiResponse.success(uploadSettings));
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
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Merchant> merchantPage = merchantService.findByStatus("pending", pageable);
        
        PageResponse<Merchant> response = PageResponse.<Merchant>builder()
                .data(merchantPage.getRecords())
                .total(merchantPage.getTotal())
                .page(page)
                .pageSize(pageSize)
                .totalPages((int) merchantPage.getPages())
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
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "状态更新请求，包含目标状态",
                    content = @Content(schema = @Schema(example = "{\"status\": \"enabled\"}"))
            )
            @RequestBody Map<String, String> request,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        String status = request.get("status");
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Status cannot be empty"));
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
    
    @Operation(summary = "获取服务列表", description = "分页获取服务列表，支持多条件筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取服务列表"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/services")
    public ResponseEntity<ApiResponse<PageResponse<com.petshop.entity.Service>>> getServices(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "服务状态（enabled/disabled）") @RequestParam(required = false) String status,
            @Parameter(description = "商家ID") @RequestParam(required = false) Integer merchantId,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        Pageable pageable = PageRequest.of(page, pageSize);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.petshop.entity.Service> servicePage;
        
        // 这里需要根据ServiceService的实际方法来实现
        // 暂时使用findAll方法
        servicePage = serviceService.findAll(pageable);
        
        PageResponse<com.petshop.entity.Service> response = PageResponse.<com.petshop.entity.Service>builder()
                .data(servicePage.getRecords())
                .total(servicePage.getTotal())
                .page(page)
                .pageSize(pageSize)
                .totalPages((int) servicePage.getPages())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @Operation(summary = "更新服务状态", description = "更新指定服务的启用/禁用状态")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "服务状态更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "服务不存在")
    })
    @PutMapping("/services/{id}/status")
    public ResponseEntity<ApiResponse<com.petshop.entity.Service>> updateServiceStatus(
            @Parameter(description = "服务ID", required = true) @PathVariable Integer id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "状态更新请求，包含目标状态",
                    content = @Content(schema = @Schema(example = "{\"status\": \"enabled\"}"))
            )
            @RequestBody Map<String, String> request,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        String status = request.get("status");
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Status cannot be empty"));
        }
        
        com.petshop.entity.Service service = serviceService.findById(id);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Service not found"));
        }
        
        if (!"enabled".equals(status) && !"disabled".equals(status)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Invalid status. Must be 'enabled' or 'disabled'"));
        }
        
        service.setStatus(status);
        com.petshop.entity.Service updatedService = serviceService.update(service);
        return ResponseEntity.ok(ApiResponse.success("Service status updated", updatedService));
    }
    
    @Operation(summary = "删除服务", description = "根据服务ID删除指定服务")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "服务删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "服务不存在")
    })
    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> deleteService(
            @Parameter(description = "服务ID", required = true) @PathVariable Integer id, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        com.petshop.entity.Service service = serviceService.findById(id);
        if (service == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        serviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "批量更新服务状态", description = "批量更新多个服务的启用/禁用状态")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "批量更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @PutMapping("/services/batch/status")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> batchUpdateServiceStatus(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "批量更新请求，包含服务ID列表和目标状态",
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
                    .body(ApiResponse.error(400, "Service IDs cannot be empty"));
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
            int updatedCount = 0;
            for (Integer id : ids) {
                com.petshop.entity.Service service = serviceService.findById(id);
                if (service != null) {
                    service.setStatus(status);
                    serviceService.update(service);
                    updatedCount++;
                }
            }
            Map<String, Integer> result = new HashMap<>();
            result.put("updatedCount", updatedCount);
            return ResponseEntity.ok(ApiResponse.success("Successfully updated " + updatedCount + " services", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
    
    @Operation(summary = "批量删除服务", description = "批量删除多个服务")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "批量删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @DeleteMapping("/services/batch")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> batchDeleteServices(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "批量删除请求，包含服务ID列表",
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
                    .body(ApiResponse.error(400, "Service IDs cannot be empty"));
        }
        
        try {
            int deletedCount = 0;
            for (Integer id : ids) {
                if (serviceService.findById(id) != null) {
                    serviceService.delete(id);
                    deletedCount++;
                }
            }
            Map<String, Integer> result = new HashMap<>();
            result.put("deletedCount", deletedCount);
            return ResponseEntity.ok(ApiResponse.success("Successfully deleted " + deletedCount + " services", result));
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
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Review> reviewPage;
        
        if ((rating == null) && 
            (keyword == null || keyword.trim().isEmpty()) && 
            merchantId == null &&
            (status == null || status.trim().isEmpty())) {
            reviewPage = reviewService.getAllReviews(page, pageSize);
        } else {
            reviewPage = reviewService.searchAdminReviews(rating, keyword, merchantId, status, page, pageSize);
        }
        
        PageResponse<Review> response = PageResponse.<Review>builder()
                .data(reviewPage.getRecords())
                .total(reviewPage.getTotal())
                .page(page)
                .pageSize(pageSize)
                .totalPages((int) reviewPage.getPages())
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
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Review> reviewPage = reviewService.getPendingReviews(keyword, page, pageSize);
        
        PageResponse<Review> response = PageResponse.<Review>builder()
                .data(reviewPage.getRecords())
                .total(reviewPage.getTotal())
                .page(page)
                .pageSize(pageSize)
                .totalPages((int) reviewPage.getPages())
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
    
    @Operation(summary = "批准评价", description = "批准指定评价")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "评价批准成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "评价不存在")
    })
    @PutMapping("/reviews/{id}/approve")
    public ResponseEntity<ApiResponse<Review>> approveReview(
            @Parameter(description = "评价ID", required = true) @PathVariable Integer id, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        Review review = reviewService.findById(id);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Review not found"));
        }
        
        review.setStatus("approved");
        Review updatedReview = reviewService.update(review);
        return ResponseEntity.ok(ApiResponse.success("Review approved", updatedReview));
    }
    
    @Operation(summary = "标记评价违规", description = "标记指定评价为违规")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "评价标记违规成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "评价不存在")
    })
    @PutMapping("/reviews/{id}/violation")
    public ResponseEntity<ApiResponse<Review>> markReviewViolation(
            @Parameter(description = "评价ID", required = true) @PathVariable Integer id, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        Review review = reviewService.findById(id);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Review not found"));
        }
        
        review.setStatus("rejected");
        Review updatedReview = reviewService.update(review);
        return ResponseEntity.ok(ApiResponse.success("Review marked as violation", updatedReview));
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
        
        SystemConfig config = systemConfigMapper.selectById(1);
        if (config == null) {
            config = new SystemConfig();
            config.setSiteName("宠物服务平台");
            systemConfigMapper.insert(config);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", config.getId());
        result.put("websiteName", config.getSiteName());
        result.put("websiteLogo", config.getLogo());
        result.put("contactEmail", config.getContactEmail());
        result.put("contactPhone", config.getContactPhone());
        result.put("copyright", config.getIcpNumber());
        result.put("siteDescription", config.getSiteDescription());
        result.put("siteKeywords", config.getSiteKeywords());
        result.put("footerText", config.getFooterText());
        result.put("appointmentOpenTime", "09:00");
        result.put("minAdvanceHours", 2);
        result.put("maxAdvanceDays", 30);
        result.put("enableReview", true);
        result.put("reviewCharLimit", 500);

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
        
        SystemConfig config = systemConfigMapper.selectById(1);
        if (config == null) {
            config = new SystemConfig();
        }
        
        if (request.containsKey("siteName")) {
            config.setSiteName((String) request.get("siteName"));
        }
        if (request.containsKey("websiteName")) {
            config.setSiteName((String) request.get("websiteName"));
        }
        if (request.containsKey("logo")) {
            config.setLogo((String) request.get("logo"));
        }
        if (request.containsKey("websiteLogo")) {
            config.setLogo((String) request.get("websiteLogo"));
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
        if (request.containsKey("copyright")) {
            config.setIcpNumber((String) request.get("copyright"));
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
        
        systemConfigMapper.updateById(config);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", config.getId());
        result.put("websiteName", config.getSiteName());
        result.put("websiteLogo", config.getLogo());
        result.put("contactEmail", config.getContactEmail());
        result.put("contactPhone", config.getContactPhone());
        result.put("copyright", config.getIcpNumber());
        result.put("siteDescription", config.getSiteDescription());
        result.put("siteKeywords", config.getSiteKeywords());
        result.put("footerText", config.getFooterText());
        result.put("appointmentOpenTime", "09:00");
        result.put("minAdvanceHours", 2);
        result.put("maxAdvanceDays", 30);
        result.put("enableReview", true);
        result.put("reviewCharLimit", 500);

        return ResponseEntity.ok(ApiResponse.success("System config updated successfully", result));
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
        
        org.springframework.data.domain.Page<Activity> activityPage;
        
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
            activityPage = activityService.searchActivities(keyword, type, status, startDateTime, endDateTime, page, pageSize);
        } else {
            activityPage = activityService.findAll(page, pageSize);
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
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "状态更新请求，包含目标状态",
                    content = @Content(schema = @Schema(example = "{\"status\": \"enabled\"}"))
            )
            @RequestBody Map<String, String> request,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }
        
        String status = request.get("status");
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Status cannot be empty"));
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
        
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Merchant> merchantPage = merchantService.getPendingMerchants(keyword, page, pageSize);
        
        PageResponse<Merchant> response = PageResponse.<Merchant>builder()
                .data(merchantPage.getRecords())
                .total(merchantPage.getTotal())
                .page(page)
                .pageSize(pageSize)
                .totalPages((int) merchantPage.getPages())
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

    @Operation(summary = "获取角色列表", description = "分页获取角色列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取角色列表"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<PageResponse<Role>>> getRoles(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Role> rolePage = roleService.getRoles(page, pageSize);

        PageResponse<Role> response = PageResponse.<Role>builder()
                .data(rolePage.getRecords())
                .total(rolePage.getTotal())
                .page(page - 1)
                .pageSize(pageSize)
                .totalPages((int) rolePage.getPages())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "添加角色", description = "创建新的角色")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "角色创建成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @PostMapping("/roles")
    public ResponseEntity<ApiResponse<Role>> addRole(
            @RequestBody RoleRequest request,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Role name cannot be empty"));
        }

        Role role = roleService.createRole(request.getName(), request.getDescription(), request.getPermissions());
        return ResponseEntity.ok(ApiResponse.success("Role created successfully", role));
    }

    @Operation(summary = "更新角色", description = "更新指定角色的信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "角色更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "角色不存在")
    })
    @PutMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<Role>> updateRole(
            @Parameter(description = "角色ID", required = true) @PathVariable Integer id,
            @RequestBody RoleRequest request,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }

        Role existingRole = roleService.findById(id);
        if (existingRole == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Role not found"));
        }

        Role updatedRole = roleService.updateRole(id, request.getName(), request.getDescription(), request.getPermissions());
        if (updatedRole == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Role not found"));
        }

        return ResponseEntity.ok(ApiResponse.success("Role updated successfully", updatedRole));
    }

    @Operation(summary = "删除角色", description = "根据角色ID删除指定角色")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "角色删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "角色不存在")
    })
    @DeleteMapping("/roles/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(
            @Parameter(description = "角色ID", required = true) @PathVariable Integer id,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }

        Role role = roleService.findById(id);
        if (role == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Role not found"));
        }

        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success("Role deleted successfully", null));
    }

    @Operation(summary = "获取权限列表", description = "获取所有权限列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取权限列表"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/permissions")
    public ResponseEntity<ApiResponse<List<Permission>>> getPermissions(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }

        List<Permission> permissions = roleService.getAllPermissions();
        return ResponseEntity.ok(ApiResponse.success(permissions));
    }

    @Operation(summary = "获取操作日志列表", description = "分页获取操作日志列表，支持筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取日志列表"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/operation-logs")
    public ResponseEntity<ApiResponse<PageResponse<OperationLog>>> getOperationLogs(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "操作人ID") @RequestParam(required = false) Integer adminId,
            @Parameter(description = "操作类型") @RequestParam(required = false) String action,
            @Parameter(description = "开始日期(yyyy-MM-dd)") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期(yyyy-MM-dd)") @RequestParam(required = false) String endDate,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }

        java.time.LocalDate startLocalDate = null;
        java.time.LocalDate endLocalDate = null;
        if (startDate != null && !startDate.trim().isEmpty()) {
            try {
                startLocalDate = java.time.LocalDate.parse(startDate);
            } catch (Exception e) {
            }
        }
        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                endLocalDate = java.time.LocalDate.parse(endDate);
            } catch (Exception e) {
            }
        }

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<OperationLog> logPage = operationLogService.getLogs(adminId, action, startLocalDate, endLocalDate, page, pageSize);

        PageResponse<OperationLog> response = PageResponse.<OperationLog>builder()
                .data(logPage.getRecords())
                .total(logPage.getTotal())
                .page(page - 1)
                .pageSize(pageSize)
                .totalPages((int) logPage.getPages())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "删除操作日志", description = "根据日志ID删除指定操作日志")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "日志删除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "日志不存在")
    })
    @DeleteMapping("/operation-logs/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOperationLog(
            @Parameter(description = "日志ID", required = true) @PathVariable Integer id,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }

        OperationLog log = operationLogService.findById(id);
        if (log == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Operation log not found"));
        }

        operationLogService.deleteLog(id);
        return ResponseEntity.ok(ApiResponse.success("Operation log deleted successfully", null));
    }

    @Operation(summary = "清空操作日志", description = "清空所有操作日志")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "日志清空成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @DeleteMapping("/operation-logs")
    public ResponseEntity<ApiResponse<Map<String, Object>>> clearOperationLogs(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }

        long deletedCount = operationLogService.deleteAllLogs();
        Map<String, Object> result = new HashMap<>();
        result.put("deletedCount", deletedCount);
        return ResponseEntity.ok(ApiResponse.success("All operation logs cleared", result));
    }

    @Operation(summary = "获取待审核评价（审核页面）", description = "审核页面获取待审核评价列表，路径别名")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取待审核评价列表"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权访问")
    })
    @GetMapping("/reviews/audit")
    public ResponseEntity<ApiResponse<PageResponse<Review>>> getAuditReviews(
            @Parameter(description = "页码，从0开始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "搜索关键字") @RequestParam(required = false) String keyword,
            HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "Unauthorized"));
        }

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Review> reviewPage = reviewService.getPendingReviews(keyword, page, pageSize);

        PageResponse<Review> response = PageResponse.<Review>builder()
                .data(reviewPage.getRecords())
                .total(reviewPage.getTotal())
                .page(page)
                .pageSize(pageSize)
                .totalPages((int) reviewPage.getPages())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
