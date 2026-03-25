package com.petshop.service;

import com.petshop.entity.Appointment;
import com.petshop.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
}
