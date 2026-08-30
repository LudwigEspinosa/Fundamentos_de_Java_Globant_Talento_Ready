package com.desafiolatam.ticketing.domain.exception;

/**
 * Thrown when booking arguments, input formats or domain state transitions are invalid.
 */
public class InvalidBookingException extends DomainException {

    public InvalidBookingException(String message) {
        super(message);
    }
}
