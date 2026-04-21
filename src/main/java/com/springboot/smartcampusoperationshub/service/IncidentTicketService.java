package com.springboot.smartcampusoperationshub.service;

import com.springboot.smartcampusoperationshub.dto.AssignTechnicianRequest;
import com.springboot.smartcampusoperationshub.dto.CreateTicketRequest;
import com.springboot.smartcampusoperationshub.dto.UpdateTicketStatusRequest;
import com.springboot.smartcampusoperationshub.exception.BadRequestException;
import com.springboot.smartcampusoperationshub.exception.ResourceNotFoundException;
import com.springboot.smartcampusoperationshub.model.IncidentTicket;
import com.springboot.smartcampusoperationshub.model.enums.NotificationType;
import com.springboot.smartcampusoperationshub.model.TicketAttachment;
import com.springboot.smartcampusoperationshub.model.TicketStatus;
import com.springboot.smartcampusoperationshub.repository.IncidentTicketRepository;
import org.springframework.stereotype.Service;
import com.springboot.smartcampusoperationshub.dto.UpdateTicketDetailsRequest;

import java.util.List;

@Service
public class IncidentTicketService {

    private final IncidentTicketRepository incidentTicketRepository;
    private final NotificationService notificationService;

    public IncidentTicketService(IncidentTicketRepository incidentTicketRepository,
                                  NotificationService notificationService) {
        this.incidentTicketRepository = incidentTicketRepository;
        this.notificationService = notificationService;
    }

    public IncidentTicket createTicket(CreateTicketRequest request, Long reportedByUserId) {
        IncidentTicket ticket = new IncidentTicket();
        ticket.setCategory(request.getCategory());
        ticket.setLocation(request.getLocation());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setPreferredContact(request.getPreferredContact());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setReportedByUserId(reportedByUserId);
        IncidentTicket saved = incidentTicketRepository.save(ticket);

        // Save Supabase attachment URLs as TicketAttachment records
        if (request.getAttachmentUrls() != null) {
            for (String url : request.getAttachmentUrls()) {
                String fileName = url.substring(url.lastIndexOf('/') + 1);
                String[] parts = fileName.split("\\.");
                String ext = parts.length > 1 ? parts[parts.length - 1].toLowerCase() : "file";
                String mime = ext.equals("png") ? "image/png" :
                              ext.equals("jpg") || ext.equals("jpeg") ? "image/jpeg" :
                              ext.equals("webp") ? "image/webp" : "image/" + ext;
                TicketAttachment att = new TicketAttachment();
                att.setFileName(fileName);
                att.setFileType(mime);
                att.setFilePath(url);
                att.setTicket(saved);
                saved.getAttachments().add(att);
            }
            incidentTicketRepository.save(saved);
        }
        return saved;
    }

    public List<IncidentTicket> getTickets(boolean isAdmin, Long userId) {
        if (isAdmin) return incidentTicketRepository.findAll();
        if (userId == null) return List.of();
        return incidentTicketRepository.findByReportedByUserId(userId);
    }

    /** @deprecated kept for backward compatibility with admin service */
    public List<IncidentTicket> getAllTickets() {
        return incidentTicketRepository.findAll();
    }

    public IncidentTicket getTicketById(Long id) {
        return incidentTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));
    }

    public IncidentTicket assignTechnician(Long id, AssignTechnicianRequest request) {
        IncidentTicket ticket = getTicketById(id);
        ticket.setAssignedTechnicianName(request.getAssignedTechnicianName());
        return incidentTicketRepository.save(ticket);
    }

    public IncidentTicket updateStatus(Long id, UpdateTicketStatusRequest request) {
        IncidentTicket ticket = getTicketById(id);

        if (request.getStatus() == null) {
            throw new BadRequestException("Status is required");
        }

        validateStatusTransition(ticket.getStatus(), request.getStatus());

        if (request.getStatus() == TicketStatus.RESOLVED) {
            if (request.getResolutionNotes() == null || request.getResolutionNotes().isBlank()) {
                throw new BadRequestException("Resolution notes are required when status is RESOLVED");
            }
            ticket.setResolutionNotes(request.getResolutionNotes());
            ticket.setRejectionReason(null);
        } else if (request.getStatus() == TicketStatus.REJECTED) {
            if (request.getRejectionReason() == null || request.getRejectionReason().isBlank()) {
                throw new BadRequestException("Rejection reason is required when status is REJECTED");
            }
            ticket.setRejectionReason(request.getRejectionReason());
            ticket.setResolutionNotes(null);
        } else {
            ticket.setResolutionNotes(null);
            ticket.setRejectionReason(null);
        }

        ticket.setStatus(request.getStatus());
        IncidentTicket saved = incidentTicketRepository.save(ticket);

        // Notify the ticket owner about status change
        if (saved.getReportedByUserId() != null) {
            NotificationType notifType = switch (request.getStatus()) {
                case RESOLVED -> NotificationType.TICKET_RESOLVED;
                case CLOSED   -> NotificationType.TICKET_CLOSED;
                default       -> NotificationType.TICKET_UPDATED;
            };
            String statusLabel = request.getStatus().name().replace("_", " ");
            notificationService.createNotification(
                saved.getReportedByUserId(),
                notifType,
                "Ticket #" + saved.getId() + " " + statusLabel,
                "Your ticket has been updated to " + statusLabel + ".",
                "/tickets"
            );
        }
        return saved;
    }

    public IncidentTicket updateTicketDetails(Long id, UpdateTicketDetailsRequest request) {

        IncidentTicket ticket = getTicketById(id);

        if (request.getCategory() != null) {
            ticket.setCategory(request.getCategory());
        }

        if (request.getLocation() != null) {
            ticket.setLocation(request.getLocation());
        }

        if (request.getDescription() != null) {
            ticket.setDescription(request.getDescription());
        }

        if (request.getPriority() != null) {
            ticket.setPriority(request.getPriority());
        }

        if (request.getPreferredContact() != null) {
            ticket.setPreferredContact(request.getPreferredContact());
        }

        return incidentTicketRepository.save(ticket);
    }

    public void deleteTicket(Long id) {
        IncidentTicket ticket = incidentTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + id));

        incidentTicketRepository.delete(ticket);
    }

    private void validateStatusTransition(TicketStatus current, TicketStatus next) {
        if (next == null) {
            throw new BadRequestException("Next status cannot be null");
        }

        if (current == next) {
            return;
        }

        boolean valid =
                (current == TicketStatus.OPEN && (next == TicketStatus.IN_PROGRESS || next == TicketStatus.REJECTED)) ||
                        (current == TicketStatus.IN_PROGRESS && (next == TicketStatus.RESOLVED || next == TicketStatus.REJECTED)) ||
                        (current == TicketStatus.RESOLVED && next == TicketStatus.CLOSED);

        if (!valid) {
            throw new BadRequestException("Invalid status transition from " + current + " to " + next);
        }
    }
}