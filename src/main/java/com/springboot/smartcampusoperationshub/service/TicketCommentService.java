package com.springboot.smartcampusoperationshub.service;

import com.springboot.smartcampusoperationshub.dto.AddCommentRequest;
import com.springboot.smartcampusoperationshub.dto.UpdateCommentRequest;
import com.springboot.smartcampusoperationshub.exception.BadRequestException;
import com.springboot.smartcampusoperationshub.exception.ResourceNotFoundException;
import com.springboot.smartcampusoperationshub.model.IncidentTicket;
import com.springboot.smartcampusoperationshub.model.TicketComment;
import com.springboot.smartcampusoperationshub.model.UserRole;
import com.springboot.smartcampusoperationshub.repository.TicketCommentRepository;
import org.springframework.stereotype.Service;

@Service
public class TicketCommentService {

    private final TicketCommentRepository ticketCommentRepository;
    private final IncidentTicketService incidentTicketService;

    public TicketCommentService(TicketCommentRepository ticketCommentRepository,
                                IncidentTicketService incidentTicketService) {
        this.ticketCommentRepository = ticketCommentRepository;
        this.incidentTicketService = incidentTicketService;
    }

    public TicketComment addComment(Long ticketId, AddCommentRequest request) {
        IncidentTicket ticket = incidentTicketService.getTicketById(ticketId);

        TicketComment comment = new TicketComment();
        comment.setCommentText(request.getCommentText());
        comment.setCommenterName(request.getCommenterName());
        comment.setCommenterRole(request.getCommenterRole());
        comment.setTicket(ticket);

        return ticketCommentRepository.save(comment);
    }

    public TicketComment updateComment(Long commentId, UpdateCommentRequest request) {
        TicketComment comment = ticketCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        boolean isOwner = comment.getCommenterName().equalsIgnoreCase(request.getRequesterName());
        boolean isAdmin = request.getRequesterRole() == UserRole.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new BadRequestException("You can only edit your own comment unless you are ADMIN");
        }

        comment.setCommentText(request.getCommentText());
        return ticketCommentRepository.save(comment);
    }

    public void deleteComment(Long commentId, String requesterName, UserRole requesterRole) {
        TicketComment comment = ticketCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        boolean isOwner = comment.getCommenterName().equalsIgnoreCase(requesterName);
        boolean isAdmin = requesterRole == UserRole.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new BadRequestException("You can only delete your own comment unless you are ADMIN");
        }

        ticketCommentRepository.delete(comment);
    }
}