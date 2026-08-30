package com.desafiolatam.ticketing.domain.exception;

/**
 * Thrown when an event cannot be located by its identifier.
 */
public class EventNotFoundException extends DomainException {

    public EventNotFoundException(String message) {
        super(message);
    }
}
