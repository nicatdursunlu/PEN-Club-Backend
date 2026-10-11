package com.penclub.backend.event.dto;

import com.penclub.backend.event.entity.EventParticipant;
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
public class EventParticipantDto {

    private UUID id;
    private UUID userId;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private String phoneNumber;
    private String educationDegree;
    private boolean registered;
    private LocalDateTime registeredAt;
    private LocalDateTime unregisteredAt;

    public static EventParticipantDto fromEntity(EventParticipant participant) {
        return EventParticipantDto.builder()
                .id(participant.getId())
                .userId(participant.getUser().getId())
                .email(participant.getUser().getEmail())
                .firstName(participant.getUser().getFirstName())
                .lastName(participant.getUser().getLastName())
                .gender(participant.getUser().getGender())
                .phoneNumber(participant.getUser().getPhoneNumber())
                .educationDegree(participant.getUser().getEducationDegree())
                .registered(participant.isRegistered())
                .registeredAt(participant.getCreatedAt())
                .unregisteredAt(participant.getUnregisteredAt())
                .build();
    }
}
