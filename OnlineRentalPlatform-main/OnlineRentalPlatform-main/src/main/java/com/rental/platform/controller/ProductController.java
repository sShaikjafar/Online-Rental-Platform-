package com.rental.platform.controller;

import com.rental.platform.dto.ApiResponse;
import com.rental.platform.dto.ProductDTO;
import java.util.stream.Collectors;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.rental.platform.dto.ProductRequest;
import com.rental.platform.exception.ResourceNotFoundException;
import com.rental.platform.model.Category;
import com.rental.platform.model.Product;
import com.rental.platform.repository.CategoryRepository;
import com.rental.platform.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@Valid @RequestBody ProductRequest request,
                                                            Authentication authentication) {
        try {
            Product product = productService.createProduct(request, authentication.getName());
            return ResponseEntity.ok(ApiResponse.success("Product created successfully", product));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Error creating product: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to create product: " + e.getMessage()));
        }
    }
    
    
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(defaultValue = "10") Double distance) {
        
        try {
            System.out.println("Getting products with params - Page: " + page + ", Size: " + size + 
                             ", CategoryId: " + categoryId + ", Keyword: " + keyword);
            
            Page<Product> productsPage = productService.getProducts(
                    page, size, sortBy, sortDir, categoryId, minPrice, maxPrice,
                    keyword, lat, lon, distance);
            
            System.out.println("Found " + productsPage.getContent().size() + " products");
            
            // Convert to DTO to avoid serialization issues
            List<ProductDTO> productDTOs = productsPage.getContent().stream()
                    .map(ProductDTO::new)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", productDTOs);
            response.put("totalPages", productsPage.getTotalPages());
            response.put("totalElements", productsPage.getTotalElements());
            response.put("size", productsPage.getSize());
            response.put("number", productsPage.getNumber());
            
            return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", response));
        } catch (Exception e) {
            System.err.println("Error in getProducts: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to load products: " + e.getMessage()));
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(ApiResponse.success("Product retrieved successfully", product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-products")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).body(ApiResponse.error("Authentication required"));
            }
            
            System.out.println("Getting products for user: " + authentication.getName());
            Page<Product> productsPage = productService.getOwnerProducts(authentication.getName(), page, size);
            
            // Convert to DTO to avoid serialization issues
            List<ProductDTO> productDTOs = productsPage.getContent().stream()
                    .map(ProductDTO::new)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", productDTOs);
            response.put("totalPages", productsPage.getTotalPages());
            response.put("totalElements", productsPage.getTotalElements());
            response.put("size", productsPage.getSize());
            response.put("number", productsPage.getNumber());
            
            System.out.println("Returning " + productDTOs.size() + " products as DTOs");
            
            return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", response));
        } catch (Exception e) {
            System.err.println("Error in getMyProducts: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error("Failed to load products: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id,
                                                            @Valid @RequestBody ProductRequest request,
                                                            Authentication authentication) {
        try {
            Product product = productService.updateProduct(id, request, authentication.getName());
            return ResponseEntity.ok(ApiResponse.success("Product updated successfully", product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Product>> updateProductStatus(@PathVariable Long id,
                                                                  @RequestParam Product.ProductStatus status,
                                                                  Authentication authentication) {
        try {
            Product product = productService.updateProductStatus(id, status, authentication.getName());
            return ResponseEntity.ok(ApiResponse.success("Product status updated successfully", product));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id,
                                                           Authentication authentication) {
        try {
            productService.deleteProduct(id, authentication.getName());
            return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Category>>> getCategories() {
        try {
            List<Category> categories = categoryRepository.findByActiveTrue();
            return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}