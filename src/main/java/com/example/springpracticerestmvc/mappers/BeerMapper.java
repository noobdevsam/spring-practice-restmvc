package com.example.springpracticerestmvc.mappers;

import com.example.springpracticerestmvc.entities.Beer;
import com.example.springpracticerestmvc.entities.BeerAudit;
import com.example.springpracticerestmvc.model.BeerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Mapper interface for converting between Beer, BeerDTO, and BeerAudit objects.
 * Utilizes MapStruct for object mapping and is configured as a Spring component.
 */
@Mapper(componentModel = "spring")
@Profile({"localdb", "default"})
@Primary
public interface BeerMapper {

    /**
     * Maps a BeerDTO object to a Beer entity.
     * Certain fields are ignored during the mapping process.
     *
     * @param beerDTO The BeerDTO object to be mapped.
     * @return The mapped Beer entity.
     */
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "beerOrderLines", ignore = true)
    Beer beerdtoToBeer(BeerDTO beerDTO);

    /**
     * Maps a Beer entity to a BeerDTO object.
     *
     * @param beer The Beer entity to be mapped.
     * @return The mapped BeerDTO object.
     */
    BeerDTO beerToBeerDto(Beer beer);

    /**
     * Maps a Beer entity to a BeerAudit object.
     * Certain fields are ignored during the mapping process.
     *
     * @param beer The Beer entity to be mapped.
     * @return The mapped BeerAudit object.
     */
    @Mapping(target = "createdDateAudit", ignore = true)
    @Mapping(target = "auditId", ignore = true)
    @Mapping(target = "auditEventType", ignore = true)
    @Mapping(target = "principalName", ignore = true)
    BeerAudit beerToBeerAudit(Beer beer);
}