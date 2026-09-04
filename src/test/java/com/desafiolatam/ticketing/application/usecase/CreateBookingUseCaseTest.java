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
import com.desafiolatam.ticketing.domain.model.enumtype.BookingStatus;
import com.desafiolatam.ticketing.domain.model.enumtype.EventStatus;
import com.desafiolatam.ticketing.domain.model.enumtype.MembershipTier;
import com.desafiolatam.ticketing.domain.model.valueobject.*;
import com.desafiolatam.ticketing.domain.repository.BookingRepository;
import com.desafiolatam.ticketing.domain.repository.CustomerRepository;
import com.desafiolatam.ticketing.domain.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateBookingUseCase Application Service Tests (AAA & Mockito)")
class CreateBookingUseCaseTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CreateBookingUseCase createBookingUseCase;

    private Customer createCustomer(String id, MembershipTier tier) {
        return new Customer(CustomerId.of(id), "Grace Hopper", Email.of("grace@navy.mil"), tier);
    }

    private Event createEvent(String id, double price, int capacity) {
        return new Event(EventId.of(id), "Tech Conclave", Money.of(price), SeatCapacity.of(capacity));
    }

    @Test
    @DisplayName("Should throw NullPointerException when constructor dependencies are null")
    void shouldThrowWhenConstructorReceivesNulls() {
        // Arrange, Act & Assert
        assertThrows(NullPointerException.class, () -> new CreateBookingUseCase(null, customerRepository, bookingRepository, paymentGateway, notificationService));
        assertThrows(NullPointerException.class, () -> new CreateBookingUseCase(eventRepository, null, bookingRepository, paymentGateway, notificationService));
        assertThrows(NullPointerException.class, () -> new CreateBookingUseCase(eventRepository, customerRepository, null, paymentGateway, notificationService));
        assertThrows(NullPointerException.class, () -> new CreateBookingUseCase(eventRepository, customerRepository, bookingRepository, null, notificationService));
        assertThrows(NullPointerException.class, () -> new CreateBookingUseCase(eventRepository, customerRepository, bookingRepository, paymentGateway, null));
    }

    @Test
    @DisplayName("Should execute booking successfully for VIP customer with 20% discount")
    void shouldExecuteBookingSuccessfullyForVipCustomer() {
        // Arrange
        Customer customer = createCustomer("CUST-VIP", MembershipTier.VIP);
        Event event = createEvent("EVT-01", 100.0, 10);
        BookingRequestDTO request = new BookingRequestDTO("CUST-VIP", "EVT-01", 2);

        when(customerRepository.findById(CustomerId.of("CUST-VIP"))).thenReturn(Optional.of(customer));
        when(eventRepository.findById(EventId.of("EVT-01"))).thenReturn(Optional.of(event));
        when(paymentGateway.charge(CustomerId.of("CUST-VIP"), Money.of(160.0))).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        BookingResponseDTO response = createBookingUseCase.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals("Grace Hopper", response.customerName());
        assertEquals("Tech Conclave", response.eventName());
        assertEquals(2, response.quantity());
        assertEquals(200.0, response.grossTotal(), 0.001);
        assertEquals(40.0, response.discountAmount(), 0.001);
        assertEquals(160.0, response.netTotal(), 0.001);
        assertEquals(BookingStatus.CONFIRMED, response.status());
        assertEquals(8, event.getAvailableSeats());

        verify(eventRepository, times(1)).save(event);
        verify(paymentGateway, times(1)).charge(CustomerId.of("CUST-VIP"), Money.of(160.0));
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(notificationService, times(1)).sendBookingConfirmation(eq(customer), any(Booking.class));
    }

    @Test
    @DisplayName("Should throw InvalidDomainException when booking request is null or quantity non-positive")
    void shouldThrowWhenRequestIsInvalid() {
        // Arrange, Act & Assert
        assertThrows(InvalidDomainException.class, () -> createBookingUseCase.execute(null));
        assertThrows(InvalidDomainException.class, () -> createBookingUseCase.execute(new BookingRequestDTO("CUST-01", "EVT-01", 0)));
        assertThrows(InvalidDomainException.class, () -> createBookingUseCase.execute(new BookingRequestDTO("CUST-01", "EVT-01", -2)));
        verifyNoInteractions(eventRepository, customerRepository, bookingRepository, paymentGateway, notificationService);
    }

    @Test
    @DisplayName("Should throw InvalidDomainException when customer is not found")
    void shouldThrowWhenCustomerNotFound() {
        // Arrange
        BookingRequestDTO request = new BookingRequestDTO("CUST-404", "EVT-01", 2);
        when(customerRepository.findById(CustomerId.of("CUST-404"))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidDomainException.class, () -> createBookingUseCase.execute(request));
        verify(customerRepository, times(1)).findById(CustomerId.of("CUST-404"));
        verifyNoInteractions(eventRepository, paymentGateway, bookingRepository, notificationService);
    }

    @Test
    @DisplayName("Should throw EventNotFoundException when event is not found")
    void shouldThrowWhenEventNotFound() {
        // Arrange
        Customer customer = createCustomer("CUST-01", MembershipTier.REGULAR);
        BookingRequestDTO request = new BookingRequestDTO("CUST-01", "EVT-404", 2);

        when(customerRepository.findById(CustomerId.of("CUST-01"))).thenReturn(Optional.of(customer));
        when(eventRepository.findById(EventId.of("EVT-404"))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EventNotFoundException.class, () -> createBookingUseCase.execute(request));
        verify(customerRepository, times(1)).findById(CustomerId.of("CUST-01"));
        verify(eventRepository, times(1)).findById(EventId.of("EVT-404"));
        verifyNoInteractions(paymentGateway, bookingRepository, notificationService);
    }

    @Test
    @DisplayName("Should rollback seats and throw PaymentFailedException when payment is rejected")
    void shouldRollbackSeatsWhenPaymentFails() {
        // Arrange
        Customer customer = createCustomer("CUST-01", MembershipTier.REGULAR);
        Event event = createEvent("EVT-01", 50.0, 10);
        BookingRequestDTO request = new BookingRequestDTO("CUST-01", "EVT-01", 2);

        when(customerRepository.findById(CustomerId.of("CUST-01"))).thenReturn(Optional.of(customer));
        when(eventRepository.findById(EventId.of("EVT-01"))).thenReturn(Optional.of(event));
        when(paymentGateway.charge(CustomerId.of("CUST-01"), Money.of(100.0))).thenReturn(false);

        // Act & Assert
        PaymentFailedException exception = assertThrows(
                PaymentFailedException.class,
                () -> createBookingUseCase.execute(request)
        );
        assertTrue(exception.getMessage().contains("Payment processing rejected"));
        assertEquals(10, event.getAvailableSeats()); // Rollback verified

        verify(eventRepository, times(2)).save(event); // 1st reserve, 2nd release rollback
        verify(notificationService, times(1)).sendBookingFailureAlert(eq(customer), anyString());
        verify(notificationService, never()).sendBookingConfirmation(any(), any());
        verifyNoInteractions(bookingRepository);
    }
}
