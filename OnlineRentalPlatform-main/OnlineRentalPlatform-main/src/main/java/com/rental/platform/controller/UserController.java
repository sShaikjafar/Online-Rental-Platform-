package com.rental.platform.controller;

import com.rental.platform.dto.ApiResponse;
import com.rental.platform.model.User;
import com.rental.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getProfile(Authentication authentication) {
        try {
            User user = userService.getUserByEmail(authentication.getName());
            return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<User>> updateProfile(@RequestBody User updatedUser,
                                                         Authentication authentication) {
        try {
            User user = userService.updateProfile(authentication.getName(), updatedUser);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody Map<String, String> passwords,
                                                            Authentication authentication) {
        try {
            String oldPassword = passwords.get("oldPassword");
            String newPassword = passwords.get("newPassword");
            
            userService.changePassword(authentication.getName(), oldPassword, newPassword);
            return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}