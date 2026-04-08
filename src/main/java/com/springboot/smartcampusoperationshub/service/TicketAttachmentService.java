package com.springboot.smartcampusoperationshub.service;

import com.springboot.smartcampusoperationshub.exception.BadRequestException;
import com.springboot.smartcampusoperationshub.exception.ResourceNotFoundException;
import com.springboot.smartcampusoperationshub.model.IncidentTicket;
import com.springboot.smartcampusoperationshub.model.TicketAttachment;
import com.springboot.smartcampusoperationshub.repository.TicketAttachmentRepository;
import com.springboot.smartcampusoperationshub.util.FileStorageUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TicketAttachmentService {

    private final TicketAttachmentRepository ticketAttachmentRepository;
    private final IncidentTicketService incidentTicketService;
    private final FileStorageUtil fileStorageUtil;

    public TicketAttachmentService(TicketAttachmentRepository ticketAttachmentRepository,
                                   IncidentTicketService incidentTicketService,
                                   FileStorageUtil fileStorageUtil) {
        this.ticketAttachmentRepository = ticketAttachmentRepository;
        this.incidentTicketService = incidentTicketService;
        this.fileStorageUtil = fileStorageUtil;
    }

    public TicketAttachment uploadAttachment(Long ticketId, MultipartFile file) {
        IncidentTicket ticket = incidentTicketService.getTicketById(ticketId);

        if (ticket.getAttachments() != null && ticket.getAttachments().size() >= 3) {
            throw new BadRequestException("Maximum 3 attachments are allowed for one ticket");
        }

        String savedPath = fileStorageUtil.saveImage(file);

        TicketAttachment attachment = new TicketAttachment();
        attachment.setFileName(file.getOriginalFilename());
        attachment.setFileType(file.getContentType());
        attachment.setFilePath(savedPath);
        attachment.setTicket(ticket);

        return ticketAttachmentRepository.save(attachment);
    }

    public void deleteAttachment(Long attachmentId) {
        TicketAttachment attachment = ticketAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id: " + attachmentId));

        fileStorageUtil.deleteFile(attachment.getFilePath());
        ticketAttachmentRepository.delete(attachment);
    }
}