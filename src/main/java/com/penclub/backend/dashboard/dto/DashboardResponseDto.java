package com.penclub.backend.dashboard.dto;

import com.penclub.backend.event.dto.EventSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDto {

    // ── Event Stats ──────────────────────────────────────────────────────────
    private long totalEvents;
    private long upcomingEventsCount;
    private long pastEventsCount;

    /** Count of events grouped by category name (e.g. {"PLAY": 3, "ENJOY": 2, "NETWORK": 1}) */
    private Map<String, Long> eventsByCategory;

    /** Count of events grouped by status name (e.g. {"DRAFT": 1, "PUBLISHED": 4, "CANCELLED": 1, "COMPLETED": 2}) */
    private Map<String, Long> eventsByStatus;

    /** Count of events grouped by event month using yyyy-MM keys */
    private Map<String, Long> eventsPerMonth;

    /** Sum of attendees across all events that have an attendee count recorded */
    private int totalAttendees;

    /** Active registrations grouped by event, ready for a horizontal bar chart */
    private List<EventRegistrationMetricDto> registrationsPerEvent;

    /** Average rating across all submitted event feedback */
    private double averageFeedbackRating;

    /** Rating counts grouped by rating value from 1 to 5 */
    private Map<Integer, Long> ratingDistribution;

    /** Average rating grouped by event */
    private List<EventRatingMetricDto> averageRatingByEvent;

    /** Member account growth grouped by registration month using yyyy-MM keys */
    private Map<String, Long> memberGrowth;

    /** Ranked event table combining active registration count and average rating */
    private List<PopularEventDto> mostPopularEvents;

    /** The next upcoming event (sorted by start date ascending), null if none */
    private EventSummaryDto nextEvent;

    /** The most recently completed event (sorted by end date descending), null if none */
    private EventSummaryDto mostRecentEvent;

    // ── Member Stats ─────────────────────────────────────────────────────────
    private long totalMembers;
    private long totalAdmins;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventRegistrationMetricDto {
        private String eventSlug;
        private String eventTitle;
        private long registrations;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventRatingMetricDto {
        private String eventSlug;
        private String eventTitle;
        private double averageRating;
        private long feedbackCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PopularEventDto {
        private String eventSlug;
        private String eventTitle;
        private long registrations;
        private double averageRating;
        private long feedbackCount;
    }
}
