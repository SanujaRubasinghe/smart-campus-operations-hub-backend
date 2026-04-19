package com.springboot.smartcampusoperationshub.controller;

import com.springboot.smartcampusoperationshub.dto.bookings.CheckInRequest;
import com.springboot.smartcampusoperationshub.dto.bookings.CheckInResponse;
import com.springboot.smartcampusoperationshub.security.UserPrincipal;
import com.springboot.smartcampusoperationshub.service.CheckInService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkin")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping("/room/{roomId}")
    public ResponseEntity<CheckInResponse> checkIn(
            @PathVariable Long roomId,
            @RequestBody CheckInRequest body,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest request) {

        if (principal == null) {
            return ResponseEntity.status(401).body(
                    new CheckInResponse(false, null, "Authentication required"));
        }

        String clientIp = extractClientIp(request);

        CheckInResponse resp = checkInService.checkIn(
                principal.getId(),
                roomId,
                body.getQrSecret(),
                clientIp);

        return ResponseEntity.ok(resp);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}