package com.smartcampus.smart_campus_backend.service;

import com.smartcampus.smart_campus_backend.dto.*;
import com.smartcampus.smart_campus_backend.exception.*;
import com.smartcampus.smart_campus_backend.model.User;
import com.smartcampus.smart_campus_backend.model.UserSession;
import com.smartcampus.smart_campus_backend.repository.UserRepository;
import com.smartcampus.smart_campus_backend.repository.UserSessionRepository;
import com.smartcampus.smart_campus_backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserSessionRepository sessionRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = getUserById(id);
        return UserPrincipal.create(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        List<SessionResponse> activeSessions = sessionRepository.findByUserAndIsValidTrue(user)
                .stream()
                .map(this::mapToSessionResponse)
                .collect(Collectors.toList());

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .picture(user.getPicture())
                .role(user.getRole().name())
                .mfaEnabled(user.isMfaEnabled())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .activeSessions(activeSessions)
                .build();
    }

    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName());
        }

        if (request.getPicture() != null) {
            user.setPicture(request.getPicture());
        }

        user = userRepository.save(user);
        log.info("Profile updated for user: {}", user.getEmail());

        return getUserProfile(userId);
    }

    public List<SessionResponse> getActiveSessions(Long userId, String currentToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        return sessionRepository.findByUserAndIsValidTrue(user)
                .stream()
                .map(session -> {
                    SessionResponse response = mapToSessionResponse(session);
                    response.setCurrentSession(session.getToken().equals(currentToken));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void terminateSession(Long userId, Long sessionId) {
        UserSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session", sessionId));

        if (!session.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to terminate this session");
        }

        session.setValid(false);
        sessionRepository.save(session);
        log.info("Session {} terminated for user {}", sessionId, userId);
    }

    private SessionResponse mapToSessionResponse(UserSession session) {
        return SessionResponse.builder()
                .id(session.getId())
                .deviceInfo(session.getDeviceInfo())
                .ipAddress(session.getIpAddress())
                .createdAt(session.getCreatedAt())
                .lastAccessedAt(session.getLastAccessedAt())
                .expiresAt(session.getExpiresAt())
                .sessionId(session.getSessionId())
                .currentSession(false)
                .build();
    }
}