package com.penclub.backend.event.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "date", nullable = false)
    private String date;

    @Column(name = "time")
    private String time;

    @Column(name = "location")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private EventCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "full_description", columnDefinition = "TEXT")
    private String fullDescription;

    @Column(name = "goal", columnDefinition = "TEXT")
    private String goal;

    @Column(name = "organizer")
    private String organizer;

    @Column(name = "location_link", columnDefinition = "TEXT")
    private String locationLink;

    @Column(name = "map_embed_url", columnDefinition = "TEXT")
    private String mapEmbedUrl;

    @Column(name = "special_note", columnDefinition = "TEXT")
    private String specialNote;

    @Column(name = "attendees")
    private Integer attendees;

    // JSON fields stored as TEXT
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "highlights", columnDefinition = "TEXT")
    private List<String> highlights;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "images", columnDefinition = "TEXT")
    private List<EventImage> images;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "agenda", columnDefinition = "TEXT")
    private List<AgendaItem> agenda;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "event_highlights", columnDefinition = "TEXT")
    private List<String> eventHighlights;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "pricing", columnDefinition = "TEXT")
    private Pricing pricing;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Nested classes for JSON fields
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EventImage {
        private String url;
        private String alt;
        private String caption;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AgendaItem {
        private String time;
        private String title;
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Pricing {
        private String full;
        private String basic;
    }
}