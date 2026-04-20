package com.springboot.smartcampusoperationshub.controller;

import com.springboot.smartcampusoperationshub.model.Resource;
import com.springboot.smartcampusoperationshub.service.ResourceService;
import com.springboot.smartcampusoperationshub.service.RecommendationService;
import com.springboot.smartcampusoperationshub.dto.RecommendationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.springboot.smartcampusoperationshub.model.enums.ResourceType;
import jakarta.validation.Valid;

import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

/**
 * Exposes RESTful endpoints for managing campus resources.
 */
@RestController
@RequestMapping("/api/v1/resources")
public class ResourceController {

    private final ResourceService resourceService;
    private final RecommendationService recommendationService;

    public ResourceController(ResourceService resourceService, RecommendationService recommendationService) {
        this.resourceService = resourceService;
        this.recommendationService = recommendationService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Resource> createResource(@Valid @RequestBody Resource resource) {
        Resource savedResource = resourceService.createResource(resource);
        return new ResponseEntity<>(savedResource, HttpStatus.CREATED); // 201 Created
    }

    @GetMapping
    public ResponseEntity<Page<Resource>> getAllResources(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ResourceType type,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Resource> resources = resourceService.getAllResources(name, type, minCapacity, location, PageRequest.of(page, size));
        return ResponseEntity.ok(resources); // 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getResourceById(@PathVariable UUID id) {
        return ResponseEntity.ok(resourceService.getResourceById(id)); // 200 OK
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Resource> updateResource(@PathVariable UUID id, @RequestBody Resource resource) {
        return ResponseEntity.ok(resourceService.updateResource(id, resource)); // 200 OK
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<Resource> updateResourceStatus(@PathVariable UUID id, @RequestBody Map<String, String> statusUpdate) {
        return ResponseEntity.ok(resourceService.updateResourceStatus(id, statusUpdate)); // 200 OK
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable UUID id) {
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    /**
     * Innovation Feature: AI-Driven Resource Recommendation
     */
    @PostMapping("/recommendations")
    public ResponseEntity<List<Resource>> getRecommendations(@RequestBody RecommendationRequest request) {
        List<Resource> recommendations = recommendationService.getRecommendations(request.getIntent());
        return ResponseEntity.ok(recommendations);
    }
    /**
     * Endpoint for uploading an image for a specific resource.
     */
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> uploadResourceImage(
            @PathVariable UUID id, 
            @RequestParam("file") MultipartFile file) {
        Resource updatedResource = resourceService.uploadImage(id, file);
        return ResponseEntity.ok(updatedResource);
    }
}
