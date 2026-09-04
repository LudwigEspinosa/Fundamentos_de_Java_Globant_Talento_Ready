package com.desafiolatam.ticketing.domain.model.valueobject;

import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BookingItem Value Object (record) Tests")
class BookingItemTest {

    @Test
    @DisplayName("Should create BookingItem and calculate subtotal automatically")
    void shouldCreateBookingItemAndCalculateSubtotal() {
        // Arrange
        EventId eventId = EventId.of("EVT-01");
        String eventName = "Neon Cyberpunk Festival";
        Money unitPrice = Money.of(45.0);
        int quantity = 3;

        // Act
        BookingItem item = BookingItem.of(eventId, eventName, unitPrice, quantity);

        // Assert
        assertEquals(eventId, item.eventId());
        assertEquals("Neon Cyberpunk Festival", item.eventName());
        assertEquals(Money.of(45.0), item.unitPrice());
        assertEquals(3, item.quantity());
        assertEquals(Money.of(135.0), item.subtotal());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Should throw InvalidDomainException when event name is null or blank")
    void shouldThrowWhenEventNameIsInvalid(String invalidName) {
        // Arrange, Act & Assert
        assertThrows(
                InvalidDomainException.class,
                () -> BookingItem.of(EventId.of("EVT-01"), invalidName, Money.of(50.0), 2)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    @DisplayName("Should throw InvalidDomainException when quantity is non-positive")
    void shouldThrowWhenQuantityIsNonPositive(int invalidQty) {
        // Arrange, Act & Assert
        assertThrows(
                InvalidDomainException.class,
                () -> BookingItem.of(EventId.of("EVT-01"), "Festival", Money.of(50.0), invalidQty)
        );
    }

    @Test
    @DisplayName("Should throw NullPointerException when EventId or UnitPrice is null")
    void shouldThrowWhenNullDependenciesProvided() {
        // Arrange, Act & Assert
        assertThrows(NullPointerException.class, () -> BookingItem.of(null, "Festival", Money.of(50.0), 2));
        assertThrows(NullPointerException.class, () -> BookingItem.of(EventId.of("EVT-01"), "Festival", null, 2));
    }
}
