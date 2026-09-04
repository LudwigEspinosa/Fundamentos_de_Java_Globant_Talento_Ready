package com.desafiolatam.ticketing.application.dto;

import com.desafiolatam.ticketing.domain.model.enumtype.BookingStatus;
import com.desafiolatam.ticketing.domain.model.enumtype.EventStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Application DTOs (records) Tests")
class ApplicationDtoTest {

    @Test
    @DisplayName("Should test BookingRequestDTO properties")
    void shouldTestBookingRequestDTO() {
        // Arrange & Act
        BookingRequestDTO dto = new BookingRequestDTO("CUST-01", "EVT-01", 3);

        // Assert
        assertEquals("CUST-01", dto.customerId());
        assertEquals("EVT-01", dto.eventId());
        assertEquals(3, dto.quantity());
        assertNotNull(dto.toString());
    }

    @Test
    @DisplayName("Should test BookingResponseDTO properties")
    void shouldTestBookingResponseDTO() {
        // Arrange & Act
        BookingResponseDTO dto = new BookingResponseDTO(
                "BKG-01", "Ada", "Concert", 2, 100.0, 20.0, 80.0, BookingStatus.CONFIRMED, "Success"
        );

        // Assert
        assertEquals("BKG-01", dto.bookingId());
        assertEquals("Ada", dto.customerName());
        assertEquals("Concert", dto.eventName());
        assertEquals(2, dto.quantity());
        assertEquals(100.0, dto.grossTotal(), 0.001);
        assertEquals(20.0, dto.discountAmount(), 0.001);
        assertEquals(80.0, dto.netTotal(), 0.001);
        assertEquals(BookingStatus.CONFIRMED, dto.status());
        assertEquals("Success", dto.message());
    }

    @Test
    @DisplayName("Should test EventResponseDTO properties")
    void shouldTestEventResponseDTO() {
        // Arrange & Act
        EventResponseDTO dto = new EventResponseDTO("EVT-01", "Cyber Fest", 50.0, 100, 80, EventStatus.ACTIVE);

        // Assert
        assertEquals("EVT-01", dto.id());
        assertEquals("Cyber Fest", dto.name());
        assertEquals(50.0, dto.basePrice(), 0.001);
        assertEquals(100, dto.totalCapacity());
        assertEquals(80, dto.availableSeats());
        assertEquals(EventStatus.ACTIVE, dto.status());
    }
}
