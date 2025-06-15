package com.example.springpracticerestmvc.mappers;

import com.example.springpracticerestmvc.entities.Beer;
import com.example.springpracticerestmvc.entities.BeerAudit;
import com.example.springpracticerestmvc.model.BeerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Mapper(componentModel = "spring")
@Profile({"localdb", "default"})
@Primary
public interface BeerMapper {

    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "beerOrderLines", ignore = true)
    Beer beerdtoToBeer(BeerDTO beerDTO);

    BeerDTO beerToBeerDto(Beer beer);

    @Mapping(target = "createdDateAudit", ignore = true)
    @Mapping(target = "auditId", ignore = true)
    @Mapping(target = "auditEventType", ignore = true)
    @Mapping(target = "principalName", ignore = true)
    BeerAudit beerToBeerAudit(Beer beer);
}























