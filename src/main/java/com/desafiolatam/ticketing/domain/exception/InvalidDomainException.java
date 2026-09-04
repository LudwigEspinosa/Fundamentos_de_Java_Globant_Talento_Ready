package com.desafiolatam.ticketing.domain.exception;

/**
 * Thrown when domain entities or value objects fail invariant self-validations.
 */
public class InvalidDomainException extends DomainException {

    public InvalidDomainException(String message) {
        super(message);
    }
}
