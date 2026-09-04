package com.desafiolatam.ticketing.infrastructure.persistence.inmemory;

import com.desafiolatam.ticketing.domain.model.entity.Customer;
import com.desafiolatam.ticketing.domain.model.valueobject.CustomerId;
import com.desafiolatam.ticketing.domain.repository.CustomerRepository;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Concrete in-memory repository adapter implementing the domain CustomerRepository contract.
 */
public class InMemoryCustomerRepository implements CustomerRepository {

    private final Map<CustomerId, Customer> database = new ConcurrentHashMap<>();

    @Override
    public Optional<Customer> findById(CustomerId customerId) {
        if (customerId == null) return Optional.empty();
        return Optional.ofNullable(database.get(customerId));
    }

    @Override
    public Customer save(Customer customer) {
        Objects.requireNonNull(customer, "Customer cannot be null");
        database.put(customer.getId(), customer);
        return customer;
    }

    public void clear() {
        database.clear();
    }
}
