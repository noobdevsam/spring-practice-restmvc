package com.example.springpracticerestmvc.entities;

import com.example.springpracticerestmvc.model.BeerStyle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Beer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, nullable = false, updatable = false, columnDefinition = "varchar")
    private UUID id;

    @Version
    private Integer version;
    private String beerName;
    private BeerStyle beerStyle;
    private String upc;
    private Integer quantityOnHand;
    private BigDecimal price;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Beer beer)) return false;

        return Objects.equals(getId(), beer.getId()) && Objects.equals(getVersion(), beer.getVersion()) && Objects.equals(getBeerName(), beer.getBeerName()) && getBeerStyle() == beer.getBeerStyle() && Objects.equals(getUpc(), beer.getUpc()) && Objects.equals(getQuantityOnHand(), beer.getQuantityOnHand()) && Objects.equals(getPrice(), beer.getPrice()) && Objects.equals(getCreatedDate(), beer.getCreatedDate()) && Objects.equals(getUpdateDate(), beer.getUpdateDate());
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
