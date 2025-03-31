package com.example.springpracticerestmvc.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Builder
public class BeerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private UUID id;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp lastModifiedDate;

    private String customerRef;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "beerOrder")
    private Set<BeerOrderLine> beerOrderLines = new HashSet<>();

    public BeerOrder(UUID id,
                     Long version,
                     Timestamp createdDate,
                     Timestamp lastModifiedDate,
                     String customerRef,
                     Customer customer,
                     Set<BeerOrderLine> beerOrderLines) {
        this.id = id;
        this.version = version;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.customerRef = customerRef;
        this.setCustomer(customer);
        this.beerOrderLines = beerOrderLines;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        customer.getBeerOrders().add(this);
    }

    public boolean isNew() {
        return this.id == null;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof BeerOrder beerOrder)) return false;

        return Objects.equals(getId(), beerOrder.getId()) && Objects.equals(getVersion(), beerOrder.getVersion()) && Objects.equals(getCreatedDate(), beerOrder.getCreatedDate()) && Objects.equals(getLastModifiedDate(), beerOrder.getLastModifiedDate()) && Objects.equals(getCustomerRef(), beerOrder.getCustomerRef()) && Objects.equals(getCustomer(), beerOrder.getCustomer()) && Objects.equals(getBeerOrderLines(), beerOrder.getBeerOrderLines());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getVersion());
        result = 31 * result + Objects.hashCode(getCreatedDate());
        result = 31 * result + Objects.hashCode(getLastModifiedDate());
        result = 31 * result + Objects.hashCode(getCustomerRef());
        result = 31 * result + Objects.hashCode(getCustomer());
        result = 31 * result + Objects.hashCode(getBeerOrderLines());
        return result;
    }
}
