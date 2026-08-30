package com.desafiolatam.ticketing.domain.model;

import com.desafiolatam.ticketing.domain.exception.EventNotActiveException;
import com.desafiolatam.ticketing.domain.exception.InsufficientSeatsException;
import com.desafiolatam.ticketing.domain.exception.InvalidBookingException;
import java.util.Objects;

/**
 * Pure domain entity representing an event with seat inventory management.
 */
public class Event {

    private final String id;
    private final String name;
    private final double basePrice;
    private final int totalCapacity;
    private int availableSeats;
    private EventStatus status;

    public Event(String id, String name, double basePrice, int totalCapacity) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidBookingException("Event ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidBookingException("Event name cannot be null or empty");
        }
        if (basePrice < 0) {
            throw new InvalidBookingException("Base price cannot be negative");
        }
        if (totalCapacity <= 0) {
            throw new InvalidBookingException("Total capacity must be greater than zero");
        }

        this.id = id.trim();
        this.name = name.trim();
        this.basePrice = basePrice;
        this.totalCapacity = totalCapacity;
        this.availableSeats = totalCapacity;
        this.status = EventStatus.ACTIVE;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        if (status == null) {
            throw new InvalidBookingException("Event status cannot be null");
        }
        this.status = status;
    }

    /**
     * Checks if the event is currently active and has available capacity.
     */
    public boolean isAvailable() {
        return this.status == EventStatus.ACTIVE && this.availableSeats > 0;
    }

    /**
     * Reserves a specific number of seats for this event.
     *
     * @param quantity number of seats to reserve
     */
    public void reserveSeats(int quantity) {
        if (quantity <= 0) {
            throw new InvalidBookingException("Reservation quantity must be greater than zero");
        }
        if (this.status != EventStatus.ACTIVE) {
            throw new EventNotActiveException("Cannot reserve seats: Event " + this.name + " is not active");
        }
        if (quantity > this.availableSeats) {
            throw new InsufficientSeatsException(
                    "Insufficient seats available for event " + this.name +
                    ". Requested: " + quantity + ", Available: " + this.availableSeats
            );
        }

        this.availableSeats -= quantity;
        if (this.availableSeats == 0) {
            this.status = EventStatus.SOLD_OUT;
        }
    }

    /**
     * Releases reserved seats back to the available inventory (e.g. on booking cancellation or failed payment rollback).
     *
     * @param quantity number of seats to release
     */
    public void releaseSeats(int quantity) {
        if (quantity <= 0) {
            throw new InvalidBookingException("Release quantity must be greater than zero");
        }

        this.availableSeats += quantity;
        if (this.availableSeats > this.totalCapacity) {
            this.availableSeats = this.totalCapacity;
        }

        if (this.availableSeats > 0 && this.status == EventStatus.SOLD_OUT) {
            this.status = EventStatus.ACTIVE;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", basePrice=" + basePrice +
                ", totalCapacity=" + totalCapacity +
                ", availableSeats=" + availableSeats +
                ", status=" + status +
                '}';
    }
}
