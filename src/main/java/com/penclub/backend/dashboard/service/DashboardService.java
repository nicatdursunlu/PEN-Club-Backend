package com.penclub.backend.dashboard.service;

import com.penclub.backend.dashboard.dto.DashboardResponseDto;
import com.penclub.backend.event.dto.EventSummaryDto;
import com.penclub.backend.event.entity.Event;
import com.penclub.backend.event.repository.EventRepository;
import com.penclub.backend.event.service.EventService;
import com.penclub.backend.user.entity.Role;
import com.penclub.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final EventRepository eventRepository;
    private final EventService eventService;
    private final UserRepository userRepository;

    public DashboardResponseDto getDashboard() {
        log.info("Building dashboard stats");

        List<Event> allEvents = eventRepository.findAll();

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
                        Collectors.counting()
                ));

        // ── Total attendees ──────────────────────────────────────────────────
        int totalAttendees = allEvents.stream()
                .filter(e -> e.getAttendees() != null)
                .mapToInt(Event::getAttendees)
                .sum();

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
                .totalAttendees(totalAttendees)
                .nextEvent(nextEvent)
                .mostRecentEvent(mostRecentEvent)
                .totalMembers(totalMembers)
                .totalAdmins(totalAdmins)
                .build();
    }
}
