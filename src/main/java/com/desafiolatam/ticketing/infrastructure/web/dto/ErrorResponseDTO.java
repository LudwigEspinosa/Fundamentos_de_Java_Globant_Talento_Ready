package com.desafiolatam.ticketing.infrastructure.web.dto;

import java.time.LocalDateTime;

/**
 * Standard unified JSON error response payload for REST endpoints.
 */
public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public static ErrorResponseDTO of(int status, String error, String message, String path) {
        return new ErrorResponseDTO(LocalDateTime.now(), status, error, message, path);
    }
}
