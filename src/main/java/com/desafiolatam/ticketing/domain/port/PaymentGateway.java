package com.desafiolatam.ticketing.domain.port;

/**
 * Output port for third-party payment processing.
 */
public interface PaymentGateway {

    /**
     * Attempts to charge the customer for a given monetary amount.
     *
     * @param customerId identifier of the customer
     * @param amount amount to charge
     * @return true if payment succeeded, false if rejected/failed
     */
    boolean charge(String customerId, double amount);
}
