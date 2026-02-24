package com.penclub.backend.dashboard.dto;

import com.penclub.backend.event.dto.EventSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    /** Sum of attendees across all events that have an attendee count recorded */
    private int totalAttendees;

    /** The next upcoming event (sorted by start date ascending), null if none */
    private EventSummaryDto nextEvent;

    /** The most recently completed event (sorted by end date descending), null if none */
    private EventSummaryDto mostRecentEvent;

    // ── Member Stats ─────────────────────────────────────────────────────────
    private long totalMembers;
    private long totalAdmins;
}
