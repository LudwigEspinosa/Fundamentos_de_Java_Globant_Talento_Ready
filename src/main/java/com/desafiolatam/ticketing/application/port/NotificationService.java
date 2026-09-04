package com.desafiolatam.ticketing.application.port;

import com.desafiolatam.ticketing.domain.model.aggregate.Booking;
import com.desafiolatam.ticketing.domain.model.entity.Customer;

/**
 * Outbound port for customer notification dispatch (email, sms, push).
 */
public interface NotificationService {

    /**
     * Sends a booking confirmation notification to the customer.
     *
     * @param customer recipient customer
     * @param booking confirmed booking details
     */
    void sendBookingConfirmation(Customer customer, Booking booking);

    /**
     * Sends an alert notification if a booking transaction fails.
     *
     * @param customer recipient customer
     * @param reason failure description
     */
    void sendBookingFailureAlert(Customer customer, String reason);
}
