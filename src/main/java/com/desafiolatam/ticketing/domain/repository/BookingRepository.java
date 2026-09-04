package com.desafiolatam.ticketing.domain.repository;

import com.desafiolatam.ticketing.domain.model.aggregate.Booking;
import com.desafiolatam.ticketing.domain.model.valueobject.BookingId;
import java.util.Optional;

/**
 * Pure domain contract for Booking aggregate persistence and retrieval boundary.
 */
public interface BookingRepository {

    /**
     * Persists or updates a booking aggregate root.
     *
     * @param booking booking aggregate to persist
     * @return persisted booking
     */
    Booking save(Booking booking);

    /**
     * Finds a booking by its typed BookingId.
     *
     * @param bookingId unique booking identifier
     * @return Optional containing the booking or empty
     */
    Optional<Booking> findById(BookingId bookingId);
}
