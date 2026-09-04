package com.desafiolatam.ticketing.infrastructure.web.controller;

import com.desafiolatam.ticketing.application.dto.BookingRequestDTO;
import com.desafiolatam.ticketing.application.dto.BookingResponseDTO;
import com.desafiolatam.ticketing.application.usecase.CancelBookingUseCase;
import com.desafiolatam.ticketing.application.usecase.CreateBookingUseCase;
import com.desafiolatam.ticketing.domain.exception.InsufficientSeatsException;
import com.desafiolatam.ticketing.domain.exception.PaymentFailedException;
import com.desafiolatam.ticketing.domain.model.aggregate.Booking;
import com.desafiolatam.ticketing.domain.model.entity.Customer;
import com.desafiolatam.ticketing.domain.model.enumtype.BookingStatus;
import com.desafiolatam.ticketing.domain.model.enumtype.MembershipTier;
import com.desafiolatam.ticketing.domain.model.valueobject.*;
import com.desafiolatam.ticketing.infrastructure.web.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingController REST API Tests (MockMvc)")
class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateBookingUseCase createBookingUseCase;

    @Mock
    private CancelBookingUseCase cancelBookingUseCase;

    @InjectMocks
    private BookingController bookingController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/bookings - Should create booking and return 201 CREATED")
    void shouldCreateBookingSuccessfully() throws Exception {
        // Arrange
        BookingResponseDTO responseDTO = new BookingResponseDTO(
                "BKG-001", "Ada Lovelace", "Neon Fest", 2, 100.0, 20.0, 80.0, BookingStatus.CONFIRMED, "Success"
        );
        when(createBookingUseCase.execute(any(BookingRequestDTO.class))).thenReturn(responseDTO);

        String jsonPayload = """
                {
                    "customerId": "CUST-01",
                    "eventId": "EVT-01",
                    "quantity": 2
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bookingId").value("BKG-001"))
                .andExpect(jsonPath("$.customerName").value("Ada Lovelace"))
                .andExpect(jsonPath("$.netTotal").value(80.0))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("POST /api/v1/bookings - Should return 409 CONFLICT when seats are insufficient")
    void shouldReturn409WhenInsufficientSeats() throws Exception {
        // Arrange
        when(createBookingUseCase.execute(any(BookingRequestDTO.class)))
                .thenThrow(new InsufficientSeatsException("Not enough seats available"));

        String jsonPayload = """
                {
                    "customerId": "CUST-01",
                    "eventId": "EVT-01",
                    "quantity": 50
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Not enough seats available"));
    }

    @Test
    @DisplayName("POST /api/v1/bookings - Should return 402 PAYMENT_REQUIRED when payment fails")
    void shouldReturn402WhenPaymentFails() throws Exception {
        // Arrange
        when(createBookingUseCase.execute(any(BookingRequestDTO.class)))
                .thenThrow(new PaymentFailedException("Payment rejected"));

        String jsonPayload = """
                {
                    "customerId": "CUST-01",
                    "eventId": "EVT-01",
                    "quantity": 2
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.status").value(402))
                .andExpect(jsonPath("$.message").value("Payment rejected"));
    }

    @Test
    @DisplayName("POST /api/v1/bookings/{id}/cancel - Should cancel booking and return 200 OK")
    void shouldCancelBookingSuccessfully() throws Exception {
        // Arrange
        Customer customer = new Customer(CustomerId.of("CUST-01"), "Ada", Email.of("ada@test.com"), MembershipTier.REGULAR);
        BookingItem item = BookingItem.of(EventId.of("EVT-01"), "Fest", Money.of(50.0), 2);
        Booking booking = new Booking(new BookingId("BKG-01"), customer, List.of(item), Money.ZERO);
        booking.cancel();

        when(cancelBookingUseCase.execute("BKG-01")).thenReturn(booking);

        // Act & Assert
        mockMvc.perform(post("/api/v1/bookings/BKG-01/cancel")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value("BKG-01"))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
