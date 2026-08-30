package com.desafiolatam.ticketing.domain.model;

import com.desafiolatam.ticketing.domain.exception.InvalidBookingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Booking Aggregate Root Tests")
class BookingTest {

    private Customer createSampleCustomer() {
        return new Customer("CUST-01", "Ada Lovelace", "ada@lovelace.org", MembershipTier.PREMIUM);
    }

    private BookingItem createSampleItem() {
        return new BookingItem("EVT-01", "Neon Festival", 100.0, 2);
    }

    @Test
    @DisplayName("Should create booking successfully with correct calculated net total")
    void shouldCreateBookingSuccessfully() {
        // Arrange
        Customer customer = createSampleCustomer();
        BookingItem item = createSampleItem();
        List<BookingItem> items = List.of(item);
        double discount = 20.0;

        // Act
        Booking booking = new Booking("BKG-001", customer, items, discount);

        // Assert
        assertEquals("BKG-001", booking.getId());
        assertEquals(customer, booking.getCustomer());
        assertEquals(1, booking.getItems().size());
        assertEquals(200.0, booking.getGrossTotal(), 0.001);
        assertEquals(20.0, booking.getDiscountAmount(), 0.001);
        assertEquals(180.0, booking.getNetTotal(), 0.001);
        assertEquals(BookingStatus.PENDING, booking.getStatus());
        assertNotNull(booking.getCreatedAt());
        assertTrue(booking.toString().contains("BKG-001"));
    }

    @Test
    @DisplayName("Should cap net total at zero if discount exceeds gross total")
    void shouldCapNetTotalAtZeroWhenDiscountExceedsGross() {
        // Arrange
        Customer customer = createSampleCustomer();
        BookingItem item = createSampleItem(); // gross = 200
        double excessDiscount = 250.0;

        // Act
        Booking booking = new Booking("BKG-002", customer, List.of(item), excessDiscount);

        // Assert
        assertEquals(0.0, booking.getNetTotal(), 0.001);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Should throw InvalidBookingException when booking ID is null or blank")
    void shouldThrowWhenBookingIdIsInvalid(String invalidId) {
        // Arrange
        Customer customer = createSampleCustomer();
        List<BookingItem> items = List.of(createSampleItem());

        // Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> new Booking(invalidId, customer, items, 0.0)
        );
        assertEquals("Booking ID cannot be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidBookingException when customer is null")
    void shouldThrowWhenCustomerIsNull() {
        // Arrange
        List<BookingItem> items = List.of(createSampleItem());

        // Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> new Booking("BKG-003", null, items, 0.0)
        );
        assertEquals("Booking customer cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidBookingException when items list is null or empty")
    void shouldThrowWhenItemsListIsNullOrEmpty() {
        // Arrange
        Customer customer = createSampleCustomer();

        // Act & Assert
        InvalidBookingException nullException = assertThrows(
                InvalidBookingException.class,
                () -> new Booking("BKG-004", customer, null, 0.0)
        );
        assertEquals("Booking items cannot be null or empty", nullException.getMessage());

        InvalidBookingException emptyException = assertThrows(
                InvalidBookingException.class,
                () -> new Booking("BKG-004", customer, Collections.emptyList(), 0.0)
        );
        assertEquals("Booking items cannot be null or empty", emptyException.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidBookingException when an item in list is null")
    void shouldThrowWhenItemInListIsNull() {
        // Arrange
        Customer customer = createSampleCustomer();
        List<BookingItem> itemsWithNull = Collections.singletonList(null);

        // Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> new Booking("BKG-005", customer, itemsWithNull, 0.0)
        );
        assertEquals("Booking item cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidBookingException when discount amount is negative")
    void shouldThrowWhenDiscountIsNegative() {
        // Arrange
        Customer customer = createSampleCustomer();
        List<BookingItem> items = List.of(createSampleItem());

        // Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> new Booking("BKG-006", customer, items, -10.0)
        );
        assertEquals("Discount amount cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Should transition booking status between CONFIRMED, FAILED and CANCELLED")
    void shouldTransitionBookingStatus() {
        // Arrange
        Customer customer = createSampleCustomer();
        Booking booking = new Booking("BKG-007", customer, List.of(createSampleItem()), 0.0);
        assertEquals(BookingStatus.PENDING, booking.getStatus());

        // Act & Assert Confirm
        booking.confirm();
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());

        // Act & Assert Cancel
        booking.cancel();
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());

        // Act & Assert Cancel already cancelled
        InvalidBookingException cancelException = assertThrows(
                InvalidBookingException.class,
                booking::cancel
        );
        assertEquals("Booking is already cancelled", cancelException.getMessage());

        // Act & Assert Failed
        Booking failedBooking = new Booking("BKG-008", customer, List.of(createSampleItem()), 0.0);
        failedBooking.markAsFailed();
        assertEquals(BookingStatus.FAILED, failedBooking.getStatus());
    }

    @Test
    @DisplayName("Should evaluate equals and hashCode based on ID")
    void shouldEvaluateEqualsAndHashCode() {
        // Arrange
        Customer customer = createSampleCustomer();
        Booking booking1 = new Booking("BKG-100", customer, List.of(createSampleItem()), 0.0);
        Booking booking2 = new Booking("BKG-100", customer, List.of(createSampleItem()), 10.0);
        Booking booking3 = new Booking("BKG-200", customer, List.of(createSampleItem()), 0.0);

        // Act & Assert
        assertEquals(booking1, booking1);
        assertEquals(booking1, booking2);
        assertNotEquals(booking1, booking3);
        assertNotEquals(booking1, null);
        assertNotEquals(booking1, "String Object");
        assertEquals(booking1.hashCode(), booking2.hashCode());
    }
}
