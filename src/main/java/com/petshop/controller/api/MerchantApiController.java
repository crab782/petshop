package com.petshop.controller.api;

import com.petshop.dto.ApiResponse;
import com.petshop.entity.Merchant;
import com.petshop.entity.MerchantSettings;
import com.petshop.entity.Service;
import com.petshop.entity.Product;
import com.petshop.entity.Category;
import com.petshop.entity.Appointment;
import com.petshop.entity.ProductOrder;
import com.petshop.entity.Review;
import com.petshop.service.MerchantService;
import com.petshop.service.ServiceService;
import com.petshop.service.ProductService;
import com.petshop.service.CategoryService;
import com.petshop.service.AppointmentService;
import com.petshop.service.ProductOrderService;
import com.petshop.service.ReviewService;
import com.petshop.service.MerchantStatsService;
import com.petshop.service.MerchantSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/merchant")
public class MerchantApiController {
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private ProductOrderService productOrderService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private MerchantStatsService merchantStatsService;
    @Autowired
    private MerchantSettingsService merchantSettingsService;

    /**
     * 获取商家资料
     * GET /api/merchant/profile
     * 
     * 获取当前登录商家的详细信息，包括商家名称、联系人、电话、邮箱、地址、Logo等
     * 
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含商家信息
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<Merchant>> getProfile(HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            Merchant freshMerchant = merchantService.findById(merchant.getId());
            return ResponseEntity.ok(ApiResponse.success(freshMerchant));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取商家资料失败：" + e.getMessage()));
        }
    }

    /**
     * 更新商家资料
     * PUT /api/merchant/profile
     * 
     * 更新当前登录商家的资料信息，包括商家名称、联系人、电话、邮箱、地址、Logo等
     * 注意：密码和状态字段不可通过此接口修改
     * 
     * @param merchant 更新的商家信息
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含更新后的商家信息
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Merchant>> updateProfile(@RequestBody Merchant merchant, HttpSession session) {
        Merchant currentMerchant = (Merchant) session.getAttribute("merchant");
        if (currentMerchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            merchant.setId(currentMerchant.getId());
            merchant.setPassword(currentMerchant.getPassword());
            merchant.setStatus(currentMerchant.getStatus());
            Merchant updatedMerchant = merchantService.update(merchant);
            session.setAttribute("merchant", updatedMerchant);
            return ResponseEntity.ok(ApiResponse.success("商家资料更新成功", updatedMerchant));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "更新商家资料失败：" + e.getMessage()));
        }
    }

    // ==================== 服务管理 API ====================

    /**
     * 获取服务列表
     * GET /api/merchant/services
     * 商家可以查看自己店铺的所有服务
     * 
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含服务列表
     */
    @GetMapping("/services")
    public ResponseEntity<ApiResponse<List<Service>>> getServices(HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            List<Service> services = serviceService.findByMerchantId(merchant.getId());
            return ResponseEntity.ok(ApiResponse.success(services));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取服务列表失败：" + e.getMessage()));
        }
    }

    /**
     * 添加服务
     * POST /api/merchant/services
     * 商家可以添加新的服务项目
     * 
     * @param service 服务信息
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含创建的服务
     */
    @PostMapping("/services")
    public ResponseEntity<ApiResponse<Service>> addService(@RequestBody Service service, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            if (service.getName() == null || service.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "服务名称不能为空"));
            }
            if (service.getPrice() == null || service.getPrice().doubleValue() < 0) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "服务价格无效"));
            }
            if (service.getDuration() == null || service.getDuration() <= 0) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "服务时长无效"));
            }
            service.setMerchant(merchant);
            Service createdService = serviceService.create(service);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("服务添加成功", createdService));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "添加服务失败：" + e.getMessage()));
        }
    }

    /**
     * 更新服务
     * PUT /api/merchant/services/{id}
     * 商家可以更新自己店铺的服务信息
     * 
     * @param id 服务ID
     * @param service 更新的服务信息
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含更新后的服务
     */
    @PutMapping("/services/{id}")
    public ResponseEntity<ApiResponse<Service>> updateService(
            @PathVariable Integer id,
            @RequestBody Service service,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            Service existingService = serviceService.findById(id);
            if (existingService == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "服务不存在"));
            }
            if (!existingService.getMerchant().getId().equals(merchant.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "无权操作此服务"));
            }
            if (service.getName() == null || service.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "服务名称不能为空"));
            }
            if (service.getPrice() == null || service.getPrice().doubleValue() < 0) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "服务价格无效"));
            }
            if (service.getDuration() == null || service.getDuration() <= 0) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "服务时长无效"));
            }
            service.setId(id);
            service.setMerchant(merchant);
            service.setCreatedAt(existingService.getCreatedAt());
            Service updatedService = serviceService.update(service);
            return ResponseEntity.ok(ApiResponse.success("服务更新成功", updatedService));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "更新服务失败：" + e.getMessage()));
        }
    }

    /**
     * 删除服务
     * DELETE /api/merchant/services/{id}
     * 商家可以删除自己店铺的服务
     * 
     * @param id 服务ID
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式
     */
    @DeleteMapping("/services/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Integer id, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            Service service = serviceService.findById(id);
            if (service == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "服务不存在"));
            }
            if (!service.getMerchant().getId().equals(merchant.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "无权操作此服务"));
            }
            serviceService.delete(id);
            return ResponseEntity.ok(ApiResponse.success("服务删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "删除服务失败：" + e.getMessage()));
        }
    }

    @PutMapping("/services/batch/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchUpdateServicesStatus(
            @RequestBody Map<String, Object> request, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");
        String status = (String) request.get("status");
        
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "服务ID列表不能为空"));
        }
        
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "状态值不能为空"));
        }
        
        if (!status.equals("enabled") && !status.equals("disabled")) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "状态值无效，只能是 enabled 或 disabled"));
        }
        
        int updatedCount = serviceService.batchUpdateStatus(ids, status, merchant.getId());
        
        Map<String, Object> result = new HashMap<>();
        result.put("updatedCount", updatedCount);
        result.put("requestedCount", ids.size());
        
        if (updatedCount == 0) {
            return ResponseEntity.ok(ApiResponse.success("未找到可更新的服务", result));
        }
        
        return ResponseEntity.ok(ApiResponse.success("批量更新服务状态成功", result));
    }

    /**
     * 获取预约列表
     * 商家可以查看自己店铺的所有服务预约
     * 
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含预约列表
     */
    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<Appointment>>> getAppointments(HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            List<Appointment> appointments = appointmentService.findByMerchantId(merchant.getId());
            return ResponseEntity.ok(ApiResponse.success(appointments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取预约列表失败：" + e.getMessage()));
        }
    }

    /**
     * 更新预约状态
     * 商家可以更新自己店铺预约的状态（如：确认、完成、取消等）
     * 状态流转：pending -> confirmed -> completed
     *          pending -> cancelled
     *          confirmed -> cancelled
     * 
     * @param id 预约ID
     * @param status 新状态（pending/confirmed/completed/cancelled）
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含更新后的预约信息
     */
    @PutMapping("/appointments/{id}/status")
    public ResponseEntity<ApiResponse<Appointment>> updateAppointmentStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            Appointment appointment = appointmentService.findById(id);
            if (appointment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "预约不存在"));
            }
            if (!appointment.getMerchant().getId().equals(merchant.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "无权操作此预约"));
            }
            
            List<String> validStatuses = Arrays.asList("pending", "confirmed", "completed", "cancelled");
            if (!validStatuses.contains(status)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "无效的预约状态，只能是 pending、confirmed、completed 或 cancelled"));
            }
            
            String currentStatus = appointment.getStatus();
            if (!isValidStatusTransition(currentStatus, status)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "状态流转无效：不能从 " + currentStatus + " 变更为 " + status));
            }
            
            appointment.setStatus(status);
            Appointment updatedAppointment = appointmentService.update(appointment);
            return ResponseEntity.ok(ApiResponse.success("预约状态更新成功", updatedAppointment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "更新预约状态失败：" + e.getMessage()));
        }
    }
    
    /**
     * 验证预约状态流转是否合法
     * 合法流转：
     * - pending -> confirmed（待处理 -> 已确认）
     * - pending -> cancelled（待处理 -> 已取消）
     * - confirmed -> completed（已确认 -> 已完成）
     * - confirmed -> cancelled（已确认 -> 已取消）
     * - completed/cancelled 不能再变更状态
     * 
     * @param currentStatus 当前状态
     * @param newStatus 新状态
     * @return 是否为合法的状态流转
     */
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        if (currentStatus.equals(newStatus)) {
            return true;
        }
        
        switch (currentStatus) {
            case "pending":
                return "confirmed".equals(newStatus) || "cancelled".equals(newStatus);
            case "confirmed":
                return "completed".equals(newStatus) || "cancelled".equals(newStatus);
            case "completed":
            case "cancelled":
                return false;
            default:
                return false;
        }
    }

    /**
     * 获取商品订单列表
     * 商家可以查看自己店铺的所有商品订单
     * 
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含订单列表
     */
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<ProductOrder>>> getOrders(HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            List<ProductOrder> orders = productOrderService.findByMerchantId(merchant.getId());
            return ResponseEntity.ok(ApiResponse.success(orders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取订单列表失败：" + e.getMessage()));
        }
    }

    /**
     * 更新商品订单状态
     * 商家可以更新自己店铺订单的状态（如：发货、完成等）
     * 状态流转：pending -> paid -> shipped -> completed
     * 
     * @param id 订单ID
     * @param status 新状态（pending/paid/shipped/completed/cancelled）
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含更新后的订单信息
     */
    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<ProductOrder>> updateOrderStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            ProductOrder order = productOrderService.findById(id);
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "订单不存在"));
            }
            if (!order.getMerchant().getId().equals(merchant.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "无权操作此订单"));
            }
            List<String> validStatuses = Arrays.asList("pending", "paid", "shipped", "completed", "cancelled");
            if (!validStatuses.contains(status)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "无效的订单状态"));
            }
            order.setStatus(status);
            ProductOrder updatedOrder = productOrderService.update(order);
            return ResponseEntity.ok(ApiResponse.success("订单状态更新成功", updatedOrder));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "更新订单状态失败：" + e.getMessage()));
        }
    }

    // ==================== 商品管理 API ====================

    /**
     * 获取商品列表
     * GET /api/merchant/products
     */
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<Product>>> getProducts(HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        List<Product> products = productService.findByMerchantId(merchant.getId());
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    /**
     * 添加商品
     * POST /api/merchant/products
     */
    @PostMapping("/products")
    public ResponseEntity<ApiResponse<Product>> addProduct(@RequestBody Product product, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        product.setMerchant(merchant);
        Product createdProduct = productService.create(product);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("商品添加成功", createdProduct));
    }

    /**
     * 获取商品详情
     * GET /api/merchant/products/{id}
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Integer id, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        Product product = productService.findById(id);
        if (product == null || !product.getMerchant().getId().equals(merchant.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "商品不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    /**
     * 更新商品
     * PUT /api/merchant/products/{id}
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Integer id, @RequestBody Product product, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        Product existingProduct = productService.findById(id);
        if (existingProduct == null || !existingProduct.getMerchant().getId().equals(merchant.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "商品不存在"));
        }
        product.setId(id);
        product.setMerchant(merchant);
        product.setCreatedAt(existingProduct.getCreatedAt());
        Product updatedProduct = productService.update(product);
        return ResponseEntity.ok(ApiResponse.success("商品更新成功", updatedProduct));
    }

    /**
     * 删除商品
     * DELETE /api/merchant/products/{id}
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Integer id, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        Product product = productService.findById(id);
        if (product == null || !product.getMerchant().getId().equals(merchant.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "商品不存在"));
        }
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("商品删除成功", null));
    }

    /**
     * 分页获取商品
     * GET /api/merchant/products/paged
     */
    @GetMapping("/products/paged")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProductsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productPage = productService.searchProducts(merchant.getId(), name, status, category, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", productPage.getContent());
        response.put("totalElements", productPage.getTotalElements());
        response.put("totalPages", productPage.getTotalPages());
        response.put("currentPage", productPage.getNumber());
        response.put("pageSize", productPage.getSize());
        response.put("hasNext", productPage.hasNext());
        response.put("hasPrevious", productPage.hasPrevious());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 更新商品状态
     * PUT /api/merchant/products/{id}/status
     */
    @PutMapping("/products/{id}/status")
    public ResponseEntity<ApiResponse<Product>> updateProductStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        Product product = productService.findById(id);
        if (product == null || !product.getMerchant().getId().equals(merchant.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "商品不存在"));
        }
        if (!"enabled".equals(status) && !"disabled".equals(status)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "无效的状态值"));
        }
        Product updatedProduct;
        if ("enabled".equals(status)) {
            updatedProduct = productService.enableProduct(id);
        } else {
            updatedProduct = productService.disableProduct(id);
        }
        return ResponseEntity.ok(ApiResponse.success("商品状态更新成功", updatedProduct));
    }

    /**
     * 批量更新商品状态
     * PUT /api/merchant/products/batch/status
     */
    @PutMapping("/products/batch/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchUpdateProductStatus(
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");
        String status = (String) request.get("status");
        
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "商品ID列表不能为空"));
        }
        if (!"enabled".equals(status) && !"disabled".equals(status)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "无效的状态值"));
        }
        
        for (Integer id : ids) {
            Product product = productService.findById(id);
            if (product == null || !product.getMerchant().getId().equals(merchant.getId())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "商品ID " + id + " 不存在或无权限"));
            }
        }
        
        int updatedCount = productService.batchUpdateStatus(ids, status);
        Map<String, Object> result = new HashMap<>();
        result.put("updatedCount", updatedCount);
        result.put("ids", ids);
        result.put("status", status);
        
        return ResponseEntity.ok(ApiResponse.success("批量更新状态成功", result));
    }

    /**
     * 批量删除商品
     * DELETE /api/merchant/products/batch
     */
    @DeleteMapping("/products/batch")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDeleteProducts(
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问"));
        }
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) request.get("ids");
        
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "商品ID列表不能为空"));
        }
        
        for (Integer id : ids) {
            Product product = productService.findById(id);
            if (product == null || !product.getMerchant().getId().equals(merchant.getId())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "商品ID " + id + " 不存在或无权限"));
            }
        }
        
        productService.batchDelete(ids);
        Map<String, Object> result = new HashMap<>();
        result.put("deletedCount", ids.size());
        result.put("ids", ids);
        
        return ResponseEntity.ok(ApiResponse.success("批量删除成功", result));
    }

    // ==================== 分类管理 API ====================

    /**
     * 获取分类列表
     * GET /api/merchant/categories
     * 
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含分类列表
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Category>>> getCategories(HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            List<Category> categories = categoryService.findByMerchantIdSorted(merchant.getId());
            return ResponseEntity.ok(ApiResponse.success(categories));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取分类列表失败：" + e.getMessage()));
        }
    }

    /**
     * 添加分类
     * POST /api/merchant/categories
     * 
     * @param category 分类信息
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含创建的分类
     */
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<Category>> addCategory(@RequestBody Category category, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            category.setMerchantId(merchant.getId());
            Category createdCategory = categoryService.create(category);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("分类添加成功", createdCategory));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "添加分类失败：" + e.getMessage()));
        }
    }

    /**
     * 更新分类
     * PUT /api/merchant/categories/{id}
     * 
     * @param id 分类ID
     * @param category 更新的分类信息
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含更新后的分类
     */
    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable Integer id,
            @RequestBody Category category,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            Category existingCategory = categoryService.findById(id);
            if (existingCategory == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "分类不存在"));
            }
            if (!existingCategory.getMerchantId().equals(merchant.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "无权操作此分类"));
            }
            category.setId(id);
            category.setMerchantId(merchant.getId());
            category.setStatus(existingCategory.getStatus());
            category.setProductCount(existingCategory.getProductCount());
            category.setCreatedAt(existingCategory.getCreatedAt());
            Category updatedCategory = categoryService.update(category);
            return ResponseEntity.ok(ApiResponse.success("分类更新成功", updatedCategory));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "更新分类失败：" + e.getMessage()));
        }
    }

    /**
     * 删除分类
     * DELETE /api/merchant/categories/{id}
     * 
     * @param id 分类ID
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式
     */
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Integer id, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            Category category = categoryService.findById(id);
            if (category == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "分类不存在"));
            }
            if (!category.getMerchantId().equals(merchant.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "无权操作此分类"));
            }
            categoryService.delete(id);
            return ResponseEntity.ok(ApiResponse.success("分类删除成功", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "删除分类失败：" + e.getMessage()));
        }
    }

    /**
     * 更新分类状态
     * PUT /api/merchant/categories/{id}/status
     * 
     * @param id 分类ID
     * @param status 新状态（enabled/disabled）
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含更新后的分类
     */
    @PutMapping("/categories/{id}/status")
    public ResponseEntity<ApiResponse<Category>> updateCategoryStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            Category category = categoryService.findById(id);
            if (category == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "分类不存在"));
            }
            if (!category.getMerchantId().equals(merchant.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "无权操作此分类"));
            }
            if (!"enabled".equals(status) && !"disabled".equals(status)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "无效的状态值，只能是 enabled 或 disabled"));
            }
            Category updatedCategory = categoryService.toggleStatus(id);
            return ResponseEntity.ok(ApiResponse.success("分类状态更新成功", updatedCategory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "更新分类状态失败：" + e.getMessage()));
        }
    }

    /**
     * 批量更新分类状态
     * PUT /api/merchant/categories/batch/status
     * 
     * @param request 包含ids（分类ID列表）和status（目标状态）的请求体
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含更新结果
     */
    @PutMapping("/categories/batch/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchUpdateCategoryStatus(
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            @SuppressWarnings("unchecked")
            List<Integer> ids = (List<Integer>) request.get("ids");
            String status = (String) request.get("status");
            
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "分类ID列表不能为空"));
            }
            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "状态值不能为空"));
            }
            if (!"enabled".equals(status) && !"disabled".equals(status)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "无效的状态值，只能是 enabled 或 disabled"));
            }
            
            for (Integer id : ids) {
                Category category = categoryService.findById(id);
                if (category == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error(404, "分类ID " + id + " 不存在"));
                }
                if (!category.getMerchantId().equals(merchant.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(ApiResponse.error(403, "无权操作分类ID " + id));
                }
            }
            
            int updatedCount;
            if ("enabled".equals(status)) {
                updatedCount = categoryService.batchEnable(ids);
            } else {
                updatedCount = categoryService.batchDisable(ids);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("updatedCount", updatedCount);
            result.put("ids", ids);
            result.put("status", status);
            
            return ResponseEntity.ok(ApiResponse.success("批量更新分类状态成功", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "批量更新分类状态失败：" + e.getMessage()));
        }
    }

    /**
     * 批量删除分类
     * DELETE /api/merchant/categories/batch
     * 
     * @param request 包含ids（分类ID列表）的请求体
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含删除结果
     */
    @DeleteMapping("/categories/batch")
    public ResponseEntity<ApiResponse<Map<String, Object>>> batchDeleteCategories(
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            @SuppressWarnings("unchecked")
            List<Integer> ids = (List<Integer>) request.get("ids");
            
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "分类ID列表不能为空"));
            }
            
            for (Integer id : ids) {
                Category category = categoryService.findById(id);
                if (category == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error(404, "分类ID " + id + " 不存在"));
                }
                if (!category.getMerchantId().equals(merchant.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(ApiResponse.error(403, "无权操作分类ID " + id));
                }
                if (category.getProductCount() > 0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(ApiResponse.error(400, "分类 " + category.getName() + " 下存在商品，无法删除"));
                }
            }
            
            categoryService.batchDelete(ids);
            Map<String, Object> result = new HashMap<>();
            result.put("deletedCount", ids.size());
            result.put("ids", ids);
            
            return ResponseEntity.ok(ApiResponse.success("批量删除分类成功", result));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "批量删除分类失败：" + e.getMessage()));
        }
    }

    // ==================== 评价管理 API ====================

    /**
     * 获取评价列表（分页）
     * GET /api/merchant/reviews
     * 
     * @param page 页码（默认0）
     * @param size 每页大小（默认10）
     * @param sortBy 排序字段（默认createdAt）
     * @param sortDir 排序方向（默认desc）
     * @param rating 评分筛选（可选，1-5）
     * @param keyword 关键字搜索（可选）
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含评价列表和分页信息
     */
    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String keyword,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            Page<Review> reviewPage = reviewService.getReviewsWithPaging(
                    merchant.getId(), rating, keyword, page, size, sortBy, sortDir);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", reviewPage.getContent());
            response.put("totalElements", reviewPage.getTotalElements());
            response.put("totalPages", reviewPage.getTotalPages());
            response.put("currentPage", reviewPage.getNumber());
            response.put("pageSize", reviewPage.getSize());
            response.put("hasNext", reviewPage.hasNext());
            response.put("hasPrevious", reviewPage.hasPrevious());
            
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取评价列表失败：" + e.getMessage()));
        }
    }

    /**
     * 获取评价统计信息
     * GET /api/merchant/reviews/statistics
     * 
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含平均评分和评分分布
     */
    @GetMapping("/reviews/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReviewStatistics(HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            Map<String, Object> statistics = reviewService.getReviewStatistics(merchant.getId());
            return ResponseEntity.ok(ApiResponse.success(statistics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取评价统计失败：" + e.getMessage()));
        }
    }

    /**
     * 获取评价详情
     * GET /api/merchant/reviews/{id}
     * 
     * @param id 评价ID
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含评价详情
     */
    @GetMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse<Review>> getReviewById(
            @PathVariable Integer id,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            Review review = reviewService.findById(id);
            if (review == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "评价不存在"));
            }
            if (!review.getMerchant().getId().equals(merchant.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "无权查看此评价"));
            }
            return ResponseEntity.ok(ApiResponse.success(review));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取评价详情失败：" + e.getMessage()));
        }
    }

    /**
     * 回复评价
     * PUT /api/merchant/reviews/{id}/reply
     * 
     * @param id 评价ID
     * @param request 包含回复内容的请求体
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含更新后的评价
     */
    @PutMapping("/reviews/{id}/reply")
    public ResponseEntity<ApiResponse<Review>> replyToReview(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            Review review = reviewService.findById(id);
            if (review == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "评价不存在"));
            }
            if (!review.getMerchant().getId().equals(merchant.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "无权回复此评价"));
            }
            
            String replyContent = request.get("replyContent");
            if (replyContent == null || replyContent.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "回复内容不能为空"));
            }
            
            Review updatedReview = reviewService.replyToReview(id, replyContent.trim());
            return ResponseEntity.ok(ApiResponse.success("回复成功", updatedReview));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "回复评价失败：" + e.getMessage()));
        }
    }

    /**
     * 删除评价
     * DELETE /api/merchant/reviews/{id}
     * 
     * @param id 评价ID
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式
     */
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Integer id,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            Review review = reviewService.findById(id);
            if (review == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "评价不存在"));
            }
            if (!review.getMerchant().getId().equals(merchant.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "无权删除此评价"));
            }
            
            reviewService.delete(id);
            return ResponseEntity.ok(ApiResponse.success("评价删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "删除评价失败：" + e.getMessage()));
        }
    }

    /**
     * 获取最近评价列表
     * GET /api/merchant/reviews/recent
     * 
     * @param limit 数量限制（默认5）
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含最近评价列表
     */
    @GetMapping("/reviews/recent")
    public ResponseEntity<ApiResponse<List<Review>>> getRecentReviews(
            @RequestParam(defaultValue = "5") int limit,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            List<Review> reviews = reviewService.getRecentReviews(merchant.getId(), limit);
            return ResponseEntity.ok(ApiResponse.success(reviews));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取最近评价失败：" + e.getMessage()));
        }
    }

    // ==================== 统计分析 API ====================

    /**
     * 获取商家首页统计数据
     * GET /api/merchant/dashboard
     * 
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含首页统计数据
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats(HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            Map<String, Object> stats = merchantStatsService.getDashboardStats(merchant.getId());
            List<Appointment> recentAppointments = merchantStatsService.getRecentAppointments(merchant.getId(), 5);
            stats.put("recentAppointments", recentAppointments);
            List<Review> recentReviews = merchantStatsService.getRecentReviews(merchant.getId(), 5);
            stats.put("recentReviews", recentReviews);
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取首页统计数据失败：" + e.getMessage()));
        }
    }

    /**
     * 获取营收统计
     * GET /api/merchant/revenue-stats
     * 
     * @param startDate 开始日期（可选，默认本月第一天）
     * @param endDate 结束日期（可选，默认今天）
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含营收统计数据
     */
    @GetMapping("/revenue-stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRevenueStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().withDayOfMonth(1);
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
            Map<String, Object> stats = merchantStatsService.getRevenueStats(merchant.getId(), start, end);
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取营收统计失败：" + e.getMessage()));
        }
    }

    /**
     * 导出营收统计
     * GET /api/merchant/revenue-stats/export
     * 
     * @param startDate 开始日期（可选，默认本月第一天）
     * @param endDate 结束日期（可选，默认今天）
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含导出数据
     */
    @GetMapping("/revenue-stats/export")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> exportRevenueStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().withDayOfMonth(1);
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
            List<Map<String, Object>> exportData = merchantStatsService.getRevenueStatsForExport(merchant.getId(), start, end);
            return ResponseEntity.ok(ApiResponse.success("导出成功", exportData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "导出营收统计失败：" + e.getMessage()));
        }
    }

    /**
     * 获取预约统计
     * GET /api/merchant/appointment-stats
     * 
     * @param startDate 开始日期（可选，默认本月第一天）
     * @param endDate 结束日期（可选，默认今天）
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含预约统计数据
     */
    @GetMapping("/appointment-stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAppointmentStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().withDayOfMonth(1);
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
            Map<String, Object> stats = merchantStatsService.getAppointmentStats(merchant.getId(), start, end);
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取预约统计失败：" + e.getMessage()));
        }
    }

    /**
     * 导出预约统计
     * GET /api/merchant/appointment-stats/export
     * 
     * @param startDate 开始日期（可选，默认本月第一天）
     * @param endDate 结束日期（可选，默认今天）
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含导出数据
     */
    @GetMapping("/appointment-stats/export")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> exportAppointmentStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().withDayOfMonth(1);
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();
            List<Map<String, Object>> exportData = merchantStatsService.getAppointmentStatsForExport(merchant.getId(), start, end);
            return ResponseEntity.ok(ApiResponse.success("导出成功", exportData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "导出预约统计失败：" + e.getMessage()));
        }
    }

    // ==================== 店铺设置 API ====================

    /**
     * 获取店铺设置
     * GET /api/merchant/settings
     * 
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含店铺设置信息
     */
    @GetMapping("/settings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSettings(HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            Map<String, Object> settings = merchantSettingsService.getSettingsOverview(merchant.getId());
            return ResponseEntity.ok(ApiResponse.success(settings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取店铺设置失败：" + e.getMessage()));
        }
    }

    /**
     * 更新店铺设置
     * PUT /api/merchant/settings
     * 
     * @param settings 更新的设置信息
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含更新后的设置
     */
    @PutMapping("/settings")
    public ResponseEntity<ApiResponse<MerchantSettings>> updateSettings(
            @RequestBody MerchantSettings settings,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            MerchantSettings updatedSettings = merchantSettingsService.updateSettings(merchant.getId(), settings);
            return ResponseEntity.ok(ApiResponse.success("店铺设置更新成功", updatedSettings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "更新店铺设置失败：" + e.getMessage()));
        }
    }

    /**
     * 切换店铺营业状态
     * POST /api/merchant/settings/toggle-status
     * 
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式，包含新的营业状态
     */
    @PostMapping("/settings/toggle-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleShopStatus(HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            boolean isOpen = merchantSettingsService.toggleShopStatus(merchant.getId());
            Map<String, Object> result = new HashMap<>();
            result.put("isOpen", isOpen);
            return ResponseEntity.ok(ApiResponse.success(isOpen ? "店铺已开启营业" : "店铺已关闭休息", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "切换店铺状态失败：" + e.getMessage()));
        }
    }

    /**
     * 修改密码
     * POST /api/merchant/change-password
     * 
     * @param request 包含oldPassword和newPassword的请求体
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody Map<String, String> request,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");
            
            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "原密码不能为空"));
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "新密码不能为空"));
            }
            if (newPassword.length() < 6) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "新密码长度不能少于6位"));
            }
            
            boolean success = merchantSettingsService.changePassword(merchant.getId(), oldPassword, newPassword);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("密码修改成功", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "原密码错误"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "修改密码失败：" + e.getMessage()));
        }
    }

    /**
     * 绑定手机号
     * POST /api/merchant/bind-phone
     * 
     * @param request 包含phone和verifyCode的请求体
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式
     */
    @PostMapping("/bind-phone")
    public ResponseEntity<ApiResponse<Void>> bindPhone(
            @RequestBody Map<String, String> request,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            String phone = request.get("phone");
            String verifyCode = request.get("verifyCode");
            
            if (phone == null || phone.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "手机号不能为空"));
            }
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "手机号格式不正确"));
            }
            if (verifyCode == null || verifyCode.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "验证码不能为空"));
            }
            
            boolean success = merchantSettingsService.bindPhone(merchant.getId(), phone, verifyCode);
            if (success) {
                Merchant updatedMerchant = merchantService.findById(merchant.getId());
                session.setAttribute("merchant", updatedMerchant);
                return ResponseEntity.ok(ApiResponse.success("手机号绑定成功", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "绑定失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "绑定手机号失败：" + e.getMessage()));
        }
    }

    /**
     * 绑定邮箱
     * POST /api/merchant/bind-email
     * 
     * @param request 包含email和verifyCode的请求体
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式
     */
    @PostMapping("/bind-email")
    public ResponseEntity<ApiResponse<Void>> bindEmail(
            @RequestBody Map<String, String> request,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            String email = request.get("email");
            String verifyCode = request.get("verifyCode");
            
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "邮箱不能为空"));
            }
            if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "邮箱格式不正确"));
            }
            if (verifyCode == null || verifyCode.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "验证码不能为空"));
            }
            
            boolean success = merchantSettingsService.bindEmail(merchant.getId(), email, verifyCode);
            if (success) {
                Merchant updatedMerchant = merchantService.findById(merchant.getId());
                session.setAttribute("merchant", updatedMerchant);
                return ResponseEntity.ok(ApiResponse.success("邮箱绑定成功", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "该邮箱已被其他商家使用"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "绑定邮箱失败：" + e.getMessage()));
        }
    }

    /**
     * 发送验证码
     * POST /api/merchant/send-verify-code
     * 
     * @param request 包含type（phone/email）和target的请求体
     * @param session HTTP会话，用于验证商家身份
     * @return 统一响应格式
     */
    @PostMapping("/send-verify-code")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendVerifyCode(
            @RequestBody Map<String, String> request,
            HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "未授权访问，请先登录"));
        }
        try {
            String type = request.get("type");
            String target = request.get("target");
            
            if (type == null || (!"phone".equals(type) && !"email".equals(type))) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "验证码类型无效"));
            }
            if (target == null || target.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "目标地址不能为空"));
            }
            
            String verifyCode = merchantSettingsService.generateVerifyCode();
            session.setAttribute("verifyCode_" + type, verifyCode);
            session.setAttribute("verifyCodeTarget_" + type, target);
            session.setMaxInactiveInterval(300);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", verifyCode);
            result.put("type", type);
            result.put("message", "验证码已发送");
            
            return ResponseEntity.ok(ApiResponse.success("验证码发送成功", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "发送验证码失败：" + e.getMessage()));
        }
    }
}
