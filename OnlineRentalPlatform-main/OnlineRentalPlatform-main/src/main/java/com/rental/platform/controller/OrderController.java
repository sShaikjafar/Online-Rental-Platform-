package com.rental.platform.controller;

import com.rental.platform.dto.ApiResponse;
import com.rental.platform.dto.OrderRequest;
import com.rental.platform.model.Order;
import com.rental.platform.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {
    
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(@Valid @RequestBody OrderRequest request,
                                                        Authentication authentication) {
        try {
            Order order = orderService.createOrder(request, authentication.getName());
            return ResponseEntity.ok(ApiResponse.success("Order created successfully", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<Page<Order>>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        try {
            Page<Order> orders = orderService.getUserOrders(authentication.getName(), page, size);
            return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-product-orders")
    public ResponseEntity<ApiResponse<Page<Order>>> getMyProductOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        try {
            Page<Order> orders = orderService.getOwnerOrders(authentication.getName(), page, size);
            return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable Long id,
                                                     Authentication authentication) {
        try {
            Order order = orderService.getOrderById(id, authentication.getName());
            return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(@PathVariable Long id,
                                                              @RequestParam Order.OrderStatus status,
                                                              Authentication authentication) {
        try {
            Order order = orderService.updateOrderStatus(id, status, authentication.getName());
            return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(@PathVariable Long id,
                                                        Authentication authentication) {
        try {
            Order order = orderService.cancelOrder(id, authentication.getName());
            return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}