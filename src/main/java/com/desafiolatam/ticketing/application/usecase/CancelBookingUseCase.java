package com.desafiolatam.ticketing.application.usecase;

import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import com.desafiolatam.ticketing.domain.model.aggregate.Booking;
import com.desafiolatam.ticketing.domain.model.aggregate.Event;
import com.desafiolatam.ticketing.domain.model.valueobject.BookingId;
import com.desafiolatam.ticketing.domain.model.valueobject.BookingItem;
import com.desafiolatam.ticketing.domain.repository.BookingRepository;
import com.desafiolatam.ticketing.domain.repository.EventRepository;
import java.util.Objects;
import java.util.Optional;

/**
 * Use Case for cancelling an existing booking and releasing seats back to inventory.
 */
public class CancelBookingUseCase {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    public CancelBookingUseCase(BookingRepository bookingRepository, EventRepository eventRepository) {
        this.bookingRepository = Objects.requireNonNull(bookingRepository, "BookingRepository cannot be null");
        this.eventRepository = Objects.requireNonNull(eventRepository, "EventRepository cannot be null");
    }

    public Booking execute(String rawBookingId) {
        BookingId bookingId = new BookingId(rawBookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new InvalidDomainException("Booking not found with ID: " + bookingId.value()));

        booking.cancel();

        for (BookingItem item : booking.getItems()) {
            Optional<Event> eventOptional = eventRepository.findById(item.eventId());
            if (eventOptional.isPresent()) {
                Event event = eventOptional.get();
                event.releaseSeats(item.quantity());
                eventRepository.save(event);
            }
        }

        return bookingRepository.save(booking);
    }
}
