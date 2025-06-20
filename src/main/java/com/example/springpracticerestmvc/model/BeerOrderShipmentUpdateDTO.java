package com.example.springpracticerestmvc.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BeerOrderShipmentUpdateDTO {

    @NotNull
    private String trackingNumber;
}
