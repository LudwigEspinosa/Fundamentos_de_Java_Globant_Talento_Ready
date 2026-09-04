package com.desafiolatam.ticketing.infrastructure.adapter;

import com.desafiolatam.ticketing.application.port.NotificationService;
import com.desafiolatam.ticketing.domain.model.aggregate.Booking;
import com.desafiolatam.ticketing.domain.model.entity.Customer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Infrastructure adapter dispatching notifications to console and in-memory log.
 */
public class ConsoleNotificationService implements NotificationService {

    private final List<String> sentMessages = new ArrayList<>();

    @Override
    public void sendBookingConfirmation(Customer customer, Booking booking) {
        String msg = "Confirmation sent to " + (customer != null ? customer.getEmail().value() : "unknown") +
                     " for booking ID: " + (booking != null ? booking.getId().value() : "unknown");
        sentMessages.add(msg);
    }

    @Override
    public void sendBookingFailureAlert(Customer customer, String reason) {
        String msg = "Failure alert sent to " + (customer != null ? customer.getEmail().value() : "unknown") +
                     ". Reason: " + reason;
        sentMessages.add(msg);
    }

    public List<String> getSentMessages() {
        return Collections.unmodifiableList(sentMessages);
    }
}
