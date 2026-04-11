package com.smartcampus.smart_campus_backend.controller;

import com.smartcampus.smart_campus_backend.dto.NotificationModel;
import com.smartcampus.smart_campus_backend.model.Notification;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class NotificationModelAssembler extends RepresentationModelAssemblerSupport<Notification, NotificationModel> {

    public NotificationModelAssembler() {
        super(NotificationController.class, NotificationModel.class);
    }

    @Override
    public NotificationModel toModel(Notification entity) {
        NotificationModel model = instantiateModel(entity);

        model.add(linkTo(methodOn(NotificationController.class).getNotificationById(entity.getId())).withSelfRel());
        model.add(linkTo(methodOn(NotificationController.class).markAsRead(entity.getId())).withRel("mark-as-read"));

        model.setId(entity.getId());
        model.setType(entity.getType().name());
        model.setTitle(entity.getTitle());
        model.setMessage(entity.getMessage());
        model.setLink(entity.getLink());
        model.setImageUrl(entity.getImageUrl());
        model.setMetadata(entity.getMetadata());
        model.setRead(entity.isRead());
        model.setCreatedAt(entity.getCreatedAt());
        model.setReadAt(entity.getReadAt());

        return model;
    }
}
