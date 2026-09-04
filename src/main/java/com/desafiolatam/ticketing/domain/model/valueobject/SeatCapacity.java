package com.desafiolatam.ticketing.domain.model.valueobject;

import com.desafiolatam.ticketing.domain.exception.InsufficientSeatsException;
import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;

/**
 * Immutable Value Object encapsulating seat inventory and capacity invariants.
 */
public record SeatCapacity(int total, int available) {

    public SeatCapacity {
        if (total <= 0) {
            throw new InvalidDomainException("Total capacity must be strictly greater than zero");
        }
        if (available < 0) {
            throw new InvalidDomainException("Available seats cannot be negative");
        }
        if (available > total) {
            throw new InvalidDomainException("Available seats (" + available + ") cannot exceed total capacity (" + total + ")");
        }
    }

    public static SeatCapacity of(int total) {
        return new SeatCapacity(total, total);
    }

    public SeatCapacity reserve(int quantity) {
        if (quantity <= 0) {
            throw new InvalidDomainException("Quantity to reserve must be greater than zero");
        }
        if (quantity > available) {
            throw new InsufficientSeatsException(
                    "Insufficient seats available: requested " + quantity + ", but only " + available + " are available"
            );
        }
        return new SeatCapacity(this.total, this.available - quantity);
    }

    public SeatCapacity release(int quantity) {
        if (quantity <= 0) {
            throw new InvalidDomainException("Quantity to release must be greater than zero");
        }
        int newAvailable = Math.min(this.total, this.available + quantity);
        return new SeatCapacity(this.total, newAvailable);
    }

    public boolean isSoldOut() {
        return this.available == 0;
    }

    public boolean hasAvailable(int quantity) {
        return quantity > 0 && this.available >= quantity;
    }
}
