package com.rental.platform.service;

import com.rental.platform.controller.WebSocketController;
import com.rental.platform.model.Order;
import com.rental.platform.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private WebSocketController webSocketController;

    public void notifyOwnerNewOrder(Order order) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", order.getId());
        data.put("productName", order.getProduct().getName());
        data.put("userName", order.getUser().getName());
        data.put("userPhone", order.getUser().getPhoneNumber());
        data.put("startDate", order.getStartDate());
        data.put("endDate", order.getEndDate());
        data.put("totalAmount", order.getTotalAmount());
        data.put("message", "New rental request for your product: " + order.getProduct().getName());

        webSocketController.sendNotificationToUser(
            order.getProduct().getOwner().getEmail(),
            "NEW_ORDER",
            data
        );
    }

    public void notifyUserOrderStatusChange(Order order, Order.OrderStatus previousStatus) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", order.getId());
        data.put("productName", order.getProduct().getName());
        data.put("previousStatus", previousStatus.name());
        data.put("newStatus", order.getStatus().name());
        data.put("message", "Your order status has been updated to: " + order.getStatus().name());

        webSocketController.sendNotificationToUser(
            order.getUser().getEmail(),
            "ORDER_STATUS_CHANGED",
            data
        );
    }

    public void notifyProductApproval(Product product, boolean approved) {
        Map<String, Object> data = new HashMap<>();
        data.put("productId", product.getId());
        data.put("productName", product.getName());
        data.put("approved", approved);
        data.put("message", approved ? 
            "Your product has been approved and is now live!" : 
            "Your product has been rejected. Please review and resubmit.");

        webSocketController.sendNotificationToUser(
            product.getOwner().getEmail(),
            "PRODUCT_APPROVAL",
            data
        );
    }
}