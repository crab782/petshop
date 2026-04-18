package com.petshop.service;

import com.petshop.entity.Appointment;
import com.petshop.entity.ProductOrder;
import com.petshop.entity.Review;
import com.petshop.repository.AppointmentRepository;
import com.petshop.repository.ProductOrderRepository;
import com.petshop.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class MerchantStatsService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private ProductOrderRepository productOrderRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    public Map<String, Object> getDashboardStats(Integer merchantId) {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        List<Appointment> todayAppointments = appointmentRepository.findAll().stream()
                .filter(a -> a.getMerchant().getId().equals(merchantId))
                .filter(a -> a.getAppointmentTime() != null)
                .filter(a -> !a.getAppointmentTime().isBefore(todayStart) && !a.getAppointmentTime().isAfter(todayEnd))
                .toList();
        stats.put("todayAppointments", todayAppointments.size());
        long pendingAppointments = appointmentRepository.findAll().stream()
                .filter(a -> a.getMerchant().getId().equals(merchantId))
                .filter(a -> "pending".equals(a.getStatus()))
                .count();
        stats.put("pendingAppointments", pendingAppointments);
        BigDecimal todayRevenue = todayAppointments.stream()
                .filter(a -> "completed".equals(a.getStatus()))
                .map(a -> a.getTotalPrice() != null ? a.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        List<ProductOrder> todayOrders = productOrderRepository.findAll().stream()
                .filter(o -> o.getMerchant().getId().equals(merchantId))
                .filter(o -> o.getCreatedAt() != null)
                .filter(o -> !o.getCreatedAt().isBefore(todayStart) && !o.getCreatedAt().isAfter(todayEnd))
                .toList();
        BigDecimal todayProductRevenue = todayOrders.stream()
                .filter(o -> "completed".equals(o.getStatus()) || "paid".equals(o.getStatus()) || "shipped".equals(o.getStatus()))
                .map(o -> o.getTotalPrice() != null ? o.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("todayRevenue", todayRevenue.add(todayProductRevenue));
        Double avgRating = reviewRepository.getAverageRatingByMerchantId(merchantId);
        stats.put("avgRating", avgRating != null ? Math.round(avgRating * 10) / 10.0 : 0.0);
        stats.put("totalReviews", reviewRepository.countByMerchantId(merchantId));
        return stats;
    }

    public Map<String, Object> getRevenueStats(Integer merchantId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        List<Appointment> appointments = appointmentRepository.findAll().stream()
                .filter(a -> a.getMerchant().getId().equals(merchantId))
                .filter(a -> a.getCreatedAt() != null)
                .filter(a -> !a.getCreatedAt().isBefore(start) && !a.getCreatedAt().isAfter(end))
                .toList();
        List<ProductOrder> orders = productOrderRepository.findAll().stream()
                .filter(o -> o.getMerchant().getId().equals(merchantId))
                .filter(o -> o.getCreatedAt() != null)
                .filter(o -> !o.getCreatedAt().isBefore(start) && !o.getCreatedAt().isAfter(end))
                .toList();
        BigDecimal serviceRevenue = appointments.stream()
                .filter(a -> "completed".equals(a.getStatus()))
                .map(a -> a.getTotalPrice() != null ? a.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal productRevenue = orders.stream()
                .filter(o -> "completed".equals(o.getStatus()) || "paid".equals(o.getStatus()) || "shipped".equals(o.getStatus()))
                .map(o -> o.getTotalPrice() != null ? o.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("totalRevenue", serviceRevenue.add(productRevenue));
        result.put("serviceRevenue", serviceRevenue);
        result.put("productRevenue", productRevenue);
        result.put("serviceOrderCount", appointments.stream().filter(a -> "completed".equals(a.getStatus())).count());
        result.put("productOrderCount", orders.stream().filter(o -> "completed".equals(o.getStatus()) || "paid".equals(o.getStatus()) || "shipped".equals(o.getStatus())).count());
        Map<LocalDate, BigDecimal> dailyRevenue = new TreeMap<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dailyRevenue.put(date, BigDecimal.ZERO);
        }
        for (Appointment a : appointments) {
            if (a.getCreatedAt() != null && "completed".equals(a.getStatus())) {
                LocalDate date = a.getCreatedAt().toLocalDate();
                if (dailyRevenue.containsKey(date)) {
                    BigDecimal current = dailyRevenue.get(date);
                    dailyRevenue.put(date, current.add(a.getTotalPrice() != null ? a.getTotalPrice() : BigDecimal.ZERO));
                }
            }
        }
        for (ProductOrder o : orders) {
            if (o.getCreatedAt() != null && ("completed".equals(o.getStatus()) || "paid".equals(o.getStatus()) || "shipped".equals(o.getStatus()))) {
                LocalDate date = o.getCreatedAt().toLocalDate();
                if (dailyRevenue.containsKey(date)) {
                    BigDecimal current = dailyRevenue.get(date);
                    dailyRevenue.put(date, current.add(o.getTotalPrice() != null ? o.getTotalPrice() : BigDecimal.ZERO));
                }
            }
        }
        result.put("dailyRevenue", dailyRevenue);
        return result;
    }

    public Map<String, Object> getAppointmentStats(Integer merchantId, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> result = new HashMap<>();
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        List<Appointment> appointments = appointmentRepository.findAll().stream()
                .filter(a -> a.getMerchant().getId().equals(merchantId))
                .filter(a -> a.getAppointmentTime() != null)
                .filter(a -> !a.getAppointmentTime().isBefore(start) && !a.getAppointmentTime().isAfter(end))
                .toList();
        result.put("totalAppointments", appointments.size());
        long completed = appointments.stream().filter(a -> "completed".equals(a.getStatus())).count();
        long cancelled = appointments.stream().filter(a -> "cancelled".equals(a.getStatus())).count();
        long pending = appointments.stream().filter(a -> "pending".equals(a.getStatus())).count();
        long confirmed = appointments.stream().filter(a -> "confirmed".equals(a.getStatus())).count();
        result.put("completedCount", completed);
        result.put("cancelledCount", cancelled);
        result.put("pendingCount", pending);
        result.put("confirmedCount", confirmed);
        double cancelRate = appointments.size() > 0 ? (double) cancelled / appointments.size() * 100 : 0;
        result.put("cancelRate", Math.round(cancelRate * 100) / 100.0);
        double completeRate = appointments.size() > 0 ? (double) completed / appointments.size() * 100 : 0;
        result.put("completeRate", Math.round(completeRate * 100) / 100.0);
        Map<LocalDate, Map<String, Long>> dailyStats = new TreeMap<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Map<String, Long> dayStat = new HashMap<>();
            dayStat.put("total", 0L);
            dayStat.put("completed", 0L);
            dayStat.put("cancelled", 0L);
            dailyStats.put(date, dayStat);
        }
        for (Appointment a : appointments) {
            LocalDate date = a.getAppointmentTime().toLocalDate();
            if (dailyStats.containsKey(date)) {
                Map<String, Long> dayStat = dailyStats.get(date);
                dayStat.put("total", dayStat.get("total") + 1);
                if ("completed".equals(a.getStatus())) {
                    dayStat.put("completed", dayStat.get("completed") + 1);
                } else if ("cancelled".equals(a.getStatus())) {
                    dayStat.put("cancelled", dayStat.get("cancelled") + 1);
                }
            }
        }
        result.put("dailyStats", dailyStats);
        Map<Integer, Long> hourlyDistribution = new TreeMap<>();
        for (int hour = 0; hour < 24; hour++) {
            hourlyDistribution.put(hour, 0L);
        }
        for (Appointment a : appointments) {
            int hour = a.getAppointmentTime().getHour();
            hourlyDistribution.put(hour, hourlyDistribution.get(hour) + 1);
        }
        result.put("hourlyDistribution", hourlyDistribution);
        return result;
    }

    public List<Map<String, Object>> getRevenueStatsForExport(Integer merchantId, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> exportData = new ArrayList<>();
        Map<String, Object> stats = getRevenueStats(merchantId, startDate, endDate);
        @SuppressWarnings("unchecked")
        Map<LocalDate, BigDecimal> dailyRevenue = (Map<LocalDate, BigDecimal>) stats.get("dailyRevenue");
        for (Map.Entry<LocalDate, BigDecimal> entry : dailyRevenue.entrySet()) {
            Map<String, Object> row = new HashMap<>();
            row.put("date", entry.getKey().toString());
            row.put("revenue", entry.getValue());
            exportData.add(row);
        }
        return exportData;
    }

    public List<Map<String, Object>> getAppointmentStatsForExport(Integer merchantId, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> exportData = new ArrayList<>();
        Map<String, Object> stats = getAppointmentStats(merchantId, startDate, endDate);
        @SuppressWarnings("unchecked")
        Map<LocalDate, Map<String, Long>> dailyStats = (Map<LocalDate, Map<String, Long>>) stats.get("dailyStats");
        for (Map.Entry<LocalDate, Map<String, Long>> entry : dailyStats.entrySet()) {
            Map<String, Object> row = new HashMap<>();
            row.put("date", entry.getKey().toString());
            row.put("total", entry.getValue().get("total"));
            row.put("completed", entry.getValue().get("completed"));
            row.put("cancelled", entry.getValue().get("cancelled"));
            exportData.add(row);
        }
        return exportData;
    }

    public List<Review> getRecentReviews(Integer merchantId, int limit) {
        return reviewRepository.findRecentReviewsByMerchantId(merchantId, limit);
    }

    public List<Appointment> getRecentAppointments(Integer merchantId, int limit) {
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getMerchant().getId().equals(merchantId))
                .sorted((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()))
                .limit(limit)
                .toList();
    }
}
