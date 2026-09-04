package com.desafiolatam.ticketing.application.usecase;

import com.desafiolatam.ticketing.application.dto.BookingRequestDTO;
import com.desafiolatam.ticketing.application.dto.BookingResponseDTO;
import com.desafiolatam.ticketing.application.port.NotificationService;
import com.desafiolatam.ticketing.application.port.PaymentGateway;
import com.desafiolatam.ticketing.domain.exception.EventNotFoundException;
import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import com.desafiolatam.ticketing.domain.exception.PaymentFailedException;
import com.desafiolatam.ticketing.domain.model.aggregate.Booking;
import com.desafiolatam.ticketing.domain.model.aggregate.Event;
import com.desafiolatam.ticketing.domain.model.entity.Customer;
import com.desafiolatam.ticketing.domain.model.valueobject.*;
import com.desafiolatam.ticketing.domain.repository.BookingRepository;
import com.desafiolatam.ticketing.domain.repository.CustomerRepository;
import com.desafiolatam.ticketing.domain.repository.EventRepository;
import java.util.Collections;
import java.util.Objects;

/**
 * Use Case / Application Service orchestrating the ticket reservation and purchase workflow.
 */
public class CreateBookingUseCase {

    private final EventRepository eventRepository;
    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;
    private final PaymentGateway paymentGateway;
    private final NotificationService notificationService;

    /**
     * Dependency injection by constructor for decoupled testing and clean architecture compliance.
     */
    public CreateBookingUseCase(
            EventRepository eventRepository,
            CustomerRepository customerRepository,
            BookingRepository bookingRepository,
            PaymentGateway paymentGateway,
            NotificationService notificationService
    ) {
        this.eventRepository = Objects.requireNonNull(eventRepository, "EventRepository cannot be null");
        this.customerRepository = Objects.requireNonNull(customerRepository, "CustomerRepository cannot be null");
        this.bookingRepository = Objects.requireNonNull(bookingRepository, "BookingRepository cannot be null");
        this.paymentGateway = Objects.requireNonNull(paymentGateway, "PaymentGateway cannot be null");
        this.notificationService = Objects.requireNonNull(notificationService, "NotificationService cannot be null");
    }

    /**
     * Executes the end-to-end booking transaction.
     *
     * @param request DTO containing customerId, eventId, and quantity
     * @return BookingResponseDTO with order details
     */
    public BookingResponseDTO execute(BookingRequestDTO request) {
        if (request == null) {
            throw new InvalidDomainException("Booking request cannot be null");
        }
        if (request.quantity() <= 0) {
            throw new InvalidDomainException("Ticket quantity must be greater than zero");
        }

        CustomerId customerId = new CustomerId(request.customerId());
        EventId eventId = new EventId(request.eventId());

        // 1. Fetch Customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new InvalidDomainException("Customer not found with ID: " + customerId.value()));

        // 2. Fetch Event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId.value()));

        // 3. Reserve Seats (encapsulates domain invariants)
        event.reserveSeats(request.quantity());
        eventRepository.save(event);

        // 4. Calculate Financials
        Money grossTotal = event.getBasePrice().multiply(request.quantity());
        Money discountAmount = customer.getTier().calculateDiscount(grossTotal);
        Money netTotal = grossTotal.subtract(discountAmount);

        // 5. Process Payment via Gateway
        boolean paymentSuccess = paymentGateway.charge(customerId, netTotal);
        if (!paymentSuccess) {
            // Rollback seat reservation
            event.releaseSeats(request.quantity());
            eventRepository.save(event);
            notificationService.sendBookingFailureAlert(customer, "Payment rejected for amount: " + netTotal);
            throw new PaymentFailedException("Payment processing rejected for customer: " + customerId.value());
        }

        // 6. Create Confirmed Booking Aggregate
        BookingId bookingId = BookingId.generate();
        BookingItem item = BookingItem.of(event.getId(), event.getName(), event.getBasePrice(), request.quantity());
        Booking booking = new Booking(bookingId, customer, Collections.singletonList(item), discountAmount);
        booking.confirm();

        Booking savedBooking = bookingRepository.save(booking);

        // 7. Dispatch Notification
        notificationService.sendBookingConfirmation(customer, savedBooking);

        return new BookingResponseDTO(
                savedBooking.getId().value(),
                customer.getName(),
                event.getName(),
                request.quantity(),
                savedBooking.getGrossTotal().amount(),
                savedBooking.getDiscountAmount().amount(),
                savedBooking.getNetTotal().amount(),
                savedBooking.getStatus(),
                "Booking successfully created and confirmed"
        );
    }
}
