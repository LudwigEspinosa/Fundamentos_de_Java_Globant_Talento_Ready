package com.desafiolatam.ticketing.domain.dto;

import com.desafiolatam.ticketing.domain.model.BookingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DTOs (BookingRequest & BookingResponse) Tests")
class BookingDtoTest {

    @Test
    @DisplayName("Should test BookingRequest properties, equals, hashCode and toString")
    void shouldTestBookingRequest() {
        // Arrange
        BookingRequest req1 = new BookingRequest("EVT-01", 3);
        BookingRequest req2 = new BookingRequest("EVT-01", 3);
        BookingRequest req3 = new BookingRequest("EVT-02", 1);

        // Act & Assert
        assertEquals("EVT-01", req1.getEventId());
        assertEquals(3, req1.getQuantity());
        assertEquals(req1, req1);
        assertEquals(req1, req2);
        assertNotEquals(req1, req3);
        assertNotEquals(req1, null);
        assertNotEquals(req1, "Other Object");
        assertEquals(req1.hashCode(), req2.hashCode());
        assertTrue(req1.toString().contains("EVT-01"));
    }

    @Test
    @DisplayName("Should test BookingResponse properties, equals, hashCode and toString")
    void shouldTestBookingResponse() {
        // Arrange
        BookingResponse res1 = new BookingResponse("BKG-01", "Ada", "Neon Fest", 2, 180.0, BookingStatus.CONFIRMED, "Success");
        BookingResponse res2 = new BookingResponse("BKG-01", "Ada", "Neon Fest", 2, 180.0, BookingStatus.CONFIRMED, "Success");
        BookingResponse res3 = new BookingResponse("BKG-02", "Bob", "Rock Fest", 1, 50.0, BookingStatus.PENDING, "Pending");

        // Act & Assert
        assertEquals("BKG-01", res1.getBookingId());
        assertEquals("Ada", res1.getCustomerName());
        assertEquals("Neon Fest", res1.getEventName());
        assertEquals(2, res1.getQuantity());
        assertEquals(180.0, res1.getTotalPaid());
        assertEquals(BookingStatus.CONFIRMED, res1.getStatus());
        assertEquals("Success", res1.getMessage());

        assertEquals(res1, res1);
        assertEquals(res1, res2);
        assertNotEquals(res1, res3);
        assertNotEquals(res1, null);
        assertNotEquals(res1, "Other Object");
        assertEquals(res1.hashCode(), res2.hashCode());
        assertTrue(res1.toString().contains("BKG-01"));
    }
}
