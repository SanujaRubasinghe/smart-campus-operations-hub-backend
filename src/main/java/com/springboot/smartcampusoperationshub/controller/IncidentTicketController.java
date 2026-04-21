package com.springboot.smartcampusoperationshub.controller;

import com.springboot.smartcampusoperationshub.dto.AssignTechnicianRequest;
import com.springboot.smartcampusoperationshub.dto.CreateTicketRequest;
import com.springboot.smartcampusoperationshub.dto.UpdateTicketDetailsRequest;
import com.springboot.smartcampusoperationshub.dto.UpdateTicketStatusRequest;
import com.springboot.smartcampusoperationshub.model.IncidentTicket;
import com.springboot.smartcampusoperationshub.security.UserPrincipal;
import com.springboot.smartcampusoperationshub.service.IncidentTicketService;
import com.springboot.smartcampusoperationshub.service.TicketPdfService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
//@PreAuthorize("hasRole('ADMIN')")
public class IncidentTicketController {

    private final IncidentTicketService incidentTicketService;
    private final TicketPdfService ticketPdfService;

    public IncidentTicketController(IncidentTicketService incidentTicketService,
                                    TicketPdfService ticketPdfService) {
        this.incidentTicketService = incidentTicketService;
        this.ticketPdfService = ticketPdfService;
    }

    @PostMapping
    public ResponseEntity<IncidentTicket> createTicket(
            @Valid @RequestBody CreateTicketRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(incidentTicketService.createTicket(request, userPrincipal.getId()));
    }

    @GetMapping
    public ResponseEntity<List<IncidentTicket>> getAllTickets() {
        return ResponseEntity.ok(incidentTicketService.getAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentTicket> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(incidentTicketService.getTicketById(id));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<IncidentTicket> assignTechnician(
            @PathVariable Long id,
            @Valid @RequestBody AssignTechnicianRequest request) {
        return ResponseEntity.ok(incidentTicketService.assignTechnician(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<IncidentTicket> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketStatusRequest request) {
        return ResponseEntity.ok(incidentTicketService.updateStatus(id, request));
    }

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

//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/{id}/pdf")
//    public ResponseEntity<byte[]> downloadTicketPdf(@PathVariable Long id) {
//        byte[] pdfBytes = ticketPdfService.generateTicketPdf(id);
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ticket-" + id + ".pdf")
//                .contentType(MediaType.APPLICATION_PDF)
//                .body(pdfBytes);
//    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pdf/all")
    public ResponseEntity<byte[]> downloadAllTicketsPdf() {
        byte[] pdfBytes = ticketPdfService.generateAllTicketsPdf();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=all-tickets-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}