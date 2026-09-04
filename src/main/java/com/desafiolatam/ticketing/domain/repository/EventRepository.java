package com.desafiolatam.ticketing.domain.repository;

import com.desafiolatam.ticketing.domain.model.aggregate.Event;
import com.desafiolatam.ticketing.domain.model.valueobject.EventId;
import java.util.List;
import java.util.Optional;

/**
 * Pure domain contract for Event persistence and retrieval boundary.
 */
public interface EventRepository {

    /**
     * Finds an event by its typed EventId.
     *
     * @param eventId unique event identifier
     * @return Optional containing the event or empty
     */
    Optional<Event> findById(EventId eventId);

    /**
     * Retrieves all events in the catalog.
     *
     * @return list of events
     */
    List<Event> findAll();

    /**
     * Persists or updates an event aggregate.
     *
     * @param event event aggregate root to save
     * @return saved event
     */
    Event save(Event event);
}
