package com.desafiolatam.ticketing.domain.model.aggregate;

import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import com.desafiolatam.ticketing.domain.model.entity.Customer;
import com.desafiolatam.ticketing.domain.model.enumtype.BookingStatus;
import com.desafiolatam.ticketing.domain.model.valueobject.BookingId;
import com.desafiolatam.ticketing.domain.model.valueobject.BookingItem;
import com.desafiolatam.ticketing.domain.model.valueobject.Money;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate Root representing an Order/Booking aggregate that encapsulates line items and total pricing.
 */
public class Booking {

    private final BookingId id;
    private final Customer customer;
    private final List<BookingItem> items;
    private final Money grossTotal;
    private final Money discountAmount;
    private final Money netTotal;
    private BookingStatus status;
    private final LocalDateTime createdAt;

    public Booking(BookingId id, Customer customer, List<BookingItem> items, Money discountAmount) {
        this.id = Objects.requireNonNull(id, "BookingId cannot be null");
        this.customer = Objects.requireNonNull(customer, "Customer cannot be null");
        if (items == null || items.isEmpty()) {
            throw new InvalidDomainException("Booking items list cannot be null or empty");
        }
        this.discountAmount = Objects.requireNonNull(discountAmount, "DiscountAmount cannot be null");

        this.items = new ArrayList<>();
        Money calculatedGross = Money.ZERO;
        for (BookingItem item : items) {
            Objects.requireNonNull(item, "Booking item cannot be null");
            this.items.add(item);
            calculatedGross = calculatedGross.add(item.subtotal());
        }

        this.grossTotal = calculatedGross;
        this.netTotal = calculatedGross.subtract(discountAmount);
        this.status = BookingStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public BookingId getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<BookingItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Money getGrossTotal() {
        return grossTotal;
    }

    public Money getDiscountAmount() {
        return discountAmount;
    }

    public Money getNetTotal() {
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
            throw new InvalidDomainException("Booking is already cancelled");
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
                "id=" + id +
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
