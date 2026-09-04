package com.desafiolatam.ticketing.infrastructure.persistence.repository;

import com.desafiolatam.ticketing.infrastructure.persistence.entity.EventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository interface for Event entities.
 */
@Repository
public interface SpringDataEventRepository extends JpaRepository<EventJpaEntity, String> {
}
