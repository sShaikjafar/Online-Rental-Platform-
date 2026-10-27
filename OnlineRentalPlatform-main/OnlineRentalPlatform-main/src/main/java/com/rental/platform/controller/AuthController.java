package com.rental.platform.controller;

import com.rental.platform.dto.ApiResponse;
import com.rental.platform.dto.LoginRequest;
import com.rental.platform.dto.RegisterRequest;
import com.rental.platform.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            Map<String, Object> result = authService.register(request);
            return ResponseEntity.ok(ApiResponse.success("Registration successful", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Map<String, Object> result = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("Login successful", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/verify-email-otp")
    public ResponseEntity<ApiResponse<String>> verifyEmailOTP(@RequestParam String otp, @RequestParam Long userId) {
        try {
            authService.verifyEmailOTP(otp, userId);
            return ResponseEntity.ok(ApiResponse.success("Email verified successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/resend-email-otp")
    public ResponseEntity<ApiResponse<String>> resendEmailOTP(@RequestParam Long userId) {
        try {
            authService.resendEmailOTP(userId);
            return ResponseEntity.ok(ApiResponse.success("Email OTP sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/verify-phone")
    public ResponseEntity<ApiResponse<String>> verifyPhone(@RequestParam String otp, @RequestParam Long userId) {
        try {
            authService.verifyPhone(otp, userId);
            return ResponseEntity.ok(ApiResponse.success("Phone verified successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/resend-phone-otp")
    public ResponseEntity<ApiResponse<String>> resendPhoneOTP(@RequestParam Long userId) {
        try {
            authService.resendPhoneOTP(userId);
            return ResponseEntity.ok(ApiResponse.success("OTP sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}