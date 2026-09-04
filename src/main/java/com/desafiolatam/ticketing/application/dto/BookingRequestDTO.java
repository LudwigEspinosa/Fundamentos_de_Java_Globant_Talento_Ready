package com.desafiolatam.ticketing.application.dto;

/**
 * Data Transfer Object representing an incoming ticket booking command.
 */
public record BookingRequestDTO(
        String customerId,
        String eventId,
        int quantity
) {}
