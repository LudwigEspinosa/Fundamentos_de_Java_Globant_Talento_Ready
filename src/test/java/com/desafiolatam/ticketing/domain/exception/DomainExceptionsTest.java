package com.desafiolatam.ticketing.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Domain Exceptions Hierarchy Tests")
class DomainExceptionsTest {

    private static class TestDomainException extends DomainException {
        public TestDomainException(String message) {
            super(message);
        }

        public TestDomainException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Test
    @DisplayName("Should create base DomainException with message and cause")
    void shouldCreateBaseDomainException() {
        // Arrange
        String msg = "Domain failure";
        RuntimeException cause = new RuntimeException("Root cause");

        // Act
        TestDomainException ex1 = new TestDomainException(msg);
        TestDomainException ex2 = new TestDomainException(msg, cause);

        // Assert
        assertEquals("Domain failure", ex1.getMessage());
        assertEquals("Domain failure", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    @DisplayName("Should create EventNotFoundException with message")
    void shouldCreateEventNotFoundException() {
        // Arrange & Act
        EventNotFoundException exception = new EventNotFoundException("Event EVT-99 not found");

        // Assert
        assertNotNull(exception);
        assertEquals("Event EVT-99 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should create EventNotActiveException with message")
    void shouldCreateEventNotActiveException() {
        // Arrange & Act
        EventNotActiveException exception = new EventNotActiveException("Event is closed");

        // Assert
        assertNotNull(exception);
        assertEquals("Event is closed", exception.getMessage());
    }

    @Test
    @DisplayName("Should create InsufficientSeatsException with message")
    void shouldCreateInsufficientSeatsException() {
        // Arrange & Act
        InsufficientSeatsException exception = new InsufficientSeatsException("No seats remaining");

        // Assert
        assertNotNull(exception);
        assertEquals("No seats remaining", exception.getMessage());
    }

    @Test
    @DisplayName("Should create InvalidBookingException with message")
    void shouldCreateInvalidBookingException() {
        // Arrange & Act
        InvalidBookingException exception = new InvalidBookingException("Invalid booking payload");

        // Assert
        assertNotNull(exception);
        assertEquals("Invalid booking payload", exception.getMessage());
    }

    @Test
    @DisplayName("Should create PaymentFailedException with message")
    void shouldCreatePaymentFailedException() {
        // Arrange & Act
        PaymentFailedException exception = new PaymentFailedException("Payment gateway unreachable");

        // Assert
        assertNotNull(exception);
        assertEquals("Payment gateway unreachable", exception.getMessage());
    }
}
