package com.springboot.smartcampusoperationshub.service;

import com.springboot.smartcampusoperationshub.dto.bookings.BookingAlternativeDTO;
import com.springboot.smartcampusoperationshub.dto.bookings.BookingRequestDTO;
import com.springboot.smartcampusoperationshub.model.Booking;
import com.springboot.smartcampusoperationshub.model.Resource;
import com.springboot.smartcampusoperationshub.model.enums.ResourceStatus;
import com.springboot.smartcampusoperationshub.repository.BookingRepository;
import com.springboot.smartcampusoperationshub.repository.ResourceRepository;
import com.springboot.smartcampusoperationshub.util.AlternativeScorer;
import com.springboot.smartcampusoperationshub.util.TimeGapFinder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlternativeRecommendationService {

    private static final int MAX_ALTERNATIVES = 3;
    private static final int CANDIDATE_POOL_SIZE = 30; // generate this many, then pick top 3

    private final BookingRepository bookingRepo;
    private final ResourceRepository resourceRepo;
    private final AlternativeScorer scorer = new AlternativeScorer();
    private final TimeGapFinder gapFinder = new TimeGapFinder();

    public AlternativeRecommendationService(BookingRepository bookingRepo,
                                            ResourceRepository resourceRepo) {
        this.bookingRepo = bookingRepo;
        this.resourceRepo = resourceRepo;
    }

    public List<BookingAlternativeDTO> findAlternatives(BookingRequestDTO request) {
        Resource original = resourceRepo.findById(request.getResourceId())
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        Duration requestedDuration = Duration.between(request.getStartTime(), request.getEndTime());

        List<BookingAlternativeDTO> candidates = new ArrayList<>();

        candidates.addAll(findSameResourceShiftedTime(original, request, requestedDuration));

        candidates.addAll(findSameTimeDifferentResource(original, request));

        candidates.addAll(findRelaxedMatches(original, request));

        Map<String, BookingAlternativeDTO> dedup = new LinkedHashMap<>();
        for (BookingAlternativeDTO alt : candidates) {
            String key = alt.getResourceId() + "::" + alt.getStartTime();
            BookingAlternativeDTO existing = dedup.get(key);
            if (existing == null || alt.getScore() > existing.getScore()) {
                dedup.put(key, alt);
            }
        }

        return dedup.values().stream()
                .sorted(Comparator.comparingDouble(BookingAlternativeDTO::getScore).reversed())
                .limit(MAX_ALTERNATIVES)
                .collect(Collectors.toList());
    }

    private List<BookingAlternativeDTO> findSameResourceShiftedTime(
            Resource original, BookingRequestDTO request, Duration duration) {

        List<Booking> dayBookings = bookingRepo.findDayBookingsForResource(
                original.getId(), request.getBookingDate());

        List<TimeGapFinder.TimeSlot> freeSlots = gapFinder.findFreeSlots(
                dayBookings, request.getStartTime(), duration);

        List<BookingAlternativeDTO> results = new ArrayList<>();
        for (TimeGapFinder.TimeSlot slot : freeSlots.stream().limit(5).toList()) {
            AlternativeScorer.ScoredAlternative scored = scorer.score(
                    original, request, original, slot.start(), slot.end());

            BookingAlternativeDTO alt = buildDto(
                    original, request.getBookingDate(), slot.start(), slot.end(),
                    scored, BookingAlternativeDTO.AlternativeStrategy.SAME_RESOURCE_SHIFTED_TIME);
            results.add(alt);
        }
        return results;
    }

    private List<BookingAlternativeDTO> findSameTimeDifferentResource(
            Resource original, BookingRequestDTO request) {

        List<Resource> sameTypeResources = resourceRepo
                .findByTypeAndStatus(original.getType(), ResourceStatus.ACTIVE)
                .stream()
                .filter(r -> !r.getId().equals(original.getId()))
                .collect(Collectors.toList());

        return findFreeResourcesAtRequestedTime(original, request, sameTypeResources,
                BookingAlternativeDTO.AlternativeStrategy.SAME_TIME_DIFFERENT_RESOURCE);
    }

    private List<BookingAlternativeDTO> findRelaxedMatches(
            Resource original, BookingRequestDTO request) {

        List<Resource> allResources = resourceRepo.findByStatus(ResourceStatus.ACTIVE)
                .stream()
                .filter(r -> !r.getId().equals(original.getId()))
                .filter(r -> r.getType() != original.getType()) // different type only
                .collect(Collectors.toList());

        return findFreeResourcesAtRequestedTime(original, request, allResources,
                BookingAlternativeDTO.AlternativeStrategy.RELAXED_MATCH);
    }

    private List<BookingAlternativeDTO> findFreeResourcesAtRequestedTime(
            Resource original, BookingRequestDTO request,
            List<Resource> candidateResources,
            BookingAlternativeDTO.AlternativeStrategy strategy) {

        if (candidateResources.isEmpty()) return Collections.emptyList();

        List<UUID> ids = candidateResources.stream().map(Resource::getId).toList();

        List<Booking> busyBookings = bookingRepo.findBusyResources(
                ids, request.getBookingDate(), request.getStartTime(), request.getEndTime());

        Set<UUID> busyIds = busyBookings.stream()
                .map(b -> b.getResource().getId())
                .collect(Collectors.toSet());

        List<BookingAlternativeDTO> results = new ArrayList<>();
        for (Resource candidate : candidateResources) {
            if (busyIds.contains(candidate.getId())) continue;

            if (request.getExpectedAttendees() != null
                    && candidate.getCapacity() != null
                    && candidate.getCapacity() < request.getExpectedAttendees()) {
                continue;
            }

            AlternativeScorer.ScoredAlternative scored = scorer.score(
                    original, request, candidate,
                    request.getStartTime(), request.getEndTime());

            BookingAlternativeDTO alt = buildDto(
                    candidate, request.getBookingDate(),
                    request.getStartTime(), request.getEndTime(),
                    scored, strategy);
            results.add(alt);
        }

        results.sort(Comparator.comparingDouble(BookingAlternativeDTO::getScore).reversed());
        return results.stream().limit(CANDIDATE_POOL_SIZE).toList();
    }

    private BookingAlternativeDTO buildDto(Resource resource, java.time.LocalDate date,
                                           LocalTime start, LocalTime end,
                                           AlternativeScorer.ScoredAlternative scored,
                                           BookingAlternativeDTO.AlternativeStrategy strategy) {
        BookingAlternativeDTO dto = new BookingAlternativeDTO();
        dto.setResourceId(resource.getId());
        dto.setResourceName(resource.getName());
        dto.setResourceType(resource.getType().name());
        dto.setLocation(resource.getLocation());
        dto.setCapacity(resource.getCapacity());
        dto.setAmenities(resource.getAmenities());
        dto.setBookingDate(date);
        dto.setStartTime(start);
        dto.setEndTime(end);
        dto.setScore(Math.round(scored.score * 1000.0) / 1000.0); // round to 3 decimals
        dto.setReasons(scored.reasons);
        dto.setStrategy(strategy);
        return dto;
    }
}