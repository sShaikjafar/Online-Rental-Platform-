package com.rental.platform.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class SMSService {
    
    @Value("${twilio.account.sid}")
    private String accountSid;
    
    @Value("${twilio.auth.token}")
    private String authToken;
    
    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;

    @PostConstruct
    public void initTwilio() {
        Twilio.init(accountSid, authToken);
    }
    
    @Async
    public void sendOTP(String phoneNumber, String otp) {
        try {
            // Format phone number for Indian numbers
            String formattedNumber = formatPhoneNumber(phoneNumber);
            
            String messageBody = "Your OTP for Rental Platform verification is: " + otp + 
                               "\n\nThis OTP will expire in 10 minutes." +
                               "\nDo not share this OTP with anyone.";
            
            Message message = Message.creator(
                    new PhoneNumber(formattedNumber),
                    new PhoneNumber(fromPhoneNumber),
                    messageBody)
                    .create();
            
            System.out.println("SMS sent successfully! SID: " + message.getSid());
            System.out.println("OTP " + otp + " sent to " + formattedNumber);
            
        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback to console logging for development
            System.out.println("FALLBACK - OTP for " + phoneNumber + ": " + otp);
        }
    }
    
    @Async
    public void sendOrderNotification(String phoneNumber, String message) {
        try {
            String formattedNumber = formatPhoneNumber(phoneNumber);
            
            Message twilioMessage = Message.creator(
                    new PhoneNumber(formattedNumber),
                    new PhoneNumber(fromPhoneNumber),
                    message)
                    .create();
            
            System.out.println("Order notification SMS sent! SID: " + twilioMessage.getSid());
            
        } catch (Exception e) {
            System.err.println("Failed to send order notification SMS: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String formatPhoneNumber(String phoneNumber) {
        // Remove any non-digit characters
        String cleaned = phoneNumber.replaceAll("[^0-9]", "");
        
        // Handle Indian phone numbers
        if (cleaned.length() == 10) {
            return "+91" + cleaned;  // Add India country code
        }
        
        // If already has country code
        if (cleaned.length() == 12 && cleaned.startsWith("91")) {
            return "+" + cleaned;
        }
        
        // If already formatted with +
        if (phoneNumber.startsWith("+")) {
            return phoneNumber;
        }
        
        // Default to India code for 10-digit numbers
        return "+91" + cleaned;
    }
    
    public boolean isValidPhoneNumber(String phoneNumber) {
        String cleaned = phoneNumber.replaceAll("[^0-9]", "");
        return cleaned.length() == 10 || cleaned.length() == 12;
    }
}