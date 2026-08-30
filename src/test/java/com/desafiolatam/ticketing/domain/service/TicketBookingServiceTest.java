package com.desafiolatam.ticketing.domain.service;

import com.desafiolatam.ticketing.domain.dto.BookingRequest;
import com.desafiolatam.ticketing.domain.dto.BookingResponse;
import com.desafiolatam.ticketing.domain.exception.EventNotActiveException;
import com.desafiolatam.ticketing.domain.exception.EventNotFoundException;
import com.desafiolatam.ticketing.domain.exception.InsufficientSeatsException;
import com.desafiolatam.ticketing.domain.exception.InvalidBookingException;
import com.desafiolatam.ticketing.domain.exception.PaymentFailedException;
import com.desafiolatam.ticketing.domain.model.Booking;
import com.desafiolatam.ticketing.domain.model.BookingItem;
import com.desafiolatam.ticketing.domain.model.BookingStatus;
import com.desafiolatam.ticketing.domain.model.Customer;
import com.desafiolatam.ticketing.domain.model.Event;
import com.desafiolatam.ticketing.domain.model.EventStatus;
import com.desafiolatam.ticketing.domain.model.MembershipTier;
import com.desafiolatam.ticketing.domain.port.BookingRepository;
import com.desafiolatam.ticketing.domain.port.EventRepository;
import com.desafiolatam.ticketing.domain.port.NotificationService;
import com.desafiolatam.ticketing.domain.port.PaymentGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TicketBookingService Business Logic & Mockito Isolation Tests")
class TicketBookingServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TicketBookingService bookingService;

    private Customer createCustomer(String id, MembershipTier tier) {
        return new Customer(id, "Test Customer", "customer@desafiolatam.com", tier);
    }

    private Event createEvent(String id, double price, int capacity) {
        return new Event(id, "Neon Music Fest", price, capacity);
    }

    @Test
    @DisplayName("Should throw NullPointerException when constructor dependencies are null")
    void shouldThrowWhenConstructorDependenciesAreNull() {
        // Arrange, Act & Assert
        assertThrows(NullPointerException.class, () -> new TicketBookingService(null, bookingRepository, paymentGateway, notificationService));
        assertThrows(NullPointerException.class, () -> new TicketBookingService(eventRepository, null, paymentGateway, notificationService));
        assertThrows(NullPointerException.class, () -> new TicketBookingService(eventRepository, bookingRepository, null, notificationService));
        assertThrows(NullPointerException.class, () -> new TicketBookingService(eventRepository, bookingRepository, paymentGateway, null));
    }

    @Test
    @DisplayName("Should successfully create booking for REGULAR customer with 0% discount")
    void shouldCreateBookingSuccessfullyForRegularCustomer() {
        // Arrange
        Customer customer = createCustomer("CUST-REG", MembershipTier.REGULAR);
        BookingRequest request = new BookingRequest("EVT-01", 2);
        Event event = createEvent("EVT-01", 50.0, 10); // gross = 100.0, discount = 0.0, net = 100.0

        when(eventRepository.findById("EVT-01")).thenReturn(Optional.of(event));
        when(paymentGateway.charge("CUST-REG", 100.0)).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        BookingResponse response = bookingService.createBooking(customer, request);

        // Assert
        assertNotNull(response);
        assertEquals("Test Customer", response.getCustomerName());
        assertEquals("Neon Music Fest", response.getEventName());
        assertEquals(2, response.getQuantity());
        assertEquals(100.0, response.getTotalPaid(), 0.001);
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
        assertEquals(8, event.getAvailableSeats());

        verify(eventRepository, times(1)).findById("EVT-01");
        verify(eventRepository, times(1)).save(event);
        verify(paymentGateway, times(1)).charge("CUST-REG", 100.0);
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(notificationService, times(1)).sendBookingConfirmation(eq(customer), any(Booking.class));
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    @DisplayName("Should successfully create booking for PREMIUM customer with 10% discount")
    void shouldCreateBookingSuccessfullyForPremiumCustomer() {
        // Arrange
        Customer customer = createCustomer("CUST-PREM", MembershipTier.PREMIUM);
        BookingRequest request = new BookingRequest("EVT-02", 2);
        Event event = createEvent("EVT-02", 100.0, 20); // gross = 200.0, discount = 20.0, net = 180.0

        when(eventRepository.findById("EVT-02")).thenReturn(Optional.of(event));
        when(paymentGateway.charge("CUST-PREM", 180.0)).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        BookingResponse response = bookingService.createBooking(customer, request);

        // Assert
        assertNotNull(response);
        assertEquals(180.0, response.getTotalPaid(), 0.001);
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();
        assertEquals(200.0, savedBooking.getGrossTotal(), 0.001);
        assertEquals(20.0, savedBooking.getDiscountAmount(), 0.001);
        assertEquals(180.0, savedBooking.getNetTotal(), 0.001);
    }

    @Test
    @DisplayName("Should successfully create booking for VIP customer with 20% discount")
    void shouldCreateBookingSuccessfullyForVipCustomer() {
        // Arrange
        Customer customer = createCustomer("CUST-VIP", MembershipTier.VIP);
        BookingRequest request = new BookingRequest("EVT-03", 3);
        Event event = createEvent("EVT-03", 100.0, 30); // gross = 300.0, discount = 60.0, net = 240.0

        when(eventRepository.findById("EVT-03")).thenReturn(Optional.of(event));
        when(paymentGateway.charge("CUST-VIP", 240.0)).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        BookingResponse response = bookingService.createBooking(customer, request);

        // Assert
        assertNotNull(response);
        assertEquals(240.0, response.getTotalPaid(), 0.001);
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
        verify(notificationService).sendBookingConfirmation(eq(customer), any(Booking.class));
    }

    @Test
    @DisplayName("Should transition event to SOLD_OUT when booking reserves all remaining seats")
    void shouldTransitionEventToSoldOutWhenBookingTakesAllSeats() {
        // Arrange
        Customer customer = createCustomer("CUST-01", MembershipTier.REGULAR);
        BookingRequest request = new BookingRequest("EVT-04", 5);
        Event event = createEvent("EVT-04", 40.0, 5);

        when(eventRepository.findById("EVT-04")).thenReturn(Optional.of(event));
        when(paymentGateway.charge(eq("CUST-01"), anyDouble())).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        BookingResponse response = bookingService.createBooking(customer, request);

        // Assert
        assertEquals(0, event.getAvailableSeats());
        assertEquals(EventStatus.SOLD_OUT, event.getStatus());
        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
    }

    @Test
    @DisplayName("Should throw InvalidBookingException when customer is null")
    void shouldThrowWhenCustomerIsNull() {
        // Arrange
        BookingRequest request = new BookingRequest("EVT-01", 2);

        // Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.createBooking(null, request)
        );
        assertEquals("Customer cannot be null", exception.getMessage());
        verifyNoInteractions(eventRepository, bookingRepository, paymentGateway, notificationService);
    }

    @Test
    @DisplayName("Should throw InvalidBookingException when booking request is null")
    void shouldThrowWhenBookingRequestIsNull() {
        // Arrange
        Customer customer = createCustomer("CUST-01", MembershipTier.REGULAR);

        // Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.createBooking(customer, null)
        );
        assertEquals("Booking request cannot be null", exception.getMessage());
        verifyNoInteractions(eventRepository, bookingRepository, paymentGateway, notificationService);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    @DisplayName("Should throw InvalidBookingException when requested quantity is zero or negative")
    void shouldThrowWhenQuantityIsZeroOrNegative(int invalidQty) {
        // Arrange
        Customer customer = createCustomer("CUST-01", MembershipTier.REGULAR);
        BookingRequest request = new BookingRequest("EVT-01", invalidQty);

        // Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.createBooking(customer, request)
        );
        assertEquals("Ticket quantity must be greater than zero", exception.getMessage());
        verifyNoInteractions(eventRepository, bookingRepository, paymentGateway, notificationService);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Should throw InvalidBookingException when event ID is null or blank")
    void shouldThrowWhenEventIdIsNullOrBlank(String invalidEventId) {
        // Arrange
        Customer customer = createCustomer("CUST-01", MembershipTier.REGULAR);
        BookingRequest request = new BookingRequest(invalidEventId, 2);

        // Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.createBooking(customer, request)
        );
        assertEquals("Event ID cannot be null or empty", exception.getMessage());
        verifyNoInteractions(eventRepository, bookingRepository, paymentGateway, notificationService);
    }

    @Test
    @DisplayName("Should throw EventNotFoundException when event ID does not exist in repository")
    void shouldThrowWhenEventDoesNotExistInRepository() {
        // Arrange
        Customer customer = createCustomer("CUST-01", MembershipTier.REGULAR);
        BookingRequest request = new BookingRequest("EVT-999", 2);

        when(eventRepository.findById("EVT-999")).thenReturn(Optional.empty());

        // Act & Assert
        EventNotFoundException exception = assertThrows(
                EventNotFoundException.class,
                () -> bookingService.createBooking(customer, request)
        );
        assertTrue(exception.getMessage().contains("Event not found with ID: EVT-999"));
        verify(eventRepository, times(1)).findById("EVT-999");
        verifyNoInteractions(paymentGateway, bookingRepository, notificationService);
    }

    @Test
    @DisplayName("Should throw EventNotActiveException when event is inactive or cancelled")
    void shouldThrowWhenEventIsInactive() {
        // Arrange
        Customer customer = createCustomer("CUST-01", MembershipTier.REGULAR);
        BookingRequest request = new BookingRequest("EVT-05", 2);
        Event event = createEvent("EVT-05", 50.0, 10);
        event.setStatus(EventStatus.CANCELLED);

        when(eventRepository.findById("EVT-05")).thenReturn(Optional.of(event));

        // Act & Assert
        assertThrows(
                EventNotActiveException.class,
                () -> bookingService.createBooking(customer, request)
        );
        verify(eventRepository, times(1)).findById("EVT-05");
        verify(eventRepository, never()).save(any(Event.class));
        verifyNoInteractions(paymentGateway, bookingRepository, notificationService);
    }

    @Test
    @DisplayName("Should throw InsufficientSeatsException when event does not have enough capacity")
    void shouldThrowWhenEventHasInsufficientSeats() {
        // Arrange
        Customer customer = createCustomer("CUST-01", MembershipTier.REGULAR);
        BookingRequest request = new BookingRequest("EVT-06", 15);
        Event event = createEvent("EVT-06", 50.0, 10);

        when(eventRepository.findById("EVT-06")).thenReturn(Optional.of(event));

        // Act & Assert
        assertThrows(
                InsufficientSeatsException.class,
                () -> bookingService.createBooking(customer, request)
        );
        verify(eventRepository, times(1)).findById("EVT-06");
        verify(eventRepository, never()).save(any(Event.class));
        verifyNoInteractions(paymentGateway, bookingRepository, notificationService);
    }

    @Test
    @DisplayName("Should rollback reserved seats, send failure alert and throw PaymentFailedException when payment is rejected")
    void shouldRollbackSeatsAndThrowWhenPaymentFails() {
        // Arrange
        Customer customer = createCustomer("CUST-FAIL", MembershipTier.REGULAR);
        BookingRequest request = new BookingRequest("EVT-07", 3);
        Event event = createEvent("EVT-07", 60.0, 10);

        when(eventRepository.findById("EVT-07")).thenReturn(Optional.of(event));
        when(paymentGateway.charge("CUST-FAIL", 180.0)).thenReturn(false);

        // Act & Assert
        PaymentFailedException exception = assertThrows(
                PaymentFailedException.class,
                () -> bookingService.createBooking(customer, request)
        );
        assertTrue(exception.getMessage().contains("Payment processing was rejected for customer: CUST-FAIL"));

        // Verify seats rollback: initial 10 -> reserved to 7 -> released back to 10
        assertEquals(10, event.getAvailableSeats());
        assertEquals(EventStatus.ACTIVE, event.getStatus());

        verify(eventRepository, times(2)).save(event); // 1st on reserve, 2nd on rollback
        verify(notificationService, times(1)).sendBookingFailureAlert(eq(customer), anyString());
        verify(notificationService, never()).sendBookingConfirmation(any(), any());
        verifyNoInteractions(bookingRepository);
    }

    @Test
    @DisplayName("Should cancel booking successfully and release seats back to event")
    void shouldCancelBookingSuccessfully() {
        // Arrange
        Customer customer = createCustomer("CUST-01", MembershipTier.REGULAR);
        BookingItem item = new BookingItem("EVT-08", "Festival", 50.0, 2);
        Booking booking = new Booking("BKG-CANCEL-1", customer, Collections.singletonList(item), 0.0);
        booking.confirm();

        Event event = createEvent("EVT-08", 50.0, 10);
        event.reserveSeats(4); // 6 available
        assertEquals(6, event.getAvailableSeats());

        when(bookingRepository.findById("BKG-CANCEL-1")).thenReturn(Optional.of(booking));
        when(eventRepository.findById("EVT-08")).thenReturn(Optional.of(event));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Booking cancelledBooking = bookingService.cancelBooking("BKG-CANCEL-1");

        // Assert
        assertEquals(BookingStatus.CANCELLED, cancelledBooking.getStatus());
        assertEquals(8, event.getAvailableSeats()); // 6 + 2 released = 8

        verify(bookingRepository, times(1)).findById("BKG-CANCEL-1");
        verify(eventRepository, times(1)).findById("EVT-08");
        verify(eventRepository, times(1)).save(event);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    @DisplayName("Should cancel booking successfully even if event is not found in repository")
    void shouldCancelBookingEvenIfEventNotFound() {
        // Arrange
        Customer customer = createCustomer("CUST-01", MembershipTier.REGULAR);
        BookingItem item = new BookingItem("EVT-DELETED", "Old Festival", 50.0, 2);
        Booking booking = new Booking("BKG-CANCEL-2", customer, Collections.singletonList(item), 0.0);
        booking.confirm();

        when(bookingRepository.findById("BKG-CANCEL-2")).thenReturn(Optional.of(booking));
        when(eventRepository.findById("EVT-DELETED")).thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Booking cancelledBooking = bookingService.cancelBooking("BKG-CANCEL-2");

        // Assert
        assertEquals(BookingStatus.CANCELLED, cancelledBooking.getStatus());
        verify(bookingRepository, times(1)).findById("BKG-CANCEL-2");
        verify(eventRepository, times(1)).findById("EVT-DELETED");
        verify(eventRepository, never()).save(any(Event.class));
        verify(bookingRepository, times(1)).save(booking);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Should throw InvalidBookingException when cancelling with null or blank ID")
    void shouldThrowWhenCancellingWithInvalidId(String invalidId) {
        // Arrange, Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.cancelBooking(invalidId)
        );
        assertEquals("Booking ID cannot be null or empty", exception.getMessage());
        verifyNoInteractions(bookingRepository, eventRepository, paymentGateway, notificationService);
    }

    @Test
    @DisplayName("Should throw InvalidBookingException when booking to cancel is not found")
    void shouldThrowWhenBookingToCancelNotFound() {
        // Arrange
        when(bookingRepository.findById("BKG-404")).thenReturn(Optional.empty());

        // Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.cancelBooking("BKG-404")
        );
        assertTrue(exception.getMessage().contains("Booking not found with ID: BKG-404"));
        verify(bookingRepository, times(1)).findById("BKG-404");
        verifyNoInteractions(eventRepository, paymentGateway, notificationService);
    }

    @Test
    @DisplayName("Should throw InvalidBookingException when attempting to cancel an already cancelled booking")
    void shouldThrowWhenBookingIsAlreadyCancelled() {
        // Arrange
        Customer customer = createCustomer("CUST-01", MembershipTier.REGULAR);
        BookingItem item = new BookingItem("EVT-01", "Festival", 50.0, 2);
        Booking booking = new Booking("BKG-CANCELLED", customer, Collections.singletonList(item), 0.0);
        booking.cancel(); // already cancelled

        when(bookingRepository.findById("BKG-CANCELLED")).thenReturn(Optional.of(booking));

        // Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> bookingService.cancelBooking("BKG-CANCELLED")
        );
        assertEquals("Booking is already cancelled", exception.getMessage());
        verify(bookingRepository, times(1)).findById("BKG-CANCELLED");
        verifyNoInteractions(eventRepository);
    }
}
