package com.desafiolatam.ticketing.domain.model;

import com.desafiolatam.ticketing.domain.exception.InvalidBookingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Customer Entity Tests")
class CustomerTest {

    @Test
    @DisplayName("Should create customer successfully with valid attributes")
    void shouldCreateCustomerSuccessfully() {
        // Arrange
        String id = "CUST-001";
        String name = "Alan Turing";
        String email = "alan.turing@enigma.org";
        MembershipTier tier = MembershipTier.VIP;

        // Act
        Customer customer = new Customer(id, name, email, tier);

        // Assert
        assertEquals("CUST-001", customer.getId());
        assertEquals("Alan Turing", customer.getName());
        assertEquals("alan.turing@enigma.org", customer.getEmail());
        assertEquals(MembershipTier.VIP, customer.getTier());
        assertTrue(customer.toString().contains("Alan Turing"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Should throw InvalidBookingException when customer ID is null or blank")
    void shouldThrowWhenIdIsInvalid(String invalidId) {
        // Arrange
        String name = "Grace Hopper";
        String email = "grace@navy.mil";
        MembershipTier tier = MembershipTier.PREMIUM;

        // Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> new Customer(invalidId, name, email, tier)
        );
        assertEquals("Customer ID cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Should throw InvalidBookingException when customer name is null or blank")
    void shouldThrowWhenNameIsInvalid(String invalidName) {
        // Arrange
        String id = "CUST-002";
        String email = "grace@navy.mil";
        MembershipTier tier = MembershipTier.PREMIUM;

        // Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> new Customer(id, invalidName, email, tier)
        );
        assertEquals("Customer name cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"invalid-email", "user@domain", "user.domain.com", "plainaddress"})
    @DisplayName("Should throw InvalidBookingException when customer email is invalid")
    void shouldThrowWhenEmailIsInvalid(String invalidEmail) {
        // Arrange
        String id = "CUST-003";
        String name = "Ada Lovelace";
        MembershipTier tier = MembershipTier.REGULAR;

        // Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> new Customer(id, name, invalidEmail, tier)
        );
        assertTrue(exception.getMessage().contains("Customer email is invalid"));
    }

    @Test
    @DisplayName("Should throw InvalidBookingException when membership tier is null")
    void shouldThrowWhenTierIsNull() {
        // Arrange
        String id = "CUST-004";
        String name = "Linus Torvalds";
        String email = "linus@linux.org";

        // Act & Assert
        InvalidBookingException exception = assertThrows(
                InvalidBookingException.class,
                () -> new Customer(id, name, email, null)
        );
        assertEquals("Customer membership tier cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should correctly evaluate equals and hashCode based on ID")
    void shouldEvaluateEqualsAndHashCode() {
        // Arrange
        Customer customer1 = new Customer("CUST-100", "John Doe", "john@example.com", MembershipTier.REGULAR);
        Customer customer2 = new Customer("CUST-100", "John Clone", "john.clone@example.com", MembershipTier.VIP);
        Customer customer3 = new Customer("CUST-200", "Jane Doe", "jane@example.com", MembershipTier.PREMIUM);

        // Act & Assert
        assertEquals(customer1, customer1);
        assertEquals(customer1, customer2);
        assertNotEquals(customer1, customer3);
        assertNotEquals(customer1, null);
        assertNotEquals(customer1, "String Object");
        assertEquals(customer1.hashCode(), customer2.hashCode());
    }
}
