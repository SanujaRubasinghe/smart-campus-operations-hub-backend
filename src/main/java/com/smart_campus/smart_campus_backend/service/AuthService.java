package com.smart_campus.smart_campus_backend.service;

import com.smartcampus.smart_campus_backend.model.User;
import com.smartcampus.smart_campus_backend.model.UserSession;
import com.smartcampus.smart_campus_backend.repository.UserRepository;
import com.smartcampus.smart_campus_backend.repository.UserSessionRepository;
import com.smartcampus.smart_campus_backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserSessionRepository sessionRepository;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void recordLogin(User user, String token, String ipAddress, String deviceInfo, LocalDateTime expiresAt) {
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        UserSession session = UserSession.builder()
                .user(user)
                .token(token)
                .ipAddress(ipAddress)
                .deviceInfo(deviceInfo)
                .expiresAt(expiresAt)
                .isValid(true)
                .createdAt(LocalDateTime.now())
                .lastAccessedAt(LocalDateTime.now())
                .build();
        
        sessionRepository.save(session);
        log.info("Login recorded for user: {}", user.getEmail());
    }

    @Transactional
    public void logout(String token) {
        sessionRepository.findByToken(token).ifPresent(session -> {
            session.setValid(false);
            sessionRepository.save(session);
            log.info("Session invalidated for token logout");
        });
    }
}
