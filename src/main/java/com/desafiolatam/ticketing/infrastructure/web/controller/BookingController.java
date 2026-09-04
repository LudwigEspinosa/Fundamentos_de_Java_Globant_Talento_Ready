package com.desafiolatam.ticketing.infrastructure.web.controller;

import com.desafiolatam.ticketing.application.dto.BookingRequestDTO;
import com.desafiolatam.ticketing.application.dto.BookingResponseDTO;
import com.desafiolatam.ticketing.application.usecase.CancelBookingUseCase;
import com.desafiolatam.ticketing.application.usecase.CreateBookingUseCase;
import com.desafiolatam.ticketing.domain.model.aggregate.Booking;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * REST Web Controller exposing ticket booking and cancellation operations.
 */
@RestController
@RequestMapping("/api/v1/bookings")
@CrossOrigin(origins = "*")
@Tag(name = "Reservas", description = "Operaciones de emisión, reserva y anulación de entradas para clientes")
public class BookingController {

    private final CreateBookingUseCase createBookingUseCase;
    private final CancelBookingUseCase cancelBookingUseCase;

    public BookingController(CreateBookingUseCase createBookingUseCase, CancelBookingUseCase cancelBookingUseCase) {
        this.createBookingUseCase = Objects.requireNonNull(createBookingUseCase, "CreateBookingUseCase cannot be null");
        this.cancelBookingUseCase = Objects.requireNonNull(cancelBookingUseCase, "CancelBookingUseCase cannot be null");
    }

    @PostMapping
    @Operation(summary = "Crear nueva reserva de tickets", description = "Procesa la reserva de entradas, aplica descuentos por fidelidad y valida el pago en pasarela.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reserva creada y confirmada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Petición inválida o evento no activo"),
            @ApiResponse(responseCode = "402", description = "Pago rechazado por la pasarela de pagos"),
            @ApiResponse(responseCode = "404", description = "Cliente o evento no encontrado"),
            @ApiResponse(responseCode = "409", description = "Stock o asientos insuficientes")
    })
    public ResponseEntity<BookingResponseDTO> createBooking(
            @RequestBody BookingRequestDTO request
    ) {
        BookingResponseDTO response = createBookingUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancelar reserva existente", description = "Anula una reserva confirmada y libera los asientos de vuelta al inventario del evento.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva anulada y stock liberado"),
            @ApiResponse(responseCode = "400", description = "La reserva ya se encontraba cancelada"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    public ResponseEntity<BookingResponseDTO> cancelBooking(
            @Parameter(description = "Identificador único de la reserva (ej: BKG-ABC12345)")
            @PathVariable("id") String id
    ) {
        Booking cancelledBooking = cancelBookingUseCase.execute(id);
        BookingResponseDTO response = new BookingResponseDTO(
                cancelledBooking.getId().value(),
                cancelledBooking.getCustomer().getName(),
                cancelledBooking.getItems().isEmpty() ? "N/A" : cancelledBooking.getItems().get(0).eventName(),
                cancelledBooking.getItems().isEmpty() ? 0 : cancelledBooking.getItems().get(0).quantity(),
                cancelledBooking.getGrossTotal().amount(),
                cancelledBooking.getDiscountAmount().amount(),
                cancelledBooking.getNetTotal().amount(),
                cancelledBooking.getStatus(),
                "Booking successfully cancelled and inventory released"
        );
        return ResponseEntity.ok(response);
    }
}
