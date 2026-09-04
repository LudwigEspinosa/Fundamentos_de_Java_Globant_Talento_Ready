package com.desafiolatam.ticketing.infrastructure.persistence.inmemory;

import com.desafiolatam.ticketing.domain.model.aggregate.Event;
import com.desafiolatam.ticketing.domain.model.valueobject.EventId;
import com.desafiolatam.ticketing.domain.repository.EventRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Concrete in-memory repository adapter implementing the domain EventRepository contract.
 */
public class InMemoryEventRepository implements EventRepository {

    private final Map<EventId, Event> database = new ConcurrentHashMap<>();

    @Override
    public Optional<Event> findById(EventId eventId) {
        if (eventId == null) return Optional.empty();
        return Optional.ofNullable(database.get(eventId));
    }

    @Override
    public List<Event> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public Event save(Event event) {
        Objects.requireNonNull(event, "Event cannot be null");
        database.put(event.getId(), event);
        return event;
    }

    public void clear() {
        database.clear();
    }
}
