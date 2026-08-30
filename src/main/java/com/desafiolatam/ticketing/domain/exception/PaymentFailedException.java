package com.desafiolatam.ticketing.domain.exception;

/**
 * Thrown when a payment attempt is rejected by the payment gateway.
 */
public class PaymentFailedException extends DomainException {

    public PaymentFailedException(String message) {
        super(message);
    }
}
