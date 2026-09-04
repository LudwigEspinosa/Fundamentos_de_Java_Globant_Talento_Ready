package com.desafiolatam.ticketing.domain.model.enumtype;

import com.desafiolatam.ticketing.domain.model.valueobject.Money;

/**
 * Customer membership levels and their associated discount rates.
 */
public enum MembershipTier {
    REGULAR(0.00),
    PREMIUM(0.10),
    VIP(0.20);

    private final double discountRate;

    MembershipTier(double discountRate) {
        this.discountRate = discountRate;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    /**
     * Calculates the discount amount as a Money value object.
     *
     * @param grossPrice gross monetary amount
     * @return calculated discount Money
     */
    public Money calculateDiscount(Money grossPrice) {
        if (grossPrice == null || grossPrice.isZero()) {
            return Money.ZERO;
        }
        return grossPrice.multiply(this.discountRate);
    }
}
