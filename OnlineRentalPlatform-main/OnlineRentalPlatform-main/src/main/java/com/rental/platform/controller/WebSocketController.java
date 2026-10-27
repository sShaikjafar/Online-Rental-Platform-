package com.rental.platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/user.connect")
    public void userConnect(@Payload Map<String, String> payload, Principal principal) {
        System.out.println("User connected: " + principal.getName());
    }

    public void sendNotificationToUser(String userEmail, String type, Object data) {
        try {
            Map<String, Object> notification = Map.of(
                "type", type,
                "data", data,
                "timestamp", System.currentTimeMillis()
            );
            
            messagingTemplate.convertAndSendToUser(
                userEmail, "/queue/notifications", notification);
            
            System.out.println("Notification sent to user: " + userEmail + ", Type: " + type);
        } catch (Exception e) {
            System.err.println("Failed to send WebSocket notification: " + e.getMessage());
        }
    }
}