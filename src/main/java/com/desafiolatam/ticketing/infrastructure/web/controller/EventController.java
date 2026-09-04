package com.desafiolatam.ticketing.infrastructure.web.controller;

import com.desafiolatam.ticketing.application.dto.EventResponseDTO;
import com.desafiolatam.ticketing.application.usecase.GetEventCatalogUseCase;
import com.desafiolatam.ticketing.domain.exception.EventNotFoundException;
import com.desafiolatam.ticketing.domain.model.aggregate.Event;
import com.desafiolatam.ticketing.domain.model.valueobject.EventId;
import com.desafiolatam.ticketing.domain.repository.EventRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * REST Web Controller exposing event catalog endpoints.
 */
@RestController
@RequestMapping("/api/v1/events")
@CrossOrigin(origins = "*")
@Tag(name = "Eventos", description = "Operaciones de consulta sobre la cartelera de conciertos y eventos de NeonPulse")
public class EventController {

    private final GetEventCatalogUseCase getEventCatalogUseCase;
    private final EventRepository eventRepository;

    public EventController(GetEventCatalogUseCase getEventCatalogUseCase, EventRepository eventRepository) {
        this.getEventCatalogUseCase = Objects.requireNonNull(getEventCatalogUseCase, "GetEventCatalogUseCase cannot be null");
        this.eventRepository = Objects.requireNonNull(eventRepository, "EventRepository cannot be null");
    }

    @GetMapping
    @Operation(summary = "Consultar cartelera de eventos", description = "Retorna todos los eventos activos y su inventario de asientos disponible.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cartelera de eventos obtenida con éxito")
    })
    public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
        List<EventResponseDTO> catalog = getEventCatalogUseCase.execute();
        return ResponseEntity.ok(catalog);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar evento por ID", description = "Obtiene los detalles de un evento específico según su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento encontrado"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    public ResponseEntity<EventResponseDTO> getEventById(
            @Parameter(description = "Identificador único del evento (ej: EVT-001)")
            @PathVariable("id") String id
    ) {
        EventId eventId = new EventId(id);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + id));

        EventResponseDTO response = new EventResponseDTO(
                event.getId().value(),
                event.getName(),
                event.getBasePrice().amount(),
                event.getTotalCapacity(),
                event.getAvailableSeats(),
                event.getStatus()
        );

        return ResponseEntity.ok(response);
    }
}
