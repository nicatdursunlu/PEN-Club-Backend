package com.penclub.backend.event.repository;

import com.penclub.backend.event.entity.Event;
import com.penclub.backend.event.entity.EventCategory;
import com.penclub.backend.event.entity.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    Optional<Event> findBySlug(String slug);
    Optional<Event> findBySlugAndStatus(String slug, EventStatus status);
    List<Event> findAllByStatus(EventStatus status);
    Page<Event> findAllByStatus(EventStatus status, Pageable pageable);
    Page<Event> findAllByCategory(EventCategory category, Pageable pageable);
    Page<Event> findAllByStatusAndCategory(EventStatus status, EventCategory category, Pageable pageable);
    boolean existsBySlug(String slug);
    void deleteBySlug(String slug);
}
