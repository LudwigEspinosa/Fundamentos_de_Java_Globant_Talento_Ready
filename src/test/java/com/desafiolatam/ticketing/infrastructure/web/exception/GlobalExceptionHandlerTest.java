package com.desafiolatam.ticketing.infrastructure.web.exception;

import com.desafiolatam.ticketing.domain.exception.EventNotActiveException;
import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import com.desafiolatam.ticketing.infrastructure.web.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest("GET", "/api/v1/test");
    }

    @Test
    @DisplayName("Should handle EventNotActiveException as 400 BAD_REQUEST")
    void shouldHandleEventNotActive() {
        // Arrange
        EventNotActiveException ex = new EventNotActiveException("Event inactive");

        // Act
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleEventNotActive(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Event inactive", response.getBody().message());
    }

    @Test
    @DisplayName("Should handle InvalidDomainException as 400 BAD_REQUEST")
    void shouldHandleInvalidDomain() {
        // Arrange
        InvalidDomainException ex = new InvalidDomainException("Invalid domain data");

        // Act
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleInvalidDomain(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid domain data", response.getBody().message());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException as 400 BAD_REQUEST")
    void shouldHandleValidationErrors() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "quantity", "must be greater than 0");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(ex.getBindingResult()).thenReturn(bindingResult);

        // Act
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleValidationErrors(ex, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("quantity: must be greater than 0", response.getBody().message());
    }

    @Test
    @DisplayName("Should handle generic Exception as 500 INTERNAL_SERVER_ERROR")
    void shouldHandleGenericException() {
        // Arrange
        Exception ex = new RuntimeException("Unexpected fatal crash");

        // Act
        ResponseEntity<ErrorResponseDTO> response = exceptionHandler.handleGenericException(ex, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Unexpected fatal crash", response.getBody().message());
    }
}
