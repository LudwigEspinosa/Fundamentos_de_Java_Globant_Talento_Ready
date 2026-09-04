package com.desafiolatam.ticketing.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Domain Exceptions Tests")
class DomainExceptionsTest {

    private static class SampleDomainException extends DomainException {
        public SampleDomainException(String msg) {
            super(msg);
        }

        public SampleDomainException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }

    @Test
    @DisplayName("Should create base DomainException with message and cause")
    void shouldCreateBaseDomainException() {
        // Arrange
        String msg = "Domain error";
        RuntimeException cause = new RuntimeException("Underlying cause");

        // Act
        SampleDomainException ex1 = new SampleDomainException(msg);
        SampleDomainException ex2 = new SampleDomainException(msg, cause);

        // Assert
        assertEquals("Domain error", ex1.getMessage());
        assertEquals("Domain error", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    @DisplayName("Should instantiate specific domain exceptions")
    void shouldInstantiateSpecificDomainExceptions() {
        // Arrange & Act
        EventNotFoundException enf = new EventNotFoundException("Event not found");
        EventNotActiveException ena = new EventNotActiveException("Event inactive");
        InsufficientSeatsException ins = new InsufficientSeatsException("Insufficient seats");
        InvalidDomainException ind = new InvalidDomainException("Invalid domain");
        PaymentFailedException pf = new PaymentFailedException("Payment failed");

        // Assert
        assertNotNull(enf);
        assertNotNull(ena);
        assertNotNull(ins);
        assertNotNull(ind);
        assertNotNull(pf);
    }
}
