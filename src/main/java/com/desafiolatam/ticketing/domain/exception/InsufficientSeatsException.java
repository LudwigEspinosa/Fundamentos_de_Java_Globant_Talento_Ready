package com.desafiolatam.ticketing.domain.exception;

/**
 * Thrown when the requested number of tickets exceeds available capacity.
 */
public class InsufficientSeatsException extends DomainException {

    public InsufficientSeatsException(String message) {
        super(message);
    }
}
