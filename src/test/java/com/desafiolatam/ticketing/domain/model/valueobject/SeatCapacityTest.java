package com.desafiolatam.ticketing.domain.model.valueobject;

import com.desafiolatam.ticketing.domain.exception.InsufficientSeatsException;
import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SeatCapacity Value Object (record) Tests")
class SeatCapacityTest {

    @Test
    @DisplayName("Should create SeatCapacity and perform reservation and release cycles")
    void shouldPerformSeatReservationAndRelease() {
        // Arrange
        SeatCapacity capacity = SeatCapacity.of(10);
        assertEquals(10, capacity.total());
        assertEquals(10, capacity.available());
        assertFalse(capacity.isSoldOut());
        assertTrue(capacity.hasAvailable(5));

        // Act - reserve partial
        SeatCapacity afterReserve = capacity.reserve(6);

        // Assert partial
        assertEquals(4, afterReserve.available());
        assertFalse(afterReserve.isSoldOut());

        // Act - reserve to sold out
        SeatCapacity soldOut = afterReserve.reserve(4);

        // Assert sold out
        assertEquals(0, soldOut.available());
        assertTrue(soldOut.isSoldOut());
        assertFalse(soldOut.hasAvailable(1));

        // Act - release seats
        SeatCapacity released = soldOut.release(3);
        assertEquals(3, released.available());
        assertFalse(released.isSoldOut());

        // Act - release more than total capacity (capped)
        SeatCapacity capped = released.release(20);
        assertEquals(10, capped.available());
    }

    @Test
    @DisplayName("Should throw InsufficientSeatsException when reservation exceeds available")
    void shouldThrowWhenReservationExceedsAvailable() {
        // Arrange
        SeatCapacity capacity = new SeatCapacity(10, 3);

        // Act & Assert
        InsufficientSeatsException exception = assertThrows(
                InsufficientSeatsException.class,
                () -> capacity.reserve(4)
        );
        assertTrue(exception.getMessage().contains("Insufficient seats available"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    @DisplayName("Should throw InvalidDomainException for non-positive reserve/release quantities")
    void shouldThrowWhenQuantitiesAreNonPositive(int invalidQty) {
        // Arrange
        SeatCapacity capacity = SeatCapacity.of(10);

        // Act & Assert
        assertThrows(InvalidDomainException.class, () -> capacity.reserve(invalidQty));
        assertThrows(InvalidDomainException.class, () -> capacity.release(invalidQty));
        assertFalse(capacity.hasAvailable(invalidQty));
    }

    @Test
    @DisplayName("Should validate constructor invariants defensively")
    void shouldValidateConstructorInvariants() {
        // Arrange, Act & Assert
        assertThrows(InvalidDomainException.class, () -> new SeatCapacity(0, 0));
        assertThrows(InvalidDomainException.class, () -> new SeatCapacity(-5, 0));
        assertThrows(InvalidDomainException.class, () -> new SeatCapacity(10, -1));
        assertThrows(InvalidDomainException.class, () -> new SeatCapacity(10, 15)); // available > total
    }
}
