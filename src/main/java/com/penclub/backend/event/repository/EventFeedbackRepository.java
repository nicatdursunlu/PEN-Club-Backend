package com.penclub.backend.event.repository;

import com.penclub.backend.event.entity.Event;
import com.penclub.backend.event.entity.EventFeedback;
import com.penclub.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventFeedbackRepository extends JpaRepository<EventFeedback, UUID> {

    Optional<EventFeedback> findByEventAndUser(Event event, User user);

    List<EventFeedback> findAllByEventOrderByCreatedAtAsc(Event event);
}
