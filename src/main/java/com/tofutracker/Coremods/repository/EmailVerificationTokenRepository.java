package com.tofutracker.Coremods.repository;

import com.tofutracker.Coremods.entity.EmailVerificationToken;
import com.tofutracker.Coremods.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    Optional<EmailVerificationToken> findByUser(User user);

    void deleteByUser(User user);

    @Modifying
    @Query("DELETE FROM EmailVerificationToken e WHERE e.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(e) > 0 FROM EmailVerificationToken e WHERE e.user = :user AND e.expiryDate > :now")
    boolean existsValidTokenForUser(@Param("user") User user, @Param("now") LocalDateTime now);
} 