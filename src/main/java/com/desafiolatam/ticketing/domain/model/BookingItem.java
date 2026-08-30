package com.desafiolatam.ticketing.domain.model;

import com.desafiolatam.ticketing.domain.exception.InvalidBookingException;
import java.util.Objects;

/**
 * Value object representing a line item in a ticket booking.
 */
public class BookingItem {

    private final String eventId;
    private final String eventName;
    private final double unitPrice;
    private final int quantity;
    private final double subtotal;

    public BookingItem(String eventId, String eventName, double unitPrice, int quantity) {
        if (eventId == null || eventId.trim().isEmpty()) {
            throw new InvalidBookingException("Item event ID cannot be null or empty");
        }
        if (eventName == null || eventName.trim().isEmpty()) {
            throw new InvalidBookingException("Item event name cannot be null or empty");
        }
        if (unitPrice < 0) {
            throw new InvalidBookingException("Unit price cannot be negative");
        }
        if (quantity <= 0) {
            throw new InvalidBookingException("Quantity must be greater than zero");
        }

        this.eventId = eventId.trim();
        this.eventName = eventName.trim();
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = unitPrice * quantity;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingItem that = (BookingItem) o;
        return Double.compare(that.unitPrice, unitPrice) == 0 &&
                quantity == that.quantity &&
                Double.compare(that.subtotal, subtotal) == 0 &&
                Objects.equals(eventId, that.eventId) &&
                Objects.equals(eventName, that.eventName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventName, unitPrice, quantity, subtotal);
    }

    @Override
    public String toString() {
        return "BookingItem{" +
                "eventId='" + eventId + '\'' +
                ", eventName='" + eventName + '\'' +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                ", subtotal=" + subtotal +
                '}';
    }
}
