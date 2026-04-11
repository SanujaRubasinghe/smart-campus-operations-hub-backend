package com.smartcampus.smart_campus_backend.controller;

import com.smartcampus.smart_campus_backend.dto.UserModel;
import com.smartcampus.smart_campus_backend.model.User;
import com.smartcampus.smart_campus_backend.security.UserPrincipal;
import com.smartcampus.smart_campus_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserModelAssembler assembler;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserModel> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserById(userPrincipal.getId());
        return ResponseEntity.ok(assembler.toModel(user));
    }
}
