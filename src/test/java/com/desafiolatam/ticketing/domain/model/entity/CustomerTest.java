package com.desafiolatam.ticketing.domain.model.entity;

import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import com.desafiolatam.ticketing.domain.model.enumtype.MembershipTier;
import com.desafiolatam.ticketing.domain.model.valueobject.CustomerId;
import com.desafiolatam.ticketing.domain.model.valueobject.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Customer Domain Entity Tests")
class CustomerTest {

    @Test
    @DisplayName("Should create customer entity successfully with value objects")
    void shouldCreateCustomerSuccessfully() {
        // Arrange
        CustomerId id = CustomerId.of("CUST-01");
        String name = "Alan Turing";
        Email email = Email.of("alan@enigma.org");
        MembershipTier tier = MembershipTier.VIP;

        // Act
        Customer customer = new Customer(id, name, email, tier);

        // Assert
        assertEquals(id, customer.getId());
        assertEquals("Alan Turing", customer.getName());
        assertEquals(email, customer.getEmail());
        assertEquals(MembershipTier.VIP, customer.getTier());
        assertTrue(customer.toString().contains("Alan Turing"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Should throw InvalidDomainException when name is null or blank")
    void shouldThrowWhenNameIsInvalid(String invalidName) {
        // Arrange, Act & Assert
        assertThrows(
                InvalidDomainException.class,
                () -> new Customer(CustomerId.of("CUST-01"), invalidName, Email.of("user@test.com"), MembershipTier.REGULAR)
        );
    }

    @Test
    @DisplayName("Should throw NullPointerException when required value objects are null")
    void shouldThrowWhenRequiredObjectsAreNull() {
        // Arrange, Act & Assert
        assertThrows(NullPointerException.class, () -> new Customer(null, "Name", Email.of("user@test.com"), MembershipTier.REGULAR));
        assertThrows(NullPointerException.class, () -> new Customer(CustomerId.of("CUST-01"), "Name", null, MembershipTier.REGULAR));
        assertThrows(NullPointerException.class, () -> new Customer(CustomerId.of("CUST-01"), "Name", Email.of("user@test.com"), null));
    }

    @Test
    @DisplayName("Should evaluate equality and hashCode based on identity CustomerId")
    void shouldEvaluateEqualityBasedOnId() {
        // Arrange
        Customer c1 = new Customer(CustomerId.of("CUST-01"), "John", Email.of("john@test.com"), MembershipTier.REGULAR);
        Customer c2 = new Customer(CustomerId.of("CUST-01"), "John Other", Email.of("other@test.com"), MembershipTier.VIP);
        Customer c3 = new Customer(CustomerId.of("CUST-02"), "Jane", Email.of("jane@test.com"), MembershipTier.PREMIUM);

        // Act & Assert
        assertEquals(c1, c1);
        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertNotEquals(c1, null);
        assertNotEquals(c1, "String Object");
        assertEquals(c1.hashCode(), c2.hashCode());
    }
}
