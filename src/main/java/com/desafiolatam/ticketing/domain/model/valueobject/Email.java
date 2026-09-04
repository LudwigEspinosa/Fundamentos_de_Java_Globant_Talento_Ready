package com.desafiolatam.ticketing.domain.model.valueobject;

import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import java.util.regex.Pattern;

/**
 * Immutable Value Object representing a validated email address.
 */
public record Email(String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDomainException("Email cannot be null or empty");
        }
        String normalized = value.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new InvalidDomainException("Email format is invalid: " + value);
        }
        value = normalized;
    }

    public static Email of(String email) {
        return new Email(email);
    }

    @Override
    public String toString() {
        return value;
    }
}
