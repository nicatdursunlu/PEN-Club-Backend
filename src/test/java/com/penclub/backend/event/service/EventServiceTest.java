package com.penclub.backend.event.service;

import com.penclub.backend.event.dto.EventRequestDto;
import com.penclub.backend.event.entity.Event;
import com.penclub.backend.event.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

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
}
