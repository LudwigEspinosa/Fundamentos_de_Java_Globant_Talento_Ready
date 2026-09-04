package com.desafiolatam.ticketing.domain.model.valueobject;

import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Money Value Object (record) Tests")
class MoneyTest {

    @Test
    @DisplayName("Should create Money and perform arithmetic operations correctly")
    void shouldPerformArithmeticOperations() {
        // Arrange
        Money m1 = Money.of(100.50);
        Money m2 = Money.of(49.50);

        // Act
        Money sum = m1.add(m2);
        Money diff = m1.subtract(m2);
        Money mult = m1.multiply(2.0);

        // Assert
        assertEquals(150.00, sum.amount(), 0.001);
        assertEquals(51.00, diff.amount(), 0.001);
        assertEquals(201.00, mult.amount(), 0.001);
        assertTrue(m1.isGreaterThan(m2));
        assertFalse(m2.isGreaterThan(m1));
        assertEquals(1, m1.compareTo(m2));
        assertEquals(0, m1.compareTo(Money.of(100.50)));
        assertFalse(m1.isZero());
        assertTrue(Money.ZERO.isZero());
        assertEquals("$100.50", m1.toString());
    }

    @Test
    @DisplayName("Should throw InvalidDomainException when amount or multiplication factor is negative")
    void shouldThrowWhenAmountOrFactorIsNegative() {
        // Arrange, Act & Assert
        assertThrows(InvalidDomainException.class, () -> new Money(-10.0));
        assertThrows(InvalidDomainException.class, () -> Money.of(50.0).multiply(-1.5));
    }

    @Test
    @DisplayName("Should cap subtraction at zero and throw on null operations")
    void shouldCapSubtractionAtZeroAndHandleNulls() {
        // Arrange
        Money m1 = Money.of(20.0);
        Money m2 = Money.of(50.0);

        // Act
        Money result = m1.subtract(m2);

        // Assert
        assertEquals(0.0, result.amount(), 0.001);
        assertThrows(NullPointerException.class, () -> m1.add(null));
        assertThrows(NullPointerException.class, () -> m1.subtract(null));
        assertThrows(NullPointerException.class, () -> m1.isGreaterThan(null));
    }

    @ParameterizedTest
    @CsvSource({
            "10.555, 10.56",
            "10.554, 10.55",
            "0.0, 0.0"
    })
    @DisplayName("Should round monetary amounts to two decimal places")
    void shouldRoundToTwoDecimalPlaces(double input, double expected) {
        // Arrange & Act
        Money money = Money.of(input);

        // Assert
        assertEquals(expected, money.amount(), 0.001);
    }
}
