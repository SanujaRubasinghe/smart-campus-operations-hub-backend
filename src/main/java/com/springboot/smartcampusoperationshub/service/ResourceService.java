package com.springboot.smartcampusoperationshub.service;

import com.springboot.smartcampusoperationshub.model.Resource;
import com.springboot.smartcampusoperationshub.model.enums.ResourceStatus;
import com.springboot.smartcampusoperationshub.exception.ResourceNotFoundException;
import com.springboot.smartcampusoperationshub.repository.ResourceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import com.springboot.smartcampusoperationshub.model.enums.ResourceType;
import com.springboot.smartcampusoperationshub.repository.ResourceSpecification;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Transactional
    public Resource createResource(Resource resource) {
        return resourceRepository.save(resource);
    }

    @Transactional(readOnly = true)
    public Page<Resource> getAllResources(ResourceType type, Integer minCapacity, String location, Pageable pageable) {
        Specification<Resource> spec = ResourceSpecification.getFilteredResources(type, minCapacity, location);
        return resourceRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Resource getResourceById(UUID id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + id));
    }

    @Transactional
    public Resource updateResource(UUID id, Resource updatedData) {
        Resource existing = getResourceById(id);
        existing.setName(updatedData.getName());
        existing.setType(updatedData.getType());
        existing.setCapacity(updatedData.getCapacity());
        existing.setLocation(updatedData.getLocation());
        existing.setAmenities(updatedData.getAmenities());
        return resourceRepository.save(existing);
    }

    @Transactional
    public Resource updateResourceStatus(UUID id, Map<String, String> statusUpdate) {
        Resource existing = getResourceById(id);
        if (statusUpdate.containsKey("status")) {
            existing.setStatus(ResourceStatus.valueOf(statusUpdate.get("status").toUpperCase()));
        }
        return resourceRepository.save(existing);
    }

    @Transactional
    public void deleteResource(UUID id) {
        Resource existing = getResourceById(id);
        resourceRepository.delete(existing);
    }
}
