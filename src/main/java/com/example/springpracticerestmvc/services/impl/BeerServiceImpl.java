package com.example.springpracticerestmvc.services.impl;

import com.example.springpracticerestmvc.model.Beer;
import com.example.springpracticerestmvc.model.BeerStyle;
import com.example.springpracticerestmvc.services.BeerService;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BeerServiceImpl implements BeerService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BeerServiceImpl.class);
    private final Map<UUID, Beer> beerMap;

    public BeerServiceImpl() {
        this.beerMap = new HashMap<>();

        var beer1 = new Beer(
                UUID.randomUUID(),
                1,
                "Galaxy Cat",
                BeerStyle.PALE_ALE,
                "12356",
                new BigDecimal("12.99"),
                122,
                LocalDateTime.now(),
                LocalDateTime.now()
                );

        var beer2 = new Beer(
                UUID.randomUUID(),
                1,
                "Crank",
                BeerStyle.PALE_ALE,
                "123562222",
                new BigDecimal("11.99"),
                4652,
                LocalDateTime.now(),
                LocalDateTime.now()
                );

        var beer3 = new Beer(
                UUID.randomUUID(),
                1,
                "Sunshine city",
                BeerStyle.IPA,
                "1235544444466",
                new BigDecimal("132.99"),
                12,
                LocalDateTime.now(),
                LocalDateTime.now()
                );

        beerMap.put(beer1.getId(), beer1);
        beerMap.put(beer2.getId(), beer2);
        beerMap.put(beer3.getId(), beer3);
    }

    @Override
    public List<Beer> listBeers() {
        return new ArrayList<>(beerMap.values());
    }

    @Override
    public Beer getBeerById(UUID beerId) {
        log.debug("Get beer by id - in service. Id: {}", beerId.toString());
        return beerMap.get(beerId);
    }

    @Override
    public Beer saveNewBeer(Beer beer) {
        var savedBeer = new Beer(
                UUID.randomUUID(),
                1,
                beer.getBeerName(),
                beer.getBeerStyle(),
                beer.getUpc(),
                beer.getPrice(),
                beer.getQuantityOnHand(),
                beer.getCreatedDate(),
                beer.getUpdateDate()
                );

        beerMap.put(savedBeer.getId(), savedBeer);
        return savedBeer;
    }

    @Override
    public void updateBeerById(UUID beerId, Beer beer) {
        var existingBeer = beerMap.get(beerId);
        existingBeer.setBeerName(beer.getBeerName());
        existingBeer.setPrice(beer.getPrice());
        existingBeer.setUpc(beer.getUpc());
        existingBeer.setQuantityOnHand(beer.getQuantityOnHand());
    }

    @Override
    public void deleteById(UUID beerId) {
        beerMap.remove(beerId);
    }

    @Override
    public void patchBeerById(UUID beerId, Beer beer) {
        var existingBeer = beerMap.get(beerId);

        if (StringUtils.hasText(beer.getBeerName())) {
            existingBeer.setBeerName(beer.getBeerName());
        }

        if (beer.getBeerStyle() != null) {
            existingBeer.setBeerStyle(beer.getBeerStyle());
        }

        if (beer.getPrice() != null) {
            existingBeer.setPrice(beer.getPrice());
        }

        if (beer.getQuantityOnHand() != null) {
            existingBeer.setQuantityOnHand(beer.getQuantityOnHand());
        }

        if (StringUtils.hasText(beer.getUpc())) {
            existingBeer.setUpc(beer.getUpc());
        }
    }
}
