package com.desafiolatam.ticketing.domain.model;

import com.desafiolatam.ticketing.domain.exception.InvalidBookingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookingItem Value Object Tests")
class BookingItemTest {

    @Test
    @DisplayName("Should create booking item and calculate correct subtotal")
    void shouldCreateBookingItemAndCalculateSubtotal() {
        // Arrange
        String eventId = "EVT-01";
        String eventName = "Summer Festival";
        double unitPrice = 75.0;
        int quantity = 4;

        // Act
        BookingItem item = new BookingItem(eventId, eventName, unitPrice, quantity);

        // Assert
        assertEquals("EVT-01", item.getEventId());
        assertEquals("Summer Festival", item.getEventName());
        assertEquals(75.0, item.getUnitPrice());
        assertEquals(4, item.getQuantity());
        assertEquals(300.0, item.getSubtotal());
        assertTrue(item.toString().contains("Summer Festival"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Should throw InvalidBookingException when event ID is null or blank")
    void shouldThrowWhenEventIdIsInvalid(String invalidEventId) {
        // Arrange, Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> new BookingItem(invalidEventId, "Festival", 50.0, 2)
        );
        assertEquals("Item event ID cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Should throw InvalidBookingException when event name is null or blank")
    void shouldThrowWhenEventNameIsInvalid(String invalidName) {
        // Arrange, Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> new BookingItem("EVT-01", invalidName, 50.0, 2)
        );
        assertEquals("Item event name cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidBookingException when unit price is negative")
    void shouldThrowWhenUnitPriceIsNegative() {
        // Arrange, Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> new BookingItem("EVT-01", "Concert", -15.0, 2)
        );
        assertEquals("Unit price cannot be negative", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    @DisplayName("Should throw InvalidBookingException when quantity is non-positive")
    void shouldThrowWhenQuantityIsNonPositive(int invalidQty) {
        // Arrange, Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> new BookingItem("EVT-01", "Concert", 50.0, invalidQty)
        );
        assertEquals("Quantity must be greater than zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should evaluate equals and hashCode based on value properties")
    void shouldEvaluateEqualsAndHashCode() {
        // Arrange
        BookingItem item1 = new BookingItem("EVT-01", "Concert A", 40.0, 2);
        BookingItem item2 = new BookingItem("EVT-01", "Concert A", 40.0, 2);
        BookingItem item3 = new BookingItem("EVT-02", "Concert B", 50.0, 1);

        // Act & Assert
        assertEquals(item1, item1);
        assertEquals(item1, item2);
        assertNotEquals(item1, item3);
        assertNotEquals(item1, null);
        assertNotEquals(item1, "String Object");
        assertEquals(item1.hashCode(), item2.hashCode());
    }
}
