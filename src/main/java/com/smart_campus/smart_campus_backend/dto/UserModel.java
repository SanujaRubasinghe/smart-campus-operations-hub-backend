package com.smart_campus.smart_campus_backend.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "users", itemRelation = "user")
public class UserModel extends RepresentationModel<UserModel> {
    private Long id;
    private String email;
    private String name;
    private String picture;
    private String role;
    private boolean mfaEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
}
