package com.springboot.smartcampusoperationshub.service;

import com.springboot.smartcampusoperationshub.model.Resource;
import com.springboot.smartcampusoperationshub.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class ResourceQrService {

    private final ResourceRepository resourceRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public ResourceQrService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public String generateSecret() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public Resource ensureSecret(Resource resource) {
        if (resource.getQrSecret() == null || resource.getQrSecret().isBlank()) {
            resource.setQrSecret(generateSecret());
            resourceRepository.save(resource);
        }
        return resource;
    }
}