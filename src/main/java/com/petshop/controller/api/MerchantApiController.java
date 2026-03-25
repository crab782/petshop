package com.petshop.controller.api;

import com.petshop.entity.Merchant;
import com.petshop.entity.Service;
import com.petshop.entity.Appointment;
import com.petshop.service.MerchantService;
import com.petshop.service.ServiceService;
import com.petshop.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/merchant")
public class MerchantApiController {
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/profile")
    public ResponseEntity<Merchant> getProfile(HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(merchant);
    }

    @PutMapping("/profile")
    public ResponseEntity<Merchant> updateProfile(@RequestBody Merchant merchant, HttpSession session) {
        Merchant currentMerchant = (Merchant) session.getAttribute("merchant");
        if (currentMerchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        merchant.setId(currentMerchant.getId());
        merchant.setPassword(currentMerchant.getPassword());
        merchant.setStatus(currentMerchant.getStatus());
        Merchant updatedMerchant = merchantService.update(merchant);
        session.setAttribute("merchant", updatedMerchant);
        return ResponseEntity.ok(updatedMerchant);
    }

    @GetMapping("/services")
    public ResponseEntity<List<Service>> getServices(HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Service> services = serviceService.findByMerchantId(merchant.getId());
        return ResponseEntity.ok(services);
    }

    @PostMapping("/services")
    public ResponseEntity<Service> addService(@RequestBody Service service, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        service.setMerchant(merchant);
        Service createdService = serviceService.create(service);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
    }

    @PutMapping("/services/{id}")
    public ResponseEntity<Service> updateService(@PathVariable Integer id, @RequestBody Service service, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Service existingService = serviceService.findById(id);
        if (existingService == null || !existingService.getMerchant().getId().equals(merchant.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        service.setId(id);
        service.setMerchant(merchant);
        Service updatedService = serviceService.update(service);
        return ResponseEntity.ok(updatedService);
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Integer id, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Service service = serviceService.findById(id);
        if (service == null || !service.getMerchant().getId().equals(merchant.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        serviceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAppointments(HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Appointment> appointments = appointmentService.findByMerchantId(merchant.getId());
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/appointments/{id}/status")
    public ResponseEntity<Appointment> updateAppointmentStatus(@PathVariable Integer id, @RequestParam String status, HttpSession session) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Appointment appointment = appointmentService.findById(id);
        if (appointment == null || !appointment.getMerchant().getId().equals(merchant.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        appointment.setStatus(status);
        Appointment updatedAppointment = appointmentService.update(appointment);
        return ResponseEntity.ok(updatedAppointment);
    }
}
