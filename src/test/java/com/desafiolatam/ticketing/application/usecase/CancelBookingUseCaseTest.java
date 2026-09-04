package com.desafiolatam.ticketing.application.usecase;

import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import com.desafiolatam.ticketing.domain.model.aggregate.Booking;
import com.desafiolatam.ticketing.domain.model.aggregate.Event;
import com.desafiolatam.ticketing.domain.model.entity.Customer;
import com.desafiolatam.ticketing.domain.model.enumtype.BookingStatus;
import com.desafiolatam.ticketing.domain.model.enumtype.MembershipTier;
import com.desafiolatam.ticketing.domain.model.valueobject.*;
import com.desafiolatam.ticketing.domain.repository.BookingRepository;
import com.desafiolatam.ticketing.domain.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CancelBookingUseCase Tests (AAA & Mockito)")
class CancelBookingUseCaseTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CancelBookingUseCase cancelBookingUseCase;

    @Test
    @DisplayName("Should cancel booking successfully and release seats back to event")
    void shouldCancelBookingSuccessfully() {
        // Arrange
        BookingId bookingId = new BookingId("BKG-01");
        EventId eventId = EventId.of("EVT-01");
        Customer customer = new Customer(CustomerId.of("CUST-01"), "Ada", Email.of("ada@test.com"), MembershipTier.REGULAR);
        BookingItem item = BookingItem.of(eventId, "Festival", Money.of(50.0), 2);
        Booking booking = new Booking(bookingId, customer, Collections.singletonList(item), Money.ZERO);
        booking.confirm();

        Event event = new Event(eventId, "Festival", Money.of(50.0), new SeatCapacity(10, 8)); // 8 available

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Booking cancelled = cancelBookingUseCase.execute("BKG-01");

        // Assert
        assertEquals(BookingStatus.CANCELLED, cancelled.getStatus());
        assertEquals(10, event.getAvailableSeats()); // 8 + 2 = 10

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).save(event);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    @DisplayName("Should cancel booking even if event is deleted from repository")
    void shouldCancelBookingWhenEventNotFound() {
        // Arrange
        BookingId bookingId = new BookingId("BKG-02");
        EventId eventId = EventId.of("EVT-OLD");
        Customer customer = new Customer(CustomerId.of("CUST-01"), "Ada", Email.of("ada@test.com"), MembershipTier.REGULAR);
        BookingItem item = BookingItem.of(eventId, "Old Event", Money.of(50.0), 2);
        Booking booking = new Booking(bookingId, customer, Collections.singletonList(item), Money.ZERO);
        booking.confirm();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Booking cancelled = cancelBookingUseCase.execute("BKG-02");

        // Assert
        assertEquals(BookingStatus.CANCELLED, cancelled.getStatus());
        verify(eventRepository, never()).save(any());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    @DisplayName("Should throw InvalidDomainException when booking is not found")
    void shouldThrowWhenBookingNotFound() {
        // Arrange
        when(bookingRepository.findById(new BookingId("BKG-404"))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidDomainException.class, () -> cancelBookingUseCase.execute("BKG-404"));
    }

    @Test
    @DisplayName("Should throw NullPointerException when constructor dependencies are null")
    void shouldThrowWhenConstructorDependenciesNull() {
        // Arrange, Act & Assert
        assertThrows(NullPointerException.class, () -> new CancelBookingUseCase(null, eventRepository));
        assertThrows(NullPointerException.class, () -> new CancelBookingUseCase(bookingRepository, null));
    }
}
