package com.penclub.backend.event.service;

import com.penclub.backend.event.dto.EventRequestDto;
import com.penclub.backend.event.dto.EventResponseDto;
import com.penclub.backend.event.dto.EventParticipantDto;
import com.penclub.backend.event.entity.Event;
import com.penclub.backend.event.entity.EventParticipant;
import com.penclub.backend.event.entity.EventStatus;
import com.penclub.backend.event.repository.EventParticipantRepository;
import com.penclub.backend.event.repository.EventRepository;
import com.penclub.backend.exception.EventAlreadyRegisteredException;
import com.penclub.backend.exception.ResourceNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventParticipantRepository eventParticipantRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void createEventGeneratesSlugFromTitleWhenMissing() {
        EventRequestDto request = EventRequestDto.builder()
                .title("New Year Party 2026")
                .date(LocalDate.of(2026, 12, 31))
                .build();

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        when(eventRepository.existsBySlug("new-year-party-2026")).thenReturn(false);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        eventService.createEvent(request);

        verify(eventRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getSlug()).isEqualTo("new-year-party-2026");
    }

    @Test
    void createEventAppendsSuffixWhenGeneratedSlugExists() {
        EventRequestDto request = EventRequestDto.builder()
                .title("New Year Party 2026")
                .date(LocalDate.of(2026, 12, 31))
                .build();

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        when(eventRepository.existsBySlug("new-year-party-2026")).thenReturn(true);
        when(eventRepository.existsBySlug("new-year-party-2026-2")).thenReturn(false);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        eventService.createEvent(request);

        verify(eventRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getSlug()).isEqualTo("new-year-party-2026-2");
    }

    @Test
    void createEventUsesProvidedSlugWhenPresent() {
        EventRequestDto request = EventRequestDto.builder()
                .slug("  custom-event-slug  ")
                .title("New Year Party 2026")
                .date(LocalDate.of(2026, 12, 31))
                .build();

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        when(eventRepository.existsBySlug("custom-event-slug")).thenReturn(false);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        eventService.createEvent(request);

        verify(eventRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getSlug()).isEqualTo("custom-event-slug");
    }

    @Test
    void createEventNormalizesTitleWhenGeneratingSlug() {
        EventRequestDto request = EventRequestDto.builder()
                .title("  Café & Networking: 2026!  ")
                .date(LocalDate.of(2026, 12, 31))
                .build();

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        when(eventRepository.existsBySlug("cafe-networking-2026")).thenReturn(false);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        eventService.createEvent(request);

        verify(eventRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getSlug()).isEqualTo("cafe-networking-2026");
    }

    @Test
    void createEventThrowsWhenSlugAndTitleAreMissing() {
        EventRequestDto request = EventRequestDto.builder()
                .date(LocalDate.of(2026, 12, 31))
                .build();

        assertThatThrownBy(() -> eventService.createEvent(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event title is required to generate slug");

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void createEventThrowsWhenTitleCannotGenerateSlug() {
        EventRequestDto request = EventRequestDto.builder()
                .title("!!!")
                .date(LocalDate.of(2026, 12, 31))
                .build();

        assertThatThrownBy(() -> eventService.createEvent(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Event title must contain letters or numbers to generate slug");

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void getEventParticipantsReturnsParticipantDetailsIncludingUnregisteredTimestamp() {
        Event event = Event.builder()
                .slug("new-year-party-2026")
                .title("New Year Party 2026")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.UPCOMING)
                .build();
        User registeredUser = memberUser();
        registeredUser.setId(UUID.randomUUID());
        registeredUser.setGender("male");
        registeredUser.setPhoneNumber("+994501234567");
        registeredUser.setEducationDegree("Bachelor");

        User unregisteredUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Jane")
                .lastName("Doe")
                .email("jane.doe@example.com")
                .password("encoded-password")
                .role(Role.MEMBER)
                .enabled(true)
                .build();
        LocalDateTime unregisteredAt = LocalDateTime.of(2026, 10, 11, 10, 0);

        EventParticipant activeParticipant = EventParticipant.builder()
                .id(UUID.randomUUID())
                .event(event)
                .user(registeredUser)
                .registered(true)
                .build();
        activeParticipant.setCreatedAt(LocalDateTime.of(2026, 10, 10, 10, 0));

        EventParticipant inactiveParticipant = EventParticipant.builder()
                .id(UUID.randomUUID())
                .event(event)
                .user(unregisteredUser)
                .registered(false)
                .unregisteredAt(unregisteredAt)
                .build();
        inactiveParticipant.setCreatedAt(LocalDateTime.of(2026, 10, 10, 11, 0));

        when(eventRepository.findBySlug("new-year-party-2026")).thenReturn(Optional.of(event));
        when(eventParticipantRepository.findAllByEventOrderByCreatedAtAsc(event))
                .thenReturn(List.of(activeParticipant, inactiveParticipant));

        List<EventParticipantDto> response = eventService.getEventParticipants("new-year-party-2026");

        assertThat(response).hasSize(2);
        assertThat(response.get(0).getEmail()).isEqualTo("nijat.dursunlu@gmail.com");
        assertThat(response.get(0).isRegistered()).isTrue();
        assertThat(response.get(0).getUnregisteredAt()).isNull();
        assertThat(response.get(0).getPhoneNumber()).isEqualTo("+994501234567");
        assertThat(response.get(1).getEmail()).isEqualTo("jane.doe@example.com");
        assertThat(response.get(1).isRegistered()).isFalse();
        assertThat(response.get(1).getUnregisteredAt()).isEqualTo(unregisteredAt);
    }

    @Test
    void getEventParticipantsThrowsWhenEventDoesNotExist() {
        when(eventRepository.findBySlug("missing-event")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventParticipants("missing-event"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Event not found with slug: missing-event");
    }

    @Test
    void registerForEventCreatesParticipationAndIncrementsAttendees() {
        Event event = Event.builder()
                .slug("new-year-party-2026")
                .title("New Year Party 2026")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.UPCOMING)
                .attendees(4)
                .build();
        User user = memberUser();

        ArgumentCaptor<EventParticipant> participantCaptor = ArgumentCaptor.forClass(EventParticipant.class);
        when(eventRepository.findBySlug("new-year-party-2026")).thenReturn(Optional.of(event));
        when(eventParticipantRepository.existsByEventAndUserAndRegisteredTrue(event, user)).thenReturn(false);
        when(eventParticipantRepository.findByEventAndUser(event, user)).thenReturn(Optional.empty());
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventResponseDto response = eventService.registerForEvent("new-year-party-2026", user);

        verify(eventParticipantRepository).save(participantCaptor.capture());
        assertThat(participantCaptor.getValue().getEvent()).isEqualTo(event);
        assertThat(participantCaptor.getValue().getUser()).isEqualTo(user);
        assertThat(participantCaptor.getValue().isRegistered()).isTrue();
        assertThat(participantCaptor.getValue().getUnregisteredAt()).isNull();
        assertThat(response.getAttendees()).isEqualTo(5);
    }

    @Test
    void registerForEventReactivatesPreviousUnregistration() {
        Event event = Event.builder()
                .slug("new-year-party-2026")
                .title("New Year Party 2026")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.UPCOMING)
                .attendees(4)
                .build();
        User user = memberUser();
        EventParticipant participant = EventParticipant.builder()
                .event(event)
                .user(user)
                .registered(false)
                .build();
        participant.setUnregisteredAt(LocalDate.now().atStartOfDay());

        when(eventRepository.findBySlug("new-year-party-2026")).thenReturn(Optional.of(event));
        when(eventParticipantRepository.existsByEventAndUserAndRegisteredTrue(event, user)).thenReturn(false);
        when(eventParticipantRepository.findByEventAndUser(event, user)).thenReturn(Optional.of(participant));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventResponseDto response = eventService.registerForEvent("new-year-party-2026", user);

        verify(eventParticipantRepository).save(participant);
        assertThat(participant.isRegistered()).isTrue();
        assertThat(participant.getUnregisteredAt()).isNull();
        assertThat(response.getAttendees()).isEqualTo(5);
    }

    @Test
    void registerForEventTreatsNullAttendeesAsZero() {
        Event event = Event.builder()
                .slug("new-year-party-2026")
                .title("New Year Party 2026")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.UPCOMING)
                .build();
        User user = memberUser();

        when(eventRepository.findBySlug("new-year-party-2026")).thenReturn(Optional.of(event));
        when(eventParticipantRepository.existsByEventAndUserAndRegisteredTrue(event, user)).thenReturn(false);
        when(eventParticipantRepository.findByEventAndUser(event, user)).thenReturn(Optional.empty());
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventResponseDto response = eventService.registerForEvent("new-year-party-2026", user);

        assertThat(response.getAttendees()).isEqualTo(1);
    }

    @Test
    void registerForEventThrowsWhenUserAlreadyRegistered() {
        Event event = Event.builder()
                .slug("new-year-party-2026")
                .title("New Year Party 2026")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.UPCOMING)
                .attendees(4)
                .build();
        User user = memberUser();

        when(eventRepository.findBySlug("new-year-party-2026")).thenReturn(Optional.of(event));
        when(eventParticipantRepository.existsByEventAndUserAndRegisteredTrue(event, user)).thenReturn(true);

        assertThatThrownBy(() -> eventService.registerForEvent("new-year-party-2026", user))
                .isInstanceOf(EventAlreadyRegisteredException.class)
                .hasMessage("User is already registered for event: new-year-party-2026");

        verify(eventParticipantRepository, never()).save(any(EventParticipant.class));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void registerForEventThrowsWhenEventIsPast() {
        Event event = Event.builder()
                .slug("past-event")
                .title("Past Event")
                .date(LocalDate.now().minusDays(1))
                .status(EventStatus.COMPLETED)
                .attendees(4)
                .build();

        when(eventRepository.findBySlug("past-event")).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.registerForEvent("past-event", memberUser()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot register for a past event: past-event");

        verify(eventParticipantRepository, never()).save(any(EventParticipant.class));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void registerForEventThrowsWhenEventDoesNotExist() {
        when(eventRepository.findBySlug("missing-event")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.registerForEvent("missing-event", memberUser()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Event not found with slug: missing-event");

        verify(eventParticipantRepository, never()).save(any(EventParticipant.class));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void unregisterFromEventDeletesParticipationAndDecrementsAttendees() {
        Event event = Event.builder()
                .slug("new-year-party-2026")
                .title("New Year Party 2026")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.UPCOMING)
                .attendees(4)
                .build();
        User user = memberUser();
        EventParticipant participant = EventParticipant.builder()
                .event(event)
                .user(user)
                .build();

        when(eventRepository.findBySlug("new-year-party-2026")).thenReturn(Optional.of(event));
        when(eventParticipantRepository.findByEventAndUserAndRegisteredTrue(event, user)).thenReturn(Optional.of(participant));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventResponseDto response = eventService.unregisterFromEvent("new-year-party-2026", user);

        verify(eventParticipantRepository).save(participant);
        assertThat(participant.isRegistered()).isFalse();
        assertThat(participant.getUnregisteredAt()).isNotNull();
        assertThat(response.getAttendees()).isEqualTo(3);
    }

    @Test
    void unregisterFromEventDoesNotDecrementBelowZero() {
        Event event = Event.builder()
                .slug("new-year-party-2026")
                .title("New Year Party 2026")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.UPCOMING)
                .attendees(0)
                .build();
        User user = memberUser();
        EventParticipant participant = EventParticipant.builder()
                .event(event)
                .user(user)
                .build();

        when(eventRepository.findBySlug("new-year-party-2026")).thenReturn(Optional.of(event));
        when(eventParticipantRepository.findByEventAndUserAndRegisteredTrue(event, user)).thenReturn(Optional.of(participant));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventResponseDto response = eventService.unregisterFromEvent("new-year-party-2026", user);

        assertThat(response.getAttendees()).isZero();
    }

    @Test
    void unregisterFromEventThrowsWhenUserIsNotRegistered() {
        Event event = Event.builder()
                .slug("new-year-party-2026")
                .title("New Year Party 2026")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.UPCOMING)
                .attendees(4)
                .build();
        User user = memberUser();

        when(eventRepository.findBySlug("new-year-party-2026")).thenReturn(Optional.of(event));
        when(eventParticipantRepository.findByEventAndUserAndRegisteredTrue(event, user)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.unregisterFromEvent("new-year-party-2026", user))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Event registration not found for event: new-year-party-2026");

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void unregisterFromEventThrowsWhenEventIsPast() {
        Event event = Event.builder()
                .slug("past-event")
                .title("Past Event")
                .date(LocalDate.now().minusDays(1))
                .status(EventStatus.COMPLETED)
                .attendees(4)
                .build();

        when(eventRepository.findBySlug("past-event")).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.unregisterFromEvent("past-event", memberUser()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot unregister from a past event: past-event");

        verify(eventParticipantRepository, never()).save(any(EventParticipant.class));
        verify(eventRepository, never()).save(any(Event.class));
    }

    private User memberUser() {
        return User.builder()
                .firstName("Nijat")
                .lastName("Dursunlu")
                .email("nijat.dursunlu@gmail.com")
                .password("encoded-password")
                .role(Role.MEMBER)
                .enabled(true)
                .build();
    }
}
