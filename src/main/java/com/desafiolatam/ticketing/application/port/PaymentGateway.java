package com.desafiolatam.ticketing.application.port;

import com.desafiolatam.ticketing.domain.model.valueobject.CustomerId;
import com.desafiolatam.ticketing.domain.model.valueobject.Money;

/**
 * Outbound port for third-party financial transaction processing.
 */
public interface PaymentGateway {

    /**
     * Attempts to charge a customer for a given monetary amount.
     *
     * @param customerId identifier of the customer
     * @param amount amount to charge
     * @return true if payment succeeded, false if rejected
     */
    boolean charge(CustomerId customerId, Money amount);
}
