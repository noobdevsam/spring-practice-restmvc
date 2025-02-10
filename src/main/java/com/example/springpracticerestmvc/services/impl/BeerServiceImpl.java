package com.example.springpracticerestmvc.services.impl;

import com.example.springpracticerestmvc.model.BeerDTO;
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
    private final Map<UUID, BeerDTO> beerMap;

    public BeerServiceImpl() {
        this.beerMap = new HashMap<>();

        var beer1 = new BeerDTO(
                UUID.randomUUID(),
                1,
                "Galaxy Cat",
                BeerStyle.PALE_ALE,
                "12356",
                122,
                new BigDecimal("12.99"),
                LocalDateTime.now(),
                LocalDateTime.now()
                );

        var beer2 = new BeerDTO(
                UUID.randomUUID(),
                1,
                "Crank",
                BeerStyle.PALE_ALE,
                "123562222",
                4652,
                new BigDecimal("11.99"),
                LocalDateTime.now(),
                LocalDateTime.now()
                );

        var beer3 = new BeerDTO(
                UUID.randomUUID(),
                1,
                "Sunshine city",
                BeerStyle.IPA,
                "1235544444466",
                12,
                new BigDecimal("132.99"),
                LocalDateTime.now(),
                LocalDateTime.now()
                );

        beerMap.put(beer1.getId(), beer1);
        beerMap.put(beer2.getId(), beer2);
        beerMap.put(beer3.getId(), beer3);
    }

    @Override
    public List<BeerDTO> listBeers() {
        return new ArrayList<>(beerMap.values());
    }

    @Override
    public Optional<BeerDTO> getBeerById(UUID beerId) {
        log.debug("Get beer by id - in service. Id: {}", beerId.toString());
        return Optional.of(
                beerMap.get(beerId)
        );
    }

    @Override
    public BeerDTO saveNewBeer(BeerDTO beerDTO) {
        var savedBeer = new BeerDTO(
                UUID.randomUUID(),
                1,
                beerDTO.getBeerName(),
                beerDTO.getBeerStyle(),
                beerDTO.getUpc(),
                beerDTO.getQuantityOnHand(),
                beerDTO.getPrice(),
                beerDTO.getCreatedDate(),
                beerDTO.getUpdateDate()
                );

        beerMap.put(savedBeer.getId(), savedBeer);
        return savedBeer;
    }

    @Override
    public Optional<BeerDTO> updateBeerById(UUID beerId, BeerDTO beerDTO) {
        var existingBeer = beerMap.get(beerId);
        existingBeer.setBeerName(beerDTO.getBeerName());
        existingBeer.setPrice(beerDTO.getPrice());
        existingBeer.setUpc(beerDTO.getUpc());
        existingBeer.setQuantityOnHand(beerDTO.getQuantityOnHand());
        return Optional.of(existingBeer);
    }

    @Override
    public void deleteById(UUID beerId) {
        beerMap.remove(beerId);
    }

    @Override
    public void patchBeerById(UUID beerId, BeerDTO beerDTO) {
        var existingBeer = beerMap.get(beerId);

        if (StringUtils.hasText(beerDTO.getBeerName())) {
            existingBeer.setBeerName(beerDTO.getBeerName());
        }

        if (beerDTO.getBeerStyle() != null) {
            existingBeer.setBeerStyle(beerDTO.getBeerStyle());
        }

        if (beerDTO.getPrice() != null) {
            existingBeer.setPrice(beerDTO.getPrice());
        }

        if (beerDTO.getQuantityOnHand() != null) {
            existingBeer.setQuantityOnHand(beerDTO.getQuantityOnHand());
        }

        if (StringUtils.hasText(beerDTO.getUpc())) {
            existingBeer.setUpc(beerDTO.getUpc());
        }
    }
}
