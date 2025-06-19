package com.example.springpracticerestmvc.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class BeerOrderCreateDTO {

    @NotNull
    private UUID customerId;

    private String customerRef;

    private Set<BeerOrderLineCreateDTO> beerOrderLines;
}
