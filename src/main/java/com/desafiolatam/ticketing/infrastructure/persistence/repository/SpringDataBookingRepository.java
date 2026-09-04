package com.desafiolatam.ticketing.infrastructure.persistence.repository;

import com.desafiolatam.ticketing.infrastructure.persistence.entity.BookingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository interface for Booking entities.
 */
@Repository
public interface SpringDataBookingRepository extends JpaRepository<BookingJpaEntity, String> {
}
