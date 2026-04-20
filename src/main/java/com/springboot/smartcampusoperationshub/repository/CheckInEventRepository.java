package com.springboot.smartcampusoperationshub.repository;

import com.springboot.smartcampusoperationshub.model.CheckInEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckInEventRepository extends JpaRepository<CheckInEvent, Long> {
}