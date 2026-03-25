package com.petshop.controller;

import com.petshop.entity.User;
import com.petshop.entity.Merchant;
import com.petshop.service.UserService;
import com.petshop.service.MerchantService;
import com.petshop.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@Tag(name = "认证管理", description = "用户、商家、管理员登录注册接口")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private AdminService adminService;

    @Operation(summary = "用户注册", description = "普通用户注册接口")
    @PostMapping("/register/user")
    public String registerUser(@RequestParam String username, @RequestParam String email, @RequestParam String password, @RequestParam String confirmPassword, @RequestParam String phone, RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "两次输入的密码不一致");
            return "redirect:/register";
        }
        if (userService.findByEmail(email) != null) {
            redirectAttributes.addFlashAttribute("error", "邮箱已被注册");
            return "redirect:/register";
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        userService.register(user);
        redirectAttributes.addFlashAttribute("success", "注册成功，请登录");
        return "redirect:/login";
    }

    @Operation(summary = "商家注册", description = "商家注册接口，需要等待审核")
    @PostMapping("/register/merchant")
    public String registerMerchant(@RequestParam String name, @RequestParam String contactPerson, @RequestParam String phone, @RequestParam String email, @RequestParam String password, @RequestParam String confirmPassword, @RequestParam String address, RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "两次输入的密码不一致");
            return "redirect:/register";
        }
        if (merchantService.findByEmail(email) != null) {
            redirectAttributes.addFlashAttribute("error", "邮箱已被注册");
            return "redirect:/register";
        }
        Merchant merchant = new Merchant();
        merchant.setName(name);
        merchant.setContactPerson(contactPerson);
        merchant.setPhone(phone);
        merchant.setEmail(email);
        merchant.setPassword(password);
        merchant.setAddress(address);
        merchantService.register(merchant);
        redirectAttributes.addFlashAttribute("success", "注册成功，等待审核");
        return "redirect:/login";
    }

    @Operation(summary = "用户登录", description = "支持用户、商家、管理员三种角色登录")
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, @RequestParam String type, HttpSession session, RedirectAttributes redirectAttributes) {
        if ("user".equals(type)) {
            User user = userService.login(email, password);
            if (user != null) {
                session.setAttribute("user", user);
                session.setAttribute("role", "ROLE_user");
                return "redirect:/user/dashboard";
            }
        } else if ("merchant".equals(type)) {
            Merchant merchant = merchantService.login(email, password);
            if (merchant != null) {
                session.setAttribute("merchant", merchant);
                session.setAttribute("role", "ROLE_merchant");
                return "redirect:/merchant/dashboard";
            }
        } else if ("admin".equals(type)) {
            if (adminService.login(email, password) != null) {
                session.setAttribute("admin", email);
                session.setAttribute("role", "ROLE_admin");
                return "redirect:/admin/dashboard";
            }
        }
        redirectAttributes.addFlashAttribute("error", "邮箱或密码错误");
        return "redirect:/login";
    }

    @Operation(summary = "退出登录", description = "用户退出登录，清除会话")
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
