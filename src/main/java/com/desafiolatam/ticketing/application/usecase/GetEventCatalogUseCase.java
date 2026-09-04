package com.desafiolatam.ticketing.application.usecase;

import com.desafiolatam.ticketing.application.dto.EventResponseDTO;
import com.desafiolatam.ticketing.domain.model.aggregate.Event;
import com.desafiolatam.ticketing.domain.repository.EventRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Use Case for retrieving the active event catalog.
 */
public class GetEventCatalogUseCase {

    private final EventRepository eventRepository;

    public GetEventCatalogUseCase(EventRepository eventRepository) {
        this.eventRepository = Objects.requireNonNull(eventRepository, "EventRepository cannot be null");
    }

    public List<EventResponseDTO> execute() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(e -> new EventResponseDTO(
                        e.getId().value(),
                        e.getName(),
                        e.getBasePrice().amount(),
                        e.getTotalCapacity(),
                        e.getAvailableSeats(),
                        e.getStatus()
                ))
                .collect(Collectors.toList());
    }
}
