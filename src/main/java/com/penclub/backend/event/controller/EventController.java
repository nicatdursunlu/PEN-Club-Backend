package com.penclub.backend.event.controller;

import com.penclub.backend.event.dto.EventRequestDto;
import com.penclub.backend.event.dto.EventResponseDto;
import com.penclub.backend.event.dto.EventSummaryDto;
import com.penclub.backend.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Event management endpoints")
public class EventController {

    private final EventService eventService;

    @GetMapping
    @Operation(summary = "Get all events", description = "Retrieves a list of all events")
    public ResponseEntity<List<EventSummaryDto>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming events", description = "Retrieves events that have not yet ended, sorted by date ascending")
    public ResponseEntity<List<EventSummaryDto>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @GetMapping("/past")
    @Operation(summary = "Get past events", description = "Retrieves events that have already ended, sorted by date descending")
    public ResponseEntity<List<EventSummaryDto>> getPastEvents() {
        return ResponseEntity.ok(eventService.getPastEvents());
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get event by slug", description = "Retrieves an event by its unique slug")
    public ResponseEntity<EventResponseDto> getEventBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(eventService.getEventBySlug(slug));
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

    @DeleteMapping("/{slug}")
    @Operation(summary = "Delete an event", description = "Deletes an event by its slug")
    public ResponseEntity<Void> deleteEvent(@PathVariable String slug) {
        eventService.deleteEvent(slug);
        return ResponseEntity.noContent().build();
    }
}