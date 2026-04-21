package com.springboot.smartcampusoperationshub.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.springboot.smartcampusoperationshub.model.IncidentTicket;
import com.springboot.smartcampusoperationshub.repository.IncidentTicketRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class TicketPdfService {

    private final IncidentTicketRepository incidentTicketRepository;

    public TicketPdfService(IncidentTicketRepository incidentTicketRepository) {
        this.incidentTicketRepository = incidentTicketRepository;
    }

    public byte[] generateTicketPdf(Long ticketId) {
        IncidentTicket ticket = incidentTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);

            document.add(new Paragraph("Incident Ticket Report", titleFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Ticket ID: " + ticket.getId(), normalFont));
            document.add(new Paragraph("Description: " + safe(ticket.getDescription()), normalFont));
            document.add(new Paragraph("Category: " + value(ticket.getCategory()), normalFont));
            document.add(new Paragraph("Priority: " + value(ticket.getPriority()), normalFont));
            document.add(new Paragraph("Status: " + value(ticket.getStatus()), normalFont));
            document.add(new Paragraph("Location: " + safe(ticket.getLocation()), normalFont));
            document.add(new Paragraph("Preferred Contact: " + safe(ticket.getPreferredContact()), normalFont));
            document.add(new Paragraph("Assigned Technician: " + safe(ticket.getAssignedTechnicianName()), normalFont));
            document.add(new Paragraph("Resolution Notes: " + safe(ticket.getResolutionNotes()), normalFont));
            document.add(new Paragraph("Rejection Reason: " + safe(ticket.getRejectionReason()), normalFont));

            document.close();
            return out.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Error while generating PDF: " + e.getMessage());
        }
    }

    private String safe(String value) {
        return value == null ? "N/A" : value;
    }

    private String value(Object value) {
        return value == null ? "N/A" : value.toString();
    }
}