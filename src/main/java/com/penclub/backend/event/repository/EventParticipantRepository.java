package com.penclub.backend.event.repository;

import com.penclub.backend.event.entity.Event;
import com.penclub.backend.event.entity.EventParticipant;
import com.penclub.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, UUID> {

    boolean existsByEventAndUserAndRegisteredTrue(Event event, User user);

    Optional<EventParticipant> findByEventAndUser(Event event, User user);

    Optional<EventParticipant> findByEventAndUserAndRegisteredTrue(Event event, User user);

    List<EventParticipant> findAllByEventOrderByCreatedAtAsc(Event event);
}
