package com.rental.platform.service;

import com.rental.platform.dto.OrderRequest;
import com.rental.platform.exception.ResourceNotFoundException;
import com.rental.platform.exception.UnauthorizedException;
import com.rental.platform.model.Order;
import com.rental.platform.model.Product;
import com.rental.platform.model.User;
import com.rental.platform.repository.OrderRepository;
import com.rental.platform.repository.ProductRepository;
import com.rental.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SMSService smsService;
    
    @Autowired
    private NotificationService notificationService;

    // In createOrder method, after saving order, add:
    // Send SMS notification to owner
    

    // In updateOrderStatus method, after updating status, add:
    // Send SMS notification to user about status change
    
    public Order createOrder(OrderRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (!product.isApproved() || product.getStatus() != Product.ProductStatus.AVAILABLE) {
            throw new IllegalArgumentException("Product is not available for rent");
        }

        // Check for conflicting orders
        List<Order.OrderStatus> activeStatuses = Arrays.asList(
                Order.OrderStatus.APPROVED, Order.OrderStatus.ACTIVE);
        
        List<Order> conflictingOrders = orderRepository.findConflictingOrders(
                product, request.getStartDate(), request.getEndDate(), activeStatuses);
        
        if (!conflictingOrders.isEmpty()) {
            throw new IllegalArgumentException("Product is not available for the selected dates");
        }

        // Calculate total amount
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        BigDecimal totalAmount = product.getPricePerDay().multiply(BigDecimal.valueOf(days));

        Order order = new Order();
        order.setUser(user);
        order.setProduct(product);
        order.setStartDate(request.getStartDate());
        order.setEndDate(request.getEndDate());
        order.setTotalAmount(totalAmount);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setSpecialInstructions(request.getSpecialInstructions());

        order = orderRepository.save(order);
        notificationService.notifyOwnerNewOrder(order);

        
        String smsMessage = "New rental request for " + product.getName() + 
                " from " + order.getStartDate() + " to " + order.getEndDate() + 
                ". Total: Rs." + order.getTotalAmount() + 
                ". Login to approve/reject.";
smsService.sendOrderNotification(product.getOwner().getPhoneNumber(), smsMessage);

        // Update product status to booked
        product.setStatus(Product.ProductStatus.BOOKED);
        productRepository.save(product);

        // Send notification to owner
        emailService.sendOrderNotification(product.getOwner().getEmail(), order);

        return order;
    }

    public Order updateOrderStatus(Long orderId, Order.OrderStatus status, String ownerEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        if (!order.getProduct().getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedException("You can only update orders for your products");
        }

        Order.OrderStatus previousStatus = order.getStatus();
        order.setStatus(status);
        
        notificationService.notifyUserOrderStatusChange(order, previousStatus);

        
        String statusMessage = "Your rental order for " + order.getProduct().getName() + 
                " has been " + status.name().toLowerCase() + 
                ". Order ID: " + order.getId();
        	smsService.sendOrderNotification(order.getUser().getPhoneNumber(), statusMessage);


        // Update product status based on order status
        Product product = order.getProduct();
        switch (status) {
            case APPROVED:
                product.setStatus(Product.ProductStatus.BOOKED);
                break;
            case ACTIVE:
                product.setStatus(Product.ProductStatus.IN_USE);
                break;
            case COMPLETED:
            case REJECTED:
            case CANCELLED:
                product.setStatus(Product.ProductStatus.AVAILABLE);
                break;
        }
        
        productRepository.save(product);
        order = orderRepository.save(order);

        // Send notification to user
        emailService.sendOrderStatusUpdate(order.getUser().getEmail(), order, previousStatus);

        return order;
    }

    public Page<Order> getUserOrders(String userEmail, int page, int size) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findByUser(user, pageable);
    }

    public Page<Order> getOwnerOrders(String ownerEmail, int page, int size) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findByProductOwner(owner, pageable);
    }

    public Order getOrderById(Long orderId, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user is the order creator or product owner
        if (!order.getUser().getId().equals(user.getId()) && 
            !order.getProduct().getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("Access denied");
        }

        return order;
    }

    public Order cancelOrder(Long orderId, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can only cancel your own orders");
        }

        if (order.getStatus() == Order.OrderStatus.ACTIVE || 
            order.getStatus() == Order.OrderStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot cancel active or completed orders");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        
        // Make product available again
        Product product = order.getProduct();
        product.setStatus(Product.ProductStatus.AVAILABLE);
        productRepository.save(product);

        return orderRepository.save(order);
    }
}