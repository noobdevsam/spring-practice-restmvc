package com.example.springpracticerestmvc.controllers;

import com.example.springpracticerestmvc.exceptions.NotFoundException;
import com.example.springpracticerestmvc.model.BeerDTO;
import com.example.springpracticerestmvc.model.BeerStyle;
import com.example.springpracticerestmvc.services.BeerService;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for managing Beer-related operations.
 * Provides endpoints for CRUD operations on Beer entities.
 */
@RestController
@SuppressWarnings("rawtypes")
public class BeerController {

    /**
     * Base path for Beer API endpoints.
     */
    public static final String BEER_PATH = "/api/v1/beer";

    /**
     * Path for Beer API endpoints with Beer ID.
     */
    public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";

    /**
     * Logger instance for logging debug information.
     */
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BeerController.class);

    /**
     * Service for Beer-related business logic.
     */
    private final BeerService beerService;

    /**
     * Constructor for BeerController.
     *
     * @param beerService Service for Beer-related operations.
     */
    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    /**
     * Endpoint to list beers with optional filters.
     *
     * @param beerName      Optional filter by beer name.
     * @param beerStyle     Optional filter by beer style.
     * @param showInventory Optional flag to show inventory details.
     * @param pageNumber    Optional page number for pagination.
     * @param pageSize      Optional page size for pagination.
     * @return A paginated list of BeerDTO objects.
     */
    @GetMapping(BEER_PATH)
    public Page<BeerDTO> listBeers(
            @RequestParam(required = false) String beerName,
            @RequestParam(required = false) BeerStyle beerStyle,
            @RequestParam(required = false) Boolean showInventory,
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize
    ) {
        return beerService.listBeers(beerName, beerStyle, showInventory, pageNumber, pageSize);
    }

    /**
     * Endpoint to retrieve a beer by its ID.
     *
     * @param beerId UUID of the beer to retrieve.
     * @return The BeerDTO object for the specified beer ID.
     * @throws NotFoundException if the beer is not found.
     */
    @GetMapping(BEER_PATH_ID)
    public BeerDTO getBeerById(@PathVariable("beerId") UUID beerId) {
        log.debug("get beer by id - in controller");
        log.debug("requested beer id: {}", beerId);
        return beerService.getBeerById(beerId)
                .orElseThrow(NotFoundException::new);
    }

    /**
     * Endpoint to create a new beer.
     *
     * @param beerDTO The BeerDTO object containing beer details.
     * @return ResponseEntity with the location of the created beer.
     */
    @SuppressWarnings("unchecked")
    @PostMapping(BEER_PATH)
    public ResponseEntity handlePost(@Validated @RequestBody BeerDTO beerDTO) {
        var savedBeer = beerService.saveNewBeer(beerDTO);

        var headers = new HttpHeaders();
        headers.add("Location", BEER_PATH + "/" + savedBeer.getId().toString());

        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    /**
     * Endpoint to update an existing beer by its ID.
     *
     * @param beerId  UUID of the beer to update.
     * @param beerDTO The BeerDTO object containing updated beer details.
     * @return ResponseEntity with HTTP status NO_CONTENT.
     * @throws NotFoundException if the beer is not found.
     */
    @PutMapping(BEER_PATH_ID)
    public ResponseEntity updateBeerById(@PathVariable("beerId") UUID beerId, @Validated @RequestBody BeerDTO beerDTO) {
        if (beerService.updateBeerById(beerId, beerDTO).isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Endpoint to delete a beer by its ID.
     *
     * @param beerId UUID of the beer to delete.
     * @return ResponseEntity with HTTP status NO_CONTENT.
     * @throws NotFoundException if the beer is not found.
     */
    @DeleteMapping(BEER_PATH_ID)
    public ResponseEntity deleteById(@PathVariable("beerId") UUID beerId) {
        if (!beerService.deleteById(beerId)) {
            throw new NotFoundException();
        }
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     * Endpoint to partially update a beer by its ID.
     *
     * @param beerId  UUID of the beer to patch.
     * @param beerDTO The BeerDTO object containing partial updates.
     * @return ResponseEntity with HTTP status NO_CONTENT.
     */
    @PatchMapping(BEER_PATH_ID)
    public ResponseEntity patchBeerById(@PathVariable("beerId") UUID beerId, @RequestBody BeerDTO beerDTO) {
        beerService.patchBeerById(beerId, beerDTO);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}