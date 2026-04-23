package com.petshop.controller.api;

import com.petshop.dto.*;
import com.petshop.entity.User;
import com.petshop.entity.Pet;
import com.petshop.entity.Appointment;
import com.petshop.entity.Address;
import com.petshop.entity.Review;
import com.petshop.service.UserService;
import com.petshop.service.PetService;
import com.petshop.service.AddressService;
import com.petshop.service.AppointmentService;
import com.petshop.service.ServiceService;
import com.petshop.service.ProductOrderService;
import com.petshop.service.ReviewService;
import com.petshop.service.UserHomeService;
import com.petshop.service.FavoriteService;
import com.petshop.service.NotificationService;
import com.petshop.exception.UnauthorizedException;
import com.petshop.exception.ResourceNotFoundException;
import com.petshop.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserApiController {
    @Autowired
    private UserService userService;
    @Autowired
    private PetService petService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private ProductOrderService productOrderService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserHomeService userHomeService;
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile() {
        User user = getCurrentUser();
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody User user) {
        User currentUser = getCurrentUser();
        user.setId(currentUser.getId());
        user.setPassword(currentUser.getPassword());
        User updatedUser = userService.update(user);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/home/stats")
    public ResponseEntity<ApiResponse<HomeStatsDTO>> getHomeStats() {
        User user = getCurrentUser();
        HomeStatsDTO stats = userHomeService.getHomeStats(user.getId());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/home/activities")
    public ResponseEntity<ApiResponse<List<ActivityDTO>>> getRecentActivities(
            @RequestParam(defaultValue = "10") int limit) {
        User user = getCurrentUser();
        List<ActivityDTO> activities = userHomeService.getRecentActivities(user.getId(), limit);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }

    @GetMapping("/pets")
    public ResponseEntity<ApiResponse<List<Pet>>> getPets() {
        User user = getCurrentUser();
        List<Pet> pets = petService.findByUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.success(pets));
    }

    @PostMapping("/pets")
    public ResponseEntity<Pet> addPet(@RequestBody Pet pet) {
        User user = getCurrentUser();
        pet.setUser(user);
        Pet createdPet = petService.create(pet);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPet);
    }

    @PutMapping("/pets/{id}")
    public ResponseEntity<Pet> updatePet(@PathVariable Integer id, @RequestBody Pet pet) {
        User user = getCurrentUser();
        Pet existingPet = petService.findById(id);
        if (existingPet == null || !existingPet.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        pet.setId(id);
        pet.setUser(user);
        Pet updatedPet = petService.update(pet);
        return ResponseEntity.ok(updatedPet);
    }

    @DeleteMapping("/pets/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Integer id) {
        User user = getCurrentUser();
        Pet pet = petService.findById(id);
        if (pet == null || !pet.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        petService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pets/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable Integer id) {
        User user = getCurrentUser();
        Pet pet = petService.findById(id);
        if (pet == null || !pet.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(pet);
    }

    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAppointments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        User user = getCurrentUser();
        Map<String, Object> appointmentResult = appointmentService.findByUserIdWithFilters(
                user.getId(), status, keyword, startDate, endDate, page, pageSize);
        return ResponseEntity.ok(ApiResponse.success(appointmentResult));
    }

    @GetMapping("/appointments/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Integer id) {
        User user = getCurrentUser();
        AppointmentDTO appointment = appointmentService.findByIdAndUserId(id, user.getId());
        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/appointments")
    public ResponseEntity<Map<String, Object>> createAppointment(
            @RequestBody CreateAppointmentRequest request) {
        User user = getCurrentUser();

        com.petshop.entity.Service service = serviceService.findById(request.getServiceId());
        if (service == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Service not found"));
        }

        Pet pet = petService.findById(request.getPetId());
        if (pet == null || !pet.getUserId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Pet not found or does not belong to user"));
        }

        LocalDateTime appointmentTime;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            appointmentTime = LocalDateTime.parse(request.getAppointmentTime(), formatter);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid appointment time format"));
        }

        Appointment appointment = appointmentService.createAppointment(
                user, service, pet, appointmentTime, request.getRemark());

        Map<String, Object> response = new HashMap<>();
        response.put("id", appointment.getId());
        response.put("success", true);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/appointments/{id}/cancel")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Integer id) {
        User user = getCurrentUser();

        boolean success = appointmentService.cancelAppointment(id, user.getId());
        if (!success) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/appointments/stats")
    public ResponseEntity<Map<String, Long>> getAppointmentStats() {
        User user = getCurrentUser();
        Map<String, Long> stats = appointmentService.getAppointmentStats(user.getId());
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<PageResponse<OrderDTO>>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        User user = getCurrentUser();
        PageResponse<OrderDTO> orders = productOrderService.getUserOrders(
                user.getId(), status, keyword, startDate, endDate, page, pageSize);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Integer id) {
        User user = getCurrentUser();
        OrderDTO order = productOrderService.getOrderDetail(id, user.getId());
        return ResponseEntity.ok(order);
    }

    @PostMapping("/orders")
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        User user = getCurrentUser();
        CreateOrderResponse response = productOrderService.createOrder(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/orders/preview")
    public ResponseEntity<OrderPreviewDTO> previewOrder(@RequestBody PreviewOrderRequest request) {
        User user = getCurrentUser();
        OrderPreviewDTO preview = productOrderService.previewOrder(user.getId(), request);
        return ResponseEntity.ok(preview);
    }

    @PostMapping("/orders/{id}/pay")
    public ResponseEntity<PayResponse> payOrder(
            @PathVariable Integer id,
            @RequestBody PayOrderRequest request) {
        User user = getCurrentUser();
        PayResponse response = productOrderService.payOrder(id, user.getId(), request.getPayMethod());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/{id}/pay/status")
    public ResponseEntity<PayStatusResponse> getPayStatus(@PathVariable Integer id) {
        User user = getCurrentUser();
        PayStatusResponse response = productOrderService.getPayStatus(id, user.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/orders/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Integer id) {
        User user = getCurrentUser();
        productOrderService.cancelOrder(id, user.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/{id}/refund")
    public ResponseEntity<Void> refundOrder(
            @PathVariable Integer id,
            @RequestBody RefundOrderRequest request) {
        User user = getCurrentUser();
        productOrderService.refundOrder(id, user.getId(), request.getReason());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/orders/{id}/confirm")
    public ResponseEntity<Void> confirmReceive(@PathVariable Integer id) {
        User user = getCurrentUser();
        productOrderService.confirmReceive(id, user.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        User user = getCurrentUser();
        productOrderService.deleteOrder(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/orders/batch-cancel")
    public ResponseEntity<Void> batchCancelOrders(@RequestBody BatchOperationRequest request) {
        User user = getCurrentUser();
        productOrderService.batchCancel(request.getIds(), user.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/orders/batch-delete")
    public ResponseEntity<Void> batchDeleteOrders(@RequestBody BatchOperationRequest request) {
        User user = getCurrentUser();
        productOrderService.batchDelete(request.getIds(), user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/services")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getServices(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        User user = getCurrentUser();
        Map<String, Object> serviceResult = appointmentService.findPurchasedServices(
                user.getId(), keyword, status, page, pageSize);
        return ResponseEntity.ok(ApiResponse.success(serviceResult));
    }

    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse<List<AddressDTO>>> getAddresses() {
        User user = getCurrentUser();
        List<AddressDTO> addresses = addressService.findByUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }

    @PostMapping("/addresses")
    public ResponseEntity<ApiResponse<AddressDTO>> addAddress(@RequestBody Address address) {
        User user = getCurrentUser();
        address.setUser(user);
        List<AddressDTO> existingAddresses = addressService.findByUserId(user.getId());
        if (existingAddresses.isEmpty()) {
            address.setIsDefault(true);
        }
        AddressDTO savedAddress = addressService.create(address);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(savedAddress));
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse<AddressDTO>> updateAddress(
            @PathVariable Integer id,
            @RequestBody Address address) {
        User user = getCurrentUser();
        Address existingAddress = addressService.findByIdEntity(id);
        if (existingAddress == null || !existingAddress.getUserId().equals(user.getId())) {
            throw new ResourceNotFoundException("地址不存在");
        }
        address.setId(id);
        address.setUser(user);
        address.setIsDefault(existingAddress.getIsDefault());
        AddressDTO updatedAddress = addressService.update(address);
        return ResponseEntity.ok(ApiResponse.success("地址更新成功", updatedAddress));
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Integer id) {
        User user = getCurrentUser();
        Address existingAddress = addressService.findByIdEntity(id);
        if (existingAddress == null || !existingAddress.getUserId().equals(user.getId())) {
            throw new ResourceNotFoundException("地址不存在");
        }
        addressService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("地址删除成功", null));
    }

    @PutMapping("/addresses/{id}/default")
    public ResponseEntity<ApiResponse<AddressDTO>> setDefaultAddress(
            @PathVariable Integer id) {
        User user = getCurrentUser();
        Address existingAddress = addressService.findByIdEntity(id);
        if (existingAddress == null || !existingAddress.getUserId().equals(user.getId())) {
            throw new ResourceNotFoundException("地址不存在");
        }
        AddressDTO updatedAddress = addressService.setDefault(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("设置默认地址成功", updatedAddress));
    }

    @PostMapping("/reviews")
    public ResponseEntity<ApiResponse<Review>> addReview(
            @RequestBody Map<String, Object> request) {
        User user = getCurrentUser();

        Integer appointmentId = (Integer) request.get("appointmentId");
        Integer rating = (Integer) request.get("rating");
        String comment = (String) request.get("comment");

        if (rating == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "评分不能为空"));
        }
        if (rating < 1 || rating > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "评分必须在1-5之间"));
        }

        AppointmentDTO appointment = appointmentService.findByIdAndUserId(appointmentId, user.getId());
        if (appointment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "预约不存在"));
        }

        if (!"completed".equals(appointment.getStatus())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "只能评价已完成的预约"));
        }

        Review existingReview = reviewService.findByAppointmentId(appointmentId);
        if (existingReview != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "该预约已评价"));
        }

        try {
            com.petshop.entity.Service service = serviceService.findById(appointment.getServiceId());
            com.petshop.entity.Merchant merchant = new com.petshop.entity.Merchant();
            merchant.setId(appointment.getMerchantId());

            Review review = new Review();
            review.setUser(user);
            review.setMerchant(merchant);
            review.setService(service);
            Appointment appointmentEntity = new Appointment();
            appointmentEntity.setId(appointmentId);
            review.setAppointment(appointmentEntity);
            review.setRating(rating);
            review.setComment(comment != null ? comment : "");

            Review savedReview = reviewService.create(review);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(savedReview));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "添加评价失败：" + e.getMessage()));
        }
    }

    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<?>> getReviews(
            @RequestParam(required = false) Integer rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User user = getCurrentUser();

        try {
            com.baomidou.mybatisplus.extension.plugins.pagination.Page<ReviewDTO> reviews = reviewService.findByUserIdWithPaging(user.getId(), rating, page, size);
            return ResponseEntity.ok(ApiResponse.success((Object) reviews));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取评价列表失败：" + e.getMessage()));
        }
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse<Review>> getReviewById(
            @PathVariable Integer id) {
        User user = getCurrentUser();

        Review review = reviewService.findById(id);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "评价不存在"));
        }

        if (!review.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, "无权查看此评价"));
        }

        return ResponseEntity.ok(ApiResponse.success(review));
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse<Review>> updateReview(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> request) {
        User user = getCurrentUser();

        Review review = reviewService.findById(id);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "评价不存在"));
        }

        if (!review.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, "无权修改此评价"));
        }

        Integer rating = (Integer) request.get("rating");
        String comment = (String) request.get("comment");

        if (rating != null) {
            if (rating < 1 || rating > 5) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error(400, "评分必须在1-5之间"));
            }
            review.setRating(rating);
        }

        if (comment != null) {
            review.setComment(comment);
        }

        try {
            Review updatedReview = reviewService.update(review);
            return ResponseEntity.ok(ApiResponse.success(updatedReview));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "更新评价失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Integer id) {
        User user = getCurrentUser();

        Review review = reviewService.findById(id);
        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "评价不存在"));
        }

        if (!review.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(403, "无权删除此评价"));
        }

        try {
            reviewService.delete(id);
            return ResponseEntity.ok(ApiResponse.success("评价删除成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "删除评价失败：" + e.getMessage()));
        }
    }

    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<List<com.petshop.dto.FavoriteDTO>>> getFavoriteMerchants() {
        User user = getCurrentUser();
        try {
            List<com.petshop.dto.FavoriteDTO> favorites = favoriteService.getFavoriteMerchants(user.getId());
            return ResponseEntity.ok(ApiResponse.success(favorites));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取收藏列表失败：" + e.getMessage()));
        }
    }

    @PostMapping("/favorites")
    public ResponseEntity<ApiResponse<com.petshop.dto.FavoriteDTO>> addMerchantFavorite(
            @RequestBody Map<String, Integer> request) {
        User user = getCurrentUser();
        Integer merchantId = request.get("merchantId");
        if (merchantId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "缺少merchantId参数"));
        }
        try {
            com.petshop.dto.FavoriteDTO favorite = favoriteService.addMerchantFavorite(user.getId(), merchantId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(favorite));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Already favorited")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error(409, "已经收藏过该商家"));
            } else if (e.getMessage().contains("Merchant not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "商家不存在"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "添加收藏失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/favorites/{id}")
    public ResponseEntity<ApiResponse<Void>> removeMerchantFavorite(
            @PathVariable Integer id) {
        User user = getCurrentUser();
        try {
            favoriteService.removeMerchantFavorite(user.getId(), id);
            return ResponseEntity.ok(ApiResponse.success("删除收藏成功", null));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Favorite not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "收藏不存在"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "删除收藏失败：" + e.getMessage()));
        }
    }

    @GetMapping("/favorites/services")
    public ResponseEntity<ApiResponse<List<com.petshop.dto.FavoriteServiceDTO>>> getFavoriteServices() {
        User user = getCurrentUser();
        try {
            List<com.petshop.dto.FavoriteServiceDTO> favorites = favoriteService.getFavoriteServices(user.getId());
            return ResponseEntity.ok(ApiResponse.success(favorites));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取收藏服务列表失败：" + e.getMessage()));
        }
    }

    @PostMapping("/favorites/services")
    public ResponseEntity<ApiResponse<com.petshop.dto.FavoriteServiceDTO>> addServiceFavorite(
            @RequestBody Map<String, Integer> request) {
        User user = getCurrentUser();
        Integer serviceId = request.get("serviceId");
        if (serviceId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "缺少serviceId参数"));
        }
        try {
            com.petshop.dto.FavoriteServiceDTO favorite = favoriteService.addServiceFavorite(user.getId(), serviceId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(favorite));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Already favorited")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error(409, "已经收藏过该服务"));
            } else if (e.getMessage().contains("Service not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "服务不存在"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "添加收藏失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/favorites/services/{id}")
    public ResponseEntity<ApiResponse<Void>> removeServiceFavorite(
            @PathVariable Integer id) {
        User user = getCurrentUser();
        try {
            favoriteService.removeServiceFavorite(user.getId(), id);
            return ResponseEntity.ok(ApiResponse.success("删除收藏成功", null));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Favorite not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "收藏不存在"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "删除收藏失败：" + e.getMessage()));
        }
    }

    @PostMapping("/favorites/products")
    public ResponseEntity<ApiResponse<com.petshop.dto.FavoriteProductDTO>> addProductFavorite(
            @RequestBody Map<String, Integer> request) {
        User user = getCurrentUser();
        Integer productId = request.get("productId");
        if (productId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, "缺少productId参数"));
        }
        try {
            com.petshop.dto.FavoriteProductDTO favorite = favoriteService.addProductFavorite(user.getId(), productId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(favorite));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Already favorited")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error(409, "已经收藏过该商品"));
            } else if (e.getMessage().contains("Product not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "商品不存在"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "添加收藏失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/favorites/products/{id}")
    public ResponseEntity<ApiResponse<Void>> removeProductFavorite(
            @PathVariable Integer id) {
        User user = getCurrentUser();
        try {
            favoriteService.removeProductFavorite(user.getId(), id);
            return ResponseEntity.ok(ApiResponse.success("删除收藏成功", null));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Favorite not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "收藏不存在"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "删除收藏失败：" + e.getMessage()));
        }
    }

    @GetMapping("/favorites/products/{id}/check")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkProductFavoriteStatus(
            @PathVariable Integer id) {
        User user = getCurrentUser();
        try {
            boolean favorited = favoriteService.isProductFavorited(user.getId(), id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("favorited", favorited);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "检查收藏状态失败：" + e.getMessage()));
        }
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getNotifications(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean isRead) {
        User user = getCurrentUser();
        try {
            List<NotificationDTO> notifications = notificationService.findByUserId(user.getId(), type, isRead);
            return ResponseEntity.ok(ApiResponse.success(notifications));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取通知列表失败：" + e.getMessage()));
        }
    }

    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markNotificationAsRead(
            @PathVariable Integer id) {
        User user = getCurrentUser();
        try {
            if (!notificationService.isOwner(id, user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "无权操作此通知"));
            }
            notificationService.markAsRead(id);
            return ResponseEntity.ok(ApiResponse.success("标记已读成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "标记已读失败：" + e.getMessage()));
        }
    }

    @PutMapping("/notifications/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllNotificationsAsRead() {
        User user = getCurrentUser();
        try {
            notificationService.markAllAsRead(user.getId());
            return ResponseEntity.ok(ApiResponse.success("全部标记已读成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "标记已读失败：" + e.getMessage()));
        }
    }

    @PutMapping("/notifications/batch-read")
    public ResponseEntity<ApiResponse<Void>> batchMarkNotificationsAsRead(
            @RequestBody BatchOperationRequest request) {
        User user = getCurrentUser();
        try {
            notificationService.markBatchAsRead(request.getIds(), user.getId());
            return ResponseEntity.ok(ApiResponse.success("批量标记已读成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "批量标记已读失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/notifications/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable Integer id) {
        User user = getCurrentUser();
        try {
            if (!notificationService.isOwner(id, user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(403, "无权删除此通知"));
            }
            notificationService.deleteNotification(id);
            return ResponseEntity.ok(ApiResponse.success("删除通知成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "删除通知失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/notifications/batch")
    public ResponseEntity<ApiResponse<Void>> batchDeleteNotifications(
            @RequestBody BatchOperationRequest request) {
        User user = getCurrentUser();
        try {
            notificationService.deleteBatch(request.getIds(), user.getId());
            return ResponseEntity.ok(ApiResponse.success("批量删除通知成功", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "批量删除通知失败：" + e.getMessage()));
        }
    }

    @GetMapping("/notifications/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUnreadNotificationCount() {
        User user = getCurrentUser();
        try {
            Map<String, Object> response = notificationService.getUnreadCountResponse(user.getId());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "获取未读通知数量失败：" + e.getMessage()));
        }
    }

    private User getCurrentUser() {
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedException("未授权访问，请先登录");
        }
        String phone;
        if (authentication.getPrincipal() instanceof UserDetails) {
            phone = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else {
            phone = authentication.getPrincipal().toString();
        }
        User user = userMapper.selectByPhone(phone);
        if (user == null) {
            throw new UnauthorizedException("用户不存在");
        }
        return user;
    }
}
