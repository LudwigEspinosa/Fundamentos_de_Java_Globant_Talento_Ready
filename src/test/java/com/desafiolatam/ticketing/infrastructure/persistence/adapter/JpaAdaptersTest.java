package com.desafiolatam.ticketing.infrastructure.persistence.adapter;

import com.desafiolatam.ticketing.domain.model.aggregate.Booking;
import com.desafiolatam.ticketing.domain.model.aggregate.Event;
import com.desafiolatam.ticketing.domain.model.entity.Customer;
import com.desafiolatam.ticketing.domain.model.enumtype.BookingStatus;
import com.desafiolatam.ticketing.domain.model.enumtype.EventStatus;
import com.desafiolatam.ticketing.domain.model.enumtype.MembershipTier;
import com.desafiolatam.ticketing.domain.model.valueobject.*;
import com.desafiolatam.ticketing.infrastructure.persistence.entity.BookingJpaEntity;
import com.desafiolatam.ticketing.infrastructure.persistence.entity.CustomerJpaEntity;
import com.desafiolatam.ticketing.infrastructure.persistence.entity.EventJpaEntity;
import com.desafiolatam.ticketing.infrastructure.persistence.repository.SpringDataBookingRepository;
import com.desafiolatam.ticketing.infrastructure.persistence.repository.SpringDataCustomerRepository;
import com.desafiolatam.ticketing.infrastructure.persistence.repository.SpringDataEventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JPA Adapters & Entity Mapping Tests")
class JpaAdaptersTest {

    @Mock
    private SpringDataEventRepository springDataEventRepository;

    @Mock
    private SpringDataCustomerRepository springDataCustomerRepository;

    @Mock
    private SpringDataBookingRepository springDataBookingRepository;

    @InjectMocks
    private JpaEventRepositoryAdapter eventAdapter;

    @InjectMocks
    private JpaCustomerRepositoryAdapter customerAdapter;

    @InjectMocks
    private JpaBookingRepositoryAdapter bookingAdapter;

    @Test
    @DisplayName("Should test JpaEventRepositoryAdapter methods and mapping")
    void shouldTestEventAdapter() {
        // Arrange
        Event event = new Event(EventId.of("EVT-01"), "Cyber Fest", Money.of(50.0), SeatCapacity.of(100));
        EventJpaEntity entity = EventJpaEntity.fromDomain(event);

        when(springDataEventRepository.save(any(EventJpaEntity.class))).thenReturn(entity);
        when(springDataEventRepository.findById("EVT-01")).thenReturn(Optional.of(entity));
        when(springDataEventRepository.findAll()).thenReturn(List.of(entity));

        // Act & Assert
        Event saved = eventAdapter.save(event);
        assertEquals("Cyber Fest", saved.getName());

        Optional<Event> found = eventAdapter.findById(EventId.of("EVT-01"));
        assertTrue(found.isPresent());

        List<Event> all = eventAdapter.findAll();
        assertEquals(1, all.size());

        assertTrue(eventAdapter.findById(null).isEmpty());
    }

    @Test
    @DisplayName("Should test JpaCustomerRepositoryAdapter methods and mapping")
    void shouldTestCustomerAdapter() {
        // Arrange
        Customer customer = new Customer(CustomerId.of("CUST-01"), "Ada", Email.of("ada@test.com"), MembershipTier.VIP);
        CustomerJpaEntity entity = CustomerJpaEntity.fromDomain(customer);

        when(springDataCustomerRepository.save(any(CustomerJpaEntity.class))).thenReturn(entity);
        when(springDataCustomerRepository.findById("CUST-01")).thenReturn(Optional.of(entity));

        // Act & Assert
        Customer saved = customerAdapter.save(customer);
        assertEquals("Ada", saved.getName());

        Optional<Customer> found = customerAdapter.findById(CustomerId.of("CUST-01"));
        assertTrue(found.isPresent());
        assertTrue(customerAdapter.findById(null).isEmpty());
    }

    @Test
    @DisplayName("Should test JpaBookingRepositoryAdapter methods and mapping across all statuses")
    void shouldTestBookingAdapter() {
        // Arrange
        Customer customer = new Customer(CustomerId.of("CUST-01"), "Ada", Email.of("ada@test.com"), MembershipTier.VIP);
        BookingItem item = BookingItem.of(EventId.of("EVT-01"), "Fest", Money.of(50.0), 2);
        Booking booking = new Booking(new BookingId("BKG-01"), customer, List.of(item), Money.of(20.0));
        booking.confirm();

        BookingJpaEntity entity = BookingJpaEntity.fromDomain(booking);

        when(springDataBookingRepository.save(any(BookingJpaEntity.class))).thenReturn(entity);
        when(springDataBookingRepository.findById("BKG-01")).thenReturn(Optional.of(entity));

        // Act & Assert
        Booking saved = bookingAdapter.save(booking);
        assertEquals(BookingStatus.CONFIRMED, saved.getStatus());

        Optional<Booking> found = bookingAdapter.findById(new BookingId("BKG-01"));
        assertTrue(found.isPresent());
        assertTrue(bookingAdapter.findById(null).isEmpty());

        // Test mapping CANCELLED and FAILED statuses
        BookingJpaEntity cancelledJpa = new BookingJpaEntity("BKG-02", CustomerJpaEntity.fromDomain(customer), 100.0, 0.0, 100.0, BookingStatus.CANCELLED, LocalDateTime.now());
        cancelledJpa.setItems(List.of());
        assertEquals(BookingStatus.CANCELLED, cancelledJpa.toDomain().getStatus());

        BookingJpaEntity failedJpa = new BookingJpaEntity("BKG-03", CustomerJpaEntity.fromDomain(customer), 100.0, 0.0, 100.0, BookingStatus.FAILED, LocalDateTime.now());
        failedJpa.setItems(List.of());
        assertEquals(BookingStatus.FAILED, failedJpa.toDomain().getStatus());
    }
}
