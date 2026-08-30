package com.desafiolatam.ticketing.domain.exception;

/**
 * Thrown when attempting an operation on an inactive, cancelled or closed event.
 */
public class EventNotActiveException extends DomainException {

    public EventNotActiveException(String message) {
        super(message);
    }
}
