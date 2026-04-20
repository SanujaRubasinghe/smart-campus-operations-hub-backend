package com.springboot.smartcampusoperationshub.service;

import com.springboot.smartcampusoperationshub.repository.NotificationPreferencesRepository;
import com.springboot.smartcampusoperationshub.repository.NotificationRepository;
import com.springboot.smartcampusoperationshub.repository.UserRepository;
import com.springboot.smartcampusoperationshub.model.Notification;
import com.springboot.smartcampusoperationshub.model.User;
import com.springboot.smartcampusoperationshub.model.enums.NotificationType;
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

    /**
     * Creates and persists a notification for the given userId.
     */
    @Transactional
    public void createNotification(Long userId, NotificationType type, String title, String message, String link) {
        userRepository.findById(userId).ifPresent(user -> {
            Notification n = new Notification();
            n.setUser(user);
            n.setType(type);
            n.setTitle(title);
            n.setMessage(message);
            n.setLink(link);
            n.setRead(false);
            n.setDeleted(false);
            notificationRepository.save(n);
        });
    }
}
