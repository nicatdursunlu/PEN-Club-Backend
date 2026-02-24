package com.penclub.backend.event.dto;

import com.penclub.backend.event.entity.Event;
import com.penclub.backend.event.entity.EventCategory;
import com.penclub.backend.event.entity.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDto {

    private UUID id;
    private String slug;
    private String title;
    private LocalDate date;
    private String time;
    private String location;
    private EventCategory category;
    private EventStatus status;
    private String description;
    private String fullDescription;
    private String goal;
    private String organizer;
    private String locationLink;
    private String mapEmbedUrl;
    private String specialNote;
    private Integer attendees;
    private List<String> highlights;
    private List<Event.EventImage> images;
    private List<Event.AgendaItem> agenda;
    private List<String> eventHighlights;
    private Event.Pricing pricing;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Static factory method to map an Event entity to EventResponseDto.
     */
    public static EventResponseDto fromEntity(Event event) {
        return EventResponseDto.builder()
                .id(event.getId())
                .slug(event.getSlug())
                .title(event.getTitle())
                .date(event.getDate())
                .time(event.getTime())
                .location(event.getLocation())
                .category(event.getCategory())
                .status(event.getStatus())
                .description(event.getDescription())
                .fullDescription(event.getFullDescription())
                .goal(event.getGoal())
                .organizer(event.getOrganizer())
                .locationLink(event.getLocationLink())
                .mapEmbedUrl(event.getMapEmbedUrl())
                .specialNote(event.getSpecialNote())
                .attendees(event.getAttendees())
                .highlights(event.getHighlights())
                .images(event.getImages())
                .agenda(event.getAgenda())
                .eventHighlights(event.getEventHighlights())
                .pricing(event.getPricing())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
