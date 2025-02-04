package com.example.springpracticerestmvc.mappers;

import com.example.springpracticerestmvc.entities.Beer;
import com.example.springpracticerestmvc.model.BeerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {

    Beer beerdtoToBeer(BeerDTO beerDTO);

    BeerDTO beerToBeerDto(Beer beer);
}























