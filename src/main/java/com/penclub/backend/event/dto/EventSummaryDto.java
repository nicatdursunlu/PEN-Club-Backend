package com.penclub.backend.event.dto;

import com.penclub.backend.event.entity.Event;
import com.penclub.backend.event.entity.EventCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSummaryDto {

    private String id;
    private String title;
    private LocalDate date;
    private String time;
    private String location;
    private String description;
    private EventCategory category;

    /**
     * Static factory method to map an Event entity to EventSummaryDto.
     * Used exclusively for the Get All Events endpoint.
     */
    public static EventSummaryDto fromEntity(Event event) {
        return EventSummaryDto.builder()
                .id(event.getSlug())
                .title(event.getTitle())
                .date(event.getDate())
                .time(event.getTime())
                .location(event.getLocation())
                .description(event.getDescription())
                .category(event.getCategory())
                .build();
    }
}
