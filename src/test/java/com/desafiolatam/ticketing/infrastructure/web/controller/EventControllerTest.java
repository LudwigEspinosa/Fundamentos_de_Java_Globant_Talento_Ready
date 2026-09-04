package com.desafiolatam.ticketing.infrastructure.web.controller;

import com.desafiolatam.ticketing.application.dto.EventResponseDTO;
import com.desafiolatam.ticketing.application.usecase.GetEventCatalogUseCase;
import com.desafiolatam.ticketing.domain.exception.EventNotFoundException;
import com.desafiolatam.ticketing.domain.model.aggregate.Event;
import com.desafiolatam.ticketing.domain.model.enumtype.EventStatus;
import com.desafiolatam.ticketing.domain.model.valueobject.EventId;
import com.desafiolatam.ticketing.domain.model.valueobject.Money;
import com.desafiolatam.ticketing.domain.model.valueobject.SeatCapacity;
import com.desafiolatam.ticketing.domain.repository.EventRepository;
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
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventController REST API Tests (MockMvc)")
class EventControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GetEventCatalogUseCase getEventCatalogUseCase;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventController eventController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/events - Should return list of events with 200 OK")
    void shouldReturnAllEvents() throws Exception {
        // Arrange
        EventResponseDTO dto = new EventResponseDTO("EVT-01", "Festival", 50.0, 100, 80, EventStatus.ACTIVE);
        when(getEventCatalogUseCase.execute()).thenReturn(List.of(dto));

        // Act & Assert
        mockMvc.perform(get("/api/v1/events").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("EVT-01"))
                .andExpect(jsonPath("$[0].name").value("Festival"))
                .andExpect(jsonPath("$[0].basePrice").value(50.0))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /api/v1/events/{id} - Should return event details when found")
    void shouldReturnEventById() throws Exception {
        // Arrange
        Event event = new Event(EventId.of("EVT-01"), "Festival", Money.of(50.0), SeatCapacity.of(100));
        when(eventRepository.findById(EventId.of("EVT-01"))).thenReturn(Optional.of(event));

        // Act & Assert
        mockMvc.perform(get("/api/v1/events/EVT-01").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("EVT-01"))
                .andExpect(jsonPath("$.name").value("Festival"))
                .andExpect(jsonPath("$.basePrice").value(50.0));
    }

    @Test
    @DisplayName("GET /api/v1/events/{id} - Should return 404 NOT_FOUND when event does not exist")
    void shouldReturn404WhenEventNotFound() throws Exception {
        // Arrange
        when(eventRepository.findById(EventId.of("EVT-999"))).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/events/EVT-999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Event not found with ID: EVT-999"));
    }
}
