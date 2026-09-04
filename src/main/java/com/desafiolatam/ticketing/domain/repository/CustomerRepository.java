package com.desafiolatam.ticketing.domain.repository;

import com.desafiolatam.ticketing.domain.model.entity.Customer;
import com.desafiolatam.ticketing.domain.model.valueobject.CustomerId;
import java.util.Optional;

/**
 * Pure domain contract for Customer persistence and lookup boundary.
 */
public interface CustomerRepository {

    /**
     * Finds a customer by their unique CustomerId.
     *
     * @param customerId unique customer identifier
     * @return Optional containing the customer or empty
     */
    Optional<Customer> findById(CustomerId customerId);

    /**
     * Persists or updates a customer entity.
     *
     * @param customer customer entity to save
     * @return saved customer
     */
    Customer save(Customer customer);
}
