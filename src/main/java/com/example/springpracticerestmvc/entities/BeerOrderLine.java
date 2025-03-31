package com.example.springpracticerestmvc.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class BeerOrderLine {

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

    @ManyToOne
    private Beer beer;

    @ManyToOne
    private BeerOrder beerOrder;

    private Integer orderQuantity = 0;
    private Integer quantityAllocated = 0;

    public boolean isNew() {
        return this.id == null;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof BeerOrderLine that)) return false;

        return Objects.equals(getId(), that.getId()) && Objects.equals(getVersion(), that.getVersion()) && Objects.equals(getCreatedDate(), that.getCreatedDate()) && Objects.equals(getLastModifiedDate(), that.getLastModifiedDate()) && Objects.equals(getBeer(), that.getBeer()) && Objects.equals(getBeerOrder(), that.getBeerOrder()) && Objects.equals(getOrderQuantity(), that.getOrderQuantity()) && Objects.equals(getQuantityAllocated(), that.getQuantityAllocated());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getVersion());
        result = 31 * result + Objects.hashCode(getCreatedDate());
        result = 31 * result + Objects.hashCode(getLastModifiedDate());
        result = 31 * result + Objects.hashCode(getBeer());
        result = 31 * result + Objects.hashCode(getBeerOrder());
        result = 31 * result + Objects.hashCode(getOrderQuantity());
        result = 31 * result + Objects.hashCode(getQuantityAllocated());
        return result;
    }
}