package com.springboot.smartcampusoperationshub.controller;

import com.springboot.smartcampusoperationshub.dto.*;
import com.springboot.smartcampusoperationshub.model.IncidentTicket;
import com.springboot.smartcampusoperationshub.model.TicketAttachment;
import com.springboot.smartcampusoperationshub.model.TicketComment;
import com.springboot.smartcampusoperationshub.service.IncidentTicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class IncidentTicketController {

    private final IncidentTicketService incidentTicketService;

    public IncidentTicketController(IncidentTicketService incidentTicketService) {
        this.incidentTicketService = incidentTicketService;
    }

    @PostMapping
    public ResponseEntity<IncidentTicket> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        return new ResponseEntity<>(incidentTicketService.createTicket(request), HttpStatus.CREATED);
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
    public ResponseEntity<IncidentTicket> assignTechnician(@PathVariable Long id,
                                                           @Valid @RequestBody AssignTechnicianRequest request) {
        return ResponseEntity.ok(incidentTicketService.assignTechnician(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<IncidentTicket> updateStatus(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(incidentTicketService.updateStatus(id, request));
    }

    @PatchMapping("/{id}/resolution")
    public ResponseEntity<IncidentTicket> addResolution(@PathVariable Long id,
                                                        @Valid @RequestBody ResolutionNoteRequest request) {
        return ResponseEntity.ok(incidentTicketService.addResolution(id, request));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<TicketComment> addComment(@PathVariable Long id,
                                                    @Valid @RequestBody CommentRequest request) {
        return new ResponseEntity<>(incidentTicketService.addComment(id, request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<TicketComment>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(incidentTicketService.getCommentsByTicket(id));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        incidentTicketService.deleteComment(commentId);
        return ResponseEntity.ok("Comment deleted successfully");
    }

    @PostMapping("/{id}/attachments")
    public ResponseEntity<TicketAttachment> uploadAttachment(@PathVariable Long id,
                                                             @RequestParam("file") MultipartFile file) throws IOException {
        return new ResponseEntity<>(incidentTicketService.uploadAttachment(id, file), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/attachments")
    public ResponseEntity<List<TicketAttachment>> getAttachments(@PathVariable Long id) {
        return ResponseEntity.ok(incidentTicketService.getAttachmentsByTicket(id));
    }
}