package com.rental.platform.service;

import com.rental.platform.exception.ResourceNotFoundException;
import com.rental.platform.model.User;
import com.rental.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User updateProfile(String email, User updatedUser) {
        User user = getUserByEmail(email);
        
        user.setName(updatedUser.getName());
        user.setAddress(updatedUser.getAddress());
        user.setGender(updatedUser.getGender());
        user.setAge(updatedUser.getAge());
        
        return userRepository.save(user);
    }

    public User changePassword(String email, String oldPassword, String newPassword) {
        User user = getUserByEmail(email);
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid old password");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    // Admin methods
    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.findAll(pageable);
    }

    public User toggleUserStatus(Long userId) {
        User user = getUserById(userId);
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user);
    }
    
 // Add this method to UserService class
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}