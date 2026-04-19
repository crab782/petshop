package com.petshop.service;

import com.petshop.dto.*;
import com.petshop.entity.*;
import com.petshop.exception.BadRequestException;
import com.petshop.exception.ResourceNotFoundException;
import com.petshop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductOrderService {
    @Autowired
    private ProductOrderRepository productOrderRepository;
    
    @Autowired
    private ProductOrderItemRepository productOrderItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private UserRepository userRepository;

    public ProductOrder findById(Integer id) {
        return productOrderRepository.findById(id).orElse(null);
    }

    public List<ProductOrder> findByMerchantId(Integer merchantId) {
        return productOrderRepository.findByMerchantId(merchantId);
    }

    public List<ProductOrder> findByUserId(Integer userId) {
        return productOrderRepository.findByUserId(userId);
    }

    public List<ProductOrder> findByMerchantIdAndStatus(Integer merchantId, String status) {
        return productOrderRepository.findByMerchantIdAndStatus(merchantId, status);
    }

    public ProductOrder update(ProductOrder order) {
        return productOrderRepository.save(order);
    }
    
    public PageResponse<OrderDTO> getUserOrders(Integer userId, String status, String keyword, 
            LocalDateTime startDate, LocalDateTime endDate, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProductOrder> orderPage = productOrderRepository.searchUserOrders(
                userId, status, keyword, startDate, endDate, pageable);
        
        List<OrderDTO> orders = orderPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return PageResponse.<OrderDTO>builder()
                .data(orders)
                .total(orderPage.getTotalElements())
                .page(page)
                .pageSize(pageSize)
                .totalPages(orderPage.getTotalPages())
                .build();
    }
    
    public OrderDTO getOrderDetail(Integer orderId, Integer userId) {
        ProductOrder order = productOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("You don't have permission to view this order");
        }
        
        return convertToDetailDTO(order);
    }
    
    @Transactional
    public CreateOrderResponse createOrder(Integer userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        
        if (!address.getUser().getId().equals(userId)) {
            throw new BadRequestException("Address does not belong to user");
        }
        
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Order items cannot be empty");
        }
        
        Map<Integer, Product> productMap = new HashMap<>();
        Map<Integer, Integer> merchantProductCount = new HashMap<>();
        BigDecimal totalProductPrice = BigDecimal.ZERO;
        
        for (CreateOrderRequest.OrderItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductId()));
            
            if (!"enabled".equals(product.getStatus())) {
                throw new BadRequestException("Product is not available: " + product.getName());
            }
            
            if (product.getStock() < item.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }
            
            productMap.put(product.getId(), product);
            merchantProductCount.merge(product.getMerchant().getId(), 1, Integer::sum);
            totalProductPrice = totalProductPrice.add(
                    product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        
        if (merchantProductCount.size() > 1) {
            throw new BadRequestException("All products must be from the same merchant");
        }
        
        Integer merchantId = merchantProductCount.keySet().iterator().next();
        Merchant merchant = productMap.values().iterator().next().getMerchant();
        
        ProductOrder order = new ProductOrder();
        order.setUser(user);
        order.setMerchant(merchant);
        order.setTotalPrice(totalProductPrice);
        order.setFreight(BigDecimal.ZERO);
        order.setStatus("pending");
        order.setPayMethod(request.getPaymentMethod());
        order.setRemark(request.getRemark());
        order.setShippingAddress(formatAddress(address));
        
        order = productOrderRepository.save(order);
        
        for (CreateOrderRequest.OrderItemRequest item : request.getItems()) {
            Product product = productMap.get(item.getProductId());
            
            ProductOrderItem orderItem = new ProductOrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice());
            productOrderItemRepository.save(orderItem);
            
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }
        
        return new CreateOrderResponse(order.getId(), order.getOrderNo());
    }
    
    public OrderPreviewDTO previewOrder(Integer userId, PreviewOrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Order items cannot be empty");
        }
        
        List<OrderPreviewItemDTO> previewItems = new ArrayList<>();
        BigDecimal productTotal = BigDecimal.ZERO;
        
        for (PreviewOrderRequest.OrderItemRequest item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + item.getProductId()));
            
            if (!"enabled".equals(product.getStatus())) {
                throw new BadRequestException("Product is not available: " + product.getName());
            }
            
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            productTotal = productTotal.add(subtotal);
            
            previewItems.add(OrderPreviewItemDTO.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .productImage(product.getImage())
                    .price(product.getPrice())
                    .quantity(item.getQuantity())
                    .subtotal(subtotal)
                    .build());
        }
        
        BigDecimal shippingFee = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal totalAmount = productTotal.add(shippingFee).subtract(discount);
        
        return OrderPreviewDTO.builder()
                .items(previewItems)
                .productTotal(productTotal)
                .shippingFee(shippingFee)
                .discount(discount)
                .totalAmount(totalAmount)
                .build();
    }
    
    @Transactional
    public PayResponse payOrder(Integer orderId, Integer userId, String payMethod) {
        ProductOrder order = productOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("You don't have permission to pay this order");
        }
        
        if (!"pending".equals(order.getStatus())) {
            throw new BadRequestException("Order cannot be paid in current status: " + order.getStatus());
        }
        
        order.setStatus("paid");
        order.setPayMethod(payMethod);
        order.setPaidAt(LocalDateTime.now());
        order.setTransactionId("TXN" + System.currentTimeMillis());
        productOrderRepository.save(order);
        
        return PayResponse.builder()
                .payId(order.getId())
                .qrCodeUrl(null)
                .redirectUrl(null)
                .expireTime(LocalDateTime.now().plusMinutes(30))
                .build();
    }
    
    public PayStatusResponse getPayStatus(Integer orderId, Integer userId) {
        ProductOrder order = productOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("You don't have permission to view this order");
        }
        
        String payStatus;
        switch (order.getStatus()) {
            case "pending":
                payStatus = "pending";
                break;
            case "paid":
            case "shipped":
            case "completed":
                payStatus = "success";
                break;
            case "cancelled":
                payStatus = "failed";
                break;
            default:
                payStatus = "pending";
        }
        
        return PayStatusResponse.builder()
                .orderId(order.getId())
                .payStatus(payStatus)
                .payTime(order.getPaidAt())
                .transactionId(order.getTransactionId())
                .build();
    }
    
    @Transactional
    public void cancelOrder(Integer orderId, Integer userId) {
        ProductOrder order = productOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("You don't have permission to cancel this order");
        }
        
        if (!Arrays.asList("pending", "paid").contains(order.getStatus())) {
            throw new BadRequestException("Order cannot be cancelled in current status: " + order.getStatus());
        }
        
        if ("paid".equals(order.getStatus())) {
            restoreStock(order.getId());
        }
        
        order.setStatus("cancelled");
        order.setCancelledAt(LocalDateTime.now());
        productOrderRepository.save(order);
    }
    
    @Transactional
    public void refundOrder(Integer orderId, Integer userId, String reason) {
        ProductOrder order = productOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("You don't have permission to refund this order");
        }
        
        if (!Arrays.asList("paid", "shipped").contains(order.getStatus())) {
            throw new BadRequestException("Order cannot be refunded in current status: " + order.getStatus());
        }
        
        restoreStock(order.getId());
        
        order.setStatus("cancelled");
        order.setCancelledAt(LocalDateTime.now());
        productOrderRepository.save(order);
    }
    
    @Transactional
    public void confirmReceive(Integer orderId, Integer userId) {
        ProductOrder order = productOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("You don't have permission to confirm this order");
        }
        
        if (!"shipped".equals(order.getStatus())) {
            throw new BadRequestException("Order cannot be confirmed in current status: " + order.getStatus());
        }
        
        order.setStatus("completed");
        order.setCompletedAt(LocalDateTime.now());
        productOrderRepository.save(order);
    }
    
    @Transactional
    public void deleteOrder(Integer orderId, Integer userId) {
        ProductOrder order = productOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("You don't have permission to delete this order");
        }
        
        if (!Arrays.asList("completed", "cancelled").contains(order.getStatus())) {
            throw new BadRequestException("Only completed or cancelled orders can be deleted");
        }
        
        productOrderItemRepository.deleteByOrderId(orderId);
        productOrderRepository.delete(order);
    }
    
    @Transactional
    public void batchCancel(List<Integer> ids, Integer userId) {
        for (Integer id : ids) {
            try {
                cancelOrder(id, userId);
            } catch (Exception e) {
            }
        }
    }
    
    @Transactional
    public void batchDelete(List<Integer> ids, Integer userId) {
        for (Integer id : ids) {
            try {
                deleteOrder(id, userId);
            } catch (Exception e) {
            }
        }
    }
    
    private void restoreStock(Integer orderId) {
        List<ProductOrderItem> items = productOrderItemRepository.findByOrderId(orderId);
        for (ProductOrderItem item : items) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
    }
    
    private String formatAddress(Address address) {
        return String.format("%s %s %s %s (%s %s)",
                address.getProvince(), address.getCity(), address.getDistrict(),
                address.getDetailAddress(), address.getContactName(), address.getPhone());
    }
    
    private OrderDTO convertToDTO(ProductOrder order) {
        List<ProductOrderItem> items = productOrderItemRepository.findByOrderId(order.getId());
        
        List<OrderItemDTO> itemDTOs = items.stream()
                .map(item -> OrderItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productImage(item.getProduct().getImage())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .collect(Collectors.toList());
        
        return OrderDTO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .status(order.getStatus())
                .createTime(order.getCreatedAt())
                .payTime(order.getPaidAt())
                .shipTime(order.getShippedAt())
                .completeTime(order.getCompletedAt())
                .cancelTime(order.getCancelledAt())
                .totalPrice(order.getTotalPrice())
                .freight(order.getFreight())
                .payMethod(order.getPayMethod())
                .remark(order.getRemark())
                .items(itemDTOs)
                .build();
    }
    
    private OrderDTO convertToDetailDTO(ProductOrder order) {
        OrderDTO dto = convertToDTO(order);
        
        String[] addressParts = order.getShippingAddress().split("\\s+");
        OrderAddressDTO addressDTO = OrderAddressDTO.builder()
                .address(order.getShippingAddress())
                .build();
        dto.setAddress(addressDTO);
        
        List<OrderTimelineItemDTO> timeline = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        timeline.add(OrderTimelineItemDTO.builder()
                .status("created")
                .time(order.getCreatedAt() != null ? order.getCreatedAt().format(formatter) : null)
                .description("Order created")
                .build());
        
        if (order.getPaidAt() != null) {
            timeline.add(OrderTimelineItemDTO.builder()
                    .status("paid")
                    .time(order.getPaidAt().format(formatter))
                    .description("Payment completed")
                    .build());
        }
        
        if (order.getShippedAt() != null) {
            timeline.add(OrderTimelineItemDTO.builder()
                    .status("shipped")
                    .time(order.getShippedAt().format(formatter))
                    .description("Order shipped")
                    .build());
        }
        
        if (order.getCompletedAt() != null) {
            timeline.add(OrderTimelineItemDTO.builder()
                    .status("completed")
                    .time(order.getCompletedAt().format(formatter))
                    .description("Order completed")
                    .build());
        }
        
        if (order.getCancelledAt() != null) {
            timeline.add(OrderTimelineItemDTO.builder()
                    .status("cancelled")
                    .time(order.getCancelledAt().format(formatter))
                    .description("Order cancelled")
                    .build());
        }
        
        dto.setTimeline(timeline);
        return dto;
    }
}
