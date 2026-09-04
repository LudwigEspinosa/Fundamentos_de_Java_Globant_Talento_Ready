package com.desafiolatam.ticketing.infrastructure.persistence.inmemory;

import com.desafiolatam.ticketing.domain.model.aggregate.Booking;
import com.desafiolatam.ticketing.domain.model.aggregate.Event;
import com.desafiolatam.ticketing.domain.model.entity.Customer;
import com.desafiolatam.ticketing.domain.model.enumtype.MembershipTier;
import com.desafiolatam.ticketing.domain.model.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("In-Memory Repositories Infrastructure Tests")
class InMemoryRepositoriesTest {

    private InMemoryEventRepository eventRepo;
    private InMemoryBookingRepository bookingRepo;
    private InMemoryCustomerRepository customerRepo;

    @BeforeEach
    void setUp() {
        eventRepo = new InMemoryEventRepository();
        bookingRepo = new InMemoryBookingRepository();
        customerRepo = new InMemoryCustomerRepository();
    }

    @Test
    @DisplayName("Should save, find and clear in InMemoryEventRepository")
    void shouldTestEventRepository() {
        // Arrange
        Event event = new Event(EventId.of("EVT-01"), "Cyber Fest", Money.of(40.0), SeatCapacity.of(50));

        // Act & Assert Save
        eventRepo.save(event);
        Optional<Event> found = eventRepo.findById(EventId.of("EVT-01"));
        assertTrue(found.isPresent());
        assertEquals("Cyber Fest", found.get().getName());

        assertEquals(1, eventRepo.findAll().size());
        assertTrue(eventRepo.findById(null).isEmpty());
        assertTrue(eventRepo.findById(EventId.of("EVT-NONEXISTENT")).isEmpty());

        eventRepo.clear();
        assertEquals(0, eventRepo.findAll().size());
    }

    @Test
    @DisplayName("Should save, find and clear in InMemoryCustomerRepository")
    void shouldTestCustomerRepository() {
        // Arrange
        Customer customer = new Customer(CustomerId.of("CUST-01"), "Linus", Email.of("linus@linux.org"), MembershipTier.VIP);

        // Act & Assert
        customerRepo.save(customer);
        Optional<Customer> found = customerRepo.findById(CustomerId.of("CUST-01"));
        assertTrue(found.isPresent());
        assertEquals("Linus", found.get().getName());

        assertTrue(customerRepo.findById(null).isEmpty());
        assertTrue(customerRepo.findById(CustomerId.of("CUST-NONEXISTENT")).isEmpty());

        customerRepo.clear();
        assertTrue(customerRepo.findById(CustomerId.of("CUST-01")).isEmpty());
    }

    @Test
    @DisplayName("Should save, find and clear in InMemoryBookingRepository")
    void shouldTestBookingRepository() {
        // Arrange
        BookingId id = BookingId.generate();
        Customer customer = new Customer(CustomerId.of("CUST-01"), "Linus", Email.of("linus@linux.org"), MembershipTier.VIP);
        BookingItem item = BookingItem.of(EventId.of("EVT-01"), "Concert", Money.of(30.0), 1);
        Booking booking = new Booking(id, customer, List.of(item), Money.ZERO);

        // Act & Assert
        bookingRepo.save(booking);
        Optional<Booking> found = bookingRepo.findById(id);
        assertTrue(found.isPresent());
        assertEquals(id, found.get().getId());

        assertTrue(bookingRepo.findById(null).isEmpty());
        assertTrue(bookingRepo.findById(BookingId.generate()).isEmpty());

        bookingRepo.clear();
        assertTrue(bookingRepo.findById(id).isEmpty());
    }
}
