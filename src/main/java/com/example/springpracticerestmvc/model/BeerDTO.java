package com.example.springpracticerestmvc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


public class BeerDTO {
    private UUID id;
    private Integer version;
    private String beerName;
    private BeerStyle beerStyle;
    private String upc;
    private Integer quantityOnHand;
    private BigDecimal price;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

    public BeerDTO(UUID id,
                   Integer version,
                   String beerName,
                   BeerStyle beerStyle,
                   String upc,
                   BigDecimal price,
                   Integer quantityOnHand,
                   LocalDateTime createdDate,
                   LocalDateTime updateDate) {
        this.id = id;
        this.version = version;
        this.beerName = beerName;
        this.beerStyle = beerStyle;
        this.upc = upc;
        this.price = price;
        this.quantityOnHand = quantityOnHand;
        this.createdDate = createdDate;
        this.updateDate = updateDate;
    }

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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getBeerName() {
        return beerName;
    }

    public void setBeerName(String beerName) {
        this.beerName = beerName;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public BeerStyle getBeerStyle() {
        return beerStyle;
    }

    public void setBeerStyle(BeerStyle beerStyle) {
        this.beerStyle = beerStyle;
    }

    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }
}
