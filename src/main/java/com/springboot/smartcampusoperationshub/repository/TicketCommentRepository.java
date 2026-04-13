package com.springboot.smartcampusoperationshub.repository;

import com.springboot.smartcampusoperationshub.model.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {
}