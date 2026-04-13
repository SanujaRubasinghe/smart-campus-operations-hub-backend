package com.springboot.smartcampusoperationshub.repository;

import com.springboot.smartcampusoperationshub.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Data access layer for Resource entities.
 * Extends JpaSpecificationExecutor to allow for dynamic, complex query building 
 * required by the catalogue filter and recommendation engine.
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID>, JpaSpecificationExecutor<Resource> {
    
    // Spring Data JPA will automatically implement this query based on the method name
    boolean existsByNameAndLocation(String name, String location);
}
