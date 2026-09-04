package com.desafiolatam.ticketing.domain.model.valueobject;

import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Identifier Value Objects (EventId, CustomerId, BookingId) Tests")
class IdValueObjectsTest {

    @Test
    @DisplayName("Should create and generate valid identifiers")
    void shouldCreateAndGenerateIdentifiers() {
        // Arrange & Act
        EventId eventId = EventId.of("EVT-01");
        CustomerId customerId = CustomerId.of("CUST-01");
        BookingId bookingId = new BookingId("BKG-01");

        // Assert
        assertEquals("EVT-01", eventId.value());
        assertEquals("EVT-01", eventId.toString());
        assertEquals("CUST-01", customerId.value());
        assertEquals("CUST-01", customerId.toString());
        assertEquals("BKG-01", bookingId.value());
        assertEquals("BKG-01", bookingId.toString());

        assertNotNull(EventId.generate());
        assertNotNull(CustomerId.generate());
        assertNotNull(BookingId.generate());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Should throw InvalidDomainException for null or blank IDs")
    void shouldThrowForInvalidIdValues(String invalidValue) {
        // Arrange, Act & Assert
        assertThrows(InvalidDomainException.class, () -> new EventId(invalidValue));
        assertThrows(InvalidDomainException.class, () -> new CustomerId(invalidValue));
        assertThrows(InvalidDomainException.class, () -> new BookingId(invalidValue));
    }
}
