package com.desafiolatam.ticketing.domain.port;

import com.desafiolatam.ticketing.domain.model.Booking;
import com.desafiolatam.ticketing.domain.model.Customer;

/**
 * Output port for dispatching customer notifications (email, SMS, push).
 */
public interface NotificationService {

    /**
     * Sends a booking confirmation message to the customer.
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
