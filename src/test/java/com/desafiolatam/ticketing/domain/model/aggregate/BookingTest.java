package com.desafiolatam.ticketing.domain.model.aggregate;

import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import com.desafiolatam.ticketing.domain.model.entity.Customer;
import com.desafiolatam.ticketing.domain.model.enumtype.BookingStatus;
import com.desafiolatam.ticketing.domain.model.enumtype.MembershipTier;
import com.desafiolatam.ticketing.domain.model.valueobject.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Booking Aggregate Root Tests")
class BookingTest {

    private Customer createSampleCustomer() {
        return new Customer(CustomerId.of("CUST-01"), "Ada Lovelace", Email.of("ada@lovelace.org"), MembershipTier.PREMIUM);
    }

    private BookingItem createSampleItem() {
        return BookingItem.of(EventId.of("EVT-01"), "Neon Festival", Money.of(100.0), 2);
    }

    @Test
    @DisplayName("Should create booking aggregate and calculate financial totals correctly")
    void shouldCreateBookingAggregateSuccessfully() {
        // Arrange
        BookingId bookingId = BookingId.generate();
        Customer customer = createSampleCustomer();
        BookingItem item = createSampleItem();
        Money discount = Money.of(20.0);

        // Act
        Booking booking = new Booking(bookingId, customer, List.of(item), discount);

        // Assert
        assertEquals(bookingId, booking.getId());
        assertEquals(customer, booking.getCustomer());
        assertEquals(1, booking.getItems().size());
        assertEquals(Money.of(200.0), booking.getGrossTotal());
        assertEquals(Money.of(20.0), booking.getDiscountAmount());
        assertEquals(Money.of(180.0), booking.getNetTotal());
        assertEquals(BookingStatus.PENDING, booking.getStatus());
        assertNotNull(booking.getCreatedAt());
        assertTrue(booking.toString().contains("Booking{"));
    }

    @Test
    @DisplayName("Should validate constructor invariants and prevent nulls")
    void shouldValidateConstructorInvariants() {
        // Arrange
        Customer customer = createSampleCustomer();
        BookingItem item = createSampleItem();

        // Act & Assert
        assertThrows(NullPointerException.class, () -> new Booking(null, customer, List.of(item), Money.ZERO));
        assertThrows(NullPointerException.class, () -> new Booking(BookingId.generate(), null, List.of(item), Money.ZERO));
        assertThrows(InvalidDomainException.class, () -> new Booking(BookingId.generate(), customer, null, Money.ZERO));
        assertThrows(InvalidDomainException.class, () -> new Booking(BookingId.generate(), customer, Collections.emptyList(), Money.ZERO));
        assertThrows(NullPointerException.class, () -> new Booking(BookingId.generate(), customer, List.of(item), null));
        assertThrows(NullPointerException.class, () -> new Booking(BookingId.generate(), customer, Collections.singletonList(null), Money.ZERO));
    }

    @Test
    @DisplayName("Should transition booking lifecycle status correctly")
    void shouldTransitionBookingLifecycleStatus() {
        // Arrange
        Customer customer = createSampleCustomer();
        Booking booking = new Booking(BookingId.generate(), customer, List.of(createSampleItem()), Money.ZERO);
        assertEquals(BookingStatus.PENDING, booking.getStatus());

        // Act & Assert Confirm
        booking.confirm();
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());

        // Act & Assert Cancel
        booking.cancel();
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());

        // Act & Assert Cancel already cancelled
        assertThrows(InvalidDomainException.class, booking::cancel);

        // Act & Assert Failed
        Booking failedBooking = new Booking(BookingId.generate(), customer, List.of(createSampleItem()), Money.ZERO);
        failedBooking.markAsFailed();
        assertEquals(BookingStatus.FAILED, failedBooking.getStatus());
    }

    @Test
    @DisplayName("Should evaluate equals and hashCode based on identity BookingId")
    void shouldEvaluateEqualsAndHashCode() {
        // Arrange
        BookingId id = BookingId.generate();
        Customer customer = createSampleCustomer();
        Booking b1 = new Booking(id, customer, List.of(createSampleItem()), Money.ZERO);
        Booking b2 = new Booking(id, customer, List.of(createSampleItem()), Money.of(10.0));
        Booking b3 = new Booking(BookingId.generate(), customer, List.of(createSampleItem()), Money.ZERO);

        // Act & Assert
        assertEquals(b1, b1);
        assertEquals(b1, b2);
        assertNotEquals(b1, b3);
        assertNotEquals(b1, null);
        assertNotEquals(b1, "Other");
        assertEquals(b1.hashCode(), b2.hashCode());
    }
}
