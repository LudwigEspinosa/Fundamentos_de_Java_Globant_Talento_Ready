package com.desafiolatam.ticketing.domain.model;

import com.desafiolatam.ticketing.domain.exception.InvalidBookingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate root entity representing a complete booking order.
 */
public class Booking {

    private final String id;
    private final Customer customer;
    private final List<BookingItem> items;
    private final double grossTotal;
    private final double discountAmount;
    private final double netTotal;
    private BookingStatus status;
    private final LocalDateTime createdAt;

    public Booking(String id, Customer customer, List<BookingItem> items, double discountAmount) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidBookingException("Booking ID cannot be null or empty");
        }
        if (customer == null) {
            throw new InvalidBookingException("Booking customer cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new InvalidBookingException("Booking items cannot be null or empty");
        }
        if (discountAmount < 0) {
            throw new InvalidBookingException("Discount amount cannot be negative");
        }

        this.id = id.trim();
        this.customer = customer;
        this.items = new ArrayList<>(items);
        this.discountAmount = discountAmount;

        double sum = 0.0;
        for (BookingItem item : items) {
            if (item == null) {
                throw new InvalidBookingException("Booking item cannot be null");
            }
            sum += item.getSubtotal();
        }
        this.grossTotal = sum;

        double calculatedNet = this.grossTotal - discountAmount;
        this.netTotal = Math.max(0.0, calculatedNet);

        this.status = BookingStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<BookingItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double getGrossTotal() {
        return grossTotal;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getNetTotal() {
        return netTotal;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void confirm() {
        this.status = BookingStatus.CONFIRMED;
    }

    public void markAsFailed() {
        this.status = BookingStatus.FAILED;
    }

    public void cancel() {
        if (this.status == BookingStatus.CANCELLED) {
            throw new InvalidBookingException("Booking is already cancelled");
        }
        this.status = BookingStatus.CANCELLED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                ", customer=" + customer +
                ", items=" + items +
                ", grossTotal=" + grossTotal +
                ", discountAmount=" + discountAmount +
                ", netTotal=" + netTotal +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
