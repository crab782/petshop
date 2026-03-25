package com.petshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String index() {
        return "redirect:/index.html";
    }
    
    @GetMapping("/login")
    public String login() {
        return "redirect:/login.html";
    }
    
    @GetMapping("/register")
    public String register() {
        return "redirect:/register.html";
    }
    
    @GetMapping("/admin/login")
    public String adminLogin() {
        return "redirect:/admin-login.html";
    }
}