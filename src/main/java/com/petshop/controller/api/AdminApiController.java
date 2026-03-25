package com.petshop.controller.api;

import com.petshop.entity.User;
import com.petshop.entity.Merchant;
import com.petshop.service.UserService;
import com.petshop.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {
    @Autowired
    private UserService userService;
    @Autowired
    private MerchantService merchantService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/merchants")
    public ResponseEntity<List<Merchant>> getMerchants(HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Merchant> merchants = merchantService.findAll();
        return ResponseEntity.ok(merchants);
    }

    @PutMapping("/merchants/{id}/status")
    public ResponseEntity<Merchant> updateMerchantStatus(@PathVariable Integer id, @RequestParam String status, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Merchant merchant = merchantService.findById(id);
        if (merchant == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        merchant.setStatus(status);
        Merchant updatedMerchant = merchantService.update(merchant);
        return ResponseEntity.ok(updatedMerchant);
    }

    @DeleteMapping("/merchants/{id}")
    public ResponseEntity<Void> deleteMerchant(@PathVariable Integer id, HttpSession session) {
        if (session.getAttribute("admin") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        merchantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
