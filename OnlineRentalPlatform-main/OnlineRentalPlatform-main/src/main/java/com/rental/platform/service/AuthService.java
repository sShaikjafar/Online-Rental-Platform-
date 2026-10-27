package com.rental.platform.service;

import com.rental.platform.dto.LoginRequest;
import com.rental.platform.dto.RegisterRequest;
import com.rental.platform.exception.ResourceNotFoundException;
import com.rental.platform.exception.UnauthorizedException;
import com.rental.platform.model.User;
import com.rental.platform.model.VerificationToken;
import com.rental.platform.repository.UserRepository;
import com.rental.platform.repository.VerificationTokenRepository;
import com.rental.platform.util.JwtUtil;
import com.rental.platform.util.OTPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VerificationTokenRepository tokenRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SMSService smsService;

    public Map<String, Object> register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAddress(request.getAddress());
        user.setGender(request.getGender());
        user.setAge(request.getAge());
        
        if ("OWNER".equals(request.getRole())) {
            user.setRole(User.Role.OWNER);
        } else {
            user.setRole(User.Role.USER);
        }

        user = userRepository.save(user);

        // Generate OTPs for both email and phone
        String emailOTP = OTPUtil.generateOTP();
        String phoneOTP = OTPUtil.generateOTP();

        // Save email verification token with OTP
        VerificationToken emailVerificationToken = new VerificationToken(
                null, emailOTP, user, VerificationToken.TokenType.EMAIL_VERIFICATION,
                LocalDateTime.now().plusMinutes(10));
        tokenRepository.save(emailVerificationToken);

        // Save phone verification token
        VerificationToken phoneVerificationToken = new VerificationToken(
                null, phoneOTP, user, VerificationToken.TokenType.PHONE_VERIFICATION,
                LocalDateTime.now().plusMinutes(10));
        tokenRepository.save(phoneVerificationToken);

        // Send OTPs via email and SMS
        emailService.sendEmailOTP(user.getEmail(), emailOTP);
        smsService.sendOTP(user.getPhoneNumber(), phoneOTP);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("email", user.getEmail());
        response.put("message", "Registration successful. Please verify your email and phone with OTP.");
        
        return response;
    }
    public Map<String, Object> login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = (User) authentication.getPrincipal();

            if (!user.isEmailVerified()) {
                throw new UnauthorizedException("Please verify your email first");
            }

            String token = jwtUtil.generateToken(user.getEmail(), user);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", getUserInfo(user));

            return response;
        } catch (Exception e) {
            e.printStackTrace(); // 👈 so you see the real cause in logs
            throw new UnauthorizedException("Invalid credentials: " + e.getMessage());
        }
    }


    public boolean verifyEmailOTP(String otp, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        VerificationToken verificationToken = tokenRepository
                .findByOtpAndUserAndUsedFalse(otp, user)
                .orElse(null);

        if (verificationToken == null || 
            verificationToken.getTokenType() != VerificationToken.TokenType.EMAIL_VERIFICATION) {
            throw new IllegalArgumentException("Invalid email OTP");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Email OTP expired");
        }

        user.setEmailVerified(true);
        userRepository.save(user);

        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        return true;
    }

    public boolean verifyPhone(String otp, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        VerificationToken verificationToken = tokenRepository
                .findByOtpAndUserAndUsedFalse(otp, user)
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP expired");
        }

        user.setPhoneVerified(true);
        userRepository.save(user);

        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        return true;
    }

    public void resendEmailOTP(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Email already verified");
        }

        // Delete old unused tokens
        tokenRepository.deleteByUserAndTokenType(user, VerificationToken.TokenType.EMAIL_VERIFICATION);

        String emailOTP = OTPUtil.generateOTP();
        VerificationToken verificationToken = new VerificationToken(
                null, emailOTP, user, VerificationToken.TokenType.EMAIL_VERIFICATION,
                LocalDateTime.now().plusMinutes(10));
        tokenRepository.save(verificationToken);

        emailService.sendEmailOTP(user.getEmail(), emailOTP);
    }

    public void resendPhoneOTP(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isPhoneVerified()) {
            throw new IllegalArgumentException("Phone already verified");
        }

        String otp = OTPUtil.generateOTP();
        VerificationToken verificationToken = new VerificationToken(
                null, otp, user, VerificationToken.TokenType.PHONE_VERIFICATION,
                LocalDateTime.now().plusMinutes(10));
        tokenRepository.save(verificationToken);

        smsService.sendOTP(user.getPhoneNumber(), otp);
    }

    private Map<String, Object> getUserInfo(User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getName());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole().name());
        userInfo.put("emailVerified", user.isEmailVerified());
        userInfo.put("phoneVerified", user.isPhoneVerified());
        userInfo.put("address", user.getAddress());
        userInfo.put("phoneNumber", user.getPhoneNumber());
        return userInfo;
    }
}