package com.smart_campus.smart_campus_backend.controller;

import com.smartcampus.smart_campus_backend.dto.UserModel;
import com.smartcampus.smart_campus_backend.model.User;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler extends RepresentationModelAssemblerSupport<User, UserModel> {

    public UserModelAssembler() {
        super(AuthController.class, UserModel.class);
    }

    @Override
    public UserModel toModel(User entity) {
        UserModel model = instantiateModel(entity);

        model.add(linkTo(methodOn(AuthController.class).getCurrentUser(null)).withSelfRel());
        model.add(linkTo(methodOn(NotificationController.class).getNotifications(null, null)).withRel("notifications"));

        model.setId(entity.getId());
        model.setEmail(entity.getEmail());
        model.setName(entity.getName());
        model.setPicture(entity.getPicture());
        model.setRole(entity.getRole().name());
        model.setMfaEnabled(entity.isMfaEnabled());
        model.setCreatedAt(entity.getCreatedAt());
        model.setLastLogin(entity.getLastLogin());

        return model;
    }
}
