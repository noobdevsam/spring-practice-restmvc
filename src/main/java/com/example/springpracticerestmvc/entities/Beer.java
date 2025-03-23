package com.example.springpracticerestmvc.entities;

import com.example.springpracticerestmvc.model.BeerStyle;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
    @Column(length = 36, nullable = false, updatable = false, columnDefinition = "varchar(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Version
    private Integer version;

    @NotNull
    @NotBlank
    @Size(max = 50)
    @Column(length = 50)
    private String beerName;

    @Enumerated(EnumType.STRING)
    @NotNull
    private BeerStyle beerStyle;

    @NotBlank
    @NotNull
    @Size(max = 255)
    private String upc;
    private Integer quantityOnHand;

    @NotNull
    private BigDecimal price;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

    @Builder
    public Beer(String beerName,
                BeerStyle beerStyle,
                String upc,
                Integer quantityOnHand,
                BigDecimal price,
                LocalDateTime createdDate,
                LocalDateTime updateDate) {
        this.beerName = beerName;
        this.beerStyle = beerStyle;
        this.upc = upc;
        this.quantityOnHand = quantityOnHand;
        this.price = price;
        this.createdDate = createdDate;
        this.updateDate = updateDate;
    }

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
