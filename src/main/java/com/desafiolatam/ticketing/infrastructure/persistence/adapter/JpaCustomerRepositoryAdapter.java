package com.desafiolatam.ticketing.infrastructure.persistence.adapter;

import com.desafiolatam.ticketing.domain.model.entity.Customer;
import com.desafiolatam.ticketing.domain.model.valueobject.CustomerId;
import com.desafiolatam.ticketing.domain.repository.CustomerRepository;
import com.desafiolatam.ticketing.infrastructure.persistence.entity.CustomerJpaEntity;
import com.desafiolatam.ticketing.infrastructure.persistence.repository.SpringDataCustomerRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * Concrete JPA Adapter implementing domain CustomerRepository contract.
 */
@Component
@Primary
public class JpaCustomerRepositoryAdapter implements CustomerRepository {

    private final SpringDataCustomerRepository springDataCustomerRepository;

    public JpaCustomerRepositoryAdapter(SpringDataCustomerRepository springDataCustomerRepository) {
        this.springDataCustomerRepository = Objects.requireNonNull(springDataCustomerRepository, "SpringDataCustomerRepository cannot be null");
    }

    @Override
    public Optional<Customer> findById(CustomerId customerId) {
        if (customerId == null) return Optional.empty();
        return springDataCustomerRepository.findById(customerId.value())
                .map(CustomerJpaEntity::toDomain);
    }

    @Override
    public Customer save(Customer customer) {
        Objects.requireNonNull(customer, "Customer cannot be null");
        CustomerJpaEntity entity = CustomerJpaEntity.fromDomain(customer);
        CustomerJpaEntity savedEntity = springDataCustomerRepository.save(entity);
        return savedEntity.toDomain();
    }
}
