package com.petshop.controller;

import com.petshop.entity.User;
import com.petshop.entity.Pet;
import com.petshop.entity.Appointment;
import com.petshop.service.UserService;
import com.petshop.service.PetService;
import com.petshop.service.AppointmentService;
import com.petshop.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PetService petService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private ServiceService serviceService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        return "redirect:/user-dashboard.html";
    }

    @GetMapping("/pets")
    public String pets(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        return "redirect:/user-pets.html";
    }

    @GetMapping("/pets/add")
    public String addPetForm(Model model) {
        return "redirect:/user-add-pet.html";
    }

    @PostMapping("/pets/add")
    public String addPet(@ModelAttribute Pet pet, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        pet.setUser(user);
        petService.create(pet);
        redirectAttributes.addFlashAttribute("success", "宠物添加成功");
        return "redirect:/user/pets";
    }

    @GetMapping("/appointments")
    public String appointments(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        return "redirect:/user-appointments.html";
    }

    @GetMapping("/appointments/book")
    public String bookAppointmentForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        return "redirect:/user-book-appointment.html";
    }

    @PostMapping("/appointments/book")
    public String bookAppointment(@ModelAttribute Appointment appointment, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        appointment.setUser(user);
        appointmentService.create(appointment);
        redirectAttributes.addFlashAttribute("success", "预约成功");
        return "redirect:/user/appointments";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        return "redirect:/user-profile.html";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User user, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");
        user.setId(currentUser.getId());
        user.setPassword(currentUser.getPassword());
        userService.update(user);
        session.setAttribute("user", user);
        redirectAttributes.addFlashAttribute("success", "个人信息更新成功");
        return "redirect:/user/profile";
    }
}
