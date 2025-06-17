package com.example.springpracticerestmvc.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class BeerOrderDTO {

    private UUID id;
    private Long version;
    private Timestamp createdDate;
    private Timestamp lastModifiedDate;

    private String customerRef;

    private CustomerDTO customer;

    private Set<BeerOrderLineDTO> beerOrderLines;

    private BeerOrderShipmentDTO beerOrderShipment;

}
