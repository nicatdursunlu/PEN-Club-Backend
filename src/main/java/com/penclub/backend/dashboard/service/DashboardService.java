package com.penclub.backend.dashboard.service;

import com.penclub.backend.dashboard.dto.DashboardResponseDto;
import com.penclub.backend.event.dto.EventSummaryDto;
import com.penclub.backend.event.entity.Event;
import com.penclub.backend.event.entity.EventCategory;
import com.penclub.backend.event.entity.EventFeedback;
import com.penclub.backend.event.entity.EventParticipant;
import com.penclub.backend.event.entity.EventStatus;
import com.penclub.backend.event.repository.EventFeedbackRepository;
import com.penclub.backend.event.repository.EventParticipantRepository;
import com.penclub.backend.event.repository.EventRepository;
import com.penclub.backend.event.service.EventService;
import com.penclub.backend.user.entity.Role;
import com.penclub.backend.user.entity.User;
import com.penclub.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final EventFeedbackRepository eventFeedbackRepository;
    private final EventService eventService;
    private final UserRepository userRepository;

    public DashboardResponseDto getDashboard() {
        log.info("Building dashboard stats");

        List<Event> allEvents = eventRepository.findAll();
        List<EventParticipant> allParticipants = eventParticipantRepository.findAll();
        List<EventFeedback> allFeedback = eventFeedbackRepository.findAll();
        List<User> allUsers = userRepository.findAll();

        // ── Event counts ─────────────────────────────────────────────────────
        long totalEvents = allEvents.size();

        // Reuse EventService's date-aware filtering (with EventStatus fallback)
        List<EventSummaryDto> upcomingList = eventService.getUpcomingEvents();
        List<EventSummaryDto> pastList = eventService.getPastEvents();

        long upcomingEventsCount = upcomingList.size();
        long pastEventsCount = pastList.size();

        // ── Events by category ───────────────────────────────────────────────
        Map<String, Long> eventsByCategory = allEvents.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCategory().name(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        Arrays.stream(EventCategory.values()).forEach(category ->
                eventsByCategory.putIfAbsent(category.name(), 0L)
        );

        Map<String, Long> eventsByStatus = allEvents.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getStatus().name(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        Arrays.stream(EventStatus.values()).forEach(status ->
                eventsByStatus.putIfAbsent(status.name(), 0L)
        );

        Map<String, Long> eventsPerMonth = allEvents.stream()
                .filter(event -> event.getDate() != null)
                .collect(Collectors.groupingBy(
                        event -> event.getDate().format(MONTH_FORMATTER),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        // ── Total attendees ──────────────────────────────────────────────────
        int totalAttendees = allEvents.stream()
                .filter(e -> e.getAttendees() != null)
                .mapToInt(Event::getAttendees)
                .sum();

        Map<UUID, Long> registrationsByEventId = allParticipants.stream()
                .filter(EventParticipant::isRegistered)
                .filter(participant -> participant.getEvent() != null && participant.getEvent().getId() != null)
                .collect(Collectors.groupingBy(
                        participant -> participant.getEvent().getId(),
                        Collectors.counting()
                ));

        List<DashboardResponseDto.EventRegistrationMetricDto> registrationsPerEvent = allEvents.stream()
                .map(event -> DashboardResponseDto.EventRegistrationMetricDto.builder()
                        .eventSlug(event.getSlug())
                        .eventTitle(event.getTitle())
                        .registrations(registrationsByEventId.getOrDefault(event.getId(), 0L))
                        .build())
                .sorted(Comparator
                        .comparingLong(DashboardResponseDto.EventRegistrationMetricDto::getRegistrations)
                        .reversed()
                        .thenComparing(DashboardResponseDto.EventRegistrationMetricDto::getEventTitle, Comparator.nullsLast(String::compareTo)))
                .toList();

        double averageFeedbackRating = roundOneDecimal(allFeedback.stream()
                .mapToInt(EventFeedback::getRating)
                .average()
                .orElse(0.0));

        Map<Integer, Long> ratingDistribution = allFeedback.stream()
                .collect(Collectors.groupingBy(
                        EventFeedback::getRating,
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        for (int rating = 1; rating <= 5; rating++) {
            ratingDistribution.putIfAbsent(rating, 0L);
        }

        ratingDistribution = ratingDistribution.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        Map<UUID, List<EventFeedback>> feedbackByEventId = allFeedback.stream()
                .filter(feedback -> feedback.getEvent() != null && feedback.getEvent().getId() != null)
                .collect(Collectors.groupingBy(feedback -> feedback.getEvent().getId()));

        List<DashboardResponseDto.EventRatingMetricDto> averageRatingByEvent = allEvents.stream()
                .map(event -> {
                    List<EventFeedback> eventFeedback = feedbackByEventId.getOrDefault(event.getId(), List.of());
                    double averageRating = roundOneDecimal(eventFeedback.stream()
                            .mapToInt(EventFeedback::getRating)
                            .average()
                            .orElse(0.0));

                    return DashboardResponseDto.EventRatingMetricDto.builder()
                            .eventSlug(event.getSlug())
                            .eventTitle(event.getTitle())
                            .averageRating(averageRating)
                            .feedbackCount(eventFeedback.size())
                            .build();
                })
                .sorted(Comparator
                        .comparingDouble(DashboardResponseDto.EventRatingMetricDto::getAverageRating)
                        .reversed()
                        .thenComparing(Comparator.comparingLong(DashboardResponseDto.EventRatingMetricDto::getFeedbackCount).reversed())
                        .thenComparing(DashboardResponseDto.EventRatingMetricDto::getEventTitle, Comparator.nullsLast(String::compareTo)))
                .toList();

        Map<String, Long> memberGrowth = allUsers.stream()
                .filter(user -> user.getRole() == Role.MEMBER)
                .filter(user -> user.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        user -> YearMonth.from(user.getCreatedAt()).format(MONTH_FORMATTER),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        List<DashboardResponseDto.PopularEventDto> mostPopularEvents = allEvents.stream()
                .map(event -> {
                    List<EventFeedback> eventFeedback = feedbackByEventId.getOrDefault(event.getId(), List.of());
                    double averageRating = roundOneDecimal(eventFeedback.stream()
                            .mapToInt(EventFeedback::getRating)
                            .average()
                            .orElse(0.0));

                    return DashboardResponseDto.PopularEventDto.builder()
                            .eventSlug(event.getSlug())
                            .eventTitle(event.getTitle())
                            .registrations(registrationsByEventId.getOrDefault(event.getId(), 0L))
                            .averageRating(averageRating)
                            .feedbackCount(eventFeedback.size())
                            .build();
                })
                .sorted(Comparator
                        .comparingLong(DashboardResponseDto.PopularEventDto::getRegistrations)
                        .reversed()
                        .thenComparing(DashboardResponseDto.PopularEventDto::getAverageRating, Comparator.reverseOrder())
                        .thenComparing(DashboardResponseDto.PopularEventDto::getEventTitle, Comparator.nullsLast(String::compareTo)))
                .toList();

        // ── Next & most recent events ────────────────────────────────────────
        // upcomingList is sorted ascending → first element is the next event
        EventSummaryDto nextEvent = upcomingList.isEmpty() ? null : upcomingList.get(0);

        // pastList is sorted descending → first element is the most recent past event
        EventSummaryDto mostRecentEvent = pastList.isEmpty() ? null : pastList.get(0);

        // ── Member stats ─────────────────────────────────────────────────────
        long totalMembers = userRepository.countByRole(Role.MEMBER);
        long totalAdmins = userRepository.countByRole(Role.ADMIN);

        return DashboardResponseDto.builder()
                .totalEvents(totalEvents)
                .upcomingEventsCount(upcomingEventsCount)
                .pastEventsCount(pastEventsCount)
                .eventsByCategory(eventsByCategory)
                .eventsByStatus(eventsByStatus)
                .eventsPerMonth(eventsPerMonth)
                .totalAttendees(totalAttendees)
                .registrationsPerEvent(registrationsPerEvent)
                .averageFeedbackRating(averageFeedbackRating)
                .ratingDistribution(ratingDistribution)
                .averageRatingByEvent(averageRatingByEvent)
                .memberGrowth(memberGrowth)
                .mostPopularEvents(mostPopularEvents)
                .nextEvent(nextEvent)
                .mostRecentEvent(mostRecentEvent)
                .totalMembers(totalMembers)
                .totalAdmins(totalAdmins)
                .build();
    }

    private double roundOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
