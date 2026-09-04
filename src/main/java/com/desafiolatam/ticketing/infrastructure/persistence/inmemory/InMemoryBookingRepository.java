package com.desafiolatam.ticketing.infrastructure.persistence.inmemory;

import com.desafiolatam.ticketing.domain.model.aggregate.Booking;
import com.desafiolatam.ticketing.domain.model.valueobject.BookingId;
import com.desafiolatam.ticketing.domain.repository.BookingRepository;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Concrete in-memory repository adapter implementing the domain BookingRepository contract.
 */
public class InMemoryBookingRepository implements BookingRepository {

    private final Map<BookingId, Booking> database = new ConcurrentHashMap<>();

    @Override
    public Booking save(Booking booking) {
        Objects.requireNonNull(booking, "Booking cannot be null");
        database.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(BookingId bookingId) {
        if (bookingId == null) return Optional.empty();
        return Optional.ofNullable(database.get(bookingId));
    }

    public void clear() {
        database.clear();
    }
}
