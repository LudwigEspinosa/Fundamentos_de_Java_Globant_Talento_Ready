package com.desafiolatam.ticketing.domain.model.valueobject;

import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import java.util.Objects;

/**
 * Immutable Value Object representing monetary values with domain arithmetic operations.
 */
public record Money(double amount) implements Comparable<Money> {

    public static final Money ZERO = new Money(0.0);

    public Money {
        if (amount < 0.0) {
            throw new InvalidDomainException("Monetary amount cannot be negative: " + amount);
        }
        // Round to 2 decimal places for financial safety
        amount = Math.round(amount * 100.0) / 100.0;
    }

    public static Money of(double amount) {
        return new Money(amount);
    }

    public Money add(Money other) {
        Objects.requireNonNull(other, "Cannot add null Money");
        return new Money(this.amount + other.amount);
    }

    public Money subtract(Money other) {
        Objects.requireNonNull(other, "Cannot subtract null Money");
        double result = this.amount - other.amount;
        return new Money(Math.max(0.0, result));
    }

    public Money multiply(double factor) {
        if (factor < 0.0) {
            throw new InvalidDomainException("Multiplication factor cannot be negative: " + factor);
        }
        return new Money(this.amount * factor);
    }

    public boolean isZero() {
        return Double.compare(this.amount, 0.0) == 0;
    }

    public boolean isGreaterThan(Money other) {
        Objects.requireNonNull(other, "Cannot compare null Money");
        return this.amount > other.amount;
    }

    @Override
    public int compareTo(Money other) {
        return Double.compare(this.amount, other.amount);
    }

    @Override
    public String toString() {
        return String.format("$%.2f", amount);
    }
}
