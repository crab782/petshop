package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petshop.dto.AppointmentDTO;
import com.petshop.dto.UserPurchasedServiceDTO;
import com.petshop.entity.Appointment;
import com.petshop.entity.Merchant;
import com.petshop.entity.Pet;
import com.petshop.entity.User;
import com.petshop.mapper.AppointmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentMapper appointmentMapper;

    public Appointment create(Appointment appointment) {
        appointment.setStatus("pending");
        appointmentMapper.insert(appointment);
        return appointment;
    }

    public Appointment findById(Integer id) {
        return appointmentMapper.selectById(id);
    }

    /**
     * 根据ID查询预约，同时加载所有关联信息（用户、服务、商家、宠物）
     *
     * @param id 预约ID
     * @return 包含所有关联信息的预约对象
     */
    public Appointment findByIdWithRelations(Integer id) {
        return appointmentMapper.selectByIdWithRelations(id);
    }

    public List<Appointment> findByUserId(Integer userId) {
        return appointmentMapper.selectList(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getUserId, userId));
    }

    public List<Appointment> findByMerchantId(Integer merchantId) {
        return appointmentMapper.selectList(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getMerchantId, merchantId));
    }

    public Appointment update(Appointment appointment) {
        appointmentMapper.updateById(appointment);
        return appointment;
    }

    public void delete(Integer id) {
        appointmentMapper.deleteById(id);
    }

    public Map<String, Object> findPurchasedServices(Integer userId, String keyword, String status, int page, int pageSize) {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getUserId, userId)
                .orderByDesc(Appointment::getCreatedAt);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Appointment::getNotes, keyword.trim())
                    .or().like(Appointment::getNotes, keyword.trim()));
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Appointment::getStatus, status);
        }
        
        Page<Appointment> appointmentPage = appointmentMapper.selectPage(
                new Page<>(page, pageSize), wrapper);
        
        List<UserPurchasedServiceDTO> dtos = appointmentPage.getRecords().stream()
                .map(this::convertToPurchasedServiceDTO)
                .collect(java.util.stream.Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", dtos);
        result.put("total", appointmentPage.getTotal());
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) appointmentPage.getTotal() / pageSize));
        return result;
    }

    private UserPurchasedServiceDTO convertToPurchasedServiceDTO(Appointment appointment) {
        String serviceStatus = mapAppointmentStatusToServiceStatus(appointment.getStatus());
        
        com.petshop.entity.Service service = appointment.getService();
        if (service == null) {
            service = new com.petshop.entity.Service();
            service.setName("未知服务");
            service.setDescription("");
        }
        
        com.petshop.entity.Merchant merchant = appointment.getMerchant();
        if (merchant == null) {
            merchant = new com.petshop.entity.Merchant();
            merchant.setId(0);
            merchant.setName("未知商家");
        }

        return UserPurchasedServiceDTO.builder()
                .id(appointment.getId())
                .name(service.getName())
                .merchant(merchant.getName())
                .merchantId(merchant.getId())
                .price(appointment.getTotalPrice())
                .purchaseDate(appointment.getCreatedAt())
                .expiryDate(appointment.getAppointmentTime())
                .status(serviceStatus)
                .category(service.getDescription() != null && !service.getDescription().isEmpty() ? "General" : "General")
                .serviceId(service.getId())
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
    public Appointment createAppointment(User user, com.petshop.entity.Service service, Pet pet, 
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
        appointmentMapper.insert(appointment);
        return appointment;
    }

    public Map<String, Object> findByUserIdWithFilters(Integer userId, String status, String keyword,
                                                        LocalDateTime startDate, LocalDateTime endDate,
                                                        int page, int pageSize) {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getUserId, userId)
                .orderByDesc(Appointment::getCreatedAt);
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Appointment::getStatus, status);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Appointment::getNotes, keyword.trim())
                    .or().like(Appointment::getNotes, keyword.trim()));
        }
        if (startDate != null) {
            wrapper.ge(Appointment::getCreatedAt, startDate);
        }
        if (endDate != null) {
            wrapper.le(Appointment::getCreatedAt, endDate);
        }
        
        Page<Appointment> appointmentPage = appointmentMapper.selectPage(
                new Page<>(page, pageSize), wrapper);
        
        List<AppointmentDTO> dtos = appointmentPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("data", dtos);
        result.put("total", appointmentPage.getTotal());
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) appointmentPage.getTotal() / pageSize));
        return result;
    }

    public AppointmentDTO findByIdAndUserId(Integer id, Integer userId) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null || !appointment.getUserId().equals(userId)) {
            return null;
        }
        return convertToDTO(appointment);
    }

    @Transactional
    public boolean cancelAppointment(Integer id, Integer userId) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null || !appointment.getUserId().equals(userId)) {
            return false;
        }
        if ("cancelled".equals(appointment.getStatus()) || "completed".equals(appointment.getStatus())) {
            return false;
        }
        appointment.setStatus("cancelled");
        appointmentMapper.updateById(appointment);
        return true;
    }

    public Map<String, Long> getAppointmentStats(Integer userId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", appointmentMapper.selectCount(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getUserId, userId)));
        stats.put("pending", appointmentMapper.selectCount(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getUserId, userId).eq(Appointment::getStatus, "pending")));
        stats.put("confirmed", appointmentMapper.selectCount(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getUserId, userId).eq(Appointment::getStatus, "confirmed")));
        stats.put("completed", appointmentMapper.selectCount(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getUserId, userId).eq(Appointment::getStatus, "completed")));
        stats.put("cancelled", appointmentMapper.selectCount(new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getUserId, userId).eq(Appointment::getStatus, "cancelled")));
        return stats;
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        com.petshop.entity.Service service = appointment.getService();
        if (service == null) {
            service = new com.petshop.entity.Service();
            service.setId(0);
            service.setName("未知服务");
            service.setPrice(java.math.BigDecimal.ZERO);
            service.setDuration(0);
        }
        
        com.petshop.entity.Merchant merchant = appointment.getMerchant();
        if (merchant == null) {
            merchant = new com.petshop.entity.Merchant();
            merchant.setId(0);
            merchant.setName("未知商家");
            merchant.setPhone("");
            merchant.setAddress("");
        }
        
        Pet pet = appointment.getPet();
        if (pet == null) {
            pet = new Pet();
            pet.setId(0);
            pet.setName("未知宠物");
            pet.setType("未知");
        }

        return AppointmentDTO.builder()
                .id(appointment.getId())
                .userId(appointment.getUser() != null ? appointment.getUser().getId() : null)
                .serviceId(service.getId())
                .merchantId(merchant.getId())
                .serviceName(service.getName())
                .merchantName(merchant.getName())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                .remark(appointment.getNotes())
                .totalPrice(appointment.getTotalPrice())
                .petId(pet.getId())
                .petName(pet.getName())
                .petType(pet.getType())
                .merchantPhone(merchant.getPhone())
                .merchantAddress(merchant.getAddress())
                .servicePrice(service.getPrice())
                .serviceDuration(service.getDuration())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}
