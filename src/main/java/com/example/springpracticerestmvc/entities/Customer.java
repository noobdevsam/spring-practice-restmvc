package com.example.springpracticerestmvc.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false, columnDefinition = "varchar(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;
    private String name;

    private String email;

    @Version
    private Integer version;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

    @OneToMany(mappedBy = "customer")
    private Set<BeerOrder> beerOrders = new HashSet<>();

    @Builder
    public Customer(String name,
                    LocalDateTime createdDate,
                    LocalDateTime updateDate) {
        this.name = name;
        this.createdDate = createdDate;
        this.updateDate = updateDate;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Customer customer)) return false;

        return Objects.equals(getId(), customer.getId()) && Objects.equals(getName(), customer.getName()) && Objects.equals(getVersion(), customer.getVersion()) && Objects.equals(getCreatedDate(), customer.getCreatedDate()) && Objects.equals(getUpdateDate(), customer.getUpdateDate());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getVersion());
        result = 31 * result + Objects.hashCode(getCreatedDate());
        result = 31 * result + Objects.hashCode(getUpdateDate());
        return result;
    }
}
