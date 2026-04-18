package com.springboot.smartcampusoperationshub.controller;

import com.springboot.smartcampusoperationshub.dto.AssignTechnicianRequest;
import com.springboot.smartcampusoperationshub.dto.CreateTicketRequest;
import com.springboot.smartcampusoperationshub.dto.UpdateTicketDetailsRequest;
import com.springboot.smartcampusoperationshub.dto.UpdateTicketStatusRequest;
import com.springboot.smartcampusoperationshub.model.IncidentTicket;
import com.springboot.smartcampusoperationshub.security.UserPrincipal;
import com.springboot.smartcampusoperationshub.service.IncidentTicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class IncidentTicketController {

    private final IncidentTicketService incidentTicketService;

    public IncidentTicketController(IncidentTicketService incidentTicketService) {
        this.incidentTicketService = incidentTicketService;
    }

    @PostMapping
    public ResponseEntity<IncidentTicket> createTicket(
            @Valid @RequestBody CreateTicketRequest request,
            Authentication authentication) {
        Long userId = authentication != null && authentication.getPrincipal() instanceof UserPrincipal
                ? ((UserPrincipal) authentication.getPrincipal()).getId() : null;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(incidentTicketService.createTicket(request, userId));
    }

    @GetMapping
    public ResponseEntity<List<IncidentTicket>> getAllTickets(Authentication authentication) {
        boolean isAdmin = authentication != null &&
                authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        Long userId = authentication != null && authentication.getPrincipal() instanceof UserPrincipal
                ? ((UserPrincipal) authentication.getPrincipal()).getId() : null;
        return ResponseEntity.ok(incidentTicketService.getTickets(isAdmin, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentTicket> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(incidentTicketService.getTicketById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/assign")
    public ResponseEntity<IncidentTicket> assignTechnician(
            @PathVariable Long id,
            @Valid @RequestBody AssignTechnicianRequest request) {
        return ResponseEntity.ok(incidentTicketService.assignTechnician(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<IncidentTicket> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketStatusRequest request) {
        return ResponseEntity.ok(incidentTicketService.updateStatus(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        incidentTicketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<IncidentTicket> updateTicketDetails(
            @PathVariable Long id,
            @RequestBody UpdateTicketDetailsRequest request) {

        return ResponseEntity.ok(incidentTicketService.updateTicketDetails(id, request));
    }
}