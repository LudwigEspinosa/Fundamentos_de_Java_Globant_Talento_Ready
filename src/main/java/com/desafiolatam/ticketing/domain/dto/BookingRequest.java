package com.desafiolatam.ticketing.domain.dto;

import java.util.Objects;

/**
 * Data Transfer Object representing an incoming ticket booking command.
 */
public class BookingRequest {

    private final String eventId;
    private final int quantity;

    public BookingRequest(String eventId, int quantity) {
        this.eventId = eventId;
        this.quantity = quantity;
    }

    public String getEventId() {
        return eventId;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingRequest that = (BookingRequest) o;
        return quantity == that.quantity && Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, quantity);
    }

    @Override
    public String toString() {
        return "BookingRequest{" +
                "eventId='" + eventId + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
