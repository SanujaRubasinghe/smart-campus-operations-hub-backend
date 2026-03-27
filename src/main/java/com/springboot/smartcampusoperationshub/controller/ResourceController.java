package com.springboot.smartcampusoperationshub.controller;

import com.springboot.smartcampusoperationshub.model.Resource;
import com.springboot.smartcampusoperationshub.service.ResourceService;
import com.springboot.smartcampusoperationshub.service.RecommendationService;
import com.springboot.smartcampusoperationshub.dto.RecommendationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.springboot.smartcampusoperationshub.model.enums.ResourceType;
import jakarta.validation.Valid;

import java.util.UUID;

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

    @PostMapping
    public ResponseEntity<Resource> createResource(@Valid @RequestBody Resource resource) {
        Resource savedResource = resourceService.createResource(resource);
        return new ResponseEntity<>(savedResource, HttpStatus.CREATED); // 201 Created
    }

    @GetMapping
    public ResponseEntity<Page<Resource>> getAllResources(
            @RequestParam(required = false) ResourceType type,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Resource> resources = resourceService.getAllResources(type, minCapacity, location, PageRequest.of(page, size));
        return ResponseEntity.ok(resources); // 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getResourceById(@PathVariable UUID id) {
        return ResponseEntity.ok(resourceService.getResourceById(id)); // 200 OK
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resource> updateResource(@PathVariable UUID id, @RequestBody Resource resource) {
        return ResponseEntity.ok(resourceService.updateResource(id, resource)); // 200 OK
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Resource> updateResourceStatus(@PathVariable UUID id, @RequestBody Map<String, String> statusUpdate) {
        return ResponseEntity.ok(resourceService.updateResourceStatus(id, statusUpdate)); // 200 OK
    }

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
}
