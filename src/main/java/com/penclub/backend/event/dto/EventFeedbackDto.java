package com.penclub.backend.event.dto;

import com.penclub.backend.event.entity.EventFeedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFeedbackDto {

    private UUID id;
    private UUID eventId;
    private String eventSlug;
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EventFeedbackDto fromEntity(EventFeedback feedback) {
        return EventFeedbackDto.builder()
                .id(feedback.getId())
                .eventId(feedback.getEvent().getId())
                .eventSlug(feedback.getEvent().getSlug())
                .userId(feedback.getUser().getId())
                .email(feedback.getUser().getEmail())
                .firstName(feedback.getUser().getFirstName())
                .lastName(feedback.getUser().getLastName())
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .createdAt(feedback.getCreatedAt())
                .updatedAt(feedback.getUpdatedAt())
                .build();
    }
}
