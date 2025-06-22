package com.example.springpracticerestmvc.entities;

import com.example.springpracticerestmvc.model.BeerOrderLineStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
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

    @Min(value = 1, message = "Order quantity must be at least 1")
    private Integer orderQuantity = 1;

    private Integer quantityAllocated = 0;

    @Builder.Default // Default value for orderLineStatus is NEW
    @Enumerated(EnumType.STRING) // Store the enum as a string in the database
    private BeerOrderLineStatus orderLineStatus = BeerOrderLineStatus.NEW;

}