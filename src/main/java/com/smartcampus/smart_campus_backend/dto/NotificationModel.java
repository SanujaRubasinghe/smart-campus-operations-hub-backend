package com.smartcampus.smart_campus_backend.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "notifications", itemRelation = "notification")
public class NotificationModel extends RepresentationModel<NotificationModel> {
    private Long id;
    private String type;
    private String title;
    private String message;
    private String link;
    private String imageUrl;
    private Map<String, Object> metadata;
    private boolean read;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
