package com.springboot.smartcampusoperationshub.controller;

import com.springboot.smartcampusoperationshub.model.User;
import com.springboot.smartcampusoperationshub.model.enums.AuthProvider;
import com.springboot.smartcampusoperationshub.model.enums.Role;
import com.springboot.smartcampusoperationshub.repository.UserRepository;
import com.springboot.smartcampusoperationshub.security.JwtTokenProvider;
import com.springboot.smartcampusoperationshub.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    /** Admin login */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email    = body.get("email");
        String password = body.get("password");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));

        // Check if user has ADMIN role
        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(403).body(Map.of("message", "Access denied. Only administrators can log in here."));
        }

        if (user.getPassword() == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
        }

        if (!user.isEnabled()) {
            return ResponseEntity.status(403).body(Map.of("message", "Account is disabled"));
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        UserPrincipal principal = UserPrincipal.create(user);
        org.springframework.security.core.Authentication auth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        principal, null, principal.getAuthorities());
        String token = tokenProvider.generateToken(auth);
        return ResponseEntity.ok(Map.of("token", token));
    }

    /** Admin registration */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email    = body.get("email");
        String password = body.get("password");
        String name     = body.get("name");

        if (email == null || password == null || name == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "name, email and password are required"));
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("message", "Email is already registered"));
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(usernameFrom(email));
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        user.setProvider(AuthProvider.local);
        user.setRole(Role.ADMIN); // Set role to ADMIN explicitly
        user.setEnabled(false); // New admin accounts must be approved
        userRepository.save(user);

        return ResponseEntity.status(201).body(Map.of("message", "Registration request submitted successfully. Please wait for an existing administrator to approve your account."));
    }

    private String usernameFrom(String email) {
        String base = email.split("@")[0].toLowerCase().replaceAll("[^a-z0-9._]", "_");
        String candidate = base;
        int attempt = 0;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + (++attempt);
        }
        return candidate;
    }
}
