package com.desafiolatam.ticketing.infrastructure.persistence.entity;

import com.desafiolatam.ticketing.domain.model.aggregate.Event;
import com.desafiolatam.ticketing.domain.model.enumtype.EventStatus;
import com.desafiolatam.ticketing.domain.model.valueobject.EventId;
import com.desafiolatam.ticketing.domain.model.valueobject.Money;
import com.desafiolatam.ticketing.domain.model.valueobject.SeatCapacity;
import jakarta.persistence.*;

/**
 * JPA Entity for Event persistence in PostgreSQL.
 */
@Entity
@Table(name = "events")
public class EventJpaEntity {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "base_price", nullable = false)
    private double basePrice;

    @Column(name = "total_capacity", nullable = false)
    private int totalCapacity;

    @Column(name = "available_seats", nullable = false)
    private int availableSeats;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private EventStatus status;

    public EventJpaEntity() {}

    public EventJpaEntity(String id, String name, double basePrice, int totalCapacity, int availableSeats, EventStatus status) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.totalCapacity = totalCapacity;
        this.availableSeats = availableSeats;
        this.status = status;
    }

    public static EventJpaEntity fromDomain(Event event) {
        return new EventJpaEntity(
                event.getId().value(),
                event.getName(),
                event.getBasePrice().amount(),
                event.getTotalCapacity(),
                event.getAvailableSeats(),
                event.getStatus()
        );
    }

    public Event toDomain() {
        SeatCapacity capacity = new SeatCapacity(this.totalCapacity, this.availableSeats);
        Event event = new Event(new EventId(this.id), this.name, Money.of(this.basePrice), capacity);
        event.setStatus(this.status);
        return event;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public int getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(int totalCapacity) { this.totalCapacity = totalCapacity; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }
}
