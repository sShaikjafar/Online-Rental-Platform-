package com.rental.platform.service;

import com.rental.platform.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendVerificationEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Email Verification - Rental Platform");
        message.setText("Please click the following link to verify your email: " +
                       "http://localhost:3000/verify-email?token=" + token);
        
        mailSender.send(message);
    }
    
    @Async
    public void sendEmailOTP(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Email Verification OTP - Rental Platform");
        message.setText("Dear User,\n\n" +
                       "Your email verification OTP for Rental Platform is: " + otp + "\n\n" +
                       "This OTP will expire in 10 minutes.\n" +
                       "Please do not share this OTP with anyone.\n\n" +
                       "If you didn't request this verification, please ignore this email.\n\n" +
                       "Best regards,\n" +
                       "Rental Platform Team");
        
        mailSender.send(message);
    }

    @Async
    public void sendOrderNotification(String ownerEmail, Order order) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(ownerEmail);
        message.setSubject("New Rental Request - Rental Platform");
        message.setText("You have received a new rental request for your product: " +
                       order.getProduct().getName() + 
                       "\nFrom: " + order.getStartDate() + " to " + order.getEndDate() +
                       "\nTotal Amount: $" + order.getTotalAmount() +
                       "\nPlease login to your dashboard to approve or reject this request.");
        
        mailSender.send(message);
    }

    @Async
    public void sendOrderStatusUpdate(String userEmail, Order order, Order.OrderStatus previousStatus) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("Order Status Update - Rental Platform");
        message.setText("Your order for " + order.getProduct().getName() + 
                       " has been updated from " + previousStatus + " to " + order.getStatus() + 
                       "\nOrder ID: " + order.getId() +
                       "\nTotal Amount: $" + order.getTotalAmount());
        
        mailSender.send(message);
    }
}