package com.desafiolatam.ticketing.domain.exception;

/**
 * Thrown when requested reservation quantity exceeds the available capacity invariant.
 */
public class InsufficientSeatsException extends DomainException {

    public InsufficientSeatsException(String message) {
        super(message);
    }
}
