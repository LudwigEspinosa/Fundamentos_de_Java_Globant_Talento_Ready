package com.desafiolatam.ticketing.infrastructure.persistence.adapter;

import com.desafiolatam.ticketing.domain.model.aggregate.Booking;
import com.desafiolatam.ticketing.domain.model.valueobject.BookingId;
import com.desafiolatam.ticketing.domain.repository.BookingRepository;
import com.desafiolatam.ticketing.infrastructure.persistence.entity.BookingJpaEntity;
import com.desafiolatam.ticketing.infrastructure.persistence.repository.SpringDataBookingRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * Concrete JPA Adapter implementing domain BookingRepository contract.
 */
@Component
@Primary
public class JpaBookingRepositoryAdapter implements BookingRepository {

    private final SpringDataBookingRepository springDataBookingRepository;

    public JpaBookingRepositoryAdapter(SpringDataBookingRepository springDataBookingRepository) {
        this.springDataBookingRepository = Objects.requireNonNull(springDataBookingRepository, "SpringDataBookingRepository cannot be null");
    }

    @Override
    public Booking save(Booking booking) {
        Objects.requireNonNull(booking, "Booking cannot be null");
        BookingJpaEntity entity = BookingJpaEntity.fromDomain(booking);
        BookingJpaEntity savedEntity = springDataBookingRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Booking> findById(BookingId bookingId) {
        if (bookingId == null) return Optional.empty();
        return springDataBookingRepository.findById(bookingId.value())
                .map(BookingJpaEntity::toDomain);
    }
}
