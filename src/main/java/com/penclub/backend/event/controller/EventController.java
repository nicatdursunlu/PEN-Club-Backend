package com.penclub.backend.event.controller;

import com.penclub.backend.event.dto.EventFeedbackDto;
import com.penclub.backend.event.dto.EventFeedbackRequestDto;
import com.penclub.backend.event.dto.EventRequestDto;
import com.penclub.backend.event.dto.EventParticipantDto;
import com.penclub.backend.event.dto.EventResponseDto;
import com.penclub.backend.event.dto.EventSummaryDto;
import com.penclub.backend.event.entity.EventCategory;
import com.penclub.backend.event.entity.EventStatus;
import com.penclub.backend.event.service.EventFeedbackService;
import com.penclub.backend.event.service.EventService;
import com.penclub.backend.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Event management endpoints")
public class EventController {

    private final EventService eventService;
    private final EventFeedbackService eventFeedbackService;

    @GetMapping
    @Operation(summary = "Get published events", description = "Retrieves publicly visible published events")
    public ResponseEntity<List<EventSummaryDto>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming events", description = "Retrieves published events that have not yet ended, sorted by date ascending")
    public ResponseEntity<List<EventSummaryDto>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @GetMapping("/past")
    @Operation(summary = "Get past events", description = "Retrieves published events that have already ended, sorted by date descending")
    public ResponseEntity<List<EventSummaryDto>> getPastEvents() {
        return ResponseEntity.ok(eventService.getPastEvents());
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all events for admin",
            description = "Retrieves paginated events for management, optionally filtered by status and category. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Page<EventResponseDto>> getAllEventsForAdmin(
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) EventCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        int resolvedPage = Math.max(page, 0);
        int resolvedSize = Math.min(Math.max(size, 1), 100);
        PageRequest pageable = PageRequest.of(resolvedPage, resolvedSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(eventService.getAllEventsForAdmin(status, category, pageable));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get event by slug", description = "Retrieves a published event by its unique slug")
    public ResponseEntity<EventResponseDto> getEventBySlug(
            @PathVariable String slug,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(eventService.getEventBySlug(slug, currentUser));
    }

    @GetMapping("/{slug}/participants")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get event participants",
            description = "Returns all participant records for an event, including users who unregistered and their unregistration timestamp. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<EventParticipantDto>> getEventParticipants(@PathVariable String slug) {
        return ResponseEntity.ok(eventService.getEventParticipants(slug));
    }

    @GetMapping("/{slug}/feedback")
    @Operation(
            summary = "Get event feedback",
            description = "Returns participant feedback for an event."
    )
    public ResponseEntity<List<EventFeedbackDto>> getEventFeedback(@PathVariable String slug) {
        return ResponseEntity.ok(eventFeedbackService.getEventFeedback(slug));
    }

    @PostMapping("/{slug}/register")
    @Operation(
            summary = "Register current user for an event",
            description = "Registers the authenticated user for the event and increments attendees by one.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<EventResponseDto> registerForEvent(
            @PathVariable String slug,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(eventService.registerForEvent(slug, currentUser));
    }

    @DeleteMapping("/{slug}/register")
    @Operation(
            summary = "Unregister current user from an event",
            description = "Unregisters the authenticated user from the event and decrements attendees by one.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<EventResponseDto> unregisterFromEvent(
            @PathVariable String slug,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(eventService.unregisterFromEvent(slug, currentUser));
    }

    @PostMapping("/{slug}/feedback")
    @Operation(
            summary = "Submit event feedback",
            description = "Submits or updates the authenticated participant's rating and optional comment after an event.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<EventFeedbackDto> submitEventFeedback(
            @PathVariable String slug,
            @Valid @RequestBody EventFeedbackRequestDto request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(eventFeedbackService.submitFeedback(slug, request, currentUser));
    }

    @PostMapping
    @Operation(summary = "Create a new event", description = "Creates a new event with the provided details")
    public ResponseEntity<EventResponseDto> createEvent(@RequestBody EventRequestDto request) {
        EventResponseDto createdEvent = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @PutMapping("/{slug}")
    @Operation(summary = "Update an event", description = "Updates an existing event by its slug")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable String slug, @RequestBody EventRequestDto request) {
        EventResponseDto updatedEvent = eventService.updateEvent(slug, request);
        return ResponseEntity.ok(updatedEvent);
    }

    @PostMapping("/{slug}/clone")
    @Operation(
            summary = "Clone an event",
            description = "Creates a new draft event by copying an existing event without participants",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<EventResponseDto> cloneEvent(@PathVariable String slug) {
        EventResponseDto clonedEvent = eventService.cloneEvent(slug);
        return ResponseEntity.status(HttpStatus.CREATED).body(clonedEvent);
    }

    @PatchMapping("/{slug}/publish")
    @Operation(
            summary = "Publish an event",
            description = "Changes an event status from draft to published so it becomes publicly visible",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<EventResponseDto> publishEvent(@PathVariable String slug) {
        return ResponseEntity.ok(eventService.publishEvent(slug));
    }

    @PatchMapping("/{slug}/cancel")
    @Operation(
            summary = "Cancel an event",
            description = "Changes an event status to cancelled so it is no longer publicly visible",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<EventResponseDto> cancelEvent(@PathVariable String slug) {
        return ResponseEntity.ok(eventService.cancelEvent(slug));
    }

    @DeleteMapping("/{slug}")
    @Operation(summary = "Delete an event", description = "Deletes an event by its slug")
    public ResponseEntity<Void> deleteEvent(@PathVariable String slug) {
        eventService.deleteEvent(slug);
        return ResponseEntity.noContent().build();
    }
}
