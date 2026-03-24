package com.smart_campus.smart_campus_backend.service;

import com.smart_campus.smart_campus_backend.model.Notification;
import com.smart_campus.smart_campus_backend.repository.NotificationPreferencesRepository;
import com.smart_campus.smart_campus_backend.repository.NotificationRepository;
import com.smart_campus.smart_campus_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferencesRepository preferencesRepository;
    private final UserRepository userRepository;

    // Store SSE emitters for real-time updates
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public Page<Notification> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalseAndIsDeletedFalse(userId);
    }
}
