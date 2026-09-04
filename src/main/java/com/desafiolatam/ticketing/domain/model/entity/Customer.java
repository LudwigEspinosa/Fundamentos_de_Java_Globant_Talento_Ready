package com.desafiolatam.ticketing.domain.model.entity;

import com.desafiolatam.ticketing.domain.exception.InvalidDomainException;
import com.desafiolatam.ticketing.domain.model.enumtype.MembershipTier;
import com.desafiolatam.ticketing.domain.model.valueobject.CustomerId;
import com.desafiolatam.ticketing.domain.model.valueobject.Email;
import java.util.Objects;

/**
 * Pure domain entity representing a registered customer with unique identity and lifecycle.
 */
public class Customer {

    private final CustomerId id;
    private final String name;
    private final Email email;
    private final MembershipTier tier;

    public Customer(CustomerId id, String name, Email email, MembershipTier tier) {
        this.id = Objects.requireNonNull(id, "CustomerId cannot be null");
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDomainException("Customer name cannot be null or empty");
        }
        this.name = name.trim();
        this.email = Objects.requireNonNull(email, "Customer email cannot be null");
        this.tier = Objects.requireNonNull(tier, "Customer membership tier cannot be null");
    }

    public CustomerId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public MembershipTier getTier() {
        return tier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email=" + email +
                ", tier=" + tier +
                '}';
    }
}
