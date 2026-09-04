package com.desafiolatam.ticketing.infrastructure.adapter;

import com.desafiolatam.ticketing.domain.model.aggregate.Booking;
import com.desafiolatam.ticketing.domain.model.entity.Customer;
import com.desafiolatam.ticketing.domain.model.enumtype.MembershipTier;
import com.desafiolatam.ticketing.domain.model.valueobject.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Infrastructure Adapters Tests")
class InfrastructureAdaptersTest {

    @Test
    @DisplayName("Should test SimulatedPaymentGateway outcomes and null safeguards")
    void shouldTestSimulatedPaymentGateway() {
        // Arrange
        SimulatedPaymentGateway gateway = new SimulatedPaymentGateway();
        CustomerId customerId = CustomerId.of("CUST-01");
        Money amount = Money.of(100.0);

        // Act & Assert Success
        assertTrue(gateway.charge(customerId, amount));

        // Act & Assert Configured failure
        gateway.setShouldSucceed(false);
        assertFalse(gateway.charge(customerId, amount));

        // Act & Assert Null inputs
        assertFalse(gateway.charge(null, amount));
        assertFalse(gateway.charge(customerId, null));
    }

    @Test
    @DisplayName("Should test ConsoleNotificationService logging and safeguards")
    void shouldTestConsoleNotificationService() {
        // Arrange
        ConsoleNotificationService notificationService = new ConsoleNotificationService();
        Customer customer = new Customer(CustomerId.of("CUST-01"), "Ada", Email.of("ada@test.com"), MembershipTier.REGULAR);
        BookingItem item = BookingItem.of(EventId.of("EVT-01"), "Fest", Money.of(50.0), 1);
        Booking booking = new Booking(BookingId.generate(), customer, List.of(item), Money.ZERO);

        // Act
        notificationService.sendBookingConfirmation(customer, booking);
        notificationService.sendBookingFailureAlert(customer, "Insufficient balance");
        notificationService.sendBookingConfirmation(null, null);
        notificationService.sendBookingFailureAlert(null, "Gateway timeout");

        // Assert
        assertEquals(4, notificationService.getSentMessages().size());
        assertTrue(notificationService.getSentMessages().get(0).contains("Confirmation sent to ada@test.com"));
        assertTrue(notificationService.getSentMessages().get(1).contains("Failure alert sent to ada@test.com"));
    }
}
