package com.rental.platform.repository;

import com.rental.platform.model.VerificationToken;
import com.rental.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    Optional<VerificationToken> findByOtpAndUserAndUsedFalse(String otp, User user);
    Optional<VerificationToken> findByUserAndTokenTypeAndUsedFalse(
            User user, VerificationToken.TokenType tokenType);
    void deleteByUser(User user);
    void deleteByUserAndTokenType(User user, VerificationToken.TokenType tokenType);
}