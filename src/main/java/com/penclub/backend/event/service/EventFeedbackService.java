package com.penclub.backend.event.service;

import com.penclub.backend.event.dto.EventFeedbackDto;
import com.penclub.backend.event.dto.EventFeedbackRequestDto;
import com.penclub.backend.event.entity.Event;
import com.penclub.backend.event.entity.EventFeedback;
import com.penclub.backend.event.entity.EventStatus;
import com.penclub.backend.event.repository.EventFeedbackRepository;
import com.penclub.backend.event.repository.EventParticipantRepository;
import com.penclub.backend.event.repository.EventRepository;
import com.penclub.backend.exception.ResourceNotFoundException;
import com.penclub.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventFeedbackService {

    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;
    private final EventFeedbackRepository eventFeedbackRepository;

    @Transactional
    public EventFeedbackDto submitFeedback(String slug, EventFeedbackRequestDto request, User currentUser) {
        Event event = eventRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with slug: " + slug));

        validateFeedbackAllowed(event, slug, currentUser);

        EventFeedback feedback = eventFeedbackRepository.findByEventAndUser(event, currentUser)
                .orElseGet(() -> EventFeedback.builder()
                        .event(event)
                        .user(currentUser)
                        .build());

        feedback.setRating(request.getRating());
        feedback.setComment(normalizeComment(request.getComment()));

        log.info("User '{}' submitted feedback for event '{}'", currentUser.getEmail(), slug);
        return EventFeedbackDto.fromEntity(eventFeedbackRepository.save(feedback));
    }

    @Transactional(readOnly = true)
    public List<EventFeedbackDto> getEventFeedback(String slug) {
        Event event = eventRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with slug: " + slug));

        return eventFeedbackRepository.findAllByEventOrderByCreatedAtAsc(event)
                .stream()
                .map(EventFeedbackDto::fromEntity)
                .toList();
    }

    private void validateFeedbackAllowed(Event event, String slug, User currentUser) {
        if (event.getStatus() == EventStatus.CANCELLED || event.getStatus() == EventStatus.DRAFT) {
            throw new IllegalArgumentException("Cannot submit feedback for an unpublished event: " + slug);
        }
        if (!hasEventEnded(event)) {
            throw new IllegalArgumentException("Cannot submit feedback before event has ended: " + slug);
        }
        if (!eventParticipantRepository.existsByEventAndUserAndRegisteredTrue(event, currentUser)) {
            throw new IllegalArgumentException("Only registered participants can submit feedback for event: " + slug);
        }
    }

    private boolean hasEventEnded(Event event) {
        return event.getStatus() == EventStatus.COMPLETED
                || (event.getDate() != null && event.getDate().isBefore(LocalDate.now()));
    }

    private String normalizeComment(String comment) {
        if (comment == null || comment.isBlank()) {
            return null;
        }
        return comment.trim();
    }
}
