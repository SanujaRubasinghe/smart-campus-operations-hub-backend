package com.springboot.smartcampusoperationshub.repository;

import com.smartcampus.smart_campus_backend.model.User;
import com.smartcampus.smart_campus_backend.model.UserSession;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findByToken(String token);

    Optional<UserSession> findBySessionId(String sessionId);

    List<UserSession> findByUserAndIsValidTrue(User user);

    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.isValid = false WHERE s.user = :user AND s.token != :currentToken")
    int invalidateAllUserSessionsExcept(@Param("user") User user, @Param("currentToken") String currentToken);

    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.isValid = false WHERE s.expiresAt < :now")
    int expireSessions(@Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserSession s WHERE s.expiresAt < :cutoffDate")
    int deleteExpiredSessions(@Param("cutoffDate") LocalDateTime cutoffDate);
}
