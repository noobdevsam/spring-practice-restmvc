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

@Mapper(componentModel = "spring")
@Profile({"localdb", "default"})
@Primary
public interface BeerOrderMapper {

    @Mapping(target = "beerOrder", ignore = true)
    BeerOrderShipment beerOrderShipmentDTOtoBeerOrderShipment(BeerOrderShipmentDTO beerOrderShipmentDTO);

    BeerOrderShipmentDTO beerOrderShipmentToBeerOrderShipmentDTO(BeerOrderShipment beerOrderShipment);

    @Mapping(target = "beerOrder", ignore = true)
    BeerOrderLine beerOrderLineDTOtoBeerOrder(BeerOrderLineDTO beerOrderLineDTO);

    BeerOrderLineDTO beerOrderLineToBeerOrderLineDTO(BeerOrderLine beerOrderLine);

    BeerOrder beerOrderDTOtoBeerOrder(BeerOrderDTO beerOrderDTO);

    BeerOrderDTO beerOrderToBeerOrderDTO(BeerOrder beerOrder);


}
