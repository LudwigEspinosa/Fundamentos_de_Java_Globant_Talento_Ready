package com.desafiolatam.ticketing.domain.model.valueobject;

import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import java.util.UUID;

/**
 * Strongly typed Value Object representing an Event identifier.
 */
public record EventId(String value) {

    public EventId {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDomainException("EventId value cannot be null or empty");
        }
        value = value.trim();
    }

    public static EventId generate() {
        return new EventId("EVT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }

    public static EventId of(String value) {
        return new EventId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
