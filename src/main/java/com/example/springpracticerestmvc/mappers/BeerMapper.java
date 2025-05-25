package com.example.springpracticerestmvc.mappers;

import com.example.springpracticerestmvc.entities.Beer;
import com.example.springpracticerestmvc.model.BeerDTO;
import org.mapstruct.Mapper;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Mapper(componentModel = "spring")
@Profile({"localdb", "default"})
@Primary
public interface BeerMapper {

    Beer beerdtoToBeer(BeerDTO beerDTO);

    BeerDTO beerToBeerDto(Beer beer);
}























