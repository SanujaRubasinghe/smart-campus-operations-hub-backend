package com.springboot.smartcampusoperationshub.controller;

import com.springboot.smartcampusoperationshub.model.Resource;
import com.springboot.smartcampusoperationshub.service.ResourceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Exposes RESTful endpoints for managing campus resources.
 */
@RestController
@RequestMapping("/api/v1/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    public ResponseEntity<Resource> createResource(@RequestBody Resource resource) {
        Resource savedResource = resourceService.createResource(resource);
        return new ResponseEntity<>(savedResource, HttpStatus.CREATED); // 201 Created
    }

    @GetMapping
    public ResponseEntity<Page<Resource>> getAllResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Resource> resources = resourceService.getAllResources(PageRequest.of(page, size));
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
}
