package com.penclub.backend.event.service;

import com.penclub.backend.event.dto.EventRequestDto;
import com.penclub.backend.event.dto.EventResponseDto;
import com.penclub.backend.event.dto.EventSummaryDto;
import com.penclub.backend.event.entity.Event;
import com.penclub.backend.event.entity.EventCategory;
import com.penclub.backend.event.entity.EventStatus;
import com.penclub.backend.event.repository.EventRepository;
import com.penclub.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    private final EventRepository eventRepository;

    public List<EventSummaryDto> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(EventSummaryDto::fromEntity)
                .toList();
    }

    public List<EventSummaryDto> getUpcomingEvents() {
        LocalDate today = LocalDate.now();
        return eventRepository.findAll()
                .stream()
                .filter(event -> isUpcoming(event, today))
                .sorted(Comparator.comparing(event -> resolveEndDateTime(event)))
                .map(EventSummaryDto::fromEntity)
                .toList();
    }

    public List<EventSummaryDto> getPastEvents() {
        LocalDate today = LocalDate.now();
        return eventRepository.findAll()
                .stream()
                .filter(event -> !isUpcoming(event, today))
                .sorted(Comparator.comparing((Event event) -> resolveEndDateTime(event)).reversed())
                .map(EventSummaryDto::fromEntity)
                .toList();
    }

    /**
     * Determines if an event is upcoming.
     * An event is upcoming if its date is today or in the future.
     * If the date is today, checks the end time to see if the event has already ended.
     */
    private boolean isUpcoming(Event event, LocalDate today) {
        if (event.getDate() == null) {
            return event.getStatus() == EventStatus.UPCOMING;
        }
        LocalDate eventDate = event.getDate();
        if (eventDate.isAfter(today)) {
            return true;
        }
        if (eventDate.isBefore(today)) {
            return false;
        }
        // Same day — check end time
        LocalDateTime now = LocalDateTime.now();
        return resolveEndDateTime(event).isAfter(now);
    }

    /**
     * Resolves the event's end date+time as a LocalDateTime for sorting.
     * Uses the end part of the time range (e.g. "3:00 PM - 6:00 PM" → 6:00 PM).
     * Falls back to end-of-day if time is null or unparseable.
     */
    private LocalDateTime resolveEndDateTime(Event event) {
        LocalDate date = event.getDate() != null ? event.getDate() : LocalDate.now();
        LocalTime endTime = LocalTime.MAX;

        if (event.getTime() != null && !event.getTime().isBlank()) {
            try {
                String timeStr = event.getTime().trim();
                // Extract end time from "3:00 PM - 6:00 PM" → "6:00 PM"
                String endPart = timeStr.contains("-")
                        ? timeStr.substring(timeStr.lastIndexOf('-') + 1).trim()
                        : timeStr.trim();
                endTime = LocalTime.parse(endPart, TIME_FORMATTER);
            } catch (Exception e) {
                log.warn("Could not parse time for event '{}': time='{}'", event.getSlug(), event.getTime());
            }
        }

        return LocalDateTime.of(date, endTime);
    }

    public EventResponseDto getEventBySlug(String slug) {
        Event event = eventRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with slug: " + slug));
        return EventResponseDto.fromEntity(event);
    }

    @Transactional
    public EventResponseDto createEvent(EventRequestDto request) {
        if (eventRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Event with slug '" + request.getSlug() + "' already exists");
        }

        Event event = Event.builder()
                .slug(request.getSlug())
                .title(request.getTitle())
                .date(request.getDate())
                .time(request.getTime())
                .location(request.getLocation())
                .category(request.getCategory() != null ? request.getCategory() : EventCategory.ENJOY)
                .status(request.getStatus() != null ? request.getStatus() : EventStatus.UPCOMING)
                .description(request.getDescription())
                .fullDescription(request.getFullDescription())
                .goal(request.getGoal())
                .organizer(request.getOrganizer())
                .locationLink(request.getLocationLink())
                .mapEmbedUrl(request.getMapEmbedUrl())
                .specialNote(request.getSpecialNote())
                .attendees(request.getAttendees())
                .highlights(request.getHighlights())
                .images(request.getImages())
                .agenda(request.getAgenda())
                .eventHighlights(request.getEventHighlights())
                .pricing(request.getPricing())
                .build();

        return EventResponseDto.fromEntity(eventRepository.save(event));
    }

    @Transactional
    public EventResponseDto updateEvent(String slug, EventRequestDto request) {
        Event existingEvent = eventRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with slug: " + slug));

        // Update fields if provided
        if (request.getTitle() != null) {
            existingEvent.setTitle(request.getTitle());
        }
        if (request.getDate() != null) {
            existingEvent.setDate(request.getDate());
        }
        if (request.getTime() != null) {
            existingEvent.setTime(request.getTime());
        }
        if (request.getLocation() != null) {
            existingEvent.setLocation(request.getLocation());
        }
        if (request.getCategory() != null) {
            existingEvent.setCategory(request.getCategory());
        }
        if (request.getStatus() != null) {
            existingEvent.setStatus(request.getStatus());
        }
        if (request.getDescription() != null) {
            existingEvent.setDescription(request.getDescription());
        }
        if (request.getFullDescription() != null) {
            existingEvent.setFullDescription(request.getFullDescription());
        }
        if (request.getGoal() != null) {
            existingEvent.setGoal(request.getGoal());
        }
        if (request.getOrganizer() != null) {
            existingEvent.setOrganizer(request.getOrganizer());
        }
        if (request.getLocationLink() != null) {
            existingEvent.setLocationLink(request.getLocationLink());
        }
        if (request.getMapEmbedUrl() != null) {
            existingEvent.setMapEmbedUrl(request.getMapEmbedUrl());
        }
        if (request.getSpecialNote() != null) {
            existingEvent.setSpecialNote(request.getSpecialNote());
        }
        if (request.getAttendees() != null) {
            existingEvent.setAttendees(request.getAttendees());
        }
        if (request.getHighlights() != null) {
            existingEvent.setHighlights(request.getHighlights());
        }
        if (request.getImages() != null) {
            existingEvent.setImages(request.getImages());
        }
        if (request.getAgenda() != null) {
            existingEvent.setAgenda(request.getAgenda());
        }
        if (request.getEventHighlights() != null) {
            existingEvent.setEventHighlights(request.getEventHighlights());
        }
        if (request.getPricing() != null) {
            existingEvent.setPricing(request.getPricing());
        }

        return EventResponseDto.fromEntity(eventRepository.save(existingEvent));
    }

    @Transactional
    public void deleteEvent(String slug) {
        Event event = eventRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with slug: " + slug));
        eventRepository.delete(event);
    }
}