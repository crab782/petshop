package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.dto.*;
import com.petshop.entity.User;
import com.petshop.entity.Merchant;
import com.petshop.exception.BadRequestException;
import com.petshop.exception.ResourceNotFoundException;
import com.petshop.mapper.MerchantMapper;
import com.petshop.mapper.UserMapper;
import com.petshop.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private UserMapper userRepository;

    @Autowired
    private MerchantMapper merchantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    private final Map<String, String> verifyCodeStore = new HashMap<>();
    private final Map<String, Long> verifyCodeExpiry = new HashMap<>();
    private static final long VERIFY_CODE_EXPIRY_MS = 5 * 60 * 1000;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String loginIdentifier = request.getLoginIdentifier();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, loginIdentifier);
        User user = userRepository.selectOne(wrapper);

        if (user == null) {
            throw new ResourceNotFoundException("User not found, please use phone number to login");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getPhone(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDTO userDTO = convertToDTO(user);
        return LoginResponse.builder()
                .token(jwt)
                .user(userDTO)
                .build();
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (request.getPhone() == null || request.getPhone().isEmpty()) {
            throw new BadRequestException("Phone number is required");
        }

        LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
        phoneWrapper.eq(User::getPhone, request.getPhone());
        if (userRepository.selectCount(phoneWrapper) > 0) {
            throw new BadRequestException("Phone number already registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail() != null ? request.getEmail() : "");
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.insert(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getPhone(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDTO userDTO = convertToDTO(user);
        return LoginResponse.builder()
                .token(jwt)
                .user(userDTO)
                .build();
    }

    @Transactional
    public Map<String, String> merchantRegister(MerchantRegisterRequest request) {
        LambdaQueryWrapper<Merchant> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(Merchant::getEmail, request.getEmail());
        if (merchantRepository.selectCount(emailWrapper) > 0) {
            throw new BadRequestException("Email already in use");
        }

        Merchant merchant = new Merchant();
        merchant.setName(request.getName());
        merchant.setEmail(request.getEmail());
        merchant.setPassword(passwordEncoder.encode(request.getPassword()));
        merchant.setPhone(request.getPhone());
        merchant.setContactPerson(request.getContact_person());
        merchant.setLogo(request.getLogo() != null ? request.getLogo() : "");
        merchant.setAddress(request.getAddress());
        merchant.setStatus("pending");
        merchant.setCreatedAt(LocalDateTime.now());
        merchant.setUpdatedAt(LocalDateTime.now());

        merchantRepository.insert(merchant);

        Map<String, String> result = new HashMap<>();
        result.put("message", "Merchant registration successful. Please wait for admin approval.");
        return result;
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }

    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("Not authenticated");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String phone = userDetails.getUsername();

        User user = userRepository.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        return convertToDTO(user);
    }

    @Transactional
    public UserDTO updateUserInfo(UserDTO userDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("Not authenticated");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String phone = userDetails.getUsername();

        User user = userRepository.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        if (userDTO.getUsername() != null && !userDTO.getUsername().isEmpty()) {
            user.setUsername(userDTO.getUsername());
        }

        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
            user.setEmail(userDTO.getEmail());
        }

        if (userDTO.getPhone() != null && !userDTO.getPhone().isEmpty()) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, userDTO.getPhone())
                   .ne(User::getId, user.getId());
            if (userRepository.selectCount(wrapper) > 0) {
                throw new BadRequestException("Phone number already in use");
            }
            user.setPhone(userDTO.getPhone());
        }

        if (userDTO.getAvatar() != null) {
            user.setAvatar(userDTO.getAvatar());
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.updateById(user);

        return convertToDTO(user);
    }

    @Transactional
    public Map<String, Object> changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("Not authenticated");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String phone = userDetails.getUsername();

        User user = userRepository.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "Old password is incorrect");
            return result;
        }

        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "New password must be at least 6 characters");
            return result;
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.updateById(user);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Password changed successfully");
        return result;
    }

    public void sendVerifyCode(SendVerifyCodeRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new BadRequestException("Email is required");
        }

        String verifyCode = generateVerifyCode();
        verifyCodeStore.put(request.getEmail(), verifyCode);
        verifyCodeExpiry.put(request.getEmail(), System.currentTimeMillis() + VERIFY_CODE_EXPIRY_MS);

        System.out.println("Verification code for " + request.getEmail() + ": " + verifyCode);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new BadRequestException("Email is required");
        }

        if (request.getVerifyCode() == null || request.getVerifyCode().isEmpty()) {
            throw new BadRequestException("Verification code is required");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters");
        }

        String storedCode = verifyCodeStore.get(request.getEmail());
        Long expiryTime = verifyCodeExpiry.get(request.getEmail());

        if (storedCode == null || !storedCode.equals(request.getVerifyCode())) {
            throw new BadRequestException("Invalid verification code");
        }

        if (expiryTime == null || System.currentTimeMillis() > expiryTime) {
            verifyCodeStore.remove(request.getEmail());
            verifyCodeExpiry.remove(request.getEmail());
            throw new BadRequestException("Verification code has expired");
        }

        User user = userRepository.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, request.getEmail()));
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.updateById(user);

        verifyCodeStore.remove(request.getEmail());
        verifyCodeExpiry.remove(request.getEmail());
    }

    private String generateVerifyCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .role("user")
                .build();
    }
}
