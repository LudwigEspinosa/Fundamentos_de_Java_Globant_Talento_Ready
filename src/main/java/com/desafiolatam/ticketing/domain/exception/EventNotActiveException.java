package com.desafiolatam.ticketing.domain.exception;

/**
 * Thrown when trying to execute operations on an inactive or cancelled event.
 */
public class EventNotActiveException extends DomainException {

    public EventNotActiveException(String message) {
        super(message);
    }
}
