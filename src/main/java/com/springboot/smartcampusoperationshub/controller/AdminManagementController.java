package com.springboot.smartcampusoperationshub.controller;

import com.springboot.smartcampusoperationshub.dto.UserModel;
import com.springboot.smartcampusoperationshub.model.User;
import com.springboot.smartcampusoperationshub.model.enums.Role;
import com.springboot.smartcampusoperationshub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminManagementController {

    private final UserRepository userRepository;
    private final UserModelAssembler userModelAssembler;

    @GetMapping("/pending")
    public ResponseEntity<List<UserModel>> getPendingAdmins() {
        List<User> pendingAdmins = userRepository.findAllByRoleAndEnabled(Role.ADMIN, false);
        List<UserModel> models = pendingAdmins.stream()
                .map(userModelAssembler::toModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(models);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveAdmin(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.badRequest().body(Map.of("message", "User is not an admin"));
        }

        user.setEnabled(true);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Admin registration request approved successfully."));
    }

    @DeleteMapping("/{id}/reject")
    public ResponseEntity<?> rejectAdmin(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.ADMIN) {
            return ResponseEntity.badRequest().body(Map.of("message", "User is not an admin"));
        }

        if (user.isEnabled()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cannot reject an already enabled admin"));
        }

        userRepository.delete(user);

        return ResponseEntity.ok(Map.of("message", "Admin registration request rejected and deleted."));
    }
}
