package com.desafiolatam.ticketing.domain.model.aggregate;

import com.desafiolatam.ticketing.domain.exception.EventNotActiveException;
import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import com.desafiolatam.ticketing.domain.model.enumtype.EventStatus;
import com.desafiolatam.ticketing.domain.model.valueobject.EventId;
import com.desafiolatam.ticketing.domain.model.valueobject.Money;
import com.desafiolatam.ticketing.domain.model.valueobject.SeatCapacity;
import java.util.Objects;

/**
 * Aggregate Root representing an Event with seat capacity invariants and status management.
 */
public class Event {

    private final EventId id;
    private final String name;
    private final Money basePrice;
    private SeatCapacity seatCapacity;
    private EventStatus status;

    public Event(EventId id, String name, Money basePrice, SeatCapacity seatCapacity) {
        this.id = Objects.requireNonNull(id, "EventId cannot be null");
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDomainException("Event name cannot be null or empty");
        }
        this.name = name.trim();
        this.basePrice = Objects.requireNonNull(basePrice, "BasePrice cannot be null");
        this.seatCapacity = Objects.requireNonNull(seatCapacity, "SeatCapacity cannot be null");
        this.status = seatCapacity.isSoldOut() ? EventStatus.SOLD_OUT : EventStatus.ACTIVE;
    }

    public EventId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Money getBasePrice() {
        return basePrice;
    }

    public SeatCapacity getSeatCapacity() {
        return seatCapacity;
    }

    public int getAvailableSeats() {
        return seatCapacity.available();
    }

    public int getTotalCapacity() {
        return seatCapacity.total();
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = Objects.requireNonNull(status, "EventStatus cannot be null");
    }

    /**
     * Invariant check: is event open for new reservations?
     */
    public boolean isAvailable() {
        return this.status == EventStatus.ACTIVE && !this.seatCapacity.isSoldOut();
    }

    /**
     * Reserves a specific number of seats for this event.
     *
     * @param quantity number of seats to reserve
     */
    public void reserveSeats(int quantity) {
        if (this.status != EventStatus.ACTIVE) {
            throw new EventNotActiveException("Cannot reserve seats: Event " + this.name + " is not active");
        }

        this.seatCapacity = this.seatCapacity.reserve(quantity);
        if (this.seatCapacity.isSoldOut()) {
            this.status = EventStatus.SOLD_OUT;
        }
    }

    /**
     * Releases reserved seats back to inventory.
     *
     * @param quantity number of seats to release
     */
    public void releaseSeats(int quantity) {
        this.seatCapacity = this.seatCapacity.release(quantity);
        if (!this.seatCapacity.isSoldOut() && this.status == EventStatus.SOLD_OUT) {
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
                "id=" + id +
                ", name='" + name + '\'' +
                ", basePrice=" + basePrice +
                ", seatCapacity=" + seatCapacity +
                ", status=" + status +
                '}';
    }
}
