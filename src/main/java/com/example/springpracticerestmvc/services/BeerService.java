package com.example.springpracticerestmvc.services;

import com.example.springpracticerestmvc.model.BeerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {
    List<BeerDTO> listBeers();

    Optional<BeerDTO> getBeerById(UUID beerId);

    BeerDTO saveNewBeer(BeerDTO beerDTO);

    void updateBeerById(UUID beerId, BeerDTO beerDTO);

    void deleteById(UUID beerId);

    void patchBeerById(UUID beerId, BeerDTO beerDTO);
}
