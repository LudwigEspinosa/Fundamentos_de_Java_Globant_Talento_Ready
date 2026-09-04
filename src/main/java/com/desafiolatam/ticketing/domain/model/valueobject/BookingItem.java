package com.desafiolatam.ticketing.domain.model.valueobject;

import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import java.util.Objects;

/**
 * Immutable Value Object representing a line item in a ticket booking order.
 */
public record BookingItem(
        EventId eventId,
        String eventName,
        Money unitPrice,
        int quantity,
        Money subtotal
) {

    public BookingItem {
        Objects.requireNonNull(eventId, "EventId cannot be null");
        if (eventName == null || eventName.trim().isEmpty()) {
            throw new InvalidDomainException("Event name cannot be null or empty");
        }
        Objects.requireNonNull(unitPrice, "UnitPrice cannot be null");
        if (quantity <= 0) {
            throw new InvalidDomainException("Item quantity must be greater than zero");
        }

        Money calculatedSubtotal = unitPrice.multiply(quantity);
        if (subtotal == null || !subtotal.equals(calculatedSubtotal)) {
            subtotal = calculatedSubtotal;
        }
        eventName = eventName.trim();
    }

    public static BookingItem of(EventId eventId, String eventName, Money unitPrice, int quantity) {
        Objects.requireNonNull(unitPrice, "UnitPrice cannot be null");
        Money subtotal = unitPrice.multiply(quantity);
        return new BookingItem(eventId, eventName, unitPrice, quantity, subtotal);
    }
}
