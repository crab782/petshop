package com.petshop.repository;

import com.petshop.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByUserId(Integer userId);
    List<Appointment> findByMerchantId(Integer merchantId);
}
