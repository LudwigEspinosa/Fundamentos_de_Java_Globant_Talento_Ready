package com.desafiolatam.ticketing.domain.dto;

import com.desafiolatam.ticketing.domain.model.BookingStatus;
import java.util.Objects;

/**
 * Data Transfer Object returned upon successfully processing a booking.
 */
public class BookingResponse {

    private final String bookingId;
    private final String customerName;
    private final String eventName;
    private final int quantity;
    private final double totalPaid;
    private final BookingStatus status;
    private final String message;

    public BookingResponse(String bookingId, String customerName, String eventName, int quantity, double totalPaid, BookingStatus status, String message) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.eventName = eventName;
        this.quantity = quantity;
        this.totalPaid = totalPaid;
        this.status = status;
        this.message = message;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getEventName() {
        return eventName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingResponse that = (BookingResponse) o;
        return quantity == that.quantity &&
                Double.compare(that.totalPaid, totalPaid) == 0 &&
                Objects.equals(bookingId, that.bookingId) &&
                Objects.equals(customerName, that.customerName) &&
                Objects.equals(eventName, that.eventName) &&
                status == that.status &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId, customerName, eventName, quantity, totalPaid, status, message);
    }

    @Override
    public String toString() {
        return "BookingResponse{" +
                "bookingId='" + bookingId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", eventName='" + eventName + '\'' +
                ", quantity=" + quantity +
                ", totalPaid=" + totalPaid +
                ", status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
