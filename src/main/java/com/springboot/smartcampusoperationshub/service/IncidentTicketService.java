package com.springboot.smartcampusoperationshub.service;

import com.springboot.smartcampusoperationshub.dto.AssignTechnicianRequest;
import com.springboot.smartcampusoperationshub.dto.CreateTicketRequest;
import com.springboot.smartcampusoperationshub.dto.UpdateTicketStatusRequest;
import com.springboot.smartcampusoperationshub.exception.BadRequestException;
import com.springboot.smartcampusoperationshub.exception.ResourceNotFoundException;
import com.springboot.smartcampusoperationshub.model.IncidentTicket;
import com.springboot.smartcampusoperationshub.model.TicketStatus;
import com.springboot.smartcampusoperationshub.repository.IncidentTicketRepository;
import org.springframework.stereotype.Service;
import com.springboot.smartcampusoperationshub.dto.UpdateTicketDetailsRequest;

import java.util.List;

@Service
public class IncidentTicketService {

    private final IncidentTicketRepository incidentTicketRepository;

    public IncidentTicketService(IncidentTicketRepository incidentTicketRepository) {
        this.incidentTicketRepository = incidentTicketRepository;
    }

    public IncidentTicket createTicket(CreateTicketRequest request) {
        IncidentTicket ticket = new IncidentTicket();
        ticket.setCategory(request.getCategory());
        ticket.setLocation(request.getLocation());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setPreferredContact(request.getPreferredContact());
        ticket.setStatus(TicketStatus.OPEN);

        return incidentTicketRepository.save(ticket);
    }

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
        return incidentTicketRepository.save(ticket);
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