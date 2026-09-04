package com.desafiolatam.ticketing.infrastructure.persistence.adapter;

import com.desafiolatam.ticketing.domain.model.aggregate.Event;
import com.desafiolatam.ticketing.domain.model.valueobject.EventId;
import com.desafiolatam.ticketing.domain.repository.EventRepository;
import com.desafiolatam.ticketing.infrastructure.persistence.entity.EventJpaEntity;
import com.desafiolatam.ticketing.infrastructure.persistence.repository.SpringDataEventRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Concrete JPA Adapter implementing domain EventRepository contract.
 */
@Component
@Primary
public class JpaEventRepositoryAdapter implements EventRepository {

    private final SpringDataEventRepository springDataEventRepository;

    public JpaEventRepositoryAdapter(SpringDataEventRepository springDataEventRepository) {
        this.springDataEventRepository = Objects.requireNonNull(springDataEventRepository, "SpringDataEventRepository cannot be null");
    }

    @Override
    public Optional<Event> findById(EventId eventId) {
        if (eventId == null) return Optional.empty();
        return springDataEventRepository.findById(eventId.value())
                .map(EventJpaEntity::toDomain);
    }

    @Override
    public List<Event> findAll() {
        return springDataEventRepository.findAll().stream()
                .map(EventJpaEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Event save(Event event) {
        Objects.requireNonNull(event, "Event cannot be null");
        EventJpaEntity entity = EventJpaEntity.fromDomain(event);
        EventJpaEntity savedEntity = springDataEventRepository.save(entity);
        return savedEntity.toDomain();
    }
}
