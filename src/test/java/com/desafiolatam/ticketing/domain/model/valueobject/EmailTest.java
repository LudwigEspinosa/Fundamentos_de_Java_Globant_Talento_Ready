package com.desafiolatam.ticketing.domain.model.valueobject;

import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Email Value Object (record) Tests")
class EmailTest {

    @Test
    @DisplayName("Should create Email successfully and normalize to lowercase")
    void shouldCreateEmailSuccessfully() {
        // Arrange
        String rawEmail = "  User.Name@Example.COM  ";

        // Act
        Email email = new Email(rawEmail);

        // Assert
        assertEquals("user.name@example.com", email.value());
        assertEquals("user.name@example.com", email.toString());
        assertEquals(email, Email.of("user.name@example.com"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Should throw InvalidDomainException when email is null or blank")
    void shouldThrowWhenEmailIsNullOrEmpty(String invalidEmail) {
        // Arrange, Act & Assert
        InvalidDomainException exception = assertThrows(
                InvalidDomainException.class,
                () -> new Email(invalidEmail)
        );
        assertEquals("Email cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"plainaddress", "missingatsign.com", "@missingusername.com", "user@domain"})
    @DisplayName("Should throw InvalidDomainException when email format is invalid")
    void shouldThrowWhenEmailFormatIsInvalid(String invalidFormat) {
        // Arrange, Act & Assert
        InvalidDomainException exception = assertThrows(
                InvalidDomainException.class,
                () -> new Email(invalidFormat)
        );
        assertTrue(exception.getMessage().contains("Email format is invalid"));
    }
}
