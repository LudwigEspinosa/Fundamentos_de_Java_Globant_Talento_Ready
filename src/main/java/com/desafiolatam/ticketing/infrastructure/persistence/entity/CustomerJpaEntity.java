package com.desafiolatam.ticketing.infrastructure.persistence.entity;

import com.desafiolatam.ticketing.domain.model.entity.Customer;
import com.desafiolatam.ticketing.domain.model.enumtype.MembershipTier;
import com.desafiolatam.ticketing.domain.model.valueobject.CustomerId;
import com.desafiolatam.ticketing.domain.model.valueobject.Email;
import jakarta.persistence.*;

/**
 * JPA Entity for Customer persistence in PostgreSQL.
 */
@Entity
@Table(name = "customers")
public class CustomerJpaEntity {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier", nullable = false, length = 32)
    private MembershipTier tier;

    public CustomerJpaEntity() {}

    public CustomerJpaEntity(String id, String name, String email, MembershipTier tier) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.tier = tier;
    }

    public static CustomerJpaEntity fromDomain(Customer customer) {
        return new CustomerJpaEntity(
                customer.getId().value(),
                customer.getName(),
                customer.getEmail().value(),
                customer.getTier()
        );
    }

    public Customer toDomain() {
        return new Customer(
                new CustomerId(this.id),
                this.name,
                Email.of(this.email),
                this.tier
        );
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public MembershipTier getTier() { return tier; }
    public void setTier(MembershipTier tier) { this.tier = tier; }
}
