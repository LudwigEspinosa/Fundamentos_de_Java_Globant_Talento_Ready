package com.desafiolatam.ticketing.domain.model;

import com.desafiolatam.ticketing.domain.exception.InvalidBookingException;
import java.util.Objects;

/**
 * Pure domain entity representing a registered customer.
 */
public class Customer {

    private final String id;
    private final String name;
    private final String email;
    private final MembershipTier tier;

    public Customer(String id, String name, String email, MembershipTier tier) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidBookingException("Customer ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidBookingException("Customer name cannot be null or empty");
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new InvalidBookingException("Customer email is invalid: " + email);
        }
        if (tier == null) {
            throw new InvalidBookingException("Customer membership tier cannot be null");
        }

        this.id = id.trim();
        this.name = name.trim();
        this.email = email.trim().toLowerCase();
        this.tier = tier;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
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
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", tier=" + tier +
                '}';
    }
}
