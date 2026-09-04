package com.desafiolatam.ticketing.infrastructure.persistence.entity;

import com.desafiolatam.ticketing.domain.model.aggregate.Booking;
import com.desafiolatam.ticketing.domain.model.entity.Customer;
import com.desafiolatam.ticketing.domain.model.enumtype.BookingStatus;
import com.desafiolatam.ticketing.domain.model.valueobject.BookingId;
import com.desafiolatam.ticketing.domain.model.valueobject.BookingItem;
import com.desafiolatam.ticketing.domain.model.valueobject.Money;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JPA Entity for Booking aggregate root persistence in PostgreSQL.
 */
@Entity
@Table(name = "bookings")
public class BookingJpaEntity {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerJpaEntity customer;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<BookingItemJpaEntity> items = new ArrayList<>();

    @Column(name = "gross_total", nullable = false)
    private double grossTotal;

    @Column(name = "discount_amount", nullable = false)
    private double discountAmount;

    @Column(name = "net_total", nullable = false)
    private double netTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private BookingStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public BookingJpaEntity() {}

    public BookingJpaEntity(String id, CustomerJpaEntity customer, double grossTotal, double discountAmount, double netTotal, BookingStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.customer = customer;
        this.grossTotal = grossTotal;
        this.discountAmount = discountAmount;
        this.netTotal = netTotal;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static BookingJpaEntity fromDomain(Booking booking) {
        CustomerJpaEntity customerJpa = CustomerJpaEntity.fromDomain(booking.getCustomer());
        BookingJpaEntity jpaEntity = new BookingJpaEntity(
                booking.getId().value(),
                customerJpa,
                booking.getGrossTotal().amount(),
                booking.getDiscountAmount().amount(),
                booking.getNetTotal().amount(),
                booking.getStatus(),
                booking.getCreatedAt()
        );

        for (BookingItem item : booking.getItems()) {
            BookingItemJpaEntity itemEntity = BookingItemJpaEntity.fromDomain(item);
            itemEntity.setBooking(jpaEntity);
            jpaEntity.getItems().add(itemEntity);
        }

        return jpaEntity;
    }

    public Booking toDomain() {
        Customer domainCustomer = this.customer.toDomain();
        List<BookingItem> domainItems = this.items.stream()
                .map(BookingItemJpaEntity::toDomain)
                .collect(Collectors.toList());

        Booking booking = new Booking(
                new BookingId(this.id),
                domainCustomer,
                domainItems,
                Money.of(this.discountAmount)
        );

        if (this.status == BookingStatus.CONFIRMED) {
            booking.confirm();
        } else if (this.status == BookingStatus.CANCELLED) {
            booking.cancel();
        } else if (this.status == BookingStatus.FAILED) {
            booking.markAsFailed();
        }

        return booking;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public CustomerJpaEntity getCustomer() { return customer; }
    public void setCustomer(CustomerJpaEntity customer) { this.customer = customer; }

    public List<BookingItemJpaEntity> getItems() { return items; }
    public void setItems(List<BookingItemJpaEntity> items) { this.items = items; }

    public double getGrossTotal() { return grossTotal; }
    public void setGrossTotal(double grossTotal) { this.grossTotal = grossTotal; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public double getNetTotal() { return netTotal; }
    public void setNetTotal(double netTotal) { this.netTotal = netTotal; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
