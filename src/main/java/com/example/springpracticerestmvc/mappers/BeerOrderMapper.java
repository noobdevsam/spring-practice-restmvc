package com.example.springpracticerestmvc.mappers;

import com.example.springpracticerestmvc.entities.BeerOrder;
import com.example.springpracticerestmvc.entities.BeerOrderLine;
import com.example.springpracticerestmvc.entities.BeerOrderShipment;
import com.example.springpracticerestmvc.model.BeerOrderDTO;
import com.example.springpracticerestmvc.model.BeerOrderLineDTO;
import com.example.springpracticerestmvc.model.BeerOrderShipmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Mapper interface for converting between BeerOrder, BeerOrderLine, BeerOrderShipment,
 * and their corresponding DTO objects. Utilizes MapStruct for object mapping and is
 * configured as a Spring component.
 */
@Mapper(componentModel = "spring")
@Profile({"localdb", "default"})
@Primary
public interface BeerOrderMapper {

    /**
     * Maps a BeerOrderShipmentDTO object to a BeerOrderShipment entity.
     * The beerOrder field is ignored during the mapping process.
     *
     * @param beerOrderShipmentDTO The BeerOrderShipmentDTO object to be mapped.
     * @return The mapped BeerOrderShipment entity.
     */
    @Mapping(target = "beerOrder", ignore = true)
    BeerOrderShipment beerOrderShipmentDTOtoBeerOrderShipment(BeerOrderShipmentDTO beerOrderShipmentDTO);

    /**
     * Maps a BeerOrderShipment entity to a BeerOrderShipmentDTO object.
     *
     * @param beerOrderShipment The BeerOrderShipment entity to be mapped.
     * @return The mapped BeerOrderShipmentDTO object.
     */
    BeerOrderShipmentDTO beerOrderShipmentToBeerOrderShipmentDTO(BeerOrderShipment beerOrderShipment);

    /**
     * Maps a BeerOrderLineDTO object to a BeerOrderLine entity.
     * The beerOrder field is ignored during the mapping process.
     *
     * @param beerOrderLineDTO The BeerOrderLineDTO object to be mapped.
     * @return The mapped BeerOrderLine entity.
     */
    @Mapping(target = "beerOrder", ignore = true)
    BeerOrderLine beerOrderLineDTOtoBeerOrder(BeerOrderLineDTO beerOrderLineDTO);

    /**
     * Maps a BeerOrderLine entity to a BeerOrderLineDTO object.
     *
     * @param beerOrderLine The BeerOrderLine entity to be mapped.
     * @return The mapped BeerOrderLineDTO object.
     */
    BeerOrderLineDTO beerOrderLineToBeerOrderLineDTO(BeerOrderLine beerOrderLine);

    /**
     * Maps a BeerOrderDTO object to a BeerOrder entity.
     *
     * @param beerOrderDTO The BeerOrderDTO object to be mapped.
     * @return The mapped BeerOrder entity.
     */
    BeerOrder beerOrderDTOtoBeerOrder(BeerOrderDTO beerOrderDTO);

    /**
     * Maps a BeerOrder entity to a BeerOrderDTO object.
     *
     * @param beerOrder The BeerOrder entity to be mapped.
     * @return The mapped BeerOrderDTO object.
     */
    BeerOrderDTO beerOrderToBeerOrderDTO(BeerOrder beerOrder);
}