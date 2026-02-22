package com.penclub.backend.event.dto;

import com.penclub.backend.event.entity.Event;
import com.penclub.backend.event.entity.EventCategory;
import com.penclub.backend.event.entity.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDto {
    private String slug;
    private String title;
    private String date;
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
}