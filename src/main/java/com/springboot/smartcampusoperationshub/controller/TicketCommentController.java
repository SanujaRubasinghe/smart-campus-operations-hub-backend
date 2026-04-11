package com.springboot.smartcampusoperationshub.controller;

import com.springboot.smartcampusoperationshub.dto.AddCommentRequest;
import com.springboot.smartcampusoperationshub.dto.UpdateCommentRequest;
import com.springboot.smartcampusoperationshub.model.TicketComment;
import com.springboot.smartcampusoperationshub.model.UserRole;
import com.springboot.smartcampusoperationshub.service.TicketCommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TicketCommentController {

    private final TicketCommentService ticketCommentService;

    public TicketCommentController(TicketCommentService ticketCommentService) {
        this.ticketCommentService = ticketCommentService;
    }

    @PostMapping("/api/tickets/{ticketId}/comments")
    public ResponseEntity<TicketComment> addComment(@PathVariable Long ticketId,
                                                    @Valid @RequestBody AddCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketCommentService.addComment(ticketId, request));
    }

    @PatchMapping("/api/comments/{commentId}")
    public ResponseEntity<TicketComment> updateComment(@PathVariable Long commentId,
                                                       @Valid @RequestBody UpdateCommentRequest request) {
        return ResponseEntity.ok(ticketCommentService.updateComment(commentId, request));
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              @RequestParam String requesterName,
                                              @RequestParam UserRole requesterRole) {
        ticketCommentService.deleteComment(commentId, requesterName, requesterRole);
        return ResponseEntity.noContent().build();
    }


}