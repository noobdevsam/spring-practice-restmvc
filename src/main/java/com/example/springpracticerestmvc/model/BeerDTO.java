package com.example.springpracticerestmvc.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeerDTO {
    private UUID id;
    private Integer version;

    @NotBlank
    @NotNull
    private String beerName;

    @NotNull
    private BeerStyle beerStyle;

    @NotBlank
    @NotNull
    private String upc;
    private Integer quantityOnHand;

    @NotNull
    private BigDecimal price;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof BeerDTO beerDTO)) return false;

        return Objects.equals(getId(), beerDTO.getId()) && Objects.equals(getVersion(), beerDTO.getVersion()) && Objects.equals(getBeerName(), beerDTO.getBeerName()) && getBeerStyle() == beerDTO.getBeerStyle() && Objects.equals(getUpc(), beerDTO.getUpc()) && Objects.equals(getQuantityOnHand(), beerDTO.getQuantityOnHand()) && Objects.equals(getPrice(), beerDTO.getPrice()) && Objects.equals(getCreatedDate(), beerDTO.getCreatedDate()) && Objects.equals(getUpdateDate(), beerDTO.getUpdateDate());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getId());
        result = 31 * result + Objects.hashCode(getVersion());
        result = 31 * result + Objects.hashCode(getBeerName());
        result = 31 * result + Objects.hashCode(getBeerStyle());
        result = 31 * result + Objects.hashCode(getUpc());
        result = 31 * result + Objects.hashCode(getQuantityOnHand());
        result = 31 * result + Objects.hashCode(getPrice());
        result = 31 * result + Objects.hashCode(getCreatedDate());
        result = 31 * result + Objects.hashCode(getUpdateDate());
        return result;
    }

}
