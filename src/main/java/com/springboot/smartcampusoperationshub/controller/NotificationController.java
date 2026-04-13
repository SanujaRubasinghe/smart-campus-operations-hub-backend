package com.springboot.smartcampusoperationshub.controller;

import com.springboot.smartcampusoperationshub.dto.NotificationModel;
import com.springboot.smartcampusoperationshub.model.Notification;
import com.springboot.smartcampusoperationshub.security.UserPrincipal;
import com.springboot.smartcampusoperationshub.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationModelAssembler assembler;
    private final PagedResourcesAssembler<Notification> pagedAssembler;

    @GetMapping
    public ResponseEntity<PagedModel<NotificationModel>> getNotifications(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Pageable pageable) {
        Page<Notification> notifications = notificationService.getUserNotifications(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(pagedAssembler.toModel(notifications, assembler));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationModel> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(notificationService.getUnreadCount(userPrincipal.getId()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationModel> markAsRead(@PathVariable Long id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(assembler.toModel(notification));
    }
}
