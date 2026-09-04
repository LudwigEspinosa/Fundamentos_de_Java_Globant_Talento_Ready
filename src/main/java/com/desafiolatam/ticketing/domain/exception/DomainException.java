package com.desafiolatam.ticketing.domain.exception;

/**
 * Base abstract runtime exception for all business domain errors in Clean Architecture.
 */
public abstract class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
