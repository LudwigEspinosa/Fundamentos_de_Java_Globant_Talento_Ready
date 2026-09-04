package com.desafiolatam.ticketing.domain.exception;

/**
 * Thrown when an event aggregate cannot be found in the domain repository.
 */
public class EventNotFoundException extends DomainException {

    public EventNotFoundException(String message) {
        super(message);
    }
}
