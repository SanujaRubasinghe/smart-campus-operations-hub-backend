package com.smart_campus.smart_campus_backend.controller;

import com.smart_campus.smart_campus_backend.dto.LoginRequest;
import com.smart_campus.smart_campus_backend.dto.LoginResponse;
import com.smart_campus.smart_campus_backend.dto.RegisterRequest;
import com.smart_campus.smart_campus_backend.dto.ApiResponse;
import com.smart_campus.smart_campus_backend.dto.UserModel;
import com.smart_campus.smart_campus_backend.model.User;
import com.smart_campus.smart_campus_backend.security.UserPrincipal;
import com.smart_campus.smart_campus_backend.security.JwtTokenProvider;
import com.smart_campus.smart_campus_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserModelAssembler assembler;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userService.getUserById(userPrincipal.getId());

        return ResponseEntity.ok(LoginResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .picture(user.getPicture())
                .mfaEnabled(user.isMfaEnabled())
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        userService.registerUser(registerRequest);
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully"));
    }

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
