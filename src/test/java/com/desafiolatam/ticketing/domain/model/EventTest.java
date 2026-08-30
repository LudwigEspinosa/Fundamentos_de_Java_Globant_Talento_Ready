package com.desafiolatam.ticketing.domain.model;

import com.desafiolatam.ticketing.domain.exception.EventNotActiveException;
import com.desafiolatam.ticketing.domain.exception.InsufficientSeatsException;
import com.desafiolatam.ticketing.domain.exception.InvalidBookingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Event Entity & Inventory Domain Logic Tests")
class EventTest {

    @Test
    @DisplayName("Should create event successfully with initial ACTIVE state")
    void shouldCreateEventSuccessfully() {
        // Arrange & Act
        Event event = new Event("EVT-01", "Neon Festival", 50.0, 100);

        // Assert
        assertEquals("EVT-01", event.getId());
        assertEquals("Neon Festival", event.getName());
        assertEquals(50.0, event.getBasePrice());
        assertEquals(100, event.getTotalCapacity());
        assertEquals(100, event.getAvailableSeats());
        assertEquals(EventStatus.ACTIVE, event.getStatus());
        assertTrue(event.isAvailable());
        assertTrue(event.toString().contains("Neon Festival"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Should throw InvalidBookingException when event ID is null or blank")
    void shouldThrowWhenEventIdIsInvalid(String invalidId) {
        // Arrange, Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> new Event(invalidId, "Rock Concert", 45.0, 50)
        );
        assertEquals("Event ID cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Should throw InvalidBookingException when event name is null or blank")
    void shouldThrowWhenEventNameIsInvalid(String invalidName) {
        // Arrange, Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> new Event("EVT-02", invalidName, 45.0, 50)
        );
        assertEquals("Event name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidBookingException when base price is negative")
    void shouldThrowWhenBasePriceIsNegative() {
        // Arrange, Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> new Event("EVT-03", "Jazz Night", -10.0, 50)
        );
        assertEquals("Base price cannot be negative", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -50})
    @DisplayName("Should throw InvalidBookingException when total capacity is non-positive")
    void shouldThrowWhenTotalCapacityIsNonPositive(int invalidCapacity) {
        // Arrange, Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> new Event("EVT-04", "Opera Gala", 80.0, invalidCapacity)
        );
        assertEquals("Total capacity must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should reserve seats and transition to SOLD_OUT when capacity reaches zero")
    void shouldReserveSeatsAndTransitionToSoldOut() {
        // Arrange
        Event event = new Event("EVT-05", "Cyber Metal", 30.0, 10);

        // Act - partial reservation
        event.reserveSeats(6);

        // Assert partial
        assertEquals(4, event.getAvailableSeats());
        assertEquals(EventStatus.ACTIVE, event.getStatus());
        assertTrue(event.isAvailable());

        // Act - complete remaining seats
        event.reserveSeats(4);

        // Assert sold out
        assertEquals(0, event.getAvailableSeats());
        assertEquals(EventStatus.SOLD_OUT, event.getStatus());
        assertFalse(event.isAvailable());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    @DisplayName("Should throw InvalidBookingException when reservation quantity is non-positive")
    void shouldThrowWhenReservationQuantityIsNonPositive(int invalidQty) {
        // Arrange
        Event event = new Event("EVT-06", "Tech Summit", 100.0, 50);

        // Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> event.reserveSeats(invalidQty)
        );
        assertEquals("Reservation quantity must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw EventNotActiveException when reserving on non-active event")
    void shouldThrowWhenReservingOnNonActiveEvent() {
        // Arrange
        Event event = new Event("EVT-07", "Cancelled Gig", 20.0, 50);
        event.setStatus(EventStatus.CANCELLED);

        // Act & Assert
        assertFalse(event.isAvailable());
        EventNotActiveException exception = assertThrows(
                EventNotActiveException.class,
                () -> event.reserveSeats(2)
        );
        assertTrue(exception.getMessage().contains("is not active"));
    }

    @Test
    @DisplayName("Should throw InsufficientSeatsException when requested seats exceed available")
    void shouldThrowWhenSeatsExceedAvailable() {
        // Arrange
        Event event = new Event("EVT-08", "Exclusive VIP Party", 200.0, 5);

        // Act & Assert
        InsufficientSeatsException exception = assertThrows(
                InsufficientSeatsException.class,
                () -> event.reserveSeats(6)
        );
        assertTrue(exception.getMessage().contains("Insufficient seats available"));
    }

    @Test
    @DisplayName("Should release seats and transition back from SOLD_OUT to ACTIVE")
    void shouldReleaseSeatsAndRestoreActiveStatus() {
        // Arrange
        Event event = new Event("EVT-09", "Indie Show", 25.0, 5);
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
    @DisplayName("Should cap available seats at total capacity when releasing extra seats")
    void shouldCapAvailableSeatsAtTotalCapacity() {
        // Arrange
        Event event = new Event("EVT-10", "Acoustic Session", 15.0, 10);
        event.reserveSeats(3);
        assertEquals(7, event.getAvailableSeats());

        // Act - releasing more than reserved
        event.releaseSeats(10);

        // Assert
        assertEquals(10, event.getAvailableSeats());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    @DisplayName("Should throw InvalidBookingException when release quantity is non-positive")
    void shouldThrowWhenReleaseQuantityIsNonPositive(int invalidQty) {
        // Arrange
        Event event = new Event("EVT-11", "Comedy Night", 30.0, 20);

        // Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> event.releaseSeats(invalidQty)
        );
        assertEquals("Release quantity must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidBookingException when setting null status")
    void shouldThrowWhenSettingNullStatus() {
        // Arrange
        Event event = new Event("EVT-12", "Classical Solo", 40.0, 30);

        // Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> event.setStatus(null)
        );
        assertEquals("Event status cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should correctly evaluate equals and hashCode based on ID")
    void shouldEvaluateEqualsAndHashCode() {
        // Arrange
        Event event1 = new Event("EVT-100", "Festival 1", 50.0, 100);
        Event event2 = new Event("EVT-100", "Festival 2", 60.0, 200);
        Event event3 = new Event("EVT-200", "Festival 1", 50.0, 100);

        // Act & Assert
        assertEquals(event1, event1);
        assertEquals(event1, event2);
        assertNotEquals(event1, event3);
        assertNotEquals(event1, null);
        assertNotEquals(event1, "Non-event string");
        assertEquals(event1.hashCode(), event2.hashCode());
    }
}
