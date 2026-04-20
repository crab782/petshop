package com.petshop.controller;

import com.petshop.entity.Merchant;
import com.petshop.entity.Service;
import com.petshop.entity.Appointment;
import com.petshop.service.MerchantService;
import com.petshop.service.ServiceService;
import com.petshop.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.BeanName;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/merchant")
@BeanName("mvcMerchantController")
public class MerchantController {
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private AppointmentService appointmentService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return "redirect:/login";
        }
        return "redirect:/merchant-dashboard.html";
    }

    @GetMapping("/services")
    public String services(HttpSession session, Model model) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return "redirect:/login";
        }
        return "redirect:/merchant-services.html";
    }

    @GetMapping("/services/add")
    public String addServiceForm(Model model) {
        return "redirect:/merchant-add-service.html";
    }

    @PostMapping("/services/add")
    public String addService(@ModelAttribute Service service, HttpSession session, RedirectAttributes redirectAttributes) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        service.setMerchant(merchant);
        serviceService.create(service);
        redirectAttributes.addFlashAttribute("success", "服务添加成功");
        return "redirect:/merchant/services";
    }

    @GetMapping("/appointments")
    public String appointments(HttpSession session, Model model) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return "redirect:/login";
        }
        return "redirect:/merchant-appointments.html";
    }

    @PostMapping("/appointments/{id}/status")
    public String updateAppointmentStatus(@PathVariable Integer id, @RequestParam String status, RedirectAttributes redirectAttributes) {
        Appointment appointment = appointmentService.findById(id);
        if (appointment != null) {
            appointment.setStatus(status);
            appointmentService.update(appointment);
            redirectAttributes.addFlashAttribute("success", "预约状态更新成功");
        }
        return "redirect:/merchant/appointments";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Merchant merchant = (Merchant) session.getAttribute("merchant");
        if (merchant == null) {
            return "redirect:/login";
        }
        return "redirect:/merchant-profile.html";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute Merchant merchant, HttpSession session, RedirectAttributes redirectAttributes) {
        Merchant currentMerchant = (Merchant) session.getAttribute("merchant");
        merchant.setId(currentMerchant.getId());
        merchant.setPassword(currentMerchant.getPassword());
        merchant.setStatus(currentMerchant.getStatus());
        merchantService.update(merchant);
        session.setAttribute("merchant", merchant);
        redirectAttributes.addFlashAttribute("success", "商家信息更新成功");
        return "redirect:/merchant/profile";
    }
}
