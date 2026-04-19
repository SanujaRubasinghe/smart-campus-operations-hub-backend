package com.springboot.smartcampusoperationshub.model;

import com.springboot.smartcampusoperationshub.model.enums.ResourceStatus;
import com.springboot.smartcampusoperationshub.model.enums.ResourceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a physical or non-physical bookable asset.
 * Includes a composite index to optimize read-heavy filtering operations.
 */
@Entity
@Table(name = "resources", indexes = {
    @Index(name = "idx_resource_search", columnList = "type, location, capacity")
})
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, length = 64)
    private String qrSecret;

    @NotBlank(message = "Resource name is mandatory")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Resource type must be specified")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType type;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private String location;

    // Stores amenities as a native JSON structure in the database for advanced querying
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> amenities = new ArrayList<>();

    // Tracks booking frequency to weight the AI recommendation engine's sorting algorithm
    @Column(name = "historical_popularity", nullable = false)
    private Integer historicalPopularity = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceStatus status = ResourceStatus.ACTIVE;

    @Column(name = "image_url")
    private String imageUrl;

    // Constructors
    public Resource() {}

    public Resource(String name, ResourceType type, Integer capacity, String location, List<String> amenities) {
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.location = location;
        this.amenities = amenities != null ? amenities : new ArrayList<>();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getQrSecret() {
        return qrSecret;
    }

    public void setQrSecret(String qrSecret) {
        this.qrSecret = qrSecret;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public ResourceType getType() { return type; }
    public void setType(ResourceType type) { this.type = type; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }
    
    public Integer getHistoricalPopularity() { return historicalPopularity; }
    public void setHistoricalPopularity(Integer historicalPopularity) { this.historicalPopularity = historicalPopularity; }
    
    public ResourceStatus getStatus() { return status; }
    public void setStatus(ResourceStatus status) { this.status = status; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
