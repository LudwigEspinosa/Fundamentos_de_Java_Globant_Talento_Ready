package com.desafiolatam.ticketing.domain.service;

import com.desafiolatam.ticketing.domain.dto.BookingRequest;
import com.desafiolatam.ticketing.domain.dto.BookingResponse;
import com.desafiolatam.ticketing.domain.exception.EventNotFoundException;
import com.desafiolatam.ticketing.domain.exception.InvalidBookingException;
import com.desafiolatam.ticketing.domain.exception.PaymentFailedException;
import com.desafiolatam.ticketing.domain.model.Booking;
import com.desafiolatam.ticketing.domain.model.BookingItem;
import com.desafiolatam.ticketing.domain.model.Customer;
import com.desafiolatam.ticketing.domain.model.Event;
import com.desafiolatam.ticketing.domain.port.BookingRepository;
import com.desafiolatam.ticketing.domain.port.EventRepository;
import com.desafiolatam.ticketing.domain.port.NotificationService;
import com.desafiolatam.ticketing.domain.port.PaymentGateway;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain Service that orchestrates ticket reservations, pricing, payment processing,
 * and notification dispatch using dependency injection.
 */
public class TicketBookingService {

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final PaymentGateway paymentGateway;
    private final NotificationService notificationService;

    /**
     * Constructor-based dependency injection for full testability and decoupling.
     */
    public TicketBookingService(
            EventRepository eventRepository,
            BookingRepository bookingRepository,
            PaymentGateway paymentGateway,
            NotificationService notificationService
    ) {
        this.eventRepository = Objects.requireNonNull(eventRepository, "EventRepository cannot be null");
        this.bookingRepository = Objects.requireNonNull(bookingRepository, "BookingRepository cannot be null");
        this.paymentGateway = Objects.requireNonNull(paymentGateway, "PaymentGateway cannot be null");
        this.notificationService = Objects.requireNonNull(notificationService, "NotificationService cannot be null");
    }

    /**
     * Executes the end-to-end booking flow:
     * 1. Validates request and customer data.
     * 2. Retrieves event and reserves seat inventory.
     * 3. Calculates customer discounts based on membership tier.
     * 4. Processes financial transaction via payment gateway.
     * 5. Rolls back seat inventory and notifies on payment failure.
     * 6. Persists confirmed booking and sends confirmation notification.
     *
     * @param customer customer placing the booking
     * @param request  requested event ID and quantity
     * @return BookingResponse with transaction outcome
     */
    public BookingResponse createBooking(Customer customer, BookingRequest request) {
        if (customer == null) {
            throw new InvalidBookingException("Customer cannot be null");
        }
        if (request == null) {
            throw new InvalidBookingException("Booking request cannot be null");
        }
        if (request.getQuantity() <= 0) {
            throw new InvalidBookingException("Ticket quantity must be greater than zero");
        }
        if (request.getEventId() == null || request.getEventId().trim().isEmpty()) {
            throw new InvalidBookingException("Event ID cannot be null or empty");
        }

        String eventId = request.getEventId().trim();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId));

        // 1. Reserve seats (validates active status and available capacity)
        event.reserveSeats(request.getQuantity());
        eventRepository.save(event);

        // 2. Calculate totals and membership tier discounts
        double grossPrice = event.getBasePrice() * request.getQuantity();
        double discountAmount = customer.getTier().calculateDiscount(grossPrice);
        double netPrice = Math.max(0.0, grossPrice - discountAmount);

        // 3. Process payment
        boolean paymentSuccess = paymentGateway.charge(customer.getId(), netPrice);
        if (!paymentSuccess) {
            // Rollback seat reservation
            event.releaseSeats(request.getQuantity());
            eventRepository.save(event);
            notificationService.sendBookingFailureAlert(customer, "Payment rejected for amount: " + netPrice);
            throw new PaymentFailedException("Payment processing was rejected for customer: " + customer.getId());
        }

        // 4. Create and save confirmed booking
        String generatedBookingId = "BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        BookingItem item = new BookingItem(event.getId(), event.getName(), event.getBasePrice(), request.getQuantity());
        Booking booking = new Booking(generatedBookingId, customer, Collections.singletonList(item), discountAmount);
        booking.confirm();

        Booking savedBooking = bookingRepository.save(booking);

        // 5. Send confirmation notification
        notificationService.sendBookingConfirmation(customer, savedBooking);

        return new BookingResponse(
                savedBooking.getId(),
                customer.getName(),
                event.getName(),
                request.getQuantity(),
                savedBooking.getNetTotal(),
                savedBooking.getStatus(),
                "Booking successfully created and confirmed"
        );
    }

    /**
     * Cancels an existing booking and restores seat capacity to the event inventory.
     *
     * @param bookingId ID of the booking to cancel
     * @return updated cancelled booking
     */
    public Booking cancelBooking(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new InvalidBookingException("Booking ID cannot be null or empty");
        }

        String cleanId = bookingId.trim();
        Booking booking = bookingRepository.findById(cleanId)
                .orElseThrow(() -> new InvalidBookingException("Booking not found with ID: " + cleanId));

        booking.cancel();

        for (BookingItem item : booking.getItems()) {
            Optional<Event> eventOptional = eventRepository.findById(item.getEventId());
            if (eventOptional.isPresent()) {
                Event event = eventOptional.get();
                event.releaseSeats(item.getQuantity());
                eventRepository.save(event);
            }
        }

        return bookingRepository.save(booking);
    }
}
