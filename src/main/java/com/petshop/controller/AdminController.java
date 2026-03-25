package com.petshop.controller;

import com.petshop.entity.User;
import com.petshop.entity.Merchant;
import com.petshop.service.UserService;
import com.petshop.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private MerchantService merchantService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/login";
        }
        return "redirect:/admin-dashboard.html";
    }

    @GetMapping("/users")
    public String users(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/login";
        }
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin-users";
    }

    @GetMapping("/merchants")
    public String merchants(HttpSession session, Model model) {
        if (session.getAttribute("admin") == null) {
            return "redirect:/login";
        }
        List<Merchant> merchants = merchantService.findAll();
        model.addAttribute("merchants", merchants);
        return "admin-merchants";
    }

    @PostMapping("/merchants/{id}/status")
    public String updateMerchantStatus(@PathVariable Integer id, @RequestParam String status, RedirectAttributes redirectAttributes) {
        Merchant merchant = merchantService.findById(id);
        if (merchant != null) {
            merchant.setStatus(status);
            merchantService.update(merchant);
            redirectAttributes.addFlashAttribute("success", "商家状态更新成功");
        }
        return "redirect:/admin/merchants";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        userService.delete(id);
        redirectAttributes.addFlashAttribute("success", "用户删除成功");
        return "redirect:/admin/users";
    }

    @PostMapping("/merchants/{id}/delete")
    public String deleteMerchant(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        merchantService.delete(id);
        redirectAttributes.addFlashAttribute("success", "商家删除成功");
        return "redirect:/admin/merchants";
    }
}
