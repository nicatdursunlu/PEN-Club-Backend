package com.penclub.backend.event.repository;

import com.penclub.backend.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    Optional<Event> findBySlug(String slug);
    boolean existsBySlug(String slug);
    void deleteBySlug(String slug);
}