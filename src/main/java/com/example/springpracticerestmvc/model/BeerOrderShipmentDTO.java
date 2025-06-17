package com.example.springpracticerestmvc.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class BeerOrderShipmentDTO {

    private UUID id;
    private Long version;
    private String trackingNumber;
    private Timestamp createdDate;
    private Timestamp lastModifiedDate;

}
