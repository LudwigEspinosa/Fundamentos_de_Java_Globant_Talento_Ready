package com.desafiolatam.ticketing.domain.port;

import com.desafiolatam.ticketing.domain.model.Event;
import java.util.Optional;

/**
 * Output port for accessing and persisting Event entities.
 */
public interface EventRepository {

    /**
     * Finds an event by its unique ID.
     *
     * @param eventId unique event identifier
     * @return Optional containing the event if found, or empty Optional
     */
    Optional<Event> findById(String eventId);

    /**
     * Persists or updates an event entity.
     *
     * @param event event to save
     * @return persisted event
     */
    Event save(Event event);
}
