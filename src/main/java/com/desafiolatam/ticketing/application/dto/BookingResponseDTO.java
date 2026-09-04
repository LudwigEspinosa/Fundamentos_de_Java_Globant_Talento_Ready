package com.desafiolatam.ticketing.application.dto;

import com.desafiolatam.ticketing.domain.model.enumtype.BookingStatus;

/**
 * Data Transfer Object returned upon executing a booking command.
 */
public record BookingResponseDTO(
        String bookingId,
        String customerName,
        String eventName,
        int quantity,
        double grossTotal,
        double discountAmount,
        double netTotal,
        BookingStatus status,
        String message
) {}
