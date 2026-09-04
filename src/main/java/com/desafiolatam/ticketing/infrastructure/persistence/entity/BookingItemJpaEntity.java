package com.desafiolatam.ticketing.infrastructure.persistence.entity;

import com.desafiolatam.ticketing.domain.model.valueobject.BookingItem;
import com.desafiolatam.ticketing.domain.model.valueobject.EventId;
import com.desafiolatam.ticketing.domain.model.valueobject.Money;
import jakarta.persistence.*;

/**
 * JPA Entity for Booking line items.
 */
@Entity
@Table(name = "booking_items")
public class BookingItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private BookingJpaEntity booking;

    @Column(name = "event_id", length = 64, nullable = false)
    private String eventId;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "subtotal", nullable = false)
    private double subtotal;

    public BookingItemJpaEntity() {}

    public BookingItemJpaEntity(String eventId, String eventName, double unitPrice, int quantity, double subtotal) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public static BookingItemJpaEntity fromDomain(BookingItem item) {
        return new BookingItemJpaEntity(
                item.eventId().value(),
                item.eventName(),
                item.unitPrice().amount(),
                item.quantity(),
                item.subtotal().amount()
        );
    }

    public BookingItem toDomain() {
        return new BookingItem(
                new EventId(this.eventId),
                this.eventName,
                Money.of(this.unitPrice),
                this.quantity,
                Money.of(this.subtotal)
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BookingJpaEntity getBooking() { return booking; }
    public void setBooking(BookingJpaEntity booking) { this.booking = booking; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
