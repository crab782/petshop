package com.petshop.service;

import com.petshop.dto.ActivityDTO;
import com.petshop.dto.HomeStatsDTO;
import com.petshop.entity.Appointment;
import com.petshop.entity.ProductOrder;
import com.petshop.entity.Review;
import com.petshop.repository.AppointmentRepository;
import com.petshop.repository.PetRepository;
import com.petshop.repository.ProductOrderRepository;
import com.petshop.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserHomeService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public HomeStatsDTO getHomeStats(Integer userId) {
        long petCount = petRepository.countByUserId(userId);
        long pendingAppointments = appointmentRepository.countByUserIdAndStatus(userId, "pending");
        long reviewCount = reviewRepository.countByUserId(userId);

        return new HomeStatsDTO(petCount, pendingAppointments, reviewCount);
    }

    public List<ActivityDTO> getRecentActivities(Integer userId, int limit) {
        List<ActivityDTO> activities = new ArrayList<>();

        List<Appointment> appointments = appointmentRepository.findByUserIdOrderByCreatedAtDesc(
            userId, 
            PageRequest.of(0, limit)
        );
        for (Appointment apt : appointments) {
            activities.add(ActivityDTO.builder()
                .id(apt.getId())
                .type("appointment")
                .title("预约服务: " + (apt.getService() != null ? apt.getService().getName() : "未知服务"))
                .time(formatTime(apt.getCreatedAt()))
                .status(getAppointmentStatusText(apt.getStatus()))
                .statusColor(getAppointmentStatusColor(apt.getStatus()))
                .relatedId(apt.getId())
                .build());
        }

        List<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(
            userId,
            PageRequest.of(0, limit)
        );
        for (Review review : reviews) {
            activities.add(ActivityDTO.builder()
                .id(review.getId())
                .type("review")
                .title("评价服务: " + (review.getService() != null ? review.getService().getName() : "未知服务"))
                .time(formatTime(review.getCreatedAt()))
                .status("已评价")
                .statusColor("green")
                .relatedId(review.getId())
                .build());
        }

        List<ProductOrder> orders = productOrderRepository.findByUserIdOrderByCreatedAtDesc(
            userId,
            PageRequest.of(0, limit)
        );
        for (ProductOrder order : orders) {
            activities.add(ActivityDTO.builder()
                .id(order.getId())
                .type("order")
                .title("商品订单 #" + order.getId())
                .time(formatTime(order.getCreatedAt()))
                .status(getOrderStatusText(order.getStatus()))
                .statusColor(getOrderStatusColor(order.getStatus()))
                .relatedId(order.getId())
                .build());
        }

        return activities.stream()
            .sorted(Comparator.comparing(ActivityDTO::getTime).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    private String formatTime(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        return time.format(TIME_FORMATTER);
    }

    private String getAppointmentStatusText(String status) {
        if (status == null) return "未知";
        return switch (status) {
            case "pending" -> "待处理";
            case "confirmed" -> "已确认";
            case "completed" -> "已完成";
            case "cancelled" -> "已取消";
            default -> status;
        };
    }

    private String getAppointmentStatusColor(String status) {
        if (status == null) return "grey";
        return switch (status) {
            case "pending" -> "orange";
            case "confirmed" -> "blue";
            case "completed" -> "green";
            case "cancelled" -> "grey";
            default -> "grey";
        };
    }

    private String getOrderStatusText(String status) {
        if (status == null) return "未知";
        return switch (status) {
            case "pending" -> "待支付";
            case "paid" -> "已支付";
            case "shipped" -> "已发货";
            case "completed" -> "已完成";
            case "cancelled" -> "已取消";
            default -> status;
        };
    }

    private String getOrderStatusColor(String status) {
        if (status == null) return "grey";
        return switch (status) {
            case "pending" -> "orange";
            case "paid" -> "blue";
            case "shipped" -> "purple";
            case "completed" -> "green";
            case "cancelled" -> "grey";
            default -> "grey";
        };
    }
}
