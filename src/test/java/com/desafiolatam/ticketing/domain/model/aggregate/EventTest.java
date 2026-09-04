package com.desafiolatam.ticketing.domain.model.aggregate;

import com.desafiolatam.ticketing.domain.exception.EventNotActiveException;
import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import com.desafiolatam.ticketing.domain.model.enumtype.EventStatus;
import com.desafiolatam.ticketing.domain.model.valueobject.EventId;
import com.desafiolatam.ticketing.domain.model.valueobject.Money;
import com.desafiolatam.ticketing.domain.model.valueobject.SeatCapacity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Event Aggregate Root Tests")
class EventTest {

    @Test
    @DisplayName("Should create event aggregate with initial ACTIVE state")
    void shouldCreateEventAggregateSuccessfully() {
        // Arrange
        EventId eventId = EventId.of("EVT-01");
        String name = "Neon Cyberpunk Festival";
        Money price = Money.of(50.0);
        SeatCapacity capacity = SeatCapacity.of(100);

        // Act
        Event event = new Event(eventId, name, price, capacity);

        // Assert
        assertEquals(eventId, event.getId());
        assertEquals("Neon Cyberpunk Festival", event.getName());
        assertEquals(price, event.getBasePrice());
        assertEquals(capacity, event.getSeatCapacity());
        assertEquals(100, event.getAvailableSeats());
        assertEquals(100, event.getTotalCapacity());
        assertEquals(EventStatus.ACTIVE, event.getStatus());
        assertTrue(event.isAvailable());
        assertTrue(event.toString().contains("Neon Cyberpunk Festival"));
    }

    @Test
    @DisplayName("Should initialize as SOLD_OUT if created with zero available seats")
    void shouldInitializeAsSoldOutWhenNoSeatsAvailable() {
        // Arrange & Act
        Event event = new Event(EventId.of("EVT-02"), "Sold Out Gig", Money.of(40.0), new SeatCapacity(10, 0));

        // Assert
        assertEquals(EventStatus.SOLD_OUT, event.getStatus());
        assertFalse(event.isAvailable());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Should throw InvalidDomainException when event name is invalid")
    void shouldThrowWhenEventNameIsInvalid(String invalidName) {
        // Arrange, Act & Assert
        assertThrows(
                InvalidDomainException.class,
                () -> new Event(EventId.of("EVT-01"), invalidName, Money.of(50.0), SeatCapacity.of(10))
        );
    }

    @Test
    @DisplayName("Should throw NullPointerException when required fields are null")
    void shouldThrowWhenRequiredFieldsAreNull() {
        // Arrange, Act & Assert
        assertThrows(NullPointerException.class, () -> new Event(null, "Event", Money.of(50.0), SeatCapacity.of(10)));
        assertThrows(NullPointerException.class, () -> new Event(EventId.of("EVT-01"), "Event", null, SeatCapacity.of(10)));
        assertThrows(NullPointerException.class, () -> new Event(EventId.of("EVT-01"), "Event", Money.of(50.0), null));
        Event event = new Event(EventId.of("EVT-01"), "Event", Money.of(50.0), SeatCapacity.of(10));
        assertThrows(NullPointerException.class, () -> event.setStatus(null));
    }

    @Test
    @DisplayName("Should reserve seats and transition to SOLD_OUT when capacity reaches zero")
    void shouldReserveSeatsAndTransitionToSoldOut() {
        // Arrange
        Event event = new Event(EventId.of("EVT-03"), "Indie Show", Money.of(30.0), SeatCapacity.of(10));

        // Act - partial reservation
        event.reserveSeats(6);
        assertEquals(4, event.getAvailableSeats());
        assertEquals(EventStatus.ACTIVE, event.getStatus());

        // Act - reserve remainder
        event.reserveSeats(4);
        assertEquals(0, event.getAvailableSeats());
        assertEquals(EventStatus.SOLD_OUT, event.getStatus());
        assertFalse(event.isAvailable());
    }

    @Test
    @DisplayName("Should throw EventNotActiveException when attempting to reserve on non-active event")
    void shouldThrowWhenReservingOnNonActiveEvent() {
        // Arrange
        Event event = new Event(EventId.of("EVT-04"), "Cancelled Event", Money.of(20.0), SeatCapacity.of(10));
        event.setStatus(EventStatus.CANCELLED);

        // Act & Assert
        assertThrows(EventNotActiveException.class, () -> event.reserveSeats(2));
    }

    @Test
    @DisplayName("Should release seats and restore ACTIVE status from SOLD_OUT")
    void shouldReleaseSeatsAndRestoreActiveStatus() {
        // Arrange
        Event event = new Event(EventId.of("EVT-05"), "Show", Money.of(25.0), SeatCapacity.of(5));
        event.reserveSeats(5);
        assertEquals(EventStatus.SOLD_OUT, event.getStatus());

        // Act
        event.releaseSeats(2);

        // Assert
        assertEquals(2, event.getAvailableSeats());
        assertEquals(EventStatus.ACTIVE, event.getStatus());
        assertTrue(event.isAvailable());
    }

    @Test
    @DisplayName("Should evaluate equals and hashCode based on identity EventId")
    void shouldEvaluateEqualsAndHashCode() {
        // Arrange
        Event e1 = new Event(EventId.of("EVT-01"), "Fest 1", Money.of(50.0), SeatCapacity.of(10));
        Event e2 = new Event(EventId.of("EVT-01"), "Fest 2", Money.of(60.0), SeatCapacity.of(20));
        Event e3 = new Event(EventId.of("EVT-02"), "Fest 1", Money.of(50.0), SeatCapacity.of(10));

        // Act & Assert
        assertEquals(e1, e1);
        assertEquals(e1, e2);
        assertNotEquals(e1, e3);
        assertNotEquals(e1, null);
        assertNotEquals(e1, "String Object");
        assertEquals(e1.hashCode(), e2.hashCode());
    }
}
