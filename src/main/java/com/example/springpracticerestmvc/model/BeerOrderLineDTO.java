package com.example.springpracticerestmvc.model;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class BeerOrderLineDTO {

    private UUID id;
    private Long version;
    private Timestamp createdDate;
    private Timestamp lastModifiedDate;

    private BeerDTO beer;

    @Min(value = 1, message = "Order quantity must be at least 1")
    private Integer orderQuantity;

    private Integer quantityAllocated;

    private BeerOrderLineStatus orderLineStatus;


}
