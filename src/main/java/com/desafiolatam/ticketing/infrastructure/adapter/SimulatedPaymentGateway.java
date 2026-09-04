package com.desafiolatam.ticketing.infrastructure.adapter;

import com.desafiolatam.ticketing.application.port.PaymentGateway;
import com.desafiolatam.ticketing.domain.model.valueobject.CustomerId;
import com.desafiolatam.ticketing.domain.model.valueobject.Money;

/**
 * Infrastructure adapter simulating payment gateway interaction.
 */
public class SimulatedPaymentGateway implements PaymentGateway {

    private boolean shouldSucceed = true;

    public void setShouldSucceed(boolean shouldSucceed) {
        this.shouldSucceed = shouldSucceed;
    }

    @Override
    public boolean charge(CustomerId customerId, Money amount) {
        if (customerId == null || amount == null) {
            return false;
        }
        return this.shouldSucceed;
    }
}
