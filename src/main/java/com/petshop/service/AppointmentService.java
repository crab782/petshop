package com.petshop.service;

import com.petshop.dto.AppointmentDTO;
import com.petshop.dto.UserPurchasedServiceDTO;
import com.petshop.entity.Appointment;
import com.petshop.entity.Merchant;
import com.petshop.entity.Pet;
import com.petshop.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    public Appointment create(Appointment appointment) {
        appointment.setStatus("pending");
        return appointmentRepository.save(appointment);
    }

    public Appointment findById(Integer id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    public List<Appointment> findByUserId(Integer userId) {
        return appointmentRepository.findByUserId(userId);
    }

    public List<Appointment> findByMerchantId(Integer merchantId) {
        return appointmentRepository.findByMerchantId(merchantId);
    }

    public Appointment update(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public void delete(Integer id) {
        appointmentRepository.deleteById(id);
    }

    public Page<UserPurchasedServiceDTO> findPurchasedServices(Integer userId, String keyword, String status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<Appointment> appointmentPage = appointmentRepository.findPurchasedServices(userId, keyword, status, pageable);

        return appointmentPage.map(this::convertToPurchasedServiceDTO);
    }

    private UserPurchasedServiceDTO convertToPurchasedServiceDTO(Appointment appointment) {
        String serviceStatus = mapAppointmentStatusToServiceStatus(appointment.getStatus());

        return UserPurchasedServiceDTO.builder()
                .id(appointment.getId())
                .name(appointment.getService().getName())
                .merchant(appointment.getMerchant().getName())
                .merchantId(appointment.getMerchant().getId())
                .price(appointment.getTotalPrice())
                .purchaseDate(appointment.getCreatedAt())
                .expiryDate(appointment.getAppointmentTime())
                .status(serviceStatus)
                .category(appointment.getService().getDescription() != null ? "General" : "General")
                .serviceId(appointment.getService().getId())
                .build();
    }

    private String mapAppointmentStatusToServiceStatus(String appointmentStatus) {
        if (appointmentStatus == null) {
            return "active";
        }
        switch (appointmentStatus) {
            case "completed":
                return "used";
            case "confirmed":
                return "active";
            case "cancelled":
                return "expired";
            default:
                return "active";
        }
    }

    @Transactional
    public Appointment createAppointment(com.petshop.entity.User user, com.petshop.entity.Service service, Pet pet, 
                                         LocalDateTime appointmentTime, String remark) {
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setService(service);
        appointment.setMerchant(service.getMerchant());
        appointment.setPet(pet);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setNotes(remark);
        appointment.setTotalPrice(service.getPrice());
        appointment.setStatus("pending");
        return appointmentRepository.save(appointment);
    }

    public Page<AppointmentDTO> findByUserIdWithFilters(Integer userId, String status, String keyword,
                                                        LocalDateTime startDate, LocalDateTime endDate,
                                                        int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<Appointment> appointmentPage = appointmentRepository.findByUserIdWithFilters(
                userId, status, keyword, startDate, endDate, pageable);
        return appointmentPage.map(this::convertToDTO);
    }

    public AppointmentDTO findByIdAndUserId(Integer id, Integer userId) {
        return appointmentRepository.findByIdAndUserIdWithDetails(id, userId)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Transactional
    public boolean cancelAppointment(Integer id, Integer userId) {
        Appointment appointment = appointmentRepository.findByIdAndUserIdWithDetails(id, userId).orElse(null);
        if (appointment == null) {
            return false;
        }
        if ("cancelled".equals(appointment.getStatus()) || "completed".equals(appointment.getStatus())) {
            return false;
        }
        appointment.setStatus("cancelled");
        appointmentRepository.save(appointment);
        return true;
    }

    public Map<String, Long> getAppointmentStats(Integer userId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", appointmentRepository.countByUserId(userId));
        stats.put("pending", appointmentRepository.countByUserIdAndStatus(userId, "pending"));
        stats.put("confirmed", appointmentRepository.countByUserIdAndStatus(userId, "confirmed"));
        stats.put("completed", appointmentRepository.countByUserIdAndStatus(userId, "completed"));
        stats.put("cancelled", appointmentRepository.countByUserIdAndStatus(userId, "cancelled"));
        return stats;
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        com.petshop.entity.Service service = appointment.getService();
        Merchant merchant = appointment.getMerchant();
        Pet pet = appointment.getPet();

        return AppointmentDTO.builder()
                .id(appointment.getId())
                .userId(appointment.getUser().getId())
                .serviceId(service != null ? service.getId() : null)
                .merchantId(merchant != null ? merchant.getId() : null)
                .serviceName(service != null ? service.getName() : null)
                .merchantName(merchant != null ? merchant.getName() : null)
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                .remark(appointment.getNotes())
                .totalPrice(appointment.getTotalPrice())
                .petId(pet != null ? pet.getId() : null)
                .petName(pet != null ? pet.getName() : null)
                .petType(pet != null ? pet.getType() : null)
                .merchantPhone(merchant != null ? merchant.getPhone() : null)
                .merchantAddress(merchant != null ? merchant.getAddress() : null)
                .servicePrice(service != null ? service.getPrice() : null)
                .serviceDuration(service != null ? service.getDuration() : null)
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}
