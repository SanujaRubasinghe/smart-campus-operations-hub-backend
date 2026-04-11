package com.springboot.smartcampusoperationshub.service;

import com.springboot.smartcampusoperationshub.model.Resource;
import com.springboot.smartcampusoperationshub.model.enums.ResourceStatus;
import com.springboot.smartcampusoperationshub.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * AI Recommendation Engine using a Weighted Content-Based Filtering Algorithm.
 */
@Service
public class RecommendationService {

    private final ResourceRepository resourceRepository;

    public RecommendationService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public List<Resource> getRecommendations(String intent) {
        if (intent == null || intent.trim().isEmpty()) {
            return List.of(); // Return empty if no intent provided
        }

        String lowerIntent = intent.toLowerCase();
        int requestedCapacity = extractCapacity(lowerIntent);

        // Fetch all active resources to score them
        List<Resource> allActiveResources = resourceRepository.findAll().stream()
                .filter(r -> r.getStatus() == ResourceStatus.ACTIVE)
                .collect(Collectors.toList());

        // Score, sort, and return the top 5 recommendations
        return allActiveResources.stream()
                .map(resource -> new ResourceScore(resource, calculateScore(resource, lowerIntent, requestedCapacity)))
                .filter(rs -> rs.score > 0) // Only return relevant items
                .sorted(Comparator.comparingInt(ResourceScore::getScore).reversed())
                .limit(5)
                .map(ResourceScore::getResource)
                .collect(Collectors.toList());
    }

    private int extractCapacity(String intent) {
        // Regex to find a number followed by words like "people", "students", "capacity"
        Pattern pattern = Pattern.compile("(\\d+)\\s*(people|students|persons|capacity|seats)");
        Matcher matcher = pattern.matcher(intent);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0; // Default if no capacity mentioned
    }

    private int calculateScore(Resource resource, String intent, int requestedCapacity) {
        int score = 0;

        // 1. Capacity Constraint (High Priority)
        if (requestedCapacity > 0) {
            if (resource.getCapacity() >= requestedCapacity && resource.getCapacity() <= requestedCapacity + 20) {
                score += 50; // Perfect size
            } else if (resource.getCapacity() >= requestedCapacity) {
                score += 20; // Too big, but fits
            } else {
                return 0; // Immediate disqualification if it's too small
            }
        }

        // 2. Type Matching
        if (intent.contains(resource.getType().name().toLowerCase().replace("_", " "))) {
            score += 30;
        }

        // 3. Amenities NLP Matching (Feature Extraction)
        for (String amenity : resource.getAmenities()) {
            if (intent.contains(amenity.toLowerCase())) {
                score += 25; // Boost score for every requested equipment match
            }
        }

        // 4. Historical Popularity (Tie-breaker based on past usage)
        score += resource.getHistoricalPopularity();

        return score;
    }

    // Helper class to map resources to their calculated AI score
    private static class ResourceScore {
        Resource resource;
        int score;

        ResourceScore(Resource resource, int score) {
            this.resource = resource;
            this.score = score;
        }

        int getScore() {
            return score;
        }
        Resource getResource() {
            return resource;
        }
    }
}
