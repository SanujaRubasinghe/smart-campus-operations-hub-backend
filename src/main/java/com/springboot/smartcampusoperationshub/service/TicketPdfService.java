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
import java.util.List;

@Service
public class TicketPdfService {

    private final IncidentTicketRepository incidentTicketRepository;

    public TicketPdfService(IncidentTicketRepository incidentTicketRepository) {
        this.incidentTicketRepository = incidentTicketRepository;
    }

    public byte[] generateAllTicketsPdf() {
        List<IncidentTicket> tickets = incidentTicketRepository.findAll();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("All Incident Tickets Report"));
            document.add(new Paragraph(" "));

            for (IncidentTicket ticket : tickets) {
                document.add(new Paragraph("Ticket ID: " + ticket.getId()));
                document.add(new Paragraph("Description: " + safe(ticket.getDescription())));
                document.add(new Paragraph("Category: " + value(ticket.getCategory())));
                document.add(new Paragraph("Priority: " + value(ticket.getPriority())));
                document.add(new Paragraph("Status: " + value(ticket.getStatus())));
                document.add(new Paragraph("Location: " + safe(ticket.getLocation())));
                document.add(new Paragraph("Preferred Contact: " + safe(ticket.getPreferredContact())));
                document.add(new Paragraph("Assigned Technician: " + safe(ticket.getAssignedTechnicianName())));
                document.add(new Paragraph("Resolution Notes: " + safe(ticket.getResolutionNotes())));
                document.add(new Paragraph("Rejection Reason: " + safe(ticket.getRejectionReason())));
                document.add(new Paragraph("--------------------------------------------------"));
                document.add(new Paragraph(" "));
            }

            document.close();
            return out.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Error while generating all tickets PDF" + e.getMessage());
        }
    }

    private String safe(String value) {
        return value == null ? "N/A" : value;
    }

    private String value(Object value) {
        return value == null ? "N/A" : value.toString();
    }
}