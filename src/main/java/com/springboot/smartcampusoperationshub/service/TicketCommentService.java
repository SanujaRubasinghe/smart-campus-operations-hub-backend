package com.springboot.smartcampusoperationshub.service;

import com.springboot.smartcampusoperationshub.dto.AddCommentRequest;
import com.springboot.smartcampusoperationshub.dto.UpdateCommentRequest;
import com.springboot.smartcampusoperationshub.exception.BadRequestException;
import com.springboot.smartcampusoperationshub.exception.ResourceNotFoundException;
import com.springboot.smartcampusoperationshub.model.IncidentTicket;
import com.springboot.smartcampusoperationshub.model.TicketComment;
import com.springboot.smartcampusoperationshub.model.UserRole;
import com.springboot.smartcampusoperationshub.model.enums.NotificationType;
import com.springboot.smartcampusoperationshub.repository.TicketCommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketCommentService {

    private static final Logger logger = LoggerFactory.getLogger(TicketCommentService.class);

    private final TicketCommentRepository ticketCommentRepository;
    private final IncidentTicketService incidentTicketService;
    private final NotificationService notificationService;

    public TicketCommentService(TicketCommentRepository ticketCommentRepository,
                                IncidentTicketService incidentTicketService,
                                NotificationService notificationService) {
        this.ticketCommentRepository = ticketCommentRepository;
        this.incidentTicketService = incidentTicketService;
        this.notificationService = notificationService;
    }

    @Transactional
    public TicketComment addComment(Long ticketId, AddCommentRequest request) {
        try {
            IncidentTicket ticket = incidentTicketService.getTicketById(ticketId);

            TicketComment comment = new TicketComment();
            comment.setCommentText(request.getCommentText());
            comment.setCommenterName(request.getCommenterName());
            comment.setCommenterRole(request.getCommenterRole() != null ? request.getCommenterRole() : UserRole.STUDENT);
            comment.setTicket(ticket);

            TicketComment saved = ticketCommentRepository.save(comment);

            // Notify the ticket owner when Admin replies
            boolean isAdminReply = "Admin".equalsIgnoreCase(request.getCommenterName())
                    || request.getCommenterRole() == UserRole.ADMIN;
            if (isAdminReply && ticket.getReportedByUserId() != null) {
                notificationService.createNotification(
                    ticket.getReportedByUserId(),
                    NotificationType.COMMENT_ADDED,
                    "Admin replied to your ticket",
                    "Your ticket #" + ticket.getId() + " (" + ticket.getCategory() + ") received a reply: "
                        + request.getCommentText(),
                    "/tickets"
                );
            }
            return saved;
        } catch (Exception e) {
            logger.error("Failed to add comment to ticket {}: {}", ticketId, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
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