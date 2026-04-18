package com.springboot.smartcampusoperationshub.repository;

import com.springboot.smartcampusoperationshub.model.IncidentTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidentTicketRepository extends JpaRepository<IncidentTicket, Long> {
    List<IncidentTicket> findByReportedByUserId(Long reportedByUserId);
}