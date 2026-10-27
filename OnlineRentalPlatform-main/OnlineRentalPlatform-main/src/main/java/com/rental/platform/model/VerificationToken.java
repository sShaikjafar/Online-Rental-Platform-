package com.rental.platform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "verification_tokens")
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private String otp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private LocalDateTime expiryDate;
    private boolean used = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum TokenType {
        EMAIL_VERIFICATION, PHONE_VERIFICATION, PASSWORD_RESET
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public VerificationToken() {}

    public VerificationToken(String token, String otp, User user, TokenType tokenType, LocalDateTime expiryDate) {
        this.token = token;
        this.otp = otp;
        this.user = user;
        this.tokenType = tokenType;
        this.expiryDate = expiryDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public TokenType getTokenType() { return tokenType; }
    public void setTokenType(TokenType tokenType) { this.tokenType = tokenType; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}