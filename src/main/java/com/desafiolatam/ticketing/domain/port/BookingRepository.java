package com.desafiolatam.ticketing.domain.port;

import com.desafiolatam.ticketing.domain.model.Booking;
import java.util.Optional;

/**
 * Output port for persisting and retrieving Booking aggregate roots.
 */
public interface BookingRepository {

    /**
     * Persists a booking.
     *
     * @param booking booking aggregate to save
     * @return persisted booking
     */
    Booking save(Booking booking);

    /**
     * Retrieves a booking by its unique ID.
     *
     * @param bookingId unique booking identifier
     * @return Optional containing the booking if found, or empty Optional
     */
    Optional<Booking> findById(String bookingId);
}
