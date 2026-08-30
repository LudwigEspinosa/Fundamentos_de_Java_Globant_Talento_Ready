package com.desafiolatam.ticketing.domain.model;

/**
 * Represents customer membership levels and their associated discount rates.
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
     * Calculates the discount amount for a given gross price.
     *
     * @param grossAmount gross monetary amount
     * @return calculated discount amount
     */
    public double calculateDiscount(double grossAmount) {
        if (grossAmount <= 0) {
            return 0.0;
        }
        return grossAmount * discountRate;
    }
}
