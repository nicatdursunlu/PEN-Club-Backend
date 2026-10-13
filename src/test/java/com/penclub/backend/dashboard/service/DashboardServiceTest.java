package com.penclub.backend.dashboard.service;

import com.penclub.backend.dashboard.dto.DashboardResponseDto;
import com.penclub.backend.event.dto.EventSummaryDto;
import com.penclub.backend.event.entity.Event;
import com.penclub.backend.event.entity.EventCategory;
import com.penclub.backend.event.entity.EventFeedback;
import com.penclub.backend.event.entity.EventParticipant;
import com.penclub.backend.event.entity.EventStatus;
import com.penclub.backend.event.repository.EventFeedbackRepository;
import com.penclub.backend.event.repository.EventParticipantRepository;
import com.penclub.backend.event.repository.EventRepository;
import com.penclub.backend.event.service.EventService;
import com.penclub.backend.user.entity.Role;
import com.penclub.backend.user.entity.User;
import com.penclub.backend.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventParticipantRepository eventParticipantRepository;

    @Mock
    private EventFeedbackRepository eventFeedbackRepository;

    @Mock
    private EventService eventService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getDashboardReturnsChartReadyAnalytics() {
        Event playEvent = event(
                "mafia-night",
                "Mafia Night",
                LocalDate.of(2026, 3, 22),
                EventCategory.ENJOY,
                EventStatus.COMPLETED,
                20
        );
        Event networkEvent = event(
                "networking",
                "Networking",
                LocalDate.of(2026, 3, 15),
                EventCategory.NETWORK,
                EventStatus.PUBLISHED,
                12
        );
        Event draftEvent = event(
                "draft-event",
                "Draft Event",
                LocalDate.of(2026, 4, 5),
                EventCategory.PLAY,
                EventStatus.DRAFT,
                null
        );

        User memberOne = member(LocalDateTime.of(2026, 1, 10, 9, 0));
        User memberTwo = member(LocalDateTime.of(2026, 1, 15, 9, 0));
        User memberThree = member(LocalDateTime.of(2026, 2, 10, 9, 0));
        User admin = user(Role.ADMIN, LocalDateTime.of(2026, 1, 1, 9, 0));

        EventParticipant activeParticipantOne = participant(playEvent, memberOne, true);
        EventParticipant activeParticipantTwo = participant(playEvent, memberTwo, true);
        EventParticipant inactiveParticipant = participant(playEvent, memberThree, false);
        EventParticipant networkParticipant = participant(networkEvent, memberThree, true);

        EventFeedback playFeedbackOne = feedback(playEvent, memberOne, 5);
        EventFeedback playFeedbackTwo = feedback(playEvent, memberTwo, 4);
        EventFeedback networkFeedback = feedback(networkEvent, memberThree, 3);

        when(eventRepository.findAll()).thenReturn(List.of(playEvent, networkEvent, draftEvent));
        when(eventParticipantRepository.findAll())
                .thenReturn(List.of(activeParticipantOne, activeParticipantTwo, inactiveParticipant, networkParticipant));
        when(eventFeedbackRepository.findAll()).thenReturn(List.of(playFeedbackOne, playFeedbackTwo, networkFeedback));
        when(userRepository.findAll()).thenReturn(List.of(memberOne, memberTwo, memberThree, admin));
        when(userRepository.countByRole(Role.MEMBER)).thenReturn(3L);
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(1L);
        when(eventService.getUpcomingEvents()).thenReturn(List.of(EventSummaryDto.fromEntity(networkEvent)));
        when(eventService.getPastEvents()).thenReturn(List.of(EventSummaryDto.fromEntity(playEvent)));

        DashboardResponseDto dashboard = dashboardService.getDashboard();

        assertThat(dashboard.getEventsByStatus())
                .containsEntry("DRAFT", 1L)
                .containsEntry("PUBLISHED", 1L)
                .containsEntry("CANCELLED", 0L)
                .containsEntry("COMPLETED", 1L);
        assertThat(dashboard.getEventsPerMonth())
                .containsEntry("2026-03", 2L)
                .containsEntry("2026-04", 1L);
        assertThat(dashboard.getRegistrationsPerEvent())
                .extracting(DashboardResponseDto.EventRegistrationMetricDto::getEventSlug,
                        DashboardResponseDto.EventRegistrationMetricDto::getRegistrations)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("mafia-night", 2L),
                        org.assertj.core.groups.Tuple.tuple("networking", 1L),
                        org.assertj.core.groups.Tuple.tuple("draft-event", 0L)
                );
        assertThat(dashboard.getAverageFeedbackRating()).isEqualTo(4.0);
        assertThat(dashboard.getRatingDistribution())
                .containsEntry(1, 0L)
                .containsEntry(2, 0L)
                .containsEntry(3, 1L)
                .containsEntry(4, 1L)
                .containsEntry(5, 1L);
        assertThat(dashboard.getAverageRatingByEvent())
                .extracting(DashboardResponseDto.EventRatingMetricDto::getEventSlug,
                        DashboardResponseDto.EventRatingMetricDto::getAverageRating,
                        DashboardResponseDto.EventRatingMetricDto::getFeedbackCount)
                .contains(
                        org.assertj.core.groups.Tuple.tuple("mafia-night", 4.5, 2L),
                        org.assertj.core.groups.Tuple.tuple("networking", 3.0, 1L),
                        org.assertj.core.groups.Tuple.tuple("draft-event", 0.0, 0L)
                );
        assertThat(dashboard.getMemberGrowth())
                .containsEntry("2026-01", 2L)
                .containsEntry("2026-02", 1L);
        assertThat(dashboard.getMostPopularEvents())
                .extracting(DashboardResponseDto.PopularEventDto::getEventSlug,
                        DashboardResponseDto.PopularEventDto::getRegistrations,
                        DashboardResponseDto.PopularEventDto::getAverageRating)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("mafia-night", 2L, 4.5),
                        org.assertj.core.groups.Tuple.tuple("networking", 1L, 3.0),
                        org.assertj.core.groups.Tuple.tuple("draft-event", 0L, 0.0)
                );
    }

    private Event event(
            String slug,
            String title,
            LocalDate date,
            EventCategory category,
            EventStatus status,
            Integer attendees
    ) {
        return Event.builder()
                .id(UUID.randomUUID())
                .slug(slug)
                .title(title)
                .date(date)
                .category(category)
                .status(status)
                .attendees(attendees)
                .build();
    }

    private EventParticipant participant(Event event, User user, boolean registered) {
        return EventParticipant.builder()
                .id(UUID.randomUUID())
                .event(event)
                .user(user)
                .registered(registered)
                .build();
    }

    private EventFeedback feedback(Event event, User user, int rating) {
        return EventFeedback.builder()
                .id(UUID.randomUUID())
                .event(event)
                .user(user)
                .rating(rating)
                .build();
    }

    private User member(LocalDateTime createdAt) {
        return user(Role.MEMBER, createdAt);
    }

    private User user(Role role, LocalDateTime createdAt) {
        User user = User.builder()
                .id(UUID.randomUUID())
                .firstName("Test")
                .lastName("User")
                .email(UUID.randomUUID() + "@example.com")
                .password("encoded-password")
                .role(role)
                .enabled(true)
                .build();
        user.setCreatedAt(createdAt);
        return user;
    }
}
