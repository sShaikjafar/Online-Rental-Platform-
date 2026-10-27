package com.rental.platform.controller;

import com.rental.platform.dto.ApiResponse;
import com.rental.platform.model.Category;
import com.rental.platform.model.Product;
import com.rental.platform.model.User;
import com.rental.platform.repository.CategoryRepository;
import com.rental.platform.service.ProductService;
import com.rental.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/pending-products")
    public ResponseEntity<ApiResponse<Page<Product>>> getPendingProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Page<Product> products = productService.getPendingProducts(page, size);
            return ResponseEntity.ok(ApiResponse.success("Pending products retrieved", products));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/products/{id}/approve")
    public ResponseEntity<ApiResponse<Product>> approveProduct(@PathVariable Long id) {
        try {
            Product product = productService.approveProduct(id);
            return ResponseEntity.ok(ApiResponse.success("Product approved successfully", product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/products/{id}/reject")
    public ResponseEntity<ApiResponse<Product>> rejectProduct(@PathVariable Long id) {
        try {
            Product product = productService.rejectProduct(id);
            return ResponseEntity.ok(ApiResponse.success("Product rejected successfully", product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<User>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Page<User> users = userService.getAllUsers(page, size);
            return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<ApiResponse<User>> toggleUserStatus(@PathVariable Long id) {
        try {
            User user = userService.toggleUserStatus(id);
            return ResponseEntity.ok(ApiResponse.success("User status updated successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody Category category) {
        try {
            if (categoryRepository.existsByName(category.getName())) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Category already exists"));
            }
            
            Category savedCategory = categoryRepository.save(category);
            return ResponseEntity.ok(ApiResponse.success("Category created successfully", savedCategory));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable Long id,
                                                              @RequestBody Category categoryRequest) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            
            category.setName(categoryRequest.getName());
            category.setDescription(categoryRequest.getDescription());
            category.setActive(categoryRequest.isActive());
            
            Category updatedCategory = categoryRepository.save(category);
            return ResponseEntity.ok(ApiResponse.success("Category updated successfully", updatedCategory));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}