package com.desafiolatam.ticketing.domain.model.valueobject;

import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import java.util.UUID;

/**
 * Strongly typed Value Object representing a Customer identifier.
 */
public record CustomerId(String value) {

    public CustomerId {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDomainException("CustomerId value cannot be null or empty");
        }
        value = value.trim();
    }

    public static CustomerId generate() {
        return new CustomerId("CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }

    public static CustomerId of(String value) {
        return new CustomerId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
