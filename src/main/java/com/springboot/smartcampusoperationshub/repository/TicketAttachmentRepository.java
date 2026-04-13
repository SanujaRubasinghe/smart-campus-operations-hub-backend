package com.springboot.smartcampusoperationshub.repository;

import com.springboot.smartcampusoperationshub.model.TicketAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketAttachmentRepository extends JpaRepository<TicketAttachment, Long> {
}