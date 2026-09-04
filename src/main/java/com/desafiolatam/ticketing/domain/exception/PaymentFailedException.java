package com.desafiolatam.ticketing.domain.exception;

/**
 * Thrown when financial transaction processing is rejected by external gateways.
 */
public class PaymentFailedException extends DomainException {

    public PaymentFailedException(String message) {
        super(message);
    }
}
