package com.petshop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petshop.dto.*;
import com.petshop.entity.User;
import com.petshop.entity.Merchant;
import com.petshop.entity.Admin;
import com.petshop.exception.BadRequestException;
import com.petshop.exception.ResourceNotFoundException;
import com.petshop.mapper.AdminMapper;
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
    private AdminMapper adminRepository;

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
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginIdentifier, request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.selectByPhone(userDetails.getUsername());
        if (user == null) {
            user = userRepository.selectByEmail(userDetails.getUsername());
        }
        
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

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
        user.setUsername(request.getUsername() != null && !request.getUsername().isEmpty() ? request.getUsername() : request.getPhone());
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
        LambdaQueryWrapper<Merchant> phoneWrapper = new LambdaQueryWrapper<>();
        phoneWrapper.eq(Merchant::getPhone, request.getPhone());
        if (merchantRepository.selectCount(phoneWrapper) > 0) {
            throw new BadRequestException("Phone number already registered");
        }

        Merchant merchant = new Merchant();
        merchant.setName(request.getName() != null && !request.getName().isEmpty() ? request.getName() : "商家" + request.getPhone());
        merchant.setEmail(request.getEmail() != null && !request.getEmail().isEmpty() ? request.getEmail() : "");
        merchant.setPassword(passwordEncoder.encode(request.getPassword()));
        merchant.setPhone(request.getPhone());
        merchant.setContactPerson(request.getContact_person() != null ? request.getContact_person() : "");
        merchant.setLogo(request.getLogo() != null ? request.getLogo() : "");
        merchant.setAddress(request.getAddress() != null ? request.getAddress() : "");
        merchant.setStatus("pending");
        merchant.setCreatedAt(LocalDateTime.now());
        merchant.setUpdatedAt(LocalDateTime.now());

        merchantRepository.insert(merchant);

        Map<String, String> result = new HashMap<>();
        result.put("message", "Merchant registration successful. Please wait for admin approval.");
        return result;
    }

    @Transactional
    public LoginResponse merchantLogin(LoginRequest request) {
        String loginIdentifier = request.getLoginIdentifier();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginIdentifier, request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Merchant merchant = merchantRepository.selectByPhone(userDetails.getUsername());
        if (merchant == null) {
            merchant = merchantRepository.selectByEmail(userDetails.getUsername());
        }

        if (merchant == null) {
            throw new ResourceNotFoundException("Merchant not found");
        }

        if (!"approved".equals(merchant.getStatus())) {
            throw new BadRequestException("Merchant account is not approved. Current status: " + merchant.getStatus());
        }

        UserDTO userDTO = UserDTO.builder()
                .id(merchant.getId())
                .username(merchant.getName())
                .email(merchant.getEmail())
                .phone(merchant.getPhone())
                .avatar(merchant.getLogo())
                .role("merchant")
                .build();

        return LoginResponse.builder()
                .token(jwt)
                .user(userDTO)
                .build();
    }

    @Transactional
    public LoginResponse adminLogin(LoginRequest request) {
        String loginIdentifier = request.getLoginIdentifier();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginIdentifier, request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Admin admin = adminRepository.selectByUsername(userDetails.getUsername());

        if (admin == null) {
            throw new ResourceNotFoundException("Admin not found");
        }

        UserDTO userDTO = UserDTO.builder()
                .id(admin.getId())
                .username(admin.getUsername())
                .role("admin")
                .build();

        return LoginResponse.builder()
                .token(jwt)
                .user(userDTO)
                .build();
    }

    @Transactional
    public Map<String, String> adminRegister(AdminRegisterRequest request) {
        String username = request.getUsername();
        if (username == null || username.isEmpty()) {
            username = request.getPhone();
        }
        
        if (username == null || username.isEmpty()) {
            throw new BadRequestException("Username or phone is required");
        }

        Admin existingAdmin = adminRepository.selectByUsername(username);
        if (existingAdmin != null) {
            throw new BadRequestException("Username already exists");
        }

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        adminRepository.insert(admin);

        Map<String, String> result = new HashMap<>();
        result.put("message", "Admin registration successful");
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
