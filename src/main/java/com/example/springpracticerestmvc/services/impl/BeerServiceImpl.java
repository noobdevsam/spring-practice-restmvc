package com.example.springpracticerestmvc.services.impl;

import com.example.springpracticerestmvc.model.BeerDTO;
import com.example.springpracticerestmvc.model.BeerStyle;
import com.example.springpracticerestmvc.services.BeerService;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementation of the BeerService interface for managing beer-related operations.
 * Provides methods for CRUD operations on beers.
 */
@Service
public class BeerServiceImpl implements BeerService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BeerServiceImpl.class);
    private final Map<UUID, BeerDTO> beerMap;

    /**
     * Constructor for BeerServiceImpl.
     * Initializes the beerMap with sample beer data.
     */
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

    /**
     * Lists beers with optional filtering and pagination.
     *
     * @param beerName      The name of the beer to filter by (optional).
     * @param beerStyle     The style of the beer to filter by (optional).
     * @param showInventory Whether to show inventory details (optional).
     * @param pageNumber    The page number to retrieve (0-based).
     * @param pageSize      The number of items per page.
     * @return A Page of BeerDTO objects.
     */
    @Override
    public Page<BeerDTO> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory, Integer pageNumber, Integer pageSize) {
        return new PageImpl<>(new ArrayList<>(beerMap.values()));
    }

    /**
     * Retrieves a beer by its ID.
     *
     * @param beerId The UUID of the beer to retrieve.
     * @return An Optional containing the BeerDTO if found, or empty if not found.
     */
    @Override
    public Optional<BeerDTO> getBeerById(UUID beerId) {
        log.debug("Get beer by id - in service. Id: {}", beerId.toString());
        return Optional.of(
                beerMap.get(beerId)
        );
    }

    /**
     * Saves a new beer.
     *
     * @param beerDTO The BeerDTO containing details of the beer to save.
     * @return The saved BeerDTO object.
     */
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

    /**
     * Updates an existing beer by its ID.
     *
     * @param beerId  The UUID of the beer to update.
     * @param beerDTO The BeerDTO containing updated details for the beer.
     * @return An Optional containing the updated BeerDTO.
     */
    @Override
    public Optional<BeerDTO> updateBeerById(UUID beerId, BeerDTO beerDTO) {
        var existingBeer = beerMap.get(beerId);
        existingBeer.setBeerName(beerDTO.getBeerName());
        existingBeer.setPrice(beerDTO.getPrice());
        existingBeer.setUpc(beerDTO.getUpc());
        existingBeer.setQuantityOnHand(beerDTO.getQuantityOnHand());
        return Optional.of(existingBeer);
    }

    /**
     * Deletes a beer by its ID.
     *
     * @param beerId The UUID of the beer to delete.
     * @return True if the beer was successfully deleted.
     */
    @Override
    public Boolean deleteById(UUID beerId) {
        beerMap.remove(beerId);
        return true;
    }

    /**
     * Partially updates a beer by its ID.
     *
     * @param beerId  The UUID of the beer to patch.
     * @param beerDTO The BeerDTO containing the fields to update.
     * @return An Optional containing the patched BeerDTO.
     */
    @Override
    public Optional<BeerDTO> patchBeerById(UUID beerId, BeerDTO beerDTO) {
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

        return Optional.of(existingBeer);
    }
}