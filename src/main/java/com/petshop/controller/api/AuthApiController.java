package com.petshop.controller.api;

import com.petshop.dto.*;
import com.petshop.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthApiController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "用户登录", description = "使用手机号和密码登录")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        try {
            String loginIdentifier = request.getLoginIdentifier();
            if (loginIdentifier == null || loginIdentifier.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "Phone number is required"));
            }
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "Password is required"));
            }

            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Login failed: " + e.getMessage()));
        }
    }

    @Operation(summary = "用户注册", description = "注册新用户账号，手机号必填且唯一，用户名和邮箱可选")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@RequestBody RegisterRequest request) {
        try {
            if (request.getPhone() == null || request.getPhone().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "Phone number is required"));
            }
            if (request.getPassword() == null || request.getPassword().length() < 6) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "Password must be at least 6 characters"));
            }

            LoginResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Registration successful", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Registration failed: " + e.getMessage()));
        }
    }

    @Operation(summary = "用户登出", description = "退出登录")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> logout() {
        try {
            authService.logout();
            Map<String, Boolean> result = new HashMap<>();
            result.put("success", true);
            return ResponseEntity.ok(ApiResponse.success("Logout successful", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Logout failed: " + e.getMessage()));
        }
    }

    @Operation(summary = "获取当前用户信息", description = "获取已登录用户的详细信息")
    @GetMapping("/userinfo")
    public ResponseEntity<ApiResponse<UserDTO>> getUserInfo() {
        try {
            UserDTO user = authService.getCurrentUser();
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to get user info: " + e.getMessage()));
        }
    }

    @Operation(summary = "更新用户信息", description = "更新当前用户的个人信息")
    @PutMapping("/userinfo")
    public ResponseEntity<ApiResponse<UserDTO>> updateUserInfo(@RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = authService.updateUserInfo(userDTO);
            return ResponseEntity.ok(ApiResponse.success("User info updated successfully", updatedUser));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to update user info: " + e.getMessage()));
        }
    }

    @Operation(summary = "修改密码", description = "修改当前用户的密码")
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Map<String, Object>>> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            if (request.getOldPassword() == null || request.getOldPassword().isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "Old password is required");
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "Old password is required"));
            }
            if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "New password must be at least 6 characters");
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "New password must be at least 6 characters"));
            }

            Map<String, Object> result = authService.changePassword(request);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(ApiResponse.success("Password changed successfully", result));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, (String) result.get("message")));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to change password: " + e.getMessage()));
        }
    }

    @Operation(summary = "发送验证码", description = "发送邮箱验证码用于密码重置")
    @PostMapping("/sendVerifyCode")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> sendVerifyCode(@RequestBody SendVerifyCodeRequest request) {
        try {
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "Email is required"));
            }

            authService.sendVerifyCode(request);
            
            Map<String, Boolean> result = new HashMap<>();
            result.put("success", true);
            return ResponseEntity.ok(ApiResponse.success("Verification code sent successfully", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to send verification code: " + e.getMessage()));
        }
    }

    @Operation(summary = "重置密码", description = "使用验证码重置密码")
    @PostMapping("/resetPassword")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "Email is required"));
            }
            if (request.getVerifyCode() == null || request.getVerifyCode().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "Verification code is required"));
            }
            if (request.getPassword() == null || request.getPassword().length() < 6) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "Password must be at least 6 characters"));
            }

            authService.resetPassword(request);
            
            Map<String, Boolean> result = new HashMap<>();
            result.put("success", true);
            return ResponseEntity.ok(ApiResponse.success("Password reset successfully", result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Failed to reset password: " + e.getMessage()));
        }
    }

    @Operation(summary = "商家注册", description = "注册新商家账号，手机号必填且唯一，邮箱可选")
    @PostMapping("/merchant/register")
    public ResponseEntity<ApiResponse<Map<String, String>>> merchantRegister(@RequestBody MerchantRegisterRequest request) {
        try {
            if (request.getPhone() == null || request.getPhone().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "Phone is required"));
            }
            if (request.getPassword() == null || request.getPassword().length() < 6) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(400, "Password must be at least 6 characters"));
            }

            Map<String, String> response = authService.merchantRegister(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Merchant registration successful", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Merchant registration failed: " + e.getMessage()));
        }
    }
}
