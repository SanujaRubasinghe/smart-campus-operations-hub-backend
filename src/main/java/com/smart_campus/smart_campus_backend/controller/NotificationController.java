package com.smart_campus.smart_campus_backend.controller;

import com.smartcampus.smart_campus_backend.dto.NotificationModel;
import com.smartcampus.smart_campus_backend.model.Notification;
import com.smartcampus.smart_campus_backend.security.UserPrincipal;
import com.smartcampus.smart_campus_backend.service.NotificationService;
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
        // This is a placeholder since I haven't implemented getById in service yet
        // but it's needed for the assembler links
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
