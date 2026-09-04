package com.desafiolatam.ticketing.application.dto;

import com.desafiolatam.ticketing.domain.model.enumtype.EventStatus;

/**
 * Data Transfer Object representing event details for catalog queries.
 */
public record EventResponseDTO(
        String id,
        String name,
        double basePrice,
        int totalCapacity,
        int availableSeats,
        EventStatus status
) {}
