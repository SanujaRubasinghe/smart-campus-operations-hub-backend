package com.springboot.smartcampusoperationshub.bootstrap;

import com.springboot.smartcampusoperationshub.model.Resource;
import com.springboot.smartcampusoperationshub.model.enums.ResourceType;
import com.springboot.smartcampusoperationshub.repository.ResourceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final ResourceRepository resourceRepository;

    public DatabaseSeeder(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (resourceRepository.count() == 0) {
            Resource r1 = new Resource("Main Auditorium", ResourceType.LECTURE_HALL, 300, "Block A", List.of("Projector", "PA System", "Stage", "AC"));
            r1.setHistoricalPopularity(85);

            Resource r2 = new Resource("Advanced Chemistry Lab", ResourceType.LAB, 40, "Block C", List.of("Fume Hoods", "Bunsen Burners", "Safety Showers"));
            r2.setHistoricalPopularity(60);

            Resource r3 = new Resource("Meeting Room Alpha", ResourceType.MEETING_ROOM, 10, "Library Level 2", List.of("Whiteboard", "Smart TV", "Video Conferencing"));
            r3.setHistoricalPopularity(95);

            Resource r4 = new Resource("Portable Projector Unit 1", ResourceType.EQUIPMENT, 1, "IT Helpdesk", List.of("HDMI", "VGA", "Laser Pointer"));
            r4.setHistoricalPopularity(40);

            Resource r5 = new Resource("Computer Lab 3", ResourceType.LAB, 60, "Block B", List.of("High-End GPUs", "Dual Monitors", "Linux workstations"));
            r5.setHistoricalPopularity(110);

            resourceRepository.saveAll(List.of(r1, r2, r3, r4, r5));
            System.out.println("Database seeded with initial resources.");
        }
    }
}
