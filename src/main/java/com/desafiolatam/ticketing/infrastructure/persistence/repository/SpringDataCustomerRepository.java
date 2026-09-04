package com.desafiolatam.ticketing.infrastructure.persistence.repository;

import com.desafiolatam.ticketing.infrastructure.persistence.entity.CustomerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository interface for Customer entities.
 */
@Repository
public interface SpringDataCustomerRepository extends JpaRepository<CustomerJpaEntity, String> {
}
