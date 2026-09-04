package com.desafiolatam.ticketing.domain.model.valueobject;

import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import java.util.UUID;

/**
 * Strongly typed Value Object representing a unique Booking identifier.
 */
public record BookingId(String value) {

    public BookingId {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDomainException("BookingId value cannot be null or empty");
        }
        value = value.trim();
    }

    /**
     * Factory method to generate a new random domain BookingId.
     */
    public static BookingId generate() {
        return new BookingId("BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }

    @Override
    public String toString() {
        return value;
    }
}
