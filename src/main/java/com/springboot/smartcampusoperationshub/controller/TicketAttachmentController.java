package com.springboot.smartcampusoperationshub.controller;

import com.springboot.smartcampusoperationshub.model.TicketAttachment;
import com.springboot.smartcampusoperationshub.service.TicketAttachmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class TicketAttachmentController {

    private final TicketAttachmentService ticketAttachmentService;

    public TicketAttachmentController(TicketAttachmentService ticketAttachmentService) {
        this.ticketAttachmentService = ticketAttachmentService;
    }

    @PostMapping(value = "/api/tickets/{ticketId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TicketAttachment> uploadAttachment(@PathVariable Long ticketId,
                                                             @RequestPart("file") MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketAttachmentService.uploadAttachment(ticketId, file));
    }

    @DeleteMapping("/api/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId) {
        ticketAttachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }
}