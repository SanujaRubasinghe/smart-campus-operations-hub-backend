package com.springboot.smartcampusoperationshub.repository;

import com.springboot.smartcampusoperationshub.model.IncidentTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentTicketRepository extends JpaRepository<IncidentTicket, Long> {
}