package com.penclub.backend.event.service;

import com.penclub.backend.event.dto.EventRequestDto;
import com.penclub.backend.event.dto.EventResponseDto;
import com.penclub.backend.event.dto.EventParticipantDto;
import com.penclub.backend.event.entity.Event;
import com.penclub.backend.event.entity.EventCategory;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
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
    void createEventDefaultsToDraftWhenStatusIsMissing() {
        EventRequestDto request = EventRequestDto.builder()
                .title("New Year Party 2026")
                .date(LocalDate.of(2026, 12, 31))
                .build();

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        when(eventRepository.existsBySlug("new-year-party-2026")).thenReturn(false);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        eventService.createEvent(request);

        verify(eventRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getStatus()).isEqualTo(EventStatus.DRAFT);
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
    void getAllEventsReturnsOnlyPublishedEvents() {
        Event publishedEvent = Event.builder()
                .slug("published-event")
                .title("Published Event")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.PUBLISHED)
                .build();

        when(eventRepository.findAllByStatus(EventStatus.PUBLISHED)).thenReturn(List.of(publishedEvent));

        assertThat(eventService.getAllEvents())
                .extracting("id")
                .containsExactly("published-event");
    }

    @Test
    void getPastEventsReturnsPastPublishedAndCompletedEvents() {
        Event pastPublishedEvent = Event.builder()
                .slug("past-published-event")
                .title("Past Published Event")
                .date(LocalDate.now().minusDays(2))
                .time("3:00 PM - 5:00 PM")
                .status(EventStatus.PUBLISHED)
                .build();
        Event completedEvent = Event.builder()
                .slug("completed-event")
                .title("Completed Event")
                .date(LocalDate.now().minusDays(1))
                .time("3:00 PM - 5:00 PM")
                .status(EventStatus.COMPLETED)
                .build();

        when(eventRepository.findAllByStatusIn(List.of(EventStatus.PUBLISHED, EventStatus.COMPLETED)))
                .thenReturn(List.of(pastPublishedEvent, completedEvent));

        assertThat(eventService.getPastEvents())
                .extracting("id")
                .containsExactly("completed-event", "past-published-event");
    }

    @Test
    void getAllEventsForAdminReturnsEventsInAllStatuses() {
        Event draftEvent = Event.builder()
                .slug("draft-event")
                .title("Draft Event")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.DRAFT)
                .createdAt(LocalDateTime.of(2026, 10, 10, 10, 0))
                .build();
        Event publishedEvent = Event.builder()
                .slug("published-event")
                .title("Published Event")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.PUBLISHED)
                .createdAt(LocalDateTime.of(2026, 10, 11, 10, 0))
                .build();
        Event cancelledEvent = Event.builder()
                .slug("cancelled-event")
                .title("Cancelled Event")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.CANCELLED)
                .createdAt(LocalDateTime.of(2026, 10, 9, 10, 0))
                .build();

        PageRequest pageable = PageRequest.of(0, 20);
        when(eventRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(publishedEvent, draftEvent, cancelledEvent), pageable, 3));

        assertThat(eventService.getAllEventsForAdmin(null, null, pageable).getContent())
                .extracting(EventResponseDto::getSlug)
                .containsExactly("published-event", "draft-event", "cancelled-event");
    }

    @Test
    void getAllEventsForAdminFiltersByStatusAndCategory() {
        Event event = Event.builder()
                .slug("draft-play-event")
                .title("Draft Play Event")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.DRAFT)
                .category(EventCategory.PLAY)
                .build();

        PageRequest pageable = PageRequest.of(0, 10);
        when(eventRepository.findAllByStatusAndCategory(EventStatus.DRAFT, EventCategory.PLAY, pageable))
                .thenReturn(new PageImpl<>(List.of(event), pageable, 1));

        assertThat(eventService.getAllEventsForAdmin(EventStatus.DRAFT, EventCategory.PLAY, pageable).getContent())
                .extracting(EventResponseDto::getSlug)
                .containsExactly("draft-play-event");
    }

    @Test
    void getAllEventsForAdminFiltersByStatusOnly() {
        Event event = Event.builder()
                .slug("draft-event")
                .title("Draft Event")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.DRAFT)
                .build();

        PageRequest pageable = PageRequest.of(0, 10);
        when(eventRepository.findAllByStatus(EventStatus.DRAFT, pageable))
                .thenReturn(new PageImpl<>(List.of(event), pageable, 1));

        assertThat(eventService.getAllEventsForAdmin(EventStatus.DRAFT, null, pageable).getContent())
                .extracting(EventResponseDto::getSlug)
                .containsExactly("draft-event");
    }

    @Test
    void getAllEventsForAdminFiltersByCategoryOnly() {
        Event event = Event.builder()
                .slug("play-event")
                .title("Play Event")
                .date(LocalDate.of(2026, 12, 31))
                .category(EventCategory.PLAY)
                .build();

        PageRequest pageable = PageRequest.of(0, 10);
        when(eventRepository.findAllByCategory(EventCategory.PLAY, pageable))
                .thenReturn(new PageImpl<>(List.of(event), pageable, 1));

        assertThat(eventService.getAllEventsForAdmin(null, EventCategory.PLAY, pageable).getContent())
                .extracting(EventResponseDto::getSlug)
                .containsExactly("play-event");
    }

    @Test
    void getEventBySlugRequiresPublishedEvent() {
        Event publishedEvent = Event.builder()
                .slug("published-event")
                .title("Published Event")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.PUBLISHED)
                .build();

        when(eventRepository.findBySlugAndStatus("published-event", EventStatus.PUBLISHED))
                .thenReturn(Optional.of(publishedEvent));

        EventResponseDto response = eventService.getEventBySlug("published-event");

        assertThat(response.getSlug()).isEqualTo("published-event");
    }

    @Test
    void getEventBySlugReturnsRegisteredTrueForCurrentUser() {
        Event publishedEvent = Event.builder()
                .slug("published-event")
                .title("Published Event")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.PUBLISHED)
                .build();
        User user = memberUser();

        when(eventRepository.findBySlug("published-event")).thenReturn(Optional.of(publishedEvent));
        when(eventParticipantRepository.existsByEventAndUserAndRegisteredTrue(publishedEvent, user)).thenReturn(true);

        EventResponseDto response = eventService.getEventBySlug("published-event", user);

        assertThat(response.isRegistered()).isTrue();
    }

    @Test
    void getEventBySlugReturnsRegisteredFalseWhenUserIsAnonymous() {
        Event publishedEvent = Event.builder()
                .slug("published-event")
                .title("Published Event")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.PUBLISHED)
                .build();

        when(eventRepository.findBySlugAndStatus("published-event", EventStatus.PUBLISHED))
                .thenReturn(Optional.of(publishedEvent));

        EventResponseDto response = eventService.getEventBySlug("published-event", null);

        assertThat(response.isRegistered()).isFalse();
    }

    @Test
    void getEventBySlugReturnsDraftEventForAuthenticatedUser() {
        Event draftEvent = Event.builder()
                .slug("draft-event")
                .title("Draft Event")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.DRAFT)
                .build();
        User user = memberUser();

        when(eventRepository.findBySlug("draft-event")).thenReturn(Optional.of(draftEvent));
        when(eventParticipantRepository.existsByEventAndUserAndRegisteredTrue(draftEvent, user)).thenReturn(false);

        EventResponseDto response = eventService.getEventBySlug("draft-event", user);

        assertThat(response.getSlug()).isEqualTo("draft-event");
        assertThat(response.getStatus()).isEqualTo(EventStatus.DRAFT);
        assertThat(response.isRegistered()).isFalse();
    }

    @Test
    void getEventBySlugThrowsWhenEventIsNotPublished() {
        when(eventRepository.findBySlugAndStatus("draft-event", EventStatus.PUBLISHED)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.getEventBySlug("draft-event"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Event not found with slug: draft-event");
    }

    @Test
    void getEventParticipantsReturnsParticipantDetailsIncludingUnregisteredTimestamp() {
        Event event = Event.builder()
                .slug("new-year-party-2026")
                .title("New Year Party 2026")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.PUBLISHED)
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
                .status(EventStatus.PUBLISHED)
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
                .status(EventStatus.PUBLISHED)
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
                .status(EventStatus.PUBLISHED)
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
                .status(EventStatus.PUBLISHED)
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
    void registerForEventThrowsWhenEventIsNotPublished() {
        Event event = Event.builder()
                .slug("draft-event")
                .title("Draft Event")
                .date(LocalDate.now().plusDays(1))
                .status(EventStatus.DRAFT)
                .attendees(4)
                .build();

        when(eventRepository.findBySlug("draft-event")).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.registerForEvent("draft-event", memberUser()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot register for an unpublished event: draft-event");

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
                .status(EventStatus.PUBLISHED)
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
                .status(EventStatus.PUBLISHED)
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
                .status(EventStatus.PUBLISHED)
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

    @Test
    void cloneEventCreatesDraftCopyWithoutAttendees() {
        Event sourceEvent = Event.builder()
                .slug("published-event")
                .title("Published Event")
                .date(LocalDate.of(2026, 12, 31))
                .time("6:00 PM - 8:00 PM")
                .location("Warsaw")
                .status(EventStatus.PUBLISHED)
                .attendees(12)
                .description("Original description")
                .build();

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        when(eventRepository.findBySlug("published-event")).thenReturn(Optional.of(sourceEvent));
        when(eventRepository.existsBySlug("published-event-copy")).thenReturn(false);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventResponseDto response = eventService.cloneEvent("published-event");

        verify(eventRepository).save(eventCaptor.capture());
        Event clonedEvent = eventCaptor.getValue();
        assertThat(clonedEvent.getSlug()).isEqualTo("published-event-copy");
        assertThat(clonedEvent.getTitle()).isEqualTo("Published Event Copy");
        assertThat(clonedEvent.getStatus()).isEqualTo(EventStatus.DRAFT);
        assertThat(clonedEvent.getAttendees()).isZero();
        assertThat(clonedEvent.getDescription()).isEqualTo("Original description");
        assertThat(response.getSlug()).isEqualTo("published-event-copy");
    }

    @Test
    void cloneEventAppendsSuffixWhenCopySlugExists() {
        Event sourceEvent = Event.builder()
                .slug("published-event")
                .title("Published Event")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.PUBLISHED)
                .build();

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        when(eventRepository.findBySlug("published-event")).thenReturn(Optional.of(sourceEvent));
        when(eventRepository.existsBySlug("published-event-copy")).thenReturn(true);
        when(eventRepository.existsBySlug("published-event-copy-2")).thenReturn(false);
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        eventService.cloneEvent("published-event");

        verify(eventRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getSlug()).isEqualTo("published-event-copy-2");
    }

    @Test
    void cloneEventThrowsWhenEventDoesNotExist() {
        when(eventRepository.findBySlug("missing-event")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.cloneEvent("missing-event"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Event not found with slug: missing-event");

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void publishEventChangesDraftToPublished() {
        Event event = Event.builder()
                .slug("draft-event")
                .title("Draft Event")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.DRAFT)
                .build();

        when(eventRepository.findBySlug("draft-event")).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventResponseDto response = eventService.publishEvent("draft-event");

        assertThat(response.getStatus()).isEqualTo(EventStatus.PUBLISHED);
        verify(eventRepository).save(event);
    }

    @Test
    void publishEventThrowsWhenEventIsCancelled() {
        Event event = Event.builder()
                .slug("cancelled-event")
                .title("Cancelled Event")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.CANCELLED)
                .build();

        when(eventRepository.findBySlug("cancelled-event")).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.publishEvent("cancelled-event"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot publish a cancelled event: cancelled-event");

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void publishEventThrowsWhenEventDoesNotExist() {
        when(eventRepository.findBySlug("missing-event")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.publishEvent("missing-event"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Event not found with slug: missing-event");

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void cancelEventChangesPublishedToCancelled() {
        Event event = Event.builder()
                .slug("published-event")
                .title("Published Event")
                .date(LocalDate.of(2026, 12, 31))
                .status(EventStatus.PUBLISHED)
                .build();

        when(eventRepository.findBySlug("published-event")).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EventResponseDto response = eventService.cancelEvent("published-event");

        assertThat(response.getStatus()).isEqualTo(EventStatus.CANCELLED);
        verify(eventRepository).save(event);
    }

    @Test
    void cancelEventThrowsWhenEventIsCompleted() {
        Event event = Event.builder()
                .slug("completed-event")
                .title("Completed Event")
                .date(LocalDate.now().minusDays(1))
                .status(EventStatus.COMPLETED)
                .build();

        when(eventRepository.findBySlug("completed-event")).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> eventService.cancelEvent("completed-event"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot cancel a completed event: completed-event");

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void cancelEventThrowsWhenEventDoesNotExist() {
        when(eventRepository.findBySlug("missing-event")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.cancelEvent("missing-event"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Event not found with slug: missing-event");

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
