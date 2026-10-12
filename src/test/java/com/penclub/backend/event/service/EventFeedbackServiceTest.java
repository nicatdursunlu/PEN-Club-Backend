package com.penclub.backend.event.service;

import com.penclub.backend.event.dto.EventFeedbackDto;
import com.penclub.backend.event.dto.EventFeedbackRequestDto;
import com.penclub.backend.event.entity.Event;
import com.penclub.backend.event.entity.EventFeedback;
import com.penclub.backend.event.entity.EventStatus;
import com.penclub.backend.event.repository.EventFeedbackRepository;
import com.penclub.backend.event.repository.EventParticipantRepository;
import com.penclub.backend.event.repository.EventRepository;
import com.penclub.backend.user.entity.Role;
import com.penclub.backend.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventFeedbackServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventParticipantRepository eventParticipantRepository;

    @Mock
    private EventFeedbackRepository eventFeedbackRepository;

    @InjectMocks
    private EventFeedbackService eventFeedbackService;

    @Test
    void submitFeedbackCreatesFeedbackForRegisteredParticipantAfterEvent() {
        Event event = pastPublishedEvent();
        User user = memberUser();
        EventFeedbackRequestDto request = EventFeedbackRequestDto.builder()
                .rating(5)
                .comment(" Great session ")
                .build();

        ArgumentCaptor<EventFeedback> feedbackCaptor = ArgumentCaptor.forClass(EventFeedback.class);
        when(eventRepository.findBySlug("past-event")).thenReturn(Optional.of(event));
        when(eventParticipantRepository.existsByEventAndUserAndRegisteredTrue(event, user)).thenReturn(true);
        when(eventFeedbackRepository.findByEventAndUser(event, user)).thenReturn(Optional.empty());
        when(eventFeedbackRepository.save(any(EventFeedback.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventFeedbackDto response = eventFeedbackService.submitFeedback("past-event", request, user);

        verify(eventFeedbackRepository).save(feedbackCaptor.capture());
        EventFeedback savedFeedback = feedbackCaptor.getValue();
        assertThat(savedFeedback.getEvent()).isEqualTo(event);
        assertThat(savedFeedback.getUser()).isEqualTo(user);
        assertThat(savedFeedback.getRating()).isEqualTo(5);
        assertThat(savedFeedback.getComment()).isEqualTo("Great session");
        assertThat(response.getRating()).isEqualTo(5);
        assertThat(response.getComment()).isEqualTo("Great session");
    }

    @Test
    void submitFeedbackUpdatesExistingFeedbackForSameEventAndUser() {
        Event event = pastPublishedEvent();
        User user = memberUser();
        EventFeedback existingFeedback = EventFeedback.builder()
                .event(event)
                .user(user)
                .rating(3)
                .comment("Original")
                .build();
        EventFeedbackRequestDto request = EventFeedbackRequestDto.builder()
                .rating(4)
                .comment("Updated")
                .build();

        when(eventRepository.findBySlug("past-event")).thenReturn(Optional.of(event));
        when(eventParticipantRepository.existsByEventAndUserAndRegisteredTrue(event, user)).thenReturn(true);
        when(eventFeedbackRepository.findByEventAndUser(event, user)).thenReturn(Optional.of(existingFeedback));
        when(eventFeedbackRepository.save(any(EventFeedback.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventFeedbackDto response = eventFeedbackService.submitFeedback("past-event", request, user);

        verify(eventFeedbackRepository).save(existingFeedback);
        assertThat(existingFeedback.getRating()).isEqualTo(4);
        assertThat(existingFeedback.getComment()).isEqualTo("Updated");
        assertThat(response.getRating()).isEqualTo(4);
    }

    @Test
    void submitFeedbackAllowsCompletedEventEvenWhenDateIsToday() {
        Event event = Event.builder()
                .slug("completed-event")
                .title("Completed Event")
                .date(LocalDate.now())
                .status(EventStatus.COMPLETED)
                .build();
        User user = memberUser();
        EventFeedbackRequestDto request = EventFeedbackRequestDto.builder()
                .rating(5)
                .build();

        when(eventRepository.findBySlug("completed-event")).thenReturn(Optional.of(event));
        when(eventParticipantRepository.existsByEventAndUserAndRegisteredTrue(event, user)).thenReturn(true);
        when(eventFeedbackRepository.findByEventAndUser(event, user)).thenReturn(Optional.empty());
        when(eventFeedbackRepository.save(any(EventFeedback.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventFeedbackDto response = eventFeedbackService.submitFeedback("completed-event", request, user);

        assertThat(response.getRating()).isEqualTo(5);
    }

    @Test
    void submitFeedbackThrowsWhenEventHasNotEnded() {
        Event event = Event.builder()
                .slug("future-event")
                .title("Future Event")
                .date(LocalDate.now().plusDays(1))
                .status(EventStatus.PUBLISHED)
                .build();

        when(eventRepository.findBySlug("future-event")).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventFeedbackService.submitFeedback("future-event", feedbackRequest(), memberUser()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot submit feedback before event has ended: future-event");

        verify(eventFeedbackRepository, never()).save(any(EventFeedback.class));
    }

    @Test
    void submitFeedbackThrowsWhenUserIsNotRegisteredParticipant() {
        Event event = pastPublishedEvent();
        User user = memberUser();

        when(eventRepository.findBySlug("past-event")).thenReturn(Optional.of(event));
        when(eventParticipantRepository.existsByEventAndUserAndRegisteredTrue(event, user)).thenReturn(false);

        assertThatThrownBy(() -> eventFeedbackService.submitFeedback("past-event", feedbackRequest(), user))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only registered participants can submit feedback for event: past-event");

        verify(eventFeedbackRepository, never()).save(any(EventFeedback.class));
    }

    @Test
    void getEventFeedbackReturnsFeedbackInCreatedOrder() {
        Event event = pastPublishedEvent();
        User user = memberUser();
        user.setId(UUID.randomUUID());
        EventFeedback feedback = EventFeedback.builder()
                .id(UUID.randomUUID())
                .event(event)
                .user(user)
                .rating(5)
                .comment("Great")
                .build();
        feedback.setCreatedAt(LocalDateTime.of(2026, 10, 10, 10, 0));

        when(eventRepository.findBySlug("past-event")).thenReturn(Optional.of(event));
        when(eventFeedbackRepository.findAllByEventOrderByCreatedAtAsc(event)).thenReturn(List.of(feedback));

        List<EventFeedbackDto> response = eventFeedbackService.getEventFeedback("past-event");

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getEmail()).isEqualTo("nijat.dursunlu@gmail.com");
        assertThat(response.get(0).getRating()).isEqualTo(5);
        assertThat(response.get(0).getComment()).isEqualTo("Great");
    }

    private Event pastPublishedEvent() {
        return Event.builder()
                .id(UUID.randomUUID())
                .slug("past-event")
                .title("Past Event")
                .date(LocalDate.now().minusDays(1))
                .status(EventStatus.PUBLISHED)
                .build();
    }

    private EventFeedbackRequestDto feedbackRequest() {
        return EventFeedbackRequestDto.builder()
                .rating(5)
                .comment("Great")
                .build();
    }

    private User memberUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .firstName("Nijat")
                .lastName("Dursunlu")
                .email("nijat.dursunlu@gmail.com")
                .password("encoded-password")
                .role(Role.MEMBER)
                .enabled(true)
                .build();
    }
}
